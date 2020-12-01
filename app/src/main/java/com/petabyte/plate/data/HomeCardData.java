package com.petabyte.plate.data;

import android.net.Uri;

public class HomeCardData {
    private String title;
    private String subtitle;
    private String description;
    private String imageUri;
    private long price;

    public HomeCardData() {
    }

    public HomeCardData(String title, String subtitle, String description, String imageUri, long price) {
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.imageUri = imageUri;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
