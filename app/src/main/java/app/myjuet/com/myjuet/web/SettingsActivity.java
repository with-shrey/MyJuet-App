package app.myjuet.com.myjuet.web;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import app.myjuet.com.myjuet.AlarmReciever;
import app.myjuet.com.myjuet.R;


public class SettingsActivity extends AppCompatActivity {
    TextInputEditText enrollment;
    TextInputEditText password;
    TextInputEditText preferred;
    TextView ttmin;
    TextView messmin;
    TextInputEditText sem;
    TextInputEditText batch;
    LinearLayout ttmin_layout;
    LinearLayout messmin_layout;
    String ringtonett;
    String ringtonemess;
    EditText admin;


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
        ttmin = (TextView) findViewById(R.id.preference_minutes_timetable);
        messmin = (TextView) findViewById(R.id.preference_minutes_mess);
        sem = (TextInputEditText) findViewById(R.id.preference_semester);
        batch = (TextInputEditText) findViewById(R.id.preference_batch);

        admin = (EditText) findViewById(R.id.admin_text);
        View view = (View) findViewById(R.id.adminview);

        Button ringmess = (Button) findViewById(R.id.mess_ring);
        Button ringtt = (Button) findViewById(R.id.tt_ring);

        ttmin_layout = (LinearLayout) findViewById(R.id.before_class_layout);
        messmin_layout = (LinearLayout) findViewById(R.id.before_meal_layout);

