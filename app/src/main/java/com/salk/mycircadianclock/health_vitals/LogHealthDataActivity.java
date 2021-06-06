package com.salk.mycircadianclock.health_vitals;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.Utility.DatePickerDailogExcercise;
import com.salk.mycircadianclock.Utility.FancyButton;
import com.salk.mycircadianclock.Utility.TabbarClick;
import com.salk.mycircadianclock.Utility.TinylocalDb;
import com.salk.mycircadianclock.api.AESEncryption;
import com.salk.mycircadianclock.api.APIManager;
import com.salk.mycircadianclock.api.APIPasrsing;
import com.salk.mycircadianclock.api.ApiConfig;
import com.salk.mycircadianclock.api.Apicallback;
import com.salk.mycircadianclock.exercise.LogExerciseDataActivity;
import com.salk.mycircadianclock.localDatabase.DatabaseRepo;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;

public class LogHealthDataActivity extends AppCompatActivity {

    //Todo declaration of XML view
    private RelativeLayout tabbar, rel_main,value_layout, systolic_layout, diastolic_layout,
            unitsection_layout, chol_total_layout, ldl_layout, hdl_layout;
    private Button select_date, start_time, value, systolic_value, diastolic_value,
            chol_total_value, ldl_value, hdl_value,one, two, three, four, five, six, seven, eight, nine, zero, cancel,
            dot;;
    private EditText edit_items;
    private Editable str;
    private TextView vital_tab, lab_tab;
    private CheckBox fasting_status_chk;
    private FancyButton save_details, fcbtn_close;
    private Spinner datatype_list, unit_list;
    private ImageView info_icon;

    //Todo declaration of health data types
    private String[] data_type_items;
    private String[] weight_unit_items = new String[] { "Lb", "Kg" };
    private String[] weight_unit_items_french = new String[] { "kg","Lb" };
    private String[] heart_unit_items = new String[] { "beats/min" };
    private String[] blood_presure_unit_items = new String[] { "mmHg", "kPa" };
    private String[] temperature_unit_items = new String[] { "Fahrenheit", "Celsius" };
    private String[] temperature_unit_items_french = new String[] { "Celsius","Fahrenheit" };
    private String[] bowel_unit_items = new String[] { "NA" };
    private String[] lab_unit_ss = new String[] { "mg/dl", "mmol/L"};
    private String[] lab_unit_ketones = new String[] { "mg/dl", "mmol/L" };
    private String[] lab_unit_common = new String[] { "mg/dl", "mmol/dl", "mmol/L" };
    private String[] lab_unit_colestrol = new String[] { "mg/dl","mmol/dl","mmol/L" };
    private String[] lab_unit_hemo = new String[] { "%" };
    private String[] lab_unit_protein = new String[] {"mg/L","mmol/dl","mmol/L" };
    private String[] lab_unit_Homocyst = new String[] { "umol/L" };
    private String[] height_unit = new String[] {"inches","cms"};
    private String[] height_unit_french = new String[] {"cm", "pouces"};

    //Todo declaration of global variables
    private ArrayAdapter<String> adapter1;
    private String log_status = "Vitals",fasting_status = "true",language = "",user_id = "",auth_token = "",
                    research_info_id = "";
    private Locale locale = Locale.US;
    private CommonUsedmethods commonUsedmethods;
    private SharedPreferences sharedPreferences;
    private TinylocalDb tinylocalDb;
    private long measured_date = System.currentTimeMillis(),original_request_time =0L;
    private DatabaseRepo databaseRepo;
    private HealthSaveRequest healthSaveRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            //to make screen full screen
            new CommonUsedmethods().makeActivityFullscreen(LogHealthDataActivity.this);

            setContentView(R.layout.activity_log_health_data);

            init();

            click_function();

            initializecommonclass();

