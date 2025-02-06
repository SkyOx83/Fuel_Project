package org.isen.carburant.widget

import org.isen.carburant.data.Shape
import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel

class Dessin  : JPanel() {
    var shapes = mutableListOf<Shape>()

    override fun paintComponent(g: Graphics) {


        super.paintComponent(g)
        background = Color.WHITE
        println("affiche")
        shapes.forEach{ s ->
            println("dessin $s")
            s.draw(g)
        }
    }
}