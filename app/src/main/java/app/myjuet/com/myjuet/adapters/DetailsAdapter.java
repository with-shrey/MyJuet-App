package app.myjuet.com.myjuet.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import app.myjuet.com.myjuet.AttendenceDetailsActivity;
import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.data.AttendenceDetails;


public class DetailsAdapter extends ArrayAdapter<AttendenceDetails> {

    public DetailsAdapter(Context context, ArrayList<AttendenceDetails> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ListLayout = convertView;
        if (ListLayout == null) {
            ListLayout = LayoutInflater.from(getContext()).inflate(
                    R.layout.detailslist_layout, parent, false);
        }
        AttendenceDetails data = getItem(position);
        //Finding all text views from list_layout
        LinearLayout layout = (LinearLayout) ListLayout.findViewById(R.id.details_linear_layout);
        TextView Date = (TextView) ListLayout.findViewById(R.id.date);
        TextView Status = (TextView) ListLayout.findViewById(R.id.status);
        TextView Type = (TextView) ListLayout.findViewById(R.id.type);

        Date.setText(data.getmDate());
        Status.setText(data.getmStatus());
        Type.setText(data.getmType());
        if (data.getmStatus().equals("Present"))
            layout.setBackgroundColor(parent.getResources().getColor(R.color.magnitude90));
        else
            layout.setBackgroundColor(parent.getResources().getColor(R.color.magnitude6));

        return ListLayout;

    }
}
