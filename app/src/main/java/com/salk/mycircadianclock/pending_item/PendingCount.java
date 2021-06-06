package com.salk.mycircadianclock.pending_item;

public class PendingCount {

    String foodName = "";
    long count = 0;

    public PendingCount(String foodName,long count){

        this.foodName = foodName;
        this.count = count;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