            vital_tab.performClick();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Todo initialization of all xml views
    private void init(){

        try {
            tabbar = findViewById(R.id.tab_bar);
            rel_main = findViewById(R.id.main);
            fcbtn_close = findViewById(R.id.btn_exit_spotify);
            info_icon = findViewById(R.id.info_icon);
            chol_total_layout = findViewById(R.id.chol_total_layout);
            ldl_layout = findViewById(R.id.ldl_layout);
            hdl_layout = findViewById(R.id.hdl_layout);
            unitsection_layout = findViewById(R.id.unitsection);
            value_layout = findViewById(R.id.value_layout);
            systolic_layout = findViewById(R.id.systolic_layout);
            diastolic_layout = findViewById(R.id.diastolic_layout);
            vital_tab = findViewById(R.id.vital_tab);
            lab_tab = findViewById(R.id.lab_tab);
            datatype_list = findViewById(R.id.datatype_list);
            unit_list = findViewById(R.id.unit_list);
            select_date = findViewById(R.id.select_date);
            start_time = findViewById(R.id.start_time);
            systolic_value = findViewById(R.id.systolic_value);
            diastolic_value = findViewById(R.id.diastolic_value);
            chol_total_value = findViewById(R.id.chol_total_value);
            ldl_value = findViewById(R.id.ldl_value);
            hdl_value = findViewById(R.id.hdl_value);
            value = findViewById(R.id.value);
            fasting_status_chk = findViewById(R.id.fasting_status);
            save_details = findViewById(R.id.btn_save_spotify);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo click functions on views
    private void click_function(){

        try {

            fasting_status_chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    if(b){

                        fasting_status = "true";
                    }else{

                        fasting_status = "false";
                    }
                }
            });

            select_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showDatePicker();
                }
            });

            start_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showTimePicker();
                }
            });

            vital_tab.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub

                    log_status = "Vitals";
                    vital_tab.setBackgroundColor(Color.parseColor("#d1d2d4"));
                    lab_tab.setBackgroundColor(Color.parseColor("#a8a8a8"));
                    set_initial_data();

                    data_type_items = new String[] { getResources().getString(R.string.WEIGHT),getResources().getString(R.string.HEIGHT),
                            getResources().getString(R.string.HEART_RATE),getResources().getString(R.string.BLOOD_PRESSURE),
                            getResources().getString(R.string.TEMPERATURE), getResources().getString(R.string.BOWEL_MOVEMENT) };


                    show_helath_data_type();
                }
            });

            lab_tab.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    log_status = "Labs";
                    lab_tab.setBackgroundColor(Color.parseColor("#d1d2d4"));
                    vital_tab.setBackgroundColor(Color.parseColor("#a8a8a8"));
                    set_initial_data();

                    data_type_items = new String[] { getResources().getString(R.string.BLOOD_GLUCOSE),getResources().getString(R.string.CHOLESTEROL),
                            getResources().getString(R.string.TRIGLYCERIDES),getResources().getString(R.string.HEMOGLOBIN_A1C) ,
                            getResources().getString(R.string.FIBRINOGEN),getResources().getString(R.string.C_REACTIVE_PROTEIN) ,
                            getResources().getString(R.string.HOMOCYSTEINE),getResources().getString(R.string.KETONES) };

                    show_helath_data_type();
                }
            });

            datatype_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    set_initial_data();
                    String selectedValue = data_type_items[i];
                    set_unit_for_health_dat_type(selectedValue);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            chol_total_value.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    showCalculator("chol_total");
                }
            });
            ldl_value.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    showCalculator("ldl");
                }
            });
            hdl_value.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    showCalculator("hdl");
                }
            });

            systolic_value.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    showCalculator("systolic");
                }
            });
            diastolic_value.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    showCalculator("diastolic");
                }
            });

            value.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    showCalculator("value");
                }
            });

            save_details.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    saveHealthDetails();



                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo initialize common classes
    private void initializecommonclass(){

        try {
            
            new TabbarClick().click(LogHealthDataActivity.this, tabbar, rel_main, "health");
            commonUsedmethods = new CommonUsedmethods();
            tinylocalDb = new TinylocalDb();
            databaseRepo = new DatabaseRepo(LogHealthDataActivity.this);
            sharedPreferences = tinylocalDb.get_shared_pref(LogHealthDataActivity.this);

            language = Locale.getDefault().getDisplayLanguage();
            if (language.matches("français")) {
                locale = Locale.FRENCH;
            }

            user_id = tinylocalDb.get_data_in_shared(sharedPreferences, "user_id");
            auth_token = tinylocalDb.get_data_in_shared(sharedPreferences, "auth_token");
            research_info_id = tinylocalDb.get_data_in_shared(sharedPreferences,"research_info_id");
            start_time.setText(commonUsedmethods.parse_date_in_this_format(System.currentTimeMillis(), "hh:mm a"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo show date picker
    private void showDatePicker() {

        try {
            final Calendar dateandtime = Calendar.getInstance(locale);

            DatePickerDailogExcercise dp = new DatePickerDailogExcercise(
                    LogHealthDataActivity.this, dateandtime,
                    new DatePickerDailogExcercise.DatePickerListner() {

                        @Override
                        public void OnDoneButton(Dialog datedialog, Calendar c) {

                            measured_date = c.getTimeInMillis();
                            select_date.setText(commonUsedmethods.parse_date_in_this_format(c.getTimeInMillis(),"dd-MM-yyyy"));
                            datedialog.dismiss();

                        }

                        @Override
                        public void OnCancelButton(Dialog datedialog) {
                            // TODO Auto-generated method stub

                            datedialog.dismiss();
                        }
                    });
            dp.setCancelable(false);
            dp.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo show time picker
    private void showTimePicker() {

        try {

            commonUsedmethods.TimePicker(LogHealthDataActivity.this, new CommonUsedmethods.TimePickerListner() {
                @Override
                public void OnDoneButton(Dialog datedialog, String hour, String min, String AM_PM) {

                    start_time.setText(hour + ":" + min + " " + AM_PM);

                    String curr_date = commonUsedmethods.parse_date_in_this_format(measured_date, "dd-MM-yyyy");

                    String helath_entry_time = hour + ":" + min + " " + AM_PM;

                    if (AM_PM.equalsIgnoreCase("")) {
                        measured_date = commonUsedmethods.convert_date_to_timestamp("dd-mm-yyy HH:mm", curr_date + " " + helath_entry_time);
                    } else {
                        measured_date = commonUsedmethods.convert_date_to_timestamp("dd-mm-yyy hh:mm a", curr_date + " " + helath_entry_time);
                    }

                    datedialog.dismiss();
                }

                @Override
                public void OnCancelButton(Dialog datedialog) {

                    datedialog.dismiss();

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo show calculator to take input
    public void showCalculator(final String type) {

        try {
            final Dialog value_dialog = new Dialog(this, android.R.style.Theme);
            value_dialog.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            value_dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
            value_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            value_dialog.setContentView(R.layout.dialog_list_health);

            Button add_items = value_dialog.findViewById(R.id.add_items);
            edit_items = value_dialog.findViewById(R.id.edit_items);
            one = value_dialog.findViewById(R.id.one);
            two = value_dialog.findViewById(R.id.two);
            three = value_dialog.findViewById(R.id.three);
            four = value_dialog.findViewById(R.id.four);
            five = value_dialog.findViewById(R.id.five);
            six = value_dialog.findViewById(R.id.six);
            seven = value_dialog.findViewById(R.id.seven);
            eight = value_dialog.findViewById(R.id.eight);
            nine = value_dialog.findViewById(R.id.nine);
            zero = value_dialog.findViewById(R.id.zero);
            dot = value_dialog.findViewById(R.id.dot);
            cancel = value_dialog.findViewById(R.id.cancel);
            add_items.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub

                    String fetch_value = edit_items.getText().toString();

                    if (fetch_value.equalsIgnoreCase("0.0")
                            || fetch_value.equalsIgnoreCase("0.")
                            || fetch_value.equalsIgnoreCase(".0")
                            || fetch_value.equalsIgnoreCase(".")
                            || fetch_value.equalsIgnoreCase("0")
                            || TextUtils.isEmpty(fetch_value)) {

                        commonUsedmethods.show_Toast(LogHealthDataActivity.this,getResources().getString(R.string.Please_set_valid_value));
                    } else {

                        set_data_in_ui(fetch_value,type);
                        value_dialog.dismiss();
                    }
                }
            });

            str = edit_items.getText();
            one.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    str = str.append(one.getText());
                    edit_items.setText(str);
                }
            });
            two.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    str = str.append(two.getText());
                    edit_items.setText(str);
                }
            });
            three.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    str = str.append(three.getText());
                    edit_items.setText(str);
                }
            });
            four.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    str = str.append(four.getText());
                    edit_items.setText(str);
                }
            });
            five.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    str = str.append(five.getText());
                    edit_items.setText(str);
                }
            });

            six.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    str = str.append(six.getText());
                    edit_items.setText(str);
                }
            });
            seven.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    str = str.append(seven.getText());
                    edit_items.setText(str);
                }
            });
            eight.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    str = str.append(eight.getText());
                    edit_items.setText(str);
                }
            });

            nine.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    str = str.append(nine.getText());
                    edit_items.setText(str);
                }
            });
            zero.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    str = str.append(zero.getText());
                    edit_items.setText(str);
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    str.clear();
                    edit_items.setText(str);
                }
            });
            dot.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    if (!edit_items.getText().toString().contains(".")) {
                        str = str.append(dot.getText());
                        edit_items.setText(str);
                    }

                }
            });


            value_dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo show health data type spinner
    private void show_helath_data_type(){

        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    LogHealthDataActivity.this,
                    android.R.layout.simple_spinner_dropdown_item,
                    data_type_items);
            datatype_list.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo set unit spinner acording to selected health data type
    private void set_unit_for_health_dat_type(String selectedValue){

        try{

            update_ui();

            if (selectedValue.equalsIgnoreCase(getResources().getString(R.string.WEIGHT))) {

                ArrayAdapter<String> weight =null;
                if(language.matches("français")) {
                    weight = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, weight_unit_items_french);
                }
                else {
                    weight = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, weight_unit_items);
                }

                unit_list.setAdapter(weight);

            }else if (selectedValue.equalsIgnoreCase(getResources().getString(R.string.HEIGHT))) {

                ArrayAdapter<String> weight = null;
                if(language.matches("français")) {
                    weight = new ArrayAdapter<String>(
                            this,
                            android.R.layout.simple_spinner_dropdown_item,
                            height_unit_french);}
                else
                {
                    weight = new ArrayAdapter<String>(
                            this,
                            android.R.layout.simple_spinner_dropdown_item,
                            height_unit);}

                unit_list.setAdapter(weight);

            }
            else if (selectedValue.equalsIgnoreCase(getResources().getString(R.string.BLOOD_GLUCOSE))) {

                ArrayAdapter<String> weight = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        lab_unit_ss);
                unit_list.setAdapter(weight);

            } else if (selectedValue.equalsIgnoreCase(getResources().getString(R.string.HEART_RATE))) {

                ArrayAdapter<String> heart = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        heart_unit_items);
                unit_list.setAdapter(heart);

            } else if (selectedValue.equalsIgnoreCase(getResources().getString(R.string.CHOLESTEROL))) {

                value_layout.setVisibility(View.GONE);
                chol_total_layout.setVisibility(View.VISIBLE);
                ldl_layout.setVisibility(View.VISIBLE);
                hdl_layout.setVisibility(View.VISIBLE);

                ArrayAdapter<String> heart = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        lab_unit_colestrol);
                unit_list.setAdapter(heart);

            } else if (selectedValue.equalsIgnoreCase(getResources().getString(R.string.BLOOD_PRESSURE))) {

                value_layout.setVisibility(View.GONE);
                systolic_layout.setVisibility(View.VISIBLE);
                diastolic_layout.setVisibility(View.VISIBLE);

                ArrayAdapter<String> bp = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        blood_presure_unit_items);
                unit_list.setAdapter(bp);

            } else if (selectedValue.equalsIgnoreCase(getResources().getString(R.string.TRIGLYCERIDES))) {

                ArrayAdapter<String> bp = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        lab_unit_colestrol);
                unit_list.setAdapter(bp);

            } else if (selectedValue.equalsIgnoreCase(getResources().getString(R.string.TEMPERATURE))) {

                ArrayAdapter<String> temp = null;
                if(language.matches("français")) {
                    temp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, temperature_unit_items_french);
                }
                else {
                    temp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, temperature_unit_items);
                }
                unit_list.setAdapter(temp);

            } else if (selectedValue.equalsIgnoreCase(getResources().getString(R.string.BOWEL_MOVEMENT))) {

                ArrayAdapter<String> temp = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        bowel_unit_items);
                unit_list.setAdapter(temp);

            } else if (selectedValue.equalsIgnoreCase(getResources().getString(R.string.HEMOGLOBIN_A1C))) {

                ArrayAdapter<String> temp = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        lab_unit_hemo);
                unit_list.setAdapter(temp);

            } else if (selectedValue.equalsIgnoreCase(getResources().getString(R.string.FIBRINOGEN))) {

                ArrayAdapter<String> temp = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        lab_unit_common);
                unit_list.setAdapter(temp);

            } else if (selectedValue.equalsIgnoreCase(getResources().getString(R.string.C_REACTIVE_PROTEIN))) {

                ArrayAdapter<String> temp = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        lab_unit_protein);
                unit_list.setAdapter(temp);

            } else if (selectedValue.equalsIgnoreCase(getResources().getString(R.string.HOMOCYSTEINE))) {

                ArrayAdapter<String> temp = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        lab_unit_Homocyst);
                unit_list.setAdapter(temp);

            } else if (selectedValue.equalsIgnoreCase(getResources().getString(R.string.KETONES))) {

                ArrayAdapter<String> temp = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        lab_unit_ketones);
                unit_list.setAdapter(temp);

            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo update UI accoding to selected health data type
    private void update_ui(){

        try {

            value_layout.setVisibility(View.VISIBLE);
            systolic_layout.setVisibility(View.GONE);
            diastolic_layout.setVisibility(View.GONE);
            chol_total_layout.setVisibility(View.GONE);
            ldl_layout.setVisibility(View.GONE);
            hdl_layout.setVisibility(View.GONE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo set initial data in text fields
    private void set_initial_data(){

        try{

            value.setText("_");
            systolic_value.setText("_");
            diastolic_value.setText("_");
            chol_total_value.setText("_");
            ldl_value.setText("_");
            hdl_value.setText("_");

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Todo set data in from input valculator
    private void set_data_in_ui(String fetch_value,String type){

        try {

            if (type.equalsIgnoreCase("value")) {
                save_details.setTextColor(Color.parseColor("#cc1626"));
                save_details.setEnabled(true);
                value.setText(fetch_value);
            } else if (type.equalsIgnoreCase("systolic")) {
                systolic_value.setText(fetch_value);
            } else if (type.equalsIgnoreCase("diastolic")) {
                diastolic_value.setText(fetch_value);
            } else if (type.equalsIgnoreCase("chol_total")) {
                chol_total_value.setText(fetch_value);
            } else if (type.equalsIgnoreCase("ldl")) {
                ldl_value.setText(fetch_value);
            } else if (type.equalsIgnoreCase("hdl")) {
                hdl_value.setText(fetch_value);
            }

            if (!systolic_value.getText().toString()
                    .equalsIgnoreCase("_")
                    && !diastolic_value.getText().toString()
                    .equalsIgnoreCase("_")) {
                save_details.setTextColor(Color.parseColor("#cc1626"));
                save_details.setEnabled(true);
            }

            if (!chol_total_value.getText().toString()
                    .equalsIgnoreCase("_")
                    && !ldl_value.getText().toString()
                    .equalsIgnoreCase("_")
                    && !hdl_value.getText().toString()
                    .equalsIgnoreCase("_")) {
                save_details.setTextColor(Color.parseColor("#cc1626"));
                save_details.setEnabled(true);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Todo check validation data and call APi to save if data is valid
    public void saveHealthDetails() {

        String data_type = datatype_list.getSelectedItem().toString();
        String value_ = value.getText().toString();
        String unit = unit_list.getSelectedItem().toString();

        if (data_type.equalsIgnoreCase(getResources().getString(R.string.BLOOD_PRESSURE))) {

            String systolic_value_ = systolic_value.getText().toString();
            String diastolic_value_ = diastolic_value.getText().toString();

            if (isValueValid(unit, systolic_value_, "systolic")
                    && isValueValid(unit, diastolic_value_, "diastolic")) {

                call_apis(data_type, "", unit, log_status,
                        systolic_value_, diastolic_value_, "", "", "");

            }

        } else if (data_type.equalsIgnoreCase(getResources().getString(R.string.CHOLESTEROL))) {
            String chol_total_value_ = chol_total_value.getText().toString();
            String ldl_value_ = ldl_value.getText().toString();
            String hdl_value_ = hdl_value.getText().toString();

            if (isValueValid(unit, chol_total_value_, "total")
                    && isValueValid(unit, ldl_value_, "Ldl")
                    && isValueValid(unit, hdl_value_, "Hdl")) {

                call_apis(data_type, "", unit, log_status, "",
                        "", chol_total_value_, ldl_value_, hdl_value_);
            }
        }
        else if (data_type.equalsIgnoreCase(getResources().getString(R.string.TEMPERATURE))) {

            if (isValueValid(unit, value_, data_type)) {

                call_apis(data_type, makeFormat(value_), unit, log_status,
                        "", "", "", "", "");
            }
        }
        else if (data_type.equalsIgnoreCase(getResources().getString(R.string.C_REACTIVE_PROTEIN))) {

            if (isValueValid(unit, value_, data_type)) {

                call_apis(data_type, makeFormat(value_), unit, log_status,
                        "", "", "", "", "");

            }
        }
        else {

            if (isValueValid(unit, value_, data_type)) {

                call_apis(data_type, value_, unit, log_status,
                        "", "", "", "", "");

            }
        }

    }

    //Todo check value is valid or not according to its unit what is slected by user
    public boolean isValueValid(String unit, String value, String value_type) {

        boolean status = true;

        try {

            Double value_ = Double.valueOf(value);

            if (unit.equalsIgnoreCase("Lb")) {

                if (value_ >= 90 && value_ <= 500) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.Lb_range));
                }
            }
            if (unit.equalsIgnoreCase("Kg")) {

                if (value_ >= 40 && value_ <= 225) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.Kg_range));
                }
            }
            if (unit.equalsIgnoreCase("beats/min")) {

                if (value_ >= 20 && value_ <= 200) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.Ferinheight_range_beats));
                }
            }
            if (unit.equalsIgnoreCase("Ferinheight") || unit.equalsIgnoreCase("Fahrenheit")) {

                if (value_ >= 90 && value_ <= 105) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.Ferinheight_range));
                }
            }
            if (unit.equalsIgnoreCase("Celsius")) {

                if (value_ >= 32.3 && value_ <= 41) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.Celsius_range));
                }
            }
            if (value_type.equalsIgnoreCase("systolic")) {

                if (unit.equalsIgnoreCase("mmHg") || unit.equalsIgnoreCase("kPa")) {
                    if (value_ >= 65 && value_ <= 200) {
                        status = true;
                    } else {
                        status = false;
                        commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.Systolic_range));
                    }
                }
            }
            if (value_type.equalsIgnoreCase("diastolic")) {

                if (unit.equalsIgnoreCase("mmHg") || unit.equalsIgnoreCase("kPa")) {
                    if (value_ >= 35 && value_ <= 150) {
                        status = true;
                    } else {
                        status = false;
                        commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.Diastolic_range));
                    }
                }
            }
            if (unit.equalsIgnoreCase("NA")) {

                if (value_ >= 0 && value_ <= 20) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.Bowel_movement));
                }
            }

            if (unit.equalsIgnoreCase("mg/dl")
                    && value_type.equalsIgnoreCase(getResources().getString(R.string.BLOOD_GLUCOSE))) {

                if (value_ >= 54 && value_ <= 300) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.Blood_Glucose));
                }
            }
            if (unit.equalsIgnoreCase("mmol/L")
                    && value_type.equalsIgnoreCase(getResources().getString(R.string.BLOOD_GLUCOSE))) {
                if (value_ >= 3.0 && value_ <= 16.5) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.Blood_Glucose_mmol));
                }

            }

            if (unit.equalsIgnoreCase("mg/dl")
                    && value_type.equalsIgnoreCase("Total")) {
                if (value_ >= 120 && value_ <= 300) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.Total_range_mg));
                }
            }
            if (unit.equalsIgnoreCase("mmol/dl")
                    && value_type.equalsIgnoreCase("Total")) {
                if (value_ >= 0.31 && value_ <= 0.776) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.Total_range_mmol));
                }
            }
            if (unit.equalsIgnoreCase("mmol/L")
                    && value_type.equalsIgnoreCase("Total")) {
                if (value_ >= 3.10 && value_ <= 7.76) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.Total_range_mmol_L));
                }
            }

            if (unit.equalsIgnoreCase("mg/dl")
                    && value_type.equalsIgnoreCase("Ldl")) {
                if (value_ >= 40 && value_ <= 200) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.LDL_range));
                }
            }
            if (unit.equalsIgnoreCase("mmol/dl")
                    && value_type.equalsIgnoreCase("Ldl")) {
                if (value_ >= 0.103 && value_ <= 0.517) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.LDL_range_mmol));
                }
            }
            if (unit.equalsIgnoreCase("mmol/L")
                    && value_type.equalsIgnoreCase("Ldl")) {
                if (value_ >= 1.03 && value_ <= 5.17) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.LDL_range_mmol_L));
                }
            }

            if (unit.equalsIgnoreCase("mg/dl")
                    && value_type.equalsIgnoreCase("hdl")) {
                if (value_ >= 20 && value_ <= 120) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.HDL_range));
                }
            }
            if (unit.equalsIgnoreCase("mmol/dl")
                    && value_type.equalsIgnoreCase("hdl")) {
                if (value_ >= 0.0517 && value_ <= 0.310) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.HDL_range_mmol));
                }
            }
            if (unit.equalsIgnoreCase("mmol/L")
                    && value_type.equalsIgnoreCase("hdl")) {
                if (value_ >= 0.517 && value_ <= 3.10) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.HDL_range_mmol_L));
                }
            }

            if (unit.equalsIgnoreCase("mg/dl")
                    && value_type.equalsIgnoreCase(getResources().getString(R.string.TRIGLYCERIDES))) {
                if (value_ >= 10 && value_ <= 700) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.TRIGLYCERIDES_range));
                }
            }
            if (unit.equalsIgnoreCase("mmol/dl")
                    && value_type.equalsIgnoreCase(getResources().getString(R.string.TRIGLYCERIDES))) {
                if (value_ >= 25 && value_ <= 330) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.TRIGLYCERIDES_range_mmol));
                }
            }
            if (unit.equalsIgnoreCase("mmol/L")
                    && value_type.equalsIgnoreCase(getResources().getString(R.string.TRIGLYCERIDES))) {
                if (value_ >= 0.1 && value_ <= 8.0) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.TRIGLYCERIDES_range_mmol_L));
                }
            }

            if (unit.equalsIgnoreCase("%")
                    && value_type.equalsIgnoreCase(getResources().getString(R.string.HEMOGLOBIN_A1C))) {
                if (value_ >= 4.5 && value_ <= 7.5) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.Hemoglobin_range));
                }
            }

            if (unit.equalsIgnoreCase("mg/dl")
                    && value_type.equalsIgnoreCase(getResources().getString(R.string.FIBRINOGEN))) {
                if (value_ >= 100 && value_ <= 500) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.Fibrinogen_range));
                }
            }
            if (unit.equalsIgnoreCase("mmol/dl")
                    && value_type.equalsIgnoreCase(getResources().getString(R.string.FIBRINOGEN))) {
                if (value_ >= 55 && value_ <= 275) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.Fibrinogen_range_mmol));
                }
            }
            if (unit.equalsIgnoreCase("mmol/L")
                    && value_type.equalsIgnoreCase(getResources().getString(R.string.FIBRINOGEN))) {
                if (value_ >= 5.5 && value_ <= 27.5) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.Fibrinogen_range_mmol_L));
                }
            }

            if (unit.equalsIgnoreCase("mmol/dl")
                    && value_type.equalsIgnoreCase(getResources().getString(R.string.C_REACTIVE_PROTEIN))) {
                if (value_ >= 1 && value_ <= 27.5) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.C_reative_range_mmol));
                }
            }
            if (unit.equalsIgnoreCase("mg/L")
                    && value_type.equalsIgnoreCase(getResources().getString(R.string.C_REACTIVE_PROTEIN))) {
                if (value_ >= 0.1 && value_ <= 5.0) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.C_reative_rang));
                }
            }
            if (unit.equalsIgnoreCase("mmol/L")
                    && value_type.equalsIgnoreCase(getResources().getString(R.string.C_REACTIVE_PROTEIN))) {
                if (value_ >= 0.1 && value_ <= 2.75) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.C_reative_range_mmol_L));
                }
            }

            if (unit.equalsIgnoreCase("mg/dl")
                    && value_type.equalsIgnoreCase(getResources().getString(R.string.KETONES))) {
                if (value_ >= 0 && value_ <= 100) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.Ketones_range));
                }
            }
            if (unit.equalsIgnoreCase("mmol/L")
                    && value_type.equalsIgnoreCase(getResources().getString(R.string.KETONES))) {

                if (value_ >= 0 && value_ <= 7) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.Ketones_range_mmol));
                }

            }

            if (unit.equalsIgnoreCase("umol/L")
                    && value_type.equalsIgnoreCase(getResources().getString(R.string.HOMOCYSTEINE))) {
                if (value_ >= 1 && value_ <= 20) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.Homocysteine_range));
                }
            }
            if ((unit.equalsIgnoreCase("inches") || unit.equalsIgnoreCase("pouces"))
                    && value_type.equalsIgnoreCase(getResources().getString(R.string.HEIGHT))) {
                if (value_ >= 45 && value_ <= 95) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.HEIGHT_range_inch));
                }
            }
            if ((unit.equalsIgnoreCase("cms") || unit.equalsIgnoreCase("cm"))
                    && value_type.equalsIgnoreCase(getResources().getString(R.string.HEIGHT))) {
                if (value_ >= 120 && value_ <= 220) {
                    status = true;
                } else {
                    status = false;
                    commonUsedmethods.show_Toast(LogHealthDataActivity.this, getResources().getString(R.string.HEIGHT_range_cm));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return status;
    }

    //Todo call health save api
    private void call_health_save_api(String data_type, String value,
                                      String unit, String log_type, String systolic_value,
                                      String diastolic_value, String chol_total_value, String ldl_value,
                                      String hdl_value) {

        try {
            String login_url = ApiConfig.HEALTH_SAVE_URL;
            JSONObject jsonObject = make_json_for_health_save_request(data_type,value,unit,log_type,
                    systolic_value,diastolic_value,chol_total_value,ldl_value,hdl_value);

            new APIManager().Apicall(auth_token, login_url, jsonObject, new Apicallback() {
                @Override
                public void success(int code, String value) {

                    if (code == 200) {

                        //parse response
                        parse_health_save_response(value, new APIPasrsing() {

                            @Override
                            public void completed() {

                                healthSaveRequest = databaseRepo.getHealthRequest(healthSaveRequest.getTimestamp());
                                databaseRepo.deleteHealthRequest(healthSaveRequest);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Toast.makeText(getApplicationContext(), R.string.Data_saved_successfully_food, Toast.LENGTH_LONG).show();

                                    }
                                });


                            }

                            @Override
                            public void error() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LogHealthDataActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
                                    }
                                });


                            }
                        });
                    } else {

                    }
                }

                @Override
                public void failure(final String value) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LogHealthDataActivity.this, value, Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo to make JSON file for health save API request
    public JSONObject make_json_for_health_save_request(String data_type, String value,
                                                        String unit, String log_type, String systolic_value,
                                                        String diastolic_value, String chol_total_value, String ldl_value,
                                                        String hdl_value) {

        JSONObject json = new JSONObject();
        try {
            json.put("participant_id", AESEncryption.encrypt(user_id));
            json.put("research_info_id",  AESEncryption.encrypt(research_info_id));
            json.put("health_data", AESEncryption.encrypt(makeJsonhealth(data_type,value,unit,log_type,
                    systolic_value,diastolic_value,chol_total_value,ldl_value,hdl_value)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    //Todo to make JSON for health vital detail
    public String makeJsonhealth(String data_type, String value,
                                 String unit, String log_type, String systolic_value,
                                 String diastolic_value, String chol_total_value, String ldl_value,
                                 String hdl_value) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("fasting", fasting_status);
            jsonObject.put("ldl_value", ldl_value);
            jsonObject.put("hdl_value", hdl_value);
            jsonObject.put("health_type", log_type);//it can be either vital or labs
            jsonObject.put("diastolic_value", diastolic_value);
            jsonObject.put("chol_total_value", chol_total_value);
            jsonObject.put("vital_type",  data_type);
            jsonObject.put("vital_unit", unit);
            jsonObject.put("vital_value", value);
            jsonObject.put("systolic_value", systolic_value);
            jsonObject.put("measured_date", commonUsedmethods.parse_date_in_this_format(measured_date,"yyyy-MM-dd HH:mm"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    //Todo to make format of a double value
    public String makeFormat(String value){
        DecimalFormat formater = new DecimalFormat("#.0");
        return formater.format(Double.valueOf(value));
    }

    //Todo parse food save response for json
    private void parse_health_save_response(String response, APIPasrsing apiPasrsing) {

        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.has("message") && !jsonObject.isNull("message")) {
                String status = jsonObject.getString("message");

                if (status.equalsIgnoreCase("success")) {
                    apiPasrsing.completed();
                } else {
                    apiPasrsing.error();
                }

            } else {

                apiPasrsing.error();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo save health api request in localdatabse
    private void save_health_json(String data_type, String value,
                                  String unit, String log_type, String systolic_value,
                                  String diastolic_value, String chol_total_value, String ldl_value,
                                  String hdl_value,long original_request_time){
        try {
            healthSaveRequest = new HealthSaveRequest();
            healthSaveRequest.setTimestamp(original_request_time);
            healthSaveRequest.setStatus("pending");
            healthSaveRequest.setHealth_description(makeJsonhealth(data_type, value, unit, log_type,
                    systolic_value, diastolic_value, chol_total_value, ldl_value, hdl_value));


            databaseRepo.insert_health_vitals(healthSaveRequest);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo save health data to local
    private void save_health_data(String data_type, String value,
                                  String unit, String log_type, String systolic_value,
                                  String diastolic_value, String chol_total_value, String ldl_value,
                                  String hdl_value,long original_request_time) {
        try {

            HealthData healthData = new HealthData();
            healthData.setVitalOrlab(log_type);
            healthData.setValue(value);
            healthData.setUnit(unit);
            healthData.setHealthData_type(data_type);
            healthData.setSystolic_value(systolic_value);
            healthData.setDiastolic_value(diastolic_value);
            healthData.setChol_total_value(chol_total_value);
            healthData.setLdl_value(ldl_value);
            healthData.setHdl_value(hdl_value);
            healthData.setActual_log_time(original_request_time);

            Long timestamp = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy",
                    commonUsedmethods.parse_date_in_this_format(measured_date, "dd-MM-yyyy"));

            healthData.setTimestamp(timestamp);

            databaseRepo.check_health_data_existance_and_update(timestamp, healthData);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    //Todo method used to call databse function functionality and API
    private void call_apis(String data_type, String value,
                           String unit, String log_type, String systolic_value,
                           String diastolic_value, String chol_total_value, String ldl_value,
                           String hdl_value){
        try {

            original_request_time = System.currentTimeMillis();
            save_health_json(data_type, value, unit, log_type,
                    systolic_value, diastolic_value, chol_total_value, ldl_value, hdl_value,original_request_time);

            save_health_data(data_type, value, unit, log_type,
                    systolic_value, diastolic_value, chol_total_value, ldl_value, hdl_value,original_request_time);

            call_health_save_api(data_type, value, unit, log_type,
                    systolic_value, diastolic_value, chol_total_value, ldl_value, hdl_value);

            onBackPressed();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo handle device back
    @Override
    public void onBackPressed() {

        try {
            finish();
            overridePendingTransition(0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
