package com.minebunch.core.utils.time;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeUtil {
    public static String formatTimeMillis(long millis) {
        long seconds = millis / 1000L;

        if (seconds <= 0) {
            return "0 seconds";
        }

        long minutes = seconds / 60;
        seconds = seconds % 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        long days = hours / 24;
        hours = hours % 24;
        long years = days / 365;
        days = days % 365;

        StringBuilder time = new StringBuilder();

        if (years != 0) {
            time.append(parseTimeSpec(years, "year"));
        }

        if (days != 0) {
            time.append(parseTimeSpec(days, "day"));
        }

        if (hours != 0) {
            time.append(parseTimeSpec(hours, "hour"));
        }

        if (minutes != 0) {
            time.append(parseTimeSpec(minutes, "minute"));
        }

        if (seconds != 0) {
            time.append(parseTimeSpec(seconds, "second"));
        }

        return time.toString().trim();
    }

    private static String parseTimeSpec(long time, String spec) {
        return time + " " + (time == 1 ? spec : spec + "s") + " ";
    }

    public static String formatTimeSeconds(long seconds) {
        return formatTimeMillis(seconds * 1000);
    }

    public static String formatTimeMillisToClock(long millis) {
        return millis / 1000L <= 0 ? "0:00" : String.format("%01d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public static String formatTimeSecondsToClock(long seconds) {
        return formatTimeMillisToClock(seconds * 1000);
    }

    public static int parseTime(String time) {
        if (time.equals("0") || time.equals("")) {
            return 0;
        }

        String[] lifeMatch = new String[]{"y", "M", "w", "d", "h", "m", "s"};
        int[] lifeInterval = new int[]{31536000, 2592000, 604800, 86400, 3600, 60, 1};
        int seconds = 0;

        for (int i = 0; i < lifeMatch.length; i++) {
            Matcher matcher = Pattern.compile("([0-9]*)" + lifeMatch[i]).matcher(time);

            while (matcher.find()) {
                seconds += Integer.parseInt(matcher.group(1)) * lifeInterval[i];
            }
        }

        return seconds;
    }
}
