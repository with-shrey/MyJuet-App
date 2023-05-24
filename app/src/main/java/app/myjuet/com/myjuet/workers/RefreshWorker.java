package app.myjuet.com.myjuet.workers;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import app.myjuet.com.myjuet.services.RefreshService;

public class RefreshWorker extends Worker {
    Context mContext;
    public RefreshWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
       Intent intent = new Intent(mContext,RefreshService.class);
        ActivityCompat.startForegroundService(mContext,intent);
        return Result.success();
    }
}
