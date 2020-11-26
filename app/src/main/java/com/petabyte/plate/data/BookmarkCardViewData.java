package com.petabyte.plate.data;

public class BookmarkCardViewData {
    private String diningTitle;
    private String diningSubtitle;
    private String diningDate;
    private String diningTime;
    private String diningLocation;
    private String diningDetailLocation;
    private String imageUri;

    public BookmarkCardViewData(String diningTitle, String diningSubtitle, String diningDate, String diningTime, String diningLocation, String diningDetailLocation, String imageUri) {
        this.diningTitle = diningTitle;
        this.diningSubtitle = diningSubtitle;
        this.diningDate = diningDate;
        this.diningTime = diningTime;
        this.diningLocation = diningLocation;
        this.diningDetailLocation = diningDetailLocation;
        this.imageUri = imageUri;
    }

    public String getDiningTitle() { return diningTitle; }

    public void setDiningTitle(String diningTitle) { this.diningTitle = diningTitle; }

    public String getDiningSubtitle() { return diningSubtitle; }

    public void setDiningSubtitle(String diningSubtitle) { this.diningTitle = diningSubtitle; }

    public String getDiningDate() { return diningDate; }

    public void setDiningDate(String diningDate) { this.diningDate = diningDate; }

    public String getDiningTime() { return diningTime; }

    public void setDiningTime(String diningTime) { this.diningTime = diningTime; }

    public String getDiningLocation() { return diningLocation; }

    public void setDiningLocation(String diningLocation) { this.diningLocation = diningLocation; }

    public String getDiningDetailLocation() { return diningDetailLocation; }

    public void setDiningDetailLocation(String diningDetailLocation) { this.diningDetailLocation = diningDetailLocation; }

    public String getImageUri() { return imageUri; }

    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
}
