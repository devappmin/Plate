package com.petabyte.plate.data;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;

public class ResultDetailData implements Serializable {
    private int price;
    private String date;
    private String title;
    private String subtitle;
    private HashMap<String, String> location;

    @Exclude private String diningUID;

    public ResultDetailData() {
    }

    public ResultDetailData(int price, String date, String title, String subtitle, HashMap<String, String> location) {
        this.price = price;
        this.date = date;
        this.title = title;
        this.subtitle = subtitle;
        this.location = location;
    }

    @Exclude
    public String getDiningUID() {
        return diningUID;
    }

    @Exclude
    public void setDiningUID(String diningUID) {
        this.diningUID = diningUID;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public HashMap<String, String> getLocation() {
        return location;
    }

    public void setLocation(HashMap<String, String> location) {
        this.location = location;
    }
}
