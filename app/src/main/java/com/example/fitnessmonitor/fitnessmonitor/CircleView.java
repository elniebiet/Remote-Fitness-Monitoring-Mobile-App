package com.example.fitnessmonitor.fitnessmonitor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Aniebiet Akpan on 22/06/20.
 */

public class CircleView extends View
{
    private static final int MARGIN = 50;

    Handler handler = new Handler();
    Paint paint = new Paint();
    RectF rect = new RectF();

    boolean drawing = false;
    float sweep = 0;

    public CircleView(Context context)
    {
        this(context, null);
    }

    public CircleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15);
        paint.setColor(Color.BLUE);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawArc(rect, 0, sweep, false, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        rect.set(MARGIN, MARGIN, w - MARGIN, h - MARGIN);
    }

    public void startAnimation()
    {
        drawing = true;
        handler.post(runnable);
    }

    Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            sweep += 10;
            if (!(sweep > 360))
            {
                invalidate();
                handler.postDelayed(this, 20);
            }
            else
            {
                drawing = false;
                sweep = 0;
            }
        }
    };
}