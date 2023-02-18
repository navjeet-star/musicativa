package com.developer.musicatiiva.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

public class AspectRatioImageView extends AppCompatImageView {
/*private Context context;
    public AspectRatioImageView(Context context) {
        super(context);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

     @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
         int height = width * getDrawable().getIntrinsicHeight() / this.getDrawable().getIntrinsicWidth();
        setMeasuredDimension(width, height);
    }*/
public AspectRatioImageView(Context context)
{
    super(context);
    this.setScaleType(ImageView.ScaleType.FIT_XY);
}

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setScaleType(ImageView.ScaleType.FIT_XY);
    }

     @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        Drawable d = getDrawable();

        if (d != null && d.getIntrinsicWidth() > 0)
        {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            if (width <= 0)
                width = getLayoutParams().width;

            int height = width * d.getIntrinsicHeight() / d.getIntrinsicWidth();
            setMeasuredDimension(width, height);
        }
        else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
