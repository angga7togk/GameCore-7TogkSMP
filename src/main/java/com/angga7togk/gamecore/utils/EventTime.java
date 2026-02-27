package com.angga7togk.gamecore.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class EventTime {

    private static final ZoneId JAKARTA = ZoneId.of("Asia/Jakarta");

    public static int getHour() {
        return LocalDateTime.now(JAKARTA).getHour();
    }

    public static int getMinute() {
        return LocalDateTime.now(JAKARTA).getMinute();
    }

    public static boolean isStartEventTime(int targetHour, int targetMinute) {
        return getHour() == targetHour && getMinute() == targetMinute;
    } 

    public static boolean isEventTime(int startHour, int startMinute, int endHour, int endMinute) {
        int now = getHour() * 60 + getMinute();
        int start = startHour * 60 + startMinute;
        int end = endHour * 60 + endMinute;

        // Case normal: contoh 12:00 - 12:30
        if (start <= end) {
            return now >= start && now <= end;
        }

        // Case lintas hari: contoh 23:50 - 00:10
        return now >= start || now <= end;
    }

}
