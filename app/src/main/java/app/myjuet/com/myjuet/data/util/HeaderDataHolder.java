package app.myjuet.com.myjuet.data.util;

import android.view.View;

import androidx.navigation.NavController;

import app.myjuet.com.myjuet.R;

public class HeaderDataHolder {
    private final NavController mNavController;
    String name;

    public HeaderDataHolder(NavController navController,String name) {
        this.name = name;
        this.mNavController = navController;
    }

    public String getName() {
        return name;
    }

    public void clickListner(View view) {
       mNavController.navigate(R.id.settings_master);
    }
}
