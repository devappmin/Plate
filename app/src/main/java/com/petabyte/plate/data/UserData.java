package com.petabyte.plate.data;

import java.util.ArrayList;
import java.util.HashMap;

public class UserData {
    private ArrayList<String> Bookmark;
    private HashMap<String, String> MyDining;

    public UserData() {

    }

    public UserData(ArrayList<String> Bookmark, HashMap<String, String> MyDining) {
        this.Bookmark = Bookmark;
        this.MyDining = MyDining;
    }

    public ArrayList<String> getBookmark() {
        return Bookmark;
    }

    public void setBookmark(ArrayList<String> Bookmark) {
        this.Bookmark = Bookmark;
    }

    public HashMap<String, String> getMyDining() {
        return MyDining;
    }

    public void setMyDining(HashMap<String, String> MyDining) {
        this.MyDining = MyDining;
    }
}
