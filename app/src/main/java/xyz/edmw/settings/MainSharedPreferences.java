package xyz.edmw.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import xyz.edmw.R;

/**
 * Created by Jun Qin on 10/1/2016.
 */
public class MainSharedPreferences {
    public static final String PREF_THEME_ID = "pref_themeId";
    public static final String PREF_DOWNLOAD_IMAGE = "pref_downloadImage";
    private static Boolean loadImageAutomatically;

    private Context context;
    private SharedPreferences preferences;

    public MainSharedPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
        loadImageAutomatically = preferences.getBoolean("image_load_check", true);
    }

    public MainSharedPreferences(Context context) {
        this.context = context;
        preferences =PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getThemeId() {
        String defValue = context.getString(R.string.pref_themeIds_default);
        String theme = preferences.getString(PREF_THEME_ID, defValue);
        switch (theme) {
            case "Black":
                return R.style.AppTheme_Black;
            case "Default":
            default:
                return R.style.AppTheme;
        }
    }

    public DownloadImage getDownloadImage() {
        String defValue = context.getString(R.string.pref_downloadImages_default);
        String downloadImage = preferences.getString(PREF_DOWNLOAD_IMAGE, defValue);
        return DownloadImage.getEnum(downloadImage);
    }

}
