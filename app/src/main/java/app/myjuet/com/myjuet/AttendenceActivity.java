package app.myjuet.com.myjuet;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.Locale;

import app.myjuet.com.myjuet.adapters.AttendenceAdapter;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.data.AttendenceDetails;
import app.myjuet.com.myjuet.data.TimeTableData;
import app.myjuet.com.myjuet.web.LoginWebkiosk;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;


public class AttendenceActivity extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<AttendenceData>> {


    //ERROR CONSTANTS
    public static final int WRONG_CREDENTIALS = 1;
    public static final int HOST_DOWN = 2;
    public static final int CANCELLED = 4;
    public static final int NO_INTERNET = 3;
    public static ArrayList<AttendenceData> listdata = new ArrayList<>();
    public static AttendenceData tempData;
    public static int Error = -1;
    private static RecyclerView list;
    private static String DateString;
    SwipeRefreshLayout swipeRefreshLayout;
    View.OnClickListener infoListner;
    String FabString;
    private AttendenceAdapter adapter;
    private String Action = "Refresh";


    public AttendenceActivity() {
    }

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
        ArrayList<AttendenceData> returnlist = (ArrayList<AttendenceData>) ois.readObject();
        DateString = (String) dateinput.readObject();
        Date dateobj = new Date();
        SimpleDateFormat formattor = new SimpleDateFormat("dd/MMM HH:mm");
        String temp = formattor.format(dateobj);
        temp = temp.substring(0, temp.indexOf(" "));
        if (DateString.substring(0, DateString.indexOf(" ")).equals(temp))
            DateString = "Today " + DateString.substring(DateString.indexOf(" "));


        ois.close();
        dateinput.close();


