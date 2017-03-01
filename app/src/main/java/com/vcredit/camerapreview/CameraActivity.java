package com.vcredit.camerapreview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.View;

import com.vcredit.cameraHelper.CameraInterface;
import com.vcredit.cameraHelper.CameraSurfaceView;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private CameraSurfaceView camSurfaceV;
    //    float previewRate = -1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        findViewById(R.id.btn_shutter).setOnClickListener(this);
        findViewById(R.id.btn_shift).setOnClickListener(this);
        findViewById(R.id.btn_continue).setOnClickListener(this);
        camSurfaceV = (CameraSurfaceView) findViewById(R.id.camera_surfaceview);
        initViewParams();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_continue:
                CameraInterface.getInstance().doStartPreview(camSurfaceV.getSurfaceHolder());
                break;
            case R.id.btn_shift:
                CameraInterface.getInstance().doShiftCamera(camSurfaceV.getSurfaceHolder());
                break;
            case R.id.btn_shutter:
                CameraInterface.getInstance().doTakePicture();
                break;
        }
    }

    private void initViewParams() {
//        ViewGroup.LayoutParams params = camSurfaceV.getLayoutParams();
//        Point p = DisplayUtil.getScreenMetrics(this);
//        params.width = p.x;
//        params.height = p.y;
//        previewRate = DisplayUtil.getScreenRate(this);
//        camSurfaceV.setLayoutParams(params);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraInterface.getInstance().doStopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Thread openThread = new Thread() {
            @Override
            public void run() {
                CameraInterface.getInstance().doOpenCamera();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //如果不在主线程，有时会导致黑屏？
                        CameraInterface.getInstance().doStartPreview(camSurfaceV.getSurfaceHolder());
                    }
                });
            }
        };
        openThread.start();
    }
}
