package org.isen.td2

import org.isen.td2.controller.DrawController
import org.isen.td2.model.DrawModel
import org.isen.td2.view.impl.DrawTestView


import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.viewer.DefaultTileFactory
import org.jxmapviewer.viewer.TileFactoryInfo
import org.jxmapviewer.viewer.GeoPosition
import java.net.HttpURLConnection
import java.net.URL
import javax.swing.JFrame
import kotlinx.serialization.*
import kotlinx.serialization.json.*

import org.isen.td2.controller.DrawControllerMap
import org.isen.td2.map.CustomWaypoint
import org.isen.td2.map.Address
import org.isen.td2.view.impl.ShapeView

import org.isen.td2.model.DrawMap
import org.isen.td2.view.impl.ShapeMap
import java.io.File
import javax.imageio.ImageIO
import java.awt.event.MouseAdapter // pour suivre la souris
import java.awt.event.MouseEvent // pour faire un event quand un clic sur la souris est fait
import java.awt.Point // pour suivre les points
import java.awt.event.MouseWheelEvent


@Serializable
data class NominatimResponse(
    val place_id: Long,
    val licence: String,
    val lat: String,
    val lon: String
)


// Déclaration globale de geoPosition comme non nullable
//lateinit var geoPosition: GeoPosition

fun main() {

    val model = DrawMap()
    val controller = DrawControllerMap(model)
    //controller.setupData("TOULON") // maintenant dans ShapeView
    controller.setup("PARIS") // maintenant dans ShapeView
    //val shapemap = ShapeView(controller)

    //shapemap.display(controller.getMapViewer())
    val shapeView = ShapeView(controller)

    controller.displayAll()


    /*
    val model = DrawModel()
    val controller = DrawController(model)

    val testGui = DrawTestView(controller)
    val shapeView = org.isen.td2.view.impl.ShapeView(controller)

    controller.displayAll()*/



    /*
    //----map-------
    val frame = JFrame("Fill & Rest")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(800, 600)

    // ✅ Changer l'icône de la fenêtre
    val iconPath = "C:\\Users\\jande\\Documents\\Project\\TD2\\app\\src\\main\\resources\\icone.png"
    val iconFile = File(iconPath)
    if (iconFile.exists()) {
        val icon = ImageIO.read(iconFile)
        frame.iconImage = icon
    } else {
        println("⚠️ Icône introuvable : $iconPath")
    }


    val mapViewer = JXMapViewer()
    /*
    val minZoom: Int = 2
    val maxZoom:Int = 17

    // Utilisation de TileFactoryInfo si OSMTileFactoryInfo ne fonctionne pas
    val tileFactoryInfo = object : TileFactoryInfo(
        "OpenStreetMap",
        1, maxZoom, maxZoom, 256, true, true,
        "https://tile.openstreetmap.org", "x", "y", "z"
    ) {
        override fun getTileUrl(x: Int, y: Int, zoom: Int): String {
            val adjustedZoom = maxZoom - zoom // Ajuster le zoom pour correspondre aux niveaux valides
            return "https://tile.openstreetmap.org/$adjustedZoom/$x/$y.png"
        }
    }

    val tileFactory = DefaultTileFactory(tileFactoryInfo)
    mapViewer.tileFactory = tileFactory

    /*
    // Centrer la carte sur une position spécifique
    mapViewer.addressLocation = org.jxmapviewer.viewer.GeoPosition(43.116669 ,5.93333) // Paris
    mapViewer.zoom = 4 // Zoom initial*/

    // Adresse à convertir en coordonnées GPS
    val adresse = "56 Chemin de la Providence, 83100 Toulon"
    val coordinates = DrawMap.getCoordinatesFromAddress(adresse)

    if (coordinates != null) {
        val (latitude, longitude) = coordinates
        println("Coordonnées de $adresse : lat=$latitude, lon=$longitude")

        // Centrer la carte sur la position trouvée
        geoPosition = GeoPosition(latitude, longitude)
        mapViewer.addressLocation = geoPosition
        CustomWaypoint.addWaypoint(mapViewer, geoPosition)
        mapViewer.zoom = 5
    } else {
        println("Impossible de trouver l'adresse.")
    }

    //------------------left click on mouse to drag--------------------
    // Add mouse listeners for dragging the map
    var lastPoint: Point? = null

    mapViewer.addMouseListener(object : MouseAdapter() {
        override fun mousePressed(e: MouseEvent) {
            lastPoint = e.point // Store the position when the mouse is pressed
        }

        override fun mouseReleased(e: MouseEvent) {
            lastPoint = null // Reset when mouse is released
        }
    })

    mapViewer.addMouseMotionListener(object : MouseAdapter() {
        override fun mouseDragged(e: MouseEvent) {
            val currentPoint = e.point
            if (lastPoint != null) {
                val deltaX = currentPoint.x - lastPoint!!.x
                val deltaY = currentPoint.y - lastPoint!!.y

                // Convertir le déplacement en pixels en un changement géographique
                val geoDelta = mapViewer.convertPointToGeoPosition(currentPoint)
                geoPosition = GeoPosition(
                    geoPosition.latitude + deltaY * 0.0003, // Ajuster le facteur de mise à l'échelle pour le mouvement vertical
                    geoPosition.longitude - deltaX * 0.0003  // Ajuster le facteur de mise à l'échelle pour le mouvement horizontal
                )

                // Mettre à jour la position de la carte
                mapViewer.setAddressLocation(geoPosition)
            }
            lastPoint = currentPoint
        }
    })



    // Ajouter un MouseWheelListener pour gérer le zoom
    mapViewer.addMouseWheelListener { e: MouseWheelEvent ->
        val zoomDelta = e.preciseWheelRotation
        val newZoom = mapViewer.zoom + zoomDelta.toInt()  // Décrémenter le zoom lorsque la molette va vers le bas
        if (newZoom in minZoom..maxZoom) {
            mapViewer.zoom = newZoom // Appliquer le zoom si dans la plage
            println("Zoom : $newZoom")  // Afficher le niveau de zoom dans la console
        }
    }*/





    frame.add(mapViewer)
    frame.isVisible = true*/
}




