package com.angga7togk.gamecore.utils;

public class TimeUtils {

    // ================= NORMALIZED =================

    // Detik sisa (0–59)
    public static int getSeconds(long seconds) {
        return (int) (seconds % 60);
    }

    // Menit sisa (0–59)
    public static int getMinutes(long seconds) {
        return (int) ((seconds / 60) % 60);
    }

    // Jam sisa (0–23)
    public static int getHours(long seconds) {
        return (int) ((seconds / 60 / 60) % 24);
    }

    // Hari sisa (tembus)
    public static int getDays(long seconds) {
        return (int) (seconds / 60 / 60 / 24);
    }

    // ================= TOTAL =================

    public static long getTotalSeconds(long seconds) {
        return seconds;
    }

    public static int getTotalMinutes(long seconds) {
        return (int) (seconds / 60);
    }

    public static int getTotalHours(long seconds) {
        return (int) (seconds / 60 / 60);
    }

    public static int getTotalDays(long seconds) {
        return (int) (seconds / 60 / 60 / 24);
    }
}
