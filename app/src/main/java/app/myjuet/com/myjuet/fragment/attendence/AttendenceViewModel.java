package app.myjuet.com.myjuet.fragment.attendence;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import app.myjuet.com.myjuet.R;
import app.myjuet.com.myjuet.data.AttendenceData;
import app.myjuet.com.myjuet.database.AppDatabase;
import app.myjuet.com.myjuet.repository.AttendenceRepository;
import app.myjuet.com.myjuet.repository.AuthRepository;
import app.myjuet.com.myjuet.utilities.AppExecutors;
import app.myjuet.com.myjuet.utilities.Constants;

public class AttendenceViewModel extends ViewModel {
    private AttendenceRepository mAttendenceRepository;
    MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    MutableLiveData<Integer> mEmptyImage = new MutableLiveData<>();
    @Inject
    public AttendenceViewModel(AttendenceRepository repository) {
        mAttendenceRepository = repository;
//        context = application;
    }

    public LiveData<List<AttendenceData>> getAttendenceData(){


        return Transformations.map(mAttendenceRepository.getAttendenceData(),(list) -> {
            if (list.size() == 0){
                mEmptyImage.setValue(R.drawable.attendence_nodata);
            }else{
                mEmptyImage.setValue(null);
            }
           return list;
        });
    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }

    public void setLoading(){
        mIsLoading.setValue(true);
    }

    public void setLoading(boolean b) {
        mIsLoading.setValue(b);
    }

    public MutableLiveData<Integer> getEmptyImage() {
        return mEmptyImage;
    }
}
