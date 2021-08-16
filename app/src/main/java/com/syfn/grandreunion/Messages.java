package com.syfn.grandreunion;

public class Messages {
    private String name, message, time, date, senderId, type;

    public Messages(String name, String message, String time, String date, String senderId, String type) {
        this.name = name;
        this.message = message;
        this.time = time;
        this.date = date;
        this.senderId = senderId;
        this.type = type;
    }

    public Messages(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
