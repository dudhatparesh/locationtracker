package com.enggdream.locationtracker.model;

import java.util.HashMap;
import java.util.Map;

public class LocationLog {
    private int battery;
    private double longitude;
    private double latitude;
    private double accuracy;
    private int workId;
    private String apiKey;
    private String locationTime;
    private String locationName;
    private double speed;
    private double altitude;

    private long locationTimeLong;
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("battery", battery);
        map.put("longitude", longitude);
        map.put("latitude", latitude);
        map.put("accuracy", accuracy);
        map.put("workId", workId);
        map.put("apiKey", apiKey);
        map.put("locationTime", locationTime);
        map.put("locationName", locationName);
        map.put("speed", speed);
        map.put("altitude", altitude);
        return map;
    }

    public LocationLog(int battery, double longitude, double latitude,
                       double accuracy, int workId, String apiKey,
                       String locationTime, double speed, double altitude, long locationTimeLong) {
        this.battery = battery;
        this.longitude = longitude;
        this.latitude = latitude;
        this.accuracy = accuracy;
        this.workId = workId;
        this.apiKey = apiKey;
        this.locationTime = locationTime;
        this.altitude = altitude;
        this.speed = speed;
        this.locationTimeLong = locationTimeLong;
    }

}
