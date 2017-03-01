##关于 相机预览 摄像头切换的小Demo
12/19/2016 2:27:18 PM 
 
	1 打开相机--Camera.open() 开启摄像头，返回Camera对象
	

----------

	2 预览相机--设置Camera预览参数 
	mParams = mCamera.getParameters();
	//设置拍照后存储的图片格式
    mParams.setPictureFormat(ImageFormat.JPEG);
	//设置图片大小和预览大小
	mParams.getSupportedxxxSizes()//获取支持的预览和照片尺寸
	mParams.setPictureSize(pictureSize.width, pictureSize.height);
	mParams.setPreviewSize(previewSize.width, previewSize.height);
	//设置焦点模式，（注意前摄像头设置会报错）
	mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
	mCamera.setParameters(mParams);

	设置Camera拍照参数
	//设置镜头顺时针旋转角度（建议将activity screenOrention设置为portrait）
	//如果不旋转90度，默认看到的是被逆时针旋转了90度的图像（可能和相机机制有关）
	mCamera.setDisplayOrientation(90);
	mCamera.setPreviewDisplay(holder);//设置展示的预览的surfaceholder
	
    mCamera.startPreview();//开启预览
    mCamera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。

----------

	3 开始拍照
	/*
	 * @param Camera.ShutterCallback
	 * 快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。传入null多数手机是无声的
	 * @param Camera.PictureCallback 拍摄的未压缩原数据的回调,可以为null
	 * @param Camera.PictureCallback 这个回调保存/处理缩放的图片
	 /
    mCamera.takePicture(null, null, mJpegPictureCallback);

----------
    4 释放资源
	mCamera.setPreviewCallback(null);
    mCamera.stopPreview();
    mCamera.release();
    mCamera = null;

----------
    5 摄像头切换
	//查看Camera.open()的源码，基本差不多
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

> 对相机预览的简单理解

	surfaceview：可以在子线程不断刷新界面（最快频率60fps）
	surface：内存中对绘制的数据存储
	surfaceholder：管理surfaceview 中的 surface

----------

    public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CameraSurfaceView";
    Context mContext;
    SurfaceHolder mSurfaceHolder;

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        mSurfaceHolder = getHolder();
	//        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);//translucent半透明 transparent透明
        mSurfaceHolder.setKeepScreenOn(true);// 屏幕常亮
        //过期 this is ignored, this value is set automatically when needed
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated...");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.i(TAG, "surfaceChanged...");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed...");
        CameraInterface.getInstance().doStopCamera();
    }

    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceHolder;
    }
	}