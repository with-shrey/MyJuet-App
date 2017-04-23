package app.myjuet.com.myjuet.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        return new DetailsAdapter.viewHolder(ContactView);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        AttendenceDetails data = list.get(position);
        //Finding all text views from list_layout
        CardView layout = holder.layout;
        TextView Date = holder.Date;
        TextView Status = holder.Status;
        TextView Type = holder.Type;

        Date.setText(data.getmDate());
        Status.setText(data.getmTime());
        Type.setText(data.getmType());
        if (data.getmStatus().equals("Present")) {

            layout.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.present));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                layout.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(204, 255, 204)));
            }
        } else {
            layout.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.absent));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                layout.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255, 204, 204)));
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressWarnings("WeakerAccess")
    public class viewHolder extends RecyclerView.ViewHolder {
        //Finding all text views from list_layout
        private CardView layout;
        private TextView Date;
        private TextView Status;
        private TextView Type;

        public viewHolder(View itemView) {
            super(itemView);
            layout = (CardView) itemView.findViewById(R.id.details_linear_layout);
            Date = (TextView) itemView.findViewById(R.id.date);
            Status = (TextView) itemView.findViewById(R.id.status);
            Type = (TextView) itemView.findViewById(R.id.type);

        }

    }
}
