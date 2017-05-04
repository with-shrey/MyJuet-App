package app.myjuet.com.myjuet.timetable;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;

import app.myjuet.com.myjuet.DrawerActivity;
import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.data.TimeTableData;
import app.myjuet.com.myjuet.utilities.SettingsActivity;

import static android.R.attr.bitmap;
import static android.net.wifi.p2p.nsd.WifiP2pServiceRequest.newInstance;
import static android.widget.Toast.makeText;
import static app.myjuet.com.myjuet.AttendenceFragment.read;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings({"UnusedAssignment", "ConstantConditions", "RedundantCast"})
public class TimeTableFragment extends Fragment {

    public static ArrayList<TimeTableData> list;
    public static String[][] data;
    ViewPager viewPager;
    boolean attendence = false;
    ImageView empty;


    public TimeTableFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        data = new String[15][2];
        View view = inflater.inflate(R.layout.fragment_time_table, container, false);
        Context context = getActivity();
        SharedPreferences prefs = context.getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        final String batch = prefs.getString(getString(R.string.key_batch), "").toUpperCase().trim();
        final String sem = prefs.getString(getString(R.string.key_semester), "").toUpperCase().trim();
        ((DrawerActivity) getActivity()).fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (batch.equals("") || sem.equals("")) {
                    makeText(((DrawerActivity) getActivity()), "Update Personal details", Toast.LENGTH_LONG).show();
                    Intent login = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(login);
                } else {
                    Intent settings = new Intent(getActivity(), TableSettingsActivity.class);
                    if (list.isEmpty())
                        makeText(getContext(), "Start By Pressing Download Button", Toast.LENGTH_LONG).show();
                    startActivity(settings);
                }
            }
        });
        viewPager = (ViewPager) view.findViewById(R.id.viewpager_tt);
        empty = (ImageView) view.findViewById(R.id.empty_image_view_tt);
        setHasOptionsMenu(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<AttendenceData> datatemp = new ArrayList<AttendenceData>();
                try {
                    datatemp = read(getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                attendence = datatemp.isEmpty();
                for (int i = 0; i < datatemp.size(); i++) {
                    data[i][0] = datatemp.get(i).getmName();
                    data[i][1] = datatemp.get(i).getmLecTut();
                }
                datatemp.clear();
                datatemp = null;
                System.gc();
            }
        }).start();


        // Inflate the layout for this fragment
        return view;
    }

    private ArrayList<TimeTableData> readSettings() throws Exception {
        Context context = getActivity();
        SharedPreferences prefs = context.getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        final String batch = prefs.getString(getString(R.string.key_batch), "").toUpperCase().trim();
        final String sem = prefs.getString(getString(R.string.key_semester), "").toUpperCase().trim();
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File settings = null;
        settings = new File(storageDir, sem + "_" + batch + ".txt");
        ObjectInput ois = null;

        ois = new ObjectInputStream(new FileInputStream(settings));
        @SuppressWarnings("unchecked")
        ArrayList<TimeTableData> returnlist = (ArrayList<TimeTableData>) ois.readObject();

        ois.close();

        return returnlist;
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        list.clear();
//        list = null;
//        Runtime.getRuntime().gc();
        System.gc();
    }

    @Override
    public void onResume() {
        super.onResume();
        empty.setVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    list = new ArrayList<>();
                    list = readSettings();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (attendence) {
                            empty.setVisibility(View.VISIBLE);

                            makeText(getContext(), "Refresh Attendence First", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent("refreshAttendence");
                            getActivity().sendBroadcast(intent);
                        } else if (!list.isEmpty()) {
                            empty.setVisibility(View.GONE);

                            viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
                                @Override
                                public android.support.v4.app.Fragment getItem(int position) {

                                    switch (position) {
                                        case TableSettingsActivity.MONDAY:
                                            return new MondayFragment();
                                        case TableSettingsActivity.TUESDAY:
                                            return new TuesdayFragment();
                                        case TableSettingsActivity.WEDNESDAY:
                                            return new WednesdayFragment();
                                        case TableSettingsActivity.THURSDAY:
                                            return new ThursdayFragment();
                                        case TableSettingsActivity.FRIDAY:
                                            return new FridayFragment();
                                        default:
                                            return new SaturdayFragment();

                                    }
                                }

                                @Override
                                public int getCount() {
                                    return 6;
                                }
                            });
                            try {
                                ((DrawerActivity) getActivity()).tabLayout.setupWithViewPager(viewPager);
                                ((DrawerActivity) getActivity()).tabLayout.getTabAt(0).setText("MONDAY");
                                ((DrawerActivity) getActivity()).tabLayout.getTabAt(1).setText("TUESDAY");
                                ((DrawerActivity) getActivity()).tabLayout.getTabAt(2).setText("WEDNESDAY");
                                ((DrawerActivity) getActivity()).tabLayout.getTabAt(3).setText("THURSDAY");
                                ((DrawerActivity) getActivity()).tabLayout.getTabAt(4).setText("FRIDAY");
                                ((DrawerActivity) getActivity()).tabLayout.getTabAt(5).setText("SATURDAY");
                                ((DrawerActivity) getActivity()).tabLayout.getTabAt(getActivity().getIntent().getIntExtra("childfragment", Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) - 2).select();

                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        } else {
                            empty.setVisibility(View.VISIBLE);

                        }
                    }
                });
            }
        }).start();
    }
}
