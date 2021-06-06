package com.salk.mycircadianclock.pending_item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.interfaces.OnRecycleItemClicks;

import java.util.List;

public class PendingItemAdapter extends RecyclerView.Adapter<PendingItemAdapter.ViewHolder> {

    private List<PendingCount> list;
    private Context context;
    private CommonUsedmethods commonUsedmethods;
    private OnRecycleItemClicks onRecycleItemClicks;


    PendingItemAdapter(List<PendingCount> list, Context context, OnRecycleItemClicks onRecycleItemClicks) {

        this.list = list;
        this.context = context;
        commonUsedmethods = new CommonUsedmethods();
        this.onRecycleItemClicks = onRecycleItemClicks;
    }

    @Override
    public PendingItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.pending_count_list_item, parent, false);
        PendingItemAdapter.ViewHolder viewHolder = new PendingItemAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PendingItemAdapter.ViewHolder holder, int position) {

        try {

            final PendingCount pendingCount = list.get(position);

            holder.text_food_name.setText(pendingCount.getFoodName());
            holder.text_food_count.setText(String.valueOf(pendingCount.getCount()));


            holder.rel_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onRecycleItemClicks.onClick(pendingCount);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView text_food_name,text_food_count;
        private RelativeLayout rel_main;


        private ViewHolder(View itemView) {
            super(itemView);

            rel_main = itemView.findViewById(R.id.rel_main);
            text_food_name = itemView.findViewById(R.id.foodnametext);
            text_food_count =  itemView.findViewById(R.id.counttext);

        }
    }
}