        morningtt = (Switch) findViewById(R.id.preference_tt_morning);
        beforemeal = (Switch) findViewById(R.id.preference_mess_before);
        beforeclass = (Switch) findViewById(R.id.preference_tt_before);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                admin.setVisibility(View.VISIBLE);
            }
        });
        ringmess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone For Mess");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(ringtonemess));
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                startActivityForResult(intent, 1);
            }
        });
        ringtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone For TimeTable");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(ringtonett));
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                startActivityForResult(intent, 2);
            }
        });
        beforemeal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    messmin_layout.setVisibility(View.VISIBLE);
                    messmin_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String temp = messmin.getText().toString();
                            show(Integer.valueOf(temp), messmin);
                        }
                    });
                } else
                    messmin_layout.setVisibility(View.GONE);
            }
        });
        beforeclass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ttmin_layout.setVisibility(View.VISIBLE);
                    ttmin_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String temp = ttmin.getText().toString();
                            show(Integer.valueOf(temp), ttmin);
                        }
                    });
                } else
                    ttmin_layout.setVisibility(View.GONE);
            }
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
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                textView.setText(String.valueOf(aNumberPicker.getValue()));

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    private void initialize() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        enrollment.setText(sharedPref.getString(getString(R.string.key_enrollment), ""));
        password.setText(sharedPref.getString(getString(R.string.key_password), ""));
        preferred.setText(sharedPref.getString(getString(R.string.key_preferred_attendence), "90"));
        sem.setText(sharedPref.getString(getString(R.string.key_semester), ""));
        batch.setText(sharedPref.getString(getString(R.string.key_batch), ""));
        ringtonemess = sharedPref.getString("notificationmess", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
        ringtonett = sharedPref.getString("notificationtt", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());

        if (sharedPref.getBoolean(getString(R.string.key_alarm_morming), true)) {
            morningtt.setChecked(true);

        }
        if (sharedPref.getBoolean(getString(R.string.key_alarm_meal), true)) {
            beforemeal.setChecked(true);
            messmin.setText(String.valueOf(sharedPref.getInt("beforemealminute", 15)));
        }
        if (sharedPref.getBoolean(getString(R.string.key_alarm_class), false)) {
            beforeclass.setChecked(true);
            ttmin.setText(String.valueOf(sharedPref.getInt("beforclassminute", 15)));
        }


    }

    private void save() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.key_enrollment), enrollment.getText().toString());
        editor.putString(getString(R.string.key_password), password.getText().toString());
        editor.putString(getString(R.string.key_preferred_attendence), preferred.getText().toString());
        editor.putString(getString(R.string.key_semester), sem.getText().toString());
        editor.putString(getString(R.string.key_batch), batch.getText().toString());
        editor.putString(getString(R.string.key_notification_mess), ringtonemess);
        editor.putString(getString(R.string.key_notification_tt), ringtonett);
        if (admin.getVisibility() == View.VISIBLE)
            editor.putString("admin", admin.getText().toString());
        if (morningtt.isChecked()) {
            editor.putBoolean(getString(R.string.key_alarm_morming), true);
        } else {
            editor.putBoolean(getString(R.string.key_alarm_morming), false);

        }
        if (beforemeal.isChecked()) {
            editor.putBoolean(getString(R.string.key_alarm_meal), true);
            editor.putInt(getString(R.string.key_minutes_before_meal), Integer.valueOf(messmin.getText().toString()));
        } else {
            editor.putBoolean(getString(R.string.key_alarm_meal), false);

        }
        if (beforeclass.isChecked()) {
            editor.putBoolean(getString(R.string.key_alarm_class), true);
            editor.putInt(getString(R.string.key_minutes_before_class), Integer.valueOf(ttmin.getText().toString()));
        } else {
            editor.putBoolean(getString(R.string.key_alarm_class), false);
        }

        editor.apply();
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        finish();

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
            cancelMessAlarms();
            cancelttAlarms();
            cancelmorningalarm();
            Intent intent = new Intent("SetAlarms");
            sendBroadcast(intent);
            return true;
        }
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

    private void cancelMessAlarms() {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 48, new Intent(this, AlarmReciever.class).putExtra("title", "BreakFast Time").putExtra("fragmentno", 2), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, 49, new Intent(this, AlarmReciever.class).putExtra("title", "Dinner Time").putExtra("fragmentno", 2), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, 50, new Intent(this, AlarmReciever.class).putExtra("title", "Lunch Time").putExtra("fragmentno", 2), PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
        am.cancel(pendingIntent1);
        am.cancel(pendingIntent2);
    }

    private void cancelttAlarms() {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        for (int i = 0; i < 6; i++) {
            am.cancel(PendingIntent.getBroadcast(this, 0 + (i), new Intent(this, AlarmReciever.class).putExtra("title", "Class @ 10:00").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT));
            am.cancel(PendingIntent.getBroadcast(this, 6 + i, new Intent(this, AlarmReciever.class).putExtra("title", "Class @ 11:00").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT));
            am.cancel(PendingIntent.getBroadcast(this, 13 + i, new Intent(this, AlarmReciever.class).putExtra("title", "Class @ 12:00").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT));
            am.cancel(PendingIntent.getBroadcast(this, 20 + i, new Intent(this, AlarmReciever.class).putExtra("title", "Class @ 02:00").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT));
            am.cancel(PendingIntent.getBroadcast(this, 27 + i, new Intent(this, AlarmReciever.class).putExtra("title", "Class @ 03:00").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT));
            am.cancel(PendingIntent.getBroadcast(this, 34 + i, new Intent(this, AlarmReciever.class).putExtra("title", "Class @ 04:00").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT));
            am.cancel(PendingIntent.getBroadcast(this, 41 + i, new Intent(this, AlarmReciever.class).putExtra("title", "Class @ 05:00").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

    void cancelmorningalarm() {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(PendingIntent.getBroadcast(this, 51, new Intent(this, AlarmReciever.class).putExtra("title", "TimeTable Of Today").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT));

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null) {
                this.ringtonemess = uri.toString();
            } else {
                this.ringtonemess = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
            }
        }
        if (resultCode == RESULT_OK && requestCode == 2) {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null) {
                this.ringtonett = uri.toString();
            } else {
                this.ringtonett = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
            }
        }
    }
}
