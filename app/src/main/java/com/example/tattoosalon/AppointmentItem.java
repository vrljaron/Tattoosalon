package com.example.tattoosalon;


public class AppointmentItem {
    private String date;
    private String time;

    public AppointmentItem(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
