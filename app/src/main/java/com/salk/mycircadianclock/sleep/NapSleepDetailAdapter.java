package com.salk.mycircadianclock.sleep;

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

public class NapSleepDetailAdapter extends RecyclerView.Adapter<NapSleepDetailAdapter.ViewHolder> {

    private List<FoodSleepExData> list;
    private Context context;
    private CommonUsedmethods commonUsedmethods;
    private OnRecycleItemClicks onRecycleItemClicks;


    NapSleepDetailAdapter(List<FoodSleepExData> list, Context context, OnRecycleItemClicks onRecycleItemClicks) {

        this.list = list;
        this.context = context;
        commonUsedmethods = new CommonUsedmethods();
        this.onRecycleItemClicks = onRecycleItemClicks;
    }

    @Override
    public NapSleepDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.delete_nap_sleep_items, parent, false);
        NapSleepDetailAdapter.ViewHolder viewHolder = new NapSleepDetailAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NapSleepDetailAdapter.ViewHolder holder, int position) {

        try {
            final FoodSleepExData foodSleepExData = list.get(position);

            holder.txtsleep.setText(commonUsedmethods.parse_date_in_this_format(foodSleepExData.getSleep_start_time(),
                    "hh:mm a"));
            holder.txtwoke.setText(commonUsedmethods.parse_date_in_this_format(foodSleepExData.getSleep_end_time(),
                    "hh:mm a"));
            holder.txtcreted.setText(commonUsedmethods.parse_date_in_this_format(foodSleepExData.getTimestamp(),
                    "dd-MM-yyyy"));

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtsleep,txtwoke,txtcreted;
        private RelativeLayout rel_main;


        private ViewHolder(View itemView) {
            super(itemView);


            rel_main = itemView.findViewById(R.id.rel_main);
            txtsleep = itemView.findViewById(R.id.dsleeptext);
            txtwoke =  itemView.findViewById(R.id.dwoketext);
            txtcreted = itemView.findViewById(R.id.dcreteat);

        }
    }
}
