package app.myjuet.com.myjuet.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.myjuet.com.myjuet.AttendenceDetailsActivity;
import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.data.AttendenceData;

/**
 * Created by Shrey on 20-Jul-17.
 */

public class CgpaAdapter extends RecyclerView.Adapter<AttendenceAdapter.ViewHolder> {
    @Override
    public AttendenceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.list_layout, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(AttendenceAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        AttendenceData data = list.get(position);

        // Set item views based on your views and data model
        TextView Name = viewHolder.Name;
        TextView Total = viewHolder.Total;
        TextView Next = viewHolder.Next;
        TextView Leaving = viewHolder.Leaving;

        GradientDrawable magnitudeCircle = (GradientDrawable) Total.getBackground();
        int color = getmColor(Integer.parseInt(data.getmLecTut()));
        magnitudeCircle.setColor(ContextCompat.getColor(mContext, color));
        Name.setText(data.getmName());
        Total.setText(data.getmLecTut());
        String next = "Leaving Next:" + data.getmOnLeaving();
        String attendnext = "Attending Next:" + data.getmOnNext();
        Leaving.setText(next);
        Next.setText(attendnext);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return list.size();
    }

    private int getmColor(int mLecTut) {
        if ((mLecTut) >= 0 && (mLecTut) <= 40) {
            return R.color.magnitude40;
        } else if ((mLecTut) > 40 && (mLecTut) < 50) {
            return R.color.magnitude40;
        } else if ((mLecTut) >= 50 && (mLecTut) < 60) {
            return R.color.magnitude50;
        } else if ((mLecTut) >= 60 && (mLecTut) < 70) {
            return R.color.magnitude50;
        } else if ((mLecTut) >= 70 && (mLecTut) < 80) {
            return R.color.magnitude70;
        } else if ((mLecTut) >= 80 && (mLecTut) <= 90) {
            return R.color.magnitude80;
        } else {
            return R.color.magnitude90;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        private TextView Name;
        private TextView Total;
        private TextView Next;
        private TextView Leaving;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        private ViewHolder(View itemView) {
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
            Intent intent = new Intent(view.getContext(), AttendenceDetailsActivity.class);
            intent.putExtra("listno", getAdapterPosition());
            view.getContext().startActivity(intent);
        }
    }
}
