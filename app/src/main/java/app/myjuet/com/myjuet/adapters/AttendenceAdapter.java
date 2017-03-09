package app.myjuet.com.myjuet.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.data.AttendenceData;


public class AttendenceAdapter extends ArrayAdapter<AttendenceData> {

    public AttendenceAdapter(Context context, ArrayList<AttendenceData> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ListLayout = convertView;
        if (ListLayout == null) {
            ListLayout = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_layout, parent, false);
        }
        AttendenceData data = getItem(position);
        //Finding all text views from list_layout
        TextView Name = (TextView) ListLayout.findViewById(R.id.name);
        TextView Total = (TextView) ListLayout.findViewById(R.id.total);
        TextView Next = (TextView) ListLayout.findViewById(R.id.next);
        TextView Leaving = (TextView) ListLayout.findViewById(R.id.leaving);

        Name.setText(data.getmName());
        Total.setText(data.getmLecTut());
        Leaving.setText("Leaving Next:" + data.getmOnLeaving());
        Next.setText("Attending Next:" + data.getmOnNext());

        return ListLayout;

    }
}
