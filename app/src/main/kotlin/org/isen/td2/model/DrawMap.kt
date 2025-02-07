package org.isen.td2.model


import kotlinx.serialization.json.Json
import org.isen.td2.NominatimResponse
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.serialization.Serializable
import org.apache.logging.log4j.kotlin.logger
import org.isen.td2.data.Shape


import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.viewer.DefaultTileFactory
import org.jxmapviewer.viewer.GeoPosition
import org.jxmapviewer.viewer.TileFactoryInfo
import org.jxmapviewer.viewer.WaypointPainter
import org.jxmapviewer.viewer.Waypoint
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import kotlin.properties.Delegates
import kotlin.system.exitProcess

@Serializable
data class NominatimResponse(
    val place_id: Long,
    val licence: String,
    var lat: String,
    var lon: String
)

lateinit var geoPosition: GeoPosition

class DrawMap {

    val minZoom: Int = 2
    val maxZoom:Int = 17


    @Serializable
    data class MapboxGeocodingResponse(
        val features: List<Feature>
    )

    @Serializable
    data class Feature(
        val geometry: Geometry
    )

    @Serializable
    data class Geometry(
        val coordinates: List<Double>
    )
        object MapboxGeocoding {
        private const val API_KEY = "pk.eyJ1IjoiZGpwb3VscGUiLCJhIjoiY202dGpnMWl1MDNkMzJqcjBrdXBmYmNyZCJ9.2jL5TCjYHtnb5x08gcDKrQ" // Key MapBox

            private val json = Json { ignoreUnknownKeys = true } // to have less big block of code

            // Fonction pour récupérer les coordonnées via l'API Mapbox Geocoding
        public fun getCoordinatesFromAddress(address: String): Pair<Double, Double>? {
            try {
                // Encodage de l'adresse dans l'URL
                val formattedAddress = address.replace(" ", "+")
                val url = URL("https://api.mapbox.com/geocoding/v5/mapbox.places/$formattedAddress.json?access_token=$API_KEY")

                // Ouvrir la connexion HTTP
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("User-Agent", "Mozilla/5.0")

                // Lire la réponse JSON
                val response = connection.inputStream.bufferedReader().use { it.readText() }

                // Désérialiser la réponse JSON avec kotlinx.serialization
                val jsonResponse = json.decodeFromString<MapboxGeocodingResponse>(response)


                // Vérifier si on a des résultats et retourner les coordonnées
                if (jsonResponse.features.isNotEmpty()) {
                    val lon = jsonResponse.features[0].geometry.coordinates[0]
                    val lat = jsonResponse.features[0].geometry.coordinates[1]
                    return Pair(lat, lon)
                } else {
                    logger.info("Erreur de géocodage : Aucune correspondance trouvée")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
        }
    companion object {
        fun AdjustMap(mapViewer: JXMapViewer){
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
        }

        fun centerMap(adresse: String, mapViewer: JXMapViewer):GeoPosition {
            val coordinates = MapboxGeocoding.getCoordinatesFromAddress(adresse)

            if (coordinates != null) {
                val (latitude, longitude) = coordinates
                logger.info("Coordonnees de $adresse")

                // Centrer la carte sur la position trouvée
                geoPosition = GeoPosition(latitude, longitude)
                mapViewer.addressLocation = geoPosition
                //CustomWaypoint.addWaypoint(mapViewer, geoPosition)
                mapViewer.zoom = 5
                return geoPosition
            } else {
                logger.error("Impossible de trouver l'adresse.")
                exitProcess(1)
            }
        }

        public fun Listen_Mouse_click(mapViewer: JXMapViewer){
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
        }

        public fun MapZoom(mapViewer: JXMapViewer){
            // Ajouter un MouseWheelListener pour gérer le zoom
            mapViewer.addMouseWheelListener { e: MouseWheelEvent ->
                val zoomDelta = e.preciseWheelRotation
                val newZoom = mapViewer.zoom + zoomDelta.toInt()  // Décrémenter le zoom lorsque la molette va vers le bas
                if (newZoom in 2..17) { // normalement minZoom et maxZoom
                    mapViewer.zoom = newZoom // Appliquer le zoom si dans la plage
                    logger.info("Zoom : $newZoom")  // Afficher le niveau de zoom dans la console
                }
            }
        }
        private val waypoints = mutableListOf<CustomWaypoint>()
        fun addWaypoint(mapViewer: JXMapViewer, position: GeoPosition) {

            val waypoint = CustomWaypoint(geoPosition)
            waypoints.add(waypoint)

            // Convertir la liste de CustomWaypoint en MutableSet<Waypoint>
            val waypointSet: MutableSet<Waypoint> = waypoints.toMutableSet()

            updateWaypointPainter(mapViewer, waypointSet)
            logger.info("enter in custom waypoint")
        }
        // Fonction pour récupérer tous les waypoints
        fun getAllWaypoints(): List<CustomWaypoint> = waypoints

        // Met à jour le WaypointPainter qui gère les affichages des waypoints
        private fun updateWaypointPainter(mapViewer: JXMapViewer, waypointset: MutableSet<Waypoint>) {
            val waypointPainter = WaypointPainter<Waypoint>()
            waypointPainter.setWaypoints(waypointset)
            mapViewer.overlayPainter = waypointPainter // Ajoute le WaypointPainter aux overlays
        }
    }

    private val pcs = PropertyChangeSupport(this)
    private val shapes = mutableListOf<Shape>()


    private var dummy:Int by Delegates.observable(0) { property, oldValue, newValue ->
        pcs.firePropertyChange(property.name, oldValue, newValue)
    }

    fun update(nb:Int) {
        dummy = nb
    }

    fun addShape(shape: Shape){
        shapes.add(shape)
        // Ici le null va forcer l'envoi du tableau sinon rien ne va se déclencher
        pcs.firePropertyChange("shapes", null, shapes)
    }

    fun addObserver(l: PropertyChangeListener){
        pcs.addPropertyChangeListener(l)
    }
}




