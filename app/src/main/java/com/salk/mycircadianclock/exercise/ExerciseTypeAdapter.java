package com.salk.mycircadianclock.exercise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.interfaces.OnRecycleItemClicks;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExerciseTypeAdapter extends RecyclerView.Adapter<ExerciseTypeAdapter.ViewHolder>  {

    private List<String> list;
    private Context context;
    private CommonUsedmethods commonUsedmethods;
    private OnRecycleItemClicks onRecycleItemClicks;
    private ArrayList<String> countlists;


    ExerciseTypeAdapter(List<String> list, Context context, OnRecycleItemClicks onRecycleItemClicks) {

        this.list = new ArrayList<>();
        this.list.addAll(list);
        this.context = context;
        countlists = new ArrayList<>();
        countlists.addAll(list);
        commonUsedmethods = new CommonUsedmethods();
        this.onRecycleItemClicks = onRecycleItemClicks;
    }

    @Override
    public ExerciseTypeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.custom_spinner, parent, false);
        ExerciseTypeAdapter.ViewHolder viewHolder = new ExerciseTypeAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ExerciseTypeAdapter.ViewHolder holder, int position) {

        try {
            final String ex_name = list.get(position);

            holder.txtexercisename.setText(ex_name);

            holder.lin_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRecycleItemClicks.onClick(ex_name);
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

        private TextView txtexercisename;
        private LinearLayout lin_main;


        private ViewHolder(View itemView) {
            super(itemView);


            lin_main = itemView.findViewById(R.id.lin_main);
            txtexercisename = itemView.findViewById(R.id.exercise_name);


        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        list.clear();
        if (charText.length() == 0) {
            list.addAll(countlists);
        } else {
            for (String wp : countlists) {
                if (wp.toLowerCase(Locale.getDefault())
                        .startsWith(charText)) {
                    list.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
