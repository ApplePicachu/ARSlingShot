package com.example.miles.slingshot3d.STLModelReaderPack;

import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by miles on 2015/9/23.
 */
public class FileReader {

    public static float[] ReadStlBinary(int fileID, Context ctx) {
        float[] ospVert = new float[0];
        InputStream inputStream = null;

        try {
            inputStream = ctx.getResources().openRawResource(fileID);

            int count;
            byte[] buffer = new byte[84];
            inputStream.read(buffer);
            count = ByteBuffer.wrap(buffer, 80, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            ospVert = new float[count * 9];
            buffer = new byte[50 * count];
            inputStream.read(buffer);
            int num1 = 0;
            int num2 = 0;

            for (int Line = 0; Line < count; Line++) {
                ByteBuffer temp = ByteBuffer.wrap(buffer, num2 + 12, 36).order(ByteOrder.LITTLE_ENDIAN);
                for (int jjj = 0; jjj < 9; jjj++) {
                    ospVert[num1 + jjj] = temp.getFloat();
                }
                num1 += 9;
                num2 += 50;
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("Model", "Error: " + e);
            return new float[]{-1.0f};
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Model", "Error: " + e);
            return new float[]{-1.0f};
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Model", "Error: " + e);
                    return new float[]{-1.0f};
                }
        }

        return ospVert;
    }
}