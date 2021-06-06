package com.salk.mycircadianclock.camera;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GFitStepCount {
    @PrimaryKey(autoGenerate = true)
    private int id;
    long starttime =0L;
    long endtime = 0L;
    int step_count =0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public int getStep_count() {
        return step_count;
    }

    public void setStep_count(int step_count) {
        this.step_count = step_count;
    }



}
