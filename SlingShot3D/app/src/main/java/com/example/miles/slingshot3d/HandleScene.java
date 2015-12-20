package com.example.miles.slingshot3d;

import android.content.Context;
import android.graphics.Color;
import android.opengl.Matrix;
import android.text.format.Time;
import android.util.Log;

import com.example.miles.slingshot3d.FlyingCalculator.DrawableObject;
import com.example.miles.slingshot3d.FlyingCalculator.FlyingCalculator;
import com.example.miles.slingshot3d.FlyingCalculator.MovingObject;
import com.example.miles.slingshot3d.TestModels.ColorCube;
import com.example.miles.slingshot3d.TestModels.ColorLine;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

public class HandleScene {
    private float[] projectileM;
    private float[] baseM;
    private boolean isArmed = false;
    private boolean isReady = true;
    private static float ARM_THRESHOLD = 75f;
    private Time time;
    private long lastDrawTime;
    private long startFlyingTime;
    private ColorCube cube[];
    private ColorLine line;
    private int noBaseMCounter = 0;
    private boolean isNoBaseM;
    private int noProjectileMCounter = 0;
    private boolean isNoProjectileM;
    private Vector3f shootingDirection;

    //****ball****//
    private MovingObject ballHShellTop;
    private MovingObject ballHShellRing;
    private MovingObject ballHShellTop1;
    private MovingObject ballHShellRing1;
    private MovingObject ballHShellButtonBase;
    private MovingObject ballHShellButton;

    //****pika****//
    private DrawableObject pikaBody;
    private DrawableObject pikaEarL;
    private DrawableObject pikaEarR;
    private DrawableObject pikaTail;

    private Context context;

    private boolean MODEL_LOADED = false;

    private MovingObject mo;
    private FlyingCalculator fc;
    private float[] launchLocMatrix = new float[16];

    public HandleScene(Context ctx) {
        baseM = new float[16];
        projectileM = new float[16];

        cube = new ColorCube[2];
        cube[0] = new ColorCube(5f, 0f, 0f, 0f, Color.GREEN);
        cube[1] = new ColorCube(45f, 0f, 0f, 0f, Color.RED);
        line = new ColorLine();

        time = new Time();
        context = ctx;
        loadSTL.start();

        shootingDirection = new Vector3f();
    }

    public void setBaseM(float[] baseM) {
        noBaseMCounter = 0;
        isNoBaseM = false;
        for (int i = 0; i < 16; i++) {
            this.baseM[i] = baseM[i];
        }
    }

    public void setProjectileM(float[] projectileM) {
        if (!isReady) {
            return;
        }
        isNoProjectileM = false;
        noProjectileMCounter = 0;
        for (int i = 0; i < 16; i++) {
            this.projectileM[i] = projectileM[i];
        }

        if (shootingDirection.length() > ARM_THRESHOLD) {
            isArmed = true;
        } else {
            isArmed = false;
        }
    }

    public final static float calc3DLength(float[] p1, float[] p2) {
        float sqsum = 0;
        for (int i = 0; i < 3; i++) {
            sqsum += Math.pow((p1[i] - p2[i]), 2);
        }
        return (float) Math.sqrt(sqsum);
    }

    public void draw(GL10 gl) {
        time.setToNow();
        if (time.toMillis(false) - startFlyingTime > 3000) {
            isReady = true;
            launchLocMatrix = new float[16];
        }
        calcShootingVector();
        if (isNoBaseM) {
            noBaseMCounter++;
        }
        if (isNoProjectileM) {
            noProjectileMCounter++;
            if (noProjectileMCounter > 3) {
                if (isArmed) {
                    // shooting process
                    time.setToNow();
                    startFlyingTime = time.toMillis(false);
                    isReady = false;
                    isArmed = false;
                    Log.e("object velocity", shootingDirection.x + " " + shootingDirection.y + " " + shootingDirection.z + " ");
                    mo = new MovingObject(new Vector3d(), new Vector3d(shootingDirection.x, shootingDirection.y, shootingDirection.z));
                    fc =  new FlyingCalculator(mo, 200);
                    launchLocMatrix = baseM;
                }
            }
        }
        isNoBaseM = true;
        isNoProjectileM = true;

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        if (!isReady) {
            fc.nextFrame();
            gl.glLoadMatrixf(baseM, 0);
            gl.glMultMatrixf(mo.getTransMatrixf(), 0);
            drawPOMBall(gl);
            Log.e("Ready", "shoot");
        }
        gl.glLoadMatrixf(baseM, 0);
        line.draw(gl);
        if (MODEL_LOADED) {
            drawPIKA(gl);
        }

        if (MODEL_LOADED && isReady) {
            gl.glLoadMatrixf(projectileM, 0);
            drawPOMBall(gl);
        }
    }

