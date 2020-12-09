package com.petabyte.plate.data;

import java.util.ArrayList;
import java.util.HashMap;

public class UserData {
    private HashMap<String, String> Bookmark;
    private HashMap<String, String> MyDining;

    public UserData() {

    }

    public UserData(HashMap<String, String> Bookmark, HashMap<String, String> MyDining) {
        this.Bookmark = Bookmark;
        this.MyDining = MyDining;
    }

    public HashMap<String, String> getBookmark() {
        return Bookmark;
    }

    public void setBookmark(HashMap<String, String> Bookmark) {
        this.Bookmark = Bookmark;
    }

    public HashMap<String, String> getMyDining() {
        return MyDining;
    }

    public void setMyDining(HashMap<String, String> MyDining) {
        this.MyDining = MyDining;
    }
}
