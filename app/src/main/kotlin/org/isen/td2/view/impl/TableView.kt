package org.isen.td2.view.impl

import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

class TableView: JFrame("Stations Information") {
    init {
        // Configuration de la fenêtre
        defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        setSize(800, 400)
        layout = BorderLayout()

        // Définir les colonnes du tableau
        val columnNames = arrayOf(
            "Adresse", "Code Postal", "Ville",
            "Gazole", "E10", "SP98", "SP95", "E85", "GPLc",
            "Nourriture", "Station de Gonflage", "Toilettes"
        )

        // Créer le modèle de données
        val model = DefaultTableModel(columnNames, 0)

        // Ajouter des données d'exemple
        model.addRow(arrayOf(
            "123 Rue Exemple", "75000", "Paris",
            true, true, false, true, false, true,
            true, true, true
        ))

        // Créer le tableau
        val table = JTable(model)

        // Ajouter le tableau à un JScrollPane
        val scrollPane = JScrollPane(table)

        // Ajouter le JScrollPane à la fenêtre
        add(scrollPane, BorderLayout.CENTER)

        // Rendre la fenêtre visible
        isVisible = true
    }
}