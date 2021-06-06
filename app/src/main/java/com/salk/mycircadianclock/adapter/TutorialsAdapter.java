package com.salk.mycircadianclock.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;

import java.util.List;

public class TutorialsAdapter extends PagerAdapter {

    private Activity activity;
    private List<Integer> arrayList;
    private CommonUsedmethods commonUsedmethods;

    public TutorialsAdapter(Activity activity, List<Integer> arrayList) {

        this.activity = activity;
        this.arrayList = arrayList;
        commonUsedmethods = new CommonUsedmethods();

    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {

        LayoutInflater inflater = activity.getLayoutInflater();

        View viewItem = inflater.inflate(R.layout.layout_slider, container, false);
        ImageView imageView = viewItem.findViewById(R.id.im_slider);

        commonUsedmethods.set_Image_Using_Glide(activity,imageView,arrayList.get(position));


        container.addView(viewItem);

        return viewItem;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // TODO Auto-generated method stub
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
        container.removeView((View) object);
    }
}