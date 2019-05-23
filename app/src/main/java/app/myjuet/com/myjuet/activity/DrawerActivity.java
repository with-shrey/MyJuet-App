package app.myjuet.com.myjuet.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import javax.inject.Inject;

import app.myjuet.com.myjuet.BuildConfig;
import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.data.util.HeaderDataHolder;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.databinding.ActivityDrawerBinding;
import app.myjuet.com.myjuet.databinding.NavHeaderDrawerBinding;
import app.myjuet.com.myjuet.utilities.SharedPreferencesUtil;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;


public class DrawerActivity extends AppCompatActivity implements
        NavController.OnDestinationChangedListener
        , NavigationView.OnNavigationItemSelectedListener,
        HasSupportFragmentInjector {
    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
    @Inject
    SharedPreferencesUtil mPreferencesUtil;
    public NavigationView mNavigationView;
    private InterstitialAd mInterstitialAd;
    private boolean mDoubleBackToExitPressedOnce = false;
    private AppBarLayout mAppBarLayout;
    private NavController mNavController;
    private ActivityDrawerBinding mBinding;
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
        if (mPreferencesUtil.getPreferences("dark", false)) {
            setTheme(R.style.DarkTheme);
            mBinding = DataBindingUtil.setContentView(this,R.layout.activity_drawer);
            findViewById(R.id.overlay).setVisibility(View.VISIBLE);
        }else{
            mBinding = DataBindingUtil.setContentView(this,R.layout.activity_drawer);
        }
        setupNavigationComponents();
        setupAndLoadAds();
        checkAndShowUpdatesDialog();
        instantiateFirebase();
    }
    void instantiateFirebase(){
        FirebaseMessaging.getInstance().subscribeToTopic("juet");
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent("app_launch",new Bundle());
    }
    void setupNavigationComponents(){
        mAppBarLayout = findViewById(R.id.app_bar);
        mNavigationView = mBinding.navView;
        Toolbar tool = findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);

        setSupportActionBar(tool);
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(collapsingToolbarLayout, tool, mNavController, mBinding.drawerLayout);
        NavigationUI.setupWithNavController(mNavigationView, mNavController);
        mNavController.addOnDestinationChangedListener(this);
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        String user = prefs.getString(getString(R.string.key_enrollment), "");
        NavHeaderDrawerBinding headerBinding = DataBindingUtil.bind(mNavigationView.getHeaderView(0));
        if (headerBinding!=null) {
            headerBinding.setData(new HeaderDataHolder(mNavController, user));
        }
        mNavigationView.setNavigationItemSelectedListener(this);


    }

    void setupAndLoadAds() {
        AdView mAdView = findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());
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
    }

    void checkAndShowUpdatesDialog() {
        if (mPreferencesUtil.getPreferences(
                "firsttime_" + BuildConfig.VERSION_CODE, true)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New In This Release");
            builder.setMessage(Html.fromHtml("1.Seating Plan\n2.Dark Theme (Settings)"));
            builder.setPositiveButton("Ok", (dialog, which) -> {
                mPreferencesUtil.savePreferences("firsttime_" + BuildConfig.VERSION_CODE, false);
                dialog.dismiss();
            });
            builder.setCancelable(false);
            builder.show();

        }
    }

    @Override
    public void onBackPressed() {
        mNavController.popBackStack();
        if (mDoubleBackToExitPressedOnce) {
            requestNewInterstitial();
            super.onBackPressed();
        }
        this.mDoubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please Click Back Again To Quit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> mDoubleBackToExitPressedOnce = false, 2000);
    }


    @Override
    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
        if (arguments != null && arguments.getBoolean("collapsed", false)) {
            mAppBarLayout.setExpanded(false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        boolean handled = NavigationUI.onNavDestinationSelected(item, mNavController);
        if (!handled)
        switch (item.getItemId()){
            case R.id.exit:
                requestNewInterstitial();
                finish();
                break;
            case R.id.reset:
                AppDatabase.newInstance(DrawerActivity.this).clearAllTables();
                break;
            case R.id.logout:
                SharedPreferences.Editor sharedPref = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE).edit();
                sharedPref.clear();
                sharedPref.apply();
                AppDatabase.newInstance(DrawerActivity.this).clearAllTables();
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        closeDrawer();
        return true;
    }

    void closeDrawer(){
        ViewParent parent = mNavigationView.getParent();
        if (parent instanceof DrawerLayout) {
            ((DrawerLayout) parent).closeDrawer(mNavigationView);
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
