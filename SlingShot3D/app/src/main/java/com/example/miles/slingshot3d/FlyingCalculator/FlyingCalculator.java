package com.example.miles.slingshot3d.FlyingCalculator;

import java.util.LinkedList;

public class FlyingCalculator {
    protected double timeInc;
    protected static final double GRAVITY = -9.81;
    private MonsterBallObject monsterBallObject;
    private LinkedList<MovingObject> objects;
    private LinkedList<ObstacleObject> obstacles;
    private double gravityAddition;

    public FlyingCalculator(MovingObject object, int timeIncMS) {
        this(timeIncMS);
        objects.add(object);
    }

    public FlyingCalculator(int timeIncMS) {
        this.timeInc = timeIncMS / 1000.0;
        this.objects = new LinkedList<MovingObject>();
        this.obstacles = new LinkedList<ObstacleObject>();
        this.gravityAddition = GRAVITY * this.timeInc;

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
            monsterBallObject.getVelocity().z += gravityAddition;

            monsterBallObject.getPositon().x += monsterBallObject.getVelocity().x * timeInc;
            monsterBallObject.getPositon().y += monsterBallObject.getVelocity().y * timeInc;
            monsterBallObject.getPositon().z += monsterBallObject.getVelocity().z * timeInc;

            handleObstacle();
        }
    }

    public void handleObstacle() {
        if (monsterBallObject != null) {
            for (ObstacleObject obstableObject : obstacles) {
                obstableObject.calculateCollision(monsterBallObject);
            }
        }
    }
}
