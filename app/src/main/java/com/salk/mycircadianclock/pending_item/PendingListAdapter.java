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
import com.salk.mycircadianclock.exercise.ExerciseEntries;
import com.salk.mycircadianclock.food.FoodStuff;
import com.salk.mycircadianclock.health_vitals.HealthSaveRequest;
import com.salk.mycircadianclock.interfaces.OnRecycleItemClicks;
import com.salk.mycircadianclock.sleep.SleepEntries;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PendingListAdapter extends RecyclerView.Adapter<PendingListAdapter.ViewHolder> {

    private Object list;
    private String type;
    private ArrayList<FoodStuff> foodStuffArrayList;
    private ArrayList<SleepEntries> sleepEntriesArrayList;
    private ArrayList<ExerciseEntries> exerciseEntriesArrayList;
    private ArrayList<HealthSaveRequest> healthSaveRequestArrayList;
    private Context context;
    private CommonUsedmethods commonUsedmethods;
    private OnRecycleItemClicks onRecycleItemClicks;


    PendingListAdapter(Object object, Context context, OnRecycleItemClicks onRecycleItemClicks,String type) {

        list = object;
        this.type = type;
        this.context = context;
        commonUsedmethods = new CommonUsedmethods();
        this.onRecycleItemClicks = onRecycleItemClicks;
    }


    @Override
    public PendingListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.pending_food_list_item, parent, false);
        PendingListAdapter.ViewHolder viewHolder = new PendingListAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PendingListAdapter.ViewHolder holder, int position) {

        try {
            if(type.equalsIgnoreCase("food")){

                String food_desc = foodStuffArrayList.get(position).getFood_description();
                String food_name = new JSONObject(food_desc).getString("food_name");
                holder.text_food_name.setText(food_name);
                holder.text_date.setText(commonUsedmethods.parse_date_in_this_format(foodStuffArrayList.get(position).getTimestamp(),
                        "MM-dd-yyyy hh:mma"));

            }else if(type.equalsIgnoreCase("sleep")){

                String food_desc = sleepEntriesArrayList.get(position).getSleep_details();
                String sleep_start = new JSONObject(food_desc).getString("sleep_time");
                String sleep_end = new JSONObject(food_desc).getString("wake_up_time");
                holder.text_food_name.setText(context.getResources().getString(R.string.Bed_time)+sleep_start+" "+context.getResources().getString(R.string.Rise_time)+sleep_end);
                holder.text_date.setText(commonUsedmethods.parse_date_in_this_format(sleepEntriesArrayList.get(position).getTimestamp(),
                        "MM-dd-yyyy hh:mma"));

            }else if(type.equalsIgnoreCase("health")){

                String food_desc = healthSaveRequestArrayList.get(position).getHealth_description();
                String helath_type = new JSONObject(food_desc).getString("health_type");
                String vital_type = new JSONObject(food_desc).getString("vital_type");
                holder.text_food_name.setText("Health : "+helath_type+"\n"+"Vital type : "+vital_type);
                holder.text_date.setText(commonUsedmethods.parse_date_in_this_format(healthSaveRequestArrayList.get(position).getTimestamp(),
                        "MM-dd-yyyy hh:mma"));

            }else if(type.equalsIgnoreCase("exercise")){

                String food_desc = exerciseEntriesArrayList.get(position).getExercise_details();
                String ex_name = new JSONObject(food_desc).getString("exercise_name");
                String ex_duration = new JSONObject(food_desc).getString("exercise_duration");
                String ex_level = new JSONObject(food_desc).getString("exercise_level");
                holder.text_food_name.setText(context.getResources().getString(R.string.Exercise_name)+ex_name+
                        "\n"+context.getResources().getString(R.string.Exercise_duration)+ex_duration+
                        "\n"+context.getResources().getString(R.string.Exercise_lebel)+ex_level);
                holder.text_date.setText(commonUsedmethods.parse_date_in_this_format(exerciseEntriesArrayList.get(position).getTimestamp(),
                        "MM-dd-yyyy hh:mma"));

            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {

        if(type.equalsIgnoreCase("food")){

            foodStuffArrayList= (ArrayList<FoodStuff>) list;
            return foodStuffArrayList.size();

        }else if(type.equalsIgnoreCase("sleep")){

            sleepEntriesArrayList = (ArrayList<SleepEntries>) list;
            return sleepEntriesArrayList.size();

        }else if(type.equalsIgnoreCase("health")){

             healthSaveRequestArrayList = (ArrayList<HealthSaveRequest>) list;
            return healthSaveRequestArrayList.size();

        }else if(type.equalsIgnoreCase("exercise")){

            exerciseEntriesArrayList = (ArrayList<ExerciseEntries>) list;
            return exerciseEntriesArrayList.size();

        }

        return 0;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView text_food_name,text_date;
        private RelativeLayout rel_main;


        private ViewHolder(View itemView) {
            super(itemView);

            rel_main = itemView.findViewById(R.id.rel_main);
            text_food_name = itemView.findViewById(R.id.foodnametext);
            text_date =  itemView.findViewById(R.id.counttext);

        }
    }
}
