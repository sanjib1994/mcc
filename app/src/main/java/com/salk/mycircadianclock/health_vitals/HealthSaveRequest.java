package com.salk.mycircadianclock.health_vitals;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class HealthSaveRequest {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String health_description;
    private String status;
    private Long timestamp;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHealth_description() {
        return health_description;
    }

    public void setHealth_description(String health_description) {
        this.health_description = health_description;
    }

    public String getStatus() {
        return status;
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
}
