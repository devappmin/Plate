package com.petabyte.plate.data;

public class DiningManagementCardData implements Comparable<DiningManagementCardData>{

    private String diningTitle;
    private String diningDate;
    private String diningLocation;
    private String diningUid;
    private int currentReservationCount;

    @Override
    public int compareTo(DiningManagementCardData o) {
        return o.getDiningDate().compareTo(this.getDiningDate());
    }

    public DiningManagementCardData() {

    }

    public DiningManagementCardData(String diningTitle, String diningDate, String diningLocation, String diningUid, int currentReservationCount) {
        this.diningTitle = diningTitle;
        this.diningDate = diningDate;
        this.diningLocation = diningLocation;
        this.diningUid = diningUid;
        this.currentReservationCount = currentReservationCount;
    }

    public String getDiningTitle() {
        return diningTitle;
    }

    public void setDiningTitle(String diningTitle) {
        this.diningTitle = diningTitle;
    }

    public String getDiningDate() {
        return diningDate;
    }

    public void setDiningDate(String diningDate) {
        this.diningDate = diningDate;
    }

    public String getDiningLocation() {
        return diningLocation;
    }

    public void setDiningLocation(String diningLocation) {
        this.diningLocation = diningLocation;
    }

    public String getDiningUid() { return diningUid; }

    public void setDiningUid(String diningUid) { this.diningUid = diningUid; }

    public int getCurrentReservationCount() { return currentReservationCount; }

    public void setCurrentReservationCount(int currentReservationCount) { this.currentReservationCount = currentReservationCount; }
}
