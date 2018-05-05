package com.enggdream.locationtracker.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.enggdream.locationtracker.CommonUtils;
import com.enggdream.locationtracker.MainActivity;
import com.enggdream.locationtracker.MainApplication;
import com.enggdream.locationtracker.PrefUtils;
import com.enggdream.locationtracker.R;
import com.enggdream.locationtracker.model.LocationLog;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class LocationTrackerService extends JobService {
    FusedLocationProviderClient mFusedLocationClient;
    DatabaseReference mDatabaseRef = MainApplication.getFirebaseDatabase().getReference("location-data");
    boolean isLocationReceived = false;
    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                if(location.getAccuracy()<200){
                    LocationLog locationLog = new LocationLog(
                            CommonUtils.getBatteryLevel(LocationTrackerService.this),
                            location.getLongitude(), location.getLatitude(), location.getAccuracy(),
                            0,
                            "",
                            CommonUtils.parseLongToServerDateTime(location.getTime()),
                            location.hasSpeed() ? location.getSpeed() : -1,
                            location.hasAltitude() ? location.getAltitude() : -1, location.getTime());



                    mDatabaseRef.child("users").child(PrefUtils.getMobileNo(LocationTrackerService.this,"")).child("logs")
                            .child( String.valueOf(location.getTime())).setValue(locationLog.toMap());
                    isLocationReceived = true;
                }
            }
        }
    };
    private String TAG = LocationTrackerService.class.getName();

    @Override
    public boolean onStartJob(JobParameters job) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        startForeground(2, getNotification());
        startLocationUpdates();
        Toast.makeText(this,"onStartJob",Toast.LENGTH_SHORT).show();
        Log.d(TAG,"onStartJobCalled");
        boolean isJobFinished = job.getExtras().getBoolean("is_more_work_needed",true);
        return isJobFinished;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Toast.makeText(this,"onStopJob called",Toast.LENGTH_SHORT).show();
        stopLocationUpdates();
        return true;
    }




    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(CommonUtils.getLocationRequest(),
                mLocationCallback,
                null );
    }

    private Notification getNotification() {
        String channelId = "";
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            channelId = createChannel();
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId);
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_background);
        String title = getString(R.string.app_name);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText("Location Tracker Trial is running");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(intent);
        return notificationBuilder.build();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createChannel(){
        String channelId = "fa_notification_channel";
        String channelName = "Location Tracker";
        NotificationChannel channel = new NotificationChannel(channelId, channelName,
                NotificationManager.IMPORTANCE_HIGH);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        return channelId;
    }



    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
}
