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
import java.awt.image.BufferedImage
import java.beans.PropertyChangeEvent
import javax.swing.*
import javax.swing.border.EmptyBorder

class ShapeView(val ctrl: FuelController) : IFuelView, JFrame("Shape View"), ActionListener {
    private val checkBoxes = mutableMapOf<String, JCheckBox>()
    private val checkBoxesImpl = mutableListOf(Pair("Boutique alimentaire", 0), Pair("Station de gonflage", 0), Pair("Toilettes", 0))
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
    private var itineraireSearchButton = JButton("Rechercher")
    private val searchPanel = JPanel()
    private var searchField = JTextField(20)

    private val mainPanel = JPanel(GridBagLayout())

    private val map: Dessin


    init {
        ctrl.registerView(this)
        preferredSize = Dimension(800, 600)

        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

        contentPane = JPanel().apply {
            this.layout = BorderLayout()
        }
        map = Dessin()
        contentPane.add(map, BorderLayout.CENTER)
        contentPane.add(makeGui(), BorderLayout.WEST)
        isVisible = false
        pack()
    }

    // Charger l'image
    /*val imagePath = "../src/main/ressources/logo.jpg" // Remplacez par le chemin de votre image
    val originalImageIcon = ImageIcon(imagePath)

    // Redimensionner l'image
    val targetWidth = 200 // Largeur cible
    val targetHeight = 150 // Hauteur cible
    val resizedImageIcon = resizeImage(originalImageIcon.image, targetWidth, targetHeight)

    // Afficher l'image dans un JLabel
    val imageLabel = JLabel(ImageIcon(resizedImageIcon))

    // Ajouter le JLabel à un JPanel
    val panel = JPanel()
    panel.add(imageLabel)

    // Ajouter le JPanel à la fenêtre
    frame.contentPane.add(panel)
    frame.isVisible = true*/

