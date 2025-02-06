package org.isen.carburant.view.impl

import org.isen.carburant.controller.FuelController
import org.isen.carburant.view.IFuelView
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.beans.PropertyChangeEvent
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants

class FuelTestView (val ctrl: FuelController):IFuelView, JFrame("Test View"), ActionListener {
    init{
        ctrl.registerView(this)
        preferredSize = Dimension(300, 200)

        //Cette ligne est super importante sinon on va avoir plusieurs fois la même opération
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE


        contentPane = JPanel().apply {
            //Le this n'est plus obligatoire à mettre
            this.layout = GridLayout(1, 2)
        }
        val myButton = JButton("push")
        myButton.addActionListener(this)
        contentPane.add(myButton)
        isVisible = false


        //C'est fait pour réduire les composants à leur taille préférée, il vaut mieux le mettre pour être sûr de respecter ce qui est demandé
        pack()
    }
    override fun display() {
        isVisible = true
    }

    override fun close() {
        isVisible = false
    }

    override fun propertyChange(evt: PropertyChangeEvent) {
        println("event: $evt")
    }

    override fun actionPerformed(e: ActionEvent?) {
        println("push")
        this.ctrl.updateModel()
    }
}