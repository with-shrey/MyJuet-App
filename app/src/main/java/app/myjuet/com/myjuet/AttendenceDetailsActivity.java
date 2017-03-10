package app.myjuet.com.myjuet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import app.myjuet.com.myjuet.adapters.DetailsAdapter;

import static app.myjuet.com.myjuet.AttendenceActivity.adapter;

public class AttendenceDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence_details);
        TextView Present = (TextView) findViewById(R.id.present);
        TextView Absent = (TextView) findViewById(R.id.absent);
        TextView leaving = (TextView) findViewById(R.id.leavingdetails);
        TextView nextattend = (TextView) findViewById(R.id.next_details);

        Present.setText(AttendenceActivity.tempData.getmCountPresent());
        Absent.setText(AttendenceActivity.tempData.getmCountAbsent());
        leaving.setText(AttendenceActivity.tempData.getmOnLeaving());
        nextattend.setText(AttendenceActivity.tempData.getmOnNext());

        ListView list = (ListView) findViewById(R.id.listdetails);
        DetailsAdapter adapter = new DetailsAdapter(this, AttendenceActivity.tempData.getmList());
        list.setAdapter(adapter);


    }
}
