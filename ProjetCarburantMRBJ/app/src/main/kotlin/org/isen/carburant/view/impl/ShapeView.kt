package org.isen.carburant.view.impl

import org.isen.carburant.controller.FuelController
import org.isen.carburant.data.Shape
import org.isen.carburant.view.IFuelView
import org.isen.carburant.widget.Dessin
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.beans.PropertyChangeEvent
import javax.swing.*
import javax.swing.border.EmptyBorder


class ShapeView(val ctrl: FuelController): IFuelView, JFrame("Shape View"), ActionListener {
    private var xTxt = JTextField(10)
    private var yTxt = JTextField(10)
    //private val dessin = Dessin()
    private val checkBoxes = mutableMapOf<String, JCheckBox>()
    private val shapeComboBox = JComboBox(arrayOf("A", "B", "C", "D"))


    private val dessin: Dessin

    init{
        ctrl.registerView(this)
        preferredSize = Dimension(300, 200)

        //Cette ligne est super importante sinon on va avoir plusieurs fois la même opération
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE


        contentPane = JPanel().apply {
            //Le this n'est plus obligatoire à mettre
            this.layout = BorderLayout()
        }
        val txt = JTextArea()
        dessin = Dessin()
        contentPane.add(dessin, BorderLayout.CENTER)
        contentPane.add(makeGui(), BorderLayout.WEST)
        //contentPane.add(makeGuiSearch(), BorderLayout.)
        isVisible = false
        pack()

        layout = FlowLayout()
        add(makeShapePanel())

        add(JLabel("Sélectionnez une forme :"))
        add(shapeComboBox)

        shapeComboBox.addActionListener {
            val selectedShape = shapeComboBox.selectedItem as String
            handleShapeSelection(selectedShape)
        }
    }


    private fun makeGui() =  JPanel().apply {
        this.layout = FlowLayout()
        //this.add(makeCoor())
        this.add(makeShapePanel())
        //this.add(makeSearch())
    }

    /*private fun makeGuiSearch() = JPanel().apply{
        //this.layout = FlowLayout()

        //add(makeShapePanel())
        //this.add(makeSearch())
    }

     */



    private fun makeShapePanel() =  JPanel().apply {
        layout = BorderLayout()

        val searchArea = JPanel().apply {
            this.layout = GridLayout(2, 1, 5, 5) // Espacement entre les lignes
            this.add(JTextField(20)).apply {
                border = EmptyBorder(20, 0, 0, 0) // Marge autour du champ de texte
            }

            val labelCriteres = JLabel("Critères : ").apply {
                horizontalAlignment = SwingConstants.CENTER
            }
            add(labelCriteres)
            isVisible = true
        }

        val shapesPanel = JPanel().apply{
            //layout = GridLayout(8,1)
            layout = GridLayout(5, 1, 0, 10) // Espacement entre les lignes
            //border = EmptyBorder(10, 10, 10, 10) // Marge autour du panneau

            /*val labelCriteres = JLabel("Critères : ").apply {
                horizontalAlignment = SwingConstants.CENTER
            }
            add(labelCriteres)
            isVisible = true*/

            addButtonWithCheckBox("carre", "Carburant")
            addButtonWithCheckBox("rectangle", "Boutique alimentaire")
            addButtonWithCheckBox("cercle", "Station de gonflage")
            addButtonWithCheckBox("elipse", "Toilettes")

            /*this.add(JButton("Carburant").apply {
                addActionListener(this@ShapeView) // ici on va écouter ce que fait le button
                actionCommand = "carre"
            })

            this.add(JButton("Boutique alimentaire").apply {
                addActionListener(this@ShapeView)
                actionCommand = "rectangle"
            })

            this.add(JButton("Station de gonflage").apply {
                addActionListener(this@ShapeView)
                actionCommand = "cercle"
            })

            this.add(JButton("Toilettes").apply {
                addActionListener(this@ShapeView)
                actionCommand = "elipse"
            })*/
        }

        val topPanel= JPanel().apply {
            //this.layout = GridLayout(1,2)
            layout = GridLayout(1, 2, 5, 0) // Espacement entre les lignes
            this.add(JButton("Ville").apply {
                addActionListener(this@ShapeView) // ici on va écouter ce que fait le button
                actionCommand = "ville"
            })

            this.add(JButton("Initéraire").apply {
                addActionListener(this@ShapeView) // ici on va écouter ce que fait le button
                actionCommand = "itineraire"
            })
        }



        add(topPanel, BorderLayout.NORTH)
        add(searchArea, BorderLayout.CENTER)
        //add(labelCriteres, BorderLayout.CENTER)
        add(shapesPanel, BorderLayout.SOUTH)
    }

