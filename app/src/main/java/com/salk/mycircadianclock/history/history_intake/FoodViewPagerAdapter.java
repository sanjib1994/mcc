package com.salk.mycircadianclock.history.history_intake;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.viewpager.widget.PagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import java.util.ArrayList;

public class FoodViewPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<FoodCollageModel> arrayList;
    private CommonUsedmethods commonUsedmethods;

    FoodViewPagerAdapter(Context context, ArrayList<FoodCollageModel> arrayList){

        this.context = context;
        this.arrayList = arrayList;
        commonUsedmethods = new CommonUsedmethods();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {

        TextView text_day = null,text_date = null,text_time = null,text_food = null,text_food1 = null;
        ImageView image_food = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        View viewItem = inflater.inflate(R.layout.food_viewpager_item, collection, false);

        text_day = viewItem.findViewById(R.id.text_day);
        text_date = viewItem.findViewById(R.id.text_date);
        text_time = viewItem.findViewById(R.id.text_time);
        text_food = viewItem.findViewById(R.id.text_food);
        text_food1 = viewItem.findViewById(R.id.text_food1);
        image_food = viewItem.findViewById(R.id.image);


        text_date.setText(commonUsedmethods.parse_date_in_this_format(arrayList.get(position).getOriginal_time(),
                  "MM-dd-yyyy"));
        text_time.setText(commonUsedmethods.parse_date_in_this_format(arrayList.get(position).getOriginal_time(),
                "HH:mma"));

        text_day.setText(commonUsedmethods.parse_date_in_this_format(arrayList.get(position).getOriginal_time(),
                "EEEE"));

        if(arrayList.get(position).getFood_image_url()==null ||
                arrayList.get(position).getFood_image_url().equalsIgnoreCase("none")
                || arrayList.get(position).getFood_image_url().equalsIgnoreCase("")){


            image_food.setImageDrawable(context.getResources().getDrawable(R.drawable.foodcollageedit_text));
            text_food1.setText(arrayList.get(position).getAnnotation_text());
            text_food.setVisibility(View.GONE);
            text_food1.setVisibility(View.VISIBLE);
        }else{


            Glide.with(context).load(arrayList.get(position).getFood_image_url())
                    .dontAnimate().placeholder(R.drawable.foodcollageedit_text).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image_food);

            text_food.setText(arrayList.get(position).getAnnotation_text());
            text_food1.setVisibility(View.GONE);
            text_food.setVisibility(View.VISIBLE);
        }

        collection.addView(viewItem);
        return viewItem;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
