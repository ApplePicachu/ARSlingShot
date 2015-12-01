package com.example.miles.slingshot3d.TestModels;

import android.graphics.Color;
import android.opengl.GLES10;

import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class ColorLine {
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mColorBuffer;
	private float[] start;
	private float[] end;

	public ColorLine() {
		start = new float[3];
		end = new float[3];
		for (int i = 0; i < 3; i++) {
			start[i] = 0f;
			end[i] = 0f;
		}
		setColor(Color.WHITE);
		setArray();
	}

	public void setColor(int color) {
		setColor(color, color);
	}

	public void setColor(int colorStart, int colorEnd) {
		float[] colors = new float[8];
		int[] colorArray = {colorStart, colorEnd};
		for (int i = 0; i < 2; i++) {
			float r = ((colorArray[i] & 0x00FF0000) >> 16) / 255.0f;
			float g = ((colorArray[i] & 0x0000FF00) >> 8) / 255.0f;
			float b = ((colorArray[i] & 0x000000FF)) / 255.0f;
			float a = ((colorArray[i] & 0xFF000000) >> 24) / 255.0f;
			colors[i*4+0] = r;
			colors[i*4+1] = g;
			colors[i*4+2] = b;
			colors[i*4+3] = a;
		}
		mColorBuffer = RenderUtils.buildFloatBuffer(colors);
	}

	public void setLine(float[] start, float[] end) {
		if (start.length >= 3 && end.length >= 3) {
			for (int i = 0; i < 3; i++) {
				this.start[i] = start[i];
				this.end[i] = end[i];
			}
			setArray();
		}
	}

	private void setArray() {
		float vertics[] = new float[6];
		for (int i = 0; i < 3; i++) {
			vertics[i] = start[i];
			vertics[i + 3] = end[i];
		}
		mVertexBuffer = RenderUtils.buildFloatBuffer(vertics);
	}

	public void draw(GL10 unused) {
		GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, mColorBuffer);
		GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, mVertexBuffer);

		GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
		GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
		GLES10.glLineWidth(5f);
		GLES10.glDrawArrays(GLES10.GL_LINES, 0, 2);

		GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
		GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);

	}
}
