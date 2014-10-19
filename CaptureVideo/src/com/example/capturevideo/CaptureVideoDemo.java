package com.example.capturevideo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class CaptureVideoDemo extends Activity implements
		SurfaceHolder.Callback {

	final int MAXTIME = 10000;

	int currentTime = 0;

	private View start;// 开始录制按钮

	private CaptureVideoController mCaptureVideoController;

	private SurfaceView surfaceview;// 显示视频的控件

	// 用来显示视频的一个接口，我靠不用还不行，也就是说用mediarecorder录制视频还得给个界面看

	// 想偷偷录视频的同学可以考虑别的办法。。嗯需要实现这个接口的Callback接口

	private SurfaceHolder surfaceHolder;

	private CaptureVideoProgressView mCaptureVideoProgressView;

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

		start = this.findViewById(R.id.start);

		// start.setOnClickListener(new TestVideoListener());

		start.setOnTouchListener(mOnTouchListener);

		mCaptureVideoProgressView = (CaptureVideoProgressView) findViewById(R.id.progress);

		surfaceview = (SurfaceView) this.findViewById(R.id.surfaceview);

		SurfaceHolder holder = surfaceview.getHolder();// 取得holder

		holder.addCallback(this); // holder加入回调接口

	}

	TimeThread mThread = new TimeThread();
	OnTouchListener mOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Log.e("onTouch", "ACTION_DOWN");
				if (!mCaptureVideoController.isCapturing
						&& currentTime < MAXTIME) {
					mCaptureVideoController.startCapture(surfaceHolder
							.getSurface());
				}
				mThread = new TimeThread();
				mThread.start();
				break;
			case MotionEvent.ACTION_MOVE:
				Log.e("onTouch", "ACTION_MOVE");
				break;
			case MotionEvent.ACTION_UP:
				Log.e("onTouch", "ACTION_UP");

				mThread.isRun = false;
				mThread = null;

				if (mCaptureVideoController != null) {
					mCaptureVideoController.stopCapture();
				}
				mCaptureVideoController.cleanInvalidFile();

				break;
			}

			return true;
		}
	};

	class TimeThread extends Thread {

		boolean isRun = true;

		@Override
		public void run() {
			while (currentTime < MAXTIME) {

				if (!isRun) {
					break;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (!isRun) {
					break;
				}

				currentTime += 100;
				mCaptureVideoProgressView.setProgress((float) currentTime
						/ MAXTIME);

				if (!isRun) {
					break;
				}
			}

			if (currentTime == MAXTIME) {
				mHandler.sendEmptyMessage(0);
			}
		}
	}

	Handler mHandler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message arg0) {

			if (mCaptureVideoController != null) {
				mCaptureVideoController.stopCapture();
			}
			Toast.makeText(CaptureVideoDemo.this, "Finished",
					Toast.LENGTH_SHORT).show();

			Intent mIntent = new Intent(CaptureVideoDemo.this,
					VideoActivity.class);
			startActivity(mIntent);

			finish();

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

		mCaptureVideoController.closeCamera();

		surfaceview = null;

		surfaceHolder = null;

	}

}