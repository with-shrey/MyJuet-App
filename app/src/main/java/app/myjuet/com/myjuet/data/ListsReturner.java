package app.myjuet.com.myjuet.data;

import java.util.ArrayList;

/**
 * Created by Shrey on 02-May-17.
 */

public class ListsReturner {
    private ArrayList<AttendenceData> dataArrayList;
    private ArrayList<ArrayList<AttendenceDetails>> detailsArrayList;

    public ListsReturner(ArrayList<AttendenceData> dataArrayList, ArrayList<ArrayList<AttendenceDetails>> detailsArrayList) {
        this.detailsArrayList = detailsArrayList;
        this.dataArrayList = dataArrayList;
    }

    public ListsReturner() {
        this.detailsArrayList = new ArrayList<>();
        this.dataArrayList = new ArrayList<>();
    }

    public ArrayList<AttendenceData> getDataArrayList() {
        return dataArrayList;
    }

    public ArrayList<ArrayList<AttendenceDetails>> getDetailsArrayList() {
        return detailsArrayList;
    }

    public void clear() {
        this.detailsArrayList.clear();
        this.dataArrayList.clear();
    }
}
