package com.xmevs.dateprocess;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by MSI on 2016/9/22.
 */

public class RotatingRect extends View {

    private Paint paint;
    private float width;
    private float nowValue;
    private float mValue;
    private long mThroughNumber;

    private final  float scale = getContext().getResources().getDisplayMetrics().density;

    public void setValue(float nowValue, float mValue, long mThroughNumber){
        this.nowValue = nowValue;//不随进度条变化
        this.mValue = mValue;
        this.mThroughNumber = mThroughNumber;
        invalidate();
    }

    public void setValue(float mValue, long mThroughNumber){
        this.mValue = mValue;
        this.mThroughNumber = mThroughNumber;
        invalidate();
    }

    public RotatingRect(Context context) {
        super(context);
        initProperties();
    }

    public RotatingRect(Context context, AttributeSet attrs) {
        super(context, attrs);
        initProperties();
    }

    public RotatingRect(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initProperties();
    }

    private void initProperties() {
        paint = new Paint();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
//        canvas.save();
        float process = (float) (mValue * 1.8);// 相当于mValue / 100 * 180

        width = canvas.getWidth();

        float width41 = width/12;
        float width43 = width - width / 12;

        String color = setColor().toString();
        paint.setColor(Color.parseColor(color.trim()));
        RectF rect = new RectF(width41, width41, width43, width43);
        canvas.drawArc(rect, 180f, process, true, paint);

        paint.setColor(Color.rgb(217, 217, 255));
        rect = new RectF(width41, width41,width43, width43);
        canvas.drawArc(rect, process+180f, 180f-process, true, paint);

        float startwith = width/5;
        float endwith = width - startwith;
        paint.setColor(Color.rgb(250, 250, 250));
        rect = new RectF(startwith, startwith, endwith, endwith);
        canvas.drawArc(rect, 180f,  180f, true, paint);

        paint.setColor(Color.rgb(78, 80, 79));
        paint.setTextSize(48 * scale *0.5f);
        paint.setTextAlign(Paint.Align.CENTER);
        String testString = (String.format("%.2f", nowValue))+"%";
        canvas.drawText(testString, rect.centerX(), rect.centerY()/5*4, paint);

        long throughWeek = mThroughNumber/7;
        long throughDay = mThroughNumber%7;
        paint.setTextSize(24 * scale *0.5f);
        paint.setColor(Color.rgb(115, 115, 115));
        if(throughDay==0) {
            testString = mThroughNumber + "天(" + throughWeek + "周整)";
        } else if(throughWeek<1) {
            testString = mThroughNumber + "天(" + throughDay + "天)";
        } else {
            testString = mThroughNumber + "天(" + throughWeek + "周又" +throughDay + "天)";
        }
        canvas.drawText(testString, rect.centerX(), rect.centerY(), paint);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

//        canvas.restore();
//        invalidate();//使view无效，就被被重新调用draw()
    }

    private String setColor() {
        if(mValue>90) return "#C57069";
        if(mValue>80) return "#CC8364";
        if(mValue>70) return "#D3965F";
        if(mValue>60) return "#D9A85A";
        if(mValue>50) return "#E0BB55";
        if(mValue>40) return "#E7CE50";
        if(mValue>30) return "#B1CD3E";
        if(mValue>20) return "#7BCC2C";
        if(mValue>10) return "#45CB19";
        else return "#0FCA07";
    }
}

