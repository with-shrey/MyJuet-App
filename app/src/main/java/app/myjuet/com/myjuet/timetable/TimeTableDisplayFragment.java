package app.myjuet.com.myjuet.timetable;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.myjuet.com.myjuet.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimeTableDisplayFragment extends Fragment {


    public TimeTableDisplayFragment() {
        // Required empty public constructor
    }


    public TimeTableDisplayFragment newInstance(String DayName) {
        TimeTableDisplayFragment myFragment = new TimeTableDisplayFragment();

        Bundle args = new Bundle();
        args.putString("Day", DayName);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.fragment_time_table_display, container, false);
        TextView view = (TextView) RootView.findViewById(R.id.fragment_text);
        view.setText(getArguments().getString("Day"));
        return RootView;
    }

}
