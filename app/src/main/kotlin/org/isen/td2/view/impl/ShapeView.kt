package org.isen.td2.view.impl

import org.apache.logging.log4j.kotlin.logger
import org.isen.td2.controller.DrawControllerMap

import org.isen.td2.model.clearWaypoints
import org.isen.td2.model.updateWaypointPainter
import org.isen.td2.model.waypointsList
import org.isen.td2.view.IFuelView
import org.jxmapviewer.JXMapViewer
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.beans.PropertyChangeEvent

import javax.imageio.ImageIO
import java.io.File
import javax.swing.*

object Carburant {
    val checkBoxesImpl = mutableListOf(
        Pair("Boutique alimentaire", 0),
        Pair("Station de gonflage", 0),
        Pair("Toilettes", 0)
    )
    val checkStates = mutableListOf(
        Pair("Boutique alimentaire", 0),
        Pair("Station de gonflage", 0),
        Pair("Toilettes", 0),
        Pair("Tous les types", 1),
        Pair("Gazole", 0),
        Pair("SP 98", 0),
        Pair("SP 95", 0),
        Pair("E 10", 0),
        Pair("E 85", 0),
        Pair("GPLc", 0)
    )
}

class ShapeView(val ctrl: DrawControllerMap) : IFuelView, JFrame("Gazogo"), ActionListener {
    private val checkBoxes = mutableMapOf<String, JCheckBox>()


    private val shapeComboBox = JComboBox(arrayOf("Tous les types", "Gazole", "SP 98", "SP 95", "E 10", "E 85", "GPLc"))
    private val searchField = JTextField(20)
    private val searchButton = JButton("Rechercher")

    private val itinerairePanel = JPanel()
    private val itineraireSearchField1 = JTextField(15)
    private val itineraireSearchField2 = JTextField(15)
    private val itineraireSearchButton = JButton("Rechercher")

    private val searchPanel = JPanel()
    private val mainPanel = JPanel(GridBagLayout())


    private val mapViewer: JXMapViewer = ctrl.getMapViewer()

    init {
        ctrl.registerView(this)
        preferredSize = Dimension(800, 600)
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE



        contentPane = JPanel().apply {
            layout = BorderLayout()
        }

        contentPane.add(mapViewer, BorderLayout.CENTER)
        contentPane.add(makeGui(), BorderLayout.WEST)

        setWindowIcon()
        isVisible = true
        pack()
    }

