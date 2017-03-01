package com.vcredit.cameraHelper;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by qiubangbang on 2016/12/12.
 */

public class CamParaUtil {
    private static final String TAG = "yanzi";
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();
    private static CamParaUtil myCamPara = null;
    private CamParaUtil(){

    }
    public static CamParaUtil getInstance(){
        if(myCamPara == null){
            myCamPara = new CamParaUtil();
            return myCamPara;
        }
        else{
            return myCamPara;
        }
    }

    public Camera.Size getBestSupportedSize(List<Camera.Size> sizes) {
        // 取能适用的最大的SIZE
        Camera.Size largestSize = sizes.get(0);
        int largestArea = sizes.get(0).height * sizes.get(0).width;
        for (Camera.Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                largestArea = area;
                largestSize = s;
            }
        }
        return largestSize;
    }

    public  class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            // TODO Auto-generated method stub
            if(lhs.width == rhs.width){
                return 0;
            }
            else if(lhs.width > rhs.width){
                return 1;
            }
            else{
                return -1;
            }
        }

    }

    /**打印支持的previewSizes
     * @param params
     */
    public  void printSupportPreviewSize(Camera.Parameters params){
        List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
        for(int i=0; i< previewSizes.size(); i++){
            Camera.Size size = previewSizes.get(i);
            Log.i(TAG, "previewSizes:width = "+size.width+" height = "+size.height);
        }

    }

    /**打印支持的pictureSizes
     * @param params
     */
    public  void printSupportPictureSize(Camera.Parameters params){
        List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();
        for(int i=0; i< pictureSizes.size(); i++){
            Camera.Size size = pictureSizes.get(i);
            Log.i(TAG, "pictureSizes:width = "+ size.width
                    +" height = " + size.height);
        }
    }
    /**打印支持的聚焦模式
     * @param params
     */
    public void printSupportFocusMode(Camera.Parameters params){
        List<String> focusModes = params.getSupportedFocusModes();
        for(String mode : focusModes){
            Log.i(TAG, "focusModes--" + mode);
        }
    }

    // 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度
    public static int getPreviewDegree(Activity activity) {
        // 获得手机的方向
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degree = 0;
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }
}
