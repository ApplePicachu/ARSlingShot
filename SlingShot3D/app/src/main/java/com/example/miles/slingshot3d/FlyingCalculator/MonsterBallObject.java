package com.example.miles.slingshot3d.FlyingCalculator;

import android.content.Context;

import com.example.miles.slingshot3d.R;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Point3d;

/**
 * Created by miles on 2015/12/29.
 */
public class MonsterBallObject extends MovingObject {
    private final double ballDiameter;
    private Point3d ballCenter;

    private DrawableObject ballHShellRing;
    private DrawableObject ballHShellTop1;
    private DrawableObject ballHShellRing1;
    private DrawableObject ballHShellButtonBase;
    private DrawableObject ballHShellButton;

    private float openAngle = 0;

    public MonsterBallObject(Context context) {
        super(R.raw.pocketmonballshelltop, new float[]{1, 1, 1}, context);
        ballHShellRing = new DrawableObject(R.raw.pocketmonballshellring, new float[]{0, 0, 0}, context);
        ballHShellRing1 = new DrawableObject(R.raw.pocketmonballshellring, new float[]{0, 0, 0}, context);
        ballHShellTop1 = new DrawableObject(R.raw.pocketmonballshelltop, new float[]{1, 0, 0}, context);

        ballHShellButton = new DrawableObject(R.raw.pocketmonballbutton, new float[]{0.5f, 0.5f, 0.5f}, context);
        ballHShellButtonBase = new DrawableObject(R.raw.pocketmonballbuttonbase, new float[]{0.5f, 0.5f, 0.5f}, context);

        ballDiameter = 50;
    }

    @Override
    public boolean isLoaded() {
        if (super.isLoaded() && ballHShellButton.isLoaded() && ballHShellButtonBase.isLoaded()
                && ballHShellRing.isLoaded() && ballHShellRing1.isLoaded() && ballHShellTop1.isLoaded()) {
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
        ballHShellRing.draw(gl);

        gl.glRotatef(180, 0, 1, 0);
        gl.glTranslatef(0, -30, 0);
        gl.glRotatef(openAngle, -1, 0, 0);
        gl.glTranslatef(0, 30, 0);
        ballHShellButton.draw(gl);
        ballHShellButtonBase.draw(gl);
        ballHShellTop1.draw(gl);
        ballHShellRing1.draw(gl);
    }

    public void setMonsterBallOpen(float timer) {
        openAngle = timer*2.0f;
        if(openAngle>75){
            openAngle = 75-(openAngle-75);
            if(openAngle<0){
                openAngle = 0;
            }
        }
    }
}
