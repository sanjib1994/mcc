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

import java.util.List;

public class PreviousdaySleepEntryAdapter extends RecyclerView.Adapter<PreviousdaySleepEntryAdapter.ViewHolder> {

    private List<PreviousSleepEntry> list;
    private Context context;
    private CommonUsedmethods commonUsedmethods;
    private OnRecycleItemClicks onRecycleItemClicks;


    PreviousdaySleepEntryAdapter(List<PreviousSleepEntry> list, Context context, OnRecycleItemClicks onRecycleItemClicks) {

        this.list = list;
        this.context = context;
        commonUsedmethods = new CommonUsedmethods();
        this.onRecycleItemClicks = onRecycleItemClicks;
    }

    @Override
    public PreviousdaySleepEntryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.previous_day_item, parent, false);
        PreviousdaySleepEntryAdapter.ViewHolder viewHolder = new PreviousdaySleepEntryAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PreviousdaySleepEntryAdapter.ViewHolder holder, int position) {

        try {
            final PreviousSleepEntry previousSleepEntry = list.get(position);

            holder.txtdate.setText(previousSleepEntry.getDate());

            if (previousSleepEntry.getSleepEntries() != null && previousSleepEntry.getSleepEntries().size() > 0) {

                Long sleep_duration = 0L;

                for (int j = 0; j < previousSleepEntry.getSleepEntries().size(); j++) {

                    sleep_duration = sleep_duration + previousSleepEntry.getSleepEntries().get(j).getSleep_duration();

                }

                if (sleep_duration != 0) {
                    int minutes = (int) ((sleep_duration / (1000 * 60)) % 60);
                    int hours = (int) ((sleep_duration / (1000 * 60 * 60)) % 24);

                    holder.durationtext.setText(String.format("%02d", hours) + "h " +
                            String.format("%02d", minutes) + " min");
                    holder.durationtext.setTextColor(context.getResources().getColor(R.color.white));
                    holder.notTimeSetlay.setVisibility(View.GONE);
                    holder.durationtext.setVisibility(View.VISIBLE);
                } else {
                    holder.notTimeSetlay.setVisibility(View.VISIBLE);
                    holder.durationtext.setVisibility(View.GONE);

                }

            }else{
                holder.notTimeSetlay.setVisibility(View.VISIBLE);
                holder.durationtext.setVisibility(View.GONE);
            }



            holder.rel_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRecycleItemClicks.onClick(previousSleepEntry);
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

        private TextView txtdate,durationtext;
        private RelativeLayout notTimeSetlay,rel_main;


        private ViewHolder(View itemView) {
            super(itemView);

            txtdate = itemView.findViewById(R.id.datetext);
            durationtext = itemView.findViewById(R.id.durationtext);
            notTimeSetlay = itemView.findViewById(R.id.notTimeSetlay);
            rel_main = itemView.findViewById(R.id.rel_main);

        }
    }
}
