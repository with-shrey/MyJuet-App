package app.myjuet.com.myjuet;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Stack;

import app.myjuet.com.myjuet.data.TimeTableData;

import static android.R.id.message;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationApplicationFragment extends Fragment {

    String Title = "";
    String Message = "";
    String Short = "";
    String Url = "";
    String Byline = "";
    String Number = "";
    JSONObject parent = new JSONObject();
    JSONObject data = new JSONObject();
    TextInputEditText title;
    TextInputEditText message;
    TextInputEditText url;
    TextInputEditText byLine;
    TextInputEditText Contact;
    TextInputEditText shorttext;

    public NotificationApplicationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View RootView = inflater.inflate(R.layout.notification_application, container, false);
        title = (TextInputEditText) RootView.findViewById(R.id.title_application);
        message = (TextInputEditText) RootView.findViewById(R.id.message_application);
        url = (TextInputEditText) RootView.findViewById(R.id.url_application);
        byLine = (TextInputEditText) RootView.findViewById(R.id.byline_application);
        Contact = (TextInputEditText) RootView.findViewById(R.id.contact_application);
        shorttext = (TextInputEditText) RootView.findViewById(R.id.short_description_application);
        return RootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sendbutton, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        try {
            data.put("id", "");
            if (!title.getText().toString().isEmpty()) {
                Title = title.getText().toString();
                data.put("title", Title);
            } else {
                Toast.makeText(getContext(), "Title Is Required", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!message.getText().toString().isEmpty()) {
                Message = message.getText().toString();
                data.put("message", Message);
            } else {
                Toast.makeText(getContext(), "Message Is Required", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!shorttext.getText().toString().isEmpty()) {
                Short = shorttext.getText().toString();
                data.put("short", Short);
            } else {
                Toast.makeText(getContext(), "Short Description Is Required", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!url.getText().toString().isEmpty()) {
                Url = url.getText().toString();
            }
            data.put("url", Url);

            if (!byLine.getText().toString().isEmpty()) {
                Byline = byLine.getText().toString();
            }
            data.put("by", "By: " + Byline);

            if (!Contact.getText().toString().isEmpty())
                Number = Contact.getText().toString();
            else {
                Toast.makeText(getContext(), "Contact Number Is Required", Toast.LENGTH_SHORT).show();
                return false;
            }
            parent.put("to", "");
            parent.put("data", data);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:myjuetapp@gmail.com")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, "Notification Request-" + prefs.getString(getString(R.string.enrollment), "").toUpperCase().trim());
        try {
            intent.putExtra(Intent.EXTRA_TEXT, Number + "\n" + parent.toString(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent.createChooser(intent, "Send Using.."));
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
