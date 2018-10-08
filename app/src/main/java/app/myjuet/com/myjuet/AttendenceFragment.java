package app.myjuet.com.myjuet;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import app.myjuet.com.myjuet.adapters.AttendenceAdapter;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.data.AttendenceDetails;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.database.AttendenceDataDao;
import app.myjuet.com.myjuet.services.RefreshService;
import app.myjuet.com.myjuet.utilities.Constants;
import app.myjuet.com.myjuet.utilities.SettingsActivity;
import app.myjuet.com.myjuet.vm.AttendenceViewModel;

import static android.content.Context.ACTIVITY_SERVICE;


@SuppressWarnings({"UnusedAssignment", "unused"})
public class AttendenceFragment extends Fragment {


    //ERROR CONSTANTS
    public static final int WRONG_CREDENTIALS = 1;
    public static final int HOST_DOWN = 2;
    public static final int NO_INTERNET = 3;
    public static int Error = -1;
    private static String DateString;
    ArrayList<AttendenceData> listdata;
    SwipeRefreshLayout swipeRefreshLayout;
    View.OnClickListener infoListner;
    String FabString;
    ImageView image;
    private RecyclerView list;
    private AttendenceAdapter adapter;
    private String Action;
    AttendenceViewModel mAttendenceViewModel;
    AttendenceDataDao mAttendenceDataDao;

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

    @SuppressWarnings("UnusedAssignment")
    public static ArrayList<AttendenceData> read(Context context) throws Exception {
        String filename = "MessgeScreenList.srl";
        String datefile = "date.srl";
        File directory = new File(context.getFilesDir().getAbsolutePath()
                + File.separator + "serlization");
        ObjectInput ois = null;
        ObjectInput dateinput = null;
        ois = new ObjectInputStream(new FileInputStream(directory
                + File.separator + filename));
        dateinput = new ObjectInputStream(new FileInputStream(directory
                + File.separator + datefile));
        @SuppressWarnings("unchecked")
        ArrayList<AttendenceData> returnlist = (ArrayList<AttendenceData>) ois.readObject();
        DateString = (String) dateinput.readObject();
        Date dateobj = new Date();
        SimpleDateFormat formattor = new SimpleDateFormat("dd/MMM HH:mm", Locale.getDefault());
        String temp = formattor.format(dateobj);
        temp = temp.substring(0, temp.indexOf(" "));
        if (DateString.substring(0, DateString.indexOf(" ")).equals(temp))
            DateString = "Today " + DateString.substring(DateString.indexOf(" "));


        ois.close();
        dateinput.close();


        return returnlist;
    }

    @SuppressWarnings("UnusedAssignment")
    public void write(Context context, ArrayList<AttendenceData> nameOfClass, ArrayList<ArrayList<AttendenceDetails>> details) {
        File directoryFile = new File(getActivity().getFilesDir().getAbsolutePath()
                + File.separator + "serlization" + File.separator + "MessgeScreenList.srl");
        boolean deleted;
        if (directoryFile.exists()) {
            deleted = directoryFile.delete();

        }
        File directory = new File(context.getFilesDir().getAbsolutePath()
                + File.separator + "serlization");
        boolean make;
        if (!directory.exists()) {
            make = directory.mkdirs();
        }
        Date dateobj = new Date();
        SimpleDateFormat formattor = new SimpleDateFormat("dd/MMM HH:mm", Locale.getDefault());

        String filename = "MessgeScreenList.srl";
        String date = "date.srl";
        String detailsfile = "detailsattendence.srl";
        ObjectOutput out = null;
        ObjectOutput dateout = null;
        ObjectOutput detailsout = null;
        DateString = formattor.format(dateobj);
        FabString = "Synced Today " + DateString.substring(DateString.indexOf(" "));
        try {
            out = new ObjectOutputStream(new FileOutputStream(directory
                    + File.separator + filename));
            out.flush();
            dateout = new ObjectOutputStream(new FileOutputStream(directory
                    + File.separator + date));
            detailsout = new ObjectOutputStream(new FileOutputStream(directory
                    + File.separator + detailsfile));
            out.writeObject(nameOfClass);
            dateout.flush();
            dateout.writeObject(DateString);
            detailsout.flush();
            detailsout.writeObject(details);
            out.close();
            dateout.close();
            detailsout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    void handleStatusCode(Constants.Status status){
        switch (status){

            case LOADING:
                break;
            case SUCCESS:
                ((DrawerActivity) getActivity()).fab.setVisibility(View.VISIBLE);
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
                ((DrawerActivity) getActivity()).fab.setVisibility(View.VISIBLE);
                ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.magnitude40)));
                ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_sync_problem_black_24dp);
                FabString = "No Internet";
                ((DrawerActivity) getActivity()).fab.performClick();
                swipeRefreshLayout.setRefreshing(false);
                break;
            case WRONG_PASSWORD:
                ((DrawerActivity) getActivity()).fab.setVisibility(View.VISIBLE);
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
                ((DrawerActivity) getActivity()).fab.setVisibility(View.VISIBLE);
                ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.magnitude40)));
                ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_sync_problem_black_24dp);
                FabString = "No Internet";
                ((DrawerActivity) getActivity()).fab.performClick();
                swipeRefreshLayout.setRefreshing(false);
                break;
            case FAILED:
                ((DrawerActivity) getActivity()).fab.setVisibility(View.VISIBLE);
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
            mAttendenceDataDao = AppDatabase.newInstance(getActivity()).AttendenceDao();
         mAttendenceViewModel = ViewModelProviders.of(this).get(AttendenceViewModel.class);

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
        ((DrawerActivity) getActivity()).fab.setVisibility(View.VISIBLE);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setPositiveButton("Background", (dialogInterface, i) -> {
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
            builder.setNegativeButton("Normal Way", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    refreshData();
                }
            });
            builder.setMessage("Choose Desired Method Of Attendence Refresh\nBackground Method Is Recommeded");
            builder.show();


        });
        listdata = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(layoutManager);
        adapter = new AttendenceAdapter(getActivity(), listdata);
        list.setAdapter(adapter);
        mAttendenceDataDao.AttendanceDataObserver().observe(this, attendenceData -> {
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
                list.scrollToPosition(0);
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
    public void refreshData() {
        image.setVisibility(View.GONE);
        swipeRefreshLayout.setKeepScreenOn(true);
        FabString = DateString;
        Action = "Refresh";
        Error = -1;
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
            ((DrawerActivity) getActivity()).fab.setVisibility(View.GONE);

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
            Error = NO_INTERNET;
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
            Error = NO_INTERNET;
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
}
