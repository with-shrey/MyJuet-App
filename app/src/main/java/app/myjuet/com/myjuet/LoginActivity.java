package app.myjuet.com.myjuet;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import app.myjuet.com.myjuet.services.RefreshService;
import app.myjuet.com.myjuet.utilities.SettingsActivity;
import app.myjuet.com.myjuet.vm.AttendenceViewModel;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText mEnrollment;
    EditText mPassword;
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
        mLogin = findViewById(R.id.btn_login);
        mLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mLogin){
            mLoginViewModel.loginUser(mEnrollment.getText().toString(),mPassword.getText().toString()).observe(this,status -> {
                switch (status){
                    case LOADING:
                        showProgress();
                        break;
                    case SUCCESS:
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
