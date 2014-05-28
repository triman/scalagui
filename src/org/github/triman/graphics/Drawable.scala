package org.github.triman.graphics

import java.awt.Graphics2D
import java.awt.Shape
import scala.collection.mutable.MutableList
import java.awt.Color
import java.awt.geom.AffineTransform
import org.github.triman.geometry.AffineTransformExtensions

trait Drawable {
	def draw(g : Graphics2D) : Unit = draw(g, AffineTransformExtensions.identity)
	def fill(g : Graphics2D) : Unit = fill(g, AffineTransformExtensions.identity)
	def draw(g : Graphics2D, t : AffineTransform) : Unit
	def fill(g : Graphics2D, t : AffineTransform) : Unit
}

// standard drawables

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

// composite drawable shape
class CompositeDrawableShape(_shapes : DrawableShape*) extends Drawable {
	shapes ++= _shapes
	
	val shapes = new MutableList[DrawableShape]()
	
	override def draw(g : Graphics2D, t : AffineTransform) : Unit = {
		shapes.foreach(_.draw(g, t))
	}
	override def fill(g : Graphics2D, t : AffineTransform) : Unit = {
		shapes.foreach(_.draw(g,t))
	}
}

// a drawable shape with some color...
class ColoredDrawableShape(val shape : DrawableShape, var fill : Color, var color : Color) extends Drawable{
	def this(shape : DrawableShape) = this(shape,null,null)
	
	override def draw(g : Graphics2D, t : AffineTransform) = {
		val c = g.getColor
		if(color != null){
			g setColor color
		}
		shape.draw(g,t)
		
		g setColor c
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