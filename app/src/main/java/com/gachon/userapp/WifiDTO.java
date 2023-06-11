package com.gachon.userapp;

public class WifiDTO {

    private String ssid;
    private String bssid;
    private String rss;

    public WifiDTO(String ssid, String bssid, String rss) {

        this.ssid = ssid;
        this.bssid = bssid;
        this.rss = rss;
    }

    public String getSSID() {
        return ssid;
    }

    public String getBSSID() {
        return bssid;
    }

    public String getRSSI() {
        return rss;
    }

}

    // setter 생략
