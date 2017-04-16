package app.myjuet.com.myjuet.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import app.myjuet.com.myjuet.AttendenceActivity;
import app.myjuet.com.myjuet.AttendenceDetailsActivity;
import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.data.TimeTableData;
import app.myjuet.com.myjuet.timetable.TimeTableFragment;

import static android.R.attr.data;
import static app.myjuet.com.myjuet.AttendenceActivity.tempData;
import static app.myjuet.com.myjuet.timetable.TimeTableFragment.count;

/**
 * Created by Shrey on 16-Apr-17.
 */

public class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.ViewHolder> {
    int day;
    private TimeTableData list;
    private ArrayList<AttendenceData> dataAttendence;
    // Store the context for easy access

    public TimeTableAdapter(TimeTableData dataTT, ArrayList<AttendenceData> MdataAttendence, int i) {
        list = dataTT;
        dataAttendence = MdataAttendence;
        day = i;
    }


    @Override
    public TimeTableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.timetable_list_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(TimeTableAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position

        // Set item views based on your views and data model
        TextView Name = viewHolder.Name;
        TextView Location = viewHolder.Location;
        TextView Time = viewHolder.Time;
        TextView percent = viewHolder.progressValue;
        TextView Type = viewHolder.Type;
        CardView cardView = viewHolder.cardView;
        ProgressBar Progress = viewHolder.Progress;

        if (position == 0 && list.getPosNine() != 0) {
            Name.setText(dataAttendence.get(list.getPosNine()).getmName());
            Location.setText(list.getLocNine());
            Type.setText(typeGiver(list.getTypeNine()));
            percent.setText(dataAttendence.get(list.getPosNine()).getmLecTut() + "%");
            Time.setText(R.string.time_9);
            Progress.setProgress(Integer.valueOf(dataAttendence.get(list.getPosNine()).getmLecTut()));
        } else if (position == 1 && list.getPosTen() != 0) {
            Name.setText(dataAttendence.get(list.getPosTen()).getmName());
            percent.setText(dataAttendence.get(list.getPosTen()).getmLecTut() + "%");

            Location.setText(list.getLocTen());
            Type.setText(typeGiver(list.getTypeTen()));

            Time.setText(R.string.time_9);
            Progress.setProgress(Integer.valueOf(dataAttendence.get(list.getPosTen()).getmLecTut()));
        } else if (position == 2 && list.getPosEleven() != 0) {
            Name.setText(dataAttendence.get(list.getPosEleven()).getmName());
            percent.setText(dataAttendence.get(list.getPosEleven()).getmLecTut() + "%");

            Location.setText(list.getLocEleven());
            Type.setText(typeGiver(list.getTypeEleven()));

            Time.setText(R.string.time_9);
            Progress.setProgress(Integer.valueOf(dataAttendence.get(list.getPosEleven()).getmLecTut()));
        } else if (position == 3 && list.getPosTwelve() != 0) {
            Name.setText(dataAttendence.get(list.getPosTwelve()).getmName());
            percent.setText(dataAttendence.get(list.getPosTwelve()).getmLecTut() + "%");

            Location.setText(list.getLocTwelve());
            Type.setText(typeGiver(list.getTypeTwelve()));

            Time.setText(R.string.time_9);
            Progress.setProgress(Integer.valueOf(dataAttendence.get(list.getPosTwelve()).getmLecTut()));
        } else if (position == 4 && list.getPosTwo() != 0) {
            Name.setText(dataAttendence.get(list.getPosTwo()).getmName());
            percent.setText(dataAttendence.get(list.getPosTwo()).getmLecTut() + "%");

            Location.setText(list.getLocTwo());
            Type.setText(typeGiver(list.getTypeTwo()));

            Time.setText(R.string.time_9);
            Progress.setProgress(Integer.valueOf(dataAttendence.get(list.getPosTwo()).getmLecTut()));
        } else if (position == 5 && list.getPosThree() != 0) {
            Name.setText(dataAttendence.get(list.getPosThree()).getmName());
            percent.setText(dataAttendence.get(list.getPosThree()).getmLecTut() + "%");

            Location.setText(list.getLocThree());
            Type.setText(typeGiver(list.getTypeThree()));

            Time.setText(R.string.time_9);
            Progress.setProgress(Integer.valueOf(dataAttendence.get(list.getPosThree()).getmLecTut()));
        } else if (position == 6 && list.getPosFour() != 0) {
            Name.setText(dataAttendence.get(list.getPosFour()).getmName());
            percent.setText(dataAttendence.get(list.getPosFour()).getmLecTut() + "%");

            Location.setText(list.getLocFour());
            Type.setText(typeGiver(list.getTypeFour()));

            Time.setText(R.string.time_9);
            Progress.setProgress(Integer.valueOf(dataAttendence.get(list.getPosFour()).getmLecTut()));
        } else if (position == 7 && list.getPosFive() != 0) {
            Name.setText(dataAttendence.get(list.getPosFive()).getmName());
            percent.setText(dataAttendence.get(list.getPosFive()).getmLecTut() + "%");

            Location.setText(list.getLocFive());
            Type.setText(typeGiver(list.getTypeFive()));

            Time.setText(R.string.time_9);
            Progress.setProgress(Integer.valueOf(dataAttendence.get(list.getPosFive()).getmLecTut()));
        } else {
            cardView.setVisibility(View.GONE);
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return 9;
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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView Name;
        public TextView Location;
        public TextView Time;
        public TextView Type;
        public TextView progressValue;
        public CardView cardView;
        public ProgressBar Progress;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardview_tt);
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