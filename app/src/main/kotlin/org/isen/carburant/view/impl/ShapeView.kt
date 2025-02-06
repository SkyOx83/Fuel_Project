package org.isen.carburant.view.impl

import com.sun.java.accessibility.util.AWTEventMonitor.addActionListener
import org.isen.carburant.controller.FuelController
import org.isen.carburant.data.Shape
import org.isen.carburant.view.IFuelView
import org.isen.carburant.widget.Dessin
import java.awt.*

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.beans.PropertyChangeEvent
import javax.swing.*
import javax.swing.border.EmptyBorder

class ShapeView(val ctrl: FuelController): IFuelView, JFrame("Shape View"), ActionListener {
    private val checkBoxes = mutableMapOf<String, JCheckBox>()
    private val checkStates = mutableListOf(Pair("Boutique alimentaire", 0), Pair("Station de gonflage", 0), Pair("Toilettes", 0), Pair("Tous les types", 1), Pair("Gazole", 0), Pair("SP 98", 0), Pair("SP 95", 0),
        Pair("E 10", 0), Pair("E 85", 0), Pair("GPLc", 0)
    )
    private val shapeComboBox = JComboBox(arrayOf("Tous les types", "Gazole", "SP 98", "SP 95", "E 10", "E 85", "GPLc"))
    private var searchText = ""
    private val searchButton = JButton("Rechercher")
    private val itinerairePanel = JPanel()
    private val itineraireSearchField1 = JTextField(20)
    private val itineraireSearchField2 = JTextField(20)
    private var itinerairesearchText1 = ""
    private var itinerairesearchText2 = ""
    private var itinerairesearchButton = JButton("Rechercher")
    private val searchPanel = JPanel()
    private var searchField = JTextField(20)

    private val map: Dessin

    init{
        ctrl.registerView(this)
        preferredSize = Dimension(800, 600)

        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE


        contentPane = JPanel().apply {
            this.layout = BorderLayout()
        }
        val txt = JTextArea()
        map = Dessin()
        contentPane.add(map, BorderLayout.CENTER)
        contentPane.add(makeGui(), BorderLayout.WEST)
        //contentPane.add(makeGuiSearch(), BorderLayout.)
        isVisible = false
        pack()
    }


    private fun makeGui() =  JPanel().apply {
        this.layout = FlowLayout()
        this.add(makeShapePanel())
        preferredSize = Dimension(250, height)
    }


