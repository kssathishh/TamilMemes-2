package ks.tamil.gag.memes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ApplicationErrorReport;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;

import ks.tamil.gag.memes.adapter.ViewPagerAdapter;
import ks.tamil.gag.memes.fragment.FragmentOne;
import ks.tamil.gag.memes.fragment.FragmentTwo;

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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Home");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
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

        setupFragment("All");

        navigationView.invalidate();

        setNavigationStyle(navigationView);




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
        FragmentOne fragmentOne = new FragmentOne();
        fragmentOne.setArguments(bundle);

        FragmentTwo  fragmentTwo = new FragmentTwo();
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
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
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
            rating_alert("Through Low Rating");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        
        String category = "All";
        if (id == R.id.nav_home)
             category = "All";
        else if (id == R.id.nav_tamil_funny)
           category = "Funny / நகைச்சுவை";
        else if (id == R.id.nav_relationship)
            category = "Relationship / காதல்";
        else if (id == R.id.nav_sad)
            category = "Sad / சோகம்";
        else if (id == R.id.nav_politics)
          category = "Politics / அரசியல்";
        else if (id == R.id.nav_cinema)
            category = "Cinema / சினிமா";
        else if (id == R.id.nav_sports)
            category = "Sports / விளையாட்டு";
        else if (id == R.id.nav_actors)
            category = "Actors / நடிகர்கள்";
        else if (id == R.id.nav_actress)
            category = "Actress / நடிகை";
        else if (id == R.id.nav_pubg)
            category = "PUBG";
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
            category = "GIF";
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
            category = "All";






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


}
