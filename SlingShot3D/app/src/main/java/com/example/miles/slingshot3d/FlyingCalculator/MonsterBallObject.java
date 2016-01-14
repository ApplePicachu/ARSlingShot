package com.example.miles.slingshot3d.FlyingCalculator;

import android.content.Context;

import com.example.miles.slingshot3d.R;

import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Created by miles on 2015/12/29.
 */
public class MonsterBallObject extends MovingObject {
    private final double ballDiameter;
    private Point3d ballCenter;

//    this(top)                                                             0
//    private DrawableObject ballHShellRing;                                1
//    private DrawableObject ballHShellTop1;                                2
//    private DrawableObject ballHShellRing1;                               3
//    private DrawableObject ballHShellButtonBase;                          4
//    private DrawableObject ballHShellButton;                              5

    private List<DrawableObject> models;

    private float openAngle = 0;

    public MonsterBallObject(Context context) {
        super(R.raw.pocketmonballshelltop, new float[]{1, 1, 1}, context);
        models = new LinkedList<DrawableObject>();
        models.add(this);//0
        addModels(R.raw.pocketmonballshellring, new float[]{0, 0, 0}, context);//1
        addModels(R.raw.pocketmonballshelltop, new float[]{1, 0, 0}, context);//2
        addModels(R.raw.pocketmonballshellring, new float[]{0, 0, 0}, context);//3

        addModels(R.raw.pocketmonballbutton, new float[]{0.5f, 0.5f, 0.5f}, context);//4
        addModels(R.raw.pocketmonballbuttonbase, new float[]{0.5f, 0.5f, 0.5f}, context);//5

        Matrix3d matrix3d = new Matrix3d();
        matrix3d.setIdentity();
        matrix3d.rotY(Math.PI);
        for (int i = 2; i < 6; i++) {
            models.get(i).setAttitude(matrix3d);
        }
        ballDiameter = 50;
    }

    public void addModels(int fileID, float[] inColor, Context ctx) {
        models.add(new DrawableObject(fileID, inColor, ctx));

        Matrix3d matrix3d = new Matrix3d(models.get(0).attitude);
        Vector3d vector3d = new Vector3d(models.get(0).positon);

        for (int i = 1; i < models.size(); i++) {
            models.get(i).setPositon(vector3d);
            models.get(i).setAttitude(matrix3d);
        }
    }

    @Override
    public boolean isLoaded() {
        if (super.isLoaded() && models.get(1).isLoaded() && models.get(2).isLoaded()
                && models.get(3).isLoaded() && models.get(4).isLoaded() && models.get(5).isLoaded()) {
            return true;
        } else {
            return false;
        }
    }

    public double getBallDiameter() {
        return ballDiameter;
    }

    public Point3d getBallCenter() {
        ballCenter = new Point3d(getPositon());
        return ballCenter;
    }

    @Override
    public void draw(GL10 gl) {
        super.draw(gl);
        models.get(1).draw(gl);
        Vector3d vector3d = new Vector3d(models.get(0).getPositon());

        gl.glTranslatef((float)vector3d.getX(), (float)vector3d.getY(), (float)vector3d.getZ());
        gl.glTranslatef(0, -30, 0);
        gl.glRotatef(openAngle, 1, 0, 0);
        gl.glTranslatef(0, 30, 0);
        gl.glTranslatef(-(float)vector3d.getX(), -(float)vector3d.getY(), -(float)vector3d.getZ());

        models.get(2).draw(gl);
        models.get(3).draw(gl);
        models.get(4).draw(gl);
        models.get(5).draw(gl);
    }

    public void setMonsterBallOpen(float timer) {
        openAngle = timer * 2.0f;
        if (openAngle > 75) {
            openAngle = 75 - (openAngle - 75);
            if (openAngle < 0) {
                openAngle = 0;
            }
        }
    }

    @Override
    public void setPositon(Vector3d positon) {
        super.setPositon(positon);

        for (int i = 1; i < models.size(); i++) {
            models.get(i).setPositon(positon);
        }
    }

    @Override
    public void setAttitude(Matrix3d attitude) {
        super.setAttitude(attitude);

        for (int i = 1; i < models.size(); i++) {
            models.get(i).setAttitude(attitude);
        }
    }
}
