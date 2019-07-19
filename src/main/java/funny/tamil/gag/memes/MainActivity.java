package funny.tamil.gag.memes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ApplicationErrorReport;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import funny.tamil.gag.memes.adapter.ViewPagerAdapter;
import funny.tamil.gag.memes.fragment.FragmentTrending;
import funny.tamil.gag.memes.fragment.FragmentFresh;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static MainActivity instance;
    boolean doubleBackToExitPressedOnce = false;



    public FragmentRefreshListener_1 getFragmentRefreshListener_1() {
        return fragmentRefreshListener_1;
    }

    public void setFragmentRefreshListener_1(FragmentRefreshListener_1 fragmentRefreshListener_1) {
        this.fragmentRefreshListener_1 = fragmentRefreshListener_1;
    }

    private FragmentRefreshListener_1 fragmentRefreshListener_1;


    public FragmentRefreshListener_2 getFragmentRefreshListener_2() {
        return fragmentRefreshListener_2;
    }

    public void setFragmentRefreshListener_2(FragmentRefreshListener_2 fragmentRefreshListener_2) {
        this.fragmentRefreshListener_2 = fragmentRefreshListener_2;
    }

    private FragmentRefreshListener_2 fragmentRefreshListener_2;

    private FirebaseAnalytics mFirebaseAnalytics;

    String category="Home";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Home");


        MobileAds.initialize(this, getString(R.string.admob_app_id));
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        FirebaseMessaging.getInstance().subscribeToTopic("all");

