package com.petabyte.plate.data;

public class BookmarkCardViewData implements Comparable<BookmarkCardViewData> {
    private String diningTitle;
    private String diningSubtitle;
    private String diningDate;
    private String diningLocation;
    private String diningDetailLocation;
    private String imageName;
    private String diningUID;

    @Override
    public int compareTo(BookmarkCardViewData o) {
        return this.getDiningDate().compareTo(o.getDiningDate());
    }

    public BookmarkCardViewData(String diningTitle, String diningSubtitle, String diningDate, String diningLocation, String diningDetailLocation, String imageName, String diningUID) {
        this.diningTitle = diningTitle;
        this.diningSubtitle = diningSubtitle;
        this.diningDate = diningDate;
        this.diningLocation = diningLocation;
        this.diningDetailLocation = diningDetailLocation;
        this.imageName = imageName;
        this.diningUID = diningUID;
    }

    public String getDiningTitle() { return diningTitle; }

    public void setDiningTitle(String diningTitle) { this.diningTitle = diningTitle; }

    public String getDiningSubtitle() { return diningSubtitle; }

    public void setDiningSubtitle(String diningSubtitle) { this.diningTitle = diningSubtitle; }

    public String getDiningDate() { return diningDate; }

    public void setDiningDate(String diningDate) { this.diningDate = diningDate; }

    public String getDiningLocation() { return diningLocation; }

    public void setDiningLocation(String diningLocation) { this.diningLocation = diningLocation; }

    public String getDiningDetailLocation() { return diningDetailLocation; }

    public void setDiningDetailLocation(String diningDetailLocation) { this.diningDetailLocation = diningDetailLocation; }

    public String getImageName() { return imageName; }

    public void setImageName(String imageName) { this.imageName = imageName; }

    public String getDiningUID() { return diningUID; }

    public void setDiningUID(String diningUID) { this.diningUID = diningUID; }
}
