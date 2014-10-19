package com.example.capturevideo;import android.content.Context;import android.graphics.Canvas;import android.graphics.Paint;import android.graphics.RectF;import android.util.AttributeSet;import android.util.Log;import android.view.View;public class CaptureVideoProgressView extends View {	Paint mPaint;	float percent;	RectF mRectF;	public CaptureVideoProgressView(Context context, AttributeSet attrs) {		super(context, attrs);		mPaint = new Paint();		mPaint.setColor(0xff440000);	}	public CaptureVideoProgressView(Context context) {		this(context, null);	}	protected void setProgress(float percent) {		Log.e("Progress", "percent : " + percent);		this.percent = percent;		postInvalidate();	}	@Override	protected void onDraw(Canvas canvas) {		super.onDraw(canvas);		int height = getHeight();		int width = getWidth();		if (mRectF == null) {			mRectF = new RectF(0, 0, percent * width, height);		} else {			mRectF.right = percent * width;		}		canvas.drawRect(mRectF, mPaint);	}}