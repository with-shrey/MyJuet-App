package app.myjuet.com.myjuet.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by .
 */
@Singleton
public class SharedPreferencesUtil {
    // file name
    private  final String PREF_NAME = "app_myjuet_com_myjuet_preferences";
    private static   SharedPreferencesUtil mInstance;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    @Inject
    public SharedPreferencesUtil(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public static synchronized SharedPreferencesUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPreferencesUtil(context);
        }
        return mInstance;
    }

    public  void savePreferences(String key,
                                       Float value) {
       
        editor = pref.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public  void savePreferences(String key,
                                       String value) {
        editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public  void savePreferences(String key,
                                       Integer value) {
       
        editor = pref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public  void savePreferences(String key,
                                       Boolean value) {
       
        editor = pref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public  void deletePreferences(String key) {
       
        editor = pref.edit();
        editor.remove(key);
        editor.apply();
    }

    public  Boolean getPreferences(String key, boolean defValue) {
        
        Boolean savedPref = pref.getBoolean(key, defValue);
        return savedPref;
    }

    public  String getPreferences(String key, String defValue) {
        
        String savedPref = pref.getString(key, defValue);
        return savedPref;
    }

    public  Integer getPreferences(String key, int defValue) {
        
        Integer savedPref = pref.getInt(key, defValue);
        return savedPref;
    }

    public  void clearAllSharedPreferencesList(Context context) {
       
        SharedPreferences.Editor sEdit = pref.edit();
        sEdit.clear();
        sEdit.apply();
    }



}

