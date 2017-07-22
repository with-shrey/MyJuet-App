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

import static app.myjuet.com.myjuet.AttendenceFragment.read;


public class AttendenceDetailsActivity extends AppCompatActivity {
    @SuppressWarnings("UnusedAssignment")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence_details);
        AdView mAdView;
        int i = getIntent().getIntExtra("listno", 0);
        ArrayList<AttendenceData> listdata = new ArrayList<>();
        try {
            listdata = AttendenceFragment.read(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AttendenceData tempData = listdata.get(i);
        mAdView = (AdView) findViewById(R.id.adViewDetails);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        double Attendence = Integer.parseInt(prefs.getString(getString(R.string.key_preferred_attendence), "90"));
        double t = Attendence / 100;

        int pa = Integer.parseInt(tempData.getmCountPresent()) + Integer.parseInt(tempData.getmCountAbsent());

        int p = Integer.parseInt(tempData.getmCountPresent());
        int res;
        String ClassText;
        double classes;
        if (!tempData.getmLecTut().contains("--")) {
            if (Integer.parseInt(tempData.getmLecTut()) < Attendence) {
                classes = Math.ceil(((t * pa) - p) / (1 - t));
                res = (int) classes;
                ClassText = "You Need To Attend " + String.valueOf(res) + " Classes To Reach Threshold " + String.valueOf(Attendence) + " %";
            } else if (Integer.parseInt(tempData.getmLecTut()) == Attendence) {
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(tempData.getmName());
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
        Present.setText(tempData.getmCountPresent());
        Absent.setText(tempData.getmCountAbsent());
        leaving.setText(tempData.getmOnLeaving());
        nextattend.setText(tempData.getmOnNext());
        lt.setText(tempData.getmLecTut());
        l.setText(tempData.getmLec());
        tut.setText(tempData.getmTut());

        RecyclerView list = (RecyclerView) findViewById(R.id.listdetails);
        list.setNestedScrollingEnabled(false);
        DetailsAdapter adapter = null;
        try {
            adapter = new DetailsAdapter(this, readdetails().get(i));
        } catch (Exception e) {
            e.printStackTrace();
        }
        list.setAdapter(adapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        list.setLayoutManager(mLayoutManager);
        scrollView.smoothScrollTo(0, 0);
    }

    ArrayList<ArrayList<AttendenceDetails>> readdetails() throws Exception {
        File directory = new File(getFilesDir().getAbsolutePath()
                + File.separator + "serlization");
        ObjectInput ois = null;
        ois = new ObjectInputStream(new FileInputStream(directory
                + File.separator + "detailsattendence.srl"));

        @SuppressWarnings("unchecked")
        ArrayList<ArrayList<AttendenceDetails>> returnlist = (ArrayList<ArrayList<AttendenceDetails>>) ois.readObject();
        Log.v("details data", returnlist.get(0).toString());
        ois.close();
        return returnlist;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
        System.gc();
    }
}
