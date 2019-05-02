package app.myjuet.com.myjuet.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by .
 */

public class SharedPreferencesUtil {
    public static final int MODE_PRIVATE = 0;
    // file name
    private static final String PREF_NAME = "app_myjuet_com_myjuet_preferences";
    private static SharedPreferencesUtil mInstance;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    public SharedPreferencesUtil(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = pref.edit();
    }

    public static synchronized SharedPreferencesUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPreferencesUtil(context);
        }
        return mInstance;
    }

    public static void savePreferences(Context context, String key,
                                       Float value) {
        SharedPreferences sPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static void savePreferences(Context context, String key,
                                       String value) {
        SharedPreferences sPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void savePreferences(Context context, String key,
                                       Integer value) {
        SharedPreferences sPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void savePreferences(Context context, String key,
                                       Boolean value) {
        SharedPreferences sPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void deletePreferences(Context context, String key) {
        SharedPreferences sPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.remove(key);
        editor.apply();
    }

    public static Boolean getPreferences(Context context, String key, boolean defValue) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Boolean savedPref = sharedPreferences.getBoolean(key, defValue);
        return savedPref;
    }

    public static String getPreferences(Context context, String key, String defValue) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String savedPref = sharedPreferences.getString(key, defValue);
        return savedPref;
    }

    public static Integer getPreferences(Context context, String key, int defValue) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Integer savedPref = sharedPreferences.getInt(key, defValue);
        return savedPref;
    }

    public static void clearAllSharedPreferencesList(Context context) {
        SharedPreferences sPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor sEdit = sPrefs.edit();
        sEdit.clear();
        sEdit.apply();
    }



}

