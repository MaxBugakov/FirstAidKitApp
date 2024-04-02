package com.example.myapplication2;

import android.app.Application;
import com.orm.SugarContext;
import com.orm.SugarDb;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}