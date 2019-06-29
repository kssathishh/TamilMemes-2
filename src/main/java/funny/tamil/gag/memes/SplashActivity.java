package funny.tamil.gag.memes;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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
