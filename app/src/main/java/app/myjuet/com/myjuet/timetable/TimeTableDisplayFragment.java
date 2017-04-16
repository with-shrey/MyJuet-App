package app.myjuet.com.myjuet.timetable;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.adapters.TimeTableAdapter;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.data.TimeTableData;

import static app.myjuet.com.myjuet.AttendenceActivity.read;
import static app.myjuet.com.myjuet.timetable.TimeTableFragment.data;
import static app.myjuet.com.myjuet.timetable.TimeTableFragment.list;
import static app.myjuet.com.myjuet.timetable.TableSettingsActivity.MONDAY;
import static app.myjuet.com.myjuet.timetable.TableSettingsActivity.TUESDAY;
import static app.myjuet.com.myjuet.timetable.TableSettingsActivity.WEDNESDAY;
import static app.myjuet.com.myjuet.timetable.TableSettingsActivity.THURSDAY;
import static app.myjuet.com.myjuet.timetable.TableSettingsActivity.FRIDAY;
import static app.myjuet.com.myjuet.timetable.TableSettingsActivity.SATURDAY;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimeTableDisplayFragment extends Fragment {


    public TimeTableDisplayFragment() {
        // Required empty public constructor
    }


    public TimeTableDisplayFragment newInstance(int Day) {
        TimeTableDisplayFragment myFragment = new TimeTableDisplayFragment();

        Bundle args = new Bundle();
        args.putInt("Day", Day);
        myFragment.setArguments(args);

        return myFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View RootView = inflater.inflate(R.layout.fragment_time_table_display, container, false);
        RecyclerView recyclerView = (RecyclerView) RootView.findViewById(R.id.recyclerview_tt);
        int i = getArguments().getInt("Day");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        switch (i) {
            case MONDAY:
                TimeTableAdapter adapter = new TimeTableAdapter(list.get(MONDAY), data, MONDAY);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(layoutManager);

                break;
            case TUESDAY:
                TimeTableAdapter adapter1 = new TimeTableAdapter(list.get(TUESDAY), data, TUESDAY);
                recyclerView.setAdapter(adapter1);
                recyclerView.setLayoutManager(layoutManager);

                break;
            case WEDNESDAY:
                TimeTableAdapter adapter2 = new TimeTableAdapter(list.get(WEDNESDAY), data, WEDNESDAY);
                recyclerView.setAdapter(adapter2);
                recyclerView.setLayoutManager(layoutManager);

                break;
            case THURSDAY:
                TimeTableAdapter adapter3 = new TimeTableAdapter(list.get(THURSDAY), data, THURSDAY);
                recyclerView.setAdapter(adapter3);
                recyclerView.setLayoutManager(layoutManager);

                break;
            case FRIDAY:
                TimeTableAdapter adapter4 = new TimeTableAdapter(list.get(FRIDAY), data, FRIDAY);
                recyclerView.setAdapter(adapter4);
                recyclerView.setLayoutManager(layoutManager);

                break;
            case SATURDAY:
                TimeTableAdapter adapter5 = new TimeTableAdapter(list.get(SATURDAY), data, SATURDAY);
                recyclerView.setAdapter(adapter5);
                recyclerView.setLayoutManager(layoutManager);

                break;
        }
        return RootView;
    }

}
