
package org.isen.td2.model

import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.viewer.GeoPosition
import org.jxmapviewer.viewer.Waypoint
import org.jxmapviewer.viewer.WaypointPainter
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

fun clearWaypoints(mapViewer: JXMapViewer) {


    // 1️⃣ Supprimer les waypoints stockés dans la liste
    waypointsList.clear()

    // 2️⃣ Supprimer tous les waypoints de la carte
    val painter = WaypointPainter<CustomWaypoint>()
    painter.setWaypoints(emptySet())  // ✅ Supprime les marqueurs visibles
    mapViewer.overlayPainter = painter

    // 3️⃣ Désactiver temporairement l'affichage des overlays
    mapViewer.setOverlayPainter(null)
    mapViewer.setOverlayPainter(painter)

    // 4️⃣ Forcer une mise à jour complète de l'affichage
    mapViewer.revalidate()
    mapViewer.repaint()
    mapViewer.updateUI()


}
fun updateWaypointPainter(mapViewer: JXMapViewer, waypointsList: MutableList<CustomWaypoint>) {
    println("🔄 Mise à jour des waypoints...")

    // ✅ Vérifier si la liste est vide
    if (waypointsList.isEmpty()) {
        println("🗑️ Aucun waypoint à afficher, suppression des overlays.")
        mapViewer.setOverlayPainter(null) // Supprime tous les waypoints
    } else {
        // 🎨 Création d'un nouveau WaypointPainter avec les waypoints
        val waypointPainter = WaypointPainter<CustomWaypoint>()
        waypointPainter.setWaypoints(waypointsList.toSet()) // ✅ Convertir la liste en Set
        mapViewer.overlayPainter = waypointPainter // ✅ Appliquer le WaypointPainter
    }

    // 🔄 Forcer la mise à jour graphique
    mapViewer.revalidate()
    mapViewer.repaint()
    mapViewer.updateUI() // 🔥 Swing refresh total

    println("✅ Waypoints mis à jour : ${waypointsList.size} waypoints affichés.")
}





// Méthode pour afficher tous les waypoints
fun paintWaypoints(g: Graphics, mapViewer: JXMapViewer) {
    waypointsList.forEach { waypoint ->
        waypoint.paintWaypoint(g, mapViewer) // Dessiner chaque waypoint sur la carte
    }
}








