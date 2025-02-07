package org.isen.td2.view

import java.beans.PropertyChangeListener

interface IFuelView: PropertyChangeListener {
    fun display()
    fun close()
}