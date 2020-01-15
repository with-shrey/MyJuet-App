package app.myjuet.com.myjuet.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.InetSocketAddress;
import java.net.Socket;

import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.services.RefreshService;
import app.myjuet.com.myjuet.utilities.Constants;
import app.myjuet.com.myjuet.utilities.SharedPreferencesUtil;
import app.myjuet.com.myjuet.utilities.webUtilities;
import app.myjuet.com.myjuet.vm.LoginViewModel;


public class SettingsActivity extends AppCompatActivity {
    TextInputEditText enrollment;
    TextInputEditText password,dob;
    TextInputEditText preferred;
    SharedPreferences.Editor editor;
    LoginViewModel mLoginViewModel;
    ProgressDialog dialog;

    int status = -1;



    Switch autosync,dark;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        if(SharedPreferencesUtil.getInstance(this).getPreferences("dark",false))
            setTheme(R.style.DarkTheme);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
        getSupportActionBar().setTitle("Settings");
        }
        enrollment = findViewById(R.id.preference_enrollment);
        password = findViewById(R.id.preference_password);
        dob = findViewById(R.id.dob);
        preferred = findViewById(R.id.preference_percent);

        autosync = findViewById(R.id.autosync);
        dark = findViewById(R.id.dark);
        dark.setChecked(SharedPreferencesUtil.getInstance(this).getPreferences("dark",false));
        dark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferencesUtil.getInstance(this).savePreferences("dark",isChecked);
            if(isChecked)
                setTheme(R.style.DarkTheme);
            else{
                setTheme(R.style.AppTheme);
            }
            Toast.makeText(this, "Restart App For Changes To Take Place", Toast.LENGTH_SHORT).show();
        });
        initialize();

    }

    public void show(int value, final TextView textView) {

        RelativeLayout linearLayout = new RelativeLayout(SettingsActivity.this);
        final NumberPicker aNumberPicker = new NumberPicker(SettingsActivity.this);
        aNumberPicker.setMaxValue(59);
        aNumberPicker.setMinValue(0);
        aNumberPicker.setValue(value);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        linearLayout.setLayoutParams(params);
        linearLayout.addView(aNumberPicker, numPicerParams);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
        alertDialogBuilder.setTitle("SELECT MINUTES");
        alertDialogBuilder.setView(linearLayout);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Ok",
                        (dialog, id) -> textView.setText(String.valueOf(aNumberPicker.getValue())))
                .setNegativeButton("Cancel",
                        (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void initialize() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        enrollment.setText(sharedPref.getString(getString(R.string.key_enrollment), ""));
        password.setText(sharedPref.getString(getString(R.string.key_password), ""));
        dob.setText(sharedPref.getString(Constants.DOB, ""));
        preferred.setText(sharedPref.getString(getString(R.string.key_preferred_attendence), "90"));
        if (sharedPref.getBoolean("autosync", true)) {
            autosync.setChecked(true);
        }
    }

    private void save() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        boolean changed = true;
        if (enrollment.getText().toString().equals(sharedPref.getString(getString(R.string.key_enrollment), ""))
                && password.getText().toString().equals(sharedPref.getString("password", ""))
                && dob.getText().toString().equals(sharedPref.getString(Constants.DOB, ""))
        )
            changed = false;

        String temp = enrollment.getText().toString().replaceAll(" ", "");
        editor.putString(getString(R.string.key_enrollment), temp.toUpperCase().trim());
        editor.putString(getString(R.string.key_password), password.getText().toString());
        editor.putString(Constants.DOB, dob.getText().toString());
        editor.putString(getString(R.string.key_preferred_attendence), preferred.getText().toString());

        if (autosync.isChecked()) {
            editor.putBoolean("autosync", true);
        } else {
            editor.putBoolean("autosync", false);

        }


        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        if (!prefs.getBoolean("firstTime", false)) {

            editor.putBoolean("firstTime", true);
        }

        if (changed) {
            String user = enrollment.getText().toString().toUpperCase();
            String date = dob.getText().toString();
            user = user.replaceAll(" ", "").trim();
            String pass = password.getText().toString().trim();

            mLoginViewModel.loginUser(user, pass, date).observe(this, status -> {
                if (status != null) {
                    switch (status) {
                        case LOADING:
                            dialog = new ProgressDialog(SettingsActivity.this);
                            dialog.setMessage("Connecting...");
                            dialog.setProgressPercentFormat(null);
                            dialog.setProgressNumberFormat(null);
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                            break;
                        case SUCCESS:
                            boolean reason = editor.commit();
                            SharedPreferencesUtil.getInstance(getApplicationContext()).savePreferences("dark",dark.isChecked());
                            Toast.makeText(SettingsActivity.this, "Saved", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent("SetAlarms");
                            sendBroadcast(intent);

                            new Handler().postDelayed(() -> {
                                Intent refresh = new Intent("refreshAttendence");
                                refresh.putExtra("manual", true);
                                sendBroadcast(refresh);
                            }, 2000);
                            Intent intent2 = new Intent(SettingsActivity.this, DrawerActivity.class);
                            startActivity(intent2);
                            Toast.makeText(SettingsActivity.this, "Background Sync Started", Toast.LENGTH_LONG).show();
                            finish();
                            dialog.dismiss();
                            break;
                        case WRONG_PASSWORD:
                            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            break;
                        case NO_INTERNET:
                            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            break;
                        case WEBKIOSK_DOWN:
                            Toast.makeText(this, "Webkiosk Not Responding", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            break;
                        case FAILED:
                            Toast.makeText(this, "Unknown Error Occured", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            break;
                    }
                }
            });
        }else{
            boolean reason = editor.commit();
            SharedPreferencesUtil.getInstance(this).savePreferences("dark",dark.isChecked());
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sendbutton, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (enrollment.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enrollment is Required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.getText().toString().isEmpty()) {
            Toast.makeText(this, "Password is Required", Toast.LENGTH_SHORT).show();

            return false;

        }else if (dob.getText().toString().isEmpty()) {
            Toast.makeText(this, "DOB is Required", Toast.LENGTH_SHORT).show();

            return false;

        } else if (preferred.getText().toString().isEmpty()) {
            Toast.makeText(this, "Preferred Percentage is Required", Toast.LENGTH_SHORT).show();

            return false;

        } else if (Integer.valueOf(preferred.getText().toString()) >= 100) {
            Toast.makeText(this, "Preferred Attendence Should Be Less Than 100", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            save();

            return true;
        }
    }

    @Override
    public void onBackPressed() {
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please Click Back Again To Quit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
        }
//        super.onBackPressed();
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
