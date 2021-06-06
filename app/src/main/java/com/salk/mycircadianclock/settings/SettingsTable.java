package com.salk.mycircadianclock.settings;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SettingsTable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String is_begin_fast_active = "0";
    private String is_fast_complete_active =  "0";
    private String reminder_hr =  "0";
    private String is_reminder_active =  "0";
    private String is_sleep_duration_active =  "0";
    private String sleep_duration =  "0";
    private String sleep_start_time =  "0";
    private String eating_window_target_start =  "0";
    private String eating_window_target_end =  "0";
    private String is_eating_window_target_active =  "0";
    private String activity_target_count_step =  "0";
    private String is_activity_target_step_active =  "0";
    private String is_time_till_fast_active =  "0";
    private String is_time_since_food_active =  "0";
    private String is_days_exercise_active =  "0";
    private String is_steps_active =  "0";
    private String is_hours_sleep_active =  "0";
    private String is_display_medicine_active =  "0";


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIs_begin_fast_active() {
        return is_begin_fast_active;
    }

    public void setIs_begin_fast_active(String is_begin_fast_active) {
        this.is_begin_fast_active = is_begin_fast_active;
    }

    public String getIs_fast_complete_active() {
        return is_fast_complete_active;
    }

    public void setIs_fast_complete_active(String is_fast_complete_active) {
        this.is_fast_complete_active = is_fast_complete_active;
    }

    public String getReminder_hr() {
        return reminder_hr;
    }

    public void setReminder_hr(String reminder_hr) {
        this.reminder_hr = reminder_hr;
    }

    public String getIs_reminder_active() {
        return is_reminder_active;
    }

    public void setIs_reminder_active(String is_reminder_active) {
        this.is_reminder_active = is_reminder_active;
    }

    public String getIs_sleep_duration_active() {
        return is_sleep_duration_active;
    }

    public void setIs_sleep_duration_active(String is_sleep_duration_active) {
        this.is_sleep_duration_active = is_sleep_duration_active;
    }

    public String getSleep_duration() {
        return sleep_duration;
    }

    public void setSleep_duration(String sleep_duration) {
        this.sleep_duration = sleep_duration;
    }

    public String getSleep_start_time() {
        return sleep_start_time;
    }

    public void setSleep_start_time(String sleep_start_time) {
        this.sleep_start_time = sleep_start_time;
    }

    public String getEating_window_target_start() {
        return eating_window_target_start;
    }

    public void setEating_window_target_start(String eating_window_target_start) {
        this.eating_window_target_start = eating_window_target_start;
    }

    public String getEating_window_target_end() {
        return eating_window_target_end;
    }

    public void setEating_window_target_end(String eating_window_target_end) {
        this.eating_window_target_end = eating_window_target_end;
    }

    public String getIs_eating_window_target_active() {
        return is_eating_window_target_active;
    }

    public void setIs_eating_window_target_active(String is_eating_window_target_active) {
        this.is_eating_window_target_active = is_eating_window_target_active;
    }

    public String getActivity_target_count_step() {
        return activity_target_count_step;
    }

    public void setActivity_target_count_step(String activity_target_count_step) {
        this.activity_target_count_step = activity_target_count_step;
    }

    public String getIs_activity_target_step_active() {
        return is_activity_target_step_active;
    }

    public void setIs_activity_target_step_active(String is_activity_target_step_active) {
        this.is_activity_target_step_active = is_activity_target_step_active;
    }

    public String getIs_time_till_fast_active() {
        return is_time_till_fast_active;
    }

    public void setIs_time_till_fast_active(String is_time_till_fast_active) {
        this.is_time_till_fast_active = is_time_till_fast_active;
    }

    public String getIs_time_since_food_active() {
        return is_time_since_food_active;
    }

    public void setIs_time_since_food_active(String is_time_since_food_active) {
        this.is_time_since_food_active = is_time_since_food_active;
    }

    public String getIs_days_exercise_active() {
        return is_days_exercise_active;
    }

    public void setIs_days_exercise_active(String is_days_exercise_active) {
        this.is_days_exercise_active = is_days_exercise_active;
    }

    public String getIs_steps_active() {
        return is_steps_active;
    }

    public void setIs_steps_active(String is_steps_active) {
        this.is_steps_active = is_steps_active;
    }

    public String getIs_hours_sleep_active() {
        return is_hours_sleep_active;
    }

    public void setIs_hours_sleep_active(String is_hours_sleep_active) {
        this.is_hours_sleep_active = is_hours_sleep_active;
    }

    public String getIs_display_medicine_active() {
        return is_display_medicine_active;
    }

    public void setIs_display_medicine_active(String is_display_medicine_active) {
        this.is_display_medicine_active = is_display_medicine_active;
    }
}
