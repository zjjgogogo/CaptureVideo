package com.example.capturevideo;

import java.io.File;
import java.io.IOException;

import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.view.Surface;
import android.view.SurfaceHolder;

public class CaptureVideoController {

	MediaRecorder mediaRecorder;

	Camera mCamera;

	boolean isCapturing = false;

	int index = 0;

	public boolean isCapturing() {
		return isCapturing;
	}

	public CaptureVideoController() {
		cleanTempFile();
	}

	protected void cleanTempFile() {

		File mFile = new File("/sdcard/tempVideo/");

		if (!mFile.exists()) {
			return;
		}

		File[] files = mFile.listFiles();

		for (File file : files) {
			file.delete();
		}

	}

	public void previewInit(SurfaceHolder holder) {

		try {
			mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
			mCamera.setDisplayOrientation(90);
			mCamera.setPreviewDisplay(holder);
			// mCamera.unlock();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeCamera() {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
		}
	}

	public void previewStart() {
		if (mCamera != null) {
			mCamera.startPreview();
		}
	}

	public void startCapture(Surface mSurface) {

		isCapturing = true;

		index++;

		mediaRecorder = new MediaRecorder();// 创建mediarecorder对象

		// 设置录制视频源为Camera(相机)

		mCamera.unlock();

		mCamera.stopPreview();

		mediaRecorder.setCamera(mCamera);

		mediaRecorder.setOrientationHint(90);

		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		 
		
		// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4

		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

		// 设置录制的视频编码h263 h264

		mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

		mediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);

		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

		// 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错

		mediaRecorder.setVideoSize(640, 480);

		// 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错

		mediaRecorder.setVideoFrameRate(30);

		mediaRecorder.setPreviewDisplay(mSurface);

		// 设置视频文件输出的路径

		File mFile = new File("/sdcard/tempVideo/");

		if (!mFile.exists()) {
			mFile.mkdirs();
		}
		mediaRecorder.setOutputFile("/sdcard/tempVideo/temp" + index + ".h264");
		try {

			// 准备录制

			mediaRecorder.prepare();

			// 开始录制

			mediaRecorder.start();

		} catch (IllegalStateException e) {

			isCapturing = false;
			e.printStackTrace();

		} catch (IOException e) {

			isCapturing = false;
			e.printStackTrace();

		}

	}

	public void cleanInvalidFile() {
		File mFile = new File("/sdcard/tempVideo/temp" + index + ".h264");
		if (mFile.exists()) {
			try {
				MovieCreator.build(mFile.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
				mFile.delete(); 
				index--;
			}
		}
	}

	public void stopCapture() {

		if (mediaRecorder != null) {

			if (!isCapturing) {
				return;
			}
			// 停止录制
			try {
				mediaRecorder.stop();

				// 释放资源

				mediaRecorder.release();
			} catch (Exception e) {
				e.printStackTrace();
			}

			mediaRecorder = null;

			isCapturing = false;

			mCamera.lock();

		}
	}
}
