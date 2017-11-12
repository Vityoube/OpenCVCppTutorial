package vkalashnykov.org.opencvcpp2;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static String TAG="MainActivity";
    JavaCameraView javaCameraView;
    BaseLoaderCallback baseLoaderCallback=new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case BaseLoaderCallback.SUCCESS: {
                    javaCameraView.enableView();
                    break;
                }
                default: {
                    super.onManagerConnected(status);
                }
            }

        }
    };

    Mat mRgba;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
//        System.loadLibrary("opencv_java3");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        javaCameraView=(JavaCameraView)findViewById(R.id.javaCameraView);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
        // Example of a call to a native method
//        TextView textView = (TextView)findViewById(R.id.sample_text);
//        textView.setText(stringFromJNI());


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView!=null)
            javaCameraView.disableView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCameraView!=null)
            javaCameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()){
            Log.i(TAG,"OpenCV is not loaded successuly");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.i(TAG,"OpenCV is loaded successfuly");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0,this,baseLoaderCallback);
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public native String validate(long matAddr,long matAddrRgba);

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba =new Mat(height,width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba=inputFrame.rgba();
        return mRgba;
    }
}
