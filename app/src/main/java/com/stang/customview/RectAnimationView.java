package com.stang.customview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Arrays;
import java.util.List;

import static android.animation.ObjectAnimator.ofInt;
import static java.lang.Math.abs;

/**
 * Created by StanG on 17.10.2016.
 */

public class RectAnimationView extends View {
    public static final String TAG = "app";
    public static final int FIGURE_NONE = 0;
    public static final int FIGURE_RECTANGLE = 1;
    public static final int FIGURE_CIRCLE= 2;
    public static final int FIGURE_IMAGE = 3;

    private int mHeight = 0;
    private int mWidth = 0;
    private int mCenterX = 0;
    private int mCenterY = 0;
    private int mRadius = 20;
    private int mCenterAlpha = 0;
    private int mSpeed = 100;
    //private int mRepeat = 1;
    //private long mRepeatedCycles = 0;
    private int mLineColor = Color.BLACK;
    private int mLineWidth = 2;
    private int mDotFigure = FIGURE_NONE;
    private int mDotWidth = 20;
    private int mDotColor = Color.BLUE;

    Paint mLinePaint;
    Paint mDotPaint;
    Paint mCenterPaint;
    private Drawable mDotsImage;
    private Drawable mCenterImage;

    private boolean isRunning = false;

    private Vertex[] mVertex;
    AnimatorSet mAnimatorSet;

    MyView.OnAnimationEventListener mAnimationListener = null;


    public void setSpeed(int speed) { mSpeed = speed; setPaintProperties(); }

    public void setLineColor(int lineColor) { mLineColor = lineColor; setPaintProperties(); }

    public void setLineWidth(int lineWidth) { mLineWidth = lineWidth; setPaintProperties(); }

    public void setDotFigure(int dotFigure) { mDotFigure = dotFigure; setPaintProperties(); }

    public void setDotWidth(int dotWidth) { mDotWidth = dotWidth; setPaintProperties(); }

    public void setDotColor(int dotColor) { mDotColor = dotColor; setPaintProperties(); }

    public void setDotsImage(Drawable image) { mDotsImage = image; mCenterImage = image; setPaintProperties(); }

    public void setMCenterAlpha(int a) {mCenterAlpha = a;}

    public int getSpeed() { return mSpeed; }

    public int getLineColor() { return mLineColor; }

    public int getLineWidth() { return mLineWidth; }

    public int getDotFigure() { return mDotFigure; }

    public int getDotWidth() { return mDotWidth; }

    public int getDotColor() { return mDotColor; }

    public Drawable getDotsImage() { return mDotsImage; }

    public boolean isRunning() { return isRunning; }


    public void setOnAnimationEventListener(MyView.OnAnimationEventListener listener) {
        mAnimationListener = listener;
    }

    public interface OnAnimationEventListener {
        void onAnimationStarted();
        void onAnimationStopped();
        void onAnimationCollapsed();
        void onAnimationExploded();
    }

    private void onAnimationStarted() {
        if (mAnimationListener != null) {
            mAnimationListener.onAnimationStarted();
        }
        Log.d(TAG, "OnStartAnimation");
    }

    private void onAnimationStopped() {
        if (mAnimationListener != null) {
            mAnimationListener.onAnimationStopped();
        }
    }

    private void onAnimationCollapsed() {
        if (mAnimationListener != null) {
            mAnimationListener.onAnimationCollapsed();
        }
    }

    private void onAnimationExploded() {
        if (mAnimationListener != null) {
            mAnimationListener.onAnimationExploded();
        }
    }


    public void startAnim() {
        Log.d(TAG,"start");
        mAnimatorSet.start();
        isRunning = true;
        invalidate();
        onAnimationStarted();
    }

