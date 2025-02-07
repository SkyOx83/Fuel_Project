/*package org.isen.td2.model


import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipInputStream
import javax.xml.parsers.DocumentBuilderFactory

// Initialisation du logger
private val logger = org.apache.logging.log4j.LogManager.getLogger("FuelAPI")

// Modèle principal pour Opendatasoft
data class FuelResponse(val nhits: Int, val records: List<Record>)
data class Record(val fields: FuelStation)

// Structure pour FuelStation avec ajout de `update_date`
data class FuelStation(
    val name: String?,
    val address: String?,
    val com_arm_name: String?,
    val price_gazole: Double?,
    val price_sp95: Double?,
    val price_sp98: Double?,
    val price_e10: Double?,
    val price_e85: Double?,
    val price_gplc: Double?,
    val services: List<String>?,
    val geo_point: List<Double>?,
    val update_date: String? // Ajout de la date de mise à jour
) {
    class Deserializer : ResponseDeserializable<FuelResponse> {
        override fun deserialize(content: String): FuelResponse = Gson().fromJson(content, FuelResponse::class.java)
    }
}



class DrawData {
    // Fonction principale pour récupérer les stations depuis Opendatasoft
    fun fetchRecentStations(city: String): Pair<List<FuelStation>, String?> {
        val url = "https://public.opendatasoft.com/api/explore/v2.1/catalog/datasets/prix-des-carburants-j-1/records?select=*&where=com_arm_name%20%3D%20'$city'&rows=200&sort=update"
        val (_, _, result) = url.httpGet().responseObject(FuelStation.Deserializer())

        return result.fold(
            success = {
                val stations = it.records.map { record -> record.fields }
                val lastUpdate = stations.mapNotNull { station -> station.update_date }.maxOrNull()
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
                    return parseXmlStations(extractedXml, city)
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

                val updateDate = getTagValue("update", pdv)
                if (updateDate != null) {
                    lastUpdate = if (lastUpdate == null || updateDate > lastUpdate) updateDate else lastUpdate
                }

                val servicesList = mutableListOf<String>()
                val servicesNodes = pdv.getElementsByTagName("service")
                for (j in 0 until servicesNodes.length) {
                    val serviceNode = servicesNodes.item(j) as Element
                    servicesList.add(serviceNode.textContent.trim())
                }

                stations.add(
                    FuelStation(
                        name = "Station inconnue",
                        address = adresse,
                        com_arm_name = ville,
                        price_gazole = priceMap["Gazole"],
                        price_sp95 = priceMap["SP95"],
                        price_sp98 = priceMap["SP98"],
                        price_e10 = priceMap["E10"],
                        price_e85 = priceMap["E85"],
                        price_gplc = priceMap["GPLc"],
                        services = if (servicesList.isNotEmpty()) servicesList else null,
                        geo_point = if (latitude != null && longitude != null) listOf(latitude, longitude) else null,
                        update_date = updateDate
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

}
*/
