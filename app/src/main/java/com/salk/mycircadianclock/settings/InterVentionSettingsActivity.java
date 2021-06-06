package com.salk.mycircadianclock.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.Utility.ConnectionDetector;
import com.salk.mycircadianclock.Utility.TabbarClick;
import com.salk.mycircadianclock.Utility.TinylocalDb;
import com.salk.mycircadianclock.api.AESEncryption;
import com.salk.mycircadianclock.api.APIManager;
import com.salk.mycircadianclock.api.APIPasrsing;
import com.salk.mycircadianclock.api.ApiConfig;
import com.salk.mycircadianclock.api.Apicallback;
import com.salk.mycircadianclock.food.FetchCommonFood;
import com.salk.mycircadianclock.localDatabase.DatabaseRepo;
import com.salk.mycircadianclock.localDatabase.FoodSleepExData;
import com.salk.mycircadianclock.localDatabase.OnDataFetched;
import com.salk.mycircadianclock.pending_item.PendingItemActivity;
import com.salk.mycircadianclock.settings.local_notification.SetLocalNotification;
import com.salk.mycircadianclock.wheel.WheelView;
import com.salk.mycircadianclock.wheel.adapters.ArrayWheelAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InterVentionSettingsActivity extends AppCompatActivity  {

    //Todo declaration of XML views
    private RelativeLayout tabbar,rel_main;
    private ToggleButton beginfast_toggle, fastcomplete_toggle, displaymedicine_toggle,
            steps_toggle, hoursofsleep_toggle, eatingwindowtarget_toggle,
            activitytarget_toggle,
            timetillfast_toggle, timesincefood_toggle, daysexercise_toggle, fastingtimer_toggle,
            sleep_duration_target_toggle;
    private TextView activitytarget_steps, eatingwindowtarget_starttime,
            eatingwindowtarget_endtime, faststartreminderhour, sleepstart_time,
            sleep_duration_time;
    private RelativeLayout food_sync_rel,sleep_sync_rel,health_sync_rel,pedingrel;
    private ImageView sleep_sync_notify,health_sync_notify,food_sync_notify;

    //Todo declaration of global variable
    private Long eating_window_start_time = 0L, eating_window_end_time =0L, sleep_start_time = 0L;
    private String current_date = "",user_id = "",research_info_id ="",auth_token = "",user_type = "";

    //Todo declaration of common classes
    private CommonUsedmethods commonUsedmethods;
    private SettingsTable settingsTable = new SettingsTable();
    private DatabaseRepo databaseRepo;
    private TinylocalDb tinylocalDb;
    private SharedPreferences sharedPreferences;
    private SetLocalNotification setLocalNotification;
    private ConnectionDetector connectionDetector;
    private KProgressHUD kProgressHUD;
    private ArrayList<FetchCommonFood> fetchCommonFoodArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            //Todo to make screen full screen
            new CommonUsedmethods().makeActivityFullscreen(InterVentionSettingsActivity.this);

            setContentView(R.layout.activity_inter_vention_settings);

            init();

            initializecommonclass();

            click_function();

            fetch_settings_data();


        }catch (Exception e){
            e.printStackTrace();
        }

    }


    //Todo initialization of all xml views
    private void init(){

        try {
            tabbar = findViewById(R.id.tab_bar);
            rel_main = findViewById(R.id.main);
            beginfast_toggle = findViewById(R.id.beginfast_toggle);
            fastcomplete_toggle = findViewById(R.id.fastcomplete_toggle);
            eatingwindowtarget_toggle = findViewById(R.id.eatingwindowtarget_toggle);
            activitytarget_toggle = findViewById(R.id.activitytarget_toggle);
            timetillfast_toggle = findViewById(R.id.timetillfast_toggle);
            timesincefood_toggle = findViewById(R.id.timesincefood_toggle);
            daysexercise_toggle = findViewById(R.id.daysexercise_toggle);
            steps_toggle = findViewById(R.id.steps_toggle);
            hoursofsleep_toggle = findViewById(R.id.hoursofsleep_toggle);
            displaymedicine_toggle = findViewById(R.id.displaymedicine_toggle);
            activitytarget_steps = findViewById(R.id.activitytarget_steps);
            eatingwindowtarget_starttime = findViewById(R.id.eatingwindowtarget_starttime);
            eatingwindowtarget_endtime = findViewById(R.id.eatingwindowtarget_endtime);
            faststartreminderhour = findViewById(R.id.faststartreminderhour);
            fastingtimer_toggle = findViewById(R.id.fastingtimer_toggle1);
            sleepstart_time = findViewById(R.id.sleepstart_time);
            sleep_duration_time = findViewById(R.id.sleep_duration_time);
            sleep_duration_target_toggle = findViewById(R.id.sleep_duration_target_toggle);
            pedingrel = findViewById(R.id.pedingrel);
            food_sync_rel = findViewById(R.id.food_sync_rel);
            sleep_sync_rel = findViewById(R.id.sleep_sync_rel);
            health_sync_rel = findViewById(R.id.health_sync_rel);
            sleep_sync_notify = findViewById(R.id.sleep_sync_notify);
            health_sync_notify =  findViewById(R.id.health_sync_notify);
            food_sync_notify = findViewById(R.id.food_sync_notify);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo click functions on views
    private void click_function(){

        try {

            food_sync_rel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(connectionDetector.isConnectingToInternet()){

                        if(kProgressHUD!=null && !kProgressHUD.isShowing()){
                           kProgressHUD.show();
                        }
                        call_fetch_common_food_API();
                    }else{
                        commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,getResources().getString(R.string.Please_check_your_internet_connection));
                    }

                }
            });

            sleep_sync_rel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(connectionDetector.isConnectingToInternet()){

                        if(kProgressHUD!=null && !kProgressHUD.isShowing()){
                            kProgressHUD.show();
                        }
                        call_sleep_sync_API();
                    }else{
                        commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,getResources().getString(R.string.Please_check_your_internet_connection));
                    }
                }
            });


            beginfast_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton arg0,
                                             boolean checked) {
                    // TODO Auto-generated method stub

                    if(user_type.equalsIgnoreCase("intervention")) {
                        check_for_eating_begins(checked, "fasting_begin");
                    }else{

                        beginfast_toggle.setChecked(false);
                        commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,getResources().getString(R.string.Baseline_settings));
                    }
                }
            });

            fastcomplete_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton arg0,
                                             boolean checked) {
                    // TODO Auto-generated method stub
                    if(user_type.equalsIgnoreCase("intervention")) {
                        check_for_eating_begins(checked, "fasting_end");
                    }else{

                        fastcomplete_toggle.setChecked(false);
                        commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,getResources().getString(R.string.Baseline_settings));
                    }
                }
            });


            eatingwindowtarget_toggle
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton arg0,
                                                     boolean checked) {
                            // TODO Auto-generated method stub
                            if(user_type.equalsIgnoreCase("intervention")) {
                                check_for_esting_eindow_target(checked);
                            }else{

                                eatingwindowtarget_toggle.setChecked(false);
                                commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,getResources().getString(R.string.Baseline_settings));
                            }
                        }
                    });

            eatingwindowtarget_starttime.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub

                    if(user_type.equalsIgnoreCase("intervention")) {
                        commonUsedmethods.TimePicker(InterVentionSettingsActivity.this, new CommonUsedmethods.TimePickerListner() {
                            @Override
                            public void OnDoneButton(Dialog datedialog, String hour, String min, String AM_PM) {

                                set_eating_window_time(hour, min, AM_PM, "start");

                                datedialog.dismiss();
                            }

                            @Override
                            public void OnCancelButton(Dialog datedialog) {

                                datedialog.dismiss();
                            }
                        });
                    }else{
                        commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,getResources().getString(R.string.Baseline_settings));
                    }


                }
            });

            eatingwindowtarget_endtime.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub

                    if(user_type.equalsIgnoreCase("intervention")) {
                        if (eating_window_start_time != 0) {
                            commonUsedmethods.TimePicker(InterVentionSettingsActivity.this, new CommonUsedmethods.TimePickerListner() {
                                @Override
                                public void OnDoneButton(Dialog datedialog, String hour, String min, String AM_PM) {

                                    set_eating_window_time(hour, min, AM_PM, "end");
                                    datedialog.dismiss();
                                }

                                @Override
                                public void OnCancelButton(Dialog datedialog) {

                                    datedialog.dismiss();
                                }
                            });
                        } else {

                            commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,
                                    getResources().getString(R.string.Select_strat_time_first));
                        }
                    }else{
                        commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,getResources().getString(R.string.Baseline_settings));
                    }

                }
            });

            activitytarget_toggle
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton arg0,
                                                     boolean checked) {
                            // TODO Auto-generated method stub
                            if(user_type.equalsIgnoreCase("intervention")) {
                                check_for_target_steps(checked);
                            }else{

                                activitytarget_toggle.setChecked(false);
                                commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,getResources().getString(R.string.Baseline_settings));
                            }
                        }
                    });

            timetillfast_toggle
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton arg0,
                                                     boolean checked) {
                            // TODO Auto-generated method stub

                            if(user_type.equalsIgnoreCase("intervention")) {
                                if (checked) {

                                    settingsTable.setIs_time_till_fast_active("1");
                                } else {


                                    settingsTable.setIs_time_till_fast_active("0");
                                }

                                databaseRepo.updateSettingsData(settingsTable);
                            }else{
                                timetillfast_toggle.setChecked(false);
                                commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,getResources().getString(R.string.Baseline_settings));
                            }

                        }
                    });

            timesincefood_toggle
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton arg0,
                                                     boolean checked) {
                            // TODO Auto-generated method stub

                            if(user_type.equalsIgnoreCase("intervention")) {
                                if (checked) {

                                    settingsTable.setIs_time_since_food_active("1");
                                } else {
                                    settingsTable.setIs_time_since_food_active("0");
                                }
                                databaseRepo.updateSettingsData(settingsTable);
                            }else{

                                timesincefood_toggle.setChecked(false);
                                commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,getResources().getString(R.string.Baseline_settings));
                            }


                        }
                    });

            daysexercise_toggle
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton arg0,
                                                     boolean checked) {
                            // TODO Auto-generated method stub

                            if(user_type.equalsIgnoreCase("intervention")) {
                                if (checked) {

                                    settingsTable.setIs_days_exercise_active("1");
                                } else {
                                    settingsTable.setIs_days_exercise_active("0");
                                }
                                databaseRepo.updateSettingsData(settingsTable);
                            }else{

                                daysexercise_toggle.setChecked(false);
                                commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,getResources().getString(R.string.Baseline_settings));
                            }

                        }
                    });

            steps_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean checked) {
                    // TODO Auto-generated method stub

                    if(user_type.equalsIgnoreCase("intervention")) {
                        if (checked) {

                            settingsTable.setIs_steps_active("1");
                        } else {
                            settingsTable.setIs_steps_active("0");
                        }
                        databaseRepo.updateSettingsData(settingsTable);
                    }else{

                        steps_toggle.setChecked(false);
                        commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,getResources().getString(R.string.Baseline_settings));
                    }

                }
            });

            hoursofsleep_toggle
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton arg0,
                                                     boolean checked) {
                            // TODO Auto-generated method stub
                            if(user_type.equalsIgnoreCase("intervention")) {
                                if (checked) {

                                    settingsTable.setIs_hours_sleep_active("1");
                                } else {
                                    settingsTable.setIs_hours_sleep_active("0");
                                }
                                databaseRepo.updateSettingsData(settingsTable);
                            }else{

                                hoursofsleep_toggle.setChecked(false);
                                commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,getResources().getString(R.string.Baseline_settings));
                            }


                        }
                    });

            displaymedicine_toggle
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton arg0,
                                                     boolean checked) {
                            // TODO Auto-generated method stub
                            if(user_type.equalsIgnoreCase("intervention")) {
                                if (checked) {

                                    settingsTable.setIs_display_medicine_active("1");
                                } else {
                                    settingsTable.setIs_display_medicine_active("0");
                                }
                                databaseRepo.updateSettingsData(settingsTable);
                            }else{

                                displaymedicine_toggle.setChecked(false);
                                commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,getResources().getString(R.string.Baseline_settings));
                            }

                        }
                    });



            activitytarget_steps.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    if(user_type.equalsIgnoreCase("intervention")) {
                        showPicker(getResources().getString(R.string.Set_target_Steps));
                    }else{

                        commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,getResources().getString(R.string.Baseline_settings));
                    }
                }
            });

            faststartreminderhour.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    if(user_type.equalsIgnoreCase("intervention")) {
                        if (eating_window_start_time != 0 && eating_window_end_time != 0) {

                            showPicker(getResources().getString(R.string.Mins_before_fasting));
                        } else {
                            commonUsedmethods.show_Toast(InterVentionSettingsActivity.this, getResources().getString(R.string.eating_window_target));
                        }
                    }else{
                        commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,getResources().getString(R.string.Baseline_settings));
                    }

                }
            });

            fastingtimer_toggle
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton arg0,
                                                     boolean checked) {
                            // TODO Auto-generated method stub
                            if(user_type.equalsIgnoreCase("intervention")) {

                                if(eating_window_end_time!=0) {
                                    if (!faststartreminderhour.getText().toString()
                                            .equalsIgnoreCase("0 hr")) {

                                        if (checked) {

                                            settingsTable.setIs_reminder_active("1");
                                            check_time_to_set_reminder(eating_window_end_time,true,"");
                                        } else {
                                            settingsTable.setIs_reminder_active("0");
                                        }
                                        databaseRepo.updateSettingsData(settingsTable);
                                    } else {

                                        commonUsedmethods.show_Toast(InterVentionSettingsActivity.this, getResources().getString(R.string.Please_set_reminder_time));
                                    }
                                }else{
                                    commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,
                                            getResources().getString(R.string.eating_window_target));
                                }
                            }else{
                                fastingtimer_toggle.setChecked(false);
                                commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,getResources().getString(R.string.Baseline_settings));
                            }
                        }
                    });

            sleep_duration_target_toggle
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton arg0,
                                                     boolean checked) {
                            // TODO Auto-generated method stub
                            if(user_type.equalsIgnoreCase("intervention")) {
                                if (sleep_start_time != 0
                                        && !sleep_duration_time.getText().toString()
                                        .equalsIgnoreCase("0 hr")) {


                                    if (checked) {

                                        settingsTable.setIs_sleep_duration_active("1");
                                    } else {
                                        settingsTable.setIs_sleep_duration_active("0");
                                    }
                                    databaseRepo.updateSettingsData(settingsTable);

                                } else {

                                    commonUsedmethods.show_Toast(InterVentionSettingsActivity.this, getResources().getString(R.string.Please_set_sleep_target));
                                }
                            }else{
                                sleep_duration_target_toggle.setChecked(false);
                                commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,getResources().getString(R.string.Baseline_settings));
                            }
                        }
                    });


            sleepstart_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(user_type.equalsIgnoreCase("intervention")) {
                        commonUsedmethods.TimePicker(InterVentionSettingsActivity.this, new CommonUsedmethods.TimePickerListner() {
                            @Override
                            public void OnDoneButton(Dialog datedialog, String hour, String min, String AM_PM) {

                                set_eating_window_time(hour, min, AM_PM, "sleep");
                                datedialog.dismiss();
                            }

                            @Override
                            public void OnCancelButton(Dialog datedialog) {

                                datedialog.dismiss();
                            }
                        });
                    }else{
                        commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,getResources().getString(R.string.Baseline_settings));
                    }
                }
            });

            sleep_duration_time.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    if(user_type.equalsIgnoreCase("intervention")) {

                          showPicker(getResources().getString(R.string.Set_hour_sleep));
                    }else{
                        commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,getResources().getString(R.string.Baseline_settings));
                    }
                }
            });


            pedingrel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(InterVentionSettingsActivity.this, PendingItemActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo initialize common classes
    private void initializecommonclass(){

        try {

            new TabbarClick().click(InterVentionSettingsActivity.this, tabbar, rel_main, "settings_inter");
            commonUsedmethods = new CommonUsedmethods();
            databaseRepo = new DatabaseRepo(InterVentionSettingsActivity.this);
            tinylocalDb = new TinylocalDb();
            setLocalNotification = new SetLocalNotification();
            sharedPreferences = tinylocalDb.get_shared_pref(InterVentionSettingsActivity.this);
            connectionDetector = new ConnectionDetector(InterVentionSettingsActivity.this);
            kProgressHUD = commonUsedmethods.show_progerssDialog(InterVentionSettingsActivity.this,"Loading....",false);
            user_id = tinylocalDb.get_data_in_shared(sharedPreferences,"user_id");
            auth_token = tinylocalDb.get_data_in_shared(sharedPreferences,"auth_token");
            research_info_id = tinylocalDb.get_data_in_shared(sharedPreferences,"research_info_id");
            user_type = tinylocalDb.get_data_in_shared(sharedPreferences,"user_type");

            current_date = commonUsedmethods.parse_date_in_this_format(System.currentTimeMillis(),"dd-MM-yyyy");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo to show custom picker view
    public void showPicker(final String status) {

        try {

            final Dialog set_time_target_alert = new Dialog(this);
            set_time_target_alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
            set_time_target_alert.setContentView(R.layout.wheel_setting_view);
            set_time_target_alert.getWindow().setGravity(Gravity.BOTTOM);
            set_time_target_alert.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            set_time_target_alert.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent);
            set_time_target_alert.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            set_time_target_alert.setCancelable(false);

            TextView Cancel_wheel = set_time_target_alert.findViewById(R.id.cancelbuttonslpwh);
            TextView Done_wheel = set_time_target_alert.findViewById(R.id.donebuttonslpwh);
            TextView title_text = set_time_target_alert.findViewById(R.id.titleslw);
            final WheelView time_wheel = set_time_target_alert.findViewById(R.id.setting_time);
            title_text.setText(status);

            final String[] time_array = getWheelRange(status);
            ArrayWheelAdapter<String> systolic_adapter = new ArrayWheelAdapter<String>(
                    this, time_array);
            time_wheel.setViewAdapter(systolic_adapter);

            Cancel_wheel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    set_time_target_alert.dismiss();
                }
            });
            Done_wheel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub

                    set_data_from_picker(status, Integer.valueOf(time_array[time_wheel.getCurrentItem()]));
                    set_time_target_alert.dismiss();
                }
            });
            set_time_target_alert.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo to design wheel range
    public String[] getWheelRange(String status) {

        try {
            ArrayList<String> temp_array = new ArrayList<String>();

            if (status.equalsIgnoreCase(getResources().getString(R.string.Set_hour))) {
                for (int i = 0; i <= 23; i++) {
                    temp_array.add(String.valueOf(i) + "");
                }
            } else if (status.equalsIgnoreCase(getResources().getString(R.string.Set_target_Steps))) {
                for (int i = 500; i <= 10000; i += 500) {
                    temp_array.add(String.valueOf(i) + "");
                }
            } else if (status.equalsIgnoreCase(getResources().getString(R.string.Set_hour_sleep))) {
                for (int i = 1; i <= 24; i++) {
                    temp_array.add(String.valueOf(i) + "");
                }
            } else if (status.equalsIgnoreCase(getResources().getString(R.string.Mins_before_fasting))) {
                for (int i = 0; i <= 120; i += 5) {
                    temp_array.add(String.valueOf(i) + "");
                }
            }

            String[] temp_array_ = new String[temp_array.size()];
            return temp_array_ = temp_array.toArray(temp_array_);
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    //Todo to check eating window start time validations
    private void check_for_eating_begins(boolean checked,String value){

        try {
            if (eating_window_start_time != 0 && eating_window_end_time != 0) {

                if (checked) {

                    if (value.equalsIgnoreCase("fasting_begin")) {

                        settingsTable.setIs_begin_fast_active("1");

                        check_time_to_set_reminder(eating_window_start_time,false,"Your target eating window has begun.");

                    } else {

                        settingsTable.setIs_fast_complete_active("1");
                        check_time_to_set_reminder(eating_window_end_time,false,"Your target eating window has ended.");

                    }


                } else {
                    if (value.equalsIgnoreCase("fasting_begin")) {

                        settingsTable.setIs_begin_fast_active("0");
                    } else {

                        settingsTable.setIs_fast_complete_active("0");
                    }

                }
                databaseRepo.updateSettingsData(settingsTable);

            } else {

                if (value.equalsIgnoreCase("fasting_begin")) {

                    beginfast_toggle.setChecked(false);
                } else {

                    fastcomplete_toggle.setChecked(false);
                }

                commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,
                        getResources().getString(R.string.eating_window_target));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //Todo to check eating window target time validations
    private void check_for_esting_eindow_target(boolean checked){

        try {
            if (eating_window_start_time != 0 && eating_window_end_time != 0) {

                if (checked) {

                    settingsTable.setIs_eating_window_target_active("1");
                } else {
                    settingsTable.setIs_eating_window_target_active("0");
                }
                databaseRepo.updateSettingsData(settingsTable);

            } else {
                eatingwindowtarget_toggle.setChecked(false);
                commonUsedmethods.show_Toast(InterVentionSettingsActivity.this, getResources().getString(R.string.eating_window_target));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo to check sleep target time validations
    private void check_for_target_steps(boolean checked){

        try {
            if (!activitytarget_steps.getText().toString()
                    .equalsIgnoreCase("0")) {

                if (checked) {

                    settingsTable.setIs_activity_target_step_active("1");
                } else {
                    settingsTable.setIs_activity_target_step_active("0");
                }
                databaseRepo.updateSettingsData(settingsTable);
            } else {
                activitytarget_toggle.setChecked(false);
                commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,
                        getResources().getString(R.string.set_step_target));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo to set eating window time
    private void set_eating_window_time(String hour,String min,String AM_PM,String value){

        try {
            String time = hour + ":" + min + " " + AM_PM;

            if (AM_PM.equalsIgnoreCase("")) {

                if (value.equalsIgnoreCase("start")) {

                    eating_window_start_time = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy HH:mm",
                            current_date + " " + time);
                    eatingwindowtarget_starttime.setText(time);

                    settingsTable.setEating_window_target_start(commonUsedmethods.parse_date_in_this_format(eating_window_start_time,
                            "HH:mm"));
                    eatingwindowtarget_endtime.performClick();

                } else if (value.equalsIgnoreCase("end")) {

                    eating_window_end_time = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy HH:mm", current_date + " " + time);

                    if (eating_window_end_time - eating_window_start_time >= 2 * 60 * 60 * 1000) {
                        eatingwindowtarget_endtime.setText(time);

                        settingsTable.setEating_window_target_end(commonUsedmethods.parse_date_in_this_format(eating_window_end_time,
                                "HH:mm"));
                    } else {

                        if (eating_window_end_time < eating_window_start_time) {
                            commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,
                                    getResources().getString(R.string.Eating_window_less));
                        } else {
                            commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,
                                    getResources().getString(R.string.Eating_window_less));
                        }

                    }

                } else if (value.equalsIgnoreCase("sleep")) {

                    sleep_start_time = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy HH:mm", current_date + " " + time);
                    sleepstart_time.setText(time);

                    settingsTable.setSleep_start_time(commonUsedmethods.parse_date_in_this_format(sleep_start_time,
                            "HH:mm"));
                }



            } else {

                if (value.equalsIgnoreCase("start")) {

                    eating_window_start_time = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy hh:mm a", current_date + " " + time);
                    eatingwindowtarget_starttime.setText(time);

                    settingsTable.setEating_window_target_start(commonUsedmethods.parse_date_in_this_format(eating_window_start_time,
                            "HH:mm"));
                    eatingwindowtarget_endtime.performClick();

                } else if (value.equalsIgnoreCase("end")) {

                    eating_window_end_time = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy hh:mm a", current_date + " " + time);
                    if (eating_window_end_time - eating_window_start_time >= 2 * 60 * 60 * 1000) {
                        eatingwindowtarget_endtime.setText(time);

                        settingsTable.setEating_window_target_end(commonUsedmethods.parse_date_in_this_format(eating_window_end_time,
                                "HH:mm"));
                    } else {

                        if (eating_window_end_time < eating_window_start_time) {
                            commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,
                                    getResources().getString(R.string.Eating_window_less));
                        } else {
                            commonUsedmethods.show_Toast(InterVentionSettingsActivity.this,
                                    getResources().getString(R.string.Eating_window_less));
                        }

                    }
                } else if (value.equalsIgnoreCase("sleep")) {

                    sleep_start_time = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy hh:mm a", current_date + " " + time);
                    sleepstart_time.setText(time);

                    settingsTable.setSleep_start_time(commonUsedmethods.parse_date_in_this_format(sleep_start_time,
                            "HH:mm"));
                }

                databaseRepo.updateSettingsData(settingsTable);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        
    }

    //Todo to set data from custom picker
    private void set_data_from_picker(String status,int selected_time){

        try {
            if (status.equalsIgnoreCase(getResources().getString(R.string.Set_target_Steps))) {

                activitytarget_steps.setText(String.valueOf(selected_time));
                activitytarget_toggle.setChecked(true);

                settingsTable.setActivity_target_count_step(String.valueOf(selected_time));

            } else if (status.equalsIgnoreCase(getResources().getString(R.string.Set_hour_sleep))) {


                sleep_duration_time.setText(selected_time + "hr");
                settingsTable.setSleep_duration(selected_time + "hr");

            } else if (status.equalsIgnoreCase(getResources().getString(R.string.Mins_before_fasting))) {


                faststartreminderhour.setText(selected_time + " min");
                settingsTable.setReminder_hr(selected_time + " min");

            }
            databaseRepo.updateSettingsData(settingsTable);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Todo fetch settings data from local database
    private void fetch_settings_data(){

        try {
            databaseRepo.getSettingsData(new OnDataFetched() {
                @Override
                public void data(Object object, int i) {

                }

                @Override
                public void data(Object object) {

                    settingsTable = (SettingsTable) object;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            set_data_in_UI(settingsTable);
                        }
                    });

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo to set data in UI after fetching grom local
    private void set_data_in_UI(SettingsTable settingsTable){

        try{

            if(!settingsTable.getReminder_hr().equalsIgnoreCase("0")){

                faststartreminderhour.setText(settingsTable.getReminder_hr());
            }
            if(!settingsTable.getSleep_start_time().equalsIgnoreCase("0")){

                sleep_start_time = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy HH:mm",current_date+" "+settingsTable.getSleep_start_time());
                sleepstart_time.setText(commonUsedmethods.parse_date_in_this_format(current_date+" "+settingsTable.getSleep_start_time(),
                        "hh:mm a","dd-MM-yyyy HH:mm"));
            }

            if(!settingsTable.getSleep_duration().equalsIgnoreCase("0")){

                sleep_duration_time.setText(settingsTable.getSleep_duration());
            }

            if(!settingsTable.getEating_window_target_start().equalsIgnoreCase("0")){

                eating_window_start_time = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy HH:mm",current_date+" "+settingsTable.getEating_window_target_start());
                eatingwindowtarget_starttime.setText(commonUsedmethods.parse_date_in_this_format(current_date+" "+settingsTable.getEating_window_target_start(),
                        "hh:mm a","dd-MM-yyyy HH:mm"));
            }


            if(!settingsTable.getEating_window_target_end().equalsIgnoreCase("0")){

                eating_window_end_time = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy HH:mm",current_date+" "+settingsTable.getEating_window_target_end());
                eatingwindowtarget_endtime.setText(commonUsedmethods.parse_date_in_this_format(current_date+" "+settingsTable.getEating_window_target_end(),
                        "hh:mm a","dd-MM-yyyy HH:mm"));
            }

            if(!settingsTable.getActivity_target_count_step().equalsIgnoreCase("0")){

                activitytarget_steps.setText(settingsTable.getActivity_target_count_step());
            }

            if(settingsTable.getIs_begin_fast_active().equalsIgnoreCase("1")){

                beginfast_toggle.setChecked(true);
            }

            if(settingsTable.getIs_fast_complete_active().equalsIgnoreCase("1")){

                fastcomplete_toggle.setChecked(true);
            }

            if(settingsTable.getIs_reminder_active().equalsIgnoreCase("1")){

                fastingtimer_toggle.setChecked(true);
            }

            if(settingsTable.getIs_sleep_duration_active().equalsIgnoreCase("1")){

                sleep_duration_target_toggle.setChecked(true);
            }

            if(settingsTable.getIs_eating_window_target_active().equalsIgnoreCase("1")){

                eatingwindowtarget_toggle.setChecked(true);
            }

            if(settingsTable.getIs_activity_target_step_active().equalsIgnoreCase("1")){

                activitytarget_toggle.setChecked(true);
            }

            if(settingsTable.getIs_time_till_fast_active().equalsIgnoreCase("1")){

                timetillfast_toggle.setChecked(true);
            }

            if(settingsTable.getIs_time_since_food_active().equalsIgnoreCase("1")){

                timesincefood_toggle.setChecked(true);
            }

            if(settingsTable.getIs_days_exercise_active().equalsIgnoreCase("1")){

                daysexercise_toggle.setChecked(true);
            }

            if(settingsTable.getIs_steps_active().equalsIgnoreCase("1")){

                steps_toggle.setChecked(true);
            }

            if(settingsTable.getIs_hours_sleep_active().equalsIgnoreCase("1")){

                hoursofsleep_toggle.setChecked(true);
            }

            if(settingsTable.getIs_display_medicine_active().equalsIgnoreCase("1")){

                displaymedicine_toggle.setChecked(true);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            databaseRepo.updateSettingsData(settingsTable);

            call_settings_save_api();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //Todo call settings API
    private void call_settings_save_api(){

        try {
            String login_url = ApiConfig.SETTINGS_SAVE;
            JSONObject jsonObject = make_settings_request_json();

            new APIManager().Apicall(auth_token,login_url, jsonObject, new Apicallback() {
                @Override
                public void success(int code, String value) {


                }

                @Override
                public void failure(final String value) {


                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Todo make json request for settings API call
    private JSONObject make_settings_request_json(){

        JSONObject json = new JSONObject();
        try {

            json.put("participant_id", AESEncryption.encrypt(user_id));
            json.put("research_info_id", AESEncryption.encrypt(research_info_id));

            json.put("is_begin_fast_active", AESEncryption.encrypt(settingsTable.getIs_begin_fast_active()));
            json.put("is_fast_complete_active", AESEncryption.encrypt(settingsTable.getIs_fast_complete_active()));
            json.put("reminder_hr", AESEncryption.encrypt(settingsTable.getReminder_hr()));
            json.put("is_reminder_active", AESEncryption.encrypt(settingsTable.getIs_reminder_active()));
            json.put("is_slp_dur_active", AESEncryption.encrypt(settingsTable.getIs_sleep_duration_active()));
            json.put("slp_duration", AESEncryption.encrypt(settingsTable.getSleep_duration()));
            json.put("slp_st_time", AESEncryption.encrypt(settingsTable.getSleep_start_time()));
            json.put("eating_window_target_start", AESEncryption.encrypt(settingsTable.getEating_window_target_start()));
            json.put("eating_window_target_end", AESEncryption.encrypt(settingsTable.getEating_window_target_end()));
            json.put("is_eating_window_target_active", AESEncryption.encrypt(settingsTable.getIs_eating_window_target_active()));
            json.put("activity_target_cnt_step", AESEncryption.encrypt(settingsTable.getActivity_target_count_step()));
            json.put("is_activity_target_step_active", AESEncryption.encrypt(settingsTable.getIs_activity_target_step_active()));
            json.put("is_time_till_fast_active", AESEncryption.encrypt(settingsTable.getIs_time_till_fast_active()));
            json.put("is_time_since_food_active", AESEncryption.encrypt(settingsTable.getIs_time_since_food_active()));
            json.put("is_days_exercise_active", AESEncryption.encrypt(settingsTable.getIs_days_exercise_active()));
            json.put("is_steps_active", AESEncryption.encrypt(settingsTable.getIs_steps_active()));
            json.put("is_hours_sleep_active", AESEncryption.encrypt(settingsTable.getIs_hours_sleep_active()));
            json.put("is_display_medicine_active", AESEncryption.encrypt(settingsTable.getIs_display_medicine_active()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    //Todo call fetch participant common food API
    private void call_fetch_common_food_API(){

        try {
            String url = ApiConfig.FETCH_COMMON_FOOD_URL;
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("participant_id", AESEncryption.encrypt(user_id));
            jsonObject.put("research_info_id", AESEncryption.encrypt(research_info_id));

            new APIManager().Apicall(auth_token,url, jsonObject, new Apicallback() {
                @Override
                public void success(int code, String value) {

                    if (code == 200) {

                        //parse response
                        parse_fetch_common_food_response(value, new APIPasrsing() {

                            @Override
                            public void completed() {


                            }

                            @Override
                            public void error() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(InterVentionSettingsActivity.this,R.string.something_wrong,Toast.LENGTH_LONG).show();
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
                            Toast.makeText(InterVentionSettingsActivity.this, value, Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Todo parse food save response for json
    private void parse_fetch_common_food_response(String response,APIPasrsing apiPasrsing){

        try {
            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject!=null && jsonObject.has("message") && !jsonObject.isNull("message")) {

                JSONObject jsonObject1 = jsonObject.getJSONObject("message");
                String annotations = jsonObject1.getString("annotations");

                if(annotations!=null && !annotations.equalsIgnoreCase("")){

                    String value = AESEncryption.decrypt(annotations);
                    JsonParser parser = new JsonParser();
                    JsonElement jsonElement = parser.parse(value).getAsJsonArray();

                    fetchCommonFoodArrayList = new Gson().fromJson(jsonElement, new TypeToken<List<FetchCommonFood>>() {
                    }.getType());


                    format_participant_common_food();
                }


            }else{

                apiPasrsing.error();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void format_participant_common_food(){

        try {

            if (fetchCommonFoodArrayList != null && fetchCommonFoodArrayList.size() > 0) {

                ArrayList<FetchCommonFood> arrayList = new ArrayList<>();

                for (int i = 0; i < fetchCommonFoodArrayList.size(); i++) {

                    FetchCommonFood fetchCommonFood = fetchCommonFoodArrayList.get(i);

                    if (fetchCommonFood.getAnnotation_text().contains(",")) {

                        String[] ar = fetchCommonFood.getAnnotation_text().split(",");

                        for (int j = 0; j < ar.length; j++) {

                            arrayList = check_and_update_commonfood_array(arrayList,fetchCommonFood,ar[j].trim());
                        }

                    } else {

                        arrayList = check_and_update_commonfood_array(
                                arrayList,fetchCommonFood,fetchCommonFood.getAnnotation_text().trim());
                    }
                }

                fetchCommonFoodArrayList.clear();
                fetchCommonFoodArrayList.addAll(arrayList);

                insert_participant_common_food_to_local(fetchCommonFoodArrayList);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private ArrayList<FetchCommonFood> check_and_update_commonfood_array(final ArrayList<FetchCommonFood> arrayList, final FetchCommonFood fetchCommonFood,
                                                                         String food){

        FetchCommonFood updatedfood = new FetchCommonFood();

        try {
            if (arrayList.size() == 0) {

                updatedfood = create_fetchcommonfood_object(fetchCommonFood, new FetchCommonFood(), food);
                arrayList.add(updatedfood);

            } else {

                updatedfood = check_food_name_is_exist(arrayList, food, fetchCommonFood.getFood_type());

                if (updatedfood.getAnnotation_text().equalsIgnoreCase("")) {

                    updatedfood = create_fetchcommonfood_object(fetchCommonFood, updatedfood, food);
                    arrayList.add(updatedfood);

                } else {

                    updatedfood.setCount(updatedfood.getCount()+fetchCommonFood.getCount());

                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return arrayList;
    }

    private FetchCommonFood create_fetchcommonfood_object(FetchCommonFood fetchCommonFood_from,FetchCommonFood fetchCommonFood_to,
                                                          String food){

        fetchCommonFood_to.setAnnotation_text(food);
        fetchCommonFood_to.setCount(fetchCommonFood_from.getCount());
        fetchCommonFood_to.setFood_type(fetchCommonFood_from.getFood_type());

        return fetchCommonFood_to;
    }

    private void insert_participant_common_food_to_local(ArrayList<FetchCommonFood> arrayList){

        try {

            if(arrayList!=null && arrayList.size()>0){

                databaseRepo.clear_common_food();
                databaseRepo.insert_Participant_Common_Food(fetchCommonFoodArrayList);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(kProgressHUD!=null && kProgressHUD.isShowing()){
                            kProgressHUD.dismiss();
                            food_sync_notify.setVisibility(View.VISIBLE);
                            food_sync_rel.setEnabled(false);
                        }
                    }
                });
            }

        }catch (Exception e){
            e.printStackTrace();

        }

    }

    private FetchCommonFood check_food_name_is_exist(ArrayList<FetchCommonFood> arrayList,String food,String food_type){

        FetchCommonFood fetchCommonFood = new FetchCommonFood();

        try {
            for (int i = 0; i < arrayList.size(); i++) {

                if (arrayList.get(i).getAnnotation_text().equalsIgnoreCase(food) &&
                        arrayList.get(i).getFood_type().equalsIgnoreCase(food_type)) {

                    fetchCommonFood = arrayList.get(i);

                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return fetchCommonFood;

    }

    /**Todo call fetch sleep sync API*/
    private void call_sleep_sync_API(){

        try {
            String url = ApiConfig.TIPS;
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("participant_id", AESEncryption.encrypt(user_id));
            jsonObject.put("research_info_id", AESEncryption.encrypt(research_info_id));
           // jsonObject.put("subject", AESEncryption.encrypt("Intervention Period"));
           // jsonObject.put("message", AESEncryption.encrypt("need some help"));


            new APIManager().Apicall(auth_token,url, jsonObject, new Apicallback() {
                @Override
                public void success(int code, String value) {

                    if (code == 200) {

                        //parse response
                        parse_sleep_sync_response(value, new APIPasrsing() {

                            @Override
                            public void completed() {


                            }

                            @Override
                            public void error() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(InterVentionSettingsActivity.this,R.string.something_wrong,Toast.LENGTH_LONG).show();
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
                            Toast.makeText(InterVentionSettingsActivity.this, value, Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**Todo parse sleep sync API*/
    private void parse_sleep_sync_response(String response,APIPasrsing apiPasrsing){

        try {
            ArrayList<FoodSleepExData> foodSleepExDataArrayList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject!=null && jsonObject.has("message") && !jsonObject.isNull("message")) {

                JSONObject jsonObject1 = jsonObject.getJSONObject("message");
                if(jsonObject1.has("sleep")){
                    String sleep_data = AESEncryption.decrypt(jsonObject1.getString("sleep"));

                }


            }else{

                apiPasrsing.error();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void check_time_to_set_reminder(long timestamp,boolean before,String body){

        if (timestamp > System.currentTimeMillis()) {

            if(!before) {
                setLocalNotification.startAlarmBroadcastReceiver(InterVentionSettingsActivity.this, timestamp,body);
            }else{

                String text = faststartreminderhour.getText().toString();
                if(text.contains(" ")){
                    String [] ar = text.split(" ");

                    long time = timestamp - Integer.valueOf(ar[0])*60*1000;
                    body = "Your target eating window will end in "+ ar[0] +" minutes.";

                    if(time >System.currentTimeMillis()){
                        setLocalNotification.startAlarmBroadcastReceiver(InterVentionSettingsActivity.this, time,body);
                    }else{
                        setLocalNotification.startAlarmBroadcastReceiver(InterVentionSettingsActivity.this,
                                time + 24 * 60 * 60 * 1000,body);
                    }
                }


            }
        } else {

            if(!before) {
                setLocalNotification.startAlarmBroadcastReceiver(InterVentionSettingsActivity.this,
                        timestamp + 24 * 60 * 60 * 1000,body);
            }else{
                String text = faststartreminderhour.getText().toString();
                if(text.contains(" ")){
                    String [] ar = text.split(" ");

                    body = "Your target eating window will end in "+ ar[0] +" minutes.";
                    long time = timestamp + 24 * 60 * 60 * 1000 - Integer.valueOf(ar[0]) * 60 * 1000;


                    setLocalNotification.startAlarmBroadcastReceiver(InterVentionSettingsActivity.this, time,body);

                }
            }

        }

    }
}
