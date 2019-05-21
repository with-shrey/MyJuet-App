package app.myjuet.com.myjuet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.utilities.SettingsActivity;
import app.myjuet.com.myjuet.utilities.SharedPreferencesUtil;
import app.myjuet.com.myjuet.vm.DrawerViewModel;

import static app.myjuet.com.myjuet.WebviewFragment.myWebView;


public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public FloatingActionButton fab;
    public TabLayout tabLayout;
    DrawerViewModel viewModel;
    public NavigationView navigationView;
    int activeFragment;
    InterstitialAd mInterstitialAd;
    boolean doubleBackToExitPressedOnce = false;
    int in = 0;
    private AppBarLayout appBarLayout;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void requestNewInterstitial() {

        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(SharedPreferencesUtil.getPreferences(this,"dark",false)) {
            setTheme(R.style.DarkTheme);
        }
//        Activity binding = DataBindingUtil.
                setContentView(R.layout.activity_drawer);
        if(SharedPreferencesUtil.getPreferences(this,"dark",false)) {
            findViewById(R.id.overlay).setVisibility(View.VISIBLE);
        }
        viewModel = ViewModelProviders.of(this).get(DrawerViewModel.class);
        viewModel.getFabVisible().observe(this,(value) -> {
            if (value){
                fab.show();
            }else{
                fab.setVisibility(View.GONE);
            }
        });
        if (SharedPreferencesUtil.getPreferences(this
                ,"firsttime_"+BuildConfig.VERSION_CODE,true)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New In This Release");
            builder.setMessage(Html.fromHtml("1.Seating Plan\n2.Dark Theme (Settings)"));
            builder.setPositiveButton("Ok", (dialog, which) -> {
                SharedPreferencesUtil.savePreferences(this
                        ,"firsttime_"+BuildConfig.VERSION_CODE,false);
                dialog.dismiss();
            });
            builder.setCancelable(false);
            builder.show();

        }
        FirebaseMessaging.getInstance().subscribeToTopic("juet");
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        AdView mAdView;
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5004802474664731/9840227206");
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

                new Handler().postDelayed(() -> mInterstitialAd.show(), 5000);
                super.onAdLoaded();
            }
        });
        requestNewInterstitial();
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        String user = prefs.getString(getString(R.string.key_enrollment), "");
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView name = headerView.findViewById(R.id.header_name);
        LinearLayout layoutheader = headerView.findViewById(R.id.header_main);
        layoutheader.setOnClickListener(view -> {
            Intent login = new Intent(DrawerActivity.this, SettingsActivity.class);
            startActivity(login);
        });
        mAdView = findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());

        appBarLayout = findViewById(R.id.app_bar);
        fab = findViewById(R.id.drawer_fab);
        tabLayout = findViewById(R.id.tablayout_tt);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        Toolbar tool = findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        if (getSupportActionBar() != null)
            getSupportActionBar().setElevation(5);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, tool, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        in = getIntent().getIntExtra("fragment", 0);
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        int latest = sharedPref.getInt("latest_version_number", 0);
        String latestCode = sharedPref.getString("latest_version_code", "0.0");
        String url = sharedPref.getString("url", "http://myjuet.tk"), code = "0.0";

        int version = 0;
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionCode;
            code = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (version < latest && !getIntent().getBooleanExtra("containsurl", false)) {
            showUpdateAlert(code, latestCode, url);
        }
        if (in != 4)
            requestNewInterstitial();
        navigationView.getMenu().getItem(in).setChecked(true);
        if (in == 0) {
            CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
            tabLayout.setVisibility(View.GONE);
            viewModel.setFabVisible(true);
            color = "#4A7BA7";
            // Handle navigation view item clicks here.
            if(SharedPreferencesUtil.getPreferences(this,"dark",false)){
                color = "#000000";
            }
            ImageView appbarimage = findViewById(R.id.image_appbar);
            Fragment fragment = new AttendenceFragment();
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.content_drawer, fragment).commitNow();
            appbarimage.setImageDrawable(ContextCompat.getDrawable(DrawerActivity.this, R.drawable.attendence));
            collapsingToolbarLayout.setTitle("ATTENDENCE");
                            collapsingToolbarLayout.setContentScrimColor(Color.parseColor(color)
);

            appBarLayout.setExpanded(true);
            activeFragment = 0;


        } else
            onNavigationItemSelected(navigationView.getMenu().getItem(in));
        name.setText(user);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onBackPressed() {
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START, true);
        } else if (activeFragment == 3) {
            if (myWebView.canGoBack()) {
                myWebView.loadUrl(WebviewFragment.prev);
//                myWebView.goBack();
            }
            } else {
                if (doubleBackToExitPressedOnce) {
                    requestNewInterstitial();
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please Click Back Again To Quit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);

        }
    }
