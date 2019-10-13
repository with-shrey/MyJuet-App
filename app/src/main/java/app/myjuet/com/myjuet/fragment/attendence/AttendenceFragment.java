package app.myjuet.com.myjuet.fragment.attendence;

import android.app.ActivityManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Bundle;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import javax.inject.Inject;

import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.adapters.AttendenceAdapter;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.database.AttendenceDataDao;
import app.myjuet.com.myjuet.databinding.ListViewBinding;
import app.myjuet.com.myjuet.di.Injectable;
import app.myjuet.com.myjuet.services.RefreshService;
import app.myjuet.com.myjuet.utilities.Constants;
import app.myjuet.com.myjuet.activity.SettingsActivity;

import static android.content.Context.ACTIVITY_SERVICE;


@SuppressWarnings({"UnusedAssignment", "unused"})
public class AttendenceFragment extends Fragment implements Injectable {
    NavController mNavController;
    //ERROR CONSTANTS
    public static final int WRONG_CREDENTIALS = 1;
    public static final int HOST_DOWN = 2;
    private static final int NO_INTERNET = 3;
    private ArrayList<AttendenceData> listdata;
    private String FabString;
    private RecyclerView list;
    private AttendenceAdapter adapter;
    private AttendenceViewModel mAttendenceViewModel;
    @Inject
    ViewModelProvider.Factory mFactory;
    public AttendenceFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menuattendence, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void startRefresh(){
        if (isMyServiceNotRunning()) {
            Intent refresh = new Intent(getActivity(),RefreshService.class);
            refresh.putExtra("manual", true);
            ActivityCompat.startForegroundService(getActivity(),refresh);
            Toast.makeText(getActivity(), "Background Sync Started", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "Background Sync Already In Progress\nHave Patience..", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                startRefresh();
                return true;
            case R.id.loginAttendence:
                mNavController.navigate(R.id.settings_master);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         mAttendenceViewModel = ViewModelProviders.of(this,mFactory).get(AttendenceViewModel.class);
        ListViewBinding binding = DataBindingUtil.inflate(inflater,R.layout.list_view, container, false);
        binding.setVm(mAttendenceViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        View rootView = binding.getRoot();
        setHasOptionsMenu(true);

        try {
            mAttendenceViewModel.getIsLoading().observe(this, (b) -> {
                if (b) {
                    startRefresh();
                    mAttendenceViewModel.setLoading(false);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        list = binding.listView;
        listdata = new ArrayList<>();
        adapter = new AttendenceAdapter(getActivity(), listdata);
        list.setAdapter(adapter);
        mAttendenceViewModel.getAttendenceData().observe(this, attendenceData -> {
            if (attendenceData != null) {
                DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                    @Override
                    public int getOldListSize() {
                        return listdata.size();
                    }

                    @Override
                    public int getNewListSize() {
                        return attendenceData.size();
                    }

                    @Override
                    public boolean areItemsTheSame(int i, int i1) {
                        return  listdata.get(i).getId().equals(attendenceData.get(i1).getId());
                    }

                    @Override
                    public boolean areContentsTheSame(int i, int i1) {
                        return listdata.get(i).equals(attendenceData.get(i1));
                    }
                });
                listdata.clear();
                listdata.addAll(attendenceData);
                result.dispatchUpdatesTo(adapter);
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = Navigation.findNavController(view);
    }

    private boolean isMyServiceNotRunning() {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("app.myjuet.com.myjuet.services.RefreshService".equals(service.service.getClassName())) {
                return false;
            }
        }
        return true;
    }
}
