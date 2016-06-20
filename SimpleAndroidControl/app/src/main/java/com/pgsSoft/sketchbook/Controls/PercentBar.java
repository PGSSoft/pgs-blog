package com.pgsSoft.sketchbook.Controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.pgsSoft.sketchbook.R;

/**
 * Created by Wojciech on 2016-06-20.
 */
public class PercentBar extends View {

    private final int DEFAULT_WIDTH = 400;
    private final int DEFAULT_HEIGHT = 100;

    private float percentage;
    private Drawable barDrawable;

    public PercentBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        percentage = 50.0f;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PercentBar, 0, 0);
        try {
            setBarDrawable(a.getDrawable(R.styleable.PercentBar_barDrawable));
        } finally {

            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = 0, height = 0;

        switch (View.MeasureSpec.getMode(widthMeasureSpec)) {

            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:

                width = View.MeasureSpec.getSize(widthMeasureSpec);

                break;
            case MeasureSpec.UNSPECIFIED:

                width = DEFAULT_WIDTH;

                break;
        }

        switch (View.MeasureSpec.getMode(heightMeasureSpec)) {

            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:

                height = View.MeasureSpec.getSize(heightMeasureSpec);

                break;
            case MeasureSpec.UNSPECIFIED:

                height = DEFAULT_HEIGHT;

                break;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if (barDrawable != null) {
            barDrawable.setBounds(0, 0, (int) (canvas.getWidth() * (percentage / 100.0f)), canvas.getHeight());
            barDrawable.draw(canvas);
        } else {

            Paint p = new Paint();
            p.setColor(Color.rgb(255, 0, 0));
            p.setStyle(Paint.Style.FILL);
            canvas.drawRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), p);
        }
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
        invalidate();
    }

    public Drawable getBarDrawable() {
        return barDrawable;
    }

    public void setBarDrawable(Drawable barDrawable) {
        this.barDrawable = barDrawable;
        invalidate();
    }
}
