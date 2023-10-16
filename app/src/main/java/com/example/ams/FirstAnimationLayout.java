package com.example.ams;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class FirstAnimationLayout extends AppCompatActivity {
    TextView tv1;
    TextView tv2;

    Button bt1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_animation_layout);

        if(getSupportActionBar()!=null){


            getSupportActionBar().hide();

        }

        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
       // bt1 = findViewById(R.id.get_started);


        Animation anim = AnimationUtils.loadAnimation(this,R.anim.move_a);

        tv1.startAnimation(anim);

        Animation anim1 = AnimationUtils.loadAnimation(this,R.anim.move_s);

        tv2.startAnimation(anim1);

        anim1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                Intent intent = new Intent(FirstAnimationLayout.this,First_Page.class);
                startActivity(intent);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

      //  Animation anim3 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.getstarted_button);
        //  bt1.setAlpha(0f);

      //  bt1.setAnimation(anim3);
    }
}