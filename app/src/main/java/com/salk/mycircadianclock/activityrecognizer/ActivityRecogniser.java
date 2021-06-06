package com.salk.mycircadianclock.activityrecognizer;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ActivityRecogniser {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int activity_count;
    private Long activity_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getActivity_count() {
        return activity_count;
    }

    public void setActivity_count(int activity_count) {
        this.activity_count = activity_count;
    }

    public Long getActivity_time() {
        return activity_time;
    }

    public void setActivity_time(Long activity_time) {
        this.activity_time = activity_time;
    }

}
