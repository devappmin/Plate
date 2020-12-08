package com.petabyte.plate.data;
import java.util.List;

public class ReservationCardData {
    private String memtype;
    private String status;
    private String title;
    private String timestamp;
    private String location;
    private String timekey;
    private List<String> dishes;

    public ReservationCardData(String memtype, String timekey, String status, String title, String timestamp, String location, List<String> dishes) {
        this.memtype = memtype;             //멤버타입
        this.timekey = timekey;             //예약이 등록된 서버 시간
        this.status = status;               //현재 상태 (입금대기중-결제완료-예약취소)
        this.title = title;                 //다이닝 이름
        this.timestamp = timestamp;         //예약시간
        this.location = location;           //다이닝 위치
        this.dishes = dishes;               //다이닝 코스 목록
    }

    public String getMemtype() {
        return memtype;
    }
    public void setMemtype(String memtype) {
        this.memtype = memtype;
    }

    public String getTimeKey() {
        return timekey;
    }
    public void setTimeKey(String timekey) {
        this.timekey = timekey;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String description) {
        this.timestamp = timestamp;
    }

    public List<String> getDishes() {
        return dishes;
    }
    public void setDishes(List<String> dishes) {
        this.dishes = dishes;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
}
