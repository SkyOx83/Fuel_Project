package org.isen.td2.data

import java.awt.Color
import java.awt.Graphics

// Dans les classes abstraites on évite de mettre des choses qui n'existe pas

abstract class Shape {
    abstract val dot:Dot
    abstract val color: Color
    abstract fun draw(g: Graphics)
}