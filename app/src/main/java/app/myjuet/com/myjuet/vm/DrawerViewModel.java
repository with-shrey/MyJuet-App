package app.myjuet.com.myjuet.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class DrawerViewModel extends ViewModel {
    public MutableLiveData<Boolean> fabVisible;
    public DrawerViewModel() {
        fabVisible = new MutableLiveData<>();
        fabVisible.setValue(true);
    }

    public LiveData<Boolean> getFabVisible() {
        return fabVisible;
    }

    public void setFabVisible(boolean fabVisible) {
        this.fabVisible.setValue(fabVisible);
    }
}
