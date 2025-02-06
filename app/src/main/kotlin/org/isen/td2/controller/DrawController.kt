package org.isen.td2.controller

import org.isen.td2.data.Shape
import org.isen.td2.model.DrawModel
import org.isen.td2.view.IDrawView

class DrawController(private val model:DrawModel) {
    private val views = mutableListOf<IDrawView>()

    fun displayAll(){
        views.forEach{i:IDrawView ->
            i.display()
        }
    }

    fun registerView(view:IDrawView){
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