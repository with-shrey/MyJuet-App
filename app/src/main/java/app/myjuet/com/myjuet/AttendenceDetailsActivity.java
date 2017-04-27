package app.myjuet.com.myjuet;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import app.myjuet.com.myjuet.adapters.DetailsAdapter;


public class AttendenceDetailsActivity extends AppCompatActivity {
    @SuppressWarnings("UnusedAssignment")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence_details);
        AdView mAdView;
        mAdView = (AdView) findViewById(R.id.adViewDetails);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        double Attendence = Integer.parseInt(prefs.getString(getString(R.string.key_preferred_attendence), "90"));
        double t = Attendence / 100;

        int pa = Integer.parseInt(AttendenceFragment.tempData.getmCountPresent()) + Integer.parseInt(AttendenceFragment.tempData.getmCountAbsent());

        int p = Integer.parseInt(AttendenceFragment.tempData.getmCountPresent());
        int res;
        String ClassText;
        double classes;
        if (Integer.parseInt(AttendenceFragment.tempData.getmLecTut()) < Attendence) {
            classes = Math.ceil(((t * pa) - p) / (1 - t));
            res = (int) classes;
            ClassText = "You Need To Attend " + String.valueOf(res) + " Classes To Reach Threshold " + String.valueOf(Attendence) + " %";
        } else if (Integer.parseInt(AttendenceFragment.tempData.getmLecTut()) == Attendence) {
            ClassText = "Don't Leave Class";
        } else {
            classes = Math.floor((p - (t * pa)) / (t)) - 1;
            res = (int) classes;
            if (res < 0) {
                res = 0;
                ClassText = "Don't Leave Class";

            } else {
                ClassText = "You Can Leave " + classes + " Classes And Reach Threshold " + String.valueOf(Attendence) + " %\n I suggest NOT to leave a class!!\n";
            }

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(AttendenceFragment.tempData.getmName());
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
        Present.setText(AttendenceFragment.tempData.getmCountPresent());
        Absent.setText(AttendenceFragment.tempData.getmCountAbsent());
        leaving.setText(AttendenceFragment.tempData.getmOnLeaving());
        nextattend.setText(AttendenceFragment.tempData.getmOnNext());
        lt.setText(AttendenceFragment.tempData.getmLecTut());
        l.setText(AttendenceFragment.tempData.getmLec());
        tut.setText(AttendenceFragment.tempData.getmTut());

        RecyclerView list = (RecyclerView) findViewById(R.id.listdetails);
        DetailsAdapter adapter = new DetailsAdapter(this, AttendenceFragment.tempData.getmList());
        list.setAdapter(adapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        list.setLayoutManager(mLayoutManager);
        scrollView.smoothScrollTo(0, 0);
    }

}
