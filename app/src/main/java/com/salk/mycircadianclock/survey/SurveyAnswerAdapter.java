package com.salk.mycircadianclock.survey;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.interfaces.OnRecycleItemClicks;
import com.salk.mycircadianclock.localDatabase.FoodSleepExData;
import java.util.ArrayList;
import java.util.List;

public class SurveyAnswerAdapter extends RecyclerView.Adapter<SurveyAnswerAdapter.ViewHolder> {

    private List<FoodSleepExData> list;
    private Context context;
    private CommonUsedmethods commonUsedmethods;
    private ArrayList<Options> arrayList;
    private String type = "";
    private int size = 0;
    private SurveyQuestionAnswerModel surveyQuestionAnswerModel;
    ArrayList<String> arrayList_answer;


    SurveyAnswerAdapter(ArrayList<Options> arrayList,Context context, String type, int size,SurveyQuestionAnswerModel
                        surveyQuestionAnswerModel) {

        this.context = context;
        this.arrayList = arrayList;
        commonUsedmethods = new CommonUsedmethods();
        this.type = type;
        this.size =size;
        this.surveyQuestionAnswerModel = surveyQuestionAnswerModel;
        arrayList_answer = new ArrayList<>();
    }

    @Override
    public SurveyAnswerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.survey_item, parent, false);
        SurveyAnswerAdapter.ViewHolder viewHolder = new SurveyAnswerAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SurveyAnswerAdapter.ViewHolder holder, final int position) {

        try {

            if(type.contains("check_box")){

                holder.radioGroup.setVisibility(View.GONE);
                holder.editText.setVisibility(View.GONE);
                holder.spinner.setVisibility(View.GONE);
                holder.checkBox.setVisibility(View.VISIBLE);

                holder.checkBox.setText(arrayList.get(position).getOpt_value());
                holder.checkBox.setTag(arrayList.get(position).getOpt_id());

                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                        if(!b){
                            arrayList_answer.remove(holder.checkBox.getTag().toString());
                        }else{
                            arrayList_answer.add(holder.checkBox.getTag().toString());
                        }

                        surveyQuestionAnswerModel.setGiven_answer(arrayList_answer);


                    }
                });

            }else if(type.contains("radio")){

                holder.checkBox.setVisibility(View.GONE);
                holder.editText.setVisibility(View.GONE);
                holder.spinner.setVisibility(View.GONE);
                holder.radioGroup.setVisibility(View.VISIBLE);

                for(int i=0;i<arrayList.size();i++){

                    final RadioButton radioButton = new RadioButton(context);
                    radioButton.setText(arrayList.get(i).getOpt_value());
                    radioButton.setTag(arrayList.get(i).getOpt_value());
                    holder.radioGroup.addView(radioButton);

                    radioButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            arrayList_answer = new ArrayList<>();
                            arrayList_answer.add(radioButton.getTag().toString());
                            surveyQuestionAnswerModel.setGiven_answer(arrayList_answer);
                        }
                    });
                }




            }else if(type.equalsIgnoreCase("dropdown")){

                holder.radioGroup.setVisibility(View.GONE);
                holder.editText.setVisibility(View.GONE);
                holder.checkBox.setVisibility(View.GONE);
                holder.spinner.setVisibility(View.VISIBLE);

                SpinAdapter adapter = new SpinAdapter(context,
                        android.R.layout.simple_spinner_dropdown_item, arrayList);
                holder.spinner.setAdapter(adapter);

                holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int postion, long arg3) {
                        // TODO Auto-generated method stub
                        String selectedValue = arrayList.get(postion).getOpt_id();
                        arrayList_answer = new ArrayList<>();
                        arrayList_answer.add(selectedValue);
                        surveyQuestionAnswerModel.setGiven_answer(arrayList_answer);


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                });
            }else{

                holder.radioGroup.setVisibility(View.GONE);
                holder.checkBox.setVisibility(View.GONE);
                holder.spinner.setVisibility(View.GONE);
                holder.editText.setVisibility(View.VISIBLE);

                holder.editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        arrayList_answer = new ArrayList<>();
                        arrayList_answer.add(charSequence.toString());
                        surveyQuestionAnswerModel.setGiven_answer(arrayList_answer);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return size;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private RadioGroup radioGroup;
        private CheckBox checkBox;
        private EditText editText;
        private Spinner spinner;

        private ViewHolder(View itemView) {
            super(itemView);


            radioGroup = itemView.findViewById(R.id.radiobtn);
            checkBox = itemView.findViewById(R.id.checkbox);
            editText = itemView.findViewById(R.id.et_answer);
            spinner = itemView.findViewById(R.id.spinner);
        }
    }

    class SpinAdapter extends ArrayAdapter<Options>{

        // Your sent context
        private Context context;
        // Your custom values for the spinner (User)
        private ArrayList<Options> arrayList;

        public SpinAdapter(Context context, int textViewResourceId,
                           ArrayList<Options> arrayList) {
            super(context, textViewResourceId, arrayList);
            this.context = context;
            this.arrayList = arrayList;
        }

        @Override
        public int getCount(){
            return arrayList.size();
        }

        @Override
        public Options getItem(int position){
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position){
            return position;
        }


        // And the "magic" goes here
        // This is for the "passive" state of the spinner
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
            TextView label = (TextView) super.getView(position, convertView, parent);
            label.setTextColor(Color.BLACK);
            // Then you can get the current item using the values array (Users array) and the current position
            // You can NOW reference each method you has created in your bean object (User class)
            label.setText(arrayList.get(position).getOpt_value());

            // And finally return your dynamic (or custom) view for each spinner item
            return label;
        }

        // And here is when the "chooser" is popped up
        // Normally is the same view, but you can customize it if you want
        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            TextView label = (TextView) super.getDropDownView(position, convertView, parent);
            label.setTextColor(Color.BLACK);
            label.setText(arrayList.get(position).getOpt_value());

            return label;
        }
    }
}
