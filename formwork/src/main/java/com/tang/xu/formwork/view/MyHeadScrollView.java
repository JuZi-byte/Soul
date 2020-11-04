package com.tang.xu.formwork.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class MyHeadScrollView extends ScrollView {

    //头部图片
    private View headPhoto;
    private int headWidth;
    private int headHeight;

    //是否在滑动
    private boolean isScroll=false;

    //第一次按下的坐标
    private float firstPosition;

    //滑动系数
    private float scrollNumber=0.3f;

    //回弹系数
    private float resultNumber=0.5f;

    public MyHeadScrollView(Context context) {
        super(context);
    }

    public MyHeadScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyHeadScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildAt(0)!=null){
            ViewGroup viewGroup = (ViewGroup) getChildAt(0);
            if (viewGroup.getChildAt(0)!=null){
                headPhoto = viewGroup.getChildAt(0);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (headWidth<=0||headHeight<=0){
            headHeight=headPhoto.getMeasuredHeight();
            headWidth=headPhoto.getMeasuredWidth();
        }

        switch (ev.getAction()){
            case MotionEvent.ACTION_MOVE:
                if (!isScroll){
                    if (getScrollY()==0){
                        firstPosition = ev.getY();
                    }else {
                        break;
                    }
                }
                int v = (int) ((ev.getY() - firstPosition) * scrollNumber);
                if (v<0){
                    break;
                }
                isScroll=true;
                setHeadView(v);
                break;
                case MotionEvent.ACTION_UP:
                    isScroll=false;
                    resultNumber();
                    break;
        }
        return true;
    }

    private void resultNumber() {
        int i =(headPhoto.getMeasuredWidth() - headWidth);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(i, 0)
                .setDuration((long) (i*resultNumber));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setHeadView((Float) animation.getAnimatedValue());
            }
        });
        valueAnimator.start();
    }

    private void setHeadView(float v) {
        if (headWidth<=0||headHeight<=0){
            return;
        }
        ViewGroup.LayoutParams layoutParams = headPhoto.getLayoutParams();
        layoutParams.width= (int) (headWidth+v);
        //现在的宽/原来的宽 得到缩放比例 * 原来的的高  得到现在的高
        layoutParams.height= (int) (headHeight*((headWidth+v)/headWidth));
        //设置间距
        ((MarginLayoutParams)layoutParams).setMargins(-(layoutParams.width-headWidth)/2,0,0,0);

        headPhoto.setLayoutParams(layoutParams);
    }
}
