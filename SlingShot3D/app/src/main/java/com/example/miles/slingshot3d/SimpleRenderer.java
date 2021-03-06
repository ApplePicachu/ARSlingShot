/*
 *  SimpleRenderer.java
 *  ARToolKit5
 *
 *  Disclaimer: IMPORTANT:  This Daqri software is supplied to you by Daqri
 *  LLC ("Daqri") in consideration of your agreement to the following
 *  terms, and your use, installation, modification or redistribution of
 *  this Daqri software constitutes acceptance of these terms.  If you do
 *  not agree with these terms, please do not use, install, modify or
 *  redistribute this Daqri software.
 *
 *  In consideration of your agreement to abide by the following terms, and
 *  subject to these terms, Daqri grants you a personal, non-exclusive
 *  license, under Daqri's copyrights in this original Daqri software (the
 *  "Daqri Software"), to use, reproduce, modify and redistribute the Daqri
 *  Software, with or without modifications, in source and/or binary forms;
 *  provided that if you redistribute the Daqri Software in its entirety and
 *  without modifications, you must retain this notice and the following
 *  text and disclaimers in all such redistributions of the Daqri Software.
 *  Neither the name, trademarks, service marks or logos of Daqri LLC may
 *  be used to endorse or promote products derived from the Daqri Software
 *  without specific prior written permission from Daqri.  Except as
 *  expressly stated in this notice, no other rights or licenses, express or
 *  implied, are granted by Daqri herein, including but not limited to any
 *  patent rights that may be infringed by your derivative works or by other
 *  works in which the Daqri Software may be incorporated.
 *
 *  The Daqri Software is provided by Daqri on an "AS IS" basis.  DAQRI
 *  MAKES NO WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 *  THE IMPLIED WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS
 *  FOR A PARTICULAR PURPOSE, REGARDING THE DAQRI SOFTWARE OR ITS USE AND
 *  OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS.
 *
 *  IN NO EVENT SHALL DAQRI BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL
 *  OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION,
 *  MODIFICATION AND/OR DISTRIBUTION OF THE DAQRI SOFTWARE, HOWEVER CAUSED
 *  AND WHETHER UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE),
 *  STRICT LIABILITY OR OTHERWISE, EVEN IF DAQRI HAS BEEN ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 *
 *  Copyright 2015 Daqri, LLC.
 *  Copyright 2011-2015 ARToolworks, Inc.
 *
 *  Author(s): Julian Looser, Philip Lamb
 *
 */

package com.example.miles.slingshot3d;

import android.content.Context;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.rendering.ARRenderer;
import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * A very simple Renderer that adds a marker and draws a cube on it.
 */
public class SimpleRenderer extends ARRenderer {
    private int markerIDs[] = {-1, -1};
    private HandleScene handleScene;
    private float[] ambientLight = new float[]{0.25f, 0.25f, 0.25f, 1.0f};
    private float[] diffuseLight = new float[]{0.85f, 0.85f, 0.85f, 1.0f};
    private float[] lightPos = new float[]{0.0f, 200.0f, 200.0f, 1.0f};

    private FloatBuffer ambientBuffer;
    private FloatBuffer diffuseBuffer;
    private FloatBuffer lightPosBuffer;
    private boolean[] twoMarker = new boolean[]{false, false};

    public SimpleRenderer(Context ctx) {
        ambientBuffer = RenderUtils.buildFloatBuffer(ambientLight);
        diffuseBuffer = RenderUtils.buildFloatBuffer(diffuseLight);
        lightPosBuffer = RenderUtils.buildFloatBuffer(lightPos);
        handleScene = new HandleScene(ctx);
    }

    /**
     * Markers can be configured here.
     */
    @Override
    public boolean configureARScene() {
        markerIDs[0] = ARToolKit.getInstance().addMarker("multi;Data/multiMarkerField.pat");
        markerIDs[1] = ARToolKit.getInstance().addMarker("multi;Data/multiMarkerSlingshot.pat");
        for (int i = 0; i < markerIDs.length; i++) {
            if (markerIDs[i] < 0) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        gl.glLightfv(gl.GL_LIGHT0, gl.GL_AMBIENT, ambientBuffer);
        gl.glLightfv(gl.GL_LIGHT0, gl.GL_DIFFUSE, diffuseBuffer);
        gl.glLightfv(gl.GL_LIGHT0, gl.GL_POSITION, lightPosBuffer);
        gl.glEnable(gl.GL_LIGHT0);
        gl.glEnable(gl.GL_COLOR_MATERIAL);
        gl.glEnable(gl.GL_LIGHTING);
    }


    /**
     * Override the draw function from ARRenderer.
     */
    @Override
    public void draw(GL10 gl) {

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // Apply the ARToolKit projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadMatrixf(ARToolKit.getInstance().getProjectionMatrix(), 0);

//        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glFrontFace(GL10.GL_CCW);

        // If the marker is visible, apply its transformation, and draw a cube
        if (ARToolKit.getInstance().queryMarkerVisible(markerIDs[0])) {
            handleScene.setBaseM(ARToolKit.getInstance().queryMarkerTransformation(markerIDs[0]));
            twoMarker[0] = true;
        }
        if (ARToolKit.getInstance().queryMarkerVisible(markerIDs[1])) {
            handleScene.setProjectileM(ARToolKit.getInstance().queryMarkerTransformation(markerIDs[1]));
            twoMarker[1] = true;
        }
        if (twoMarker[0] && twoMarker[1]) {
            handleScene.draw(gl);
        }
    }


}