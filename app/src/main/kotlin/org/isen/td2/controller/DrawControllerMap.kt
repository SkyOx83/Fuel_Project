package org.isen.td2.controller


import org.isen.td2.model.geoPosition
import org.jxmapviewer.JXMapViewer
import org.isen.td2.model.DrawMap

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.isen.td2.data.Shape
import org.isen.td2.view.IFuelView
import org.isen.td2.view.impl.Carburant
import org.isen.td2.view.impl.tab
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipInputStream
import javax.xml.parsers.DocumentBuilderFactory

// Initialisation du logger
private val logger = org.apache.logging.log4j.LogManager.getLogger("FuelAPI")

// Modèle principal pour OpenDataSoft

data class FuelResponse(val records: List<Record>)
data class Record(val fields: FuelStation)

// Structure des stations-service
data class FuelStation(
    @SerializedName("cp") val cp:String?,
    @SerializedName("address") val address: String?,
    @SerializedName("com_arm_name") val comArmName: String?,
    @SerializedName("update") val updateDate: String?,
    @SerializedName("price_gazole") val priceGazole: Double?,
    @SerializedName("price_sp95") val priceSp95: Double?,
    @SerializedName("price_sp98") val priceSp98: Double?,
    @SerializedName("price_e10") val priceE10: Double?,
    @SerializedName("price_e85") val priceE85: Double?,
    @SerializedName("price_gplc") val priceGplc: Double?,
    @SerializedName("services") val services: List<String>?,
    val geo_point: List<Double>?,

    )

{
    class Deserializer : ResponseDeserializable<FuelResponse> {
        override fun deserialize(content: String): FuelResponse = Gson().fromJson(content, FuelResponse::class.java)
    }
}

// Fonction principale pour récupérer les stations depuis Opendatasoft
fun fetchRecentStations(city: String): Pair<List<FuelStation>, String?> {
    val url = "https://public.opendatasoft.com/api/explore/v2.1/catalog/datasets/prix-des-carburants-j-1/records?select=cp%2Caddress%2Ccom_arm_name%2CMAX(update)%2Cservices%2Cprice_gazole%2Cprice_sp95%2Cprice_sp98%2Cprice_e10%2Cprice_e85%2Cprice_gplc&where=com_arm_name%20%3D%20\"$city\""
    val (_, _, result) = url.httpGet().responseObject(FuelStation.Deserializer())

    return result.fold(
        success = {
            val stations = it.records.map { record -> record.fields }
            val lastUpdate = stations.mapNotNull { station -> station.updateDate }.maxOrNull()
            Pair(stations, lastUpdate)
        },
        failure = {
            logger.error("❌ Échec de récupération depuis Opendatasoft, tentative avec Roulez-Éco...")
            fetchBackupStations(city)
        }
    )
}

// Fonction pour récupérer les stations depuis Roulez-Éco (ZIP contenant XML)
fun fetchBackupStations(city: String): Pair<List<FuelStation>, String?> {
    val backupUrl = "https://donnees.roulez-eco.fr/opendata/jour"
    val (_, _, result) = backupUrl.httpGet().response()
    return result.fold(
        success = { responseData ->
            val tempZipFile = File.createTempFile("roulez-eco", ".zip")
            tempZipFile.writeBytes(responseData)

            val extractedXml = extractXmlFromZip(tempZipFile)
            if (extractedXml != null) {
                val result = parseXmlStations(extractedXml, city)

                // 🗑️ Supprimer les fichiers après utilisation
                deleteTempFiles(tempZipFile, extractedXml)

                return result
            }

            logger.error("❌ Impossible d'extraire le fichier XML du ZIP.")
            Pair(emptyList(), null)

        },
        failure = {
            logger.error("❌ Échec de récupération depuis Roulez-Éco. Aucune donnée disponible.")
            Pair(emptyList(), null)
        }

    )

}

fun deleteTempFiles(zipFile: File?, xmlFile: File?) {
    try {
        if (xmlFile != null && xmlFile.exists()) {
            xmlFile.delete()
            logger.info("🗑️ Fichier XML supprimé : ${xmlFile.absolutePath}")
        }

        if (zipFile != null && zipFile.exists()) {
            zipFile.delete()
            logger.info("🗑️ Fichier ZIP supprimé : ${zipFile.absolutePath}")
        }
    } catch (e: Exception) {
        logger.error("❌ Erreur lors de la suppression des fichiers temporaires : ${e.message}")
    }
}

