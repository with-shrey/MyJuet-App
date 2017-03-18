package app.myjuet.com.myjuet;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.myjuet.com.myjuet.adapters.AttendenceAdapter;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.web.LoginWebkiosk;
import app.myjuet.com.myjuet.web.webUtilities;

import static app.myjuet.com.myjuet.web.webUtilities.AttendenceCrawler;
import static java.security.AccessController.getContext;

public class AttendenceActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<AttendenceData>> {

    //ERROR CONSTANTS
    public static final int WRONG_CREDENTIALS = 1;
    public static final int HOST_DOWN = 2;
    public static final int NO_INTERNET = 3;
    public static AttendenceAdapter adapter;
    public static AttendenceData tempData;
    public static ArrayList<AttendenceData> listdata = new ArrayList<>();
    public static RecyclerView list;
    public static int Error = -1;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView EmptyView;
    private ActionBar action;
    private String Url = "https://webkiosk.juet.ac.in/CommonFiles/UserAction.jsp";

    public void write(Context context, ArrayList<AttendenceData> nameOfClass) {
        File directory = new File(context.getFilesDir().getAbsolutePath()
                + File.separator + "serlization");

        if (!directory.exists()) {
            directory.mkdirs();
        }
        Date dateobj = new Date();
        SimpleDateFormat formattor = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        String filename = "MessgeScreenList.srl";
        String date = "date.srl";
        ObjectOutput out = null;
        ObjectOutput dateout = null;
        String dateString = formattor.format(dateobj).toString();
        action.setSubtitle(dateString);
        try {
            out = new ObjectOutputStream(new FileOutputStream(directory
                    + File.separator + filename));
            dateout = new ObjectOutputStream(new FileOutputStream(directory
                    + File.separator + date));
            out.writeObject(nameOfClass);
            dateout.writeObject(formattor.format(dateobj).toString());
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String user = prefs.getString(getString(R.string.enrollment), getString(R.string.defaultuser));
        String pass = prefs.getString(getString(R.string.password), getString(R.string.defaultpassword));
        //TODO:check wheather values are dafault and show error
        String PostParam = "txtInst=Institute&InstCode=JUET&txtUType=Member+Type&UserType=S&txtCode=Enrollment No&MemberCode=" + user + "&txtPIN=Password%2FPin&Password=" + pass + "&BTNSubmit=Submit";
        return new AttendenceLoader(this, Url, PostParam);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menuattendence, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                listdata.clear();
                adapter.notifyDataSetChanged();
                AttendenceActivity.list.getRecycledViewPool().clear();

                refreshData();
                return true;
            case R.id.loginAttendence:
                Intent login = new Intent(this, LoginWebkiosk.class);
                startActivity(login);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<AttendenceData>> loader, ArrayList<AttendenceData> AttendenceDatas) {

        try {
            listdata.clear();
            adapter.notifyDataSetChanged();
            AttendenceActivity.list.getRecycledViewPool().clear();


            Log.v("Shrey", String.valueOf(Error));
            if (Error == WRONG_CREDENTIALS) {
                File directoryFile = new File(this.getFilesDir().getAbsolutePath()
                        + File.separator + "serlization" + File.separator + "MessgeScreenList.srl");
                if (directoryFile.exists())
                    directoryFile.delete();
                EmptyView.setText("Wrong Credentials");
            } else if (Error == HOST_DOWN) {
                listdata.addAll(AttendenceDatas);
                adapter.notifyDataSetChanged();
                AttendenceActivity.list.getRecycledViewPool().clear();

                EmptyView.setText("Webkiosk Down/Timed Out(3s)");
            } else {
                File directoryFile = new File(this.getFilesDir().getAbsolutePath()
                        + File.separator + "serlization" + File.separator + "MessgeScreenList.srl");
                if (directoryFile.exists())
                    directoryFile.delete();
                write(AttendenceActivity.this, AttendenceDatas);
                listdata.addAll(AttendenceDatas);
                adapter.notifyDataSetChanged();
                AttendenceActivity.list.getRecycledViewPool().clear();

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            swipeRefreshLayout.setRefreshing(false);
            loader.reset();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<AttendenceData>> loader) {
        listdata.clear();
        adapter.notifyDataSetChanged();
        list.getRecycledViewPool().clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        action = getSupportActionBar();
        list = (RecyclerView) findViewById(R.id.list_view);
        EmptyView = (TextView) findViewById(R.id.emptyview_main);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listdata.clear();
                refreshData();
            }
        });
        listdata = new ArrayList<>();
        adapter = new AttendenceAdapter(AttendenceActivity.this, listdata);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();
        File directory = new File(this.getFilesDir().getAbsolutePath()
                + File.separator + "serlization" + File.separator + "MessgeScreenList.srl");
        if (directory.exists()) {
            try {
                listdata.clear();

                listdata.addAll(read(AttendenceActivity.this));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            listdata.clear();
            list.getRecycledViewPool().clear();
            adapter.notifyDataSetChanged();
            refreshData();
        }

    }

    @Override
    protected void onStart() {
        list.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();
        super.onStart();
    }

    private ArrayList<AttendenceData> read(Context context) throws Exception {
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
        String dates = (String) dateinput.readObject();
        action.setSubtitle(dates);

        ois.close();
        dateinput.close();


        return returnlist;
    }

    private void refreshData() {
        EmptyView.setText("");
        Error = -1;
        swipeRefreshLayout.setRefreshing(true);
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            Log.v("Shrey", "isConnected");
            CookieHandler.setDefault(new CookieManager());
            LoaderManager loader = getLoaderManager();
            loader.initLoader(0, null, this);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            EmptyView.setText("No Internet Connections");
            Error = NO_INTERNET;
        }
    }


}
