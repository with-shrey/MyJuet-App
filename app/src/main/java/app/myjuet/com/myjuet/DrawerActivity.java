package app.myjuet.com.myjuet;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import app.myjuet.com.myjuet.timetable.TimeTableFragment;
import app.myjuet.com.myjuet.utilities.SettingsActivity;

import static android.R.attr.id;
import static app.myjuet.com.myjuet.WebviewFragment.myWebView;


public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public FloatingActionButton fab;
    public TabLayout tabLayout;
    public NavigationView navigationView;
    int activeFragment;
    InterstitialAd mInterstitialAd;
    boolean doubleBackToExitPressedOnce = false;
    private AppBarLayout appBarLayout;

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
        FirebaseMessaging.getInstance().subscribeToTopic("juet");
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        AdView mAdView;
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-5004802474664731~4072895207");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5004802474664731/9840227206");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }

            }
        });

        requestNewInterstitial();
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        String user = prefs.getString(getString(R.string.key_enrollment), "");
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView name = (TextView) headerView.findViewById(R.id.header_name);
        LinearLayout layoutheader = (LinearLayout) headerView.findViewById(R.id.header_main);
        layoutheader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(DrawerActivity.this, SettingsActivity.class);
                startActivity(login);
            }
        });
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        fab = (FloatingActionButton) findViewById(R.id.drawer_fab);
        tabLayout = (TabLayout) findViewById(R.id.tablayout_tt);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        if (getSupportActionBar() != null)
            getSupportActionBar().setElevation(5);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, tool, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        int i = getIntent().getIntExtra("fragment", 0);
        navigationView.getMenu().getItem(i).setChecked(true);
        if (i == 0) {
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            tabLayout.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
            ImageView appbarimage = (ImageView) findViewById(R.id.image_appbar);
            Fragment fragment = new AttendenceFragment();
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.content_drawer, fragment).commitNow();
            appbarimage.setImageDrawable(ContextCompat.getDrawable(DrawerActivity.this, R.drawable.attendence));
            collapsingToolbarLayout.setTitle("ATTENDENCE");
            collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(DrawerActivity.this, R.color.Attendence));
            appBarLayout.setExpanded(true);
            activeFragment = 0;


        } else
        onNavigationItemSelected(navigationView.getMenu().getItem(i));
        name.setText(user);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


    }

    @Override
    public void onBackPressed() {
        requestNewInterstitial();
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START, true);
        } else if (activeFragment == 3) {
            if (myWebView.canGoBack()) {
                myWebView.loadUrl(WebviewFragment.prev);
//                myWebView.goBack();
            }
            } else {
                if (doubleBackToExitPressedOnce) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please Click Back Again To Quit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        requestNewInterstitial();
        // Handle navigation view item clicks here.
        final int id = item.getItemId();
        if (id == R.id.exit) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
            finish();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction transition = getSupportFragmentManager().beginTransaction();
                // transition.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transition.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                if (id == R.id.attendence_drawer) {
                    CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
                    tabLayout.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);

                    Fragment fragment = new AttendenceFragment();
                    transition.replace(R.id.content_drawer, fragment).commitNow();
                    collapsingToolbarLayout.setTitle("ATTENDENCE");
                    collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(DrawerActivity.this, R.color.Attendence));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            appBarLayout.setExpanded(true);
                        }
                    }, 1000);
            activeFragment = 0;


        } else if (id == R.id.timetable_drawer) {
                    tabLayout.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);

                    activeFragment = 1;
                    Fragment fragment = new TimeTableFragment();
                    transition.replace(R.id.content_drawer, fragment).commitNow();
            fab.setImageResource(R.drawable.ic_settings);
                    fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(DrawerActivity.this, R.color.colorAccent)));
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
                    collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(DrawerActivity.this, R.color.Attendence));
            if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("");
            collapsingToolbarLayout.setTitle("");

                    activeFragment = 1;


        } else if (id == R.id.annapurna_drawer) {

                    fab.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            activeFragment = 2;
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            collapsingToolbarLayout.setContentScrimColor(Color.BLACK);
            collapsingToolbarLayout.setTitle("ANNAPURNA");
            appBarLayout.setExpanded(false);
            Fragment fragment = new MessFragment();
                    transition.replace(R.id.content_drawer, fragment).commitNow();


        } else if (id == R.id.web_view_drawer) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            }
                        }
                    }, 2500);
                    fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(DrawerActivity.this, R.color.magnitude40)));
            fab.setImageResource(R.drawable.ic_sync_problem_black_24dp);
            fab.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);

            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            collapsingToolbarLayout.setContentScrimColor(Color.DKGRAY);
            collapsingToolbarLayout.setTitle("WEBVIEW");           // setSupportActionBar(tool);
            appBarLayout.setExpanded(false);
            Fragment fragment = new WebviewFragment();
                    transition.replace(R.id.content_drawer, fragment).commitNow();


            activeFragment = 3;
            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        } else if (id == R.id.findyourway) {
            activeFragment = 4;

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

                    tabLayout.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
                    collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(DrawerActivity.this, R.color.Attendence));
            collapsingToolbarLayout.setTitle("Contact Us");           // setSupportActionBar(tool);
            appBarLayout.setExpanded(false);
            Fragment fragment = new ContactFragment();
                    transition.replace(R.id.content_drawer, fragment).commitNow();

            activeFragment = 5;


        } else if (id == R.id.request_notification) {

                    tabLayout.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
                    collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(DrawerActivity.this, R.color.Attendence));
            collapsingToolbarLayout.setTitle("Request Notification");           // setSupportActionBar(tool);
            appBarLayout.setExpanded(false);
            Fragment fragment = new NotificationApplicationFragment();
                    transition.replace(R.id.content_drawer, fragment).commitNow();

            activeFragment = 6;
        } else if (id == R.id.feedback_drawer) {

                    tabLayout.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
                    collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(DrawerActivity.this, R.color.Attendence));
            collapsingToolbarLayout.setTitle("FeedBack");           // setSupportActionBar(tool);
            appBarLayout.setExpanded(false);
            Fragment fragment = new ReportFragment();
                    transition.replace(R.id.content_drawer, fragment).commitNow();

            activeFragment = 7;


        } else if (id == R.id.settings_master) {

                    Intent intent = new Intent(DrawerActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.cgpa_drawer) {

                    fab.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    activeFragment = 8;
                    CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
                    collapsingToolbarLayout.setContentScrimColor(Color.parseColor("#78909c"));
                    collapsingToolbarLayout.setTitle("CGPA/SGPA");
                    appBarLayout.setExpanded(false);
                    Fragment fragment = new SgpaCgpa();
                    transition.replace(R.id.content_drawer, fragment).commitNow();
                }

            }
        }, 300);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }


}