        return returnlist;
    }

    public void write(Context context, ArrayList<AttendenceData> nameOfClass) {
        File directory = new File(context.getFilesDir().getAbsolutePath()
                + File.separator + "serlization");

        if (!directory.exists()) {
            directory.mkdirs();
        }
        Date dateobj = new Date();
        SimpleDateFormat formattor = new SimpleDateFormat("dd/MMM HH:mm");

        String filename = "MessgeScreenList.srl";
        String date = "date.srl";
        ObjectOutput out = null;
        ObjectOutput dateout = null;
        DateString = formattor.format(dateobj);
        FabString = "Synced Today " + DateString.substring(DateString.indexOf(" "));
        try {
            out = new ObjectOutputStream(new FileOutputStream(directory
                    + File.separator + filename));
            dateout = new ObjectOutputStream(new FileOutputStream(directory
                    + File.separator + date));
            out.writeObject(nameOfClass);
            dateout.writeObject(DateString);
            out.close();
            dateout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Loader<ArrayList<AttendenceData>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String Url = "https://webkiosk.juet.ac.in/CommonFiles/UserAction.jsp";
        String user = prefs.getString(getString(R.string.enrollment), "").toUpperCase().trim();
        String pass = prefs.getString(getString(R.string.password), "");
        Log.v("User name", user + pass);
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
                Intent login = new Intent(getActivity(), LoginWebkiosk.class);
                startActivity(login);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<AttendenceData>> loader, ArrayList<AttendenceData> AttendenceDatas) {
        swipeRefreshLayout.setKeepScreenOn(false);
        ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.magnitude80)));
        ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_info_outline_black_24dp);
        ((DrawerActivity) getActivity()).fab.setOnClickListener(infoListner);
        FabString = "Synced Today";
        Action = "Refresh";
        try {
            Log.v("Shrey", String.valueOf(Error));


            if (Error == WRONG_CREDENTIALS) {
                ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.magnitude40)));
                ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_sync_problem_black_24dp);
                FabString = "Wrong Credentials";
                Action = "Login";
                ((DrawerActivity) getActivity()).fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, FabString, Snackbar.LENGTH_LONG).setAction(Action, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent login = new Intent(getActivity(), LoginWebkiosk.class);
                                startActivity(login);
                                ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.magnitude40)));
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
                ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.magnitude40)));
                ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_sync_problem_black_24dp);
                FabString = "Webkiosk Down/Timed Out(3s)";
                ((DrawerActivity) getActivity()).fab.performClick();
            } else if (Error == -1 && !AttendenceDatas.isEmpty()) {
                File directoryFile = new File(getActivity().getFilesDir().getAbsolutePath()
                        + File.separator + "serlization" + File.separator + "MessgeScreenList.srl");
                if (directoryFile.exists())
                    directoryFile.delete();
                write(getActivity(), AttendenceDatas);
                listdata.clear();
                adapter.notifyDataSetChanged();
                list.getRecycledViewPool().clear();
                listdata.addAll(AttendenceDatas);
                adapter.notifyDataSetChanged();
                AttendenceActivity.list.getRecycledViewPool().clear();

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<AttendenceData>> loader) {
        listdata.clear();
        adapter.notifyDataSetChanged();
        list.getRecycledViewPool().clear();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_view, container, false);
        setHasOptionsMenu(true);
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
        ((DrawerActivity) getActivity()).fab.setOnClickListener(infoListner);

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
        list.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();
        if (layoutManager.findFirstVisibleItemPosition() == 1) {
            ((DrawerActivity) getActivity()).appBarLayout.setExpanded(true);
        }

        File directory = new File(getActivity().getFilesDir().getAbsolutePath()
                + File.separator + "serlization" + File.separator + "MessgeScreenList.srl");
        if (directory.isFile()) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        listdata.clear();
                        listdata.addAll(read(getActivity()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ((DrawerActivity) getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            list.getRecycledViewPool().clear();
                            adapter.notifyDataSetChanged();
                            if (DateString.contains("Today")) {
                                ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.magnitude80)));
                                ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_info_outline_black_24dp);
                                FabString = "Data Synced Today At " + DateString.substring(DateString.indexOf(" "));
                                DateString = FabString;
                            } else {
                                ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.magnitude40)));
                                ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_action_name);
                                FabString = "Data last Synced " + DateString.substring(0, DateString.indexOf(" "));
                                DateString = FabString;
                            }
                            ((DrawerActivity) getActivity()).fab.performClick();
                        }
                    });

                }

            }
            ).start();

        } else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String user = prefs.getString(getString(R.string.enrollment), "");
            String pass = prefs.getString(getString(R.string.password), "");
            if (!user.equals("") || !pass.equals("")) {
                listdata.clear();
            list.getRecycledViewPool().clear();
            adapter.notifyDataSetChanged();
                refreshData();
            } else {
                Intent login = new Intent(getActivity(), LoginWebkiosk.class);
                startActivity(login);
                FabString = "Kindly Refresh To Login";
                ((DrawerActivity) getActivity()).fab.performClick();
            }
        }

        return rootView;
    }

    public void refreshData() {
        swipeRefreshLayout.setKeepScreenOn(true);
//        if (DateString.contains("Today") && DateString !=null) {
//            ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.magnitude80)));
//            ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_info_outline_black_24dp);
//        }
//        else
//            ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.magnitude40)));
//        ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_action_name);
//        ((DrawerActivity) getActivity()).fab.setOnClickListener(infoListner);
        FabString = DateString;
        Action = "Refresh";
        Error = -1;
        swipeRefreshLayout.setRefreshing(true);
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {

            Log.v("Shrey", "isConnected");
            CookieHandler.setDefault(new CookieManager());
            final LoaderManager loaderAtt = getLoaderManager();

            if (loaderAtt.getLoader(0) == null)
                loaderAtt.initLoader(0, null, this);
            else
                loaderAtt.restartLoader(0, null, this);
            ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_sync_disabled_black_24dp);
            ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.magnitude60)));
            FabString = "Click Orange Button To Stop";
            Action = "";
            ((DrawerActivity) getActivity()).fab.performClick();
            ((DrawerActivity) getActivity()).fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    swipeRefreshLayout.setRefreshing(false);
                    if (DateString.contains("Today")) {
                        ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.magnitude80)));
                        ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_info_outline_black_24dp);

                    } else
                        ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.magnitude40)));
                    ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_action_name);
                    ((DrawerActivity) getActivity()).fab.setOnClickListener(infoListner);
                    FabString = DateString;
                    Action = "Refresh";
                    loaderAtt.getLoader(0).stopLoading();
                }
            });
        } else {
            swipeRefreshLayout.setRefreshing(false);
            ((DrawerActivity) getActivity()).fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.magnitude40)));
            ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_sync_problem_black_24dp);
            FabString = "No Internet Connections";
            ((DrawerActivity) getActivity()).fab.performClick();
            Error = NO_INTERNET;
            swipeRefreshLayout.setKeepScreenOn(false);

        }

    }

}
