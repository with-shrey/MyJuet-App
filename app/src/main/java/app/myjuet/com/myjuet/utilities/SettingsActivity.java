package app.myjuet.com.myjuet.utilities;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
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

import app.myjuet.com.myjuet.DrawerActivity;
import app.myjuet.com.myjuet.R;


public class SettingsActivity extends AppCompatActivity {
    TextInputEditText enrollment;
    TextInputEditText password;
    TextInputEditText preferred;
    SharedPreferences.Editor editor;

    int status = -1;



    Switch autosync,dark;
    boolean doubleBackToExitPressedOnce = false;

    private static boolean pingHost(String host, int port, int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), timeout);
            socket.close();
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if(SharedPreferencesUtil.getPreferences(this,"dark",false))
            setTheme(R.style.DarkTheme);
        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
        getSupportActionBar().setTitle("Settings");
        }
        enrollment = findViewById(R.id.preference_enrollment);
        password = findViewById(R.id.preference_password);
        preferred = findViewById(R.id.preference_percent);

        autosync = findViewById(R.id.autosync);
        dark = findViewById(R.id.dark);
        dark.setChecked(SharedPreferencesUtil.getPreferences(this,"dark",false));
        dark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferencesUtil.savePreferences(this,"dark",isChecked);
            if(isChecked)
                setTheme(R.style.DarkTheme);
            else{
                setTheme(R.style.AppTheme);
            }
            this.recreate();
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
        preferred.setText(sharedPref.getString(getString(R.string.key_preferred_attendence), "90"));
        if (sharedPref.getBoolean("autosync", true)) {
            autosync.setChecked(true);
        }
    }

    private void save() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        boolean changed = true;
        if (enrollment.getText().toString().equals(sharedPref.getString(getString(R.string.key_enrollment), "")) && password.getText().toString().equals(sharedPref.getString("password", "")))
            changed = false;

        String temp = enrollment.getText().toString().replaceAll(" ", "");
        editor.putString(getString(R.string.key_enrollment), temp.toUpperCase().trim());
        editor.putString(getString(R.string.key_password), password.getText().toString());
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
        String Url = "https://webkiosk.juet.ac.in/CommonFiles/UserAction.jsp";
        String user = enrollment.getText().toString().toUpperCase();
        user = user.replaceAll(" ", "").trim();
        String pass = password.getText().toString().trim();
        String PostParam = "txtInst=Institute&InstCode=JUET&txtuType=Member+Type&UserType=S&txtCode=Enrollment+No&MemberCode=" + user + "&txtPin=Password%2FPin&Password=" + pass + "&BTNSubmit=Submit";
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (changed) {
            if (isConnected) {
                CookieHandler.setDefault(new CookieManager());
                new login().execute(Url, PostParam, "https://webkiosk.juet.ac.in/StudentFiles/Academic/StudentAttendanceList.jsp");
            } else {
                Toast.makeText(this, "No Internet", Toast.LENGTH_LONG).show();
            }
        } else {
            boolean reason = editor.commit();
            SharedPreferencesUtil.savePreferences(this,"dark",dark.isChecked());
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



    private class login extends AsyncTask<String, Integer, Integer> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(SettingsActivity.this);
            dialog.setMessage("Connecting...");
            dialog.setProgressPercentFormat(null);
            dialog.setProgressNumberFormat(null);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected Integer doInBackground(String... strings) {
            String Content = null;

            try {
                if (!pingHost("webkiosk.juet.ac.in", 80, 6000)) {
                    return 1;
                }
                publishProgress(1);
                Content = webUtilities.sendPost(strings[0], strings[1]);
                publishProgress(2);
                Log.v("Login", Content);
                if (Content.contains("Login</a>"))
                    return 0;
                else if (Content.contains("Invalid Password"))
                    return 3;
                else if (Content.contains("Wrong Member"))
                    return 4;
                else
                    return 2;
            } catch (Exception e) {
                e.printStackTrace();
            }


//            try {
//                Content = webUtilities.GetPageContent(strings[2]);


//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            publishProgress(3);
            return -1;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values[0] == 1)
                dialog.setMessage("Logging In...");
            else if (values[0] == 2)
                dialog.setMessage("Processing Data...");
            else {
                dialog.setMessage("Just A Minute...");

            }

        }

        @Override
        protected void onPostExecute(Integer aBoolean) {
            status = aBoolean;
            Log.v("Response", String.valueOf(aBoolean));
            if (aBoolean == 2) {
                boolean reason = editor.commit();
                SharedPreferencesUtil.savePreferences(getApplicationContext(),"dark",dark.isChecked());
                new Thread(() -> {
                    Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
                    shortcutintent.putExtra("duplicate", false);
                    shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "My Juet");
                    Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher);
                    shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
                    shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), DrawerActivity.class));
                    sendBroadcast(shortcutintent);
                }).start();
                new Thread(() -> {
                    Intent shortcutintent1 = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
                    shortcutintent1.putExtra("duplicate", false);
                    shortcutintent1.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Webkiosk");
                    Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher);
                    shortcutintent1.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
                    Intent drawerIntent = new Intent(getApplicationContext(), DrawerActivity.class);
                    drawerIntent.putExtra("fragment", 4);
                    drawerIntent.putExtra("containsurl", false);
                    Parcelable icon2 = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_webkiosk);
                    shortcutintent1.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon2);
                    shortcutintent1.putExtra(Intent.EXTRA_SHORTCUT_INTENT, drawerIntent);
                    sendBroadcast(shortcutintent1);
                }).start();
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
            } else if (aBoolean == 0) {
                Toast.makeText(SettingsActivity.this, "Invalid Login", Toast.LENGTH_LONG).show();
            } else if (aBoolean == 4) {
                Toast.makeText(SettingsActivity.this, "Wrong Enrollment No", Toast.LENGTH_LONG).show();
            } else if (aBoolean == 3) {
                Toast.makeText(SettingsActivity.this, "Wrong Password", Toast.LENGTH_LONG).show();
            } else if (aBoolean == 1) {
                Toast.makeText(SettingsActivity.this, "Webkiosk Unreachable\nTry Again Later", Toast.LENGTH_LONG).show();

            } else
                Toast.makeText(SettingsActivity.this, "Unknown Error\nTryAgain Later", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }


    }
}
