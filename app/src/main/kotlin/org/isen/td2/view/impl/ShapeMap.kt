package org.isen.td2.view.impl

import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFrame
import org.isen.td2.controller.DrawControllerMap
import org.jxmapviewer.JXMapViewer

class ShapeMap {
init {



}
    public fun displayMap(mapViewer: JXMapViewer){
        val frame = JFrame("Fill & Rest")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(800, 600)

        // ✅ Changer l'icône de la fenêtre
        val iconPath = "C:\\Users\\jande\\Documents\\Project\\TD2\\app\\src\\main\\resources\\icone.png"
        val iconFile = File(iconPath)
        if (iconFile.exists()) {
            val icon = ImageIO.read(iconFile)
            frame.iconImage = icon
        } else {
            println("⚠️ Icône introuvable : $iconPath")
        }


        frame.add(mapViewer)
        frame.isVisible = true
    }


}