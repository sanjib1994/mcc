package com.salk.mycircadianclock.localDatabase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class FoodSleepExData implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    Long timestamp = 0L;
    Long sleep_duration = 0L;
    String type = "";
    String food_type = "";
    String food_name = "";
    Long sleep_start_time = 0L;
    Long sleep_end_time = 0L;
    String food_taken_time = "";
    String sleep_difficulties = "";
    int step_count = 0;
    boolean enough_sleep = false;
    Long sleep_actual_date = 0L;
    String exercise_duration = "";
    String exercise_name = "";
    Long actual_log_request = 0L;
    String server_status = "pending";

    public int getId() {
        return id;
    }

    public Long getSleep_start_time() {
        return sleep_start_time;
    }

    public void setSleep_start_time(Long sleep_start_time) {
        this.sleep_start_time = sleep_start_time;
    }

    public Long getSleep_end_time() {
        return sleep_end_time;
    }

    public void setSleep_end_time(Long sleep_end_time) {
        this.sleep_end_time = sleep_end_time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFood_type() {
        return food_type;
    }

    public void setFood_type(String food_type) {
        this.food_type = food_type;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getFood_taken_time() {
        return food_taken_time;
    }

    public Long getSleep_duration() {
        return sleep_duration;
    }

    public void setSleep_duration(Long sleep_duration) {
        this.sleep_duration = sleep_duration;
    }

    public Long getSleep_actual_date() {
        return sleep_actual_date;
    }

    public void setSleep_actual_date(Long sleep_actual_date) {
        this.sleep_actual_date = sleep_actual_date;
    }

    public void setFood_taken_time(String food_taken_time) {
        this.food_taken_time = food_taken_time;
    }

    public int getStep_count() {
        return step_count;
    }

    public void setStep_count(int step_count) {
        this.step_count = step_count;
    }

    public boolean isEnough_sleep() {
        return enough_sleep;
    }

    public void setEnough_sleep(boolean enough_sleep) {
        this.enough_sleep = enough_sleep;
    }

    public String getSleep_difficulties() {
        return sleep_difficulties;
    }

    public void setSleep_difficulties(String sleep_difficulties) {
        this.sleep_difficulties = sleep_difficulties;
    }

    public String getExercise_duration() {
        return exercise_duration;
    }

    public void setExercise_duration(String exercise_duration) {
        this.exercise_duration = exercise_duration;
    }

    public String getExercise_name() {
        return exercise_name;
    }

    public void setExercise_name(String exercise_name) {
        this.exercise_name = exercise_name;
    }

    public Long getActual_log_request() {
        return actual_log_request;
    }

    public void setActual_log_request(Long actual_log_request) {
        this.actual_log_request = actual_log_request;
    }

    public String getServer_status() {
        return server_status;
    }

    public void setServer_status(String server_status) {
        this.server_status = server_status;
    }
}
