package org.isen.td2.map

import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.viewer.GeoPosition
import org.jxmapviewer.viewer.Waypoint
import org.jxmapviewer.viewer.WaypointPainter
import java.awt.Color
import java.awt.Graphics2D

class CustomWaypoint( private val geoPosition: GeoPosition) : Waypoint {

    companion object {
        fun addWaypoint(mapViewer: JXMapViewer, position: GeoPosition) {
            val waypoints = setOf(CustomWaypoint(position))

            val painter = object : WaypointPainter<CustomWaypoint>() {
                fun doPaint(g: Graphics2D, map: JXMapViewer, waypoints: Set<CustomWaypoint>) {
                    g.color = Color.RED
                    waypoints.forEach { wp ->
                        val pt = map.convertGeoPositionToPoint(wp.position)
                        g.fillOval((pt.x - 5).toInt(), (pt.y - 5).toInt(), 10, 10)
                    }
                }
            }
            painter.setWaypoints(waypoints)
            mapViewer.overlayPainter = painter
        }
    }

    override fun getPosition(): GeoPosition {
        return geoPosition
    }
}