    fun resizeImage(image: Image, width: Int, height: Int): Image {
        val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g2d = bufferedImage.createGraphics()
        g2d.drawImage(image, 0, 0, width, height, null)
        g2d.dispose()
        return bufferedImage
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

        // Barre de recherche
        searchPanel.layout = FlowLayout()
        searchPanel.add(searchField)
        searchPanel.add(searchButton)
        constraints.gridy = 1
        mainPanel.add(searchPanel, constraints)

        searchButton.addActionListener {
            val searchText = searchField.text
            println("Recherche: $searchText")
        }

        searchField.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER) {
                    val searchText = searchField.text
                    println("Recherche: $searchText")
                }
            }
        })

        // Itinéraire panel
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
            println("Recherche départ: $itinerairesearchText1")
            println("Recherche arrivée: $itinerairesearchText2")
        }

        constraints.gridy = 2
        mainPanel.add(itinerairePanel, constraints)

        // Critères de sélection
        val shapesPanel = JPanel(GridBagLayout())
        val shapeConstraints = GridBagConstraints()
        shapeConstraints.fill = GridBagConstraints.HORIZONTAL
        shapeConstraints.insets = Insets(5, 5, 5, 5)

        shapeConstraints.gridy = 0
        shapesPanel.add(JLabel("Critères : "), shapeConstraints)

        // Type de carburant
        val carburantPanel = JPanel(FlowLayout())
        carburantPanel.add(JLabel("Carburant :"))
        carburantPanel.add(shapeComboBox)
        shapeConstraints.gridy = 1
        shapesPanel.add(carburantPanel, shapeConstraints)

        shapeComboBox.addActionListener {
            val selectedShape = shapeComboBox.selectedItem as String
            handleShapeSelection(selectedShape)
        }

        // Ajouter les cases à cocher
        var yPosition = 2
        for (pair in checkBoxesImpl) {
            addButtonWithCheckBox(pair.first, pair.first, shapesPanel, yPosition)
            yPosition++
        }

        constraints.gridy = 3
        mainPanel.add(shapesPanel, constraints)

        return mainPanel
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

    override fun display() {
        isVisible = true
    }

    override fun close() {
        isVisible = false
    }

    override fun propertyChange(evt: PropertyChangeEvent) {
        if (evt.propertyName == "shapes") {
            val s = evt.newValue

            if (s is MutableList<*>) {
                map.shapes = s as MutableList<Shape>
                this.repaint()
            }
        }
    }

    private fun handleShapeSelection(shape: String) {
        // Liste des options présentes dans le ComboBox
        val comboBoxOptions = listOf("Tous les types", "Gazole", "SP 98", "SP 95", "E 10", "E 85", "GPLc")

        // Remettre à 0 uniquement les éléments présents dans le ComboBox et qui ne sont pas sélectionnés
        checkStates.forEachIndexed { index, pair ->
            if (comboBoxOptions.contains(pair.first) && pair.second == 1) {
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

    override fun actionPerformed(e: ActionEvent) {
        val checkbox = checkBoxes[e.actionCommand]
        if (checkbox != null) {
            checkbox.isSelected = !checkbox.isSelected
            val index = checkStates.indexOfFirst { it.first == e.actionCommand }
            if (index != -1) {
                checkStates[index] = Pair(e.actionCommand, if (checkbox.isSelected) 1 else 0)
            }
            println("chexk: $checkStates")
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

    fun GetCheckState(void: Void): MutableList<Pair<String, Int>> {
        return checkStates
    }
}


/*    private val checkBoxes = mutableMapOf<String, JCheckBox>()
    private val checkBoxesImpl = mutableListOf(Pair("Boutique alimentaire", 0), Pair("Station de gonflage", 0), Pair("Toilettes", 0))
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
    private var itineraireSearchButton = JButton("Rechercher")
    private val searchPanel = JPanel()
    private var searchField = JTextField(20)

    private val mainPanel = JPanel(GridBagLayout())


    private val map: Dessin

    init{
        ctrl.registerView(this)
        preferredSize = Dimension(800, 600)

        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE


        contentPane = JPanel().apply {
            this.layout = BorderLayout()
        }
        //val txt = JTextArea()
        map = Dessin()
        contentPane.add(map, BorderLayout.CENTER)
        contentPane.add(makeGui(), BorderLayout.WEST)
        //contentPane.add(makeGuiSearch(), BorderLayout.)
        isVisible = false
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

        // Barre de recherche
        val searchPanel = JPanel(FlowLayout())
        searchPanel.add(searchField)
        searchPanel.add(searchButton)
        constraints.gridy = 1
        //
        searchPanel.isVisible = true
        mainPanel.add(searchPanel, constraints)

        searchButton.addActionListener {
            val searchText = searchField.text
            println("Recherche: $searchText")
        }

        searchField.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER) {
                    val searchText = searchField.text
                    println("Recherche: $searchText")
                }
            }
        })

        // Itinéraire panel
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
            println("Recherche départ: $itinerairesearchText1")
            println("Recherche arrivée: $itinerairesearchText2")
        }

        constraints.gridy = 2
        mainPanel.add(itinerairePanel, constraints)

        // Critères de sélection
        val shapesPanel = JPanel(GridBagLayout())
        val shapeConstraints = GridBagConstraints()
        shapeConstraints.fill = GridBagConstraints.HORIZONTAL
        shapeConstraints.insets = Insets(5, 5, 5, 5)

        shapeConstraints.gridy = 0
        shapesPanel.add(JLabel("Critères : "), shapeConstraints)

        // Type de carburant
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
        for (pair in checkBoxesImpl) {
            addButtonWithCheckBox(pair.first, pair.first, shapesPanel, yPosition)
            yPosition++
        }

        constraints.gridy = 3
        mainPanel.add(shapesPanel, constraints)

        return mainPanel
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


        /*searchPanel.layout = FlowLayout()
        searchPanel.add(searchField)
        searchPanel.add(searchButton)
        searchPanel.isVisible = true*/



    private fun JPanel.addButtonWithCheckBox(command: String, label: String, panel: JPanel, yPos: Int) {
        this.add(JButton(label).apply {
            addActionListener(this@ShapeView)
            actionCommand = command
        })
        val checkBox = JCheckBox().apply {
            isEnabled = false // La case à cocher est uniquement mise à jour par le bouton
        }
        checkBoxes[command] = checkBox

        val constraints = GridBagConstraints()
        constraints.gridy = yPos
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.insets = Insets(5, 5, 5, 5)

        //add(checkBox)

        //panel.add(button, constraints)
        constraints.gridx = 1
        panel.add(checkBox, constraints)
    }


    override fun display() {
        isVisible = true
    }

    override fun close() {
        isVisible = false
    }

    override fun propertyChange(evt: PropertyChangeEvent) {
        if (evt.propertyName == "shapes") {
            val s = evt.newValue

            if (s is MutableList<*>) {
                map.shapes = s as MutableList<Shape>
                this.repaint()
            }
        }
    }

    private fun handleShapeSelection(shape: String) {
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

    override fun actionPerformed(e: ActionEvent) {
        val checkbox = checkBoxes[e.actionCommand]
        if (checkbox != null) {
            checkbox.isSelected = !checkbox.isSelected
            val index = checkStates.indexOfFirst { it.first == e.actionCommand }
            if (index != -1) {
                checkStates[index] = Pair(e.actionCommand, if (checkbox.isSelected) 1 else 0)
            }
            println("chexk: $checkStates")
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
    fun GetCheckState (void: Void) : MutableList<Pair<String, Int>> {
        return checkStates
    }
}*/