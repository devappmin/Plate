package com.petabyte.plate.data;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiningMasterData {
    private int bookmark;
    private int price;
    private String date;
    private String title;
    private String subtitle;
    private String description;
    private List<String> dishes;
    private List<String> images;
    private List<String> style;
    private HashMap<String, Integer> count;
    private HashMap<String, String> location;
    private HashMap<String, Double> coordinate;
    private HashMap<String, Map<String, Double>> schedules;

    @Exclude private int dishCount = 0;
    @Exclude private int imageCount = 0;

    public DiningMasterData() {
    }

    public DiningMasterData(int bookmark, int price, String title, String subtitle, String description) {
        this.bookmark = bookmark;
        this.price = price;
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;

        date = "";
        count = new HashMap<>();
        dishes =  new ArrayList<>();
        images = new ArrayList<>();
        style = new ArrayList<>();
        coordinate = new HashMap<>();
        location = new HashMap<>();
        schedules = new HashMap<>();
    }

    public DiningMasterData(int bookmark, int price, String date, String title, String subtitle, String description, List<String> dishes,
                            List<String> images, List<String> style, HashMap<String, Integer> count, HashMap<String, String> location,
                            HashMap<String, Double> coordinate, HashMap<String, Map<String, Double>> schedules, int dishCount, int imageCount) {
        this.bookmark = bookmark;
        this.price = price;
        this.date = date;
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.dishes = dishes;
        this.images = images;
        this.style = style;
        this.count = count;
        this.location = location;
        this.coordinate = coordinate;
        this.schedules = schedules;
        this.dishCount = dishCount;
        this.imageCount = imageCount;
    }

    @Exclude
    public List<FoodStyle> getFoodStyle() {
        List<FoodStyle> styleList = new ArrayList<>();
        for (String style : style) {
            styleList.add(FoodStyle.getFoodStyle(style));
        }
        return styleList;
    }

    @Exclude
    public void addDishes(String[] dishList) {
        dishes.addAll(Arrays.asList(dishList));
    }

    @Exclude
    public void addCount(int current, int max) {
        count.put("Current", current);
        count.put("Max", max);
    }

    @Exclude
    public void addImages(String[] imageList) {
        images.addAll(Arrays.asList(imageList));
    }

    @Exclude
    public void addLocation(double x, double y) {
        location.put("x", String.valueOf(x));
        location.put("y", String.valueOf(y));
    }

    @Exclude
    public void addSchedules(List<Double> ends, List<Double> starts) {
        Map<String, Double> singleSchedules = new HashMap<>();

        for (int i = 0; i < ends.size(); i++) {
            singleSchedules.put("start", starts.get(i));
            singleSchedules.put("end", ends.get(i));
        }

        for (int i = 0; i < singleSchedules.size(); i++) {
            schedules.put(String.valueOf(i), singleSchedules);
        }
    }

    public int getBookmark() {
        return bookmark;
    }

    public void setBookmark(int bookmark) {
        this.bookmark = bookmark;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getDishes() {
        return dishes;
    }

    public void setDishes(List<String> dishes) {
        this.dishes = dishes;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public HashMap<String, Integer> getCount() {
        return count;
    }

    public void setCount(HashMap<String, Integer> count) {
        this.count = count;
    }

    public HashMap<String, String> getLocation() {
        return location;
    }

    public void setLocation(HashMap<String, String> location) {
        this.location = location;
    }

    public HashMap<String, Double> getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(HashMap<String, Double> coordinate) {
        this.coordinate = coordinate;
    }

    public HashMap<String, Map<String, Double>> getSchedules() {
        return schedules;
    }

    public void setSchedules(HashMap<String, Map<String, Double>> schedules) {
        this.schedules = schedules;
    }

    public List<String> getStyle() {
        return style;
    }

    public void setStyle(List<String> style) {
        this.style = style;
    }

    public int getDishCount() {
        return dishCount;
    }

    public void setDishCount(int dishCount) {
        this.dishCount = dishCount;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }
}