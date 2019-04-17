package cau.seoulargogaja;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class Intro extends AppCompatActivity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Handler h= new Handler();
        h.postDelayed(new Runnable(){
            public void run(){
                Intent i = new Intent (Intro.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        },1000);
    }
    protected void onPause(){
        super.onPause();
        finish();
    }
}