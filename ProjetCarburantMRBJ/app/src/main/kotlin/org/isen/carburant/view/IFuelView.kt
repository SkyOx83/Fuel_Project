package org.isen.carburant.view

import java.beans.PropertyChangeListener

interface IFuelView: PropertyChangeListener {
    fun display()
    fun close()
}