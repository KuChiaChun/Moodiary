package com.example.david.myapplication;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.util.Stack;

/**
 * Created by Haoz on 2017/3/12 0012.
 */

public class AppManager {
    private static Stack<AppCompatActivity> activityStack;
    private static AppManager instance;

    private AppManager(){}

    public static AppManager getAppManager(){
        if(instance == null){
            synchronized (AppManager.class){
                if(instance == null){
                    instance = new AppManager();
                }
            }
        }
        return instance;
    }
    
    public void addActivity(AppCompatActivity activity){
        if(activityStack == null){
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * 获得当前Activity(栈顶Activity)
     * 
     * @return
     */
    public AppCompatActivity currentActivity(){
        
        if(activityStack == null || activityStack.isEmpty()){
            return null;
        }
        AppCompatActivity acitivity = activityStack.lastElement();
        return acitivity;
    }
    
    public AppCompatActivity findActivity(Class<?> cls){
        AppCompatActivity activity = null;
        for (AppCompatActivity appCompatActivity : activityStack) {
            if(appCompatActivity.getClass().equals(cls)){
                activity = appCompatActivity;
                break;
            }
        }
        return activity;
    }
    
    public void finishActivity(){
        AppCompatActivity activity = activityStack.lastElement();
        finishActivity(activity);
    }
    
    public void finishActivity(AppCompatActivity activity){
        if(activity != null){
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }
    
    public void finishActivity(Class<?> cls){
        for (AppCompatActivity activity : activityStack) {
            if(activity.getClass().equals(cls)){
                finishActivity(activity);
            }
        }
    }
    
    public void finishAllActivity(){
            for (int i = 0, size = activityStack.size(); i < size; i++) {
                if (null != activityStack.get(i)) {
                    activityStack.get(i).finish();
                }
            }
            activityStack.clear();
    }

    public void AppExit(Context context){
        try{
            finishAllActivity();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Created by pc on 2015/5/11.
     */
    public static class MoveImage extends android.support.v7.widget.AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener, View.OnTouchListener {

        private boolean mInit;
        private boolean mIsCanMove;
        private float mLastX;
        private float mLastY;
        private Matrix mScaleMatrix;
        private int mMoveSlop;

        public MoveImage(Context context) {
            this(context, null);
        }

        public MoveImage(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public MoveImage(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);

            init(context);
        }

        private void init(Context context) {
            mScaleMatrix = new Matrix();
            mMoveSlop = ViewConfiguration.get(context).getScaledTouchSlop();
            setOnTouchListener(this);
            mIsCanMove = false;
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            getViewTreeObserver().addOnGlobalLayoutListener(this);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }

        @Override
        public void onGlobalLayout() {
            if (!mInit) {
                Drawable d = getDrawable();
                if (d != null) {
                    float scale = 1.0f;

                    int dw = d.getIntrinsicWidth();
                    int dh = d.getIntrinsicHeight();

                    int width = getWidth();
                    int height = getHeight();

                    if (dw > width && dh < height) {
                        scale = width * 1.0f / dw;
                    } else if (dw < width && dh > height) {
                        scale = height * 1.0f / dh;
                    } else if (dw < width && dh < height) {
                        scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
                    } else if (dw > width && dh > height) {
                        scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
                    } else {
                        scale = 1.0f;
                    }

                    int centerX = (width - dw) / 2;
                    int centerY = (height - dh) / 2;
                    mScaleMatrix.postTranslate(centerX, centerY);
                    scale = scale / 2.0f;
                    mScaleMatrix.postScale(scale, scale, width / 2, height / 2);
                    setImageMatrix(mScaleMatrix);
                }
            }
            mInit = true;
        }

        private RectF getMatrixRectF() {
            RectF rectf = new RectF();
            final Matrix matrix = mScaleMatrix;
            Drawable d = getDrawable();
            if (d != null) {
                rectf.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                matrix.mapRect(rectf);
            }
            return rectf;
        }

        private boolean isMoveAction(float dx, float dy) {
            return Math.sqrt(dx * dx + dy * dy) > mMoveSlop;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            RectF rectF = getMatrixRectF();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastX = event.getX();
                    mLastY = event.getY();
                    mIsCanMove = rectF.contains(mLastX, mLastY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mIsCanMove) {
                        float x = event.getX();
                        float y = event.getY();
                        float deltaX = x - mLastX;
                        float deltaY = y - mLastY;

                        if (isMoveAction(deltaX, deltaY)) {
                            checkBorderWhenMove(deltaX, deltaY, rectF);
                            setImageMatrix(mScaleMatrix);
                        }

                        mLastX = x;
                        mLastY = y;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mIsCanMove = false;
                    break;
            }
            return true;
        }

        private void checkBorderWhenMove(float deltaX, float deltaY, RectF rectF) {
            int width = getWidth();
            int height = getHeight();

            if (rectF.left + deltaX < 0) {
                deltaX = -rectF.left;
            } else if (rectF.right + deltaX > width) {
                deltaX = width - rectF.right;
            }
            if (rectF.top + deltaY < 0) {
                deltaY = -rectF.top;
            } else if (rectF.bottom + deltaY > height) {
                deltaY = height - rectF.bottom;
            }
            mScaleMatrix.postTranslate(deltaX, deltaY);
        }
    }
}




















