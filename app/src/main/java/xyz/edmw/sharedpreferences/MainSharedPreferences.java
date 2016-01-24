package xyz.edmw.sharedpreferences;

import android.content.SharedPreferences;

import xyz.edmw.R;

/**
 * Created by Jun Qin on 10/1/2016.
 */
public class MainSharedPreferences {
    private SharedPreferences preferences;

    public static final String PREF_THEME_ID = "pref_themeId";
    private static Boolean loadImageAutomatically;

    public MainSharedPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
        loadImageAutomatically = preferences.getBoolean("image_load_check", true);
    }

    public static boolean getLoadImageAutomatically() {
        return loadImageAutomatically;
    }

    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor  = preferences.edit();;
        editor.putBoolean(key, value);
        editor.apply();

        // Update
        loadImageAutomatically = value;
    }

    public int getThemeId() {
        String theme = preferences.getString(PREF_THEME_ID, "Default");
        switch (theme) {
            case "Black":
                return R.style.AppTheme_Black;
            case "Default":
            default:
                return R.style.AppTheme;
        }
    }

}
