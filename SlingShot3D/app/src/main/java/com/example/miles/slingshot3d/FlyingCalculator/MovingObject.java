package com.example.miles.slingshot3d.FlyingCalculator;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;


public class MovingObject extends DrawableObject {
	protected Vector3d velocity;
	protected AxisAngle4d rotVel;
	protected double drag = 0.0;
	public MovingObject(){
		velocity = new Vector3d(10, 10, 10);
		rotVel = new AxisAngle4d(1, 0, 0, 0);
	}
	public MovingObject(Vector3d position, Vector3d inVelocity) {
		super();
		super.setPositon(position);
		rotVel = new AxisAngle4d();
		rotVel.set(1, 0, 0, 0); // x,y,z,a
		velocity = new Vector3d(inVelocity.getX(), inVelocity.getY(), inVelocity.getZ());
	}
	//TODO is touch ground 
}