    private void calcShootingVector() {
        float[] pointBase = new float[4];
        pointBase[0] = baseM[12];
        pointBase[1] = baseM[13];
        pointBase[2] = baseM[14];
        pointBase[3] = 1;
        float[] pointProjectile = new float[4];
        pointProjectile[0] = projectileM[12];
        pointProjectile[1] = projectileM[13];
        pointProjectile[2] = projectileM[14];
        pointProjectile[3] = 1;

        float[] invM = new float[16];
        Matrix.invertM(invM, 0, baseM, 0);
        Matrix.multiplyMV(pointBase, 0, invM, 0, pointBase, 0);
        Matrix.multiplyMV(pointProjectile, 0, invM, 0, pointProjectile, 0);
        shootingDirection.x = -pointProjectile[0];
        shootingDirection.y = -pointProjectile[1];
        shootingDirection.z = -pointProjectile[2];
        float h = 120.0f - (shootingDirection.length() - ARM_THRESHOLD) * 120.0f / 40.0f;
        float[] hsv = {h, 1.0f, 1.0f};
        line.setLine(pointBase, pointProjectile);
        line.setColor(Color.GREEN, Color.HSVToColor(255, hsv));

    }

    private void drawPOMBall(GL10 gl){
        ballHShellTop.draw(gl);
        ballHShellRing.draw(gl);
        ballHShellButton.draw(gl);
        ballHShellButtonBase.draw(gl);

        gl.glRotatef(180, 0, 1, 0);
        ballHShellTop1.draw(gl);
        ballHShellRing1.draw(gl);
    }

    private void drawPIKA(GL10 gl) {
        gl.glTranslatef(0, -80.0f, -80.0f);
        gl.glScalef(8.0f, 8.0f, 8.0f);
        pikaBody.draw(gl);
        pikaEarL.draw(gl);
        pikaEarR.draw(gl);
        pikaTail.draw(gl);
    }

    Thread loadSTL = new Thread(new Runnable() {
        @Override
        public void run() {
            Log.e("Model", "MODEL_LOADED: " + MODEL_LOADED);
            //****ball****//
            ballHShellTop = new MovingObject(R.raw.pocketmonballshelltop, new float[]{1, 1, 1}, context);
            ballHShellRing = new MovingObject(R.raw.pocketmonballshellring, new float[]{0, 0, 0}, context);
            ballHShellRing1 = new MovingObject(R.raw.pocketmonballshellring, new float[]{0, 0, 0}, context);
            ballHShellTop1 = new MovingObject(R.raw.pocketmonballshelltop, new float[]{1, 0, 0}, context);

            ballHShellButton = new MovingObject(R.raw.pocketmonballbutton, new float[]{0.5f, 0.5f, 0.5f}, context);
            ballHShellButtonBase = new MovingObject(R.raw.pocketmonballbuttonbase, new float[]{0.5f, 0.5f, 0.5f}, context);

            //****pika****//
            pikaBody = new DrawableObject(R.raw.pikachufixed, new float[]{1, 1, 0}, context);
            pikaEarL = new DrawableObject(R.raw.pikachufixedearl, new float[]{0, 0, 0}, context);
            pikaEarR = new DrawableObject(R.raw.pikachufixedearr, new float[]{0, 0, 0}, context);
            pikaTail = new DrawableObject(R.raw.pikachufixedtail, new float[]{184f / 255f, 134f / 255f, 11f / 255f}, context);


            if (ballHShellButton.isLoaded() && ballHShellButtonBase.isLoaded() && ballHShellRing.isLoaded()
                    && ballHShellRing1.isLoaded() && ballHShellTop.isLoaded() && ballHShellTop1.isLoaded()
                    && pikaTail.isLoaded() && pikaEarR.isLoaded() && pikaEarL.isLoaded() && pikaBody.isLoaded()) {
                MODEL_LOADED = true;
            }
        }
    });
}
