package com.example.miles.slingshot3d.FlyingCalculator;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

public class DrawableObject {
	protected Vector3d positon;
	protected Matrix3d attitude;
	protected Matrix4d transMatrix;
	protected double mass;
	protected Object object;
	protected  boolean isDraw = true;
	//TODO bounding box
	public DrawableObject() {
		positon = new Vector3d();
		attitude = new Matrix3d();
		attitude.setIdentity();
		transMatrix = new Matrix4d();
		transMatrix.setIdentity();
		mass = 1;
	}
	public Matrix4d getTransMatrix(){
		transMatrix.setIdentity();
		transMatrix.setRotation(attitude);
		transMatrix.setTranslation(positon);
		return transMatrix;
	}

	public float[] getTransMatrixf(){
		transMatrix.setIdentity();
		transMatrix.setRotation(attitude);
		transMatrix.setTranslation(positon);

		return new float[]{
				(float) transMatrix.m00, (float) transMatrix.m10, (float) transMatrix.m20, (float) transMatrix.m30,
				(float) transMatrix.m01, (float) transMatrix.m11, (float) transMatrix.m21, (float) transMatrix.m31,
				(float) transMatrix.m02, (float) transMatrix.m12, (float) transMatrix.m22, (float) transMatrix.m32,
				(float) transMatrix.m03, (float) transMatrix.m13, (float) transMatrix.m23, (float) transMatrix.m33,

		};
	}

	public String getPosString(){
		return positon.x+"\t"+positon.y+"\t"+positon.z;
	}
	public Vector3d getPositon() {
		return positon;
	}
	public void setPositon(Vector3d positon) {
		this.positon = positon;
	}
	public void setAttitude(Matrix3d attitude) {
		this.attitude = attitude;
	}
	
}
	
