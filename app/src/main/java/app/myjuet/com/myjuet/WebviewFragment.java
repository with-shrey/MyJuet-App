package app.myjuet.com.myjuet;


import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import static android.content.Context.DOWNLOAD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class WebviewFragment extends Fragment {

    public static WebView myWebView;
    public static String prev;
    boolean isConnected;
    String SnackString;
    String link = new String();
    AlertDialog.Builder builder = null;

    public WebviewFragment() {
        // Required empty public constructor
    }

    private static boolean pingHost(String host, int port, int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), timeout);
            socket.close();
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        @SuppressWarnings("RedundantStringConstructorCall") String notlink = new String();
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
        final ProgressBar progressBar = (ProgressBar) RootView.findViewById(R.id.progress_webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        myWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        myWebView.setScrollbarFadingEnabled(false);
        Context context = getActivity();
        SharedPreferences prefs = context.getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        final String Url = "https://webkiosk.juet.ac.in/CommonFiles/UserAction.jsp";
        String user = prefs.getString(getString(R.string.key_enrollment), "").toUpperCase().trim();
        String pass = prefs.getString(getString(R.string.key_password), "");
        final String PostParam = "txtInst=Institute&InstCode=JUET&txtuType=Member+Type&UserType=S&txtCode=Enrollment+No&MemberCode=" + user + "&txtPin=Password%2FPin&Password=" + pass + "&BTNSubmit=Submit";
        Context mContext = getActivity();
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            progressBar.setProgressTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.black, mContext.getTheme())));
        } else
            progressBar.getProgressDrawable().setColorFilter(mContext.getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                final int REQUEST_WRITE_STORAGE = 112;
                boolean hasPermission = (ContextCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                Toast.makeText(getContext(), "Starting Download ...\nPlease Wait..", Toast.LENGTH_LONG).show();

                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                final String FileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, FileName);
                DownloadManager dm = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                BroadcastReceiver onComplete = new BroadcastReceiver() {
                    public void onReceive(Context ctxt, Intent intent) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                OpenNewVersion(Environment.getExternalStorageDirectory() + "/Download/", FileName);
                            }
                        }, 10000);
                        getActivity().unregisterReceiver(this);
                    }
                };
                getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        });
        myWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {
                try {
                if (progressBar.getVisibility() == View.GONE) {
                    if (((DrawerActivity) getActivity()).fab.getVisibility() == View.VISIBLE)
                    ((DrawerActivity) getActivity()).fab.setVisibility(View.GONE);

                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(progress);
                if (progress == 100) {
                    try {
                        if (link.equals(Url + "?" + PostParam) && ((DrawerActivity) getActivity()).activeFragment == 3)
                            optionsDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressBar.setProgress(0);
                    progressBar.setVisibility(View.GONE);
                }

            }
        });
        link = Url + "?" + PostParam;
        if (isConnected) {
            if (!getActivity().getIntent().getBooleanExtra("containsurl", false)) {
                link = Url + "?" + PostParam;
            myWebView.loadUrl(link);
                prev = link;
            } else {
                Toast.makeText(getContext(), notlink, Toast.LENGTH_LONG).show();
                link = notlink;
                myWebView.loadUrl(link);
                prev = link;

            }
        } else if (!isConnected) {
            ((DrawerActivity) getActivity()).fab.setVisibility(View.VISIBLE);
            SnackString = "NoInternet";
            ((DrawerActivity) getActivity()).fab.performClick();
        } else {
            ((DrawerActivity) getActivity()).fab.setVisibility(View.VISIBLE);
            SnackString = "Webkiosk Down/Timed Out";
            ((DrawerActivity) getActivity()).fab.performClick();
        }

        return RootView;
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
    AlertDialog dialog;
    private void optionsDialog() {
        if (dialog != null){
            if (dialog.isShowing()){
                dialog.cancel();
            }
            dialog = null;
        }
        if (((DrawerActivity) getActivity()).fab.getVisibility() == View.VISIBLE)
        ((DrawerActivity) getActivity()).fab.setVisibility(View.GONE);
        CharSequence colors[] = new CharSequence[]{"Alert Message", "Full Website", "Attendence", "Date Sheet", "Seating Plan", "Exam Marks", "CGPA/SGPA", "Open In Browser", "Change Password"};
            builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("WEBKIOSK-JUET");
            builder.setItems(colors, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            link = "https://webkiosk.juet.ac.in/StudentFiles/PersonalFiles/ShowAlertMessageSTUD.jsp";
                            if (((DrawerActivity) getActivity()).activeFragment == 3)
                                myWebView.loadUrl(link);
                            prev = link;
                            builder = null;
                            break;
                        case 1:

                            link = "https://webkiosk.juet.ac.in/StudentFiles/StudentPage.jsp";
                            if (((DrawerActivity) getActivity()).activeFragment == 3)
                                myWebView.loadUrl(link);
                            prev = link;
                            builder = null;


                            break;
                        case 2:

                            link = "https://webkiosk.juet.ac.in/StudentFiles/Academic/StudentAttendanceList.jsp";
                            if (((DrawerActivity) getActivity()).activeFragment == 3)
                                myWebView.loadUrl(link);
                            prev = link;
                            builder = null;

                            break;
                        case 3:

                            link = "https://webkiosk.juet.ac.in/StudentFiles/Exam/StudViewDateSheet.jsp";
                            if (((DrawerActivity) getActivity()).activeFragment == 3)
                                myWebView.loadUrl(link);
                            prev = link;
                            builder = null;

                            break;
                        case 4:
                            link = "https://webkiosk.juet.ac.in/StudentFiles/Exam/StudViewSeatPlan.jsp";
                            if (((DrawerActivity) getActivity()).activeFragment == 3)
                                myWebView.loadUrl(link);
                            prev = link;
                            builder = null;

                            break;
                        case 5:
                            link = "https://webkiosk.juet.ac.in/StudentFiles/Exam/StudentEventMarksView.jsp";
                            if (((DrawerActivity) getActivity()).activeFragment == 3)
                                myWebView.loadUrl(link);
                            prev = link;
                            builder = null;

                            break;
                        case 6:

                            ((DrawerActivity) getActivity()).fab.setVisibility(View.GONE);
                            link = "https://webkiosk.juet.ac.in/StudentFiles/Exam/StudCGPAReport.jsp";
                            if (((DrawerActivity) getActivity()).activeFragment == 3)
                                myWebView.loadUrl(link);
                            prev = link;
                            builder = null;

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
                            builder = null;

                            break;
                        case 8:
                            final AlertDialog.Builder change = new AlertDialog.Builder(getActivity());
                            change.setMessage("NOTE:Kindly Update your Password in Settings.\nAre You Sure You Want To Change Your Password?");
                            change.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    link = "https://webkiosk.juet.ac.in/CommonFiles/ChangePassword.jsp";
                                    if (((DrawerActivity) getActivity()).activeFragment == 3)
                                        myWebView.loadUrl(link);
                                }

                            });
                            change.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            change.show();
                            builder = null;
                            break;
                    }
                }

            });

            dialog = builder.show();

    }

    public void sendIntentBrowser() {
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

    void OpenNewVersion(String location, String FileName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(location + FileName)),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}