    /*private fun makeCoor() =  JPanel().apply {
        this.layout = GridLayout(2,1)
        this.add(JLabel("x: "))
        this.add(xTxt, BorderLayout.CENTER)
        //this.add((JLabel("y: ")))
        //this.add((yTxt))
    }*/

    private fun JPanel.addButtonWithCheckBox(command: String, label: String) {
        this.add(JButton(label).apply {
            addActionListener(this@ShapeView)
            actionCommand = command
        })
        val checkBox = JCheckBox().apply {
            isEnabled = false // La case à cocher est uniquement mise à jour par le bouton
        }
        checkBoxes[command] = checkBox
        add(checkBox)
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
        //checkBoxes[e.actionCommand]?.isSelected = true // Met à jour la case à cocher
        val checkbox = checkBoxes[e.actionCommand]
        if (checkbox != null) {
            if (checkbox.isSelected == true)
            {
                    println("ok true")
                    checkbox.isSelected = false // Met à jour la case à cocher
            }
            else if (checkBoxes[e.actionCommand]?.isSelected == false)
            {
                    println("ok false")
                    checkbox.isSelected = true // Met à jour la case à cocher
            }
        }
        if (e.actionCommand == "ville") {

        }
        if (e.actionCommand == "itineraire") {

        }
        /*if (e.actionCommand =="carre") {
            val dot = Dot(xTxt.text.toInt(), yTxt.text.toInt()) //xtxt est un text file
            //pour ecrire les random, on fait 1 .. 200
            val shape = Carre(dot, Color.RED, (1..200).random())
            ctrl.addShape(shape)
        } else if (e.actionCommand == "rectangle") {
            val dot = Dot(xTxt.text.toInt(), yTxt.text.toInt()) //xtxt est un text file
            //pour ecrire les random, on fait 1 .. 200
            val shape = Rectangle(dot, Color.RED, Dimension((1..200).random(), (1..200).random()))
            ctrl.addShape(shape)
        }*/
    }
}

object DropdownMenuExample {
    fun main(args: Array<String?>?) {
        SwingUtilities.invokeLater({ DropdownFrame() })
    }
}

internal class DropdownFrame : JFrame() {
    private val dropdown: JComboBox<String>
    private val selectedLabel: JLabel

    init {
        setTitle("Menu Déroulant Swing")
        setSize(300, 200)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
        setLayout(FlowLayout())


        val options: Array<String> = arrayOf("Option 1", "Option 2", "Option 3", "Option 4")
        dropdown = JComboBox(options)
        dropdown.addActionListener(DropdownListener())


        selectedLabel = JLabel("Sélection : " + dropdown.getSelectedItem())

        add(dropdown)
        add(selectedLabel)

        setVisible(true)
    }

    private inner class DropdownListener : ActionListener {
        @Override
        override fun actionPerformed(e: ActionEvent?) {
            selectedLabel.setText("Sélection : " + dropdown.getSelectedItem())
        }
    }
}

private fun handleShapeSelection(shape: String) {
    when (shape) {
        "Option 1" -> {
            // Logique derrière l'option 1
        }
        "Option 2" -> {
            // Logique derrière l'option 2
        }
        "Option 3" -> {
            // Logique derrière l'option 3
        }
        "Option 4" -> {
            // Logique derrière l'option 4
        }
    }
}