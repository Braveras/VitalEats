package com.vitaleats.utilities;

import androidx.annotation.Nullable;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.vitaleats.R;
import com.vitaleats.login.MainActivity;
import com.vitaleats.termsofservice.Fragment1;

public class SplashActivity extends AppCompatActivity {

    ImageView foodTray_lid, foodTray_base, app_name, fondo;
    boolean ToS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ToS = SharedPrefsUtil.getBoolean(this, "ToS");
        openApp(ToS);

        foodTray_lid = findViewById(R.id.foodtray_lid);
        foodTray_base = findViewById(R.id.foodtray_base);
        app_name = findViewById(R.id.app_name_splash);
        fondo = findViewById(R.id.fondo);

        Glide.with(this)
                .load(R.drawable.frutas)
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .centerCrop()
                .into(fondo);

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
                Glide.with(getApplicationContext()) // Load the drawable resource into a Bitmap object using Glide
                        .asBitmap()
                        .load(R.drawable.foodtray_lid)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                // I created a function to reuse the code that returns a TransitionDrawable
                                TransitionDrawable transitionFilter = applyFilter(resource, "#5EBC67");
                                // Set the TransitionDrawable on the ImageView
                                foodTray_lid.setImageDrawable(transitionFilter);
                                // Start the transition animation
                                transitionFilter.startTransition(500); // 500ms = 0.5s
                            }
                        });
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(R.drawable.foodtray_base)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                TransitionDrawable transitionFilter = applyFilter(resource, "#5EBC67");
                                foodTray_base.setImageDrawable(transitionFilter);
                                transitionFilter.startTransition(500);
                            }
                        });

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

    /*@Override
    public void onStart() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
        }
    }*/

    private void openApp(boolean b) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent intent;
                if (b) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, Fragment1.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Intent mainIntent = new Intent(SplashActivity.this, MainBn.class);
                    startActivity(mainIntent);
                    finish();
                }

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