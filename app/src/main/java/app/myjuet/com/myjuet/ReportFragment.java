package app.myjuet.com.myjuet;

import android.content.Context;
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
import android.widget.EditText;

import app.myjuet.com.myjuet.R;


public class ReportFragment extends Fragment {

    EditText editText;

    public ReportFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View Rootview = inflater.inflate(R.layout.fragment_report, container, false);
        editText = (EditText) Rootview.findViewById(R.id.report_suggest);
        return Rootview;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sendbutton, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:myjuetapp@gmail.com")); // only email apps should handle this
        //   intent.putExtra(Intent.EXTRA_EMAIL, "myjuetapp@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Report/Suggestion");
        intent.putExtra(Intent.EXTRA_TEXT, editText.getText());
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent.createChooser(intent, "Send Using.."));
        } else {
        } //TODO:send to browser with data copy paste

        return true;
    }
}