    private fun makeShapePanel() =  JPanel().apply {
        layout = BorderLayout()

        /*val searchButton = JButton("Rechercher").apply {
            preferredSize = Dimension(40, 25) // Réduction de la taille du bouton
            addActionListener {
                searchText = searchField // Sauvegarde du texte dans la variable
                println("Recherche: $searchText")
            }
        }

        searchField.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER) {
                    searchText = searchField // Sauvegarde du texte lors de l'appui sur Entrée
                    println("Recherche: $searchText")
                }
            }
        }*/


        searchPanel.layout = FlowLayout()
        searchPanel.add(searchField)
        searchPanel.add(searchButton)
        searchPanel.isVisible = true

        val searchArea = JPanel().apply {
            this.layout = GridLayout(3, 1, 5, 5) // Espacement entre les lignes

            searchButton.addActionListener {
                searchText = searchField.text
                println("Recherche: $searchText")
            }

            searchField.addKeyListener(object : KeyAdapter() {
                override fun keyPressed(e: KeyEvent) {
                    if (e.keyCode == KeyEvent.VK_ENTER) {
                        searchText = searchField.text
                        println("Recherche: $searchText")
                    }
                }
            })
            /*this.add(JTextField(20)).apply {
                border = EmptyBorder(20, 0, 0, 0) // Marge autour du champ de texte
            }*/
            /*val searchField = JTextField(15).apply {
                layout = GridLayout(1, 1, 5, 0)
                //border = EmptyBorder(20, 0, 0, 0) // Marge autour du champ de texte
            }

            //Image de loupe 🔍
            val searchButton = JButton("Rechercher").apply {
                preferredSize = Dimension(40, 25) // Réduction de la taille du bouton
                addActionListener {
                    searchText = searchField // Sauvegarde du texte dans la variable
                    println("Recherche: $searchText")
                }
            }

            searchField.addKeyListener(object : KeyAdapter() {
                override fun keyPressed(e: KeyEvent) {
                    if (e.keyCode == KeyEvent.VK_ENTER) {
                        searchText = searchField // Sauvegarde du texte lors de l'appui sur Entrée
                        println("Recherche: $searchText")
                    }
                }
            })*/

            //this.add(searchField)
            //this.add(searchButton)
        }



        val shapesPanel = JPanel().apply{

            val labelCriteres = JLabel("Critères : ").apply {
                horizontalAlignment = SwingConstants.CENTER
            }
            add(labelCriteres)
            isVisible = true

            layout = GridLayout(7, 1, 0, 10) // Espacement entre les lignes

            val carburantPanel = JPanel().apply {
                layout = FlowLayout(/*FlowLayout.CENTER, 5, 0*/)
                add(JLabel("Carburant :"))
                add(shapeComboBox)
            }
            add(carburantPanel)

            shapeComboBox.addActionListener {
                val selectedShape = shapeComboBox.selectedItem as String
                handleShapeSelection(selectedShape)
            }
            addButtonWithCheckBox("Boutique alimentaire", "Boutique alimentaire")
            addButtonWithCheckBox("Station de gonflage", "Station de gonflage")
            addButtonWithCheckBox("Toilettes", "Toilettes")
        }


        itinerairePanel.layout = GridLayout(3, 2, 5, 5)
        itinerairePanel.add(JLabel("Départ:"))
        itinerairePanel.add(itineraireSearchField1)
        itinerairePanel.add(JLabel("Arrivée:"))
        itinerairePanel.add(itineraireSearchField2)
        itinerairePanel.add(itinerairesearchButton)
        itinerairePanel.isVisible = false

        itinerairesearchButton.addActionListener {
            itinerairesearchText1 = itineraireSearchField1.text
            itinerairesearchText2 = itineraireSearchField2.text
            println("Recherche départ: $itinerairesearchText1")
            println("Recherche arrivée: $itinerairesearchText2")
        }

        /*itineraireSearchField1.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER) {
                    itinerairesearchText1 = itineraireSearchField1.text
                    println("Recherche départ: $itinerairesearchText1")
                }
            }
        })

        itineraireSearchField2.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER) {
                    itinerairesearchText2 = itineraireSearchField2.text
                    println("Recherche Arrivée: $itinerairesearchText2")
                }
            }
        })*/

        val topPanel= JPanel().apply {
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
        add(searchPanel, BorderLayout.CENTER)
        add(itinerairePanel, BorderLayout.BEFORE_LINE_BEGINS)
        add(shapesPanel, BorderLayout.SOUTH)
    }

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

            if (s is MutableList<*>) {
                map.shapes = s as MutableList<Shape>
                this.repaint()
            }
        }
    }

    private fun handleShapeSelection(shape: String) {
        /*val isSelected = when (shape) {
            "Tous les types" -> {
                println("type")
                1
            }
            "Gazole" -> {
                println("gazole")
                1
            }
            "SP 98" -> {
                println("98")
                1
            }
            "SP 95" -> {
                println("95")
                1
            }
            "E 10" -> {
                println("10")
                1
            }
            "E 85" -> {
                println("85")
                1
            }
            "GPLc" -> {
                println("GPLc")
                1
            }
            else -> 0
        }
        // Afficher les états de sélection
        println("Sélection des formes : $shape -> $isSelected")
         */

        // Trouver l'index de l'option précédemment sélectionnée et mettre à jour son état à 0
        checkStates.forEachIndexed { index, pair ->
            if (pair.second == 1) {
                checkStates[index] = Pair(pair.first, 0)
            }
        }

        // Mettre à jour l'état de l'option nouvellement sélectionnée à 1
        val index = checkStates.indexOfFirst { it.first == shape }
        if (index != -1) {
            checkStates[index] = Pair(shape, 1)
        }

        // Afficher les états de sélection
        println("Sélection des formes : $checkStates")
    }

    override fun actionPerformed(e: ActionEvent) { //methode qui sert à exécuter la commande
        val checkbox = checkBoxes[e.actionCommand]
        if (checkbox != null) {
            checkbox.isSelected = !checkbox.isSelected
            //checkStates.add(Pair(e.actionCommand, if (checkbox.isSelected) 1 else 0))

            val index = checkStates.indexOfFirst { it.first == e.actionCommand }
            if (index != -1) {
                checkStates[index] = Pair(e.actionCommand, if (checkbox.isSelected) 1 else 0)
            }
            println("chexk: $checkStates")
        }
        if (e.actionCommand == "itineraire") {
            itinerairePanel.isVisible = true
            searchPanel.isVisible = false

        } else if (e.actionCommand == "ville") {
            itinerairePanel.isVisible = false
            searchPanel.isVisible = true
        }
    }
    fun GetCheckState (void: Void) : MutableList<Pair<String, Int>> {
        return checkStates
    }
}

