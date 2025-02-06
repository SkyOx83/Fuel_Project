package org.isen.carburant.model

import org.isen.carburant.data.Shape
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import kotlin.properties.Delegates

class FuelModel() {
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