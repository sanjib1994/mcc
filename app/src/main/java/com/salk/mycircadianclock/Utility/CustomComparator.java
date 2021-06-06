package com.salk.mycircadianclock.Utility;

import com.salk.mycircadianclock.food.FetchCommonFood;

import java.util.Comparator;

public class CustomComparator implements Comparator<FetchCommonFood> {
    @Override
    public int compare(FetchCommonFood o1, FetchCommonFood o2) {
        return Long.compare(o1.getCount(), o2.getCount());
    }
}