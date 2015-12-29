package com.example.miles.slingshot3d.FlyingCalculator;

import android.content.Context;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;


public class MovingObject extends DrawableObject {
    protected Vector3d velocity;
    protected AxisAngle4d rotVel;
    protected double drag = 0.0;

    public MovingObject() {
        super();
        initBaseMO();
    }

    public MovingObject(float[] Vert, float[] inColor) {
        super(Vert, inColor);
        initBaseMO();
    }

    public MovingObject(int fileID, float[] inColor, Context context) {
        super(fileID, inColor, context);
        initBaseMO();
    }

    public MovingObject(Vector3d position, Vector3d inVelocity) {
        super();
        super.setPositon(position);
        rotVel = new AxisAngle4d();
        rotVel.set(1, 0, 0, 0); // x,y,z,a
        velocity = new Vector3d(inVelocity.getX(), inVelocity.getY(), inVelocity.getZ());
    }

    public MovingObject(Vector3d position, Vector3d inVelocity, float[] Vert, float[] inColor) {
        super(Vert, inColor);
        super.setPositon(position);
        rotVel = new AxisAngle4d();
        rotVel.set(1, 0, 0, 0); // x,y,z,a
        velocity = new Vector3d(inVelocity.getX(), inVelocity.getY(), inVelocity.getZ());
    }

    public MovingObject(Vector3d position, Vector3d inVelocity, int fileID, float[] inColor, Context ctx) {
        super(fileID, inColor, ctx);
        super.setPositon(position);
        rotVel = new AxisAngle4d();
        rotVel.set(1, 0, 0, 0); // x,y,z,a
        velocity = new Vector3d(inVelocity.getX(), inVelocity.getY(), inVelocity.getZ());
    }

    public Vector3d getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3d velocity) {
        this.velocity = velocity;
    }

    public AxisAngle4d getRotVel() {
        return rotVel;
    }

    public void setRotVel(AxisAngle4d rotVel) {
        this.rotVel = rotVel;
    }

    private void initBaseMO() {
        velocity = new Vector3d(10, 10, 10);
        rotVel = new AxisAngle4d(1, 0, 0, 0);
    }


    //TODO is touch ground
}