// Fonction pour extraire le fichier XML du ZIP
fun extractXmlFromZip(zipFile: File): File? {
    ZipInputStream(FileInputStream(zipFile)).use { zipInputStream ->
        var entry = zipInputStream.nextEntry
        while (entry != null) {
            if (entry.name.endsWith(".xml")) {
                logger.info("📂 Extraction du fichier XML : ${entry.name}")

                val extractedFile = File.createTempFile("roulez-eco", ".xml")
                extractedFile.outputStream().use { it.write(zipInputStream.readBytes()) }
                zipInputStream.closeEntry()
                return extractedFile
            }
            entry = zipInputStream.nextEntry
        }
    }
    return null
}

// Fonction pour parser le fichier XML
fun parseXmlStations(xmlFile: File, city: String): Pair<List<FuelStation>, String?> {
    val stations = mutableListOf<FuelStation>()
    var lastUpdate: String? = null

    val factory = DocumentBuilderFactory.newInstance()
    val builder = factory.newDocumentBuilder()
    val document: Document = builder.parse(xmlFile)
    document.documentElement.normalize()

    val pdvList = document.getElementsByTagName("pdv")

    for (i in 0 until pdvList.length) {
        val pdv = pdvList.item(i) as Element
        val ville = getTagValue("ville", pdv)
        val cp = pdv.getAttribute("cp") // ✅ Récupération correcte de cp comme un attribut XML
        val stationName = getTagValue("brand", pdv) ?: "Inconnu"
        val brand = getTagValue("marque", pdv) ?: "Non spécifié"

        if (ville?.uppercase() == city.uppercase()) {

            val adresse = getTagValue("adresse", pdv)
            val latitude = pdv.getAttribute("latitude").toDoubleOrNull()?.div(100000)
            val longitude = pdv.getAttribute("longitude").toDoubleOrNull()?.div(100000)

            val priceMap = mutableMapOf<String, Double?>()
            val prixList = pdv.getElementsByTagName("prix")
            for (j in 0 until prixList.length) {
                val prixNode = prixList.item(j) as Element
                val carburant = prixNode.getAttribute("nom")
                val valeur = prixNode.getAttribute("valeur").toDoubleOrNull()
                if (carburant.isNotEmpty()) {
                    priceMap[carburant] = valeur
                }
            }

            var lastUpdate: String? = null
            //val prixList = pdv.getElementsByTagName("prix")
            for (j in 0 until prixList.length) {
                val prixNode = prixList.item(j) as Element
                val maj = prixNode.getAttribute("maj") // Récupération de la date

                if (maj.isNotEmpty()) {
                    lastUpdate = if (lastUpdate == null || maj > lastUpdate) maj else lastUpdate
                }
            }


            val servicesList = mutableListOf<String>()
            val servicesNodes = pdv.getElementsByTagName("service")
            for (j in 0 until servicesNodes.length) {
                val serviceNode = servicesNodes.item(j) as Element
                servicesList.add(serviceNode.textContent.trim())
            }

            stations.add(
                FuelStation(
                    address = adresse,
                    cp = cp,
                    comArmName = ville,
                    priceGazole = priceMap["Gazole"],
                    priceSp95 = priceMap["SP95"],
                    priceSp98 = priceMap["SP98"],
                    priceE10 = priceMap["E10"],
                    priceE85 = priceMap["E85"],
                    priceGplc = priceMap["GPLc"],
                    services = if (servicesList.isNotEmpty()) servicesList else null,
                    geo_point = if (latitude != null && longitude != null) listOf(latitude, longitude) else null,
                    updateDate = lastUpdate ?: "Non disponible"

                )
            )
        }
    }

    return Pair(stations, lastUpdate)
}

// Fonction pour extraire la valeur d'un tag XML
fun getTagValue(tag: String, element: Element): String? {
    val nodeList = element.getElementsByTagName(tag)
    return if (nodeList.length > 0) nodeList.item(0).textContent else null
}

// Modèle pour la réponse de l’API
data class ApiResponse(
    val records: List<Record>
)





// 3️⃣ Intégration dans `DrawControllerMap`
class DrawControllerMap(private val model: DrawMap) {
    private val mapViewer = JXMapViewer()

    fun getMapViewer(): JXMapViewer {
        return mapViewer
    }

