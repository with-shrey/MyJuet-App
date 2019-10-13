package app.myjuet.com.myjuet.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import app.myjuet.com.myjuet.R;


public class ContactFragment extends Fragment {


    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        setHasOptionsMenu(false);
        ImageView shreyfb = view.findViewById(R.id.shrey_fb);
        ImageView shreygmail = view.findViewById(R.id.shrey_gmail);
        ImageView ig = view.findViewById(R.id.shrey_ig);

        shreyfb.setOnClickListener(view12 -> sendIntent("https://www.facebook.com/connectwithshrey"));
        ig.setOnClickListener(view12 -> sendIntent("https://www.instagram.com/connectwithshrey"));
        shreygmail.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:connectwithshrey@gmail.com"));
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(Intent.createChooser(intent, "Send Using.."));
            } else {
                Toast.makeText(getContext(), "No Email App Found", Toast.LENGTH_LONG).show();
                Uri webpage = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gm&hl=en");
                Intent browser = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(browser);
                }
            }
        });

        return view;
    }

    void sendIntent(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}
