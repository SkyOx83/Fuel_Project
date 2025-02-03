package org.isen.carburant.data.impl

import org.isen.carburant.data.Dot
import java.awt.Color
import java.awt.Dimension

class Carre(dot: Dot, color: Color, cote:Int): Rectangle(dot, color, Dimension(cote, cote)) {
}