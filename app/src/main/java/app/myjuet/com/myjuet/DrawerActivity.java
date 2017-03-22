package app.myjuet.com.myjuet;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import static android.R.id.toggle;
import static app.myjuet.com.myjuet.R.menu.drawer;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
    protected void onStart() {
        navigationView.getMenu().getItem(0).isChecked();
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.attendence_drawer));
        super.onStart();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();
        FragmentTransaction transition = getFragmentManager().beginTransaction();
        if (id == R.id.attendence_drawer) {
            Fragment fragment = new AttendenceActivity();
            transition.replace(R.id.content_drawer, fragment);
            transition.commit();
            Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, tool, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            toggle.setDrawerIndicatorEnabled(true);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

        } else if (id == R.id.timetable_drawer) {

        } else if (id == R.id.Events_drawer) {

        } else if (id == R.id.annapurna_drawer) {

        } else if (id == R.id.contact_drawer) {

        } else if (id == R.id.feedback_drawer) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
