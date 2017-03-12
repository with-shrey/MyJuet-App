package app.myjuet.com.myjuet;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import java.util.ArrayList;

import app.myjuet.com.myjuet.adapters.AttendenceAdapter;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.web.webUtilities;

import static app.myjuet.com.myjuet.web.webUtilities.AttendenceCrawler;
import static java.security.AccessController.getContext;

public class AttendenceActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<AttendenceData>> {

    public static AttendenceAdapter adapter;
    public static AttendenceData tempData;
    public static ArrayList<AttendenceData> listdata;


    TextView EmptyView;
    ProgressBar progressBar;
    private String Url = "https://webkiosk.juet.ac.in/CommonFiles/UserAction.jsp";
    private String PostParam = "txtInst=Institute&InstCode=JUET&txtUType=Member+Type&UserType=S&txtCode=Enrollment No&MemberCode=161B222&txtPIN=Password%2FPin&Password=jaishriram&BTNSubmit=Submit";

    public static void write(Context context, ArrayList<AttendenceData> nameOfClass) {
        File directory = new File(context.getFilesDir().getAbsolutePath()
                + File.separator + "serlization");

        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filename = "MessgeScreenList.srl";
        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(new FileOutputStream(directory
                    + File.separator + filename));
            out.writeObject(nameOfClass);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Loader<ArrayList<AttendenceData>> onCreateLoader(int i, Bundle bundle) {
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
                refreshData();
                return true;
            case R.id.clear:
                listdata.clear();
                adapter.notifyDataSetChanged();
                File directoryFile = new File(this.getFilesDir().getAbsolutePath()
                        + File.separator + "serlization" + File.separator + "MessgeScreenList.srl");
                directoryFile.delete();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<AttendenceData>> loader, ArrayList<AttendenceData> AttendenceDatas) {
        write(AttendenceActivity.this, AttendenceDatas);
        listdata.clear();
        adapter.notifyDataSetChanged();
        try {
            listdata.addAll(AttendenceDatas);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<AttendenceData>> loader) {
        listdata.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        RecyclerView list = (RecyclerView) findViewById(R.id.list_view);
        EmptyView = (TextView) findViewById(R.id.emptyview_main);
        progressBar = (ProgressBar) findViewById(R.id.progress_main);
        listdata = new ArrayList<>();
        adapter = new AttendenceAdapter(AttendenceActivity.this, listdata);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(this));
        progressBar.setVisibility(View.GONE);
        File directory = new File(this.getFilesDir().getAbsolutePath()
                + File.separator + "serlization" + File.separator + "MessgeScreenList.srl");
        if (directory.exists()) {
            try {

                listdata.addAll(read(AttendenceActivity.this));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            refreshData();
        }

    }

    private ArrayList<AttendenceData> read(Context context) throws Exception {
        String filename = "MessgeScreenList.srl";
        File directory = new File(context.getFilesDir().getAbsolutePath()
                + File.separator + "serlization");
        ObjectInput ois = null;
        ois = new ObjectInputStream(new FileInputStream(directory
                + File.separator + filename));
        ArrayList<AttendenceData> returnlist = (ArrayList<AttendenceData>) ois.readObject();
        ois.close();


        return returnlist;
    }

    private void refreshData() {
        progressBar.setVisibility(View.VISIBLE);
        File directoryFile = new File(this.getFilesDir().getAbsolutePath()
                + File.separator + "serlization" + File.separator + "MessgeScreenList.srl");
        if (directoryFile.exists())
            directoryFile.delete();
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            CookieHandler.setDefault(new CookieManager());
            LoaderManager loader = getLoaderManager();
            loader.initLoader(0, null, this);
        } else {
            progressBar.setVisibility(View.GONE);
            EmptyView.setText("No Internet Connections");
        }
    }


}
