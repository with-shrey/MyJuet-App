package app.myjuet.com.myjuet.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.data.SgpaData;


/**
 * Created by Shrey on 20-Jul-17.
 */

public class CgpaAdapter extends RecyclerView.Adapter<CgpaAdapter.ViewHolder> {
    private ArrayList<SgpaData> list;
    // Store the context for easy access
    private Context mContext;

    public CgpaAdapter(Context context, ArrayList<SgpaData> contacts) {
        list = contacts;
        mContext = context;
    }
    @Override
    public CgpaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.cgpadatalayout, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(CgpaAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        SgpaData data = list.get(position);

        // Set item views based on your views and data model
        TextView GradePoints = viewHolder.GradePoints;
        TextView PointsSecured = viewHolder.PointsSecured;
        TextView CourseCredits = viewHolder.CourseCredits;
        TextView EarnedCredits = viewHolder.EarnedCredits;
        TextView Cgpa = viewHolder.Cgpa;
        TextView Sgpa = viewHolder.Sgpa;
        TextView Sem = viewHolder.Semester;

        GradientDrawable CgpaRect = (GradientDrawable) Cgpa.getBackground();
        GradientDrawable SgpaRect = (GradientDrawable) Sgpa.getBackground();
        CgpaRect.setColor(ContextCompat.getColor(mContext, getmColor(data.getmCgpa())));
        SgpaRect.setColor(ContextCompat.getColor(mContext, getmColor(data.getmSgpa())));

        Sem.setText(String.valueOf(data.getmSem()));
        GradePoints.setText(String.valueOf(data.getmGradePoints()));
        PointsSecured.setText(String.valueOf(data.getmPointssecuredcgpa()));
        EarnedCredits.setText(String.valueOf(data.getMearned()));
        CourseCredits.setText(String.valueOf(data.getMcoursecredits()));
        Cgpa.setText(String.valueOf(data.getmCgpa()));
        Sgpa.setText(String.valueOf(data.getmSgpa()));

    }

    // Returns the PointsSecured count of items in the list
    @Override
    public int getItemCount() {
        return list.size();
        //list.size();
    }

    private int getmColor(float mLecTut) {
        if ((mLecTut) >= 0 && (mLecTut) <= 6) {
            return R.color.magnitude40;
        } else if ((mLecTut) > 6 && (mLecTut) < 7) {
            return R.color.magnitude70;
        } else if ((mLecTut) >= 7 && (mLecTut) <= 10) {
            return R.color.magnitude80;
        } else {
            return R.color.magnitude90;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        private TextView GradePoints;
        private TextView PointsSecured;
        private TextView CourseCredits;
        private TextView EarnedCredits;
        private TextView Semester;
        private TextView Cgpa;
        private TextView Sgpa;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        private ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            GradePoints = (TextView) itemView.findViewById(R.id.gradepoints);
            PointsSecured = (TextView) itemView.findViewById(R.id.pointssecured);
            CourseCredits = (TextView) itemView.findViewById(R.id.coursecredits);
            EarnedCredits = (TextView) itemView.findViewById(R.id.earnedcredits);
            Semester = (TextView) itemView.findViewById(R.id.semesterno);
            Cgpa = (TextView) itemView.findViewById(R.id.cgpa);
            Sgpa = (TextView) itemView.findViewById(R.id.sgpa);
        }

    }
}