/*
class ShapeView(val ctrl: FuelController) : IFuelView, JFrame("Shape View"), ActionListener {
    private val checkBoxes = mutableMapOf<String, JCheckBox>()
    private val shapeComboBox = JComboBox(arrayOf("Tous les types", "Gazole", "SP 98", "SP 95", "E10", "B 10", "E 85"))
    private val searchField = JTextField(20)
    private val searchButton = JButton("Rechercher")
    private val itinerairePanel = JPanel()
    private val itineraireSearchField1 = JTextField(20)
    private val itineraireSearchField2 = JTextField(20)
    private val itinerairesearchButton = JButton("Rechercher")
    private val searchPanel = JPanel()
    private val sidebar = JPanel()
    private val map: Dessin

    init {
        ctrl.registerView(this)
        preferredSize = Dimension(800, 600)
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        contentPane = JPanel(BorderLayout())

        map = Dessin()
        contentPane.add(map, BorderLayout.CENTER)
        contentPane.add(makeSidebar(), BorderLayout.WEST)
        isVisible = false
        pack()
    }

    private fun makeSidebar() = sidebar.apply {
        preferredSize = Dimension(250, height)
        layout = BorderLayout()
        add(makeTopButtonsPanel(), BorderLayout.NORTH)
        add(makeSearchPanel(), BorderLayout.CENTER)
        add(makeCriteriaPanel(), BorderLayout.SOUTH)
    }

    private fun makeTopButtonsPanel() = JPanel(GridLayout(1, 2, 5, 5)).apply {
        add(JButton("Ville").apply {
            addActionListener(this@ShapeView)
            actionCommand = "ville"
        })
        add(JButton("Itinéraire").apply {
            addActionListener(this@ShapeView)
            actionCommand = "itineraire"
        })
    }

    private fun makeSearchPanel() = searchPanel.apply {
        layout = GridLayout(2, 1, 5, 5)
        add(searchField)
        add(searchButton)
        searchButton.addActionListener {
            println("Recherche: ${searchField.text}")
        }
    }

    private fun makeCriteriaPanel() = JPanel(BorderLayout()).apply {
        val labelCriteres = JLabel("Critères : ").apply {
            horizontalAlignment = SwingConstants.CENTER
        }
        add(labelCriteres, BorderLayout.NORTH)

        val criteresPanel = JPanel(GridLayout(6, 1, 0, 10)).apply {
            add(shapeComboBox)
            shapeComboBox.addActionListener {
                println("Sélection: ${shapeComboBox.selectedItem}")
            }
            addButtonWithCheckBox("rectangle", "Boutique alimentaire")
            addButtonWithCheckBox("cercle", "Station de gonflage")
            addButtonWithCheckBox("elipse", "Toilettes")
        }
        add(criteresPanel, BorderLayout.CENTER)
    }

    private fun JPanel.addButtonWithCheckBox(command: String, label: String) {
        val panel = JPanel(FlowLayout(FlowLayout.LEFT))
        panel.add(JButton(label).apply {
            addActionListener(this@ShapeView)
            actionCommand = command
        })
        val checkBox = JCheckBox().apply { isEnabled = false }
        checkBoxes[command] = checkBox
        panel.add(checkBox)
        add(panel)
    }

    private fun makeItinerairePanel() = itinerairePanel.apply {
        layout = GridLayout(3, 1, 5, 5)
        removeAll()
        add(JLabel("Départ:"))
        add(itineraireSearchField1)
        add(JLabel("Arrivée:"))
        add(itineraireSearchField2)
        itinerairesearchButton.preferredSize = Dimension(250, 40)
        add(itinerairesearchButton)
        itinerairesearchButton.addActionListener {
            println("Départ: ${itineraireSearchField1.text}, Arrivée: ${itineraireSearchField2.text}")
        }
    }
}
*/