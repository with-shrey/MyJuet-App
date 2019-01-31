package app.myjuet.com.myjuet.timetable;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.adapters.TimeTableAdapter;

import static app.myjuet.com.myjuet.timetable.TableSettingsActivity.FRIDAY;
import static app.myjuet.com.myjuet.timetable.TimeTableFragment.data;
import static app.myjuet.com.myjuet.timetable.TimeTableFragment.list;

/**
 * Created by Shrey on 17-Apr-17.
 */

public class FridayFragment extends Fragment {
    int[] info;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View RootView = inflater.inflate(R.layout.fragment_time_table_display, container, false);
                info = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
                for (int i = 0; i < 8; i++) {
                    if (list.get(FRIDAY).getPos(i) != 0) {
                        info[info[8]++] = i;

                    }

                }
                        TimeTableAdapter adapter = new TimeTableAdapter(getActivity(), list.get(FRIDAY), data, FRIDAY, info[8], info);
                        final RecyclerView recyclerView = (RecyclerView) RootView.findViewById(R.id.recyclerview_tt);
                        recyclerView.getRecycledViewPool().clear();
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return RootView;
    }
}
