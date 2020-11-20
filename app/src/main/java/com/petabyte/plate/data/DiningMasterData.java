package com.petabyte.plate.data;

public class DiningMasterData {
    private int bookMark;
    private String description;
    private String[] images;
    private int countCurrent;
    private int countMax;
    private String subtitle;

    public DiningMasterData() {
    }

    public DiningMasterData(int bookMark, String description, String[] images, int countCurrent, int countMax, String subtitle) {
        this.bookMark = bookMark;
        this.description = description;
        this.images = images;
        this.countCurrent = countCurrent;
        this.countMax = countMax;
        this.subtitle = subtitle;
    }

    public int getBookMark() {
        return bookMark;
    }

    public void setBookMark(int bookMark) {
        this.bookMark = bookMark;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public int getCountCurrent() {
        return countCurrent;
    }

    public void setCountCurrent(int countCurrent) {
        this.countCurrent = countCurrent;
    }

    public int getCountMax() {
        return countMax;
    }

    public void setCountMax(int countMax) {
        this.countMax = countMax;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}