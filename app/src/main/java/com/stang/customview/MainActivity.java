package com.stang.customview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by StanG on 17.10.2016.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    RectAnimationView myView1;
    RectAnimationView myView2;
    RectAnimationView myView3;
    RectAnimationView myView4;
    MyView myView5;


    TextView myTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: before");
        myTextView = (TextView) findViewById(R.id.tv);

        myView1 = (RectAnimationView) findViewById(R.id.v1);
        myView2 = (RectAnimationView) findViewById(R.id.v2);
        myView3 = (RectAnimationView) findViewById(R.id.v3);

        //myView1.setSpeed(100);

        myView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myView1.isRunning()){
                    myView1.stopAnim();
                } else {
                    myView1.startAnim();
                }
            }
        });

        myView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myView2.isRunning()){
                    myView2.stopAnim();
                } else {
                    myView2.startAnim();
                }
            }
        });

        myView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myView3.isRunning()){
                    myView3.stopAnim();
                } else {
                    myView3.startAnim();
                }
            }
        });



        myView3.setOnAnimationEventListener(new MyView.OnAnimationEventListener() {
            @Override
            public void onAnimationStarted() {
                myTextView.setText("Animation of myView3 STARTED");
            }

            @Override
            public void onAnimationStopped() {
                myTextView.setText("Animation of myView3 STOPPED");
            }

            @Override
            public void onAnimationCollapsed() {
                myTextView.setText("Animation of myView3 collapsed");
            }

            @Override
            public void onAnimationExploded() {
                myTextView.setText("Animation of myView3 exploded");
            }
        });
        Log.d(TAG, "onCreate: after");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }


}
