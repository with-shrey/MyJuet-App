package app.myjuet.com.myjuet.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.data.AttendenceDetails;


public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.viewHolder> {
    private ArrayList<AttendenceDetails> list;
    private Context mContext;

    public DetailsAdapter(Context Context, ArrayList<AttendenceDetails> list) {
        this.list = list;
        mContext = Context;
    }

    @Override
    public DetailsAdapter.viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflator = LayoutInflater.from(context);
        View ContactView = inflator.inflate(R.layout.detailslist_layout, parent, false);
        DetailsAdapter.viewHolder Holder = new DetailsAdapter.viewHolder(ContactView);
        return Holder;
    }

    @Override
    public void onBindViewHolder(DetailsAdapter.viewHolder holder, int position) {
        AttendenceDetails data = list.get(position);
        //Finding all text views from list_layout
        LinearLayout layout = (LinearLayout) holder.layout;
        TextView Date = (TextView) holder.Date;
        TextView Status = (TextView) holder.Status;
        TextView Type = (TextView) holder.Type;

        Date.setText(data.getmDate());
        Status.setText(data.getmTime());
        Type.setText(data.getmType());
        if (data.getmStatus().equals("Present"))
            layout.setBackgroundColor(mContext.getResources().getColor(R.color.present));
        else
            layout.setBackgroundColor(mContext.getResources().getColor(R.color.absent));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        //Finding all text views from list_layout
        public LinearLayout layout;
        public TextView Date;
        public TextView Status;
        public TextView Type;

        public viewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.details_linear_layout);
            Date = (TextView) itemView.findViewById(R.id.date);
            Status = (TextView) itemView.findViewById(R.id.status);
            Type = (TextView) itemView.findViewById(R.id.type);

        }

    }
}
