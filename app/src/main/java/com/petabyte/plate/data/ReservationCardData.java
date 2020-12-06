package com.petabyte.plate.data;

import android.util.Log;

public class ReservationCardData {
    private String status;
    private String title;
    private String timestamp;
    private String location;

    public ReservationCardData() {}
    public ReservationCardData(String status, String title, String timestamp, String location) {
        this.status = status;
        this.title = title;
        this.timestamp = timestamp;
        this.location = location;
        Log.d("ji1dev", "new data created");
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String description) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
}
