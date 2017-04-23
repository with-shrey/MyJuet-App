package app.myjuet.com.myjuet;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import app.myjuet.com.myjuet.web.webUtilities;

import static android.R.attr.description;
import static android.R.attr.lockTaskMode;


/**
 * A simple {@link Fragment} subclass.
 */
public class WebviewFragment extends Fragment {

    public static WebView myWebView;
    public static ProgressDialog progressDialog;
    boolean isConnected;
    String SnackString;
    String link;
    String Loading;

    public WebviewFragment() {
        // Required empty public constructor
    }

    public static void goBackWebview() {
        myWebView.goBack();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String notlink = new String();
        if (getActivity().getIntent().getBooleanExtra("containsurl", false))
            notlink = getActivity().getIntent().getStringExtra("url");
        View RootView = inflater.inflate(R.layout.fragment_webview, container, false);
        setHasOptionsMenu(true);
        ((DrawerActivity) getActivity()).fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, SnackString, Snackbar.LENGTH_LONG).setAction("Refresh", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myWebView.loadUrl(link);
                    }
                }).show();
            }
        });
        myWebView = (WebView) RootView.findViewById(R.id.web_view_layout);
        WebSettings webSettings = myWebView.getSettings();
        myWebView.setInitialScale(1);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        myWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        myWebView.setScrollbarFadingEnabled(false);
        Context context = getActivity();
        SharedPreferences prefs = context.getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        String Url = "https://webkiosk.juet.ac.in/CommonFiles/UserAction.jsp";
        String user = prefs.getString(getString(R.string.key_enrollment), "").toUpperCase().trim();
        String pass = prefs.getString(getString(R.string.key_password), "");
        String PostParam = "txtInst=Institute&InstCode=JUET&txtuType=Member+Type&UserType=S&txtCode=Enrollment+No&MemberCode=" + user + "&txtPin=Password%2FPin&Password=" + pass + "&BTNSubmit=Submit";
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.setWebChromeClient(new WebChromeClient() {
            private ProgressDialog mProgress;

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                ((DrawerActivity) getActivity()).fab.setVisibility(View.VISIBLE);
                SnackString = consoleMessage.toString();
                ((DrawerActivity) getActivity()).fab.performClick();

                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (mProgress == null) {
                    mProgress = new ProgressDialog(getActivity());
                    mProgress.setMessage("Loading WebPage..");
                    mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgress.setMax(100);
                    mProgress.show();
                }
                mProgress.setProgress(progress);
                if (progress == 100) {
                    mProgress.dismiss();
                    mProgress = null;
                }

            }
        });
        if (isConnected) {
            if (!getActivity().getIntent().getBooleanExtra("containsurl", false)) {
            link = Url + "?" + PostParam;
            myWebView.loadUrl(link);
                optionsDialog();
            } else {
                Toast.makeText(getContext(), notlink, Toast.LENGTH_LONG).show();
                link = notlink;
                myWebView.loadUrl(link);
            }
        } else {
            ((DrawerActivity) getActivity()).fab.setVisibility(View.VISIBLE);
            SnackString = "NoInternet";
            ((DrawerActivity) getActivity()).fab.performClick();
        }

        return RootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        if (((DrawerActivity) getActivity()).getSupportActionBar() != null)
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle("Webview");
        ((DrawerActivity) getActivity()).appBarLayout.setExpanded(false);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.webview_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.optionsdialog_webview) {
            optionsDialog();
        } else {
            ((DrawerActivity) getActivity()).fab.setVisibility(View.GONE);
            myWebView.loadUrl(link);
        }
        return super.onOptionsItemSelected(item);
    }

    private void optionsDialog() {
        ((DrawerActivity) getActivity()).fab.setVisibility(View.GONE);
        CharSequence colors[] = new CharSequence[]{"Alert Message", "Full Website", "Attendence", "Date Sheet", "Seating Plan", "Exam Marks", "Cgpa/Sgpa", "Open In Browser", "Change Password"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("WEBKIOSK-JUET");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                            link = "https://webkiosk.juet.ac.in/StudentFiles/PersonalFiles/ShowAlertMessageSTUD.jsp";
                            myWebView.loadUrl(link);
                        break;
                    case 1:

                            link = "https://webkiosk.juet.ac.in/StudentFiles/StudentPage.jsp";
                            myWebView.loadUrl(link);

                        break;
                    case 2:

                            link = "https://webkiosk.juet.ac.in/StudentFiles/Academic/StudentAttendanceList.jsp";
                            myWebView.loadUrl(link);
                        break;
                    case 3:

                            link = "https://webkiosk.juet.ac.in/StudentFiles/Exam/StudViewDateSheet.jsp";
                            myWebView.loadUrl(link);
                        break;
                    case 4:
                            link = "https://webkiosk.juet.ac.in/StudentFiles/Exam/StudViewSeatPlan.jsp";
                            myWebView.loadUrl(link);
                        break;
                    case 5:
                            link = "https://webkiosk.juet.ac.in/StudentFiles/Exam/StudentEventMarksView.jsp";
                            myWebView.loadUrl(link);
                        break;
                    case 6:

                        ((DrawerActivity) getActivity()).fab.setVisibility(View.GONE);
                            link = "https://webkiosk.juet.ac.in/StudentFiles/Exam/StudCGPAReport.jsp";
                            myWebView.loadUrl(link);
                        break;
                    case 7:
                        final AlertDialog.Builder confirm = new AlertDialog.Builder(getActivity());
                        confirm.setMessage("You Are Going To Open Webkiosk In Your Browser.\nAre You Sure?");
                        confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                sendIntentBrowser();
                            }

                        });
                        confirm.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        confirm.show();
                        break;
                    case 8:
                        final AlertDialog.Builder change = new AlertDialog.Builder(getActivity());
                        change.setMessage("NOTE:Kindly Update your Password in Settings.\nAre You Sure You Want To Change Your Password?");
                        change.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                    link = "https://webkiosk.juet.ac.in/CommonFiles/ChangePassword.jsp";
                                    myWebView.loadUrl(link);
                            }

                        });
                        change.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        change.show();
                        break;
                }
            }

        });
        builder.show();

    }

    private void sendIntentBrowser() {
        Context context = getActivity();
        SharedPreferences prefs = context.getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        final String Url = "https://webkiosk.juet.ac.in/CommonFiles/UserAction.jsp";
        String user = prefs.getString(getString(R.string.key_enrollment), "");
        String pass = prefs.getString(getString(R.string.key_password), "");
        final String PostParam = "txtInst=Institute&InstCode=JUET&txtUType=Member+Type&UserType=S&txtCode=Enrollment No&MemberCode=" + user + "&txtPIN=Password%2FPin&Password=" + pass + "&BTNSubmit=Submit";

        Uri webpage = Uri.parse(Url + "?" + PostParam);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }


}