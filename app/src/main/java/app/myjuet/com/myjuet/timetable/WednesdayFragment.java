package app.myjuet.com.myjuet.timetable;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.adapters.TimeTableAdapter;

import static app.myjuet.com.myjuet.timetable.TableSettingsActivity.MONDAY;
import static app.myjuet.com.myjuet.timetable.TableSettingsActivity.WEDNESDAY;
import static app.myjuet.com.myjuet.timetable.TimeTableFragment.data;
import static app.myjuet.com.myjuet.timetable.TimeTableFragment.list;

/**
 * Created by Shrey on 17-Apr-17.
 */

public class WednesdayFragment extends Fragment {
    int[] info = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.fragment_time_table_display, container, false);
        final RecyclerView recyclerView = (RecyclerView) RootView.findViewById(R.id.recyclerview_tt);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 8; i++) {
                    if (list.get(WEDNESDAY).getPos(i) != 0) {
                        info[info[8]++] = i;

                    }

                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TimeTableAdapter adapter = new TimeTableAdapter(list.get(WEDNESDAY), data, WEDNESDAY, info[8], info);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }
                });
            }
        }).start();


        return RootView;
    }
}
