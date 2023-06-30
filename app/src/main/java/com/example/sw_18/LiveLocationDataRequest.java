package com.example.sw_18;

public class LiveLocationDataRequest {
    private String promiseId;
    private String userId;
    private double latitude;
    private double longitude;

    public LiveLocationDataRequest(String promiseId, String userId, double latitude, double longitude) {
        this.promiseId = promiseId;
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Add getters and setters if needed
    // ...
}