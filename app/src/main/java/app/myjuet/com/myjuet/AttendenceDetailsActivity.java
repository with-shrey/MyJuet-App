package app.myjuet.com.myjuet;

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
        setTitle(AttendenceActivity.tempData.getmName());
        TextView Present = (TextView) findViewById(R.id.present);
        TextView Absent = (TextView) findViewById(R.id.absent);
        TextView leaving = (TextView) findViewById(R.id.leavingdetails);
        TextView nextattend = (TextView) findViewById(R.id.next_details);

        Present.setText(AttendenceActivity.tempData.getmCountPresent());
        Absent.setText(AttendenceActivity.tempData.getmCountAbsent());
        leaving.setText(AttendenceActivity.tempData.getmOnLeaving());
        nextattend.setText(AttendenceActivity.tempData.getmOnNext());

        RecyclerView list = (RecyclerView) findViewById(R.id.listdetails);
        DetailsAdapter adapter = new DetailsAdapter(this, AttendenceActivity.tempData.getmList());
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(this));

    }
}
