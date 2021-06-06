package com.salk.mycircadianclock.history.history_intake;

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
import com.salk.mycircadianclock.localDatabase.FoodSleepExData;


import java.util.List;

public class WeekListAdapter extends RecyclerView.Adapter<WeekListAdapter.ViewHolder> {

    private List<String> list;
    private Context context;
    private CommonUsedmethods commonUsedmethods;
    private OnRecycleItemClicks onRecycleItemClicks;


    public WeekListAdapter(List<String> list, Context context, OnRecycleItemClicks onRecycleItemClicks) {

        this.list = list;
        this.context = context;
        commonUsedmethods = new CommonUsedmethods();
        this.onRecycleItemClicks = onRecycleItemClicks;
    }

    @Override
    public WeekListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.week_list_item, parent, false);
        WeekListAdapter.ViewHolder viewHolder = new WeekListAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WeekListAdapter.ViewHolder holder, final int position) {

        try {

            holder.text_date.setText(list.get(position));

            holder.rel_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRecycleItemClicks.onClick(list.get(position));
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

        private TextView text_date;
        private RelativeLayout rel_main;


        private ViewHolder(View itemView) {
            super(itemView);


            rel_main = itemView.findViewById(R.id.rel_main);
            text_date = itemView.findViewById(R.id.text);

        }
    }
}
