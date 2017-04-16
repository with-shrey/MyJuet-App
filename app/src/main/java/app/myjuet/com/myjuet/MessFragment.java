package app.myjuet.com.myjuet;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ResponseCache;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.R.attr.bitmap;
import static android.R.attr.data;
import static android.R.attr.format;
import static android.R.attr.path;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.ALARM_SERVICE;
import static android.support.v4.content.FileProvider.getUriForFile;
import static android.support.v7.widget.AppCompatDrawableManager.get;
import static app.myjuet.com.myjuet.R.id.refresh;
import static app.myjuet.com.myjuet.WebviewFragment.progressDialog;
import static com.google.android.gms.internal.zzs.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessFragment extends Fragment {


    String mCurrentPhotoPath;
    ImageView mImageView;
    Uri file;



    public MessFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.mess_layout, container, false);
        setHasOptionsMenu(true);
        ((DrawerActivity) getActivity()).fab.setVisibility(View.GONE);
        mImageView = (ImageView) RootView.findViewById(R.id.anapurna_img);
        TextView day = (TextView) RootView.findViewById(R.id.day_mess);
        String Day = "Today is " + new SimpleDateFormat("EEEE").format(new Date());
        day.setText(Day);
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String imageFileName = "Mess";
        File image = null;
        image = new File(storageDir, imageFileName + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        if (image.exists()) {
            new Thread(new Runnable() {
                public void run() {
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                    final Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mImageView.setImageBitmap(bitmap);
                        }
                    });
                }
            }
            ).start();

        } else {
            refreshimage();
        }



        return RootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mess_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        if (item.getItemId() == R.id.camera_mess) {
            refreshimage();
        }
        if (item.getItemId() == R.id.fetch_img) {
            int FILE_SELECT_CODE = 0;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            try {
                startActivityForResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        FILE_SELECT_CODE);
            } catch (android.content.ActivityNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshimage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading");
        progressDialog.show();


        StorageReference imageRef = storageRef.child("MessImage.jpg");
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String imageFileName = "Mess";
        File image = null;
        image = new File(storageDir, imageFileName + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        imageRef.getFile(image).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                mImageView.setImageBitmap(bitmap);
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } catch (NullPointerException e) {
                    Log.e("mess", "progress", e);
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } catch (NullPointerException pe) {
                    Log.e("mess", "progress", pe);
                }
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String admin = prefs.getString("admin", getString(R.string.defaultuser));
        String File = "mess/image.jpg";
        if (admin.equals("Myjuet.xyzadmin"))
            File = "MessImage.jpg";

        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String imageFileName = "Mess";
        File image = null;
        image = new File(storageDir, imageFileName + ".jpg");
        int ERROR = -1;
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                Uri file = data.getData();

                final Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setData(file);
                intent.putExtra("noFaceDetection", true);
                intent.putExtra("output", Uri.fromFile(image));
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, 1);
                } else {
                    requestCode = 1;
                    ERROR = 1;
                    Toast.makeText(getActivity(), "Crop Failed!!\nUploading Image...", Toast.LENGTH_SHORT).show();
                }
            }
            if (requestCode == 1) {
                Uri file = Uri.fromFile(image);
                if (ERROR == 1)
                    file = data.getData();
                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Uploading");
            progressDialog.show();
            // Get the Uri of the selected file
            StorageReference riversRef = storageRef.child(File);

            riversRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getActivity(), "Upload SuccessFull", Toast.LENGTH_LONG).show();
                    try {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } catch (NullPointerException pe) {
                        Log.e("mess", "progress", pe);
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getActivity(), "Upload Failed", Toast.LENGTH_LONG).show();
                            try {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                            } catch (NullPointerException pe) {
                                Log.e("mess", "progress", pe);
                            }

                        }
                    });

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
