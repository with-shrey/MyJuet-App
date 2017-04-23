package app.myjuet.com.myjuet.timetable;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by Shrey on 15-Apr-17.
 */

public abstract class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

    static boolean userSelect = false;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        userSelect = true;
        return false;
    }

    @Override
    public abstract void onItemSelected(AdapterView<?> parent, View view, int pos, long id);

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}
