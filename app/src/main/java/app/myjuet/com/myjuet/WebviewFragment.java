package app.myjuet.com.myjuet;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
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
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import app.myjuet.com.myjuet.web.webUtilities;

import static android.R.attr.description;
import static android.R.attr.lockTaskMode;


/**
 * A simple {@link Fragment} subclass.
 */
public class WebviewFragment extends Fragment {

    private static WebView myWebView;
    boolean isConnected;
    String SnackString;
    String link;
    String Loading;
    private ProgressDialog progressDialog;

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
        View RootView = inflater.inflate(R.layout.fragment_webview, container, false);
        setHasOptionsMenu(true);
        ((DrawerActivity) getActivity()).fab.setVisibility(View.VISIBLE);
        ((DrawerActivity) getActivity()).fab.setImageResource(R.drawable.ic_menu_black_24dp);
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String Url = "https://webkiosk.juet.ac.in/CommonFiles/UserAction.jsp";
        String user = prefs.getString(getString(R.string.enrollment), getString(R.string.defaultuser));
        String pass = prefs.getString(getString(R.string.password), getString(R.string.defaultpassword));
        String PostParam = "txtInst=Institute&InstCode=JUET&txtUType=Member+Type&UserType=S&txtCode=Enrollment No&MemberCode=" + user + "&txtPIN=Password%2FPin&Password=" + pass + "&BTNSubmit=Submit";
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onLoadResource(WebView view, String url) {
                // Check to see if there is a progress dialog
                    // If no progress dialog, make one and set message
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage(Loading);
                    progressDialog.show();

                    // Hide the webview while loading
                    myWebView.setEnabled(false);
                }


            public void onPageFinished(WebView view, String url) {
                // Page is done loading;
                // hide the progress dialog and show the webview
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        myWebView.setEnabled(true);
                    }
                } catch (NullPointerException e) {
                    Log.e("WEbview", "progress", e);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                SnackString = "ERROR:" + description + "ErrorCode:" + String.valueOf(errorCode);
                ((DrawerActivity) getActivity()).fab.performClick();

            }
        });
        if (isConnected) {
            Loading = "Signing In To Webkiosk";
            link = Url + "?" + PostParam;
            myWebView.loadUrl(link);
            optionsDialog();
        } else {
            SnackString = "NoInternet";
            ((DrawerActivity) getActivity()).fab.performClick();
        }
        return RootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
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

            myWebView.loadUrl(link);
        }
        return super.onOptionsItemSelected(item);
    }

    private void optionsDialog() {
        CharSequence colors[] = new CharSequence[]{"Full Website", "Attendence", "Date Sheet", "Seating Plan", "Exam Marks", "Cgpa/Sgpa", "Open In Browser", "Change Password"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("WEBKIOSK-JUET");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (isConnected) {
                            Loading = "Getting Page Data";
                            link = "https://webkiosk.juet.ac.in/StudentFiles/StudentPage.jsp";
                            myWebView.loadUrl(link);
                        } else {
                            SnackString = "No Internet";
                            ((DrawerActivity) getActivity()).fab.performClick();
                        }
                        break;
                    case 1:
                        if (isConnected) {
                            Loading = "Loading Attendence";
                            link = "https://webkiosk.juet.ac.in/StudentFiles/Academic/StudentAttendanceList.jsp";
                            myWebView.loadUrl(link);
                        } else {
                            SnackString = "No Internet";
                            ((DrawerActivity) getActivity()).fab.performClick();
                        }
                        break;
                    case 2:
                        if (isConnected) {
                            Loading = "Loading DateSheet";
                            link = "https://webkiosk.juet.ac.in/StudentFiles/Exam/StudViewDateSheet.jsp";
                            myWebView.loadUrl(link);
                        } else {
                            SnackString = "No Internet";
                            ((DrawerActivity) getActivity()).fab.performClick();
                        }
                        break;
                    case 3:
                        if (isConnected) {
                            Loading = "Loading Seating Plan";
                            link = "https://webkiosk.juet.ac.in/StudentFiles/Exam/StudViewSeatPlan.jsp";
                            myWebView.loadUrl(link);
                        } else {
                            SnackString = "No Internet";
                            ((DrawerActivity) getActivity()).fab.performClick();
                        }
                        break;
                    case 4:
                        if (isConnected) {
                            Loading = "Loading Exam Marks";
                            link = "https://webkiosk.juet.ac.in/StudentFiles/Exam/StudentEventMarksView.jsp";
                            myWebView.loadUrl(link);
                        } else {
                            SnackString = "No Internet";
                            ((DrawerActivity) getActivity()).fab.performClick();
                        }
                        break;
                    case 5:
                        if (isConnected) {
                            Loading = "Loading CGPA";
                            link = "https://webkiosk.juet.ac.in/StudentFiles/Exam/StudCGPAReport.jsp";
                            myWebView.loadUrl(link);
                        } else {
                            SnackString = "No Internet";
                            ((DrawerActivity) getActivity()).fab.performClick();
                        }
                        break;
                    case 6:
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
                    case 7:
                        final AlertDialog.Builder change = new AlertDialog.Builder(getActivity());
                        change.setMessage("NOTE:Kindly Update your Password in Settings.\nAre You Sure You Want To Change Your Password?");
                        change.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (isConnected) {
                                    Loading = "Loading Password Page";
                                    link = "https://webkiosk.juet.ac.in/CommonFiles/ChangePassword.jsp";
                                    myWebView.loadUrl(link);
                                } else {
                                    SnackString = "No Internet";
                                    ((DrawerActivity) getActivity()).fab.performClick();
                                }
                            }

                        });
                        change.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        change.show();
                }
            }

        });
        builder.show();

    }

    private void sendIntentBrowser() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String Url = "https://webkiosk.juet.ac.in/CommonFiles/UserAction.jsp";
        String user = prefs.getString(getString(R.string.enrollment), getString(R.string.defaultuser));
        String pass = prefs.getString(getString(R.string.password), getString(R.string.defaultpassword));
        final String PostParam = "txtInst=Institute&InstCode=JUET&txtUType=Member+Type&UserType=S&txtCode=Enrollment No&MemberCode=" + user + "&txtPIN=Password%2FPin&Password=" + pass + "&BTNSubmit=Submit";

        Uri webpage = Uri.parse(Url + "?" + PostParam);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}