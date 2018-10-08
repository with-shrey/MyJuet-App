package app.myjuet.com.myjuet.services;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class jobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        ActivityCompat.startForegroundService(this,new Intent(this, RefreshService.class));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
