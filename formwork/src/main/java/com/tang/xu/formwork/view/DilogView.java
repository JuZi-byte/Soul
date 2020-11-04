package com.tang.xu.formwork.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.tang.xu.formwork.R;

public class DilogView extends View {

    //背景
    private Bitmap backgroup;
    //画笔
    private Paint mPaint;
    //空白块
    private Bitmap nullBackgroup;
    //空白块画笔
    private Paint nullPaint;
    //移动方块
    private Bitmap moveBackground;
    //移动方块画笔
    private Paint movePaint;

    public int width;
    public int height;

    public int NullSize;

    public int Move_X;
    public int Move_Y;

    public int Move=200;

    private int err=10;

    public OnResult onResult;

    public void setOnResult(OnResult onResult) {
        this.onResult = onResult;
    }

    public DilogView(Context context) {
        super(context);
        init();
    }

    public DilogView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DilogView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        nullPaint = new Paint();
        movePaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackgroup(canvas);
        drawNullgroup(canvas);
        drawModegroup(canvas);
    }

    private void drawModegroup(Canvas canvas) {
        moveBackground = Bitmap.createBitmap(backgroup,Move_X,Move_Y,NullSize,NullSize);
        canvas.drawBitmap(moveBackground,Move,Move_Y,movePaint);
    }

    private void drawNullgroup(Canvas canvas) {
        nullBackgroup = BitmapFactory.decodeResource(getResources(),R.drawable.img_null_card);
        NullSize = nullBackgroup.getWidth();
        Move_X = width/3*2;
        Move_Y = height/2-(NullSize/2);
        canvas.drawBitmap(nullBackgroup,Move_X,Move_Y,nullPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    private void drawBackgroup(Canvas canvas) {
        //获取图片
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_bg);
        //创建一个空BitMap
        backgroup = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        //绘制图片到空的Bitmap
        Canvas bgCanvas = new Canvas(backgroup);
        bgCanvas.drawBitmap(bitmap,null,new Rect(0,0,width,height),mPaint);
        //绘制到Viwe上
        canvas.drawBitmap(backgroup,null,new Rect(0,0,width,height),mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //判断点击地点是否是方块内容
                break;
            case MotionEvent.ACTION_MOVE:
                    if (event.getX() > 0 && event.getX() < (width-NullSize)) {
                        Move= (int) event.getX();
                        invalidate();
                    }
                break;
            case MotionEvent.ACTION_UP:
                if (Move>(Move_X-err)&&Move<(Move_X+err)){
                    if (onResult!=null){
                        onResult.onResult();
                    }
                }
                break;
        }
        return true;
    }
    public interface OnResult{
        void onResult();
    }
}
