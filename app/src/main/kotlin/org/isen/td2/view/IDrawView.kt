package org.isen.td2.view

import java.beans.PropertyChangeListener

interface IDrawView: PropertyChangeListener {
    fun display()
    fun close()
}