package com.petabyte.plate.data;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiningMasterData {
    private int Bookmark;
    private int Price;
    private String Title;
    private String Subtitle;
    private String Description;
    private String Location;
    private String DetailLocation;
    private Map<String, Integer> Count;
    private Map<String, String> Dishes;
    private Map<String, String> Images;
    private Map<String, Double> Coordinate;
    private Map<String, Map<String, Double>> Schedules;
    private int dishCount = 0;
    private int imageCount = 0;

    public DiningMasterData() {
    }

    public DiningMasterData(int bookmark, int price, String title, String subtitle, String description) {
        Bookmark = bookmark;
        Price = price;
        Title = title;
        Subtitle = subtitle;
        Description = description;

        Location = "";
        DetailLocation = "";
        Dishes = new HashMap<>();
        Images = new HashMap<>();
        Count = new HashMap<>();
        Coordinate = new HashMap<>();
        Schedules = new HashMap<>();
    }

    public DiningMasterData(int bookmark, int price, String title, String subtitle, String description, Map<String, Integer> count, Map<String, String> dishes, Map<String, String> images, Map<String, Double> coordinate, Map<String, Map<String, Double>> schedules) {
        Bookmark = bookmark;
        Price = price;
        Title = title;
        Subtitle = subtitle;
        Description = description;
        Count = count;
        Dishes = dishes;
        Images = images;
        Coordinate = coordinate;
        Schedules = schedules;

        Location = "";
        DetailLocation = "";
    }

    @Exclude
    public void addDishes(String[] dishList) {

        for(String dish : dishList)
            Dishes.put(++dishCount + "", dish);

    }

    @Exclude
    public void addCount(int current, int max) {
        Count.put("Current", current);
        Count.put("Max", max);
    }

    @Exclude
    public void addImages(String[] imageList) {
        for (String image : imageList)
            Images.put(++imageCount + "", image);
    }

    @Exclude
    public void addCoordinate(double x, double y) {
        Coordinate.put("x", x);
        Coordinate.put("y", y);
    }

    @Exclude
    public void addSchedules(List<Double> ends, List<Double> starts) {
        Map<String, Double> singleSchedules = new HashMap<>();

        for (int i = 0; i < ends.size(); i++) {
            singleSchedules.put("start", starts.get(i));
            singleSchedules.put("end", ends.get(i));
        }

        for (int i = 0; i < singleSchedules.size(); i++) {
            Schedules.put(i + "+", singleSchedules);
        }
    }

    public int getBookmark() {
        return Bookmark;
    }

    public void setBookmark(int bookmark) {
        Bookmark = bookmark;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getSubtitle() {
        return Subtitle;
    }

    public void setSubtitle(String subtitle) {
        Subtitle = subtitle;
    }

    public String getDescription() {
        return Description;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getDetailLocation() {
        return DetailLocation;
    }

    public void setDetailLocation(String detailLocation) {
        DetailLocation = detailLocation;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Map<String, Integer> getCount() {
        return Count;
    }

    public void setCount(Map<String, Integer> count) {
        Count = count;
    }

    public Map<String, String> getDishes() {
        return Dishes;
    }

    public void setDishes(Map<String, String> dishes) {
        Dishes = dishes;
    }

    public Map<String, String> getImages() {
        return Images;
    }

    public void setImages(Map<String, String> images) {
        Images = images;
    }

    public Map<String, Double> getCoordinate() {
        return Coordinate;
    }

    public void setCoordinate(Map<String, Double> coordinate) {
        Coordinate = coordinate;
    }

    public Map<String, Map<String, Double>> getSchedules() {
        return Schedules;
    }

    public void setSchedules(Map<String, Map<String, Double>> schedules) {
        Schedules = schedules;
    }
}