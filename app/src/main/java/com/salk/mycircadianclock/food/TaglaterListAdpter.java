package com.salk.mycircadianclock.food;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.interfaces.OnRecycleItemClicks;

import java.util.List;


public class TaglaterListAdpter extends RecyclerView.Adapter<TaglaterListAdpter.ViewHolder>{

    private List<Taglater> list;
    private Context context;
    private CommonUsedmethods commonUsedmethods;
    private OnRecycleItemClicks onRecycleItemClicks;


    public TaglaterListAdpter(List<Taglater> list, Context context, OnRecycleItemClicks onRecycleItemClicks) {

        this.list = list;
        this.context = context;
        commonUsedmethods = new CommonUsedmethods();
        this.onRecycleItemClicks = onRecycleItemClicks;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.tag_later_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Taglater myListData = list.get(position);
        commonUsedmethods.set_Image_Using_Glide(context,holder.imageView,myListData.getImage());
        holder.txtdate.setText(commonUsedmethods.parse_date_in_this_format(myListData.getTimestamp(),"dd-MM-yyyy hh:mma"));

        holder.rel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onRecycleItemClicks.onClick(myListData);
            }
        });

        if(position==list.size()-1){
            holder.view.setVisibility(View.GONE);
        }else{
            holder.view.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView txtdate;
        private View view;
        private RelativeLayout rel1;

        private ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.last_taken_food_image_one);
            txtdate = itemView.findViewById(R.id.date_one);
            view = itemView.findViewById(R.id.view);
            rel1 = itemView.findViewById(R.id.rel1);
        }
    }
}