package com.salk.mycircadianclock.history.history_intake;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.interfaces.OnRecycleItemClicks;
import java.util.List;

public class HistoryIntakeListAdapter extends RecyclerView.Adapter<HistoryIntakeListAdapter.ViewHolder> {

    private List<HistoryIntakeListModel> list;
    private Context context;
    private CommonUsedmethods commonUsedmethods;
    private OnRecycleItemClicks onRecycleItemClicks;


    HistoryIntakeListAdapter(List<HistoryIntakeListModel> list, Context context, OnRecycleItemClicks onRecycleItemClicks) {

        this.list = list;
        this.context = context;
        commonUsedmethods = new CommonUsedmethods();
        this.onRecycleItemClicks = onRecycleItemClicks;
    }

    @Override
    public HistoryIntakeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.history_intake_list_item, parent, false);
        HistoryIntakeListAdapter.ViewHolder viewHolder = new HistoryIntakeListAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryIntakeListAdapter.ViewHolder holder, final int position) {

        try {

            if(position%2==0){
                holder.rel_parent.setBackgroundResource(R.color.list_filter_bg);
            }else{
                holder.rel_parent.setBackgroundResource(R.color.white);
            }

            if(position == list.size()-1){
                holder.bottom_view.setVisibility(View.VISIBLE);
            }else{
                holder.bottom_view.setVisibility(View.GONE);
            }

            holder.text_date.setText(list.get(position).getDate());
            holder.text_foods.setText(list.get(position).getValue());

            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView text_foods, text_date;
        private RelativeLayout rel_main;
        private HorizontalScrollView rel_parent;
        private View bottom_view;


        private ViewHolder(View itemView) {
            super(itemView);


            rel_main = itemView.findViewById(R.id.rel_main);
            text_foods= itemView.findViewById(R.id.text_foods);
            text_date = itemView.findViewById(R.id.text_time);
            rel_parent = itemView.findViewById(R.id.parentview);
            bottom_view = itemView.findViewById(R.id.view_4);
        }
    }
}
