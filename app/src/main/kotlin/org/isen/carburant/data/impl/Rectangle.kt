package org.isen.carburant.data.impl

import org.isen.carburant.data.Dot
import org.isen.carburant.data.Shape
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics

open class Rectangle(override val dot: Dot, override val color: Color, val size: Dimension) : Shape() {
    override fun draw(g: Graphics) {
        g.color
        g.fillRect(dot.x, dot.y, size.width, size.height)
    }
}