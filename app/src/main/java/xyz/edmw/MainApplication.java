package xyz.edmw;

import android.app.Application;
import android.content.Context;

public class MainApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        if (context == null) {
            throw new NullPointerException();
        }
        return context;
    }
}
