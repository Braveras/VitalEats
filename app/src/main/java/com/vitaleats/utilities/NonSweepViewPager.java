package com.vitaleats.utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class NonSweepViewPager extends ViewPager {
    public NonSweepViewPager(@NonNull Context context) {
        super(context);
    }

    public NonSweepViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    // Overrideamos los eventos de sweep y toques en la pantalla para que el usuario
    // solo pueda pasar de fragmentos con los botones, obligando a pasar por los checks
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
