package app.myjuet.com.myjuet.web;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.Toast;

import app.myjuet.com.myjuet.R;


public class SettingsActivity extends AppCompatActivity {
    TextInputEditText enrollment;
    TextInputEditText password;
    TextInputEditText preferred;
    NumberPicker ttmin;
    NumberPicker messmin;
    TextInputEditText sem;
    TextInputEditText batch;
    LinearLayout ttmin_layout;
    LinearLayout messmin_layout;


    Switch morningtt;
    Switch beforeclass;
    Switch beforemeal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        enrollment = (TextInputEditText) findViewById(R.id.preference_enrollment);
        password = (TextInputEditText) findViewById(R.id.preference_password);
        preferred = (TextInputEditText) findViewById(R.id.preference_percent);
        ttmin = (NumberPicker) findViewById(R.id.preference_minutes_timetable);
        messmin = (NumberPicker) findViewById(R.id.preference_minutes_mess);
        sem = (TextInputEditText) findViewById(R.id.preference_semester);
        batch = (TextInputEditText) findViewById(R.id.preference_batch);

        ttmin_layout = (LinearLayout) findViewById(R.id.before_class_layout);
        messmin_layout = (LinearLayout) findViewById(R.id.before_meal_layout);

        morningtt = (Switch) findViewById(R.id.preference_tt_morning);
        beforemeal = (Switch) findViewById(R.id.preference_mess_before);
        beforeclass = (Switch) findViewById(R.id.preference_tt_before);
        beforemeal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    messmin_layout.setVisibility(View.VISIBLE);
                    messmin.setMaxValue(59);
                    messmin.setMinValue(0);
                    messmin.setValue(15);
                } else
                    messmin_layout.setVisibility(View.GONE);
            }
        });
        beforeclass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ttmin_layout.setVisibility(View.VISIBLE);
                    ttmin.setMaxValue(59);
                    ttmin.setMinValue(0);
                    ttmin.setValue(15);
                } else
                    ttmin_layout.setVisibility(View.GONE);
            }
        });
        initialize();
    }

    private void initialize() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        enrollment.setText(sharedPref.getString(getString(R.string.key_enrollment), ""));
        password.setText(sharedPref.getString(getString(R.string.key_password), ""));
        preferred.setText(sharedPref.getString(getString(R.string.key_preferred_attendence), "90"));
        sem.setText(sharedPref.getString(getString(R.string.key_semester), ""));
        batch.setText(sharedPref.getString(getString(R.string.key_batch), ""));

        if (sharedPref.getBoolean(getString(R.string.key_alarm_morming), true)) {
            morningtt.setChecked(true);

        }
        if (sharedPref.getBoolean(getString(R.string.key_alarm_meal), true)) {
            beforemeal.setChecked(true);
            messmin.setValue(sharedPref.getInt("beforemealminute", 15));
        }
        if (sharedPref.getBoolean(getString(R.string.key_alarm_class), false)) {
            beforeclass.setChecked(true);
            messmin.setValue(sharedPref.getInt("beforclassminute", 15));
        }


    }

    private void save() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("enrollment", enrollment.getText().toString());
        editor.putString("password", password.getText().toString());
        editor.putString("preferred", preferred.getText().toString());
        editor.putString("semester", sem.getText().toString());
        editor.putString("batch", batch.getText().toString());
        if (morningtt.isChecked()) {
            editor.putBoolean("ttmorning", true);
        }
        if (beforemeal.isChecked()) {
            editor.putBoolean("beforemeal", true);
            editor.putInt("beforemealminute", messmin.getValue());
        }
        if (beforeclass.isChecked()) {
            editor.putBoolean("beforeclass", true);
            editor.putInt("beforeclassminute", ttmin.getValue());
        }

        editor.apply();

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

        } else if (sem.getText().toString().isEmpty()) {
            Toast.makeText(this, "Semester is Required", Toast.LENGTH_SHORT).show();

            return false;

        } else if (batch.getText().toString().isEmpty()) {
            Toast.makeText(this, "Batch is Required", Toast.LENGTH_SHORT).show();
            return false;

        } else {
            save();
            return true;
        }
    }
}
