package app.myjuet.com.myjuet.di;


import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import app.myjuet.com.myjuet.fragment.attendence.AttendenceViewModel;
import app.myjuet.com.myjuet.vm.LoginViewModel;
import app.myjuet.com.myjuet.vm.MyJuetViewModelFactory;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(AttendenceViewModel.class)
    abstract ViewModel bindAttendenceViewModel(AttendenceViewModel attendenceViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(MyJuetViewModelFactory factory);
}
