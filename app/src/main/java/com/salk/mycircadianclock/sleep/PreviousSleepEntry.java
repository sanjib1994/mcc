package com.salk.mycircadianclock.sleep;

import com.salk.mycircadianclock.localDatabase.FoodSleepExData;

import java.util.List;

public class PreviousSleepEntry {

    List<FoodSleepExData> sleepEntries;
    String date;
    Long sleep_duration =0L;
    Long measuredate = 0L;

    public List<FoodSleepExData> getSleepEntries() {
        return sleepEntries;
    }

    public void setSleepEntries(List<FoodSleepExData> sleepEntries) {
        this.sleepEntries = sleepEntries;
    }

    public String getDate() {
        return date;
    }

    public Long getMeasuredate() {
        return measuredate;
    }

    public void setMeasuredate(Long measuredate) {
        this.measuredate = measuredate;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getSleep_duration() {
        return sleep_duration;
    }

    public void setSleep_duration(Long sleep_duration) {
        this.sleep_duration = sleep_duration;
    }
}
