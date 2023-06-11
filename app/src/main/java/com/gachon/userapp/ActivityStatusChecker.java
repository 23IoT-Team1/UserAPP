package com.gachon.userapp;

public class ActivityStatusChecker {
    private static boolean isActivityAlive = false;

    public static void setActivityStatus(boolean isAlive) {
        isActivityAlive = isAlive;
    }

    public static boolean isActivityAlive() {
        return isActivityAlive;
    }
}