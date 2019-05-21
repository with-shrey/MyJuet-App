package app.myjuet.com.myjuet;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import app.myjuet.com.myjuet.adapters.AttendenceAdapter;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.database.AttendenceDataDao;
import app.myjuet.com.myjuet.services.RefreshService;
import app.myjuet.com.myjuet.utilities.Constants;
import app.myjuet.com.myjuet.utilities.SettingsActivity;
import app.myjuet.com.myjuet.vm.AttendenceViewModel;
import app.myjuet.com.myjuet.vm.DrawerViewModel;

import static android.content.Context.ACTIVITY_SERVICE;


@SuppressWarnings({"UnusedAssignment", "unused"})
public class AttendenceFragment extends Fragment {


    //ERROR CONSTANTS
    DrawerViewModel viewModel ;
    public static final int WRONG_CREDENTIALS = 1;
    public static final int HOST_DOWN = 2;
    private static final int NO_INTERNET = 3;
    private static String DateString;
    private ArrayList<AttendenceData> listdata;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View.OnClickListener infoListner;
    private String FabString;
    ImageView image;
    private RecyclerView list;
    private AttendenceAdapter adapter;
    private String Action;
    private AttendenceViewModel mAttendenceViewModel;

    public AttendenceFragment() {
    }

    public void  updateFab(){
        Date dateobj = new Date();
        SimpleDateFormat formattor = new SimpleDateFormat("dd/MMM HH:mm", Locale.getDefault());
        String temp = formattor.format(dateobj);
        temp = temp.substring(0, temp.indexOf(" "));
        if (DateString.substring(0, DateString.indexOf(" ")).equals(temp))
            DateString = "Today " + DateString.substring(DateString.indexOf(" "));

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menuattendence, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                if (!isMyServiceRunning()) {
                    Intent refresh = new Intent("refreshAttendence");
                    refresh.putExtra("manual", true);
                    getActivity().sendBroadcast(refresh);
                    Toast.makeText(getActivity(), "Background Sync Started", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Background Sync Already In Progress\nHave Patience..", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.loginAttendence:
                Intent login = new Intent(getActivity(), SettingsActivity.class);
                startActivity(login);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void handleStatusCode(Constants.Status status){
        if (status != Constants.Status.LOADING){
            swipeRefreshLayout.setRefreshing(false);
        }
        switch (status){
            case LOADING:
                break;
            case SUCCESS:
                viewModel.setFabVisible(true);
                image.setVisibility(View.GONE);
                swipeRefreshLayout.setKeepScreenOn(false);
                ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.magnitude80)));
                ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_info_outline_black_24dp);
                ((DrawerActivity) getActivity()).fab.setOnClickListener(infoListner);
                if (adapter.getItemCount() == 0) {
                    image.setVisibility(View.VISIBLE);
                } else {
                    image.setVisibility(View.GONE);
                }
                FabString = "Synced Today";
                Action = "Refresh";

                break;
            case WEBKIOSK_DOWN:
                viewModel.setFabVisible(true);
                ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.magnitude40)));
                ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_sync_problem_black_24dp);
                FabString = "No Internet";
                ((DrawerActivity) getActivity()).fab.performClick();
                swipeRefreshLayout.setRefreshing(false);
                break;
            case WRONG_PASSWORD:
                viewModel.setFabVisible(true);
                ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.magnitude40)));
                ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_sync_problem_black_24dp);
                FabString = "Wrong Credentials";
                Action = "Login";
                ((DrawerActivity) getActivity()).fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, FabString, Snackbar.LENGTH_LONG).setAction(Action, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent login = new Intent(getActivity(), SettingsActivity.class);
                                startActivity(login);
                                ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.magnitude40)));
                                ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_action_name);
                                ((DrawerActivity) getActivity()).fab.setOnClickListener(infoListner);
                                FabString = "Refresh to Login";
                                Action = "Refresh";
                            }
                        }).show();
                    }
                });
                ((DrawerActivity) getActivity()).fab.performClick();
                swipeRefreshLayout.setRefreshing(false);
                break;
            case NO_INTERNET:
                viewModel.setFabVisible(true);
                ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.magnitude40)));
                ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_sync_problem_black_24dp);
                FabString = "No Internet";
                ((DrawerActivity) getActivity()).fab.performClick();
                swipeRefreshLayout.setRefreshing(false);
                break;
            case FAILED:
                viewModel.setFabVisible(true);
                ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.magnitude40)));
                ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_sync_problem_black_24dp);
                FabString = "Webkiosk Down/Timed Out(3s)";
                ((DrawerActivity) getActivity()).fab.performClick();
                swipeRefreshLayout.setRefreshing(false);
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AttendenceDataDao attendenceDataDao = AppDatabase.newInstance(getActivity()).AttendenceDao();
         mAttendenceViewModel = ViewModelProviders.of(this).get(AttendenceViewModel.class);
        viewModel = ViewModelProviders.of(this).get(DrawerViewModel.class);
        View rootView = inflater.inflate(R.layout.list_view, container, false);
        setHasOptionsMenu(true);

        Action = "Refresh";
        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        DateString = prefs.getString(Constants.DATE,null);
        Date dateobj = new Date();
        SimpleDateFormat formattor = new SimpleDateFormat("dd/MMM HH:mm", Locale.getDefault());
        String temp = formattor.format(dateobj);
        temp = temp.substring(0, temp.indexOf(" "));
        if (DateString != null && DateString.substring(0, DateString.indexOf(" ")).equals(temp)) {
            FabString = "Synced Today";
            ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.magnitude80)));
            ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_info_outline_black_24dp);
        }else if (DateString == null){
            FabString = "Last Synced Never";
            ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.magnitude40)));
            ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_action_name);
        }else{
            FabString = "Last Synced " + DateString;
            ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.magnitude80)));
            ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_info_outline_black_24dp);
        }
        viewModel.setFabVisible(true);
        infoListner = view -> Snackbar.make(view, FabString, Snackbar.LENGTH_LONG).setAction(Action, view1 -> {
            if (!isMyServiceRunning()) {
                Intent refresh = new Intent(getActivity(), RefreshService.class);
                refresh.putExtra("manual", true);
                ActivityCompat.startForegroundService(getActivity(), refresh);
                Toast.makeText(getActivity(), "Background Sync Started\nCheck Progress In Notifications", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getActivity(), "Sync Is Running", Toast.LENGTH_LONG).show();
            }
        }).show();

        ((DrawerActivity) getActivity()).fab.setOnClickListener(infoListner);
        image = rootView.findViewById(R.id.attendence_emptyview);
        list = rootView.findViewById(R.id.list_view);
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!isMyServiceRunning()) {
                Intent refresh = new Intent(getActivity(),RefreshService.class);
                refresh.putExtra("manual", true);
                ActivityCompat.startForegroundService(getActivity(),refresh);
                Toast.makeText(getActivity(), "Background Sync Started\nCheck Progress In Notifications", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Background Sync Already Running\nHave Patience...", Toast.LENGTH_LONG).show();

            }
            swipeRefreshLayout.setRefreshing(false);
        });
        listdata = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(layoutManager);
        adapter = new AttendenceAdapter(getActivity(), listdata);
        list.setAdapter(adapter);
        attendenceDataDao.AttendanceDataObserver().observe(this, attendenceData -> {
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
            if (adapter.getItemCount() == 0) {
                image.setVisibility(View.VISIBLE);
                image.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.attendence_nodata));
                image.setScaleType(ImageView.ScaleType.FIT_XY);

            } else {
                image.setVisibility(View.GONE);
            }
        });

            Context context = getActivity();
            prefs = context.getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
            String user = prefs.getString(getString(R.string.key_enrollment), "");
            String pass = prefs.getString(getString(R.string.key_password), "");
            if (!user.equals("") || !pass.equals("")) {
                listdata.clear();
            list.getRecycledViewPool().clear();
            adapter.notifyDataSetChanged();

            } else {
                Intent login = new Intent(getActivity(), SettingsActivity.class);
                getActivity().finish();
                startActivity(login);
                FabString = "Kindly Refresh To Login";
                ((DrawerActivity) getActivity()).fab.performClick();
            }


        return rootView;
    }

    @SuppressLint("RestrictedApi")
    private void refreshData() {
        image.setVisibility(View.GONE);
        swipeRefreshLayout.setKeepScreenOn(true);
        FabString = DateString;
        Action = "Refresh";
        int error = -1;
        swipeRefreshLayout.setRefreshing(true);
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected && !isMyServiceRunning()) {
            mAttendenceViewModel.loginUser().observe(this,status -> {
                if (status == Constants.Status.SUCCESS){
                    mAttendenceViewModel.startLoading().observe(AttendenceFragment.this,status1 -> {
                        if (status1 == Constants.Status.SUCCESS){
                            swipeRefreshLayout.setRefreshing(false);
                            Date dateobj = new Date();
                            SimpleDateFormat formattor = new SimpleDateFormat("dd/MMM HH:mm", Locale.getDefault());
                            String DateString = formattor.format(dateobj);
                            SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(Constants.DATE,DateString);
                            editor.apply();
                            handleStatusCode(status1);
                            mAttendenceViewModel.loadDetails().observe(AttendenceFragment.this,status2 -> {
                                if (status2 != Constants.Status.SUCCESS) {
                                    handleStatusCode(status2);
                                }
                            });
                        }else{
                            handleStatusCode(status1);
                        }
                    });
                }else{
                    handleStatusCode(status);
                }
            });
            viewModel.setFabVisible(false);

        } else if (isMyServiceRunning()) {
            if (adapter.getItemCount() == 0)
                image.setVisibility(View.VISIBLE);
            else
                image.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.magnitude40)));
            ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_sync_problem_black_24dp);
            FabString = "Sync Already In Progress In Background";
            ((DrawerActivity) getActivity()).fab.performClick();
            error = NO_INTERNET;
            swipeRefreshLayout.setKeepScreenOn(false);
        } else {
            if (adapter.getItemCount() == 0)
                image.setVisibility(View.VISIBLE);
            else
                image.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.magnitude40)));
            ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_sync_problem_black_24dp);
            FabString = "No Internet Connections";
            ((DrawerActivity) getActivity()).fab.performClick();
            error = NO_INTERNET;
            swipeRefreshLayout.setKeepScreenOn(false);

        }

    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("app.myjuet.com.myjuet.services.RefreshService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter!=null)
            adapter.notifyDataSetChanged();
    }
}
