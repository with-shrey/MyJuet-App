package app.myjuet.com.myjuet;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import app.myjuet.com.myjuet.adapters.DetailsAdapter;
import app.myjuet.com.myjuet.data.AttendenceDetails;

import static android.R.attr.action;
import static app.myjuet.com.myjuet.AttendenceActivity.adapter;
import static app.myjuet.com.myjuet.R.id.Attendence;

public class AttendenceDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence_details);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        double Attendence = Integer.parseInt(prefs.getString(getString(R.string.preferedAttendence), getString(R.string.defaultattendence)));
        double t = Attendence / 100;

        int pa = Integer.parseInt(AttendenceActivity.tempData.getmCountPresent()) + Integer.parseInt(AttendenceActivity.tempData.getmCountAbsent());

        int p = Integer.parseInt(AttendenceActivity.tempData.getmCountPresent());
        int res;
        String ClassText;
        double classes;
        if (Integer.parseInt(AttendenceActivity.tempData.getmLecTut()) < Attendence) {
            classes = Math.ceil(((t * pa) - p) / (1 - t));
            res = (int) classes;
            ClassText = "You Need To Attend " + String.valueOf(res) + " Classes To Reach Threshold " + String.valueOf(Attendence) + " %";
        } else if (Integer.parseInt(AttendenceActivity.tempData.getmLecTut()) == Attendence) {
            ClassText = "Don't Leave Class";
        } else {
            classes = Math.floor((p - (t * pa)) / (t)) - 1;
            res = (int) classes;
            if (res < 0) {
                res = 0;
                ClassText = "Don't Leave Class";

            } else {
                ClassText = "Hurray!! You Can Leave " + classes + " Classes\n I suggest NOT to leave a class!!\n";
            }

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(AttendenceActivity.tempData.getmName());
        getSupportActionBar().setElevation(5);
        TextView Present = (TextView) findViewById(R.id.present);
        TextView Absent = (TextView) findViewById(R.id.absent);
        TextView leaving = (TextView) findViewById(R.id.leavingdetails);
        TextView nextattend = (TextView) findViewById(R.id.nextdetails);
        TextView lt = (TextView) findViewById(R.id.lecandtut);
        TextView l = (TextView) findViewById(R.id.lec);
        TextView tut = (TextView) findViewById(R.id.tut);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scroller);
        TextView classesno = (TextView) findViewById(R.id.noofclasses);

        classesno.setText(ClassText);
        Present.setText(AttendenceActivity.tempData.getmCountPresent());
        Absent.setText(AttendenceActivity.tempData.getmCountAbsent());
        leaving.setText(AttendenceActivity.tempData.getmOnLeaving());
        nextattend.setText(AttendenceActivity.tempData.getmOnNext());
        lt.setText(AttendenceActivity.tempData.getmLecTut());
        l.setText(AttendenceActivity.tempData.getmLec());
        tut.setText(AttendenceActivity.tempData.getmTut());

        RecyclerView list = (RecyclerView) findViewById(R.id.listdetails);
        DetailsAdapter adapter = new DetailsAdapter(this, AttendenceActivity.tempData.getmList());
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(this));
        scrollView.smoothScrollTo(0, 0);
    }
}
