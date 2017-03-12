package app.myjuet.com.myjuet.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.myjuet.com.myjuet.AttendenceActivity;
import app.myjuet.com.myjuet.AttendenceDetailsActivity;
import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.data.AttendenceData;

import static android.R.attr.data;
import static app.myjuet.com.myjuet.AttendenceActivity.adapter;
import static app.myjuet.com.myjuet.AttendenceActivity.tempData;


public class AttendenceAdapter extends RecyclerView.Adapter<AttendenceAdapter.ViewHolder> {
    private ArrayList<AttendenceData> list;
    // Store the context for easy access
    private Context mContext;

    public AttendenceAdapter(Context context, ArrayList<AttendenceData> contacts) {
        list = contacts;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public AttendenceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.list_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(AttendenceAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        AttendenceData data = list.get(position);

        // Set item views based on your views and data model
        TextView Name = (TextView) viewHolder.Name;
        TextView Total = (TextView) viewHolder.Total;
        TextView Next = (TextView) viewHolder.Next;
        TextView Leaving = (TextView) viewHolder.Leaving;

        GradientDrawable magnitudeCircle = (GradientDrawable) Total.getBackground();
        int color = getmColor(Integer.parseInt(data.getmLecTut()));

        magnitudeCircle.setColor(getContext().getResources().getColor(color));
        Name.setText(data.getmName());
        Total.setText(data.getmLecTut());
        Leaving.setText("Leaving Next:" + data.getmOnLeaving());
        Next.setText("Attending Next:" + data.getmOnNext());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return list.size();
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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView Name;
        public TextView Total;
        public TextView Next;
        public TextView Leaving;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            Name = (TextView) itemView.findViewById(R.id.name);
            Total = (TextView) itemView.findViewById(R.id.total);
            Next = (TextView) itemView.findViewById(R.id.next);
            Leaving = (TextView) itemView.findViewById(R.id.leaving);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            tempData = AttendenceActivity.listdata.get(getAdapterPosition());
            Intent intent = new Intent(view.getContext(), AttendenceDetailsActivity.class);
            view.getContext().startActivity(intent);
        }
    }
}

