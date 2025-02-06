package org.isen.td2.view.impl

import org.isen.td2.controller.DrawController
import org.isen.td2.data.Dot
import org.isen.td2.data.Shape
import org.isen.td2.data.impl.Carre
import org.isen.td2.data.impl.Rectangle
import org.isen.td2.view.IDrawView
import org.isen.td2.widget.Dessin
import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.viewer.DefaultTileFactory
import org.jxmapviewer.viewer.TileFactoryInfo
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.beans.PropertyChangeEvent
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.WindowConstants
import javax.swing.border.Border

class ShapeView(val ctrl: DrawController): IDrawView, JFrame("Shape View"), ActionListener {
    private var xTxt = JTextField(10)
    private var yTxt = JTextField(10)
    //private val dessin = Dessin()

    private val dessin: Dessin

    init{
        ctrl.registerView(this)
        preferredSize = Dimension(800, 600)

        //Cette ligne est super importante sinon on va avoir plusieurs fois la même opération
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE


        contentPane = JPanel().apply {
            //Le this n'est plus obligatoire à mettre
            this.layout = BorderLayout()
        }
        val txt = JTextArea()
        dessin = Dessin()
        contentPane.add(dessin, BorderLayout.CENTER)
        contentPane.add(makeGui(), BorderLayout.SOUTH)
        /*
        val mapViewer = JXMapViewer()

        val maxZoom:Int = 17

        // Utilisation de TileFactoryInfo si OSMTileFactoryInfo ne fonctionne pas
        val tileFactoryInfo = object : TileFactoryInfo(
            "OpenStreetMap",
            1, maxZoom, maxZoom, 256, true, true,
            "https://tile.openstreetmap.org", "x", "y", "z"
        ) {
            override fun getTileUrl(x: Int, y: Int, zoom: Int): String {
                val adjustedZoom = maxZoom - zoom // Ajuster le zoom pour correspondre aux niveaux valides
                return "https://tile.openstreetmap.org/$adjustedZoom/$x/$y.png"
            }
        }

        val tileFactory = DefaultTileFactory(tileFactoryInfo)
        mapViewer.tileFactory = tileFactory

        // Centrer la carte sur une position spécifique
        mapViewer.addressLocation = org.jxmapviewer.viewer.GeoPosition(48.8566, 2.3522) // Paris
        mapViewer.zoom = 4 // Zoom initial

        contentPane.add(mapViewer, BorderLayout.CENTER )
        */
        isVisible = false
        //C'est fait pour réduire les composants à leur taille préférée, il vaut mieux le mettre pour être sûr de respecter ce qui est demandé
        pack()
    }

    /*
    private fun makeGui(): JPanel{
        return JPanel().apply {
            layout = BorderLayout
        }
    }
     */

    //Même chose que ce qui a été mis au dessus en commentaire
    private fun makeGui() =  JPanel().apply {
        this.layout = FlowLayout()
        this.add(makeCoor())
        this.add(makeShapePanel())
    }

    private fun makeShapePanel() =  JPanel().apply {
        this.layout = GridLayout(2,2)
        this.add(JButton("Carre").apply {
            addActionListener(this@ShapeView) // ici on va écouter ce que fait le button
            actionCommand = "carre"
        })
        this.add(JButton("Rectangle").apply {
            addActionListener(this@ShapeView)
            actionCommand = "rectangle"
        })
        this.add(JButton("Cercle").apply {
            addActionListener(this@ShapeView)
            actionCommand = "cercle"
        })
        this.add(JButton("Elipse").apply {
            addActionListener(this@ShapeView)
            actionCommand = "elipse"
        })
    }

    private fun makeCoor() =  JPanel().apply {
        this.layout = GridLayout(2,2)
        this.add(JLabel("x: "))
        this.add(xTxt)
        this.add((JLabel("y: ")))
        this.add((yTxt))
    }


    override fun display() {
        isVisible = true
    }

    override fun close() {
        isVisible = false
    }

    override fun propertyChange(evt: PropertyChangeEvent) {
        println("event: $evt") //a bannir et utiliser le logger à la place
        if (evt.propertyName == "shapes") {
            val s = evt.newValue

            // On met une étoile car la comparaison s'arrête au type et pas à la valeur
            if (s is MutableList<*>) {
                //ici on le cast mais on ne fai
                dessin.shapes = s as MutableList<Shape>
                this.repaint()
            }
        }
    }

    override fun actionPerformed(e: ActionEvent) { //methode qui sert à exécuter la commande
        if (e.actionCommand =="carre") {
            val dot = Dot(xTxt.text.toInt(), yTxt.text.toInt()) //xtxt est un text file
            //pour ecrire les random, on fait 1 .. 200
            val shape = Carre(dot, Color.RED, (1..200).random())
            ctrl.addShape(shape)
        } else if (e.actionCommand == "rectangle") {
            val dot = Dot(xTxt.text.toInt(), yTxt.text.toInt()) //xtxt est un text file
            //pour ecrire les random, on fait 1 .. 200
            val shape = Rectangle(dot, Color.RED, Dimension((1..200).random(), (1..200).random()))
            ctrl.addShape(shape)
        }
    }
}