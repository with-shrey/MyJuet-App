package app.myjuet.com.myjuet;


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
import app.myjuet.com.myjuet.data.DateSheet;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.database.DateSheetDao;
import app.myjuet.com.myjuet.utilities.AppExecutors;
import app.myjuet.com.myjuet.vm.DateSheetViewModel;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple [Fragment] subclass.
 */
public class DateSheetFragment extends Fragment {
    DateSheetViewModel mDateSheetViewModel;
    DateSheetDao mDateSheetDao;
    ArrayList<DateSheet> mDateSheets;
    AppExecutors mAppExecutors = AppExecutors.newInstance();
    DateSheetAdapter mAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_date_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDateSheetViewModel = ViewModelProviders.of(getActivity()).get(DateSheetViewModel.class);
        mDateSheetDao = AppDatabase.newInstance(getActivity()).DateSheetDao();
        mDateSheets = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new DateSheetAdapter();
        recyclerView.setAdapter(mAdapter);
        mDateSheetDao.dateSheet().observe(this,list -> {
            if (list != null) {
                mAppExecutors.diskIO().execute(() -> {
                    mDateSheets.clear();
                    mDateSheets.addAll(list);
                    mAppExecutors.mainThread().execute(() -> {
                        mAdapter.notifyDataSetChanged();
                    });
                });

            }else{
                Log.v("DateSheet", "Null");
            }
        });
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(()->{
            mDateSheetViewModel.loadDateSheet().observe(this,status -> {
                Log.v("DateSheet", "" + status);
                switch (status){
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

    }


    class DateSheetAdapter extends RecyclerView.Adapter<DateSheetAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.datesheet_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DateSheet ds = mDateSheets.get(position);
            String[] dates = TextUtils.split(ds.getDate(),"-");
            holder.date.setText(dates[0] + "/" + dates[1]);
            holder.dateFull.setText(ds.getDate());
            holder.time.setText(ds.getTime());
            holder.name.setText(ds.getSubjectName());
            GradientDrawable magnitudeCircle = (GradientDrawable) holder.date.getBackground();
            magnitudeCircle.setColor(ContextCompat.getColor(DateSheetFragment.this.getActivity(),R.color.magnitude1));
        }

        @Override
        public int getItemCount() {
            return mDateSheets.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView date,name,dateFull,time;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                date = itemView.findViewById(R.id.date);
                name = itemView.findViewById(R.id.name);
                dateFull = itemView.findViewById(R.id.date_full);
                time = itemView.findViewById(R.id.time);
            }
        }
    }
}// Required empty public constructor
