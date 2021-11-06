package tn.esprit.lifestyle.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity
public class ToDo implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String text;
    private boolean done;
    private Reminder reminder;
    private LocalDate date;
    private LocalTime time;
    private String weekDays;
    private Date doneDate;

    public ToDo() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Reminder getReminder() {
        return reminder;
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(String weekDays) {
        this.weekDays = weekDays;
    }

    public Date getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(Date doneDate) {
        this.doneDate = doneDate;
    }

    public String getReminderDayText() {
        if (this.reminder == Reminder.ONE_TIME)
            return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String reminder = "";
        if (this.reminder == Reminder.REPEAT) {
            int i=0;
            for (String day : weekDays.split(",")) {
                if (i%4==0)
                    reminder+=System.getProperty("line.separator");
                if (day.equals("Tuesday") || day.equals("Thursday"))
                    reminder += day.substring(0,4)+".";
                else
                    reminder += day.substring(0,3)+".";
                i++;
            }
            return reminder;
        }
        return "";
    }
}
