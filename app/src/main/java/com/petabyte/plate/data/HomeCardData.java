package com.petabyte.plate.data;

import android.net.Uri;

public class HomeCardData {
    private String title;
    private String subTitle;
    private String description;
    private String imageUri;
    public HomeCardData() {
    }

    public HomeCardData(String title, String subTitle, String description, String imageUri) {
        this.title = title;
        this.description = description;
        this.imageUri = imageUri;
        this.subTitle = subTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
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
}
