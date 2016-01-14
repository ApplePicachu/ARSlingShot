package com.example.miles.slingshot3d;

import android.content.Context;
import android.graphics.Color;
import android.opengl.Matrix;
import android.text.format.Time;
import android.util.Log;

import com.example.miles.slingshot3d.FlyingCalculator.DrawableObject;
import com.example.miles.slingshot3d.FlyingCalculator.FlyingCalculator;
import com.example.miles.slingshot3d.FlyingCalculator.MonsterBallObject;
import com.example.miles.slingshot3d.FlyingCalculator.ObstacleObject;
import com.example.miles.slingshot3d.TestModels.ColorCube;
import com.example.miles.slingshot3d.TestModels.ColorLine;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

public class HandleScene {
    private static float SLING_SHOT_HEIGHT = 90.0f;
    private float[] projectileM;
    private float[] baseM;
    private boolean isArmed = false;
    private boolean isReady = true;
    private static float ARM_THRESHOLD = 75f;
    private Time time;
    private long lastDrawTime;
    private long startFlyingTime;
    private long captureTimer;
    private ColorCube cube[];
    private ColorLine line;
    private int noBaseMCounter = 0;
    private boolean isNoBaseM;
    private int noProjectileMCounter = 0;
    private boolean isNoProjectileM;
    private Vector3f shootingDirection;


    //****ball****//
    private MonsterBallObject monsterball;
    private float openTimer = 0;

    //****pika****//
    private DrawableObject pikaBody;
    private DrawableObject pikaNose;
    private DrawableObject pikaEarL;
    private DrawableObject pikaEarR;
    private DrawableObject pikaTail;
    private Point3f picaPosition;

    //****wall****//
    private ObstacleObject brickWall1;
    private ObstacleObject brickWall2;

    //****ground****//
    private ObstacleObject ground;

    //****tree****//
    private DrawableObject treeBase;
    private DrawableObject treeLeaf;

    private Context context;

    private boolean MODEL_LOADED = false;

    private FlyingCalculator fc;

