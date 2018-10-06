package app.myjuet.com.myjuet;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import app.myjuet.com.myjuet.adapters.DetailsAdapter;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.data.AttendenceDetails;
import app.myjuet.com.myjuet.database.AppDatabase;

import static app.myjuet.com.myjuet.AttendenceFragment.read;


public class AttendenceDetailsActivity extends AppCompatActivity {
    AppDatabase mAppDatabase;
    ArrayList<AttendenceDetails> listdata;
    AttendenceData mAttendenceData;
    @SuppressWarnings("UnusedAssignment")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppDatabase = AppDatabase.newInstance(this);
        setContentView(R.layout.activity_attendence_details);
        AdView mAdView;
        String i = getIntent().getStringExtra("id");
        TextView classesno = (TextView) findViewById(R.id.noofclasses);
        TextView Present = (TextView) findViewById(R.id.present);
        TextView Absent = (TextView) findViewById(R.id.absent);
        TextView leaving = (TextView) findViewById(R.id.leavingdetails);
        TextView nextattend = (TextView) findViewById(R.id.nextdetails);
        TextView lt = (TextView) findViewById(R.id.lecandtut);
        TextView l = (TextView) findViewById(R.id.lec);
        TextView tut = (TextView) findViewById(R.id.tut);
        Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setElevation(5);
        mAppDatabase.AttendenceDao().getAttendenceById(i).observe(this,attendenceData -> {
            mAttendenceData = attendenceData;
             SharedPreferences prefs = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
             double Attendence = Integer.parseInt(prefs.getString(getString(R.string.key_preferred_attendence), "90"));
             double t = Attendence / 100;
             int pa = Integer.parseInt(mAttendenceData.getmCountPresent()) + Integer.parseInt(mAttendenceData.getmCountAbsent());

             int p = Integer.parseInt(mAttendenceData.getmCountPresent());
             int res;
             String ClassText;
             double classes;
             if (!mAttendenceData.getmLecTut().contains("--")) {
                 if (Integer.parseInt(mAttendenceData.getmLecTut()) < Attendence) {
                     classes = Math.ceil(((t * pa) - p) / (1 - t));
                     res = (int) classes;
                     ClassText = "You Need To Attend " + String.valueOf(res) + " Classes To Reach Threshold " + String.valueOf(Attendence) + " %";
                 } else if (Integer.parseInt(mAttendenceData.getmLecTut()) == Attendence) {
                     ClassText = "Don't Leave Class";
                 } else {
                     classes = Math.floor((p - (t * pa)) / (t)) - 1;
                     res = (int) classes;
                     if (res <= 0) {
                         res = 0;
                         ClassText = "Don't Leave Class";

                     } else {
                         ClassText = "You Can Leave " + classes + " Classes And Reach Threshold " + String.valueOf(Attendence) + " %\n I suggest NOT to leave a class!!\n";
                     }

                 }
             } else
                 ClassText = "No Classes Updated Yet!!";

             classesno.setText(ClassText);
            Present.setText(mAttendenceData.getmCountPresent());
            Absent.setText(mAttendenceData.getmCountAbsent());
            leaving.setText(mAttendenceData.getmOnLeaving());
            nextattend.setText(mAttendenceData.getmOnNext());
            lt.setText(mAttendenceData.getmLecTut());
            l.setText(mAttendenceData.getmLec());
            tut.setText(mAttendenceData.getmTut());
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(mAttendenceData.getmName());
         });
        listdata = new ArrayList<>();
        RecyclerView list = (RecyclerView) findViewById(R.id.listdetails);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scroller);
        list.setNestedScrollingEnabled(false);
        DetailsAdapter adapter = new DetailsAdapter(this, listdata);
        list.setAdapter(adapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        list.setLayoutManager(mLayoutManager);
        scrollView.smoothScrollTo(0, 0);
        mAppDatabase.AttendenceDetailsDao().AttendenceDetails(i).observe(this,attendenceDetails -> {
            listdata.clear();
            if (attendenceDetails != null && attendenceDetails.size()>0) {
                listdata.addAll(attendenceDetails);
            }else{
                listdata.clear();
                listdata.add(new AttendenceDetails("N/A","--","--"));
            }
            assert attendenceDetails != null;
            adapter.notifyDataSetChanged();
        });
        mAdView = (AdView) findViewById(R.id.adViewDetails);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

}
