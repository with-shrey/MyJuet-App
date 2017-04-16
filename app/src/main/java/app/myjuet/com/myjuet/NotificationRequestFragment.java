package app.myjuet.com.myjuet;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


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
        setHasOptionsMenu(true);

        View RootView = inflater.inflate(R.layout.fragment_notification_request, container, false);
        return RootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sendbutton, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, "myjuetapp@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Report/Suggestion");
        intent.putExtra(Intent.EXTRA_TEXT, DataString);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent.createChooser(intent, "Send Using.."));
        } else {
        } //TODO:send to browser with data copy paste
        return true;
    }
}
