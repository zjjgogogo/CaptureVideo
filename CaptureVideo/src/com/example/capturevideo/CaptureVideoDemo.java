package com.example.capturevideo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

/**
 * 
 * class name：TestBasicVideo<BR>
 * 
 * class description：一个简单的录制视频例子<BR>
 * 
 * PS：实现基本的录制保存文件<BR>
 * 
 * 
 * 
 * @version 1.00 2011/09/21
 * 
 * @author CODYY)peijiangping
 */

public class CaptureVideoDemo extends Activity implements
		SurfaceHolder.Callback {

	final int MAX_TIME = 8;

	final int ADD_TIME = 1;

	final int CD_TIME = 1000;

	int currentTime = 0;

	private Button start;// 开始录制按钮

	private CaptureVideoController mCaptureVideoController;

	private SurfaceView surfaceview;// 显示视频的控件

	// 用来显示视频的一个接口，我靠不用还不行，也就是说用mediarecorder录制视频还得给个界面看

	// 想偷偷录视频的同学可以考虑别的办法。。嗯需要实现这个接口的Callback接口

	private SurfaceHolder surfaceHolder;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,

		WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

		// 设置横屏显示

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// 选择支持半透明模式,在有surfaceview的activity中使用。

		getWindow().setFormat(PixelFormat.TRANSLUCENT);

		setContentView(R.layout.activity_capture_video_demo);

		mCaptureVideoController = new CaptureVideoController();

		init();

	}

	private void init() {

		start = (Button) this.findViewById(R.id.start);

		start.setOnClickListener(new TestVideoListener());

		surfaceview = (SurfaceView) this.findViewById(R.id.surfaceview);

		SurfaceHolder holder = surfaceview.getHolder();// 取得holder

		holder.addCallback(this); // holder加入回调接口
		 

	}

	class TestVideoListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			if (v == start) {

				if (!mCaptureVideoController.isCapturing) {
					mCaptureVideoController.startCapture(surfaceHolder
							.getSurface());
					mHandler.sendEmptyMessageDelayed(0, CD_TIME);
				}
			}

		}

	}

	Handler mHandler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message arg0) {

			currentTime += ADD_TIME;
			if (currentTime < MAX_TIME) {
				mHandler.sendEmptyMessageDelayed(0, CD_TIME);
			} else {
				if (mCaptureVideoController != null) {
					mCaptureVideoController.stopCapture();
				}
				Toast.makeText(CaptureVideoDemo.this, "Finished",
						Toast.LENGTH_SHORT).show();
			}

			return false;
		}
	});

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,

	int height) {

		// 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder

		surfaceHolder = holder;
		

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		// 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder

		surfaceHolder = holder;

		mCaptureVideoController.previewInit(holder);
		
		mCaptureVideoController.previewStart();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		// surfaceDestroyed的时候同时对象设置为null

		surfaceview = null;

		surfaceHolder = null;

	}

}