    public void stopAnim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAnimatorSet.pause();
        } else {
            mAnimatorSet.cancel();
        }
        isRunning = false;
        onAnimationStopped();
    }


    //-------------------------------
    // RecrAnimationView constructors
    //-------------------------------

    public RectAnimationView(Context context) {
        super(context);
    }

    public RectAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RectAnimationView,
                0, 0
        );

        try {
            mLineColor = a.getColor(R.styleable.RectAnimationView_rav_line_color, Color.BLACK);
            mDotColor = a.getColor(R.styleable.RectAnimationView_rav_dot_color, Color.BLACK);
            mSpeed = a.getInt(R.styleable.RectAnimationView_rav_speed_animation, 100);
            mDotFigure = a.getInt(R.styleable.RectAnimationView_rav_dot_figure, FIGURE_CIRCLE);
            mDotWidth = a.getDimensionPixelSize(R.styleable.RectAnimationView_rav_dot_width, 10);
            mLineWidth = a.getDimensionPixelSize(R.styleable.RectAnimationView_rav_line_width, 1);
        } finally {
            a.recycle();
        }

        mRadius = mDotWidth;

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                onAnimationExploded();
                //
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                onAnimationCollapsed();
                isRunning = false;
                //
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                //
                isRunning = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //
            }
        });

        mLinePaint = new Paint();
        mDotPaint = new Paint();
        mCenterPaint = new Paint();

        mVertex = new Vertex[4];

        setDotsImage(context.getResources().getDrawable(R.drawable.ok_));

        setPaintProperties();
    }


    private void setPaintProperties(){
        mCenterPaint.setColor(mDotColor);
        mCenterImage.setAlpha(mCenterAlpha);
        mCenterPaint.setAlpha(mCenterAlpha);
        mDotPaint.setColor(mDotColor);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(mLineWidth);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;
        mCenterX = mWidth /2;
        mCenterY = mHeight /2;
        Log.d(TAG, "onSizeChanged: " + mHeight + ":" + mWidth + "   center: " + mCenterX + ":" + mCenterY + "   mRadius: " + mRadius);
        init();
    }


    private void init() {
        Point center = new Point(mCenterX, mCenterY);
        Point leftUp = new Point(mRadius, mRadius);
        Point leftBottom = new Point(mRadius, mHeight-mRadius);
        Point rightUp = new Point(mWidth-mRadius, mRadius);
        Point rightBottom = new Point(mWidth - mRadius, mHeight - mRadius);

        Point left = new Point(mRadius, mCenterY);
        Point bottom = new Point(mCenterX, mHeight - mRadius);
        Point right = new Point(mWidth - mRadius, mCenterY);
        Point up = new Point(mCenterX, mRadius);

        mVertex[0] = new Vertex(leftUp, left, center);
        mVertex[1] = new Vertex(leftBottom, bottom, center);
        mVertex[2] = new Vertex(rightBottom, right, center);
        mVertex[3] = new Vertex(rightUp, up, center);

        int delay = 200;

        AnimatorSet playTo = new AnimatorSet();
        playTo.playTogether(
                mVertex[0].setStartDelay(delay * 0).getAnimTo(),
                mVertex[1].setStartDelay(delay * 1).getAnimTo(),
                mVertex[2].setStartDelay(delay * 2).getAnimTo(),
                mVertex[3].setStartDelay(delay * 3).getAnimTo()
                );

        AnimatorSet playFrom = new AnimatorSet();
        playFrom.playTogether(
                mVertex[0].setStartDelay(delay * 0).getAnimFrom(),
                mVertex[1].setStartDelay(delay * 1).getAnimFrom(),
                mVertex[2].setStartDelay(delay * 2).getAnimFrom(),
                mVertex[3].setStartDelay(delay * 3).getAnimFrom()
        );

        ObjectAnimator alphaAnim = ObjectAnimator.ofInt(this, "mCenterAlpha", new int[]{0,255,0,255,0,255,0,255,0,255,0}).setDuration(2000);

        mAnimatorSet.playSequentially(playTo, playFrom, alphaAnim);
     }


    @Override
    protected void onDraw(Canvas canvas) {
        drawLines(canvas);
        drawDots(canvas);
        if(isRunning()) postInvalidateDelayed(50);
    }

    private void drawDots(Canvas canvas) {
        switch (mDotFigure){
            case FIGURE_NONE: //none
                break;

            case FIGURE_RECTANGLE:
                mCenterPaint.setAlpha(mCenterAlpha);
                canvas.drawRect(mHeight /2- mRadius *2, mWidth /2- mRadius *2, mHeight /2+ mRadius *2, mWidth /2+ mRadius *2, mCenterPaint);
                for (int i = 0; i < mVertex.length; i++) {
                    canvas.drawRect(mVertex[i].getX()- mRadius, mVertex[i].getY()- mRadius, mVertex[i].getX()+ mRadius, mVertex[i].getY()+ mRadius, mDotPaint);
                }
                break;

            case FIGURE_CIRCLE:
                mCenterPaint.setAlpha(mCenterAlpha);
                canvas.drawCircle(mHeight /2, mWidth /2, mRadius *2, mCenterPaint);
                for (int i = 0; i < mVertex.length; i++) {
                    canvas.drawCircle(mVertex[i].getX(), mVertex[i].getY(), mRadius, mDotPaint);
                }
                break;

            case FIGURE_IMAGE:
                mCenterImage.setBounds((int)(mHeight / 2 - mRadius *2), (int)(mWidth / 2 - mRadius *2),
                        (int)(mHeight / 2 + mRadius *2), (int)(mWidth / 2 + mRadius *2));
                mCenterImage.setAlpha(mCenterAlpha);
                mCenterImage.draw(canvas);

                mDotsImage.setAlpha(255);
                for (int i = 0; i < mVertex.length; i++) {
                    mDotsImage.setBounds(mVertex[i].getX()- mRadius, mVertex[i].getY()- mRadius,
                            mVertex[i].getX()+ mRadius, mVertex[i].getY()+ mRadius);
                    mDotsImage.draw(canvas);
                }
                break;
        }
    }

    private void drawLines(Canvas canvas) {
        canvas.drawLine(mVertex[0].getX(), mVertex[0].getY(), mVertex[1].getX(), mVertex[1].getY(), mLinePaint);
        canvas.drawLine(mVertex[0].getX(), mVertex[0].getY(), mVertex[2].getX(), mVertex[2].getY(), mLinePaint);
        canvas.drawLine(mVertex[0].getX(), mVertex[0].getY(), mVertex[3].getX(), mVertex[3].getY(), mLinePaint);

        canvas.drawLine(mVertex[1].getX(), mVertex[1].getY(), mVertex[2].getX(), mVertex[2].getY(), mLinePaint);
        canvas.drawLine(mVertex[1].getX(), mVertex[1].getY(), mVertex[3].getX(), mVertex[3].getY(), mLinePaint);

        canvas.drawLine(mVertex[2].getX(), mVertex[2].getY(), mVertex[3].getX(), mVertex[3].getY(), mLinePaint);
    }


    public class Point {
        public int x = 0;
        public int y = 0;

        public Point(int x, int y) { this.x = x; this.y = y; }

        public int getX() { return x; }
        public int getY() {
            return y;
        }
        public void setX(int x) { this.x = x;}
        public void setY(int y) { this.y = y;}
     }

    public class VertexL {
        private int mStartDelay = 0;
        private List<Point> mPath;
        public Point mCurrentPoint;

        public int getX(){ return mCurrentPoint.getX(); }
        public int getY(){ return mCurrentPoint.getY(); }

        public VertexL(Point ... points) {
            if(points != null) {
                mPath = Arrays.asList(points);
                mCurrentPoint = new Point(
                        mPath.get(0).getX(),
                        mPath.get(0).getY()
                        );
            }
        }

        public VertexL() {

        }

        public AnimatorSet getAnimTo(){
            int duration = 1000;
            int[] xArray = new int[mPath.size()];
            int[] yArray = new int[mPath.size()];

            for (int i = 0; i < mPath.size(); i++) {
                xArray[i] = mPath.get(i).getX();
                yArray[i] = mPath.get(i).getY();
            }

            AnimatorSet result = new AnimatorSet();
            result.playTogether(ObjectAnimator.ofInt(this.mCurrentPoint,"x",xArray).setDuration(duration),
                    ObjectAnimator.ofInt(this.mCurrentPoint,"y", yArray).setDuration(duration));
            return result;
        }

        public AnimatorSet getAnimFrom(){
            int duration = 1000;
            int[] xArray = new int[mPath.size()];
            int[] yArray = new int[mPath.size()];

            for (int i = 0; i < mPath.size(); i++) {
                xArray[i] = mPath.get(mPath.size() - i -1).getX();
                yArray[i] = mPath.get(mPath.size() - i -1).getY();
            }

            AnimatorSet result = new AnimatorSet();
            result.playTogether(ObjectAnimator.ofInt(this.mCurrentPoint,"x",xArray).setDuration(duration),
                    ObjectAnimator.ofInt(this.mCurrentPoint,"y", yArray).setDuration(duration));
            result.setStartDelay(mStartDelay);
            return result;
        }

        public VertexL setStartDelay(int delay) {
            mStartDelay = delay;
            return this;
        }
    }

    public class Vertex {
        private int mStartDelay = 0;
        private Point[] mPath;
        public Point mCurrentPoint;

        public int getX(){ return mCurrentPoint.getX(); }
        public int getY(){ return mCurrentPoint.getY(); }

        public Vertex(Point ... points) {
            if(points != null) {
                mPath = points;
                mCurrentPoint = new Point(
                        mPath[0].getX(),
                        mPath[0].getY()
                );
            }
        }

        public Vertex(){

        }

        public AnimatorSet getAnimTo(){
            int duration = 1000;
            int[] xArray = new int[mPath.length];
            int[] yArray = new int[mPath.length];

            for (int i = 0; i < mPath.length; i++) {
                xArray[i] = mPath[i].getX();
                yArray[i] = mPath[i].getY();
            }

            AnimatorSet result = new AnimatorSet();
            result.playTogether(ObjectAnimator.ofInt(this.mCurrentPoint,"x",xArray).setDuration(duration),
                    ObjectAnimator.ofInt(this.mCurrentPoint,"y", yArray).setDuration(duration));
            result.setStartDelay(mStartDelay);
            return result;
        }

        public AnimatorSet getAnimFrom(){
            int duration = 1000;
            int[] xArray = new int[mPath.length];
            int[] yArray = new int[mPath.length];

            for (int i = 0; i < mPath.length; i++) {
                xArray[i] = mPath[mPath.length - i -1].getX();
                yArray[i] = mPath[mPath.length - i -1].getY();
            }

            AnimatorSet result = new AnimatorSet();
            result.playTogether(ObjectAnimator.ofInt(this.mCurrentPoint,"x",xArray).setDuration(duration),
                    ObjectAnimator.ofInt(this.mCurrentPoint,"y", yArray).setDuration(duration));
            result.setStartDelay(mStartDelay);
            return result;
        }

        public Vertex setStartDelay(int delay) {
            mStartDelay = delay;
            return this;
        }
    }


}
