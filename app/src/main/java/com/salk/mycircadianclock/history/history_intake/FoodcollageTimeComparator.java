package com.salk.mycircadianclock.history.history_intake;

import com.salk.mycircadianclock.food.FetchCommonFood;

import java.util.Comparator;

public class FoodcollageTimeComparator implements Comparator<FoodCollageModel> {
    @Override
    public int compare(FoodCollageModel o1, FoodCollageModel o2) {
        return Long.compare(o1.getOriginal_time(), o2.getOriginal_time());
    }
}