    private fun makeGui(): JPanel {
        val constraints = GridBagConstraints()
        constraints.insets = Insets(5, 5, 5, 5)
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.anchor = GridBagConstraints.NORTH

        val topPanel = JPanel(GridLayout(1, 2, 5, 0))
        val villeButton = JButton("Ville").apply {
            addActionListener(this@ShapeView)
            actionCommand = "ville"
        }
        val itineraireButton = JButton("Itinéraire").apply {
            addActionListener(this@ShapeView)
            actionCommand = "itineraire"
        }
        topPanel.add(villeButton)
        topPanel.add(itineraireButton)

        constraints.gridx = 0
        constraints.gridy = 0
        mainPanel.add(topPanel, constraints)

        searchPanel.layout = FlowLayout()
        searchPanel.add(searchField)
        searchPanel.add(searchButton)
        constraints.gridy = 1
        mainPanel.add(searchPanel, constraints)

        searchButton.addActionListener {
            clearWaypoints(mapViewer)
            val searchText = searchField.text
            if (searchText != ""){
                logger.info("Recherche: $searchText")
                ctrl.setup(searchText)
                ctrl.setupData(searchText)
                searchField.text = ""
            }

        }

        searchField.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER) {
                    clearWaypoints(mapViewer)
                    mapViewer.overlayPainter = null
                    val searchText = searchField.text
                    if (searchText != "") {
                        logger.info("Recherche: $searchText")
                        ctrl.setup(searchText)
                        ctrl.setupData(searchText)
                        searchField.text = ""
                    }
                }
            }
        })

        itinerairePanel.layout = GridLayout(3, 2, 5, 5)
        itinerairePanel.add(JLabel("Départ:"))
        itinerairePanel.add(itineraireSearchField1)
        itinerairePanel.add(JLabel("Arrivée:"))
        itinerairePanel.add(itineraireSearchField2)
        itinerairePanel.add(itineraireSearchButton)
        itinerairePanel.isVisible = false

        itineraireSearchButton.addActionListener {
            val itinerairesearchText1 = itineraireSearchField1.text
            val itinerairesearchText2 = itineraireSearchField2.text
            logger.info("Recherche départ: $itinerairesearchText1")
            logger.info("Recherche arrivée: $itinerairesearchText2")
        }

        constraints.gridy = 2
        mainPanel.add(itinerairePanel, constraints)

        val shapesPanel = JPanel(GridBagLayout())
        val shapeConstraints = GridBagConstraints()
        shapeConstraints.fill = GridBagConstraints.HORIZONTAL
        shapeConstraints.insets = Insets(5, 5, 5, 5)

        shapeConstraints.gridy = 0
        shapesPanel.add(JLabel("Critères : "), shapeConstraints)

        val carburantPanel = JPanel(FlowLayout())
        carburantPanel.add(JLabel("Carburant :"))
        carburantPanel.add(shapeComboBox)
        shapeConstraints.gridy = 1
        shapesPanel.add(carburantPanel, shapeConstraints)

        shapeComboBox.addActionListener {
            val selectedShape = shapeComboBox.selectedItem as String
            handleShapeSelection(selectedShape)
        }

        var yPosition = 2
        for (pair in Carburant.checkBoxesImpl) {
            addButtonWithCheckBox(pair.first, pair.first, shapesPanel, yPosition)
            yPosition++
        }

        constraints.gridy = 3
        constraints.gridwidth = 2
        mainPanel.add(shapesPanel, constraints)

        // Ajouter un bouton pour ouvrir la nouvelle page
        val openTableButton = JButton("Afficher le Tableau")
        openTableButton.addActionListener {
            // Créer et afficher la nouvelle fenêtre avec le tableau
            TableView()
        }

        constraints.gridy = yPosition + 1
        constraints.gridwidth = GridBagConstraints.REMAINDER
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.weightx = 1.0
        mainPanel.add(openTableButton, constraints)

        return mainPanel
    }

    private fun handleShapeSelection(Shape: String) {
        // Liste des options présentes dans le ComboBox
        val comboBoxOptions = listOf("Tous les types", "Gazole", "SP 98", "SP 95", "E 10", "E 85", "GPLc")

        // Remettre à 0 uniquement les éléments présents dans le ComboBox et qui ne sont pas sélectionnés
        Carburant.checkStates.forEachIndexed { index, pair ->
            if (comboBoxOptions.contains(pair.first) && pair.second == 1) {
                Carburant.checkStates[index] = Pair(pair.first, 0)
            }
        }

        // Mettre à jour l'état de l'option nouvellement sélectionnée à 1
        val index = Carburant.checkStates.indexOfFirst { it.first == Shape }
        if (index != -1) {
            Carburant.checkStates[index] = Pair(Shape, 1)
        }

        // Afficher les états de sélection
        logger.info("Sélection des formes : $Carburant.checkStates")

    }


    private fun setWindowIcon() {
        val iconPath = "C:\\Users\\jande\\Documents\\Project\\TD2\\app\\src\\main\\resources\\icone.png"
        val iconFile = File(iconPath)
        if (iconFile.exists()) {
            val icon = ImageIO.read(iconFile)
            this.iconImage = icon
        } else {
            println("⚠️ Icône introuvable : $iconPath")
        }
    }

    private fun addButtonWithCheckBox(command: String, label: String, panel: JPanel, yPos: Int) {
        val button = JButton(label).apply {
            addActionListener(this@ShapeView)
            actionCommand = command
        }
        val checkBox = JCheckBox().apply {
            isEnabled = false
        }
        checkBoxes[command] = checkBox

        val constraints = GridBagConstraints()
        constraints.gridy = yPos
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.insets = Insets(5, 5, 5, 5)

        panel.add(button, constraints)
        constraints.gridx = 1
        panel.add(checkBox, constraints)
    }

    override fun actionPerformed(e: ActionEvent) {
        val checkbox = checkBoxes[e.actionCommand]
        if (checkbox != null) {
            checkbox.isSelected = !checkbox.isSelected
            val index = Carburant.checkStates.indexOfFirst { it.first == e.actionCommand }
            if (index != -1) {
                Carburant.checkStates[index] = Pair(e.actionCommand, if (checkbox.isSelected) 1 else 0)
            }
            logger.info("check: $Carburant.checkStates")
        }
        if (e.actionCommand == "itineraire") {
            itinerairePanel.isVisible = true
            searchPanel.isVisible = false
            searchField.isVisible = false
            searchButton.isVisible = false
        } else if (e.actionCommand == "ville") {
            itinerairePanel.isVisible = false
            searchPanel.isVisible = true
            searchField.isVisible = true
            searchButton.isVisible = true
        }
    }
    companion object {
        fun GetCheckState(void: Void): MutableList<Pair<String, Int>> {
            return Carburant.checkStates
        }
    }

    override fun display() {
        isVisible = true
    }

    override fun close() {
        isVisible = false
    }

    override fun propertyChange(evt: PropertyChangeEvent) {

    }
}
