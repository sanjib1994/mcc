package com.salk.mycircadianclock.settings.local_notification;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ModelLocalNotification {

    @PrimaryKey(autoGenerate = true)
    private int id;
    String notification_type ="";
    String notification_message ="";
    int frequency = 7;
    int threshold = 1;
    boolean is_notify = false;

    public String getNotification_type() {
        return notification_type;
    }

    public void setNotification_type(String notification_type) {
        this.notification_type = notification_type;
    }

    public String getNotification_message() {
        return notification_message;
    }

    public void setNotification_message(String notification_message) {
        this.notification_message = notification_message;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public boolean isIs_notify() {
        return is_notify;
    }

    public void setIs_notify(boolean is_notify) {
        this.is_notify = is_notify;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
