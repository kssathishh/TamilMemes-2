package ks.tamil.gag.memes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import ks.tamil.gag.memes.adapter.ViewPagerAdapter;
import ks.tamil.gag.memes.fragment.FragmentOne;
import ks.tamil.gag.memes.fragment.FragmentTwo;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static MainActivity instance;

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

        adapter.addFragment(fragmentOne, "Trending");
        adapter.addFragment(fragmentTwo, "Fresh");

        viewPager.setAdapter(adapter);
        allTabs.setupWithViewPager(viewPager);
    }













    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
        else if (id == R.id.nav_sports_templates)
            category = "Sports / விளையாட்டு - T";
        else if (id == R.id.nav_politics_templates)
            category = "Politics / அரசியல் - T";
        else if (id == R.id.nav_youtube)
            category = "YouTube Templates";
        else if (id == R.id.nav_other)
            category = "Others";
        else if (id == R.id.nav_other_templates)
            category = "Other Templates";
        else
            category = "All";






        if(getFragmentRefreshListener_1()!=null){
            getFragmentRefreshListener_1().onRefresh_fone(category);

        }
        if(getFragmentRefreshListener_2()!=null){
            getFragmentRefreshListener_2().onRefresh_ftwo(category);

        }


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
}
