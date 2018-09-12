package com.uok.se.busguidingsystem;

/**
 * Created by USER on 11-09-2018.
 */

public class Location {
    private String email, lat, lng;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public Location(String email, String lat, String lng) {
        this.email = email;
        this.lat = lat;
        this.lng = lng;
    }

    public  Location(){

    }
}
