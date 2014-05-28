package org.github.triman.window

import scala.swing.Panel
import java.awt.Graphics2D
import java.awt.geom.Point2D
import java.awt.geom.AffineTransform
import scala.collection.mutable.MutableList
import scala.swing.event.MousePressed
import scala.swing.event.MouseDragged
import scala.swing.event.MouseReleased
import scala.swing.event.MouseWheelMoved
import java.awt.Point
import java.awt.geom.Ellipse2D
import java.awt.geom.NoninvertibleTransformException
import java.awt.RenderingHints
import org.github.triman.graphics.Drawable

class Canvas extends Panel{
	
	val shapes = new MutableList[Drawable]()
	
	private var currentX = 0.0
	private var currentY = 0.0
	private var zoom = 2.0
	private var previousX = 0.0
	private var previousY = 0.0
	
	listenTo(mouse.clicks, mouse.moves, mouse.wheel)
	reactions += {
      case e: MousePressed  => {
    	  previousX = e.point.getX()
    	  previousY = e.point.getY()
      }
      
      case e: MouseDragged  => {
			// Determine the old and new mouse coordinates based on the translated coordinate space.
			val adjPreviousPoint = getTranslatedPoint(previousX, previousY)
			val adjNewPoint = getTranslatedPoint(e.point.getX(), e.point.getY())
                 
                val newX = adjNewPoint.getX() - adjPreviousPoint.getX();
                val newY = adjNewPoint.getY() - adjPreviousPoint.getY();
 
                previousX = e.point.getX();
                previousY = e.point.getY();
                 
                currentX += newX;
                currentY += newY;
                 
                repaint();
      }
      case e : MouseWheelMoved => {
    	  zoom += .1 * -e.rotation
    	  zoom = Math.max(0.00001, zoom)
    	  repaint()
      }
    }
	
	private def currentTransform() = {
		val tx = new AffineTransform();
         
        val centerX = size.width.asInstanceOf[Double] / 2
        val centerY = size.height.asInstanceOf[Double] / 2
         
        tx.translate(centerX, centerY)
        tx.scale(zoom, zoom)
        tx.translate(currentX, currentY)
        
        tx
	}
	
	private def getTranslatedPoint(x : Double, y : Double) = {
		val tx = currentTransform
        val point2d = new Point2D.Double(x, y)
        try {
            tx.inverseTransform(point2d, null);
        } catch {
        	case e : NoninvertibleTransformException => {
        		e.printStackTrace()
        		null
        	}
        }
	}
	
	override def paintComponent(g: Graphics2D) = {
		super.paintComponent(g)
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON)
		
		shapes.foreach(s => {
				s.fill(g, currentTransform)
				s.draw(g, currentTransform)
			})
    }
	
	
	
	
	
}