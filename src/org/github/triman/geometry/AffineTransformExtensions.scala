package org.github.triman.geometry

import java.awt.geom.AffineTransform


object AffineTransformExtensions {
	lazy val identity = {
		val a = new AffineTransform
		a.setToIdentity
		a
	}
}