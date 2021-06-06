package com.salk.mycircadianclock.sleep;

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
import com.salk.mycircadianclock.localDatabase.FoodSleepExData;
import java.util.List;

public class SleepDataEditAdapter extends RecyclerView.Adapter<SleepDataEditAdapter.ViewHolder> {

    private List<FoodSleepExData> list;
    private Context context;
    private CommonUsedmethods commonUsedmethods;
    private OnRecycleItemClicks onRecycleItemClicks;


    SleepDataEditAdapter(List<FoodSleepExData> list, Context context, OnRecycleItemClicks onRecycleItemClicks) {

        this.list = list;
        this.context = context;
        commonUsedmethods = new CommonUsedmethods();
        this.onRecycleItemClicks = onRecycleItemClicks;
    }

    @Override
    public SleepDataEditAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.date_items, parent, false);
        SleepDataEditAdapter.ViewHolder viewHolder = new SleepDataEditAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SleepDataEditAdapter.ViewHolder holder, int position) {

        try {
            final FoodSleepExData foodSleepExData = list.get(position);

            holder.txtsleep.setText(commonUsedmethods.parse_date_in_this_format(foodSleepExData.getSleep_start_time(),"hh:mma"));
            holder.txtwoke.setText(commonUsedmethods.parse_date_in_this_format(foodSleepExData.getSleep_end_time(),"hh:mma"));

            if (foodSleepExData.getSleep_duration() != 0) {
                int minutes = (int) ((foodSleepExData.getSleep_duration() / (1000 * 60)) % 60);
                int hours = (int) ((foodSleepExData.getSleep_duration() / (1000 * 60 * 60)) % 24);


                holder.txtduration.setText(String.format("%02d", hours) + "h " +
                        String.format("%02d", minutes) + " min");

            }

            if(foodSleepExData.isEnough_sleep()){

                holder.txtenought_slep_icon.setImageResource(R.mipmap.esleep);
            }else{
                holder.txtenought_slep_icon.setImageResource(R.mipmap.not_esleep);
            }


            holder.rel_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRecycleItemClicks.onClick(foodSleepExData);
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

        private TextView txtsleep,txtwoke,txtduration;
        private ImageView txtenought_slep_icon;
        private RelativeLayout rel_main;


        private ViewHolder(View itemView) {
            super(itemView);

            txtsleep = itemView.findViewById(R.id.sleeptext);
            txtwoke =  itemView.findViewById(R.id.woketext);
            txtduration = itemView.findViewById(R.id.durationtext);
            txtenought_slep_icon = itemView.findViewById(R.id.enough_status_icon);
            rel_main = itemView.findViewById(R.id.rel_main);

        }
    }
}
