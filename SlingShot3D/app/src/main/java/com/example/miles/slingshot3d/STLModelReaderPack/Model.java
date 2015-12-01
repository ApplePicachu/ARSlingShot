package com.example.miles.slingshot3d.STLModelReaderPack;

import android.util.Log;

import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by miles on 2015/11/17.
 */
public class Model {
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer normalBuffer;

    public float[] vertex;
    public float[] normals;
    public float color[];

    private boolean Loaded = false;

    public Model(float[] Vert,  float[] inColor){
        vertex = Vert;

        if(vertex[0] != Float.NaN) {
            normals = VectorCal.getNormByPtArray(vertex);
            color = new float[vertex.length/3*4];
            for(int i = 0; i < color.length ; i = i+4)
            {
                color[i+0] =inColor[0];
                color[i+1] =inColor[1];
                color[i+2] =inColor[2];
                color[i+3] =1.0f;
            }
            System.gc();
            Log.v("loaded: ", "Loaded");
            Loaded = true;

            vertexBuffer = RenderUtils.buildFloatBuffer(vertex);
            normalBuffer = RenderUtils.buildFloatBuffer(normals);
            colorBuffer = RenderUtils.buildFloatBuffer(color);

        }else {
            Log.v("loaded E*: ", "UnLoaded");
        }

    }

    public Model(String filename, float[] inColor) {
        vertex = FileReader.ReadStlBinary(filename);
        if(vertex[0] != -1) {
            normals = VectorCal.getNormByPtArray(vertex);
            color = new float[vertex.length/3*4];
            for(int i = 0; i < color.length ; i = i+4)
            {
                color[i+0] =inColor[0];
                color[i+1] =inColor[1];
                color[i+2] =inColor[2];
                color[i+3] =1.0f;
            }
            System.gc();
            Log.v(filename + " loaded: ", "Loaded");
            Loaded = true;

            vertexBuffer = RenderUtils.buildFloatBuffer(vertex);
            normalBuffer = RenderUtils.buildFloatBuffer(normals);
            colorBuffer = RenderUtils.buildFloatBuffer(color);

        }else {
            Log.v(filename + "loaded E*: ", "UnLoaded");
        }
    }

    public boolean isLoaded(){
        return Loaded;
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
