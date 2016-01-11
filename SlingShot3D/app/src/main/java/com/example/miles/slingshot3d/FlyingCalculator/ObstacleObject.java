package com.example.miles.slingshot3d.FlyingCalculator;

import android.content.Context;

import com.example.miles.slingshot3d.R;

import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Created by miles on 2015/12/29.
 */
public class ObstacleObject extends DrawableObject {
    public static final int OBSTACLE_TYPE_WALL = 0;
    public static final int OBSTACLE_TYPE_BOOSTER = 1;
    public static final int OBSTACLE_TYPE_BREAK = 2;
    public static final int OBSTACLE_TYPE_GROUND = 3;
    private int obstacleType;
    private double velocityChangeRate = 1.0;
    private List<ContactFace> contactFaces;
    private List<DrawableObject> models;

    public ObstacleObject(int obstacleType, int fileID, float[] inColor, Context ctx) {
        super(fileID, inColor, ctx);
        models = new LinkedList<DrawableObject>();
        models.add(this);
        this.obstacleType = obstacleType;
        contactFaces = new LinkedList<ContactFace>();
    }

    public void addModels(int fileID, float[] inColor, Context ctx) {
        models.add(new DrawableObject(fileID, inColor, ctx));
    }

    public void setContactFace(ContactFace contactFace) {
        contactFaces.add(contactFace);
    }

    public void calculateCollision(MonsterBallObject target) {
        Matrix4d transMatrix = getTransMatrix();
        for (ContactFace contactFace : contactFaces) {
            if (contactFace.getContactPosAndVec(target.getBallCenter(), target.getBallDiameter(), transMatrix)) {
                //System.out.println("hit");
                switch (obstacleType) {
                    case OBSTACLE_TYPE_WALL:
                        handleMonsterBallBounce(target, contactFace.getContactVector());
                        break;
                    case OBSTACLE_TYPE_BOOSTER:
                        handleBoost(target);
                        break;
                    case OBSTACLE_TYPE_BREAK:
                        handleBreak(target);
                        break;
                    case OBSTACLE_TYPE_GROUND:
                        handleVanish(target);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void handleMonsterBallBounce(MonsterBallObject target, Vector3d contactVector) {
        Vector3d targetVelocity = target.getVelocity();
        double bounceAngle = targetVelocity.angle(contactVector);
//		System.out.println("bounceAngle:" + bounceAngle);
        if (round(bounceAngle, 3) == round(Math.PI, 3)) {
            target.getVelocity().scale(-1);
            return;
        }
        Vector3d velocityRotAxis = new Vector3d();
        velocityRotAxis.cross(targetVelocity, contactVector);
        velocityRotAxis.normalize();
//		System.out.println(velocityRotAxis.toString());
//		System.out.println(2 * bounceAngle - Math.PI);
        Matrix4d velocityChange = new Matrix4d();
        velocityChange.set(1);
        velocityChange.setRotation(new AxisAngle4d(velocityRotAxis, 2 * bounceAngle - Math.PI));
//		System.out.println(velocityChange.toString());
//		System.out.println(target.getVelocity().toString());
        velocityChange.transform(targetVelocity);
        target.setVelocity(targetVelocity);
//		System.out.println(target.getVelocity().toString());
    }

    private void handleBoost(MovingObject target) {
        target.getVelocity().scale(velocityChangeRate);
    }

    private void handleBreak(MovingObject target) {
        target.getVelocity().scale(velocityChangeRate);
    }
    private void handleVanish(MovingObject target){
        target.getPositon().set(-999, -999, -999);
        target.getVelocity().set(0, 0, 0);
    }
    public static ObstacleObject create(int type, Vector3d position, Context ctx) {
        ObstacleObject obstacleObject = null;
        switch (type) {
            case OBSTACLE_TYPE_WALL:
                obstacleObject = new ObstacleObject(OBSTACLE_TYPE_WALL, R.raw.wallbase, new float[]{0.8f, 0.8f, 0.8f}, ctx);
                obstacleObject.addModels(R.raw.wallbrick, new float[]{178f / 255f, 34 / 255f, 34 / 255f}, ctx);
                obstacleObject.setPositon(position);
                ContactFace contactFace = new ContactFace(new Point3d(0, 0, 0), new Vector3d(0, -1, 0), new Vector3d(0, 0, 1), 40.5, 48, false);
                obstacleObject.setContactFace(contactFace);
                break;
            case OBSTACLE_TYPE_GROUND:
                obstacleObject = new ObstacleObject(OBSTACLE_TYPE_GROUND, R.raw.wallbase, new float[]{0.8f, 0.8f, 0.8f}, ctx);
                obstacleObject.setPositon(position);
                contactFace = new ContactFace(new Point3d(0, 0, 0), new Vector3d(0, 0, 1), new Vector3d(1, 0, 0), 405, 480, false);
                obstacleObject.setContactFace(contactFace);
            default:
                break;
        }
        return obstacleObject;
    }

    public static double round(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Override
    public boolean isLoaded() {
        boolean loaded = super.isLoaded();
        for (int i = 1; i < models.size(); i++) {
            loaded = loaded & models.get(i).isLoaded();
        }
        return loaded;
    }

    @Override
    public void draw(GL10 gl) {
        super.draw(gl);
        for (int i = 1; i < models.size(); i++) {
            models.get(i).draw(gl);
        }
    }
}
