package com.salk.mycircadianclock.history.history_intake;

public class FoodCollageModel {

    String annotation_text = "";
    String original_logtime = "";
    String log_time = "";
    String food_image_url = "";
    long original_time = 0L;


    public String getAnnotation_text() {
        return annotation_text;
    }

    public void setAnnotation_text(String annotation_text) {
        this.annotation_text = annotation_text;
    }

    public String getOriginal_logtime() {
        return original_logtime;
    }

    public void setOriginal_logtime(String original_logtime) {
        this.original_logtime = original_logtime;
    }

    public String getLog_time() {
        return log_time;
    }

    public void setLog_time(String log_time) {
        this.log_time = log_time;
    }

    public String getFood_image_url() {
        return food_image_url;
    }

    public long getOriginal_time() {
        return original_time;
    }

    public void setOriginal_time(long original_time) {
        this.original_time = original_time;
    }

    public void setFood_image_url(String food_image_url) {
        this.food_image_url = food_image_url;
    }
}
