package com.salk.mycircadianclock.history.history_intake;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.interfaces.OnRecycleItemClicks;
import java.util.List;

public class FoodCollageAdpter extends RecyclerView.Adapter<FoodCollageAdpter.ViewHolder> {

    private List<FoodCollageModel> list;
    private Context context;
    private CommonUsedmethods commonUsedmethods;
    private OnRecycleItemClicks onRecycleItemClicks;


    public FoodCollageAdpter(List<FoodCollageModel> list, Context context, OnRecycleItemClicks onRecycleItemClicks) {

        this.list = list;
        this.context = context;
        commonUsedmethods = new CommonUsedmethods();
        this.onRecycleItemClicks = onRecycleItemClicks;
    }

    @Override
    public FoodCollageAdpter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.food_collage_list_item, parent, false);
        FoodCollageAdpter.ViewHolder viewHolder = new FoodCollageAdpter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FoodCollageAdpter.ViewHolder holder, final int position) {

        try {

            if(list.get(position).getFood_image_url()==null ||
                    list.get(position).getFood_image_url().equalsIgnoreCase("none")
                    || list.get(position).getFood_image_url().equalsIgnoreCase("")){

                holder.text_food.setVisibility(View.VISIBLE);
                holder.imageView.setVisibility(View.VISIBLE);

                holder.text_food.setText(list.get(position).getAnnotation_text());

                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.foodcollageedit_text));

            }else{

                holder.text_food.setVisibility(View.GONE);
                holder.imageView.setVisibility(View.VISIBLE);

                Glide.with(context).load(list.get(position).getFood_image_url())
                        .dontAnimate().placeholder(R.drawable.foodcollageedit_text).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.imageView);

            }

            holder.text_date.setText(commonUsedmethods.parse_date_in_this_format(list.get(position)
            .getOriginal_time(),"MM-dd-yyyy,HH:mma"));

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRecycleItemClicks.onClick(position);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView text_food,text_date;
        private RelativeLayout rel_main;
        private ImageView imageView;


        private ViewHolder(View itemView) {
            super(itemView);


            rel_main = itemView.findViewById(R.id.rel_main);
            text_food = itemView.findViewById(R.id.text_food);
            text_date = itemView.findViewById(R.id.text_date);
            imageView = itemView.findViewById(R.id.image);



        }
    }
}
