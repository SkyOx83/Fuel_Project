package org.isen.td2.widget

import org.isen.td2.data.Shape
import org.isen.td2.data.impl.Rectangle
import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel

class Dessin : JPanel() {
    var shapes = mutableListOf<Shape>()

    override fun paintComponent(g: Graphics) {


        super.paintComponent(g)
        background = Color.WHITE
        //g.color = Color.BLACK
        //g.fillRect(100, 100, 150, 200)
        println("affiche")
        shapes.forEach{ s ->
            println("dessin $s")
            s.draw(g)

            // Cette partie n'est pas du tout la bonne chose à faire mais c'est quand même une autre manière de faire
            /*
            if (s is Rectangle) {
                g.color = s.color
                g.fillRect(s.dot.x, 100, 150, 200)
            }
             */

        }
    }
}