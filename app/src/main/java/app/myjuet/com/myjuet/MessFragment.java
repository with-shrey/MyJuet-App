package app.myjuet.com.myjuet;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import static android.app.Activity.RESULT_OK;
import static com.google.android.gms.internal.zzs.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("UnusedAssignment")
public class MessFragment extends Fragment {


    String mCurrentPhotoPath;
    ImageView mImageView;
    Bitmap bitmap = null;


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
        String Day = "Today is " + new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());
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
                    int IMAGE_MAX_SIZE = 500000;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(mCurrentPhotoPath, options);
                    int scale = 1;
                    while ((options.outWidth * options.outHeight) * (1 / Math.pow(scale, 2)) >
                            IMAGE_MAX_SIZE) {
                        scale++;
                    }
                    if (scale > 1) {
                        scale--;
                        options = new BitmapFactory.Options();
                        options.inSampleSize = scale;
                        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
                        int height = bitmap.getHeight();
                        int width = bitmap.getWidth();

                        double y = Math.sqrt(IMAGE_MAX_SIZE
                                / (((double) width) / height));
                        double x = (y / height) * width;
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) x,
                                (int) y, true);
                        bitmap.recycle();
                        bitmap = scaledBitmap;
                        System.gc();
                    } else {
                        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);

                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mImageView.setImageBitmap(bitmap);
                        }
                    });

            }

            }).start();


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
        progressDialog.setMessage("Refreshing...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setProgressPercentFormat(null);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
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
                int IMAGE_MAX_SIZE = 500000;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mCurrentPhotoPath, options);
                int scale = 1;
                while ((options.outWidth * options.outHeight) * (1 / Math.pow(scale, 2)) >
                        IMAGE_MAX_SIZE) {
                    scale++;
                }
                if (scale > 1) {
                    scale--;
                    options = new BitmapFactory.Options();
                    options.inSampleSize = scale;
                    bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
                    int height = bitmap.getHeight();
                    int width = bitmap.getWidth();

                    double y = Math.sqrt(IMAGE_MAX_SIZE
                            / (((double) width) / height));
                    double x = (y / height) * width;
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) x,
                            (int) y, true);
                    bitmap.recycle();
                    bitmap = scaledBitmap;
                    System.gc();
                } else {
                    bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);

                }
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
    public void onDetach() {
        super.onDetach();
        bitmap.recycle();
        bitmap = null;
        Runtime.getRuntime().gc();
        System.gc();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        Context context = getActivity();
        SharedPreferences prefs = context.getSharedPreferences(getString(R.string.preferencefile), Context.MODE_PRIVATE);
        String admin = prefs.getString("admin", "");
        String File = "mess/image.jpg";
        if (admin.equals(getString(R.string.admin_key)))
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
                progressDialog.setMessage("Uploading...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setProgressNumberFormat(null);
                progressDialog.setProgressPercentFormat(null);
                progressDialog.setIndeterminate(true);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
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
