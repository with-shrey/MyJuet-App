package app.myjuet.com.myjuet.timetable;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import app.myjuet.com.myjuet.recievers.AlarmReciever;
import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.data.TimeTableData;

import static app.myjuet.com.myjuet.AttendenceFragment.read;
import static app.myjuet.com.myjuet.R.array.Days;

@SuppressWarnings({"UnusedAssignment", "unused"})
public class TableSettingsActivity extends AppCompatActivity implements Runnable {
    public static final int MONDAY = 0;
    public static final int TUESDAY = 1;
    public static final int WEDNESDAY = 2;
    public static final int THURSDAY = 3;
    public static final int FRIDAY = 4;
    public static final int SATURDAY = 5;

    ArrayList<TimeTableData> settings = new ArrayList<>();

    String batch;
    String sem;


    ArrayList<String> Subjects = new ArrayList<>();

    @Override
    public void run() {
        Subjects.add("SUBJECT");
        ArrayList<AttendenceData> data = new ArrayList<>();
        try {
            data = read(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < data.size(); i++) {
            Subjects.add(data.get(i).getmName());
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_settings);
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        batch = sharedPref.getString("batch", "").toUpperCase().trim();
        sem = sharedPref.getString("semester", "").toUpperCase().trim();
        new Thread(new Runnable() {
            public void run() {

                try {
                    settings = readSettings();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (settings.isEmpty()) {
                    for (int i = 0; i < 6; i++)
                        settings.add(new TimeTableData());
                }
            }

        }
        ).start();


        Thread thread = new Thread(this);
        final ArrayAdapter<String> SubjectsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Subjects);
        ArrayAdapter<CharSequence> TypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.type, android.R.layout.simple_spinner_item);
        TypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        thread.run();
        SubjectsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarsettings);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
        getSupportActionBar().setElevation(5);
            getSupportActionBar().setTitle("TimeTable");
        }
        final Spinner Day = (Spinner) findViewById(R.id.spinner_day);
        final Spinner nine = (Spinner) findViewById(R.id.spinner_9);
        final Spinner ten = (Spinner) findViewById(R.id.spinner_10);
        final Spinner eleven = (Spinner) findViewById(R.id.spinner_11);
        final Spinner twelve = (Spinner) findViewById(R.id.spinner_12);
        final Spinner two = (Spinner) findViewById(R.id.spinner_2);
        final Spinner three = (Spinner) findViewById(R.id.spinner_3);
        final Spinner four = (Spinner) findViewById(R.id.spinner_4);
        final Spinner five = (Spinner) findViewById(R.id.spinner_5);

        final Spinner nineType = (Spinner) findViewById(R.id.spinner_9_type);
        final Spinner tenType = (Spinner) findViewById(R.id.spinner_10_type);
        final Spinner elevenType = (Spinner) findViewById(R.id.spinner_11_type);
        final Spinner twelveType = (Spinner) findViewById(R.id.spinner_12_type);
        final Spinner twoType = (Spinner) findViewById(R.id.spinner_2_type);
        final Spinner threeType = (Spinner) findViewById(R.id.spinner_3_type);
        final Spinner fourType = (Spinner) findViewById(R.id.spinner_4_type);
        final Spinner fiveType = (Spinner) findViewById(R.id.spinner_5_type);

        final TextInputEditText nineText = (TextInputEditText) findViewById(R.id.spinner_9_text);
        final TextInputEditText tenText = (TextInputEditText) findViewById(R.id.spinner_10_text);
        final TextInputEditText elevenText = (TextInputEditText) findViewById(R.id.spinner_11_text);
        final TextInputEditText twelveText = (TextInputEditText) findViewById(R.id.spinner_12_text);
        final TextInputEditText twoText = (TextInputEditText) findViewById(R.id.spinner_2_text);
        final TextInputEditText threeText = (TextInputEditText) findViewById(R.id.spinner_3_text);
        final TextInputEditText fourText = (TextInputEditText) findViewById(R.id.spinner_4_text);
        final TextInputEditText fiveText = (TextInputEditText) findViewById(R.id.spinner_5_text);

        nine.setAdapter(SubjectsAdapter);
        ten.setAdapter(SubjectsAdapter);
        eleven.setAdapter(SubjectsAdapter);
        twelve.setAdapter(SubjectsAdapter);
        two.setAdapter(SubjectsAdapter);
        three.setAdapter(SubjectsAdapter);
        four.setAdapter(SubjectsAdapter);
        five.setAdapter(SubjectsAdapter);

        nineType.setAdapter(TypeAdapter);
        tenType.setAdapter(TypeAdapter);
        elevenType.setAdapter(TypeAdapter);
        twelveType.setAdapter(TypeAdapter);
        twoType.setAdapter(TypeAdapter);
        threeType.setAdapter(TypeAdapter);
        fourType.setAdapter(TypeAdapter);
        fiveType.setAdapter(TypeAdapter);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                Days, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Day.setAdapter(adapter);
        Day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (Day.getSelectedItem().toString().trim().equals("Monday")) {
                    nine.setSelection(settings.get(MONDAY).getPosNine(), false);
                    nineType.setSelection(settings.get(MONDAY).getTypeNine(), false);
                    nineText.setText(settings.get(MONDAY).getLocNine());
                    ten.setSelection(settings.get(MONDAY).getPosTen(), false);
                    tenType.setSelection(settings.get(MONDAY).getTypeTen(), false);
                    tenText.setText(settings.get(MONDAY).getLocTen());
                    eleven.setSelection(settings.get(MONDAY).getPosEleven(), false);
                    elevenType.setSelection(settings.get(MONDAY).getTypeEleven(), false);
                    elevenText.setText(settings.get(MONDAY).getLocEleven());
                    twelve.setSelection(settings.get(MONDAY).getPosTwelve(), false);
                    twelveType.setSelection(settings.get(MONDAY).getTypeTwelve(), false);
                    twelveText.setText(settings.get(MONDAY).getLocTwelve());
                    two.setSelection(settings.get(MONDAY).getPosTwo(), false);
                    twoType.setSelection(settings.get(MONDAY).getTypeTwo(), false);
                    twoText.setText(settings.get(MONDAY).getLocTwo());
                    three.setSelection(settings.get(MONDAY).getPosThree(), false);
                    threeType.setSelection(settings.get(MONDAY).getTypeThree(), false);
                    threeText.setText(settings.get(MONDAY).getLocThree());
                    four.setSelection(settings.get(MONDAY).getPosFour(), false);
                    fourType.setSelection(settings.get(MONDAY).getTypeFour(), false);
                    fourText.setText(settings.get(MONDAY).getLocFour());
                    five.setSelection(settings.get(MONDAY).getPosFive(), false);
                    fiveType.setSelection(settings.get(MONDAY).getTypeFive(), false);
                    fiveText.setText(settings.get(MONDAY).getLocFive());
                } else if (Day.getSelectedItem().toString().trim().equals("Tuesday")) {
                    nine.setSelection(settings.get(TUESDAY).getPosNine(), false);
                    nineType.setSelection(settings.get(TUESDAY).getTypeNine(), false);
                    nineText.setText(settings.get(TUESDAY).getLocNine());
                    ten.setSelection(settings.get(TUESDAY).getPosTen(), false);
                    tenType.setSelection(settings.get(TUESDAY).getTypeTen(), false);
                    tenText.setText(settings.get(TUESDAY).getLocTen());
                    eleven.setSelection(settings.get(TUESDAY).getPosEleven(), false);
                    elevenType.setSelection(settings.get(TUESDAY).getTypeEleven(), false);
                    elevenText.setText(settings.get(TUESDAY).getLocEleven());
                    twelve.setSelection(settings.get(TUESDAY).getPosTwelve(), false);
                    twelveType.setSelection(settings.get(TUESDAY).getTypeTwelve(), false);
                    twelveText.setText(settings.get(TUESDAY).getLocTwelve());
                    two.setSelection(settings.get(TUESDAY).getPosTwo(), false);
                    twoType.setSelection(settings.get(TUESDAY).getTypeTwo(), false);
                    twoText.setText(settings.get(TUESDAY).getLocTwo());
                    three.setSelection(settings.get(TUESDAY).getPosThree(), false);
                    threeType.setSelection(settings.get(TUESDAY).getTypeThree(), false);
                    threeText.setText(settings.get(TUESDAY).getLocThree());
                    four.setSelection(settings.get(TUESDAY).getPosFour(), false);
                    fourType.setSelection(settings.get(TUESDAY).getTypeFour(), false);
                    fourText.setText(settings.get(TUESDAY).getLocFour());
                    five.setSelection(settings.get(TUESDAY).getPosFive(), false);
                    fiveType.setSelection(settings.get(TUESDAY).getTypeFive(), false);
                    fiveText.setText(settings.get(TUESDAY).getLocFive());
                } else if (Day.getSelectedItem().toString().trim().equals("Wednesday")) {

                    nine.setSelection(settings.get(WEDNESDAY).getPosNine(), false);
                    nineType.setSelection(settings.get(WEDNESDAY).getTypeNine(), false);
                    nineText.setText(settings.get(WEDNESDAY).getLocNine());
                    ten.setSelection(settings.get(WEDNESDAY).getPosTen(), false);
                    tenType.setSelection(settings.get(WEDNESDAY).getTypeTen(), false);
                    tenText.setText(settings.get(WEDNESDAY).getLocTen());
                    eleven.setSelection(settings.get(WEDNESDAY).getPosEleven(), false);
                    elevenType.setSelection(settings.get(WEDNESDAY).getTypeEleven(), false);
                    elevenText.setText(settings.get(WEDNESDAY).getLocEleven());
                    twelve.setSelection(settings.get(WEDNESDAY).getPosTwelve(), false);
                    twelveType.setSelection(settings.get(WEDNESDAY).getTypeTwelve(), false);
                    twelveText.setText(settings.get(WEDNESDAY).getLocTwelve());
                    two.setSelection(settings.get(WEDNESDAY).getPosTwo(), false);
                    twoType.setSelection(settings.get(WEDNESDAY).getTypeTwo(), false);
                    twoText.setText(settings.get(WEDNESDAY).getLocTwo());
                    three.setSelection(settings.get(WEDNESDAY).getPosThree(), false);
                    threeType.setSelection(settings.get(WEDNESDAY).getTypeThree(), false);
                    threeText.setText(settings.get(WEDNESDAY).getLocThree());
                    four.setSelection(settings.get(WEDNESDAY).getPosFour(), false);
                    fourType.setSelection(settings.get(WEDNESDAY).getTypeFour(), false);
                    fourText.setText(settings.get(WEDNESDAY).getLocFour());
                    five.setSelection(settings.get(WEDNESDAY).getPosFive(), false);
                    fiveType.setSelection(settings.get(WEDNESDAY).getTypeFive(), false);
                    fiveText.setText(settings.get(WEDNESDAY).getLocFive());

                } else if (Day.getSelectedItem().toString().trim().equals("Thursday")) {

                    nine.setSelection(settings.get(THURSDAY).getPosNine(), false);
                    nineType.setSelection(settings.get(THURSDAY).getTypeNine(), false);
                    nineText.setText(settings.get(THURSDAY).getLocNine());
                    ten.setSelection(settings.get(THURSDAY).getPosTen(), false);
                    tenType.setSelection(settings.get(THURSDAY).getTypeTen(), false);
                    tenText.setText(settings.get(THURSDAY).getLocTen());
                    eleven.setSelection(settings.get(THURSDAY).getPosEleven(), false);
                    elevenType.setSelection(settings.get(THURSDAY).getTypeEleven(), false);
                    elevenText.setText(settings.get(THURSDAY).getLocEleven());
                    twelve.setSelection(settings.get(THURSDAY).getPosTwelve(), false);
                    twelveType.setSelection(settings.get(THURSDAY).getTypeTwelve(), false);
                    twelveText.setText(settings.get(THURSDAY).getLocTwelve());
                    two.setSelection(settings.get(THURSDAY).getPosTwo(), false);
                    twoType.setSelection(settings.get(THURSDAY).getTypeTwo(), false);
                    twoText.setText(settings.get(THURSDAY).getLocTwo());
                    three.setSelection(settings.get(THURSDAY).getPosThree(), false);
                    threeType.setSelection(settings.get(THURSDAY).getTypeThree(), false);
                    threeText.setText(settings.get(THURSDAY).getLocThree());
                    four.setSelection(settings.get(THURSDAY).getPosFour(), false);
                    fourType.setSelection(settings.get(THURSDAY).getTypeFour(), false);
                    fourText.setText(settings.get(THURSDAY).getLocFour());
                    five.setSelection(settings.get(THURSDAY).getPosFive(), false);
                    fiveType.setSelection(settings.get(THURSDAY).getTypeFive(), false);
                    fiveText.setText(settings.get(THURSDAY).getLocFive());
                } else if (Day.getSelectedItem().toString().trim().equals("Friday")) {

                    nine.setSelection(settings.get(FRIDAY).getPosNine(), false);
                    nineType.setSelection(settings.get(FRIDAY).getTypeNine(), false);
                    nineText.setText(settings.get(FRIDAY).getLocNine());
                    ten.setSelection(settings.get(FRIDAY).getPosTen(), false);
                    tenType.setSelection(settings.get(FRIDAY).getTypeTen(), false);
                    tenText.setText(settings.get(FRIDAY).getLocTen());
                    eleven.setSelection(settings.get(FRIDAY).getPosEleven(), false);
                    elevenType.setSelection(settings.get(FRIDAY).getTypeEleven(), false);
                    elevenText.setText(settings.get(FRIDAY).getLocEleven());
                    twelve.setSelection(settings.get(FRIDAY).getPosTwelve(), false);
                    twelveType.setSelection(settings.get(FRIDAY).getTypeTwelve(), false);
                    twelveText.setText(settings.get(FRIDAY).getLocTwelve());
                    two.setSelection(settings.get(FRIDAY).getPosTwo(), false);
                    twoType.setSelection(settings.get(FRIDAY).getTypeTwo(), false);
                    twoText.setText(settings.get(FRIDAY).getLocTwo());
                    three.setSelection(settings.get(FRIDAY).getPosThree(), false);
                    threeType.setSelection(settings.get(FRIDAY).getTypeThree(), false);
                    threeText.setText(settings.get(FRIDAY).getLocThree());
                    four.setSelection(settings.get(FRIDAY).getPosFour(), false);
                    fourType.setSelection(settings.get(FRIDAY).getTypeFour(), false);
                    fourText.setText(settings.get(FRIDAY).getLocFour());
                    five.setSelection(settings.get(FRIDAY).getPosFive(), false);
                    fiveType.setSelection(settings.get(FRIDAY).getTypeFive(), false);
                    fiveText.setText(settings.get(FRIDAY).getLocFive());
                } else if (Day.getSelectedItem().toString().trim().equals("Saturday")) {

                    nine.setSelection(settings.get(SATURDAY).getPosNine(), false);
                    nineType.setSelection(settings.get(SATURDAY).getTypeNine(), false);
                    nineText.setText(settings.get(SATURDAY).getLocNine());
                    ten.setSelection(settings.get(SATURDAY).getPosTen(), false);
                    tenType.setSelection(settings.get(SATURDAY).getTypeTen(), false);
                    tenText.setText(settings.get(SATURDAY).getLocTen());
                    eleven.setSelection(settings.get(SATURDAY).getPosEleven(), false);
                    elevenType.setSelection(settings.get(SATURDAY).getTypeEleven(), false);
                    elevenText.setText(settings.get(SATURDAY).getLocEleven());
                    twelve.setSelection(settings.get(SATURDAY).getPosTwelve(), false);
                    twelveType.setSelection(settings.get(SATURDAY).getTypeTwelve(), false);
                    twelveText.setText(settings.get(SATURDAY).getLocTwelve());
                    two.setSelection(settings.get(SATURDAY).getPosTwo(), false);
                    twoType.setSelection(settings.get(SATURDAY).getTypeTwo(), false);
                    twoText.setText(settings.get(SATURDAY).getLocTwo());
                    three.setSelection(settings.get(SATURDAY).getPosThree(), false);
                    threeType.setSelection(settings.get(SATURDAY).getTypeThree(), false);
                    threeText.setText(settings.get(SATURDAY).getLocThree());
                    four.setSelection(settings.get(SATURDAY).getPosFour(), false);
                    fourType.setSelection(settings.get(SATURDAY).getTypeFour(), false);
                    fourText.setText(settings.get(SATURDAY).getLocFour());
                    five.setSelection(settings.get(SATURDAY).getPosFive(), false);
                    fiveType.setSelection(settings.get(SATURDAY).getTypeFive(), false);
                    fiveText.setText(settings.get(SATURDAY).getLocFive());
                }

            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        SpinnerInteractionListener ninelistner = new SpinnerInteractionListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                settings.get(Day.getSelectedItemPosition()).setTypeNine(pos);
                if (SpinnerInteractionListener.userSelect) {
                    settings.get(Day.getSelectedItemPosition()).setPosNine(pos);
                    if (nine.getSelectedItem().toString().contains("LAB")) {
                        nineType.setSelection(3, false);
                    } else if (pos == 0)
                        nine.setSelection(0, false);
                    else
                        nine.setSelection(1, false);
                    SpinnerInteractionListener.userSelect = false;
                }
            }
        };
        nineType.setOnTouchListener(ninelistner);
        nineType.setOnItemSelectedListener(ninelistner);

        SpinnerInteractionListener tenlistner = new SpinnerInteractionListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                settings.get(Day.getSelectedItemPosition()).setTypeTen(pos);
                if (SpinnerInteractionListener.userSelect) {
                    settings.get(Day.getSelectedItemPosition()).setPosTen(pos);
                    if (ten.getSelectedItem().toString().contains("LAB")) {
                        tenType.setSelection(3, false);
                    } else if (pos == 0)
                        ten.setSelection(0, false);
                    else
                        ten.setSelection(1, false);
                    SpinnerInteractionListener.userSelect = false;
                }
            }
        };
        tenType.setOnTouchListener(tenlistner);
        tenType.setOnItemSelectedListener(tenlistner);

        SpinnerInteractionListener elevenlistner = new SpinnerInteractionListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                settings.get(Day.getSelectedItemPosition()).setTypeEleven(pos);
                if (SpinnerInteractionListener.userSelect) {
                    settings.get(Day.getSelectedItemPosition()).setPosEleven(pos);
                    if (eleven.getSelectedItem().toString().contains("LAB")) {
                        elevenType.setSelection(3, false);
                    } else if (pos == 0)
                        eleven.setSelection(0, false);
                    else
                        eleven.setSelection(1, false);
                    SpinnerInteractionListener.userSelect = false;
                }
            }
        };
        elevenType.setOnTouchListener(elevenlistner);
        elevenType.setOnItemSelectedListener(elevenlistner);

        SpinnerInteractionListener twelvelistner = new SpinnerInteractionListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                settings.get(Day.getSelectedItemPosition()).setTypeTwelve(pos);
                if (SpinnerInteractionListener.userSelect) {
                    settings.get(Day.getSelectedItemPosition()).setPosTwelve(pos);
                    if (twelve.getSelectedItem().toString().contains("LAB")) {
                        twelveType.setSelection(3, false);
                    } else if (pos == 0)
                        twelve.setSelection(0, false);
                    else
                        twelve.setSelection(1, false);
                    SpinnerInteractionListener.userSelect = false;
                }
            }
        };
        twelveType.setOnTouchListener(twelvelistner);
        twelveType.setOnItemSelectedListener(twelvelistner);

        SpinnerInteractionListener twolistner = new SpinnerInteractionListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                settings.get(Day.getSelectedItemPosition()).setTypeTwo(pos);
                if (SpinnerInteractionListener.userSelect) {
                    settings.get(Day.getSelectedItemPosition()).setPosTwo(pos);
                    if (two.getSelectedItem().toString().contains("LAB")) {
                        twoType.setSelection(3, false);
                    } else if (pos == 0)
                        two.setSelection(0, false);
                    else
                        two.setSelection(1, false);
                    SpinnerInteractionListener.userSelect = false;
                }
            }
        };
        twoType.setOnTouchListener(twolistner);
        twoType.setOnItemSelectedListener(twolistner);

        SpinnerInteractionListener threelistner = new SpinnerInteractionListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                settings.get(Day.getSelectedItemPosition()).setTypeThree(pos);
                if (SpinnerInteractionListener.userSelect) {
                    settings.get(Day.getSelectedItemPosition()).setPosThree(pos);
                    if (three.getSelectedItem().toString().contains("LAB")) {
                        threeType.setSelection(3, false);
                    } else if (pos == 0)
                        three.setSelection(0, false);
                    else
                        three.setSelection(1, false);
                    SpinnerInteractionListener.userSelect = false;
                }
            }
        };
        threeType.setOnTouchListener(threelistner);
        threeType.setOnItemSelectedListener(threelistner);


        SpinnerInteractionListener fourlistner = new SpinnerInteractionListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                settings.get(Day.getSelectedItemPosition()).setTypeFour(pos);
                if (SpinnerInteractionListener.userSelect) {
                    settings.get(Day.getSelectedItemPosition()).setPosFour(pos);
                    if (four.getSelectedItem().toString().contains("LAB")) {
                        fourType.setSelection(3, false);
                    } else if (pos == 0)
                        four.setSelection(0, false);
                    else
                        four.setSelection(1, false);
                    SpinnerInteractionListener.userSelect = false;
                }
            }
        };
        fourType.setOnTouchListener(fourlistner);
        fourType.setOnItemSelectedListener(fourlistner);

        SpinnerInteractionListener fivelistner = new SpinnerInteractionListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                settings.get(Day.getSelectedItemPosition()).setTypeFive(pos);
                if (SpinnerInteractionListener.userSelect) {
                    settings.get(Day.getSelectedItemPosition()).setPosFive(pos);
                    if (five.getSelectedItem().toString().contains("LAB")) {
                        fiveType.setSelection(3, false);
                    } else if (pos == 0)
                        five.setSelection(0, false);
                    else
                        five.setSelection(1, false);
                    SpinnerInteractionListener.userSelect = false;
                }
            }
        };
        fiveType.setOnTouchListener(fivelistner);
        fiveType.setOnItemSelectedListener(fivelistner);


        SpinnerInteractionListener nineTypeListner = new SpinnerInteractionListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                settings.get(Day.getSelectedItemPosition()).setTypeNine(pos);
                if (SpinnerInteractionListener.userSelect || settings.get(Day.getSelectedItemPosition()).getLocNine().isEmpty()) {
                    if (nineType.getSelectedItemPosition() == 1)
                        nineText.setText(R.string.lt);
                    else if (nineType.getSelectedItemPosition() == 2)
                        nineText.setText(R.string.cr);
                    else {
                        nineText.setText("");
                    }
                    SpinnerInteractionListener.userSelect = false;
                }
            }
        };
        nineType.setOnTouchListener(nineTypeListner);
        nineType.setOnItemSelectedListener(nineTypeListner);

        SpinnerInteractionListener tenTypeListner = new SpinnerInteractionListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                settings.get(Day.getSelectedItemPosition()).setTypeTen(pos);
                if (SpinnerInteractionListener.userSelect || settings.get(Day.getSelectedItemPosition()).getLocTen().isEmpty()) {
                    if (tenType.getSelectedItemPosition() == 1)
                        tenText.setText(R.string.lt);
                    else if (tenType.getSelectedItemPosition() == 2)
                        tenText.setText(R.string.cr);
                    else {
                        tenText.setText("");
                    }
                    SpinnerInteractionListener.userSelect = false;
                }
            }
        };
        tenType.setOnTouchListener(tenTypeListner);
        tenType.setOnItemSelectedListener(tenTypeListner);

        SpinnerInteractionListener elevenTypeListner = new SpinnerInteractionListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                settings.get(Day.getSelectedItemPosition()).setTypeEleven(pos);
                if (SpinnerInteractionListener.userSelect || settings.get(Day.getSelectedItemPosition()).getLocEleven().isEmpty()) {
                    if (elevenType.getSelectedItemPosition() == 1)
                        elevenText.setText(R.string.lt);
                    else if (elevenType.getSelectedItemPosition() == 2)
                        elevenText.setText(R.string.cr);
                    else {
                        elevenText.setText("");
                    }
                    SpinnerInteractionListener.userSelect = false;
                }
            }
        };
        elevenType.setOnTouchListener(elevenTypeListner);
        elevenType.setOnItemSelectedListener(elevenTypeListner);

        SpinnerInteractionListener twelveTypeListner = new SpinnerInteractionListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                settings.get(Day.getSelectedItemPosition()).setTypeTwelve(pos);
                if (SpinnerInteractionListener.userSelect || settings.get(Day.getSelectedItemPosition()).getLocTwelve().isEmpty()) {
                    if (twelveType.getSelectedItemPosition() == 1)
                        twelveText.setText(R.string.lt);
                    else if (twelveType.getSelectedItemPosition() == 2)
                        twelveText.setText(R.string.cr);
                    else {
                        twelveText.setText("");
                    }
                    SpinnerInteractionListener.userSelect = false;
                }
            }
        };
        twelveType.setOnTouchListener(twelveTypeListner);
        twelveType.setOnItemSelectedListener(twelveTypeListner);

        SpinnerInteractionListener twoTypeListner = new SpinnerInteractionListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                settings.get(Day.getSelectedItemPosition()).setTypeTwo(pos);
                if (SpinnerInteractionListener.userSelect || settings.get(Day.getSelectedItemPosition()).getLocTwo().isEmpty()) {
                    if (twoType.getSelectedItemPosition() == 1)
                        twoText.setText(R.string.lt);
                    else if (twoType.getSelectedItemPosition() == 2)
                        twoText.setText(R.string.cr);
                    else {
                        twoText.setText("");
                    }
                    SpinnerInteractionListener.userSelect = false;
                }
            }
        };
        twoType.setOnTouchListener(twoTypeListner);
        twoType.setOnItemSelectedListener(twoTypeListner);

        SpinnerInteractionListener threeTypeListner = new SpinnerInteractionListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                settings.get(Day.getSelectedItemPosition()).setTypeThree(pos);
                if (SpinnerInteractionListener.userSelect || settings.get(Day.getSelectedItemPosition()).getLocThree().isEmpty()) {
                    if (threeType.getSelectedItemPosition() == 1)
                        threeText.setText(R.string.lt);
                    else if (threeType.getSelectedItemPosition() == 2)
                        threeText.setText(R.string.cr);
                    else {
                        threeText.setText("");
                    }
                    SpinnerInteractionListener.userSelect = false;
                }
            }
        };
        threeType.setOnTouchListener(threeTypeListner);
        threeType.setOnItemSelectedListener(threeTypeListner);

        SpinnerInteractionListener fourTypeListner = new SpinnerInteractionListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                settings.get(Day.getSelectedItemPosition()).setTypeFour(pos);
                if (SpinnerInteractionListener.userSelect || settings.get(Day.getSelectedItemPosition()).getLocFour().isEmpty()) {
                    if (fourType.getSelectedItemPosition() == 1)
                        fourText.setText(R.string.lt);
                    else if (fourType.getSelectedItemPosition() == 2)
                        fourText.setText(R.string.cr);
                    else {
                        fourText.setText("");
                    }
                    SpinnerInteractionListener.userSelect = false;
                }
            }
        };
        fourType.setOnTouchListener(fourTypeListner);
        fourType.setOnItemSelectedListener(fourTypeListner);

        SpinnerInteractionListener fiveTypeListner = new SpinnerInteractionListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                settings.get(Day.getSelectedItemPosition()).setTypeFive(pos);
                if (SpinnerInteractionListener.userSelect || settings.get(Day.getSelectedItemPosition()).getLocFive().isEmpty()) {
                    if (fiveType.getSelectedItemPosition() == 1)
                        fiveText.setText(R.string.lt);
                    else if (fiveType.getSelectedItemPosition() == 2)
                        fiveText.setText(R.string.cr);
                    else {
                        fiveText.setText("");
                    }
                    SpinnerInteractionListener.userSelect = false;
                }
            }
        };
        fiveType.setOnTouchListener(fiveTypeListner);
        fiveType.setOnItemSelectedListener(fiveTypeListner);

        nineText.addTextChangedListener(new TextWatcherAfter() {
            @Override
            public void afterTextChanged(Editable editable) {
                settings.get(Day.getSelectedItemPosition()).setLocNine(editable.toString());
            }

        });
        tenText.addTextChangedListener(new TextWatcherAfter() {
            @Override
            public void afterTextChanged(Editable editable) {
                settings.get(Day.getSelectedItemPosition()).setLocTen(editable.toString());
            }

        });
        elevenText.addTextChangedListener(new TextWatcherAfter() {
            @Override
            public void afterTextChanged(Editable editable) {
                settings.get(Day.getSelectedItemPosition()).setLocEleven(editable.toString());
            }

        });
        twelveText.addTextChangedListener(new TextWatcherAfter() {
            @Override
            public void afterTextChanged(Editable editable) {
                settings.get(Day.getSelectedItemPosition()).setLocTwelve(editable.toString());
            }

        });
        twoText.addTextChangedListener(new TextWatcherAfter() {
            @Override
            public void afterTextChanged(Editable editable) {
                settings.get(Day.getSelectedItemPosition()).setLocTwo(editable.toString());
            }

        });
        threeText.addTextChangedListener(new TextWatcherAfter() {
            @Override
            public void afterTextChanged(Editable editable) {
                settings.get(Day.getSelectedItemPosition()).setLocThree(editable.toString());
            }

        });
        fourText.addTextChangedListener(new TextWatcherAfter() {
            @Override
            public void afterTextChanged(Editable editable) {
                settings.get(Day.getSelectedItemPosition()).setLocFour(editable.toString());
            }

        });
        fiveText.addTextChangedListener(new TextWatcherAfter() {
            @Override
            public void afterTextChanged(Editable editable) {
                settings.get(Day.getSelectedItemPosition()).setLocFive(editable.toString());
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.timetable_settings_menu, menu);
        return true;

    }

    @SuppressWarnings("UnusedAssignment")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (item.getItemId() == R.id.save_timetable) {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = null;
            image = new File(storageDir, sem + "_" + batch + ".txt");
            if (storageDir != null && !storageDir.exists()) {
                boolean a = storageDir.mkdir();
            }
            ObjectOutput out = null;
            try {
                out = new ObjectOutputStream(new FileOutputStream(image));
                out.writeObject(settings);
                out.close();
                Toast.makeText(this, "Settings Saved Successfully\nUpload Data if Completed", Toast.LENGTH_LONG).show();
                cancelMessAlarms();
                cancelttAlarms();
                cancelmorningalarm();
                Intent intent = new Intent("SetAlarms");
                sendBroadcast(intent);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        uploadtt();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.setMessage("Upload Your Settings For Your BatctchMates\nDo You Want To Upload Your Settings?");
                builder.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (item.getItemId() == R.id.table_upload)


            if (isConnected)
                uploadtt();
            else {
                Toast.makeText(this, "Enable Internet & TryAgain", Toast.LENGTH_LONG).show();

            }
        else if (item.getItemId() == R.id.table_download) {
            final AlertDialog.Builder confirm = new AlertDialog.Builder(TableSettingsActivity.this);
            confirm.setMessage("Your Existing Settings Will Be Replaced\nAre You Sure?");
            confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ConnectivityManager cm =
                            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    boolean isConnected = activeNetwork != null &&
                            activeNetwork.isConnectedOrConnecting();
                    if (isConnected) {
                        DownloadData();
                    }
                    else
                        Toast.makeText(TableSettingsActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();


                }

            });
            confirm.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            confirm.show();
        }

        return true;
    }

    @SuppressWarnings({"unchecked", "UnusedAssignment"})
    public ArrayList<TimeTableData> readSettings() throws Exception {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        final String batch = prefs.getString(getString(R.string.key_batch), "").toUpperCase().trim();
        final String sem = prefs.getString(getString(R.string.key_semester), "").toUpperCase().trim();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File settings = null;
        settings = new File(storageDir, sem + "_" + batch + ".txt");
        ObjectInput ois = null;

        ois = new ObjectInputStream(new FileInputStream(settings));
        ArrayList<TimeTableData> returnlist = (ArrayList<TimeTableData>) ois.readObject();

        ois.close();

        return returnlist;
    }

    private void uploadtt() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String File = "TimeTable/" + sem + "_" + batch + ".txt";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File settings = null;
        settings = new File(storageDir, sem + "_" + batch + ".txt");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Your Settings...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setProgressPercentFormat(null);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        // Get the Uri of the selected file
        Uri file = Uri.fromFile(settings);

        StorageReference riversRef = storageRef.child(File);

        riversRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                try {
                    Toast.makeText(TableSettingsActivity.this, "Upload Success", Toast.LENGTH_LONG).show();
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } catch (NullPointerException pe) {
                    Log.e("table Settings", "progress", pe);
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(TableSettingsActivity.this, "Upload Failed", Toast.LENGTH_LONG).show();
                        try {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        } catch (NullPointerException pe) {
                            Log.e("mess", "progress", pe);
                        }

                    }
                });


    }

    private void DownloadData() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String File = "TimeTable/" + sem + "_" + batch + ".txt";
        StorageReference riversRef = storageRef.child(File);
        File settingsFile = null;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        settingsFile = new File(storageDir, sem + "_" + batch + ".txt");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setProgressPercentFormat(null);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        riversRef.getFile(settingsFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(TableSettingsActivity.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                    cancelMessAlarms();
                    cancelttAlarms();
                    cancelmorningalarm();
                    Intent intent = new Intent("SetAlarms");
                    sendBroadcast(intent);
                    finish();
                } catch (NullPointerException pe) {
                    Log.e("table Settings", "progress", pe);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        String text;
                        if (e.toString().contains(":") && e.toString().contains("Object"))
                            text = e.toString().substring(e.toString().indexOf(":") + 1).replace("Object", "Data");
                        else text = e.toString();
                        Toast.makeText(TableSettingsActivity.this, text, Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException pe) {
                    Log.e("Fetch Data", "progress", pe);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TableSettingsActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setMessage("Unsaved Data Will Be Lost\nDo You Want To Leave?");
        builder.setNeutralButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File image = null;
                image = new File(storageDir, sem + "_" + batch + ".txt");
                if (storageDir != null && !storageDir.exists()) {
                    boolean a = storageDir.mkdir();
                }
                ObjectOutput out = null;
                try {
                    out = new ObjectOutputStream(new FileOutputStream(image));
                    out.writeObject(settings);
                    out.close();
                    Toast.makeText(TableSettingsActivity.this, "Settings Saved Successfully\nUpload Data if Completed", Toast.LENGTH_LONG).show();
                    cancelMessAlarms();
                    cancelttAlarms();
                    cancelmorningalarm();
                    Intent intent = new Intent("SetAlarms");
                    sendBroadcast(intent);
                    AlertDialog.Builder builder = new AlertDialog.Builder(TableSettingsActivity.this);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            uploadtt();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    builder.setMessage("Upload Your Settings For Your BatctchMates\nDo You Want To Upload Your Settings?");
                    builder.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        builder.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
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
            am.cancel(PendingIntent.getBroadcast(this, (i), new Intent(this, AlarmReciever.class).putExtra("title", "Class @ 10:00").putExtra("fragmentno", 1), PendingIntent.FLAG_UPDATE_CURRENT));
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


}
