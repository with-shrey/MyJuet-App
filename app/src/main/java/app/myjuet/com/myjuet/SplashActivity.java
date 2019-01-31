package app.myjuet.com.myjuet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        String user = sharedPref.getString(getString(R.string.key_enrollment), "");
        String pass = sharedPref.getString(getString(R.string.key_password), "");
        if (user.equals("") || pass.equals("")){
            Intent intent = new Intent (this,LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent (this,DrawerActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
