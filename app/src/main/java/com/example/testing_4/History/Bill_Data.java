package com.example.testing_4.History;


public class Bill_Data {
    private String total;
    private String location;
    private String date;
    private String friend_list;
    private String value_list;
    private String myValue;

    public Bill_Data(){

    }

    public Bill_Data(String date, String friend_list, String location , String myValue, String total, String value_list) {
        this.date = date;
        this.friend_list = friend_list;
        this.location = location;
        this.myValue = myValue;
        this.total = total;
        this.value_list = value_list;

    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFriend_list() {
        return friend_list;
    }

    public void setFriend_list(String friend_list) {
        this.friend_list = friend_list;
    }

    public String getValue_list() {
        return value_list;
    }

    public void setValue_list(String value_list) {
        this.value_list = value_list;
    }

    public String getMyValue() {
        return myValue;
    }

    public void setMyValue(String myValue) {this.myValue = myValue;
    }
}
