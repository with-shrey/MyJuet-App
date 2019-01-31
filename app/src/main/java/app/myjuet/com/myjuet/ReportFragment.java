package app.myjuet.com.myjuet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;



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
        intent.putExtra(Intent.EXTRA_SUBJECT, "Report/Suggestion");
        intent.putExtra(Intent.EXTRA_TEXT, editText.getText());
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
        return true;
    }
}