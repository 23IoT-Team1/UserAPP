package com.gachon.userapp;

import java.util.HashMap;

public class ReferencePointDTO {

    private final String rp;
    private final String place;
    private final int x;
    private final int y;
    private final HashMap<String, Double> connect;

    public ReferencePointDTO(String rp, String place, int x, int y, HashMap<String, Double> connect) {
        this.rp = rp;
        this.place = place;
        this.x = x;
        this.y = y;
        this.connect = connect;
    }

    public String getRp() {
        return rp;
    }

    public String getPlace() {
        return place;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public HashMap<String, Double> getConnect() {
        return connect;
    }
}
