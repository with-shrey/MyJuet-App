package app.myjuet.com.myjuet.services;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;


@SuppressLint("NewApi")
public class jobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        startService(new Intent(this, RefreshService.class));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
