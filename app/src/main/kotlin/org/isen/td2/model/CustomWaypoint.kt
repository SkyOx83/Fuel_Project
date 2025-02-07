
package org.isen.td2.model

import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.viewer.GeoPosition
import org.jxmapviewer.viewer.Waypoint
import javax.swing.ImageIcon
import java.awt.Graphics
import java.awt.Color
import java.awt.Point
import java.awt.geom.Point2D

class CustomWaypoint(private val geoPosition: GeoPosition) : Waypoint {


    // Retourne la position géographique du waypoint
    override fun getPosition(): GeoPosition {
        return geoPosition
    }

    // Méthode pour dessiner un waypoint
    fun paintWaypoint(g: Graphics, map: JXMapViewer) {
        val pt: Point2D = map.convertGeoPositionToPoint(geoPosition)
        g.color = Color.RED // Choisir la couleur du waypoint
        g.fillOval((pt.x - 5).toInt(), (pt.y - 5).toInt(), 10, 10)
    }
}

// Liste pour gérer les waypoints
val waypointsList = mutableListOf<CustomWaypoint>()

// Fonction pour ajouter un waypoint
fun addWaypoint(mapViewer: JXMapViewer, position: GeoPosition) {
    val waypoint = CustomWaypoint(position)
    waypointsList.add(waypoint) // Ajouter le waypoint à la liste
    mapViewer.repaint() // Rafraîchir la carte pour afficher le waypoint
}

// Fonction pour effacer tous les waypoints
fun clearWaypoints(mapViewer: JXMapViewer) {
    waypointsList.clear() // Vider la liste des waypoints
    mapViewer.repaint() // Rafraîchir la carte pour effacer tous les waypoints
}

// Méthode pour afficher tous les waypoints
fun paintWaypoints(g: Graphics, mapViewer: JXMapViewer) {
    waypointsList.forEach { waypoint ->
        waypoint.paintWaypoint(g, mapViewer) // Dessiner chaque waypoint sur la carte
    }
}




