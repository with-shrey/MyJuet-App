package app.myjuet.com.myjuet;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import app.myjuet.com.myjuet.services.RefreshService;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText mEnrollment;
    EditText mPassword,mPrefferredPercentage;
    Button mLogin;
    LoginViewModel mLoginViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    void init(){
        mLoginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        mEnrollment = findViewById(R.id.input_enrollment);
        mPassword = findViewById(R.id.input_password);
        mPrefferredPercentage = findViewById(R.id.input_attendence);

        mLogin = findViewById(R.id.btn_login);
        mLogin.setOnClickListener(this);
    }

    boolean validate(){
        if (mEnrollment.getText().toString().isEmpty()) {
            Toast.makeText(this, "Enrollement is Required", Toast.LENGTH_SHORT).show();

            return false;

        }
        if (mPrefferredPercentage.getText().toString().isEmpty()) {
            Toast.makeText(this, "Preferred Percentage is Required", Toast.LENGTH_SHORT).show();

            return false;

        }
        return true;
    }

    void createShortcuts(){
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutintent.putExtra("duplicate", false);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "My Juet");
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), DrawerActivity.class));
        sendBroadcast(shortcutintent);
        Intent shortcutintent1 = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutintent1.putExtra("duplicate", false);
        shortcutintent1.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Webkiosk");
        icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher);
        shortcutintent1.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        Intent drawerIntent = new Intent(getApplicationContext(), DrawerActivity.class);
        drawerIntent.putExtra("fragment", 4);
        drawerIntent.putExtra("containsurl", false);
        Parcelable icon2 = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_webkiosk);
        shortcutintent1.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon2);
        shortcutintent1.putExtra(Intent.EXTRA_SHORTCUT_INTENT, drawerIntent);
        sendBroadcast(shortcutintent1);
    }

    @Override
    public void onClick(View view) {
        if (view == mLogin && validate()){

            mLoginViewModel.loginUser(mEnrollment.getText().toString(),mPassword.getText().toString()).observe(this,status -> {
                if (status != null) {
                    switch (status){
                        case LOADING:
                            showProgress();
                            break;
                        case SUCCESS:
                            SharedPreferences.Editor editor = this.getSharedPreferences(this.getString(R.string.preferencefile), Context.MODE_PRIVATE).edit();
                            editor.putString(getString(R.string.key_preferred_attendence), mPrefferredPercentage.getText().toString());
                            editor.putBoolean("autosync", true);
                            editor.apply();
                            createShortcuts();
                            Toast.makeText(this, "Login Successfull", Toast.LENGTH_SHORT).show();
                            Intent refresh = new Intent(this,RefreshService.class);
                            refresh.putExtra("manual",true);
                            ActivityCompat.startForegroundService(this,refresh);
                            Intent intent = new Intent(this,DrawerActivity.class);
                            startActivity(intent);
                            finish();
                            dismissProgress();
                            break;
                        case WRONG_PASSWORD:
                            Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show();
                            dismissProgress();
                            break;
                        case NO_INTERNET:
                            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
                            dismissProgress();
                            break;
                        case WEBKIOSK_DOWN:
                            Toast.makeText(this, "Webkiosk Not Responding", Toast.LENGTH_SHORT).show();
                            dismissProgress();
                            break;
                        case FAILED:
                            Toast.makeText(this, "Unknown Error Occured", Toast.LENGTH_SHORT).show();
                            dismissProgress();
                            break;
                    }
                }
            });
        }
    }
    ProgressDialog dialog;
    void showProgress(){
        dialog = new ProgressDialog(this);
        dialog.setMessage("Logging In...");
        dialog.setProgressPercentFormat(null);
        dialog.setProgressNumberFormat(null);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    void dismissProgress(){
        if (dialog!=null && dialog.isShowing())
           dialog.dismiss();
    }

}
