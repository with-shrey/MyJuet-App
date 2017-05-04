package app.myjuet.com.myjuet;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import app.myjuet.com.myjuet.adapters.AttendenceAdapter;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.data.AttendenceDetails;
import app.myjuet.com.myjuet.data.ListsReturner;
import app.myjuet.com.myjuet.utilities.SettingsActivity;

import static android.content.Context.ACTIVITY_SERVICE;


@SuppressWarnings({"UnusedAssignment", "unused"})
public class AttendenceFragment extends Fragment implements LoaderManager.LoaderCallbacks<ListsReturner> {


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


    public AttendenceFragment() {
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
    public Loader<ListsReturner> onCreateLoader(int i, Bundle bundle) {
        Context context = getActivity();
        SharedPreferences prefs = context.getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        String Url = "https://webkiosk.juet.ac.in/CommonFiles/UserAction.jsp";
        String user = prefs.getString(getString(R.string.key_enrollment), "").toUpperCase().trim();
        String pass = prefs.getString(getString(R.string.key_password), "");
        String PostParam = "txtInst=Institute&InstCode=JUET&txtuType=Member+Type&UserType=S&txtCode=Enrollment+No&MemberCode=" + user + "&txtPin=Password%2FPin&Password=" + pass + "&BTNSubmit=Submit";
        return new AttendenceLoader(getActivity(), Url, PostParam);

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
                refreshData();
                return true;
            case R.id.loginAttendence:
                Intent login = new Intent(getActivity(), SettingsActivity.class);
                startActivity(login);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @SuppressWarnings("unused")
    @Override
    public void onLoadFinished(Loader<ListsReturner> loader, ListsReturner AttendenceDatas) {
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
        try {


            if (Error == WRONG_CREDENTIALS) {

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


            } else if (Error == HOST_DOWN) {

                ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.magnitude40)));
                ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_sync_problem_black_24dp);
                FabString = "Webkiosk Down/Timed Out(3s)";
                ((DrawerActivity) getActivity()).fab.performClick();
            } else if (Error == -1 && !AttendenceDatas.getDataArrayList().isEmpty() && !AttendenceDatas.getDetailsArrayList().isEmpty()) {

                write(getActivity(), AttendenceDatas.getDataArrayList(), AttendenceDatas.getDetailsArrayList());
                Log.v("details data", AttendenceDatas.getDetailsArrayList().get(0).get(0).getmDate());
                listdata.clear();
                list.getRecycledViewPool().clear();
                adapter.notifyDataSetChanged();
                listdata.addAll(AttendenceDatas.getDataArrayList());
                adapter.notifyDataSetChanged();
                list.getRecycledViewPool().clear();
                if (adapter.getItemCount() == 0) {
                    image.setVisibility(View.VISIBLE);
                } else {
                    image.setVisibility(View.GONE);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<ListsReturner> loader) {
        listdata.clear();
        adapter.notifyDataSetChanged();
        list.getRecycledViewPool().clear();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.list_view, container, false);
        setHasOptionsMenu(true);

        Action = "Refresh";
        DateString = "";
        ((DrawerActivity) getActivity()).fab.setVisibility(View.VISIBLE);
        infoListner = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, FabString, Snackbar.LENGTH_LONG).setAction(Action, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refreshData();
                    }
                }).show();
            }
        };
        listdata = new ArrayList<>();
        ((DrawerActivity) getActivity()).fab.setOnClickListener(infoListner);
        image = (ImageView) rootView.findViewById(R.id.attendence_emptyview);
        list = (RecyclerView) rootView.findViewById(R.id.list_view);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        listdata = new ArrayList<>();
        adapter = new AttendenceAdapter(getActivity(), listdata);
        list.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(layoutManager);
        File directory = new File(getActivity().getFilesDir().getAbsolutePath()
                + File.separator + "serlization" + File.separator + "MessgeScreenList.srl");
        if (directory.isFile()) {

            new Thread(new Runnable() {
                public void run() {
                    try {
                        listdata.addAll(read(getActivity()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    (getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            list.getRecycledViewPool().clear();
                            adapter.notifyDataSetChanged();
                            if (DateString.contains("Today")) {
                                ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.magnitude80)));
                                ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_info_outline_black_24dp);
                                FabString = "Data Synced Today At " + DateString.substring(DateString.indexOf(" "));
                                DateString = FabString;
                            } else {
                                ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.magnitude40)));
                                ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_action_name);
                                if (DateString.contains(" ")) {
                                    FabString = "Data last Synced " + DateString.substring(0, DateString.indexOf(" "));
                                }
                                DateString = "";
                                DateString = FabString;
                            }
                            if (adapter.getItemCount() == 0) {
                                image.setVisibility(View.VISIBLE);
                            } else {
                                image.setVisibility(View.GONE);
                            }

                            ((DrawerActivity) getActivity()).fab.performClick();

                        }
                    });

                }

            }
            ).start();

        } else {
            Context context = getActivity();
            SharedPreferences prefs = context.getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
            String user = prefs.getString(getString(R.string.key_enrollment), "");
            String pass = prefs.getString(getString(R.string.key_password), "");
            if (!user.equals("") || !pass.equals("")) {
                listdata.clear();
            list.getRecycledViewPool().clear();
            adapter.notifyDataSetChanged();
                if (adapter.getItemCount() == 0) {
                    image.setVisibility(View.VISIBLE);
                } else {
                    image.setVisibility(View.GONE);
                }
            } else {
                Intent login = new Intent(getActivity(), SettingsActivity.class);
                startActivity(login);
                FabString = "Kindly Refresh To Login";
                ((DrawerActivity) getActivity()).fab.performClick();
                if (adapter.getItemCount() == 0) {
                    image.setVisibility(View.VISIBLE);
                } else {
                    image.setVisibility(View.GONE);
                }
            }
        }

        return rootView;
    }

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

            CookieHandler.setDefault(new CookieManager());
            final LoaderManager loaderAtt = getLoaderManager();

            if (loaderAtt.getLoader(0) == null)
                loaderAtt.initLoader(0, null, this);
            else
                loaderAtt.restartLoader(0, null, this);
            ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_sync_disabled_black_24dp);
            ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.magnitude60)));
            FabString = "Click Orange Button To Stop";
            Action = "";
            ((DrawerActivity) getActivity()).fab.performClick();
            ((DrawerActivity) getActivity()).fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    swipeRefreshLayout.setRefreshing(false);
                    if (DateString.contains("Today")) {
                        ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.magnitude80)));
                        ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_info_outline_black_24dp);

                    } else
                        ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.magnitude40)));
                    ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_action_name);
                    ((DrawerActivity) getActivity()).fab.setOnClickListener(infoListner);
                    FabString = DateString;
                    Action = "Refresh";
                    loaderAtt.getLoader(0).stopLoading();
                    if (adapter.getItemCount() == 0) {
                        image.setVisibility(View.VISIBLE);
                    } else {
                        image.setVisibility(View.GONE);
                    }
                }
            });
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        LoaderManager loaderManager = getLoaderManager();
        try {
            if (loaderManager.getLoader(0) != null) {
                loaderManager.getLoader(0).stopLoading();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDetach() {
        super.onDetach();
//        listdata.clear();
//        adapter.notifyDataSetChanged();
//        adapter = null;
//        listdata = null;
//        Runtime.getRuntime().gc();
        System.gc();
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
