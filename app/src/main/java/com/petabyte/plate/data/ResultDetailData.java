package com.petabyte.plate.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.database.Exclude;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultDetailData {
    private int price;
    private String date;
    private String title;
    private String subtitle;
    private List<String> images;
    private List<String> style;
    private HashMap<String, String> location;
    private HashMap<String, Long> count;
    private HashMap<String, Map<String, Long>> schedules;

    @Exclude
    private String diningUID;

    public ResultDetailData() {
    }

    public ResultDetailData(int price, String date, String title, String subtitle,
                            HashMap<String, String> location, HashMap<String, Long> count,
                            HashMap<String, Map<String, Long>> schedules, List<String> images, List<String> style) {
        this.price = price;
        this.date = date;
        this.title = title;
        this.subtitle = subtitle;
        this.location = location;
        this.count = count;
        this.schedules = schedules;
        this.images = images;
        this.style = style;
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

    public HashMap<String, Long> getCount() {
        return count;
    }

    public void setCount(HashMap<String, Long> count) {
        this.count = count;
    }

    public HashMap<String, Map<String, Long>> getSchedules() {
        return schedules;
    }

    public void setSchedules(HashMap<String, Map<String, Long>> schedules) {
        this.schedules = schedules;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getStyle() {
        return style;
    }

    public void setStyle(List<String> style) {
        this.style = style;
    }
}