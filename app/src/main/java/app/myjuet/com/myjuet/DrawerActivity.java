package app.myjuet.com.myjuet;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import static android.R.id.toggle;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public FloatingActionButton fab;
    public AppBarLayout appBarLayout;
    NavigationView navigationView;
    int activeFragment;
    Toolbar tool;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        fab = (FloatingActionButton) findViewById(R.id.drawer_fab);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        getSupportActionBar().setElevation(2);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, tool, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (activeFragment == 4)
            WebviewFragment.goBackWebview();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START, true);
        } else
            drawer.openDrawer(GravityCompat.START);
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transition = getFragmentManager().beginTransaction();
        if (id == R.id.attendence_drawer) {
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            collapsingToolbarLayout.setContentScrimColor(Color.RED);
            collapsingToolbarLayout.setTitle("Attendence");
            android.app.Fragment fragment = new AttendenceActivity();
            transition.replace(R.id.content_drawer, fragment);
            transition.commit();
            // setSupportActionBar(tool);
            activeFragment = 0;


        } else if (id == R.id.timetable_drawer) {
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            collapsingToolbarLayout.setContentScrimColor(Color.MAGENTA);
            collapsingToolbarLayout.setTitle("TimeTable");
            getSupportActionBar().setTitle("TimeTable");
            activeFragment = 1;

        } else if (id == R.id.Events_drawer) {
            getSupportActionBar().setTitle("Events/Alerts");
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            collapsingToolbarLayout.setContentScrimColor(Color.MAGENTA);
            collapsingToolbarLayout.setTitle("Attendence");
            activeFragment = 2;


        } else if (id == R.id.annapurna_drawer) {
            activeFragment = 3;
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            collapsingToolbarLayout.setContentScrimColor(Color.BLACK);
            collapsingToolbarLayout.setTitle("Annapurna Mess");
            getSupportActionBar().setTitle("Annapurna Menu");
            appBarLayout.setExpanded(false);
            Fragment fragment = new MessFragment();
            transition.replace(R.id.content_drawer, fragment);
            transition.commit();


        } else if (id == R.id.web_view_drawer) {
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            collapsingToolbarLayout.setContentScrimColor(Color.DKGRAY);
            collapsingToolbarLayout.setTitle("WebView");           // setSupportActionBar(tool);
            appBarLayout.setExpanded(false);
            Fragment fragment = new WebviewFragment();
            transition.replace(R.id.content_drawer, fragment);
            transition.commit();

            activeFragment = 4;
            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        } else if (id == R.id.contact_drawer) {

        } else if (id == R.id.feedback_drawer) {

        } else if (id == R.id.exit) {
            finish();
            System.exit(0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
