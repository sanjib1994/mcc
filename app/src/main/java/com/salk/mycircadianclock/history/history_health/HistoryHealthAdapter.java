package com.salk.mycircadianclock.history.history_health;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.interfaces.OnRecycleItemClicks;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryHealthAdapter extends RecyclerView.Adapter<HistoryHealthAdapter.ViewHolder> {

    private List<HealthListModel> list;
    private Context context;
    private CommonUsedmethods commonUsedmethods;
    private OnRecycleItemClicks onRecycleItemClicks;
    private String filter_type = "";


    HistoryHealthAdapter(List<HealthListModel> list, Context context, OnRecycleItemClicks onRecycleItemClicks,
                               String filter_type) {

        this.list = list;
        this.context = context;
        commonUsedmethods = new CommonUsedmethods();
        this.onRecycleItemClicks = onRecycleItemClicks;
        this.filter_type = filter_type;
    }

    @Override
    public HistoryHealthAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.activity_health_list_items, parent, false);
        HistoryHealthAdapter.ViewHolder viewHolder = new HistoryHealthAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryHealthAdapter.ViewHolder holder, final int position) {

        try {

            HealthListModel  healthListModel = list.get(position);

            if (position % 2 == 0) {
                holder.rel_parent.setBackgroundResource(R.color.list_filter_bg);
                holder.star_image.setVisibility(View.VISIBLE);

            } else {
                holder.rel_parent.setBackgroundResource(R.color.white);
                holder.star_image.setVisibility(View.GONE);

            }

            if(position == list.size()-1){
                holder.bottom_view.setVisibility(View.VISIBLE);
            }else{
                holder.bottom_view.setVisibility(View.GONE);
            }

            if(healthListModel.getDate().contains("-")){

                String[] arr = healthListModel.getDate().split("-");
                String date = commonUsedmethods.parse_date_in_this_format(
                        arr[0],"MM/dd","yyyy/MM/dd")+"-"+
                        commonUsedmethods.parse_date_in_this_format(
                                arr[1],"MM/dd","yyyy/MM/dd");

                holder.list_time.setText(date);

            }else{

                holder.list_time.setText(commonUsedmethods.parse_date_in_this_format(
                        healthListModel.getDate(),"MM/dd","yyyy/MM/dd"
                ));
            }
            holder.list_value.setText(healthListModel.getValue()+" - "+healthListModel.getTime());



        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rel_parent;
        private View bottom_view;
        private TextView list_time,list_value;
        private ImageView star_image;

        private ViewHolder(View itemView) {
            super(itemView);

            rel_parent = itemView.findViewById(R.id.parent_view);
            bottom_view = itemView.findViewById(R.id.view_4);
            list_time = itemView.findViewById(R.id.list_time);
            list_value = itemView.findViewById(R.id.list_value);
            star_image = itemView.findViewById(R.id.star_image);
        }
    }
}

