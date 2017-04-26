package app.myjuet.com.myjuet;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by Shrey on 27-Apr-17.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
