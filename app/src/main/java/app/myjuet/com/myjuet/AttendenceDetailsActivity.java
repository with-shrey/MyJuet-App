package app.myjuet.com.myjuet;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import app.myjuet.com.myjuet.adapters.DetailsAdapter;
import app.myjuet.com.myjuet.data.AttendenceDetails;

import static app.myjuet.com.myjuet.AttendenceActivity.adapter;
import static app.myjuet.com.myjuet.R.id.Attendence;

public class AttendenceDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence_details);
        ActionBar action = getSupportActionBar();
        setTitle(AttendenceActivity.tempData.getmName());
        TextView Present = (TextView) findViewById(R.id.present);
        TextView Absent = (TextView) findViewById(R.id.absent);
        TextView leaving = (TextView) findViewById(R.id.leavingdetails);
        TextView nextattend = (TextView) findViewById(R.id.nextdetails);
        TextView lt = (TextView) findViewById(R.id.lecandtut);
        TextView l = (TextView) findViewById(R.id.lec);
        TextView t = (TextView) findViewById(R.id.tut);

        Present.setText(AttendenceActivity.tempData.getmCountPresent());
        Absent.setText(AttendenceActivity.tempData.getmCountAbsent());
        leaving.setText(AttendenceActivity.tempData.getmOnLeaving());
        nextattend.setText(AttendenceActivity.tempData.getmOnNext());
        lt.setText(AttendenceActivity.tempData.getmLecTut());
        l.setText(AttendenceActivity.tempData.getmLec());
        t.setText(AttendenceActivity.tempData.getmTut());

        RecyclerView list = (RecyclerView) findViewById(R.id.listdetails);
        DetailsAdapter adapter = new DetailsAdapter(this, AttendenceActivity.tempData.getmList());
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(this));

    }
}
