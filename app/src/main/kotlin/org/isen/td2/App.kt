package org.isen.td2
import kotlinx.serialization.*
import org.isen.td2.controller.DrawControllerMap
import org.isen.td2.view.impl.ShapeView
import org.isen.td2.model.DrawMap


@Serializable
data class NominatimResponse(
    val place_id: Long,
    val licence: String,
    val lat: String,
    val lon: String
)


fun main() {

    val model = DrawMap()
    val controller = DrawControllerMap(model)
    controller.setup("PARIS") // lieu par défaut
    val shapeView = ShapeView(controller)
    controller.displayAll()
}




