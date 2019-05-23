package app.myjuet.com.myjuet.fragment;


import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.adapters.ExpandableListAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindYourWayFragment extends Fragment {
    ExpandableListView expListView;
    ExpandableListAdapter listAdapter;
    HashMap<String, List<String>> listDataChild;
    List<String> listDataHeader;

    public FindYourWayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_your_way, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prepareListData();
        this.expListView = view.findViewById(R.id.lv_exp);
        prepareListData();
        this.listAdapter = new ExpandableListAdapter(getActivity(), this.listDataHeader, this.listDataChild);
        this.expListView.setAdapter(this.listAdapter);
        this.expListView.setOnGroupClickListener((parent, v, groupPosition, id) -> false);
        this.expListView.setOnGroupExpandListener(groupPosition -> Toast.makeText(getActivity(), "Search :" + listDataHeader.get(groupPosition), Toast.LENGTH_SHORT).show());
        this.expListView.setOnGroupCollapseListener(groupPosition -> {

        });
        this.expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Dialog d = new Dialog(getActivity());
                TextView tv = new TextView(getActivity());
                tv.setTextSize(20.0f);
                switch (groupPosition) {
                    case 0:
                        switch (childPosition) {
                            case 0:
                                d.setTitle("Cr-1");
                                tv.setText("AB-1(Ramanujam Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 1:
                                d.setTitle("Cr-2");
                                tv.setText("AB-1(Ramanujam Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 2:
                                d.setTitle("Cr-3");
                                tv.setText("AB-1(Ramanujam Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 3:
                                d.setTitle("Cr-4");
                                tv.setText("AB-1(Ramanujam Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 4:
                                d.setTitle("Cr-5");
                                tv.setText("AB-1(Ramanujam Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 5:
                                break;
                            case 6:
                                d.setTitle("Cr-7");
                                tv.setText("AB-1(Ramanujam Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 7:
                                d.setTitle("Cr-8");
                                tv.setText("AB-1(Ramanujam Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 8:
                                d.setTitle("Cr-9");
                                tv.setText("AB-2(Raman Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 9:
                                d.setTitle("Cr-10");
                                tv.setText("AB-2(Raman Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 10:
                                d.setTitle("Cr-11");
                                tv.setText("AB-2(Raman Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 11:
                                d.setTitle("Cr-12");
                                tv.setText("AB-2(Raman Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 12:
                                d.setTitle("Cr-14");
                                tv.setText("AB-2(Raman Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 13:
                                d.setTitle("Cr-15");
                                tv.setText("AB-2(Raman Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 14:
                                d.setTitle("Cr-16");
                                tv.setText("AB-2(Raman Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 15:
                                d.setTitle("Cr-17");
                                tv.setText("AB-2(Raman Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 16:
                                d.setTitle("Cr-18");
                                tv.setText("AB-2(Raman Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 17:
                                d.setTitle("Cr-19");
                                tv.setText("AB-2(Raman Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 18:
                                d.setTitle("Cr-20");
                                tv.setText("AB-3(Vishveswarya Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 19:
                                d.setTitle("Cr-21");
                                tv.setText("AB-3(Vishveswarya Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 20:
                                d.setTitle("Cr-22");
                                tv.setText("AB-3(Vishveswarya Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 21:
                                d.setTitle("Cr-23");
                                tv.setText("AB-3(Vishveswarya Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 22:
                                d.setTitle("Cr-24");
                                tv.setText("AB-3(Vishveswarya Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 23:
                                break;
                            case 24:
                                d.setTitle("Cr-26");
                                tv.setText("AB-3(Vishveswarya Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 25:
                                d.setTitle("Cr-27");
                                tv.setText("AB-3(Vishveswarya Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 26:
                                d.setTitle("Cr-28");
                                tv.setText("AB-3(Vishveswarya Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 27:
                                d.setTitle("Cr-29");
                                tv.setText("AB-3(Vishveswarya Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 28:
                                d.setTitle("Cr-30");
                                tv.setText("AB-3(Vishveswarya Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 29:
                                d.setTitle("Cr-31");
                                tv.setText("AB-3(Vishveswarya Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 30:
                                d.setTitle("Cr-32");
                                tv.setText("AB-3(Vishveswarya Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                        }
                        break;
                    case 1:
                        switch (childPosition) {
                            case 0:
                                d.setTitle("Computer Lab-1");
                                tv.setText("AB-1(Ramanujan Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 1:
                                d.setTitle("Computer Lab-2");
                                tv.setText("AB-1(Ramanujan Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 2:
                                d.setTitle("Computer Lab-3");
                                tv.setText("AB-1(Ramanujan Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 3:
                                d.setTitle("Computer Lab-4");
                                tv.setText("AB-1(Ramanujan Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 4:
                                d.setTitle("Computer Lab-5");
                                tv.setText("AB-1(Ramanujan Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 5:
                                d.setTitle("Communication Lab-1");
                                tv.setText("AB-1(Ramanujan Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 6:
                                d.setTitle("Communication Lab-2");
                                tv.setText("AB-1(Ramanujan Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case MotionEventCompat.ACTION_HOVER_MOVE /*7*/:
                                d.setTitle("Physics Lab-1");
                                tv.setText("AB-1(Ramanujan Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 8:
                                d.setTitle("Physics Lab-2");
                                tv.setText("AB-2(Raman Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 9:
                                d.setTitle("Mass Transfer Lab-1");
                                tv.setText("AB-2(Raman Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 10:
                                d.setTitle("Mass Transfer Lab-2");
                                tv.setText("AB-2(Raman Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 11:
                                d.setTitle("Heat Transfer Lab-1");
                                tv.setText("AB-2(Raman Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 12:
                                d.setTitle("Heat Transfer Lab-2");
                                tv.setText("AB-2(Raman Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 13:
                                d.setTitle("DSP Lab");
                                tv.setText("AB-1(Ramanujan Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 14:
                                d.setTitle("VLSI Lab");
                                tv.setText("AB-1(Ramanujan Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 15:
                                d.setTitle("Power Electronics Lab");
                                tv.setText("AB-1(Ramanujan Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 16:
                                d.setTitle("Anolog Lab(ECE)");
                                tv.setText("AB-1(Ramanujan Bhawan)Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 17:
                                d.setTitle("Language Lab");
                                tv.setText("AB-2(Raman Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 18:
                                d.setTitle("Simulation Lab");
                                tv.setText("AB-2(Raman Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 19:
                                d.setTitle("Instrumentation Lab");
                                tv.setText("AB-2(Raman Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 20:
                                d.setTitle("Thermodynamic Lab & Chemical Store");
                                tv.setText("AB-2(Raman Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 21:
                                d.setTitle("Chemistry Lab & Research Lab");
                                tv.setText("AB-2(Raman Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 22:
                                d.setTitle("Chemical Reaction Lab & Environmental Lab");
                                tv.setText("AB-2(Raman Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 23:
                                d.setTitle("Solid Fluid Lab");
                                tv.setText("AB-2(Raman Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 24:
                                d.setTitle("Boiler Room");
                                tv.setText("AB-2(Raman Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 25:
                                d.setTitle("Thermal Power Project Simulator");
                                tv.setText("AB-3(Vishveswarya Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                        }
                        break;
                    case 2:
                        switch (childPosition) {
                            case 0:
                                d.setTitle("CSE Faculty Room 1,2,3");
                                tv.setText("AB-1(Ramanujan Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 1:
                                d.setTitle("ECE Faculty Room 1,2,3");
                                tv.setText("AB-1(Ramanujan Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 2:
                                d.setTitle("ECE Faculty Room 4,5");
                                tv.setText("AB-1(Ramanujan Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 3:
                                d.setTitle("Physics Faculty Room 1,2");
                                tv.setText("AB-1(Ramanujan Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 4:
                                d.setTitle("CHE Faculty Room 1,2,3");
                                tv.setText("AB-2(Raman Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 5:
                                d.setTitle("PD Faculty Room 1");
                                tv.setText("AB-2(Raman Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 6:
                                d.setTitle("PD Faculty Room 2");
                                tv.setText("AB-2(Raman Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case MotionEventCompat.ACTION_HOVER_MOVE /*7*/:
                                d.setTitle("CHE Faculty Room 4,5,6");
                                tv.setText("AB-2(Raman Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 8:
                                d.setTitle("CE Faculty Room 1,2,3");
                                tv.setText("AB-2(Raman Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 9:
                                d.setTitle("MIX Faculty Room 1,2,3");
                                tv.setText("AB-2(Raman Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 10:
                                d.setTitle("ME Faculty Room 1,2,3");
                                tv.setText("AB-3(Vishveswarya Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                        }
                        break;
                    case 3:
                        switch (childPosition) {
                            case 0:
                                d.setTitle("HSS Hod");
                                tv.setText("AB-2(Raman Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 1:
                                d.setTitle("CRDC Hod");
                                tv.setText("AB-1(Ramanujan Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 2:
                                d.setTitle("CSE Hod");
                                tv.setText("AB-1(Ramanujan Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 3:
                                d.setTitle("ECE Hod");
                                tv.setText("AB-1(Ramanujan Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 4:
                                d.setTitle("CHE Hod");
                                tv.setText("AB-2(Raman Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                        }
                        break;
                    case 4:
                        switch (childPosition) {
                            case 0:
                                d.setTitle("Registrar");
                                tv.setText("AB-3(Vishveswarya Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 1:
                                d.setTitle("Chancellor");
                                tv.setText("AB-3(Vishveswarya Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 2:
                                d.setTitle("Vice Chancellor");
                                tv.setText("AB-3(Vishveswarya Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 3:
                                d.setTitle("Staff Conf. Room");
                                tv.setText("AB-3(Vishveswarya Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 4:
                                d.setTitle("Exam Section");
                                tv.setText("AB-3(Vishveswarya Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 5:
                                d.setTitle("Registry");
                                tv.setText("AB-3(Vishveswarya Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 6:
                                d.setTitle("CAFO & Accts");
                                tv.setText("AB-3(Vishveswarya Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case MotionEventCompat.ACTION_HOVER_MOVE /*7*/:
                                d.setTitle("Estate Office");
                                tv.setText("AB-3(Vishveswarya Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 8:
                                d.setTitle("EMI Room");
                                tv.setText("AB-3(Vishveswarya Bhawan) Second Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 9:
                                d.setTitle("Axis Bank ATM");
                                tv.setText("AB-2(Raman Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 10:
                                d.setTitle("Dean (Acad.)");
                                tv.setText("AB-1(Ramanujan Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 11:
                                d.setTitle("Dean (Resch.)");
                                tv.setText("AB-1(Ramanujan Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 12:
                                d.setTitle("Visting Faculty Room");
                                tv.setText("AB-1(Ramanujan Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 13:
                                d.setTitle("OBC/AXIS Bank Counter");
                                tv.setText("AB-1(Ramanujan Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                        }
                        break;
                    case 5:
                        switch (childPosition) {
                            case 0:
                                d.setTitle("LT-1");
                                tv.setText("AB-1(Ramanujan Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 1:
                                d.setTitle("LT-2");
                                tv.setText("AB-1(Ramanujan Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 2:
                                d.setTitle("LT-3");
                                tv.setText("AB-1(Ramanujan Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 3:
                                d.setTitle("LT-4");
                                tv.setText("AB-1(Ramanujan Bhawan) First Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 4:
                                d.setTitle("LT-5");
                                tv.setText("AB-2(Raman Bhawan) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 5:
                                d.setTitle("LT-6");
                                tv.setText("(Near Wind Tunnel) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case 6:
                                d.setTitle("LT-7");
                                tv.setText("(Near Wind Tunnel) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                            case MotionEventCompat.ACTION_HOVER_MOVE /*7*/:
                                d.setTitle("LT-8");
                                tv.setText("(Near Wind Tunnel) Ground Floor!");
                                d.setContentView(tv);
                                d.show();
                                break;
                        }
                        break;
                }
                return false;
            }
        });
    }



    private void prepareListData() {
        this.listDataHeader = new ArrayList<>();
        this.listDataChild = new HashMap();
        this.listDataHeader.add("Cr's");
        this.listDataHeader.add("Lab's");
        this.listDataHeader.add("Faculty Rooms");
        this.listDataHeader.add("Hod's Cabin");
        this.listDataHeader.add("Offices");
        this.listDataHeader.add("Lt's");
        List<String> cr = new ArrayList<>();
        cr.add("CR 1");
        cr.add("CR 2");
        cr.add("CR 3");
        cr.add("CR 4");
        cr.add("CR 5");
        cr.add("CR 6");
        cr.add("CR 7");
        cr.add("CR 8");
        cr.add("CR 9");
        cr.add("CR 10");
        cr.add("CR 11");
        cr.add("CR 12");
        cr.add("CR 14");
        cr.add("CR 15");
        cr.add("CR 16");
        cr.add("CR 17");
        cr.add("CR 18");
        cr.add("CR 19");
        cr.add("CR 20");
        cr.add("CR 21");
        cr.add("CR 22");
        cr.add("CR 23");
        cr.add("CR 24");
        cr.add("CR 25");
        cr.add("CR 26");
        cr.add("CR 27");
        cr.add("CR 28");
        cr.add("CR 29");
        cr.add("CR 30");
        cr.add("CR 31");
        List<String> labs = new ArrayList<>();
        labs.add("Computer Lab-1");
        labs.add("Computer Lab-2");
        labs.add("Computer Lab-3");
        labs.add("Computer Lab-4");
        labs.add("Computer Lab-5");
        labs.add("Communication Lab-1");
        labs.add("Communication Lab-2");
        labs.add("Physics Lab-1");
        labs.add("Physics Lab-2");
        labs.add("Mass Transfer Lab-1");
        labs.add("Mass Transfer Lab-2");
        labs.add("Heat Transfer Lab-1");
        labs.add("Heat Transfer Lab-2");
        labs.add("DSP Lab");
        labs.add("VLSI Lab");
        labs.add("Power Electronics Lab");
        labs.add("Anolog Lab(ECE)");
        labs.add("Language Lab");
        labs.add("Simulation Lab");
        labs.add("Instrumentation Lab");
        labs.add("Thermodynamic Lab & Chemical Store");
        labs.add("Chemistry Lab & Research Lab");
        labs.add("Chemical Reaction Lab & Environmental Lab");
        labs.add("Solid Fluid Lab");
        labs.add("Boiler Room");
        labs.add("Thermal Power Project Simulator");
        List<String> faculty = new ArrayList<>();
        faculty.add("CSE Faculty Room 1,2,3");
        faculty.add("ECE Faculty Room 1,2,3");
        faculty.add("ECE Faculty Room 4,5");
        faculty.add("Physics Faculty Room 1,2");
        faculty.add("CHE Faculty Room 1,2,3");
        faculty.add("PD Faculty Room 1");
        faculty.add("PD Faculty Room 2");
        faculty.add("CHE Faculty Room 4,5,6");
        faculty.add("CE Faculty Room 1,2,3");
        faculty.add("MIX Faculty Room 1,2,3");
        faculty.add("ME Faculty Room 1,2,3");
        List<String> hod = new ArrayList<>();
        hod.add("HSS Hod");
        hod.add("CRDC Hod");
        hod.add("CSE Hod");
        hod.add("ECE Hod");
        hod.add("CHE Hod");
        List<String> office = new ArrayList<>();
        office.add("Registrar");
        office.add("Chancellor");
        office.add("Vice Chancellor");
        office.add("Staff Conf. Room");
        office.add("Exam Section");
        office.add("Registry");
        office.add("CAFO & Accts");
        office.add("Estate Office");
        office.add("EMI Room");
        office.add("Axis Bank Atm");
        office.add("Dean (Acad.)");
        office.add("Dean (Resch.)");
        office.add("Visting Faculty Room");
        office.add("OBC/AXIS Bank Counter");
        List<String> lt = new ArrayList<>();
        lt.add("LT 1");
        lt.add("LT 2");
        lt.add("LT 3");
        lt.add("LT 4");
        lt.add("LT 5");
        lt.add("LT 6");
        lt.add("LT 7");
        lt.add("LT 8");
        this.listDataChild.put(this.listDataHeader.get(0), cr);
        this.listDataChild.put(this.listDataHeader.get(1), labs);
        this.listDataChild.put(this.listDataHeader.get(2), faculty);
        this.listDataChild.put(this.listDataHeader.get(3), hod);
        this.listDataChild.put(this.listDataHeader.get(4), office);
        this.listDataChild.put(this.listDataHeader.get(5), lt);
    }
}
