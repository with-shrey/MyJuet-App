package app.myjuet.com.myjuet;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import static android.R.attr.fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationRequestFragment extends Fragment {

    String DataString;

    public NotificationRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(false);

        View RootView = inflater.inflate(R.layout.fragment_notification_request, container, false);
        CardView simple = (CardView) RootView.findViewById(R.id.notification_normal);
        CardView text = (CardView) RootView.findViewById(R.id.notification_bigtext);
        CardView picture = (CardView) RootView.findViewById(R.id.notification_bigpicture);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        simple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new NotificationApplicationFragment().newInstance(1);
                fragmentTransaction.replace(R.id.layout_notification, fragment);
                fragmentTransaction.commit();
            }
        });
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new NotificationApplicationFragment().newInstance(2);
                fragmentTransaction.replace(R.id.layout_notification, fragment);
                fragmentTransaction.commit();
            }
        });
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new NotificationApplicationFragment().newInstance(3);
                fragmentTransaction.replace(R.id.layout_notification, fragment);
                fragmentTransaction.commit();
            }
        });


        return RootView;

    }


}
