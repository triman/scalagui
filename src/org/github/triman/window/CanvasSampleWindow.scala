package org.github.triman.window

import scala.swing.Frame
import java.awt.Dimension
import javax.swing.JFrame
import org.github.triman.graphics._
import java.awt.geom.RoundRectangle2D
import java.awt.Color
import java.awt.BasicStroke

object CanvasSampleWindow extends Frame{

	def main(args: Array[String]): Unit = {
			visible = true
			peer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
			val canvas = new Canvas
			canvas.preferredSize = new Dimension(1000,500)
			contents = canvas
			
			var s = new CompositeDrawableShape(
					new ColoredDrawableShape(DrawableShapeCompanion.Shape2DrawableShape(new RoundRectangle2D.Double(0,0,10,10,3,3)),null,Color.RED,
							new BasicStroke(3.0f, BasicStroke.CAP_BUTT,
							BasicStroke.JOIN_MITER, 10.0f, Array[Float](10), 0.0f)),
					new ColoredDrawableShape(DrawableShapeCompanion.Shape2DrawableShape(new RoundRectangle2D.Double(20,20,5,10,3,3)),Color.YELLOW,Color.GREEN)
					)
			
			canvas.shapes += s
	}

}