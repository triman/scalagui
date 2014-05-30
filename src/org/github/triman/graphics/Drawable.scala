/**
 * Drawable objects
 */
package org.github.triman.graphics

import java.awt.Graphics2D
import java.awt.Shape
import scala.collection.mutable.MutableList
import java.awt.Color
import java.awt.geom.AffineTransform
import org.github.triman.geometry.AffineTransformExtensions
import java.awt.Stroke

/**
 * Drawable interface. A drawable object is something that can be drawn
 * (its border is printed onto the Graphics2D object) and filled (its surface
 * is printed onto the Graphics2D).
 */
trait Drawable {
	/**
	 * Draw the border on the Graphics2D.
	 * @param g The Graphics2D where the object will be printed
	 */
	def draw(g : Graphics2D) : Unit = draw(g, AffineTransformExtensions.identity)
	/**
	 * Fill the object onto the Graphics2D
	 * @param g : The Graphics2D where the object will be printed
	 */
	def fill(g : Graphics2D) : Unit = fill(g, AffineTransformExtensions.identity)
	/**
	 * Draw a transformed version of the object
	 * @param g The Graphics2D object where the object will be drawn
	 * @param t The AffineTransform to apply to the object before drawing it
	 */
	def draw(g : Graphics2D, t : AffineTransform) : Unit
	/**
	 * Fill a transformed version of the object
	 * @param g The Graphics2D object where the object will be filled
	 * @param t The AffineTransform to apply to the object before filling it
	 */
	def fill(g : Graphics2D, t : AffineTransform) : Unit
}

/**
 * Basic drawable shape. It's an extension of the standard Java Shape interface
 * @see java.awt.Shape
 */
class DrawableShape(val shape : Shape) extends Drawable {
	override def draw(g : Graphics2D, t : AffineTransform){
		g.draw(t.createTransformedShape(shape))
	}
	override def fill(g : Graphics2D, t : AffineTransform){
		g.fill(t.createTransformedShape(shape))
	}
}

// mixes to add the DrawableShape trait to any Shape object
object DrawableShapeCompanion {
	implicit def Shape2DrawableShape(s : Shape) = new DrawableShape(s)
	implicit def DrawableShape2Shape(s : DrawableShape) = s.shape
}

/**
 * Drawable object composed of several drawables.
 * @param _shapes Shapes that will be composed in order to create the object
 */
class CompositeDrawableShape(_shapes : Drawable*) extends Drawable {
	/**
	 * Shapes that composes the object
	 */
	val shapes = new MutableList[Drawable]()
	
	shapes ++= _shapes
	
	override def draw(g : Graphics2D, t : AffineTransform) : Unit = {
		shapes.foreach(_.draw(g, t))
	}
	override def fill(g : Graphics2D, t : AffineTransform) : Unit = {
		shapes.foreach(_.fill(g,t))
	}
}

/**
 * Defines an object that can be drawn with a color
 * @param shape The shape to draw
 * @param fill The color used to fill the shape
 * @param color The border color
 * @param stroke The stroke that will be used
 */
class ColoredDrawableShape(val shape : DrawableShape, var fill : Color, var color : Color, var stroke : Stroke) extends Drawable{
	/**
	 * @param shape The shape to draw
	 */
	def this(shape : DrawableShape) = this(shape,null,null,null)
	/**
	 * @param shape The shape to draw
	 * @param fill The color used to fill the shape
	 * @param color The border color
	 */
	def this(shape : DrawableShape, fill : Color, color : Color) = this(shape, fill, color, null)
	
	override def draw(g : Graphics2D, t : AffineTransform) = {
		val c = g.getColor
		val s = g.getStroke
		if(color != null){
			g setColor color
		}
		if(stroke != null){
			g setStroke stroke
		}
		shape.draw(g,t)
		
		g setColor c
		g setStroke s
	}
	override def fill(g : Graphics2D, t : AffineTransform) = {
		val c = g.getColor
		if(fill != null){
			g setColor fill
		}
		shape.fill(g,t)
		
		g setColor c
	}
	
}
object ColoredDrawableShapeCompanion{
	implicit def DrawableShape2ColoredDrawableShape(s : DrawableShape) = new ColoredDrawableShape(s)
	implicit def ColoredDrawableShape2DrawableShape(s : ColoredDrawableShape) = s.shape
}