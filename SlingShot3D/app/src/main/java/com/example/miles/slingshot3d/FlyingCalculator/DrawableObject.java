package com.example.miles.slingshot3d.FlyingCalculator;

import android.content.Context;
import android.util.Log;

import com.example.miles.slingshot3d.STLModelReaderPack.FileReader;
import com.example.miles.slingshot3d.STLModelReaderPack.VectorCal;

import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

public class DrawableObject {
    protected double mass;
    protected Vector3d positon;
    protected Matrix3d attitude;
    protected Matrix4d transMatrix;

    public float[] vertex;
    public float[] normals;
    public float[] color;

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer normalBuffer;


    protected boolean isDraw = true;

    private boolean Loaded = false;

    //TODO bounding box

    public DrawableObject() {
        initDO();
    }

    public DrawableObject(float[] Vert, float[] inColor) {
        initDO();
        if (Vert[0] != Float.NaN) {
            vertex = Vert;
            initBuffer(vertex, inColor);
        } else {
            Log.v("loaded E*: ", "UnLoaded");
        }
    }

    public DrawableObject(int fileID, float[] inColor, Context ctx) {
        initDO();
        vertex = FileReader.ReadStlBinary(fileID, ctx);
        if (vertex[0] != -1) {
            initBuffer(vertex, inColor);
        } else {
            Log.v("loaded E*: ", "UnLoaded");
        }
    }


    private void initDO() {
        positon = new Vector3d();
        attitude = new Matrix3d();
        attitude.setIdentity();
        transMatrix = new Matrix4d();
        transMatrix.setIdentity();
        mass = 1;
    }

    private void initBuffer(float[] Vert, float[] inColor) {
        normals = VectorCal.getNormByPtArray(Vert);
        color = new float[Vert.length / 3 * 4];
        for (int i = 0; i < color.length; i = i + 4) {
            color[i + 0] = inColor[0];
            color[i + 1] = inColor[1];
            color[i + 2] = inColor[2];
            color[i + 3] = 1.0f;
        }
        System.gc();
        Log.v("loaded: ", "Loaded");
        Loaded = true;

        vertexBuffer = RenderUtils.buildFloatBuffer(vertex);
        normalBuffer = RenderUtils.buildFloatBuffer(normals);
        colorBuffer = RenderUtils.buildFloatBuffer(color);
    }

    public boolean isLoaded() {
        return Loaded;
    }

    public Matrix4d getTransMatrix() {
        transMatrix.setIdentity();
        transMatrix.setRotation(attitude);
        transMatrix.setTranslation(positon);
        return transMatrix;
    }

    public float[] getTransMatrixf() {
        transMatrix.setIdentity();
        transMatrix.setRotation(attitude);
        transMatrix.setTranslation(positon);

        return new float[]{
                (float) transMatrix.m00, (float) transMatrix.m10, (float) transMatrix.m20, (float) transMatrix.m30,
                (float) transMatrix.m01, (float) transMatrix.m11, (float) transMatrix.m21, (float) transMatrix.m31,
                (float) transMatrix.m02, (float) transMatrix.m12, (float) transMatrix.m22, (float) transMatrix.m32,
                (float) transMatrix.m03, (float) transMatrix.m13, (float) transMatrix.m23, (float) transMatrix.m33,

        };
    }

    public String getPosString() {
        return positon.x + "\t" + positon.y + "\t" + positon.z;
    }

    public Vector3d getPositon() {
        return positon;
    }

    public void setPositon(Vector3d positon) {
        this.positon = positon;
    }

    public void setAttitude(Matrix3d attitude) {
        this.attitude = attitude;
    }

    public void draw(GL10 gl) {

        gl.glColorPointer(4, gl.GL_FLOAT, 0, colorBuffer);
        gl.glVertexPointer(3, gl.GL_FLOAT, 0, vertexBuffer);
        gl.glNormalPointer(gl.GL_FLOAT, 0, normalBuffer);

        gl.glEnableClientState(gl.GL_COLOR_ARRAY);
        gl.glEnableClientState(gl.GL_VERTEX_ARRAY);
        gl.glEnableClientState(gl.GL_NORMAL_ARRAY);

        gl.glDrawArrays(gl.GL_TRIANGLES, 0, vertex.length / 3);

        gl.glDisableClientState(gl.GL_COLOR_ARRAY);
        gl.glDisableClientState(gl.GL_VERTEX_ARRAY);
        gl.glEnableClientState(gl.GL_NORMAL_ARRAY);

    }

}

