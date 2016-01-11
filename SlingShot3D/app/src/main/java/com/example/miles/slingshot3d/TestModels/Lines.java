package com.example.miles.slingshot3d.TestModels;

import android.graphics.Color;
import android.opengl.GLES10;

import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by miles on 2016/1/11.
 */
public class Lines {
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;
    private static final int MAX_POINTS = 100;
    private int pointsCount;
    private float[] lines;

    public Lines() {
        setDashedColor(Color.BLACK, Color.YELLOW);
    }


    public void setDashedColor(int color1, int color2) {
        float[] colors = new float[MAX_POINTS*4];
        int[] colorArray = {color1, color2};
        for (int i = 0; i < MAX_POINTS; i++) {
            float r = ((colorArray[i%2] & 0x00FF0000) >> 16) / 255.0f;
            float g = ((colorArray[i%2] & 0x0000FF00) >> 8) / 255.0f;
            float b = ((colorArray[i%2] & 0x000000FF)) / 255.0f;
            float a = ((colorArray[i%2] & 0xFF000000) >> 24) / 255.0f;
            colors[i*4+0] = r;
            colors[i*4+1] = g;
            colors[i*4+2] = b;
            colors[i*4+3] = a;
        }
        mColorBuffer = RenderUtils.buildFloatBuffer(colors);
    }

    public void setLines(float[] lines) {
        pointsCount = lines.length/4;
        if (pointsCount > MAX_POINTS)
            pointsCount = MAX_POINTS;
        this.lines = lines;
        setArray();
    }

    private void setArray() {
        mVertexBuffer = RenderUtils.buildFloatBuffer(lines);
    }

    public void draw(GL10 unused) {
        GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, mColorBuffer);
        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, mVertexBuffer);

        GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
        GLES10.glLineWidth(5f);
        GLES10.glDrawArrays(GLES10.GL_LINES, 0, pointsCount);

        GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);

    }
}
