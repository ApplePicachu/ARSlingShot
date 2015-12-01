package com.example.miles.slingshot3d.FlyingCalculator;

import java.util.LinkedList;

public class FlyingCalculator {
	protected double timeInc;
	protected static final double GRAVITY = -9.81;

	private LinkedList<MovingObject> objects;
	private double gravityAddition;

	public FlyingCalculator(MovingObject object, int timeIncMS) {
		this(timeIncMS);
		objects.add(object);
	}

	public FlyingCalculator(int timeIncMS) {
		this.timeInc = timeIncMS/1000.0;
		this.objects = new LinkedList<MovingObject>();
		this.gravityAddition = GRAVITY * this.timeInc;
		
	}
	public void addObject(MovingObject object){
		objects.add(object);
	}
	public void nextFrame() {
		for (MovingObject object : objects) {
			object.velocity.z += gravityAddition;

			object.positon.x += object.velocity.x * timeInc;
			object.positon.y += object.velocity.y * timeInc;
			object.positon.z += object.velocity.z * timeInc;
		}
	}
}
