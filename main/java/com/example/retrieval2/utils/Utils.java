package com.example.retrieval2.utils;

import android.content.Context;
import android.graphics.Bitmap;

import org.pytorch.Tensor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.FloatBuffer;

import static org.pytorch.torchvision.TensorImageUtils.bitmapToFloatBuffer;

public class Utils {
    public static Tensor bitmapToFloat32Tensor(
            final Bitmap bitmap, final float[] normMeanRGB, final float normStdRGB[]) {
        return bitmapToFloat32Tensor(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), normMeanRGB, normStdRGB);
    }
    public static Tensor bitmapToFloat32Tensor(
            final Bitmap bitmap,
            int x,
            int y,
            int width,
            int height,
            float[] normMeanRGB,
            float[] normStdRGB) {

        final FloatBuffer floatBuffer = Tensor.allocateFloatBuffer(3 * width * height);
        bitmapToFloatBuffer(bitmap, x, y, width, height, normMeanRGB, normStdRGB, floatBuffer, 0);
        return Tensor.fromBlob(floatBuffer, new long[]{1, 3, height, width});
    }
    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }
}
