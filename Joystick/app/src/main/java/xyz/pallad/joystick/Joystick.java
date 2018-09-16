/*
* Joystick Component
* 
* Copyright (c) 2018 Glenn Pallad
* Released under the MIT license.
*/

package xyz.pallad.joystick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.lang.Math;

import static android.content.ContentValues.TAG;

public class Joystick extends View {

	private static final int DEFAULT_SIZE = 170;
	private Paint mPaint;
	private float originPointX;
	private float originPointY;
	private float innerCircleX;
	private float innerCircleY;
	private float innerCircleR;
	private float innerCircleRangeRadius;
	private float rate;
	private int mWidth;
	private int mHeight;
	private boolean initInnerCircleXYFlag = true;

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
			initInnerCircleXYFlag = false;
			innerCircleRangeRadius = 0.46f * getWidth()/2;
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
			if (pointerRadius < innerCircleRangeRadius) {
				innerCircleX = event.getX();
				innerCircleY = event.getY();
				Log.d(TAG, "onTouchEvent: 1");
			} else {
				rate = pointerRadius/innerCircleRangeRadius;
				innerCircleX = getCCX(event)/rate + originPointX;
				innerCircleY = getCCY(event)/rate + originPointY;
				Log.d(TAG, "onTouchEvent: 2");
			}
		} else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
			innerCircleX = originPointX;
			innerCircleY = originPointY;
		}
		invalidate();
		return true;
	}

	/**
	* Get CenterCoordinate X.
	* @param event MotionEvent that we wanna get its X value in CenterCoordinate, 
	* 			   Which is a hypothetical coordinate whose origin point is (originPointX, originPointY).
	*/
	private float getCCX(MotionEvent event) {
		return event.getX() - originPointX;
	}

	/**
	* Get CenterCoordinate Y.
	* @param event MotionEvent that we wanna get its Y value in CenterCoordinate, 
	* 			   Which is a hypothetical coordinate whose origin point is (originPointX, originPointY).
	*/
	private float getCCY(MotionEvent event) {
		return event.getY() - originPointY;
	}
}
