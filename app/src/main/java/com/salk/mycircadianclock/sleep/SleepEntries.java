package com.salk.mycircadianclock.sleep;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SleepEntries {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String sleep_details;
    private String status;
    private String previous_sleep_entries = "";
    private Long timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public String getSleep_details() {
        return sleep_details;
    }

    public void setSleep_details(String sleep_details) {
        this.sleep_details = sleep_details;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPrevious_sleep_entries() {
        return previous_sleep_entries;
    }

    public void setPrevious_sleep_entries(String previous_sleep_entries) {
        this.previous_sleep_entries = previous_sleep_entries;
    }
}
