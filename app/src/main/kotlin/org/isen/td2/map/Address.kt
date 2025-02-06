package org.isen.td2.map

import kotlinx.serialization.json.Json
import org.isen.td2.NominatimResponse
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.serialization.Serializable

@Serializable
data class NominatimResponse(
    val place_id: Long,
    val licence: String,
    val lat: String,
    val lon: String
)


class Address {
    // pour éviter d'écrire une grosse ligne 44
    companion object {
        private val json = Json {
            ignoreUnknownKeys = true // IMPORTANT pour éviter les erreurs
        }

        // Fonction pour récupérer les coordonnées via l'API Nominatim
        public fun getCoordinatesFromAddress(address: String): Pair<Double, Double>? {
            try {
                // Encodage de l'adresse dans l'URL
                val formattedAddress = address.replace(" ", "+")
                val url = URL("https://nominatim.openstreetmap.org/search?q=$formattedAddress&format=json")

                // Ouvrir la connexion HTTP
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0"
                ) // Nécessaire pour éviter le blocage par Nominatim

                // Lire la réponse JSON
                val response = connection.inputStream.bufferedReader().use { it.readText() }

                // Désérialiser la réponse JSON avec kotlinx.serialization
                val jsonResponse = json.decodeFromString<List<NominatimResponse>>(response)

                // Vérifier si on a des résultats
                if (jsonResponse.isNotEmpty()) {
                    val lat = jsonResponse[0].lat.toDouble()
                    val lon = jsonResponse[0].lon.toDouble()
                    return Pair(lat, lon)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
}