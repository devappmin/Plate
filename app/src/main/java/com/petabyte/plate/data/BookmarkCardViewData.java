package com.petabyte.plate.data;

import android.net.Uri;

public class BookmarkCardViewData {
    private String diningName;
    private String diningDate;
    private String diningLocation;
    private String imageUri;

    public BookmarkCardViewData() {
    }

    public BookmarkCardViewData(String diningName, String diningDate, String diningLocation, String imageUri) {
        this.diningName = diningName;
        this.diningDate = diningDate;
        this.diningLocation = diningLocation;
        this.imageUri = imageUri;
    }

    public String getDiningName() {
        return diningName;
    }

    public void setDiningName(String diningName) {
        this.diningName = diningName;
    }

    public String getDiningDate() {
        return diningDate;
    }

    public void setDiningDate(String diningDate) {
        this.diningDate = diningDate;
    }

    public String getDiningLocation() { return diningLocation; }

    public void setDiningLocation(String diningLocation) {
        this.diningLocation = diningLocation;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
