package app.myjuet.com.myjuet;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import app.myjuet.com.myjuet.timetable.TimeTableFragment;
import app.myjuet.com.myjuet.web.LoginWebkiosk;

import static android.widget.Toast.makeText;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public FloatingActionButton fab;
    public AppBarLayout appBarLayout;
    NavigationView navigationView;
    int activeFragment;
    Toolbar tool;
    InterstitialAd mInterstitialAd;
    private AdView mAdView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-5004802474664731~4072895207");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5004802474664731/9840227206");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String user = prefs.getString(getString(R.string.enrollment), getString(R.string.defaultuser));
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView name = (TextView) headerView.findViewById(R.id.header_name);
        LinearLayout layoutheader = (LinearLayout) headerView.findViewById(R.id.header_main);
        layoutheader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(DrawerActivity.this, LoginWebkiosk.class);
                startActivity(login);
            }
        });
        mAdView = (AdView) findViewById(R.id.adView);
        if (isConnected) {
        AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else {
            mAdView.setVisibility(View.GONE);
        }
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        fab = (FloatingActionButton) findViewById(R.id.drawer_fab);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        getSupportActionBar().setElevation(5);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, tool, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
        name.setText(user);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START, true);
        } else {
            Toast.makeText(this, "Press Exit To Quit", Toast.LENGTH_SHORT).show();
            drawer.openDrawer(GravityCompat.START);
        }
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transition = getFragmentManager().beginTransaction();
        if (id == R.id.attendence_drawer) {
            fab.setVisibility(View.VISIBLE);
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.Attendence));
            collapsingToolbarLayout.setTitle("Attendence");
            android.app.Fragment fragment = new AttendenceActivity();
            transition.replace(R.id.content_drawer, fragment);
            transition.commit();
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
            // setSupportActionBar(tool);
            activeFragment = 0;


        } else if (id == R.id.timetable_drawer) {
            fab.setVisibility(View.VISIBLE);
            fab.setImageResource(R.drawable.ic_settings);
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            collapsingToolbarLayout.setContentScrimColor(Color.MAGENTA);
            collapsingToolbarLayout.setTitle("TimeTable");
            getSupportActionBar().setTitle("TimeTable");
            appBarLayout.setExpanded(true);
            Fragment fragment = new TimeTableFragment();
            transition.replace(R.id.content_drawer, fragment);
            transition.commit();
            activeFragment = 1;
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }

        } else if (id == R.id.annapurna_drawer) {
            fab.setVisibility(View.GONE);

            activeFragment = 2;
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            collapsingToolbarLayout.setContentScrimColor(Color.BLACK);
            collapsingToolbarLayout.setTitle("Annapurna");
            appBarLayout.setExpanded(false);
            Fragment fragment = new MessFragment();
            transition.replace(R.id.content_drawer, fragment);
            transition.commit();
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }


        } else if (id == R.id.web_view_drawer) {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.magnitude40)));
            fab.setImageResource(R.drawable.ic_sync_problem_black_24dp);
            fab.setVisibility(View.GONE);

            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            collapsingToolbarLayout.setContentScrimColor(Color.DKGRAY);
            collapsingToolbarLayout.setTitle("WebView");           // setSupportActionBar(tool);
            appBarLayout.setExpanded(false);
            Fragment fragment = new WebviewFragment();
            transition.replace(R.id.content_drawer, fragment);
            transition.commit();
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }

            activeFragment = 3;
            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        } else if (id == R.id.findyourway) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.acc.juetlocate");
            if (launchIntent != null) {
                startActivity(launchIntent);//null pointer check in case package name was not found
            } else {
                Uri webpage = Uri.parse("https://play.google.com/store/apps/details?id=com.acc.juetlocate&hl=en");
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }

        } else if (id == R.id.contact_drawer) {
            fab.setVisibility(View.GONE);
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.Attendence));
            collapsingToolbarLayout.setTitle("Contact Us");           // setSupportActionBar(tool);
            appBarLayout.setExpanded(false);
            Fragment fragment = new ContactFragment();
            transition.replace(R.id.content_drawer, fragment);
            transition.commit();
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
            activeFragment = 4;

        } else if (id == R.id.request_notification) {
            fab.setVisibility(View.GONE);
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.Attendence));
            collapsingToolbarLayout.setTitle("Request Notification");           // setSupportActionBar(tool);
            appBarLayout.setExpanded(false);
            Fragment fragment = new NotificationRequestFragment();
            transition.replace(R.id.content_drawer, fragment);
            transition.commit();
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        } else if (id == R.id.feedback_drawer) {
            fab.setVisibility(View.GONE);
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.Attendence));
            collapsingToolbarLayout.setTitle("FeedBack");           // setSupportActionBar(tool);
            appBarLayout.setExpanded(false);
            Fragment fragment = new ReportFragment();
            transition.replace(R.id.content_drawer, fragment);
            transition.commit();
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
            activeFragment = 5;


        } else if (id == R.id.exit) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
            finish();
            System.exit(0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
