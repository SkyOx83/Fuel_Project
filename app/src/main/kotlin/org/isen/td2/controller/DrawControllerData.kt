/*package org.isen.td2.controller

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import org.isen.td2.model.DrawMap
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipInputStream
import javax.xml.parsers.DocumentBuilderFactory
import org.isen.td2.model.DrawData


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

class DrawControllerData(private val modelData: DrawData) {
    fun setupData(){
        print("Entrez le nom d'une ville : ")
        val city = readLine()?.trim()?.uppercase() ?: "TOULON"

        logger.info("🔎 Recherche des stations-service à $city...")

        val (stations, lastUpdate) = modelData.fetchRecentStations(city)

        logger.info("📅 Dernière mise à jour : $lastUpdate")
        logger.info("✅ ${stations.size} stations récupérées pour $city")

        stations.forEach { station ->
            println(
                """
            🏪 ${station.name}
            📍 ${station.address}, ${station.com_arm_name}
            📅 Mise à jour : ${station.update_date ?: "Non disponible"}
            ⛽ Prix Gazole: ${station.price_gazole}, SP95: ${station.price_sp95}, SP98: ${station.price_sp98},
               E10: ${station.price_e10}, E85: ${station.price_e85}, GPLc: ${station.price_gplc}
            🛠 Services: ${station.services?.joinToString(", ") ?: "Non disponible"}
            📌 GPS: (${station.geo_point?.getOrNull(0)}, ${station.geo_point?.getOrNull(1)})
            --------------------------------------
            """.trimIndent()
            )
        }



        stations.forEach(stations -> )
    }
}*/