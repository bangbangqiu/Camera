package com.vcredit.cameraHelper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import com.vcredit.Utils.FileUtil;
import com.vcredit.Utils.ImageUtil;

import java.io.IOException;

/**
 * 用于拍照更能的封装类
 * Created by qiubangbang on 2016/12/12.
 */

public class CameraInterface {

    private static final String TAG = "yanzi";
    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;
    private static CameraInterface mCameraInterface;
    //默认为后摄像头
    private int cameraDirect = Camera.CameraInfo.CAMERA_FACING_BACK;

    private CameraInterface() {
    }

    public static synchronized CameraInterface getInstance() {
        if (mCameraInterface == null) {
            mCameraInterface = new CameraInterface();
        }
        return mCameraInterface;
    }

    public CameraInterface doOpenCamera() {
        mCamera = Camera.open();
        return this;
    }

    public void doStartPreview(SurfaceHolder holder) {
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {

            mParams = mCamera.getParameters();
            mParams.setPictureFormat(ImageFormat.JPEG);//设置拍照后存储的图片格式

//            设置PreviewSize（预览尺寸）和PictureSize(照片分辨率)
            Camera.Size pictureSize = CamParaUtil.getInstance().getBestSupportedSize(mParams.getSupportedPictureSizes());
            mParams.setPictureSize(pictureSize.width, pictureSize.height);
            Camera.Size previewSize = CamParaUtil.getInstance().getBestSupportedSize(mParams.getSupportedPreviewSizes());
            mParams.setPreviewSize(previewSize.width, previewSize.height);

//            CamParaUtil.getInstance().printSupportFocusMode(mParams);
            //设置连续对焦,并且前摄像头设置这个参数 部分机型报错（不清楚原因）
            if (cameraDirect == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            mCamera.setDisplayOrientation(90);
            mCamera.setParameters(mParams);


            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();//开启预览
                mCamera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。
            } catch (IOException e) {
                e.printStackTrace();
            }

            isPreviewing = true;

            mParams = mCamera.getParameters(); //重新get一次
            Log.i(TAG, "最终设置:PreviewSize--With = " + mParams.getPreviewSize().width
                    + "Height = " + mParams.getPreviewSize().height);
            Log.i(TAG, "最终设置:PictureSize--With = " + mParams.getPictureSize().width
                    + "Height = " + mParams.getPictureSize().height);
        }
    }

    public void doShiftCamera(SurfaceHolder holder) {
        //查看Camera.open()的源码
        if (null != mCamera) {
            int numberOfCameras = Camera.getNumberOfCameras();
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing != cameraDirect) {
                    //记得释放camera，方便其他应用调用
                    doStopCamera();
                    // 打开当前选中的摄像头
                    mCamera = Camera.open(i);
                    cameraDirect = cameraInfo.facing;
                    doStartPreview(holder);
                    break;
                }
            }
        }
    }

    public void doStopCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mCamera.release();
            mCamera = null;
        }
    }

    public void doTakePicture() {
        if (isPreviewing && (mCamera != null)) {
//            mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
            //第一个回调传入空，就不会有 咔嚓 的相门声了（还是根据系统而定）
            mCamera.takePicture(null, null, mJpegPictureCallback);
        }
    }

    /*为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量*/
    //快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
        }
    };

    // 拍摄的未压缩原数据的回调,可以为null
    Camera.PictureCallback mRawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
        }
    };

    //这个回调处理缩放的图片
    Camera.PictureCallback mJpegPictureCallback = new Camera.PictureCallback()
            //对jpeg图像数据的回调,最重要的一个回调
    {
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap b = null;
            if (null != data) {
                b = BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图
                isPreviewing = false;
                Log.d(TAG, "onPictureTaken: " + data.length);
            }
            //保存图片到sdcard
            if (null != b) {
                //设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation", 90)失效。
                //图片竟然不能旋转了，故这里要旋转下
                Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
                FileUtil.saveBitmap(rotaBitmap);
            }
        }
    };

}
