package com.petabyte.plate.data;

public class HomeAwardsData {
    private String title;
    private String cardImage;
    private String mainImage;

    public HomeAwardsData() {
    }

    public HomeAwardsData(String title, String cardImage, String mainImage) {
        this.title = title;
        this.cardImage = cardImage;
        this.mainImage = mainImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCardImage() {
        return cardImage;
    }

    public void setCardImage(String cardImage) {
        this.cardImage = cardImage;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }
}
