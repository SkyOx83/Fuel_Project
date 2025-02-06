package org.isen.td2.controller

import org.isen.td2.model.geoPosition
import org.jxmapviewer.JXMapViewer
import org.isen.td2.model.DrawMap
import org.isen.td2.model.DrawData

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


class DrawControllerMap(private val model: DrawMap, private val modelData: DrawData) {
    private val mapViewer = JXMapViewer()


    fun getMapViewer(): JXMapViewer {
        return mapViewer
    }

    fun setup(adresse:String){
        DrawMap.AdjustMap(mapViewer)
        var geoDouble = DrawMap.getCoordinatesFromAddress(adresse)
        geoPosition = DrawMap.centerMap(adresse, mapViewer)
        DrawMap.Listen_Mouse_click(mapViewer)
        DrawMap.MapZoom(mapViewer)
    }


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

        //1

        geoPosition =  DrawMap.centerMap("283 Rue Henri Sainte-Claire Deville", mapViewer)
        DrawMap.addWaypoint(mapViewer, geoPosition)

        //2

        geoPosition =  DrawMap.centerMap("160 BOULEVARD G.CLEMENCEAU", mapViewer)
        DrawMap.addWaypoint(mapViewer, geoPosition)

        //3
        /*
        geoPosition =  DrawMap.centerMap("AV INFANTERIE DE MARINE", mapViewer)
        DrawMap.addWaypoint(mapViewer, geoPosition)

        //4
        geoPosition =  DrawMap.centerMap("322 AV.LE BELLEGOU RODE", mapViewer)
        DrawMap.addWaypoint(mapViewer, geoPosition)

        //5
        geoPosition =  DrawMap.centerMap("2338 Avenue Joseph Gasquet", mapViewer)
        DrawMap.addWaypoint(mapViewer, geoPosition)

        //6
        geoPosition =  DrawMap.centerMap("508 AVENUE FOCH", mapViewer)
        DrawMap.addWaypoint(mapViewer, geoPosition)

        //7
        geoPosition =  DrawMap.centerMap("620 Rue David", mapViewer)
        DrawMap.addWaypoint(mapViewer, geoPosition)*/

        //8
        /*
        geoPosition =  DrawMap.centerMap("95 Boulevard G�n�ral Brosset", mapViewer)
        DrawMap.addWaypoint(mapViewer, geoPosition)*/


        /*stations.forEach{ station ->
            geoPosition =  DrawMap.centerMap(station.address.toString(), mapViewer)
            println("test")
            DrawMap.addWaypoint(mapViewer, geoPosition)
            println("test2")
        }*/
    }



}