//---------------------
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                //Intent intent = new Intent(MainActivity.this, UploadTestActivity.class);
                startActivity(intent);



            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setItemIconTintList(null);
        instance=this;

        setupFragment("Home");

        navigationView.invalidate();

        setNavigationStyle(navigationView);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    104);
        }

          }



    public void setNavigationStyle(NavigationView navigationView) {

        Menu menu = navigationView.getMenu();
        MenuItem tools = menu.findItem(R.id.item_tamil_memes);
        SpannableString s = new SpannableString(tools.getTitle());
        s.setSpan(new TextAppearanceSpan(MainActivity.this, R.style.navigation_title_style_1), 0, s.length(), 0);
        tools.setTitle(s);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);

         tools = menu.findItem(R.id.item_Other_memes);
         s = new SpannableString(tools.getTitle());
        s.setSpan(new TextAppearanceSpan(MainActivity.this, R.style.navigation_title_style_2), 0, s.length(), 0);
        tools.setTitle(s);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);

        tools = menu.findItem(R.id.item_templates);
        s = new SpannableString(tools.getTitle());
        s.setSpan(new TextAppearanceSpan(MainActivity.this, R.style.navigation_title_style_3), 0, s.length(), 0);
        tools.setTitle(s);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);


    }

    public static MainActivity getInstance() {
        return instance;
    }



    private void setupFragment(String category) {
        ViewPager   viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout allTabs = (TabLayout) findViewById(R.id.tabs);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putString("category", category);

        Log.v("tag3",bundle.getString("category"));
        FragmentTrending fragmentOne = new FragmentTrending();
        fragmentOne.setArguments(bundle);

        FragmentFresh fragmentTwo = new FragmentFresh();
        fragmentTwo.setArguments(bundle);


        adapter.addFragment(fragmentTwo, "Fresh");
        adapter.addFragment(fragmentOne, "Trending");

        viewPager.setAdapter(adapter);
        allTabs.setupWithViewPager(viewPager);
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("rate_pref", 0);
            Log.i("Rated - ",pref.getBoolean("rated",false)+"");


            if (!pref.getBoolean("rated", false))
            {
                rating_alert("exit");
            }
            else {

                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_feedback) {
            sendFeedback("Through Feedback");
            return true;
        }
        if (id == R.id.action_rate) {
            rating_alert("Through Rating");
            return true;
        }
        if (id == R.id.action_share_app) {

            share();
            return true;
        }
        if (id == R.id.action_refresh) {

            if(getFragmentRefreshListener_1()!=null){
                getFragmentRefreshListener_1().onRefresh_fone(category);

            }
            if(getFragmentRefreshListener_2()!=null){
                getFragmentRefreshListener_2().onRefresh_ftwo(category);

            }

            setTitle(category);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        
         category = "Home";
        if (id == R.id.nav_home)
             category = "Home";
        else if (id == R.id.nav_favorites)
            category = "Favorites";
        else if (id == R.id.nav_trending)
            category = "Trending";
        else if (id == R.id.nav_tamil_funny)
            category = "Funny / நகைச்சுவை";
        else if (id == R.id.nav_relationship)
            category = "Relationship / காதல்";
        else if (id == R.id.nav_college)
            category = "College";
        else if (id == R.id.nav_sad)
            category = "Sad / சோகம்";
        else if (id == R.id.nav_politics)
          category = "Politics / அரசியல்";
        else if (id == R.id.nav_corporate)
            category = "Corporate / கார்பொரேட்";
        else if (id == R.id.nav_cinema)
            category = "Cinema / சினிமா";
        else if (id == R.id.nav_sports)
            category = "Sports / விளையாட்டு";
        else if (id == R.id.nav_actors)
            category = "Actors / நடிகர்கள்";
        else if (id == R.id.nav_actress)
            category = "Actress / நடிகை";
        else if (id == R.id.nav_pubg)
            category = "Games";
        else if (id == R.id.nav_mokkai)
            category = "Mokkai / மொக்கை";
        else if (id == R.id.nav_other_tamil)
            category = "Others / மற்றவை";
//------------------------------------------------------
        else if (id == R.id.nav_funny)
            category = "Funny";
        else if (id == R.id.nav_hollywood)
            category = "Hollywood / ஹாலிவுட்";
        else if (id == R.id.nav_savage)
            category = "Savage";
        else if (id == R.id.nav_dark_humor)
            category = "Dark Humor";
        else if (id == R.id.nav_wtf)
            category = "WTF";
        else if (id == R.id.nav_gif)
            category = "GIF / Video";
        else if (id == R.id.nav_other)
            category = "Others";
//---------------------------------------------------------------
        else if (id == R.id.nav_trending_templates)
            category = "Trending Templates";
        else if (id == R.id.nav_vadivelu)
            category = "Vadivelu / வடிவேலு";
        else if (id == R.id.nav_santhanam)
            category = "Santhanam / சந்தானம்";
        else if (id == R.id.nav_goundamani_senthil)
            category = "Goundamani-Senthil/கவுண்டமணி";
        else if (id == R.id.nav_vivek)
            category = "Vivek / விவேக்";
        else if (id == R.id.nav_actors_templates)
            category = "Actors - Templates";
        else if (id == R.id.nav_actress_templates)
            category = "Actress - Templates";
        else if (id == R.id.nav_movies_templates)
            category = "Movie - Templates";
        else if (id == R.id.nav_sports_templates)
            category = "Sports / விளையாட்டு - T";
        else if (id == R.id.nav_politics_templates)
            category = "Politics / அரசியல் - T";
        else if (id == R.id.nav_youtube)
            category = "YouTube Templates";
        else if (id == R.id.nav_other_templates)
            category = "Other Templates";



        else if (id == R.id.nav_share_app)
            share();
        else if (id == R.id.nav_exit)
            exit();



        else
            category = "Home";




        Bundle params = new Bundle();
        params.putString("activity", "MainActivity-Drawer");
        params.putString("category_clicked", category);
        mFirebaseAnalytics.logEvent("category_"+category, params);

        if(getFragmentRefreshListener_1()!=null){
            getFragmentRefreshListener_1().onRefresh_fone(category);

        }
        if(getFragmentRefreshListener_2()!=null){
            getFragmentRefreshListener_2().onRefresh_ftwo(category);

        }

        setTitle(category);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public interface FragmentRefreshListener_1{
        void onRefresh_fone(String category);

    }
    public interface FragmentRefreshListener_2{
        void onRefresh_ftwo(String category);

    }


    private void exit() {



        finish();
    }

    private void share() {

        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tamil GAG Memes");
            String shareMessage= "Have a Look at this App, Its Funny & Interesting\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Share App"));
        } catch(Exception e) {
            //e.toString();
        }

    }


    public void rating_alert(final String feedback_text){




        // TODO Auto-generated method stub
        final AlertDialog alertadd = new AlertDialog.Builder(
                MainActivity.this).create();

        alertadd.setTitle("Like the App?");

        if(feedback_text.contains("exit"))
            alertadd.setButton(DialogInterface.BUTTON_NEGATIVE, "Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertadd.dismiss();
                    finish();
                }
            });
        else
            alertadd.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertadd.dismiss();
                }
            });






        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
    final View view = factory.inflate(R.layout.dialog_rate, null);

    RatingBar rb =  view.findViewById(R.id.ratingBar);


    rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {


            SharedPreferences pref = getApplicationContext().getSharedPreferences("rate_pref", 0);

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("rated", true);
            editor.apply();
            Log.i("Rated1 - ",pref.getBoolean("rated",false)+"");


            Bundle params = new Bundle();
            params.putString("activity", "MainActivity-rate-"+feedback_text);
            mFirebaseAnalytics.logEvent("rate_"+ Math.round(rating), params);


            Log.i("rating", rating + "-" + getPackageName());
            if (rating >= 3.5)
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
            else
                sendFeedback(feedback_text);

            alertadd.dismiss();
        }
    });


    alertadd.setView(view);


    alertadd.show();
}

    protected void sendFeedback(String feedback_text) {
        try {
            int i = 3 / 0;
        } catch (Exception e) {
            ApplicationErrorReport report = new ApplicationErrorReport();
            report.packageName = report.processName = getApplication()
                    .getPackageName();
            report.time = System.currentTimeMillis();
            report.type = ApplicationErrorReport.TYPE_CRASH;
            report.systemApp = false;

            ApplicationErrorReport.CrashInfo crash = new ApplicationErrorReport.CrashInfo();
            crash.exceptionClassName = e.getClass().getSimpleName();
            crash.exceptionMessage = e.getMessage();

            StringWriter writer = new StringWriter();
            PrintWriter printer = new PrintWriter(writer);
            e.printStackTrace(printer);

            crash.stackTrace = writer.toString();

            StackTraceElement stack = e.getStackTrace()[0];
            crash.throwClassName = feedback_text;
            crash.throwFileName = stack.getFileName();
            crash.throwLineNumber = stack.getLineNumber();
            crash.throwMethodName = stack.getMethodName();

            report.crashInfo = crash;

            Intent intent = new Intent(Intent.ACTION_APP_ERROR);
            intent.putExtra(Intent.EXTRA_BUG_REPORT, report);
            startActivity(intent);
        }   }


        public void  open_drawer(String cat){
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
            drawerLayout.openDrawer(Gravity.LEFT,true);
        }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 104: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    File file = new File(Environment.getExternalStorageDirectory() + "/Tamil GAG/");
                    file.mkdirs();
                }
                return;
            }
        }
    }

}
