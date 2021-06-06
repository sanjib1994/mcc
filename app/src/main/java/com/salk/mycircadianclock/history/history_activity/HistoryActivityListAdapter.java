package com.salk.mycircadianclock.history.history_activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.interfaces.OnRecycleItemClicks;
import java.util.List;

public class HistoryActivityListAdapter extends RecyclerView.Adapter<HistoryActivityListAdapter.ViewHolder> {

    private List<ActivityListModel> list;
    private Context context;
    private CommonUsedmethods commonUsedmethods;
    private OnRecycleItemClicks onRecycleItemClicks;
    private String filter_type = "";


    HistoryActivityListAdapter(List<ActivityListModel> list, Context context, OnRecycleItemClicks onRecycleItemClicks,
                               String filter_type) {

        this.list = list;
        this.context = context;
        commonUsedmethods = new CommonUsedmethods();
        this.onRecycleItemClicks = onRecycleItemClicks;
        this.filter_type = filter_type;
    }

    @Override
    public HistoryActivityListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.history_activity_list_item, parent, false);
        HistoryActivityListAdapter.ViewHolder viewHolder = new HistoryActivityListAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryActivityListAdapter.ViewHolder holder, final int position) {

        try {

            ActivityListModel  activityListModel = list.get(position);

            holder.active_statusprogressBar.setMax(15000);

            if (position % 2 == 0) {
                holder.rel_parent.setBackgroundResource(R.color.list_filter_bg);
                holder.lay1.setBackgroundResource(R.color.list_filter_bg);
                holder.lay2.setBackgroundResource(R.color.list_filter_bg);
                holder.lay3.setBackgroundResource(R.color.list_filter_bg);
            } else {
                holder.rel_parent.setBackgroundResource(R.color.white);
                holder.lay1.setBackgroundResource(R.color.white);
                holder.lay2.setBackgroundResource(R.color.white);
                holder.lay3.setBackgroundResource(R.color.white);
            }

            if(position == list.size()-1){
                holder.bottom_view.setVisibility(View.VISIBLE);
            }else{
                holder.bottom_view.setVisibility(View.GONE);
            }

            if(activityListModel.getDay_range().contains("-")){

                String[] arr = activityListModel.getDay_range().split("-");
                String date = commonUsedmethods.parse_date_in_this_format(
                        arr[0],"MM/dd","yyyy/MM/dd")+"-"+
                        commonUsedmethods.parse_date_in_this_format(
                                arr[1],"MM/dd","yyyy/MM/dd");

                holder.list_time.setText(date);

            }else{

                holder.list_time.setText(commonUsedmethods.parse_date_in_this_format(
                        activityListModel.getDay_range(),"MM/dd","yyyy/MM/dd"
                ));
            }

            if(activityListModel.getSleep_duration().equalsIgnoreCase("")
                || activityListModel.getSleep_duration().equalsIgnoreCase("0")
                || activityListModel.getSleep_duration().equalsIgnoreCase("0.0")
                || activityListModel.getSleep_duration().equalsIgnoreCase("0.00")
                || activityListModel.getSleep_duration().equalsIgnoreCase("00.00")
                || activityListModel.getSleep_duration().equalsIgnoreCase("00.0")
                    || activityListModel.getSleep_duration().equalsIgnoreCase("0:0")
                    || activityListModel.getSleep_duration().equalsIgnoreCase("0:00")
                    || activityListModel.getSleep_duration().equalsIgnoreCase("00:00")
                    || activityListModel.getSleep_duration().equalsIgnoreCase("00:0")){

                holder.sleepview.setText("0");
                holder.sleepview_statusprogressBar.setProgress(0);

            }else{

                if(activityListModel.getSleep_duration().contains(":")){

                    String ar[] = activityListModel.getSleep_duration().split(":");

                    int hour = Integer.valueOf(ar[0]);
                    int mins = Integer.valueOf(ar[1]);

                    holder.sleepview.setText(String.valueOf(hour)+" h "+String.valueOf(mins)+" m");
                    holder.sleepview_statusprogressBar.setProgress(hour);
                }else{

                    int hour = Integer.valueOf(activityListModel.getSleep_duration());
                    holder.sleepview.setText(String.valueOf(hour)+" h "+" 0 m");
                    holder.sleepview_statusprogressBar.setProgress(hour);
                }

            }

            if(     activityListModel.getExercise_duration().equalsIgnoreCase("")
                    || activityListModel.getExercise_duration().equalsIgnoreCase("0")
                    || activityListModel.getExercise_duration().equalsIgnoreCase("0.0")
                    || activityListModel.getExercise_duration().equalsIgnoreCase("0.00")
                    || activityListModel.getExercise_duration().equalsIgnoreCase("00.00")
                    || activityListModel.getExercise_duration().equalsIgnoreCase("00.0")
                    || activityListModel.getExercise_duration().equalsIgnoreCase("0:0")
                    || activityListModel.getExercise_duration().equalsIgnoreCase("0:00")
                    || activityListModel.getExercise_duration().equalsIgnoreCase("00:00")
                    || activityListModel.getExercise_duration().equalsIgnoreCase("00:0")){

                holder.exerciseview.setText("0");
                holder.exercise_statusprogressBar.setProgress(0);

            }else{

                if(activityListModel.getExercise_duration().contains(":")){

                    String ar[] = activityListModel.getExercise_duration().split(":");

                    int hour = Integer.valueOf(ar[0]);
                    int mins = Integer.valueOf(ar[1]);

                    holder.exerciseview.setText(String.valueOf(hour)+" h "+String.valueOf(mins)+" m");
                    holder.exercise_statusprogressBar.setProgress(hour);
                }else{

                    int hour = Integer.valueOf(activityListModel.getExercise_duration());
                    holder.exerciseview.setText(String.valueOf(hour)+" h "+" 0 m");
                    holder.exercise_statusprogressBar.setProgress(hour);
                }

            }

            if(activityListModel.getStep_count().equalsIgnoreCase("")
                    || activityListModel.getStep_count().equalsIgnoreCase("0")
                    || activityListModel.getStep_count().equalsIgnoreCase("0.0")
                    || activityListModel.getStep_count().equalsIgnoreCase("0.00")
                    || activityListModel.getStep_count().equalsIgnoreCase("00.00")
                    || activityListModel.getStep_count().equalsIgnoreCase("00.0")
                    || activityListModel.getStep_count().equalsIgnoreCase("0:0")
                    || activityListModel.getStep_count().equalsIgnoreCase("0:00")
                    || activityListModel.getStep_count().equalsIgnoreCase("00:00")
                    || activityListModel.getStep_count().equalsIgnoreCase("00:0")){

                holder.activeview.setText("0");
                holder.active_statusprogressBar.setProgress(0);

            }else{


                holder.sleepview.setText(activityListModel.getStep_count());
                holder.sleepview_statusprogressBar.setProgress(Integer.valueOf(activityListModel.getStep_count()));

            }

            if(filter_type.equalsIgnoreCase("day") || filter_type.equalsIgnoreCase("jump_date")){

                holder.sleepview_occrance.setVisibility(View.GONE);
                holder.exerciseview_occrance.setVisibility(View.GONE);
                holder.activeview_occrance.setVisibility(View.GONE);
            }else{

                holder.sleepview_occrance.setVisibility(View.VISIBLE);
                holder.exerciseview_occrance.setVisibility(View.VISIBLE);
                holder.activeview_occrance.setVisibility(View.VISIBLE);

                if(!activityListModel.getSleep_days().equalsIgnoreCase("")
                        && !activityListModel.getSleep_days().equalsIgnoreCase("0")){

                    holder.sleepview_occrance.setText("("+activityListModel.getSleep_days()+")");
                }else{
                    holder.sleepview_occrance.setVisibility(View.GONE);
                }

                if(!activityListModel.getExercise_days().equalsIgnoreCase("")
                        && !activityListModel.getExercise_days().equalsIgnoreCase("0")){

                    holder.exerciseview_occrance.setText("("+activityListModel.getExercise_days()+")");
                }else{
                    holder.exerciseview_occrance.setVisibility(View.GONE);
                }

                if(!activityListModel.getStep_days().equalsIgnoreCase("")
                        && !activityListModel.getStep_days().equalsIgnoreCase("0")){

                    holder.activeview_occrance.setText("("+activityListModel.getStep_days()+")");
                }else{
                    holder.activeview_occrance.setVisibility(View.GONE);
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rel_main,lay1,lay2,lay3;
        private RelativeLayout rel_parent;
        private View bottom_view;
        private ProgressBar sleepview_statusprogressBar,exercise_statusprogressBar,active_statusprogressBar;
        TextView list_time,sleepview,activeview,sleepview_occrance,exerciseview_occrance,activeview_occrance,exerciseview;
        

        private ViewHolder(View itemView) {
            super(itemView);

            rel_main = itemView.findViewById(R.id.rel_main);
            rel_parent = itemView.findViewById(R.id.parentview);
            bottom_view = itemView.findViewById(R.id.view_4);
            sleepview_statusprogressBar = itemView.findViewById(R.id.sleepview_statusprogressBar);
            exercise_statusprogressBar = itemView.findViewById(R.id.exercise_statusprogressBar);
            active_statusprogressBar = itemView.findViewById(R.id.active_statusprogressBar);
            list_time = itemView.findViewById(R.id.list_time);
            sleepview = itemView.findViewById(R.id.sleepview);
            activeview =itemView.findViewById(R.id.activeview);
            sleepview_occrance = itemView.findViewById(R.id.sleepview_occurance);
            exerciseview_occrance = itemView.findViewById(R.id.exerciseview_occurance);
            activeview_occrance = itemView.findViewById(R.id.activeview_occurance);
            exerciseview = itemView.findViewById(R.id.exerciseview);
            lay1 = itemView.findViewById(R.id.lay1);
            lay2 = itemView.findViewById(R.id.lay2);
            lay3 = itemView.findViewById(R.id.lay3);


        }
    }
}
