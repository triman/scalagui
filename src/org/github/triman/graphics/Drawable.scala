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
import java.awt.geom.Area

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
	
	/**
	 * Getter for the Shape object underneeth the drawable
	 */
	def shape : Shape
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
 * Drawable object associated with an affine transform. The transform is applied before all other
 * transforms.
 * @param drawable The drawable object to be transformed
 * @param transform The AffineTransform to apply to the Drawable.
 */
class TransformableDrawable(var drawable : Drawable, var transform : AffineTransform) extends Drawable{
	override def draw(g : Graphics2D, t : AffineTransform){
		val t2 = new AffineTransform(transform)
		t2.preConcatenate(t)
		drawable.draw(g, t2)
	}
	override def fill(g : Graphics2D, t : AffineTransform){
		val t2 = new AffineTransform(transform)
		t2.preConcatenate(t)
		drawable.fill(g, t2)
	}
	
	override def shape() : Shape = {
		transform.createTransformedShape(drawable.shape)
	}
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
	
	override def shape() : Shape = {
		val r = new Area()
		shapes.foreach(s => r add new Area(s.shape))
		r
	}
}

/**
 * Defines an object that can be drawn with a color
 * @param shape The shape to draw
 * @param fill The color used to fill the shape
 * @param color The border color
 * @param stroke The stroke that will be used
 */
class ColoredDrawableShape(val drawableshape : DrawableShape, var fill : Color, var color : Color, var stroke : Stroke) extends Drawable{
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
		drawableshape.draw(g,t)
		
		g setColor c
		g setStroke s
	}
	override def fill(g : Graphics2D, t : AffineTransform) = {
		val c = g.getColor
		if(fill != null){
			g setColor fill
		}
		drawableshape.fill(g,t)
		
		g setColor c
	}
	
	override def shape() : Shape = {
		drawableshape.shape
	}
	
}
object ColoredDrawableShapeCompanion{
	implicit def DrawableShape2ColoredDrawableShape(s : DrawableShape) = new ColoredDrawableShape(s)
	implicit def ColoredDrawableShape2DrawableShape(s : ColoredDrawableShape) = s.shape
}

/**
 * Defines a shape that won't be moved nor resized when drawn
 */
class UntransformedDrawable(drawable : Drawable) extends Drawable {
	override def draw(g : Graphics2D, t : AffineTransform){
		val t2 = new AffineTransform()
		t2.setToIdentity()
		drawable.draw(g, t2)
	}
	override def fill(g : Graphics2D, t : AffineTransform){
		val t2 = new AffineTransform()
		t2.setToIdentity()
		drawable.fill(g, t2)
	}
	
	override def shape() : Shape = {
		drawable.shape
	}
}

sealed abstract class HorizontalAnchor
sealed abstract class VerticalAnchor
object Top extends VerticalAnchor
object Bottom extends VerticalAnchor
object Left extends HorizontalAnchor
object Right extends HorizontalAnchor

class AnchoredDrawable(var verticalAnchor : VerticalAnchor, var horizontalAnchor : HorizontalAnchor, drawable : Drawable) extends UntransformedDrawable(drawable){
	override def draw(g : Graphics2D, t : AffineTransform){
		val t2 = new AffineTransform()
		t2.setToIdentity()
		var dx = 0
		var dy = 0
		
		if(verticalAnchor == Bottom){
			dy = g.getClipBounds().height - shape.getBounds().height
		}
		if(horizontalAnchor == Right){
			dx = g.getClipBounds().width - shape.getBounds().width
		}
		t2.translate(dx, dy)
		
		drawable.draw(g, t2)
	}
	override def fill(g : Graphics2D, t : AffineTransform){
		val t2 = new AffineTransform()
		t2.setToIdentity()
		var dx = 0
		var dy = 0
		
		if(verticalAnchor == Bottom){
			dy = g.getClipBounds().height - shape.getBounds().height
		}
		if(horizontalAnchor == Right){
			dx = g.getClipBounds().width - shape.getBounds().width
		}
		t2.translate(dx, dy)
		
		drawable.fill(g, t2)
	}
}
