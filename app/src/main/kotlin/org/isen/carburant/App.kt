package org.isen.carburant

import org.isen.carburant.controller.FuelController
import org.isen.carburant.model.FuelModel
import org.isen.carburant.view.impl.FuelTestView
import org.isen.carburant.view.impl.ShapeView

fun main() {
    val model = FuelModel()
    val controller = FuelController(model)

    //val testGui = FuelTestView(controller)
    val shapeView = ShapeView(controller)

    controller.displayAll()
}
