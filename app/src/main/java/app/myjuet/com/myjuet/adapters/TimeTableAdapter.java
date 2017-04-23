package app.myjuet.com.myjuet.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.data.TimeTableData;


public class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.ViewHolder> {
    private int day;
    private int Count;
    private int[] Detailsdata;
    private TimeTableData list;
    private ArrayList<AttendenceData> dataAttendence;
    // Store the context for easy access

    public TimeTableAdapter(TimeTableData dataTT, ArrayList<AttendenceData> MdataAttendence, int i, int count, int[] detailsData) {
        list = dataTT;
        dataAttendence = MdataAttendence;
        day = i;
        Count = count;
        Detailsdata = detailsData;
    }


    @Override
    public TimeTableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.timetable_list_layout, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(TimeTableAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position

        // Set item views based on your views and data model
        String[] time = new String[]{"9:00", "10:00", "11:00", "12:00", "02:00", "03:00", "04:00", "05:00"};
        TextView Name = viewHolder.Name;
        TextView Location = viewHolder.Location;
        TextView Time = viewHolder.Time;
        TextView percent = viewHolder.progressValue;
        TextView Type = viewHolder.Type;
        ProgressBar Progress = viewHolder.Progress;

        Name.setText(dataAttendence.get(list.getPos(Detailsdata[position]) - 1).getmName());
        Location.setText(list.getLoc(Detailsdata[position]));
        Time.setText(time[Detailsdata[position]]);
        percent.setText(dataAttendence.get(list.getPos(Detailsdata[position]) - 1).getmLecTut());
        Type.setText(typeGiver(list.getType(Detailsdata[position])));
        Progress.setProgress(Integer.valueOf(dataAttendence.get(list.getPos(Detailsdata[position]) - 1).getmLecTut()));

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return Count;
    }

    private String typeGiver(int i) {
        if (i == 0)
            return "N/A";
        else if (i == 1)
            return "Lecture";
        else if (i == 2)
            return "Tutorial";
        else
            return "Lab";
    }

//    public int getmColor(int mLecTut) {
//        if ((mLecTut) >= 0 && (mLecTut) <= 40) {
//            return R.color.magnitude40;
//        } else if ((mLecTut) > 40 && (mLecTut) < 50) {
//            return R.color.magnitude50;
//        } else if ((mLecTut) >= 50 && (mLecTut) < 60) {
//            return R.color.magnitude60;
//        } else if ((mLecTut) >= 60 && (mLecTut) < 70) {
//            return R.color.magnitude6070;
//        } else if ((mLecTut) >= 70 && (mLecTut) < 80) {
//            return R.color.magnitude70;
//        } else if ((mLecTut) >= 80 && (mLecTut) <= 90) {
//            return R.color.magnitude80;
//        } else {
//            return R.color.magnitude90;
//        }
//    }

    @SuppressWarnings("WeakerAccess")
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        private TextView Name;
        private TextView Location;
        private TextView Time;
        private TextView Type;
        private TextView progressValue;
        private ProgressBar Progress;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            progressValue = (TextView) itemView.findViewById(R.id.percent_value_tt);
            Name = (TextView) itemView.findViewById(R.id.subname_tt);
            Location = (TextView) itemView.findViewById(R.id.location_tt);
            Time = (TextView) itemView.findViewById(R.id.time_tt);
            Type = (TextView) itemView.findViewById(R.id.type_tt);
            Progress = (ProgressBar) itemView.findViewById(R.id.progress_tt);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}