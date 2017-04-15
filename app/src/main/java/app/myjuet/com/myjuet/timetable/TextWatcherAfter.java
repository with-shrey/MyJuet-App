package app.myjuet.com.myjuet.timetable;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Shrey on 15-Apr-17.
 */

public abstract class TextWatcherAfter implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public abstract void afterTextChanged(Editable editable);


}
