/*
 * Joystick Component
 *
 * Copyright (c) 2018 Wulfric Lee
 * Released under the MIT license.
 */

package com.bitwitcher.joystick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.text.DecimalFormat;

public class Joystick extends View {
	private static final int DEFAULT_SIZE = 170;
	private Paint mPaint;
	private float originPointX;
	private float originPointY;
	private float innerCircleX;
	private float innerCircleY;
	private float innerCircleR;
	private float innerCircleRangeRadius;
	private float mCCX;
	private float mCCY;
	private float rate;
	private int mWidth;
	private int mHeight;
	private boolean initInnerCircleXYFlag = true;

	/* factorX and factorY range from 0 ~ 1, which are used as output. */
	public static float factorX = 0f;
	public static float factorY = 0f;

	DecimalFormat decimalFormat = new DecimalFormat("#.##");

	public Joystick(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(0xffbdc3c7);
		mPaint.setShadowLayer(15f, 0f, 0f, 0xff000000);
		setLayerType(LAYER_TYPE_SOFTWARE, mPaint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(calculateMeasure(widthMeasureSpec), calculateMeasure(heightMeasureSpec));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (initInnerCircleXYFlag) {
			originPointX = getWidth()/2;
			originPointY = getHeight()/2;
			innerCircleX = originPointX;
			innerCircleY = originPointY;
			innerCircleRangeRadius = 0.46f * getWidth()/2;
			initInnerCircleXYFlag = false;
		}
		canvas.save();
		mWidth = getWidth();
		mHeight = getHeight();
		innerCircleR = mWidth/4f;
		canvas.drawCircle(originPointX, originPointY, mWidth/2.3f, mPaint);
		mPaint.setColor(0xff2c3e50);
		canvas.drawCircle(innerCircleX, innerCircleY, innerCircleR, mPaint);
		mPaint.setColor(0xffbdc3c7);
		canvas.restore();
	}

	private int calculateMeasure(int measureSpec) {
		int result = (int) (DEFAULT_SIZE * getResources().getDisplayMetrics().density);
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			Log.d("Joystick", "MeasureSpec.EXACTLY");
			result = specSize;
		} else if (specMode == MeasureSpec.AT_MOST) {
			Log.d("Joystick", "MeasureSpec.AT_MOST");
			result = Math.min(result, specSize);
		}
		return result;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_MOVE) {
			float pointerRadius = (float) Math.sqrt(Math.pow(getCCX(event), 2) + Math.pow(getCCY(event), 2));
			mCCX = getCCX(event);
			mCCY = getCCY(event);
			if (pointerRadius < innerCircleRangeRadius) {
				innerCircleX = event.getX();
				innerCircleY = event.getY();
				factorX = mCCX/innerCircleRangeRadius;
				factorY = (-mCCY)/innerCircleRangeRadius;
				Log.d("Joystick", "onTouchEvent: 1");
				Log.d("Joystick", "factorX = " + decimalFormat.format(factorX));
				Log.d("Joystick", "factorY = " + decimalFormat.format(factorY));
			} else {
				rate = pointerRadius/innerCircleRangeRadius;
				innerCircleX = mCCX/rate + originPointX;
				innerCircleY = mCCY/rate + originPointY;
				factorX = mCCX/rate/innerCircleRangeRadius;
				factorY = (-mCCY)/rate/innerCircleRangeRadius;
				Log.d("Joystick", "onTouchEvent: 2");
				Log.d("Joystick", "factorX = " + decimalFormat.format(factorX));
				Log.d("Joystick", "factorY = " + decimalFormat.format(factorY));
			}
		} else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
			factorX = 0f;
			factorY = 0f;
			Log.d("Joystick", "onTouchEvent: ACTION_UP");
			Log.d("Joystick", "factorX = " + decimalFormat.format(factorX));
			Log.d("Joystick", "factorY = " + decimalFormat.format(factorY));
			innerCircleX = originPointX;
			innerCircleY = originPointY;
		}
		invalidate();
		return true;
	}

	/**
	 * Get CenterCoordinate X.
	 * @param event 	MotionEvent that we wanna get its X value in CenterCoordinate,
	 * 				Which is a hypothetical coordinate whose origin point is (originPointX, originPointY).
	 */
	private float getCCX(MotionEvent event) {
		return event.getX() - originPointX;
	}

	/**
	 * Get CenterCoordinate Y.
	 * @param event 	MotionEvent that we wanna get its Y value in CenterCoordinate,
	 * 				Which is a hypothetical coordinate whose origin point is (originPointX, originPointY).
	 * @NOTE 		For Y, DOWNSIDE is POSITIVE, UPSIDE is NEGATIVE.
	 */
	private float getCCY(MotionEvent event) {
		return event.getY() - originPointY;
	}
}
