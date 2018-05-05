package com.enggdream.locationtracker;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {
    private static final String PREF_NAME ="location_tracker";
    private static final String KEY_MOBILE_NO = "mobile_no";
    private static final String IS_TRACKING_ON = "is_tracking_on";

    public static String getMobileNo(Context context,String defaultValue){
        return getData(context,KEY_MOBILE_NO,defaultValue);
    }
    public static void setMobileNo(Context context,String mobileNo){
        setData(context, KEY_MOBILE_NO,mobileNo);
    }

    public static boolean isTrackingOn(Context context, boolean defaultValue){
        String value = getData(context, IS_TRACKING_ON,String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }
    public static void setTracking(Context context, boolean value){
        setData(context, IS_TRACKING_ON,String.valueOf(value));
    }
    private static String getData(Context context, String key, String defaultValue){
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return preferences.getString(key,defaultValue);
    }

    private static void setData(Context context, String key, String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }
}
