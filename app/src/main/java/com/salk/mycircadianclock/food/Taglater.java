package com.salk.mycircadianclock.food;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Taglater implements Serializable{

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String image="";
    private Long timestamp;
    private String food_camerainfo="";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public String getFood_camerainfo() {
        return food_camerainfo;
    }

    public void setFood_camerainfo(String food_camerainfo) {
        this.food_camerainfo = food_camerainfo;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
