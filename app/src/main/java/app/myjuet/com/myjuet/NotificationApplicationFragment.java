package app.myjuet.com.myjuet;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static android.R.id.message;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationApplicationFragment extends Fragment {

    String text;
    Uri imgUri = null;
    String Title = "";
    String Message = "";
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
    TextView path;
    int i;
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
        setHasOptionsMenu(true);
        View RootView = inflater.inflate(R.layout.notification_application, container, false);
        title = (TextInputEditText) RootView.findViewById(R.id.title_application);
        message = (TextInputEditText) RootView.findViewById(R.id.message_application);
        TextView messagetext = (TextView) RootView.findViewById(R.id.length_message_application);
        url = (TextInputEditText) RootView.findViewById(R.id.url_application);
        byLine = (TextInputEditText) RootView.findViewById(R.id.byline_application);
        Contact = (TextInputEditText) RootView.findViewById(R.id.contact_application);
        path = (TextView) RootView.findViewById(R.id.path_attachment);
        Button img = (Button) RootView.findViewById(R.id.button_application);
        LinearLayout linearLayout = (LinearLayout) RootView.findViewById(R.id.image_application);
        i = getArguments().getInt("type");
        if (i == 2) {
            messagetext.setText("Max.Length 200");
            message.setMaxLines(20);
        } else if (i == 3) {
            linearLayout.setVisibility(View.VISIBLE);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int FILE_SELECT_CODE = 0;
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);

                    try {
                        startActivityForResult(
                                Intent.createChooser(intent, "Select a Image.. "),
                                FILE_SELECT_CODE);
                    } catch (android.content.ActivityNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }


        return RootView;
    }

    public void ImageChoose() {
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
            if (!url.getText().toString().isEmpty()) {
                Url = url.getText().toString();
                data.put("url", Url);
            }
            if (!byLine.getText().toString().isEmpty()) {
                Byline = byLine.getText().toString();
                data.put("Byline", Byline);
            }
            if (!Contact.getText().toString().isEmpty())
                Number = Contact.getText().toString();
            else {
                Toast.makeText(getContext(), "Contact Number Is Required", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (i == 3) {
                if (imgUri == null) {
                    Toast.makeText(getContext(), "Choose Image", Toast.LENGTH_SHORT).show();

                    return false;
                }
                data.put("urlImg", "");
            }
            parent.put("to", "");
            parent.put("data", data);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:myjuetapp@gmail.com")); // only email apps should handle this
        if (i == 3)
            intent.putExtra(Intent.EXTRA_STREAM, imgUri);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Notification Request-" + prefs.getString(getString(R.string.enrollment), "").toUpperCase().trim());
        try {
            intent.putExtra(Intent.EXTRA_TEXT, Number + "\n" + parent.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent.createChooser(intent, "Send Using.."));
        } else {
        } //TODO:send to browser with data copy paste
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                imgUri = data.getData();
                path.setText(imgUri.getPath());
                Toast.makeText(getContext(), "Image Added", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
