package org.anddev.andengine.util;

import android.util.FloatMath;


/**
 * <p>This class is basically a java-space replacement for the native {@link android.graphics.Matrix} class.</p>
 * 
 * <p>Math taken from <a href="http://www.senocular.com/flash/tutorials/transformmatrix/">senocular.com</a>.</p>
 * 
 * This class represents an affine transformation with the following matrix:
 * <pre> [ a , b , 0 ]
 * [ c , d , 0 ]
 * [ tx, ty, 1 ]</pre>
 * where:
 * <ul>
 *  <li><b>a</b> is the <b>x scale</b></li>
 *  <li><b>b</b> is the <b>y skew</b></li>
 *  <li><b>c</b> is the <b>x skew</b></li>
 *  <li><b>d</b> is the <b>y scale</b></li>
 *  <li><b>tx</b> is the <b>x translation</b></li>
 *  <li><b>ty</b> is the <b>y translation</b></li>
 * </ul>
 *
 * <p>TODO Think if that caching of Transformation through the TransformationPool really needs to be thread-safe or if one simple reused static Transform object is enough.</p>
 * 
 * @author Nicolas Gramlich
 * @since 15:47:18 - 23.12.2010
 */
public class Transformation {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private float a; /* x scale */
	private float b; /* y skew */
	private float c; /* x skew */
	private float d; /* y scale */
	private float tx; /* x translation */
	private float ty; /* y translation */

	// ===========================================================
	// Constructors
	// ===========================================================

	public Transformation() {
		this.a = 1.0f;
		this.d = 1.0f;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public String toString() {
		return "Transformation{[" + this.a + ", " + this.c + ", " + this.tx + "][" + this.b + ", " + this.d + ", " + this.ty + "][0.0, 0.0, 1.0]}";
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public void reset() {
		this.setToIdentity();
	}

	public void setToIdentity() {
		this.a = 1.0f;
		this.d = 1.0f;

		this.b = 0.0f;
		this.c = 0.0f;
		this.tx = 0.0f;
		this.ty = 0.0f;
	}

	public void setTo(final Transformation pTransformation) {
		this.a = pTransformation.a;
		this.d = pTransformation.d;

		this.b = pTransformation.b;
		this.c = pTransformation.c;
		this.tx = pTransformation.tx;
		this.ty = pTransformation.ty;
	}

	public void preTranslate(final float pX, final float pY) {
		this.preConcat(1, 0, 0, 1, pX, pY);
	}

	public void postTranslate(final float pX, final float pY) {
		this.postConcat(1, 0, 0, 1, pX, pY);
	}

	public Transformation setToTranslate(final float pX, final float pY) {
		this.a = 1;
		this.b = 0;
		this.c = 0;
		this.d = 1;
		this.tx = pX;
		this.ty = pY;

		return this;
	}

	public void preScale(final float pScaleX, final float pScaleY) {
		this.preConcat(pScaleX, 0, 0, pScaleY, 0, 0);
	}

	public void postScale(final float pScaleX, final float pScaleY) {
		this.postConcat(pScaleX, 0, 0, pScaleY, 0, 0);
	}

	public Transformation setToScale(final float pScaleX, final float pScaleY) {
		this.a = pScaleX;
		this.b = 0;
		this.c = 0;
		this.d = pScaleY;
		this.tx = 0;
		this.ty = 0;

		return this;
	}

	public void preRotate(final float pAngle) {
		final float angleRad = MathUtils.degToRad(pAngle);

		final float sin = FloatMath.sin(angleRad);
		final float cos = FloatMath.cos(angleRad);

		this.preConcat(cos, sin, -sin, cos, 0, 0);
	}

	public void postRotate(final float pAngle) {
		final float angleRad = MathUtils.degToRad(pAngle);

		final float sin = FloatMath.sin(angleRad);
		final float cos = FloatMath.cos(angleRad);

		this.postConcat(cos, sin, -sin, cos, 0, 0);
	}

	public Transformation setToRotate(final float pAngle) {
		final float angleRad = MathUtils.degToRad(pAngle);

		final float sin = FloatMath.sin(angleRad);
		final float cos = FloatMath.cos(angleRad);

		this.a = cos;
		this.b = sin;
		this.c = -sin;
		this.d = cos;
		this.tx = 0;
		this.ty = 0;

		return this;
	}

	public void postConcat(final Transformation pTransformation) {

		final float a2 = pTransformation.a;
		final float b2 = pTransformation.b;
		final float c2 = pTransformation.c;
		final float d2 = pTransformation.d;
		final float tx2 = pTransformation.tx;
		final float ty2 = pTransformation.ty;

		this.postConcat(a2, b2, c2, d2, tx2, ty2);
	}

	private void postConcat(final float pA, final float pB, final float pC, final float pD, final float pTX, final float pTY) {
		final float a = this.a;
		final float b = this.b;
		final float c = this.c;
		final float d = this.d;
		final float tx = this.tx;
		final float ty = this.ty;

		this.a = a * pA + b * pC;
		this.b = a * pB + b * pD;
		this.c = c * pA + d * pC;
		this.d = c * pB + d * pD;
		this.tx = tx * pA + ty * pC + pTX;
		this.ty = tx * pB + ty * pD + pTY;
	}

	public void preConcat(final Transformation pTransformation) {
		final float a1 = pTransformation.a;
		final float b1 = pTransformation.b;
		final float c1 = pTransformation.c;
		final float d1 = pTransformation.d;
		final float tx1 = pTransformation.tx;
		final float ty1 = pTransformation.ty;

		this.preConcat(a1, b1, c1, d1, tx1, ty1);
	}

	private void preConcat(final float pA, final float pB, final float pC, final float pD, final float pTX, final float pTY) {
		final float a = this.a;
		final float b = this.b;
		final float c = this.c;
		final float d = this.d;
		final float tx = this.tx;
		final float ty = this.ty;

		this.a = pA * a + pB * c;
		this.b = pA * b + pB * d;
		this.c = pC * a + pD * c;
		this.d = pC * b + pD * d;
		this.tx = pTX * a + pTY * c + tx;
		this.ty = pTX * b + pTY * d + ty;
	}

	public void transform(final float[] pVertices) {
		int count = pVertices.length / 2;
		int i = 0;
		int j = 0;
		while(--count >= 0) {
			final float x = pVertices[i++];
			final float y = pVertices[i++];
			pVertices[j++] = x * this.a + y * this.c + this.tx;
			pVertices[j++] = x * this.b + y * this.d + this.ty;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}