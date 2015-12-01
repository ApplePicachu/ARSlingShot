package com.example.miles.slingshot3d.STLModelReaderPack;

import android.opengl.Matrix;

/**
 * Created by miles on 2015/8/11.
 */
public class VectorCal {

    public static float getAngleDeg(float[] p1, float[] p2)
    {
        float ans = (float) (Math.acos( dot(p1, p2) / (magnitude(p1) * magnitude(p2)) )/3.1415926*180.0);
        if(Float.isNaN(ans)) {return 0.0f;}
        else {return ans;}
    }

    public static float[] getVecByPoint(float[] p1, float[] p2)
    {
        float[] result = new float[3];
        result[0] = p2[0] - p1[0];
        result[1] = p2[1] - p1[1];
        result[2] = p2[2] - p1[2];
        normalize(result);
        return result;
    }

    public static float[] getNormByPtArray(float[] ptArray)
    {
        float[] result = new float[ptArray.length];
        for(int i = 0; i < ptArray.length/9; i++)
        {
            float[] temp;
            float[] v1;
            float[] v2;

            int k =9*i;

            float[]p1 = new float[]{ptArray[k], ptArray[k+1] ,ptArray[k+2]};
            float[]p2 = new float[]{ptArray[k+3], ptArray[k+4] ,ptArray[k+5]};
            float[]p3 = new float[]{ptArray[k+6], ptArray[k+7] ,ptArray[k+8]};

            v1 = getVecByPoint(p1, p2);
            v2 = getVecByPoint(p2, p3);

            temp = cross(v1, v2);
            normalize(temp);

            result[k] = temp[0];
            result[k+1] = temp[1];
            result[k+2] = temp[2];

            result[k+3] = temp[0];
            result[k+4] = temp[1];
            result[k+5] = temp[2];

            result[k+6] = temp[0];
            result[k+7] = temp[1];
            result[k+8] = temp[2];
        }
        return result;
    }

    public static float dot(float[] v1, float[] v2) {
        float res = 0;
        for (int i=0;i<v1.length;i++)
            res += v1[i]*v2[i];
        return res;
    }

    public static float[] cross(float[] p1, float[] p2) {
        float[] result = new float[3];
        result[0] = p1[1]*p2[2]-p2[1]*p1[2];
        result[1] = p1[2]*p2[0]-p2[2]*p1[0];
        result[2] = p1[0]*p2[1]-p2[0]*p1[1];
        normalize(result);
        return result;
    }

    public static void normalize(float[] vector) {
        scalarMultiply(vector, 1 / magnitude(vector));
    }

    public static void scalarMultiply(float[] vector, float scalar) {
        for (int i=0;i<vector.length;i++)
            vector[i] *= scalar;
    }

    public static float magnitude(float[] vector) {
        return (float) Math.sqrt(vector[0] * vector[0] +
                vector[1] * vector[1] +
                vector[2] * vector[2]);
    }


    public static float[] rotAngMatrixMultiVec(float[] points, float rotAng, float[] rotVn){

        float[] transMatrix = new float[16];
        Matrix.setIdentityM(transMatrix, 0);

        float[] balls1 = new float[]{points[0], points[1],  points[2], 1};
        float[] balls2 = new float[]{points[3], points[4],  points[5], 1};
        float[] balls3 = new float[]{points[6], points[7],  points[8], 1};

        Matrix.rotateM(transMatrix, 0, rotAng, rotVn[0], rotVn[1], rotVn[2]);

//        Log.e("ARRegiatration TR", transMatrix[0] + " " + transMatrix[1] + " " + transMatrix[2] + " " + transMatrix[3]+"\n"
//                +transMatrix[4] + " " + transMatrix[5] + " " + transMatrix[6] + " " + transMatrix[7]+"\n"
//                +transMatrix[8] + " " + transMatrix[9] + " " + transMatrix[10] + " " + transMatrix[11]+"\n"
//                +transMatrix[12] + " " + transMatrix[13] + " " + transMatrix[14] + " " + transMatrix[15]+"\n");

        Matrix.multiplyMV(balls1, 0, transMatrix, 0, balls1, 0);
        Matrix.multiplyMV(balls2, 0, transMatrix, 0, balls2, 0);
        Matrix.multiplyMV(balls3, 0, transMatrix, 0, balls3, 0);

        return new float[]{balls1[0], balls1[1], balls1[2],
                balls2[0], balls2[1], balls2[2],
                balls3[0], balls3[1], balls3[2]};

    }

    public static float[] rotMatrixMultiVec(float[] points, float[] matrix){


        float[] balls1 = new float[]{points[0], points[1],  points[2], 1};
        float[] balls2 = new float[]{points[3], points[4],  points[5], 1};
        float[] balls3 = new float[]{points[6], points[7],  points[8], 1};


//        Log.e("ARRegiatration TR", transMatrix[0] + " " + transMatrix[1] + " " + transMatrix[2] + " " + transMatrix[3]+"\n"
//                +transMatrix[4] + " " + transMatrix[5] + " " + transMatrix[6] + " " + transMatrix[7]+"\n"
//                +transMatrix[8] + " " + transMatrix[9] + " " + transMatrix[10] + " " + transMatrix[11]+"\n"
//                +transMatrix[12] + " " + transMatrix[13] + " " + transMatrix[14] + " " + transMatrix[15]+"\n");

        Matrix.multiplyMV(balls1, 0, matrix, 0, balls1, 0);
        Matrix.multiplyMV(balls2, 0, matrix, 0, balls2, 0);
        Matrix.multiplyMV(balls3, 0, matrix, 0, balls3, 0);

        return new float[]{balls1[0], balls1[1], balls1[2],
                balls2[0], balls2[1], balls2[2],
                balls3[0], balls3[1], balls3[2]};

    }
}
