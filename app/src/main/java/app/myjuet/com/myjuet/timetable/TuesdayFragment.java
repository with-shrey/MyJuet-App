package app.myjuet.com.myjuet.timetable;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.adapters.TimeTableAdapter;
import app.myjuet.com.myjuet.data.TimeTableData;

import static app.myjuet.com.myjuet.timetable.TableSettingsActivity.MONDAY;
import static app.myjuet.com.myjuet.timetable.TableSettingsActivity.TUESDAY;
import static app.myjuet.com.myjuet.timetable.TimeTableFragment.data;
import static app.myjuet.com.myjuet.timetable.TimeTableFragment.list;

/**
 * Created by Shrey on 17-Apr-17.
 */

public class TuesdayFragment extends Fragment {
    int[] info = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View RootView = inflater.inflate(R.layout.fragment_time_table_display, container, false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                info = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
                for (int i = 0; i < 8; i++) {
                    if (list.get(TUESDAY).getPos(i) != 0) {
                        info[info[8]++] = i;
                    }

                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TimeTableAdapter adapter = new TimeTableAdapter(list.get(TUESDAY), data, TUESDAY, info[8], info);
                        RecyclerView recyclerView = (RecyclerView) RootView.findViewById(R.id.recyclerview_tt);
                        recyclerView.getRecycledViewPool().clear();
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }
                });
            }
        }).start();


        return RootView;
    }
}
