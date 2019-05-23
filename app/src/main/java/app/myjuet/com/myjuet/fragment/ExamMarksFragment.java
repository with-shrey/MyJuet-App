package app.myjuet.com.myjuet.fragment;


import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.data.ExamMarks;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.database.ExamMarksDao;
import app.myjuet.com.myjuet.utilities.AppExecutors;
import app.myjuet.com.myjuet.vm.ExamMarksViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExamMarksFragment extends Fragment {
    ExamMarksViewModel mExamMarksViewModel;
    ExamMarksDao mExamMarksDao;
    ArrayList<ExamMarks> mExamMarks;
    AppExecutors mAppExecutors = AppExecutors.newInstance();
    ExamMarksFragment.ExamMarksAdapter mAdapter;

    public ExamMarksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exam_marks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mExamMarksDao = AppDatabase.newInstance(getActivity()).ExamMarksDao();
        mExamMarksViewModel = ViewModelProviders.of(getActivity()).get(ExamMarksViewModel.class);
        mExamMarks = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ExamMarksAdapter();
        recyclerView.setAdapter(mAdapter);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            mExamMarksViewModel.loadExamMarks().observe(this, status -> {
                switch (status) {
                    case LOADING:
                        swipeRefreshLayout.setRefreshing(true);
                        break;
                    case SUCCESS:
                        swipeRefreshLayout.setRefreshing(false);
                        break;
                    case WRONG_PASSWORD:
                    case NO_INTERNET:
                    case WEBKIOSK_DOWN:
                    case FAILED:
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "Error Occurred", Toast.LENGTH_SHORT).show();
                        break;
                }
            });
        });
        mExamMarksDao.examMarks().observe(this, list -> {
            if (list != null) {
                mAppExecutors.diskIO().execute(() -> {
                    mExamMarks.clear();
                    mExamMarks.addAll(list);
                    mAppExecutors.mainThread().execute(() -> {
                        mAdapter.notifyDataSetChanged();
                    });
                });

            } else {
                Log.v("DateSheet", "Null");
            }
        });
    }

    public class ExamMarksAdapter extends RecyclerView.Adapter<ExamMarksAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.datesheet_item, parent, false);
            return new ExamMarksAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ExamMarks ds = mExamMarks.get(position);
            holder.date.setText(ds.getTotalMarks());
            holder.time.setText(ds.getMarksString());
            holder.dateFull.setVisibility(View.GONE);
            holder.name.setText(ds.getSubjectName());
            GradientDrawable magnitudeCircle = (GradientDrawable) holder.date.getBackground();
            magnitudeCircle.setColor(ContextCompat.getColor(ExamMarksFragment.this.getActivity(), R.color.magnitude1));
        }

        @Override
        public int getItemCount() {
            return mExamMarks.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView date, name, dateFull, time;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                date = itemView.findViewById(R.id.date);
                name = itemView.findViewById(R.id.name);
                dateFull = itemView.findViewById(R.id.date_full);
                time = itemView.findViewById(R.id.time);
            }
        }
    }
}
