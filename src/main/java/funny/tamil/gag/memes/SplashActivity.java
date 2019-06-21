package funny.tamil.gag.memes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SubscriptionPlan;
import android.view.MotionEvent;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View easySplashScreenView = new EasySplashScreen(SplashActivity.this)
                .withFullScreen()

                .withSplashTimeOut(1500)
                .withBackgroundResource(android.R.color.black)
                .withTargetActivity(MainActivity.class)
                .withLogo(R.drawable.thasmall)
                .create();

        setContentView(easySplashScreenView);



    }


}
