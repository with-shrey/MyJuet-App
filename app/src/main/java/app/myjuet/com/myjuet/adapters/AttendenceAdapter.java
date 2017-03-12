package app.myjuet.com.myjuet.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
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

        GradientDrawable magnitudeCircle = (GradientDrawable) Total.getBackground();
        int color = getmColor(Integer.parseInt(data.getmLecTut()));

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(ListLayout.getResources().getColor(color));
        Name.setText(data.getmName());
        Total.setText(data.getmLecTut());
        Leaving.setText("Leaving Next:" + data.getmOnLeaving());
        Next.setText("Attending Next:" + data.getmOnNext());

        return ListLayout;

    }

    public int getmColor(int mLecTut) {
        if ((mLecTut) >= 0 && (mLecTut) <= 60) {
            return R.color.magnitude6;
        } else if ((mLecTut) > 60 && (mLecTut) < 70) {
            return R.color.magnitude7;
        } else if ((mLecTut) >= 70 && (mLecTut) <= 80) {
            return R.color.magnitude80;
        } else if ((mLecTut) >= 80 && (mLecTut) < 90) {
            return R.color.magnitude89;
        } else {
            return R.color.magnitude90;
        }
    }
}
