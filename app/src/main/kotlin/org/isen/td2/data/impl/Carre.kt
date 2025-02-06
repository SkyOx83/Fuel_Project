package org.isen.td2.data.impl

import org.isen.td2.data.Dot
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics

class Carre(dot: Dot, color:Color, cote:Int): Rectangle(dot, color, Dimension(cote, cote)) {
}