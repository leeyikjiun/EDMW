package xyz.edmw.sharedpreferences;

import android.content.SharedPreferences;

/**
 * Created by Jun Qin on 10/1/2016.
 */
public class MainSharedPreferences {

    private SharedPreferences preferences;

    private static Boolean loadImageAutomatically;

    public MainSharedPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
        loadImageAutomatically = preferences.getBoolean("image_load_check", true);
    }

    public static Boolean getLoadImageAutomatically() {
        return loadImageAutomatically;
    }

    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor  = preferences.edit();;
        editor.putBoolean(key, value);
        editor.apply();

        // Update
        loadImageAutomatically = value;
    }
}
