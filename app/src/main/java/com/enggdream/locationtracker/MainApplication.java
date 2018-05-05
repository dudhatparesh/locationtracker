package com.enggdream.locationtracker;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MainApplication extends Application {
    private static MainApplication mInstance;
    private static FirebaseDatabase firebaseDatabase;
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        mInstance = this;
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        Fabric.with(this, new Crashlytics());
    }

    public static MainApplication getInstance() {
        return mInstance;
    }
    public static FirebaseDatabase getFirebaseDatabase(){
        return  firebaseDatabase;
    }
}
