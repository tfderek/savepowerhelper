package com.mfox.savepower.savepowerhelper;
import com.mfox.savepower.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Created by zhangyuanlu on 2017/5/11.
 */

public class MProgressBar extends ProgressBar {

    private String mText;
    private float mTextSize;
    private int mTextColor;
    private float fontScale;
    private Rect mRect;
    private Paint mPaint;
    private int minWidth;
    private int minHeight;
    private LinearGradient mLinearGradient;
    private Matrix mGradientMatrix;
    private int mViewWidth=0;
    private int mTranslate=0;
    private boolean mAnimating = true;
    private int mStartColor,mEndColor;

    public MProgressBar(Context context) {
        super(context);
    }

    public MProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        TypedArray ta=context.obtainStyledAttributes(attrs,R.styleable.MProgressBar);
        mText=ta.getString(R.styleable.MProgressBar_text);
        mTextSize=ta.getFloat(R.styleable.MProgressBar_textSize,14)*fontScale+0.5f;
        mTextColor=ta.getColor(R.styleable.MProgressBar_textColor, Color.BLACK);
        mStartColor=ta.getColor(R.styleable.MProgressBar_startColor,Color.BLACK);
        mEndColor=ta.getColor(R.styleable.MProgressBar_endColor,Color.parseColor("#c0c0c0"));
        minWidth=minHeight=250;
        ta.recycle();
    }

    public MProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText(mText,getMeasuredWidth()/2-mRect.width(),getMeasuredHeight()-mRect.height()/3,mPaint);
        if(mAnimating&&mGradientMatrix!=null) {
            mTranslate += mViewWidth / 10;
            if (mTranslate > 2 * mViewWidth)
                mTranslate = -mViewWidth;
            mGradientMatrix.setTranslate(mTranslate, 0);
            mLinearGradient.setLocalMatrix(mGradientMatrix);
            postInvalidateDelayed(100);
        }
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(mPaint==null)
        	mPaint=new Paint();
        mRect=new Rect();
        mPaint.getTextBounds(mText,0,mText.length(),mRect);
        int width=getMeasuredSize(widthMeasureSpec+mRect.width(),true);
        int height=getMeasuredSize(heightMeasureSpec+mRect.height(),false);
        setMeasuredDimension(width,height+10);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        if(mViewWidth==0)
            mViewWidth=getMeasuredWidth();
        if(mViewWidth>0){
            mLinearGradient=new LinearGradient(0, 0, mViewWidth, 0,
                    new int[] { mStartColor, 0xffffffff,mEndColor },
                    null, Shader.TileMode.CLAMP);
            mPaint.setShader(mLinearGradient);
            mGradientMatrix = new Matrix();
        }
    }

    private int getMeasuredSize(int length, boolean isWidth){
        int specMode=MeasureSpec.getMode(length);
        int specSize=MeasureSpec.getSize(length);
        int retSize=0;
        int padding=(isWidth?getPaddingLeft()+getPaddingRight():getPaddingTop()+getPaddingBottom());
        if(specMode==MeasureSpec.EXACTLY)
            retSize=specSize;
        else{
            retSize=(isWidth?minWidth+padding:minHeight+padding);
            if(specMode==MeasureSpec.UNSPECIFIED)
                retSize=Math.min(retSize,specSize);
        }
        return retSize;
    }
}
