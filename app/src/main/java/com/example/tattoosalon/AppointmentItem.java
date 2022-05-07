package com.example.tattoosalon;


public class AppointmentItem {
    private String id;
    private String date;
    private String time;
    private String guestEmail;
    private boolean reserved;

    public AppointmentItem(String date, String time, String guestEmail, boolean reserved) {
        this.date = date;
        this.time = time;
        this.guestEmail = guestEmail;
        this.reserved = false;
    }

    public AppointmentItem() {
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String _getId() {
        return id;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public void setId(String id) {
        this.id = id;
    }
}
