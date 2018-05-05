package com.enggdream.locationtracker;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {
    public static LocationRequest getLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return  mLocationRequest;
    }
    public static LocationSettingsRequest getLocationSettingBuilder(){

        return new LocationSettingsRequest.Builder()
                .addLocationRequest(getLocationRequest()).build();
    }
    public static int getBatteryLevel(Context context) {
        Intent batteryStatusIntent = context.registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    }

    public static String parseLongToServerDateTime(long longDateTime) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        /* simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));*/
        String dateString = simpleDateFormat.format(new Date(longDateTime));
        return dateString;
    }
}