    private float[] launchLocMatrix = new float[16];
    private boolean isCaptured = false;
    private int gameState = 0;
    private boolean STATE_CHANGING = false;

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
        picaPosition = new Point3f(0, 380, -80);
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
        if (MODEL_LOADED) {
            //States
            time.setToNow();
            if ((time.toMillis(false) - startFlyingTime > 3000) | fc.isTouchGround()) {
                isReady = true;
                launchLocMatrix = new float[16];
            }
            if (time.toMillis(false) - captureTimer > 3000 && isCaptured) {
                //switching game state
                gameState = (gameState + 1) % 4;
                STATE_CHANGING = true;
                isCaptured = false;
                openTimer = 0;
                monsterball.setMonsterBallOpen(0);
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
                        fc.setIsTouchGround(false);
                        Log.e("object velocity", shootingDirection.x + " " + shootingDirection.y + " " + shootingDirection.z + " ");
                        monsterball.setPositon(new Vector3d());
                        monsterball.setVelocity(new Vector3d(shootingDirection.x, shootingDirection.y, shootingDirection.z));
                        launchLocMatrix = baseM;
                    }
                }
            }
            isNoBaseM = true;
            isNoProjectileM = true;

            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadMatrixf(baseM, 0);
            ground.draw(gl);
            brickWall1.draw(gl);
            brickWall2.draw(gl);
            drawTree(gl);
            if (isReady) {
                //not shooting yet
                monsterball.setPositon(new Vector3d());
                gl.glLoadMatrixf(projectileM, 0);
                monsterball.draw(gl);
            } else if (isCaptured) {
                monsterball.setPositon(new Vector3d(50, 380, -90 + 50));
                openTimer++;
                monsterball.setMonsterBallOpen(openTimer);

                gl.glLoadMatrixf(baseM, 0);
                monsterball.draw(gl);

                gl.glLoadMatrixf(baseM, 0);
                gl.glTranslatef(+35.0f / (90.0f - openTimer + 0.1f), -30 / (90.0f - openTimer + 0.1f), +35 / (90.0f - openTimer + 0.1f));
                drawPIKA(gl, 8 / openTimer * 4);
            } else {
                //flying
                fc.nextFrame();
                float captureDistance = new Point3f(monsterball.getPositon()).distance(new Point3f(picaPosition.x, picaPosition.y, picaPosition.z + 40));
//                Log.d("flying", "captureDistance=" + captureDistance);
                if (captureDistance < 30) {
                    time.setToNow();
                    captureTimer = time.toMillis(false);
                    isCaptured = true;
                    monsterball.setPositon(new Vector3d(35, 350, -90 + 35));
                }
                gl.glLoadMatrixf(baseM, 0);
                monsterball.draw(gl);
                Log.e("Ready", "shoot");
            }

            gl.glLoadMatrixf(baseM, 0);
            line.draw(gl);

            if (!isCaptured) {
                gl.glLoadMatrixf(baseM, 0);
                drawPIKA(gl, 8);
            }

            //game state switching
            if (STATE_CHANGING) {
                switch (gameState) {
                    case 0: {
                        brickWall1.setPositon(new Vector3d(0, 200, -SLING_SHOT_HEIGHT + 10000));
                        brickWall2.setPositon(new Vector3d(0, 200, -SLING_SHOT_HEIGHT + 10000));
                    }
                    break;
                    case 1: {
                        brickWall1.setPositon(new Vector3d(0, 300, -SLING_SHOT_HEIGHT + 70));
                        brickWall2.setPositon(new Vector3d(0, 400, -SLING_SHOT_HEIGHT + 150));
                        Matrix3d matrix3d = new Matrix3d();
                        matrix3d.setIdentity();
                        matrix3d.rotX(3*Math.PI / 4);
                        matrix3d.mul(2.0);
                        brickWall2.setAttitude(matrix3d);
                    }
                    break;
                    case 2: {
                        brickWall1.setPositon(new Vector3d(-21, 300, -SLING_SHOT_HEIGHT + 70));
                        brickWall2.setPositon(new Vector3d(21, 300, -SLING_SHOT_HEIGHT + 70));
                        Matrix3d matrix3d = new Matrix3d();
                        matrix3d.setIdentity();
                        matrix3d.rotX(Math.PI / 4);
                        brickWall2.setAttitude(matrix3d);
                    }
                    break;
                    case 3: {
                        Log.e("flying", "Win");
                    }
                    break;
                }
                STATE_CHANGING = false;
            }
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
        float h = 120.0f - (shootingDirection.length() - ARM_THRESHOLD) * 100.0f / 40.0f;
        float[] hsv = {h, 1.0f, 1.0f};
        line.setLine(pointBase, pointProjectile);
        line.setColor(Color.GREEN, Color.HSVToColor(255, hsv));
    }

    private void drawWall(GL10 gl, ObstacleObject wall, float scale, float angle) {
        gl.glLoadMatrixf(baseM, 0);
        gl.glTranslatef(wall.getPositonf()[0], wall.getPositonf()[1], wall.getPositonf()[2]);
        gl.glScalef(scale, scale, scale);
        gl.glRotatef(90 + angle, 1, 0, 0);
        wall.draw(gl);
    }

    private void drawPIKA(GL10 gl, float scale) {
        gl.glTranslatef(picaPosition.x, picaPosition.y, picaPosition.z);
        gl.glScalef(scale, scale, scale);
        pikaBody.draw(gl);
        pikaNose.draw(gl);
        pikaEarL.draw(gl);
        pikaEarR.draw(gl);
        pikaTail.draw(gl);
    }

    private void drawTree(GL10 gl) {
        gl.glTranslatef(80.0f, 80.0f, 0);
        gl.glRotatef(90, 1, 0, 0);
        gl.glScalef(0.05f, 0.05f, 0.05f);
        treeBase.draw(gl);
        treeLeaf.draw(gl);
    }

    Thread loadSTL = new Thread(new Runnable() {
        @Override
        public void run() {
            Log.e("Model", "MODEL_LOADED: " + MODEL_LOADED);
            //****ball****//
            monsterball = new MonsterBallObject(context);

            //****pika****//
            pikaBody = new DrawableObject(R.raw.pikachufixed, new float[]{1, 1, 0}, context);
            pikaNose = new DrawableObject(R.raw.pikachufixednose, new float[]{0, 0, 0}, context);
            pikaEarL = new DrawableObject(R.raw.pikachufixedearl, new float[]{0, 0, 0}, context);
            pikaEarR = new DrawableObject(R.raw.pikachufixedearr, new float[]{0, 0, 0}, context);
            pikaTail = new DrawableObject(R.raw.pikachufixedtail, new float[]{184f / 255f, 134f / 255f, 11f / 255f}, context);

            //****wall****//
            brickWall1 = ObstacleObject.create(ObstacleObject.OBSTACLE_TYPE_WALL, new Vector3d(0, 200, -SLING_SHOT_HEIGHT + 10000), context);
            Matrix3d matrix3d = new Matrix3d();
            matrix3d.setIdentity();
            matrix3d.rotX(Math.PI / 2);
            brickWall1.setAttitude(matrix3d);

            brickWall2 = ObstacleObject.create(ObstacleObject.OBSTACLE_TYPE_WALL, new Vector3d(0, 200, -SLING_SHOT_HEIGHT + 10000), context);
            matrix3d = new Matrix3d();
            matrix3d.setIdentity();
            matrix3d.rotX(Math.PI / 2);
            brickWall2.setAttitude(matrix3d);

            //****ground****//
            ground = ObstacleObject.create(ObstacleObject.OBSTACLE_TYPE_GROUND, new Vector3d(0, 200, -SLING_SHOT_HEIGHT - 10), context);
            matrix3d = new Matrix3d();
            matrix3d.setIdentity();
            matrix3d.mul(10.0);
            ground.setAttitude(matrix3d);

            //****tree****//
            treeBase = new DrawableObject(R.raw.tbase, new float[]{184f / 255f * 0.05f, 134f / 255f * 0.05f, 11f / 255f * 0.05f}, context);
            treeLeaf = new DrawableObject(R.raw.tleaf, new float[]{0, 0.05f, 0}, context);

            fc = new FlyingCalculator(150);
            fc.setMonsterBall(monsterball);
            fc.addObstacle(brickWall1);
            fc.addObstacle(brickWall2);
            fc.addObstacle(ground);

            if (monsterball.isLoaded()
                    && pikaTail.isLoaded() && pikaEarR.isLoaded() && pikaEarL.isLoaded() && pikaBody.isLoaded()
                    && brickWall1.isLoaded()
                    && treeBase.isLoaded() && treeLeaf.isLoaded()) {
                MODEL_LOADED = true;
            }
        }
    });
}
