package com.salk.mycircadianclock.food;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class FetchCommonFood {

    @PrimaryKey(autoGenerate = true)
    private int id;
    String annotation_text = "";
    String food_type = "";
    long count = 0L;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnnotation_text() {
        return annotation_text;
    }

    public void setAnnotation_text(String annotation_text) {
        this.annotation_text = annotation_text;
    }

    public String getFood_type() {
        return food_type;
    }

    public void setFood_type(String food_type) {
        this.food_type = food_type;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
