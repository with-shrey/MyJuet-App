package app.myjuet.com.myjuet;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import app.myjuet.com.myjuet.adapters.AttendenceAdapter;
import app.myjuet.com.myjuet.adapters.CgpaAdapter;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.data.SgpaData;
import app.myjuet.com.myjuet.utilities.webUtilities;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static app.myjuet.com.myjuet.R.string.url;
import static app.myjuet.com.myjuet.utilities.webUtilities.crawlCGPA;
import static app.myjuet.com.myjuet.utilities.webUtilities.sendPost;


/**
 * A simple {@link Fragment} subclass.
 */
public class SgpaCgpa extends Fragment {
    CgpaAdapter adapter;
    RecyclerView list;
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    LineGraphSeries<DataPoint> series2;
    private ArrayList<SgpaData> data;
    private SwipeRefreshLayout refreshLayout;
    public SgpaCgpa() {
        // Required empty public constructor
    }

    private static boolean pingHost(String host, int port, int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), timeout);
            socket.close();
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        data = new ArrayList<>();
        adapter = new CgpaAdapter(getActivity(), data);
        // Inflate the layout for this fragment
        View RootView = inflater.inflate(R.layout.fragment_sgpa_cgpa, container, false);
        list = (RecyclerView) RootView.findViewById(R.id.sgparecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);
        graph = (GraphView) RootView.findViewById(R.id.graphsgpa);
        ScrollView scroll = (ScrollView) RootView.findViewById(R.id.cgpascroll);
        refreshLayout = (SwipeRefreshLayout) RootView.findViewById(R.id.refreshsgpa);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ConnectivityManager cm =
                        (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (isConnected)
                refreshcg();
                else {
                    refreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            data.addAll(read(getActivity()));
            if (data.isEmpty())
                Toast.makeText(getActivity(), "Swipe Down To Refresh", Toast.LENGTH_LONG).show();
            adapter.notifyDataSetChanged();
            list.getRecycledViewPool().clear();
            series = new LineGraphSeries<>(point2());
            series.setTitle("CGPA");
            series.setColor(Color.rgb(255, 128, 128));

            series.setDrawDataPoints(true);
            series.setDataPointsRadius(7);
            series.setThickness(5);

            series2 = new LineGraphSeries<>(point1());
            series2.setTitle("SGPA");
            series2.setColor(Color.rgb(112, 219, 112));
            series2.setDrawDataPoints(true);
            series2.setDataPointsRadius(7);
            series2.setThickness(5);

            graph.addSeries(series);
            graph.addSeries(series2);
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMinY(graph.getViewport().getMinY(false) - 0.5);
            graph.getViewport().setMaxY(graph.getViewport().getMaxY(false) + 0.5);
        } catch (Exception e) {
            e.printStackTrace();
        }


        graph.setScaleX(1);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Semester");
        graph.getGridLabelRenderer().setNumHorizontalLabels(8);
        graph.getGridLabelRenderer().setGridColor(Color.rgb(179, 236, 255));
        graph.getGridLabelRenderer().setHighlightZeroLines(false);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(8);
        scroll.smoothScrollTo(0, 0);
        return RootView;
    }

    private void refreshcg() {
        Context context = getActivity();
        SharedPreferences prefs = context.getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        String Url = "https://webkiosk.juet.ac.in/CommonFiles/UserAction.jsp";
        String user = prefs.getString(getString(R.string.key_enrollment), "").toUpperCase().trim();
        String pass = prefs.getString(getString(R.string.key_password), "");
        String PostParam = "txtInst=Institute&InstCode=JUET&txtuType=Member+Type&UserType=S&txtCode=Enrollment+No&MemberCode=" + user + "&txtPin=Password%2FPin&Password=" + pass + "&BTNSubmit=Submit";
        String cont = "https://webkiosk.juet.ac.in/StudentFiles/Exam/StudCGPAReport.jsp";
        CookieHandler.setDefault(new CookieManager());
        new download().execute(Url, PostParam, cont);
    }

    public DataPoint[] point1() {
        int n = data.size();
        DataPoint[] point = new DataPoint[n];
        for (int i = 0; i < n; i++) {
            DataPoint v = new DataPoint(data.get(i).getmSem(), data.get(i).getmCgpa());
            point[i] = v;
        }
        return point;
    }

    public DataPoint[] point2() {
        int n = data.size();
        DataPoint[] point = new DataPoint[n];
        for (int i = 0; i < n; i++) {
            DataPoint v = new DataPoint(data.get(i).getmSem(), data.get(i).getmSgpa());
            point[i] = v;
        }
        return point;
    }

    public void writeTofile(Context context, ArrayList<SgpaData> datalist) {
        try {
            File directoryFile = new File(getActivity().getFilesDir().getAbsolutePath()
                    + File.separator + "serlization" + File.separator + "sgpa.srl");
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


        ObjectOutput out = null;

            out = new ObjectOutputStream(new FileOutputStream(directory
                    + File.separator + "sgpa.srl"));
            out.flush();
            out.writeObject(datalist);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<SgpaData> read(Context context) throws Exception {
        String filename = "sgpa.srl";
        File directory = new File(context.getFilesDir().getAbsolutePath()
                + File.separator + "serlization");
        ObjectInput ois = null;

        ois = new ObjectInputStream(new FileInputStream(directory
                + File.separator + filename));
        @SuppressWarnings("unchecked")
        ArrayList<SgpaData> returnlist = (ArrayList<SgpaData>) ois.readObject();

        ois.close();

        return returnlist;
    }

    private class download extends AsyncTask<String, Integer, ArrayList<SgpaData>> {

        @Override
        protected ArrayList<SgpaData> doInBackground(String... strings) {
            ArrayList<SgpaData> list = new ArrayList<>();
            try {
                if (!pingHost("webkiosk.juet.ac.in", 80, 5000)) {
                    return list;
                }
                String temp = webUtilities.sendPost(strings[0], strings[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String Content = null;
            try {
                Content = webUtilities.GetPageContent(strings[2]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                list.addAll(webUtilities.crawlCGPA(Content));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<SgpaData> sgpaDatas) {

            refreshLayout.setRefreshing(false);
            data.clear();
            data.addAll(sgpaDatas);
            adapter.notifyDataSetChanged();
            list.getRecycledViewPool().clear();
            series = new LineGraphSeries<>(point2());
            series.setTitle("CGPA");
            series.setColor(Color.rgb(255, 128, 128));
            series.setDrawDataPoints(true);
            series.setDataPointsRadius(7);
            series.setThickness(5);

            series2 = new LineGraphSeries<>(point1());
            series2.setTitle("SGPA");
            series2.setColor(Color.rgb(112, 219, 112));
            series2.setDrawDataPoints(true);
            series2.setDataPointsRadius(7);
            series2.setThickness(5);

            graph.addSeries(series);
            graph.addSeries(series2);
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMinY(graph.getViewport().getMinY(false) - 0.5);
            graph.getViewport().setMaxY(graph.getViewport().getMaxY(false) + 0.5);
            writeTofile(getActivity(), data);
        }

    }

}
