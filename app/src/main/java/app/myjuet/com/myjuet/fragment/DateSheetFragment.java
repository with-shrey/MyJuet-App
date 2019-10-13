package app.myjuet.com.myjuet.fragment;


import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.data.DateSheet;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.database.DateSheetDao;
import app.myjuet.com.myjuet.utilities.AppExecutors;
import app.myjuet.com.myjuet.vm.DateSheetViewModel;


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
        TextView emptyView = view.findViewById(R.id.empty_text);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new DateSheetAdapter();
        recyclerView.setAdapter(mAdapter);
        mDateSheetDao.dateSheet().observe(this,list -> {
            if (list != null) {
                mAppExecutors.diskIO().execute(() -> {
                    mDateSheets.clear();
                    mDateSheets.addAll(list);
                    mAppExecutors.mainThread().execute(() -> {
                        emptyView.setVisibility(mDateSheets.size() > 0 ? View.GONE : View.VISIBLE);
                        mAdapter.notifyDataSetChanged();
                    });
                });

            }else{
            }
        });
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(()->{
            mDateSheetViewModel.loadDateSheet().observe(this,status -> {
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

        private SimpleDateFormat format;

        public DateSheetAdapter() {
            format = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        }

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
            if (dates.length >= 2)
            holder.date.setText(dates[0] + "/" + dates[1]);
            else{
                holder.date.setText(ds.getDate());
            }
            int color = R.color.magnitude1;
            try {
                Date date =format.parse(ds.getDate()+" "+ds.getTime());
                Date todayDate = new Date();
                if (date.compareTo(todayDate) < 0){
                    color = R.color.grey;
                    holder.doneImage.setVisibility(View.VISIBLE);
                    Picasso.get().load(R.drawable.mission_accomplished).fit().into(holder.doneImage);
                }else{
                    holder.doneImage.setVisibility(View.GONE);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                holder.doneImage.setVisibility(View.GONE);
            }
            holder.dateFull.setText(ds.getDate());
            holder.time.setText(ds.getTime());
            holder.name.setText(ds.getSubjectName());
            GradientDrawable magnitudeCircle = (GradientDrawable) holder.date.getBackground();
            magnitudeCircle.setColor(ContextCompat.getColor(DateSheetFragment.this.getActivity(),color));
        }

        @Override
        public int getItemCount() {
            return mDateSheets.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public ImageView doneImage;
            TextView date,name,dateFull,time;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                date = itemView.findViewById(R.id.date);
                name = itemView.findViewById(R.id.name);
                dateFull = itemView.findViewById(R.id.date_full);
                time = itemView.findViewById(R.id.time);
                doneImage = itemView.findViewById(R.id.done_image);
            }
        }
    }
}// Required empty public constructor
