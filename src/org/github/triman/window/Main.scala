package org.github.triman.window

import scala.swing.Frame
import java.awt.Dimension
import javax.swing.JFrame
import org.github.triman.graphics._
import java.awt.geom.RoundRectangle2D
import java.awt.Color

object MainFrame extends Frame{

	def main(args: Array[String]): Unit = {
			visible = true
			peer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
			val canvas = new Canvas
			canvas.preferredSize = new Dimension(1000,500)
			contents = canvas
			
			var s = new ColoredDrawableShape(DrawableShapeCompanion.Shape2DrawableShape(new RoundRectangle2D.Double(0,0,10,10,3,3)),null,Color.RED)
			
			canvas.shapes += s
	}

}