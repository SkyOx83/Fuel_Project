package org.isen.td2.controller

import org.isen.td2.data.Shape
import org.isen.td2.model.DrawModel
import org.isen.td2.view.IFuelView

class DrawController(private val model:DrawModel) {
    private val views = mutableListOf<IFuelView>()

    fun displayAll(){
        views.forEach{i:IFuelView ->
            i.display()
        }
    }

    fun registerView(view:IFuelView){
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