String color;
    @SuppressLint("RestrictedApi")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        color = "#4A7BA7";
        // Handle navigation view item clicks here.
        if(SharedPreferencesUtil.getPreferences(this,"dark",false)){
            color = "#000000";
        }
        final int id = item.getItemId();
        if (id == R.id.exit) {
            requestNewInterstitial();
            finish();
        }

        new Handler().postDelayed(() -> {
            FragmentTransaction transition = getSupportFragmentManager().beginTransaction();
            // transition.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transition.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
            if (id == R.id.attendence_drawer) {
                CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
                tabLayout.setVisibility(View.GONE);
                viewModel.setFabVisible(true);
                Fragment fragment = new AttendenceFragment();
                transition.replace(R.id.content_drawer, fragment).commitNow();
                collapsingToolbarLayout.setTitle("ATTENDENCE");
                collapsingToolbarLayout.setContentScrimColor(Color.parseColor(color)
);
                new Handler().postDelayed(() -> appBarLayout.setExpanded(true), 1000);
        activeFragment = 0;


    } else if (id == R.id.web_view_drawer) {
                new Handler().postDelayed(() -> {
                }, 2500);
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(DrawerActivity.this, R.color.magnitude40)));
        fab.setImageResource(R.drawable.ic_sync_problem_black_24dp);
                viewModel.setFabVisible(false);
        tabLayout.setVisibility(View.GONE);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
                collapsingToolbarLayout.setContentScrimColor(Color.parseColor(color)
);
        collapsingToolbarLayout.setTitle("WEBVIEW");           // setSupportActionBar(tool);
        appBarLayout.setExpanded(false);
        Fragment fragment = new WebviewFragment();
                transition.replace(R.id.content_drawer, fragment).commitNow();

        activeFragment = 3;
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    } else if (id == R.id.date_sheet) {
                viewModel.setFabVisible(false);
        tabLayout.setVisibility(View.GONE);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
                        collapsingToolbarLayout.setContentScrimColor(Color.parseColor(color)
);

        collapsingToolbarLayout.setTitle("Date Sheet");           // setSupportActionBar(tool);
        appBarLayout.setExpanded(false);
        Fragment fragment = new DateSheetFragment();
                transition.replace(R.id.content_drawer, fragment).commitNow();

        activeFragment = 10;
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    } else if (id == R.id.seating_plan) {
                viewModel.setFabVisible(false);
        tabLayout.setVisibility(View.GONE);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
                        collapsingToolbarLayout.setContentScrimColor(Color.parseColor(color)
);

        collapsingToolbarLayout.setTitle("Seating Plan");           // setSupportActionBar(tool);
        appBarLayout.setExpanded(false);
        Fragment fragment = new SeatingPlanFragment();
                transition.replace(R.id.content_drawer, fragment).commitNow();

        activeFragment = 15;
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }else if (id == R.id.exam_marks) {
                viewModel.setFabVisible(false);
        tabLayout.setVisibility(View.GONE);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
                        collapsingToolbarLayout.setContentScrimColor(Color.parseColor(color)
);

        collapsingToolbarLayout.setTitle("Exam Marks");           // setSupportActionBar(tool);
        appBarLayout.setExpanded(false);
        Fragment fragment = new ExamMarksFragment();
                transition.replace(R.id.content_drawer, fragment).commitNow();

        activeFragment = 11;
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
                viewModel.setFabVisible(false);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
                collapsingToolbarLayout.setContentScrimColor(Color.parseColor(color));
        collapsingToolbarLayout.setTitle("Contact Us");           // setSupportActionBar(tool);
        appBarLayout.setExpanded(false);
        Fragment fragment = new ContactFragment();
                transition.replace(R.id.content_drawer, fragment).commitNow();
        activeFragment = 5;


    }  else if (id == R.id.feedback_drawer) {

                tabLayout.setVisibility(View.GONE);
                viewModel.setFabVisible(false);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
                                collapsingToolbarLayout.setContentScrimColor(Color.parseColor(color)
);

        collapsingToolbarLayout.setTitle("FeedBack");           // setSupportActionBar(tool);
        appBarLayout.setExpanded(false);
        Fragment fragment = new ReportFragment();
                transition.replace(R.id.content_drawer, fragment).commitNow();

        activeFragment = 7;


    } else if (id == R.id.settings_master) {

                Intent intent = new Intent(DrawerActivity.this, SettingsActivity.class);
        startActivity(intent);
    } else if (id == R.id.cgpa_drawer) {

                viewModel.setFabVisible(false);
                tabLayout.setVisibility(View.GONE);
                activeFragment = 8;
                CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
                collapsingToolbarLayout.setContentScrimColor(Color.parseColor(color)
);
                collapsingToolbarLayout.setTitle("CGPA/SGPA");
                appBarLayout.setExpanded(false);
                Fragment fragment = new SgpaCgpa();
                transition.replace(R.id.content_drawer, fragment).commitNow();
            }else if(id == R.id.logout){
                SharedPreferences.Editor sharedPref = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE).edit();
                sharedPref.clear();
                sharedPref.apply();
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                finish();
            }else if(id == R.id.reset){
                AppDatabase.newInstance(DrawerActivity.this).clearAllTables();
            }


        }, 300);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if ((ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    void showUpdateAlert(String current, String code, String Url) {
        final String url = Url;
        final Context context = DrawerActivity.this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("UPDATE", (dialogInterface, i) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id="+context.getPackageName()));
            startActivity(intent);

        });
        builder.setNegativeButton("LATER", (dialogInterface, i) -> {

        });
        builder.setMessage("New App Version " + code + " is Available" + "\nCurrent App Version " + current);
        builder.show();
    }


}
