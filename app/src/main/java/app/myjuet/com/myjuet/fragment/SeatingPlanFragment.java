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

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.data.SeatingPlan;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.database.SeatingPlanDao;
import app.myjuet.com.myjuet.utilities.AppExecutors;
import app.myjuet.com.myjuet.vm.SeatingPlanViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class SeatingPlanFragment extends Fragment {

    SeatingPlanViewModel mSeatingPlanViewModel;
    SeatingPlanDao mSeatingPlanDao;
    ArrayList<SeatingPlan> mSeatingPlans;
    SeatingPlanAdapter mAdapter;
    AppExecutors mAppExecutors = AppExecutors.newInstance();
    public SeatingPlanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_date_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSeatingPlanViewModel = ViewModelProviders.of(getActivity()).get(SeatingPlanViewModel.class);
        mSeatingPlanDao = AppDatabase.newInstance(getActivity()).SeatingPlanDao();
        mSeatingPlans = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new SeatingPlanFragment.SeatingPlanAdapter();
        recyclerView.setAdapter(mAdapter);
        mSeatingPlanDao.seatingPlan().observe(this,list -> {
            if (list != null) {
                mAppExecutors.diskIO().execute(() -> {
                    mSeatingPlans.clear();
                    mSeatingPlans.addAll(list);
                    mAppExecutors.mainThread().execute(() -> {
                        mAdapter.notifyDataSetChanged();
                    });
                });

            }else{
                Log.v("SeatingPlan", "Null");
            }
        });
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(()->{
            mSeatingPlanViewModel.loadSeatingPlan().observe(this,status -> {
                Log.v("SeatingPlan", "" + status);
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

    class SeatingPlanAdapter extends RecyclerView.Adapter<SeatingPlanFragment.SeatingPlanAdapter.ViewHolder> {
        SimpleDateFormat format;
        public SeatingPlanAdapter() {
            format = new SimpleDateFormat("dd-MM-yyyy hh:mm a");

        }

        @NonNull
        @Override
        public SeatingPlanFragment.SeatingPlanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.seating_plan_item, parent, false);
            return new SeatingPlanFragment.SeatingPlanAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SeatingPlanFragment.SeatingPlanAdapter.ViewHolder holder, int position) {
            SeatingPlan ds = mSeatingPlans.get(position);
            holder.seat.setText(printFirst(ds.getRoomName())+"\n"+ds.getSeatNo());
            holder.seatDesc.setText("Row: "+ds.getRow()+" Col: "+ds.getColumn()+" Seat: "+ds.getSeatNo());
            holder.room.setText(ds.getRoomName());
            holder.dateFull.setText(ds.getDate());
            holder.time.setText(ds.getTime());
            holder.name.setText(ds.getSubjectName());
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

            GradientDrawable magnitudeCircle = (GradientDrawable) holder.seat.getBackground();
            magnitudeCircle.setColor(ContextCompat.getColor(SeatingPlanFragment.this.getActivity(),color));
        }
        String printFirst(String s)
        {

            StringBuilder letters= new StringBuilder();
            String[] myName = s.split(" ");
            for(int i = 0; i < myName.length; i++) {
                letters.append(myName[i].charAt(0));
                if (letters.toString().equals("LT") || letters.toString().equals("CR")){
                    letters.append("-");
                }
            }
            return letters.toString();
        }

        @Override
        public int getItemCount() {
            return mSeatingPlans.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView seatDesc,seat,name,dateFull,time,room;
            ImageView doneImage;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                dateFull = itemView.findViewById(R.id.date_full);
                time = itemView.findViewById(R.id.time);
                seatDesc = itemView.findViewById(R.id.room_desc);
                seat = itemView.findViewById(R.id.seat);
                room = itemView.findViewById(R.id.room);
                doneImage = itemView.findViewById(R.id.done_image);
            }
        }
    }
}