    fun setup(adresse: String?) {
        if (adresse == null) {
            logger.info("⚠️ Adresse fournie est nulle, impossible d'afficher la carte.")
            return
        }

        model.let {
            DrawMap.AdjustMap(mapViewer)
            geoPosition = DrawMap.centerMap(adresse, mapViewer)
            DrawMap.Listen_Mouse_click(mapViewer)
            DrawMap.MapZoom(mapViewer)
        }
    }

    fun setupData(adresse: String) {
        //print("Entrez le nom d'une ville : ")
        //val city = readLine()?.trim()?.uppercase() ?: "TOULON"
        val city = adresse

        logger.info("🔎 Recherche des stations-service à $city...")

        val (stations, lastUpdate) = fetchRecentStations(city)

        logger.info("📅 Dernière mise à jour : $lastUpdate")
        logger.info("✅ ${stations.size} stations récupérées pour $city")
        //verifConditionStationOption(station, Carburant.checkBoxesImpl)
        stations.forEach { station ->
            if (verifConditionStationCarburant(station, Carburant.checkStates)){
                logger.info(
                    """
            📍 ${station.address},${station.cp} ${station.comArmName}
            📅 Mise à jour : ${station.updateDate ?: "Non disponible"}
            ⛽ Prix Gazole: ${station.priceGazole?: "Indisponible"}, SP95: ${station.priceSp95?: "Indisponible"}, SP98: ${station.priceSp98?: "Indisponible"}, 
               E10: ${station.priceE10?: "Indisponible"}, E85: ${station.priceE85?: "Indisponible"}, GPLc: ${station.priceGplc?: "Indisponible"}
            🛠 Services: ${station.services?.joinToString(", ") ?: "Non disponible"}
            📌 GPS: ${(station.geo_point?.getOrNull(0))}, ${station.geo_point?.getOrNull(1)})
            --------------------------------------
            """.trimIndent()
                )
                geoPosition =  DrawMap.centerMap("${station.address}, ${station.cp} ${station.comArmName}" , mapViewer)
                DrawMap.addWaypoint(mapViewer, geoPosition)


                tab.model.addRow(arrayOf(station.address, station.cp, station.comArmName, station.priceGazole,
                    station.priceE10, station.priceSp98, station.priceSp95, station.priceE85, station.priceGplc))
            }
        }
    }
    fun verifConditionStationCarburant(station: FuelStation, checkState: MutableList<Pair<String, Int>>): Boolean {
        if (checkState[3].second == 1 ) {return true}
        if (checkState[4].second == 1 ){if (checkState[4].first != "Gazole" || station.priceGazole == null) {return false}}
        if (checkState[5].second == 1 ){if (checkState[5].first != "SP 98" || station.priceSp98 == null) {return false}}
        if (checkState[6].second == 1 ){if (checkState[6].first != "SP 95" || station.priceSp95 == null) {return false}}
        if (checkState[7].second == 1 ){if (checkState[7].first != "E 10" || station.priceE10 == null) {return false}}
        if (checkState[8].second == 1 ){if (checkState[8].first != "E 85" || station.priceE85 == null) {return false}}
        if (checkState[9].second == 1 ){if (checkState[8].first != "GPLc" || station.priceGplc == null ) {return false}}
        return true
    } //checkState[9].first != "GPLc" ||

    fun verifConditionStationOption(station: FuelStation, Service: MutableList<Pair<String, Int>>): Boolean {
        var boutique: Boolean = false
        var toilette: Boolean = false
        var gonflage: Boolean = false
        if (Service[0].second == 0) {
            boutique = true
        }
        if (Service[1].second == 0) {
            toilette = true
        }
        if (Service[2].second == 0) {
            gonflage = true
        }


        station.services?.forEach { service ->

            when (service) {
                "Boutique alimentaire" -> {
                    if (Service[0].second == 1) {
                        boutique = true
                        // return@forEach
                    }
                }

                "Toilettes Publiques" -> {
                    if (Service[2].second == 1) {
                        toilette = true
                        //return@forEach
                    }
                }

                "Station de gonflage" -> {
                    if (Service[1].second == 1) {
                        gonflage = true
                        //return@forEach
                    }
                }
            }
            if (boutique && toilette && gonflage) {
                return true
            }
            return false

        }
        return false
    }


    private val views = mutableListOf<IFuelView>()

    fun displayAll(){
        views.forEach{i: IFuelView ->
            i.display()
        }
    }

    fun registerView(view: IFuelView){
        model.addObserver(view)
        views.add(view)
    }

    fun addShape(shape: Shape){
        model.addShape(shape)
    }

    fun updateModel(){
        model.update(5)
    }
}

