package app.myjuet.com.myjuet.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import app.myjuet.com.myjuet.AttendenceDetailsActivity;
import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.data.AttendenceData;



public class AttendenceAdapter extends RecyclerView.Adapter<AttendenceAdapter.ViewHolder> {
    private ArrayList<AttendenceData> list;
    // Store the context for easy access
    private Context mContext;
    private int preferred = 90;
    SharedPreferences prefs;

    public AttendenceAdapter(Context context, ArrayList<AttendenceData> contacts) {
        list = contacts;
        mContext = context;
        prefs = mContext.getSharedPreferences(mContext.getString(R.string.preferencefile), Context.MODE_PRIVATE);
        preferred = Integer.parseInt(prefs.getString(mContext.getString(R.string.key_preferred_attendence), "90"));

    }

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
        ProgressBar Loading = viewHolder.Loading;

        GradientDrawable magnitudeCircle = (GradientDrawable) Total.getBackground();
        int color;
        try {
            color = getmColor(Integer.parseInt(data.getmLecTut()));
        } catch (Exception e) {
            e.printStackTrace();
            color = R.color.magnitude90;
        }
        magnitudeCircle.setColor(ContextCompat.getColor(mContext, color));
        Name.setText(data.getmName());
        Total.setText(data.getmLecTut());
        String next = "Leaving Next:" + data.getmOnLeaving();
        String attendnext = "Attending Next:" + data.getmOnNext();
        Leaving.setText(next);
        Next.setText(attendnext);
        Loading.setVisibility(data.isLoading() ? View.VISIBLE : View.GONE);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return list.size();
    }

    private int getmColor(int mLecTut) {
        preferred = Integer.parseInt(prefs.getString(mContext.getString(R.string.key_preferred_attendence), "90"));
        if (mLecTut >= preferred){
            return R.color.magnitude90;
        }else {
            if ((mLecTut) >= preferred - 50 && (mLecTut) < preferred - 40) {
                return R.color.magnitude40;
            } else if ((mLecTut) >= preferred - 40 && (mLecTut) < preferred - 30) {
                return R.color.magnitude40;
            } else if ((mLecTut) >= preferred - 30 && (mLecTut) < preferred - 20) {
                return R.color.magnitude50;
            } else if ((mLecTut) >= preferred - 20 && (mLecTut) < preferred - 10) {
                return R.color.magnitude50;
            } else if ((mLecTut) >= preferred - 10 && (mLecTut) < preferred) {
                return R.color.magnitude70;
            }else{
                return R.color.magnitude1;
            }
        }
       /* if ((mLecTut) >= 0 && (mLecTut) <= 40) {
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
        }*/
    }

    @SuppressWarnings("WeakerAccess")
    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        private TextView Name;
        private TextView Total;
        private TextView Next;
        private TextView Leaving;
        private ProgressBar Loading;

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
            Loading = (ProgressBar) itemView.findViewById(R.id.loading);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), AttendenceDetailsActivity.class);
            intent.putExtra("listno", getAdapterPosition());
            intent.putExtra("id", list.get(getAdapterPosition()).getId());
            view.getContext().startActivity(intent);
        }
    }
}

