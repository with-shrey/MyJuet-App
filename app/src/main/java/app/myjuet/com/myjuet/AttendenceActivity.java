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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import app.myjuet.com.myjuet.adapters.AttendenceAdapter;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.web.webUtilities;

import static app.myjuet.com.myjuet.web.webUtilities.AttendenceCrawler;

public class AttendenceActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<AttendenceData>> {

    public static AttendenceAdapter adapter;
    public static AttendenceData tempData;
    TextView EmptyView;
    ProgressBar progressBar;
    private String Url = "https://webkiosk.juet.ac.in/CommonFiles/UserAction.jsp";
    private String PostParam = "txtInst=Institute&InstCode=JUET&txtUType=Member+Type&UserType=S&txtCode=Enrollment No&MemberCode=161B222&txtPIN=Password%2FPin&Password=jaishriram&BTNSubmit=Submit";


    @Override
    public Loader<ArrayList<AttendenceData>> onCreateLoader(int i, Bundle bundle) {
        return new AttendenceLoader(this, Url, PostParam);

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<AttendenceData>> loader, ArrayList<AttendenceData> AttendenceDatas) {
        adapter.clear();
        if (adapter != null)
            adapter.addAll(AttendenceDatas);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<AttendenceData>> loader) {
        adapter.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        ListView list = (ListView) findViewById(R.id.list_view);
        EmptyView = (TextView) findViewById(R.id.emptyview_main);
        progressBar = (ProgressBar) findViewById(R.id.progress_main);
        adapter = new AttendenceAdapter(AttendenceActivity.this, new ArrayList<AttendenceData>());
        list.setAdapter(adapter);
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
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tempData = adapter.getItem(i);
                Intent intent = new Intent(AttendenceActivity.this, AttendenceDetailsActivity.class);
                startActivity(intent);
            }
        });
    }


}
