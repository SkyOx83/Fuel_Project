package org.isen.carburant.controller

import org.isen.carburant.data.Shape
import org.isen.carburant.model.FuelModel
import org.isen.carburant.view.IFuelView

class FuelController (private val model: FuelModel) {
    private val views = mutableListOf<IFuelView>()

    fun displayAll(){
        views.forEach{i: IFuelView ->
            i.display()
        }
    }

    fun registerView(view: IFuelView){
        model.addObserver(view)
        views.add(view)
    }

    fun addShape(shape: Shape){
        model.addShape(shape)
    }

    fun updateModel(){
        model.update(5)
    }
}