package com.vitaleats;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;

import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class SplashActivity extends AppCompatActivity {

    ImageView foodTray_lid, foodTray_base, app_name, fondo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        openApp();

        foodTray_lid = findViewById(R.id.foodtray_lid);
        foodTray_base = findViewById(R.id.foodtray_base);
        app_name = findViewById(R.id.app_name_splash);
        fondo = findViewById(R.id.fondo);

        Glide.with(this)
                .load(R.drawable.frutas)
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .centerCrop()
                .into(fondo);

        LinearLayout splashLayout = findViewById(R.id.splash_parent);

        app_name.setVisibility(View.INVISIBLE);

        Animation shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
        Animation openLidAnim = AnimationUtils.loadAnimation(this, R.anim.lid_reveal);
        Animation app_name_anim = AnimationUtils.loadAnimation(this, R.anim.fadein_sizeup);
        Animation shrink = AnimationUtils.loadAnimation(this, R.anim.shrink);
        Animation lid_close = AnimationUtils.loadAnimation(this, R.anim.lid_close);
        Animation fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        Animation fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        app_name_anim.setFillAfter(true);
        lid_close.setFillAfter(true);
        openLidAnim.setFillAfter(true);
        shrink.setFillAfter(true);
        fade_in.setFillAfter(true);
        fade_out.setFillAfter(true);

        foodTray_lid.startAnimation(shakeAnim);
        shakeAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                foodTray_lid.startAnimation(openLidAnim);
            }
        });

        openLidAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                app_name.setVisibility(View.VISIBLE);
                app_name.startAnimation(app_name_anim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                foodTray_base.startAnimation(shrink);
            }
        });


        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                foodTray_lid.startAnimation(lid_close);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                foodTray_lid.startAnimation(fade_out);
                foodTray_base.startAnimation(fade_out);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        fade_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                foodTray_lid.setVisibility(View.INVISIBLE);
                foodTray_base.setVisibility(View.INVISIBLE);
                foodTray_lid.setImageResource(R.drawable.foodtray_lid_cartoon);
                foodTray_base.setImageResource(R.drawable.foodtray_base_cartoon);
                foodTray_lid.setVisibility(View.VISIBLE);
                foodTray_base.setVisibility(View.VISIBLE);
                foodTray_lid.startAnimation(fade_in);
                foodTray_base.startAnimation(fade_in);
            }
        });

    }

    private void openApp() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, Fragment1.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 6500);


    }

    private TransitionDrawable applyFilter(@NonNull Bitmap resource, String colorHex) {

        // Create a new Bitmap object with the same dimensions as the original Bitmap
        Bitmap newBitmap = Bitmap.createBitmap(resource.getWidth(), resource.getHeight(), resource.getConfig());

        // Create a Canvas object and apply the color filter to the original Bitmap
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(Color.parseColor(colorHex), PorterDuff.Mode.MULTIPLY));
        canvas.drawBitmap(resource, 0, 0, paint);

        // Create a TransitionDrawable object using the original and modified Bitmaps
        Drawable[] layers = {new BitmapDrawable(getResources(), resource), new BitmapDrawable(getResources(), newBitmap)};
        TransitionDrawable transitionDrawable = new TransitionDrawable(layers);

        return transitionDrawable;

    }
}