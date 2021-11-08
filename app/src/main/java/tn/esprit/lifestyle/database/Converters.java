package tn.esprit.lifestyle.database;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

import tn.esprit.lifestyle.entities.ActivityType;
import tn.esprit.lifestyle.entities.Reminder;

public class Converters {
    @TypeConverter
    public static String reminderToString(Reminder reminder) {
        return reminder == null ? null : reminder.toString();
    }

    @TypeConverter
    public static Reminder stringToReminder(String string) {
        for (Reminder r : Reminder.values()) {
            if (r.toString().equals(string))
                return r;
        }
        return Reminder.NONE;
    }

    @TypeConverter
    public static String localdateToString(LocalDate date) {
        if (date == null)
            return null;
        return date.getYear()+"-"+date.getMonthValue()+"-"+date.getDayOfMonth();
    }

    @TypeConverter
    public static LocalDate stringToLocaldate(String string) {
        if (string == null || string.isEmpty())
            return null;
        String[] date =string.split("-");
        return LocalDate.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
    }

    @TypeConverter
    public static String localtimeToString(LocalTime time) {
        if (time == null)
            return null;
        return time.getHour()+":"+time.getMinute();
    }

    @TypeConverter
    public static LocalTime stringToLocaltime(String string) {
        if (string == null || string.isEmpty())
            return null;
        String[] time =string.split(":");
        return LocalTime.of(Integer.parseInt(time[0]), Integer.parseInt(time[1]));
    }

    @TypeConverter
    public static Date timestampToDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String activityTypeToString(ActivityType activityType) {
        return activityType == null ? null : activityType.toString();
    }

    @TypeConverter
    public static ActivityType stringToActivutyType(String string) {
        for (ActivityType activityType : ActivityType.values()) {
            if (activityType.toString().equals(string))
                return activityType;
        }
        return null;
    }
}
