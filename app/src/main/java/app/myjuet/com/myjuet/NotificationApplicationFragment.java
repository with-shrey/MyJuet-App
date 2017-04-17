package app.myjuet.com.myjuet;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationApplicationFragment extends Fragment {

    public NotificationApplicationFragment() {
        // Required empty public constructor
    }

    public NotificationApplicationFragment newInstance(int i) {

        Bundle args = new Bundle();
        args.putInt("type", i);

        NotificationApplicationFragment fragment = new NotificationApplicationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.notification_application, container, false);
    }

}
