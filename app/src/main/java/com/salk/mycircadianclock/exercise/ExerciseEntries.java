package com.salk.mycircadianclock.exercise;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ExerciseEntries {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String exercise_details;
    private String status;
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

    public String getExercise_details() {
        return exercise_details;
    }

    public void setExercise_details(String exercise_details) {
        this.exercise_details = exercise_details;
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
