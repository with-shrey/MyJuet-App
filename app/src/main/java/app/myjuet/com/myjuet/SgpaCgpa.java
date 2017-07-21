package app.myjuet.com.myjuet;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;

import app.myjuet.com.myjuet.data.SgpaData;
import app.myjuet.com.myjuet.utilities.webUtilities;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static app.myjuet.com.myjuet.R.string.url;


/**
 * A simple {@link Fragment} subclass.
 */
public class SgpaCgpa extends Fragment {
    private ArrayList<SgpaData> data;
    private SwipeRefreshLayout refreshLayout;

    public SgpaCgpa() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        data = new ArrayList<>();
        // Inflate the layout for this fragment
        View RootView = inflater.inflate(R.layout.fragment_sgpa_cgpa, container, false);
        GraphView graph = (GraphView) RootView.findViewById(R.id.graphsgpa);
        refreshLayout = (SwipeRefreshLayout) RootView.findViewById(R.id.refreshsgpa);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshcg();
            }
        });
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(1, 1),
                new DataPoint(2, 5),
                new DataPoint(3, 8),
                new DataPoint(4, 1),
                new DataPoint(5, 5),
                new DataPoint(6, 8),
                new DataPoint(7, 5),
                new DataPoint(8, 8)
        });
        series.setTitle("CGPA");
        series.setColor(Color.GREEN);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setThickness(8);

        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(1, 1),
                new DataPoint(2, 6),
                new DataPoint(3, 9),
                new DataPoint(4, 1),
                new DataPoint(5, 1),
                new DataPoint(6, 5),
                new DataPoint(7, 10),
                new DataPoint(8, 8)
        });
        series2.setTitle("SGPA");
        series2.setColor(Color.RED);
        series2.setDrawDataPoints(true);
        series2.setDataPointsRadius(10);
        series2.setThickness(8);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(10);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(8);
        graph.setScaleX(1);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Semester");
        graph.getGridLabelRenderer().setVerticalAxisTitle("CGPA/SGPA");
        graph.getGridLabelRenderer().setNumHorizontalLabels(8);
        graph.getGridLabelRenderer().setNumVerticalLabels(11);
        graph.addSeries(series);
        graph.addSeries(series2);

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
        new download().execute(Url, PostParam, cont);
    }

    private class download extends AsyncTask<String, Integer, ArrayList<SgpaData>> {

        @Override
        protected ArrayList<SgpaData> doInBackground(String... strings) {
            CookieHandler.setDefault(new CookieManager());
            try {
                webUtilities.sendPost(strings[0], strings[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String Content = null;
            try {
                Content = webUtilities.GetPageContent(strings[2]);
                Log.v("Sgpa", Content);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return webUtilities.crawlCGPA(Content);
        }

        @Override
        protected void onPostExecute(ArrayList<SgpaData> sgpaDatas) {
            refreshLayout.setRefreshing(false);
            data.clear();
            data.addAll(sgpaDatas);
        }

    }

}
