package com.example.miles.slingshot3d.FlyingCalculator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

public class FlyingCalculator {
    protected double timeInc;
    protected static final double GRAVITY = -9.81;
    private MonsterBallObject monsterBallObject;
    private LinkedList<MovingObject> objects;
    private LinkedList<ObstacleObject> obstacles;
    private double gravityAddition;
    private boolean isTouchGround = false;
    private final Vector3d noVelocity;

    public FlyingCalculator(MovingObject object, int timeIncMS) {
        this(timeIncMS);
        objects.add(object);
    }

    public FlyingCalculator(int timeIncMS) {
        this.timeInc = timeIncMS / 1000.0;
        this.objects = new LinkedList<MovingObject>();
        this.obstacles = new LinkedList<ObstacleObject>();
        this.gravityAddition = GRAVITY * this.timeInc;
        this.noVelocity = new Vector3d(0,0,0);
    }

    public void setMonsterBall(MonsterBallObject monsterBall) {
        this.monsterBallObject = monsterBall;
    }

    public void addObstacle(ObstacleObject obstacle) {
        obstacles.add(obstacle);
    }

    public void addObject(MovingObject object) {
        objects.add(object);
    }

    public void nextFrame() {
        if (monsterBallObject != null) {
            if (monsterBallObject.getVelocity().equals(noVelocity)){
                isTouchGround = true;
                return;
            }
            monsterBallObject.getVelocity().z += gravityAddition;
            Vector3d vector3d = new Vector3d(monsterBallObject.getPositon());

            vector3d.x += monsterBallObject.getVelocity().x * timeInc;
            vector3d.y += monsterBallObject.getVelocity().y * timeInc;
            vector3d.z += monsterBallObject.getVelocity().z * timeInc;
            monsterBallObject.setPositon(vector3d);

            handleObstacle();
        }
    }
    public float[] getFlyingRoute(Vector3d velocity, int maxCount){
        List<Point3f> points = new ArrayList<Point3f>(maxCount);

        //TODO

        float[] result = new float[points.size()*3];
        return result;
    }
    public boolean isTouchGround(){
        return isTouchGround;
    }
    public void setIsTouchGround(boolean isTouchGround){
        this.isTouchGround = isTouchGround;
    }
    public void handleObstacle() {
        if (monsterBallObject != null) {
            for (ObstacleObject obstableObject : obstacles) {
                obstableObject.calculateCollision(monsterBallObject);
            }
        }
    }
}
