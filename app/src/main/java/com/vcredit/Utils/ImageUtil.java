package com.vcredit.Utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by qiubangbang on 2016/12/12.
 */

public class ImageUtil {
    /**
     * 旋转Bitmap
     * @param b
     * @param rotateDegree
     * @return
     */
    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree){
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateDegree);
        Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
        return rotaBitmap;
    }
}
