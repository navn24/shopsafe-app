package com.navn.safeshop;

import android.app.Application;
import android.content.Context;

public class GlobalAppClass extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

}