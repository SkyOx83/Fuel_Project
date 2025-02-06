package org.isen.carburant.data

import java.awt.Color
import java.awt.Graphics

abstract class Shape {
    abstract val dot:Dot
    abstract val color: Color
    abstract fun draw(g: Graphics)
}