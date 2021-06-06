package com.salk.mycircadianclock.exercise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.salk.mycircadianclock.interfaces.OnRecycleItemClicks;
import com.salk.mycircadianclock.localDatabase.DatabaseRepo;
import com.salk.mycircadianclock.localDatabase.FoodSleepExData;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LogExerciseDataActivity extends AppCompatActivity {

    private RelativeLayout tabbar, rel_main,log_excercise_title_layout, date_layout, start_layout,
            end_layout, date_activity_main_layout, stop_timing_layout,
            containor4_layout;
    private Button exercise_start_time, select_date, light_title, moderate_title,
            intense_title, exercise_end_time,exercise_type_text_btn;
    private ImageView start_excercise, stop_excercise, resume_excercise;
    private FancyButton btn_save, btn_exit;
    private TextView stop_timing, stop_timer;


    private Locale locale = Locale.US;
    private String language = "",ex_level = "light",ex_duration = "00:00",user_id = "",auth_token = "",research_info_id = "";
    private CommonUsedmethods commonUsedmethods;
    private ArrayList<String> arr_exercise_type = new ArrayList<>();
    private Long measured_date = System.currentTimeMillis(),ex_end_timeinmills = 0L,ex_duration_time = 0L,original_request_time =0L;
    private TinylocalDb tinylocalDb;
    private SharedPreferences sharedPreferences,sharedPreferences_exercise;
    private SharedPreferences.Editor sharedPreferences_exercise_editor;
    private DatabaseRepo databaseRepo;
    private ShowTimer showTimer = new ShowTimer();
    private ExerciseEntries exerciseEntries;
    private FoodSleepExData foodSleepExData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            //to make screen full screen
            new CommonUsedmethods().makeActivityFullscreen(LogExerciseDataActivity.this);

            setContentView(R.layout.activity_log_exercise_data);

            init();

            click_function();

            setImageAccordingTolanguage();

            getExerciseType_array();

            check_exercise_start_pause();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void init(){

        try {
            tabbar = findViewById(R.id.tab_bar);
            rel_main = findViewById(R.id.main);
            start_excercise = findViewById(R.id.start_excercise);
            stop_excercise = findViewById(R.id.stop_excercise);
            resume_excercise = findViewById(R.id.resume_excercise);
            btn_save = findViewById(R.id.btn_save_spotify);
            btn_exit = findViewById(R.id.btn_exit_spotify);
            exercise_start_time = findViewById(R.id.exercise_start_time);
            exercise_end_time = findViewById(R.id.exercise_end_time);
            select_date = findViewById(R.id.select_date);
            log_excercise_title_layout = findViewById(R.id.log_excercise_title_layout);
            date_layout = findViewById(R.id.date_layout);
            start_layout = findViewById(R.id.start_layout);
            end_layout = findViewById(R.id.end_layout);
            date_activity_main_layout = findViewById(R.id.date_activity_main_layout);
            stop_timing_layout = findViewById(R.id.stop_timing_layout);
            containor4_layout = findViewById(R.id.containor4_layout);
            stop_timing = findViewById(R.id.stop_timing);
            stop_timer = findViewById(R.id.stop_timer);
            light_title = findViewById(R.id.light_title);
            moderate_title = findViewById(R.id.moderate_title);
            intense_title = findViewById(R.id.intense_title);
            exercise_type_text_btn = findViewById(R.id.excercise_list);


            new TabbarClick().click(LogExerciseDataActivity.this, tabbar, rel_main, "exercise");
            commonUsedmethods = new CommonUsedmethods();
            tinylocalDb = new TinylocalDb();
            databaseRepo = new DatabaseRepo(LogExerciseDataActivity.this);
            sharedPreferences = tinylocalDb.get_shared_pref(LogExerciseDataActivity.this);
            sharedPreferences_exercise_editor = tinylocalDb.create_shared_pref(LogExerciseDataActivity.this, "exercise_Pref");
            sharedPreferences_exercise = tinylocalDb.get_shared_pref_exercise(LogExerciseDataActivity.this);
            language = Locale.getDefault().getLanguage();
            user_id = tinylocalDb.get_data_in_shared(sharedPreferences, "user_id");
            auth_token = tinylocalDb.get_data_in_shared(sharedPreferences, "auth_token");
            research_info_id = tinylocalDb.get_data_in_shared(sharedPreferences, "research_info_id");

            language = Locale.getDefault().getDisplayLanguage();
            if(language.matches("français")){
                locale = Locale.FRENCH;

                exercise_start_time.setText(commonUsedmethods.parse_date_in_this_format(System.currentTimeMillis(),"HH:mm"));
            }else{
                locale = Locale.US;

                exercise_start_time.setText(commonUsedmethods.parse_date_in_this_format(System.currentTimeMillis(),"hh:mm a"));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void click_function(){

        try {

            select_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showDatePicker();
                }
            });

            exercise_start_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showTimePicker("start");
                }
            });

            exercise_end_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showTimePicker("end");
                }
            });

            light_title.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    ex_level = "light";
                    setBackground_for_ligh_intense_moderate(light_title);
                }
            });
            moderate_title.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub

                    ex_level = "moderate";
                    setBackground_for_ligh_intense_moderate(moderate_title);
                }
            });
            intense_title.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub

                    ex_level = "intense";
                    setBackground_for_ligh_intense_moderate(intense_title);

                }
            });

            exercise_type_text_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showList();
                }
            });

            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if(!check_validation()){

                        tinylocalDb.clear_shared_pref_data(sharedPreferences_exercise);

                        original_request_time = System.currentTimeMillis();
                        save_exercise_data_to_local(original_request_time);
                        save_sleep_to_foodsleepex_table(original_request_time);

                        call_exercise_api();
                        onBackPressed();
                    }

                }
            });

            btn_exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    tinylocalDb.clear_shared_pref_data(sharedPreferences_exercise);
                    onBackPressed();
                }
            });

            start_excercise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    updateUiforTimer();
                    measured_date = System.currentTimeMillis();
                    tinylocalDb.put_data_in_shared(sharedPreferences_exercise_editor, "start_exercise_time", String.valueOf(measured_date));
                    showTimer.StartTimer(measured_date, 0);
                }
            });

            stop_excercise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showTimer.StopTimer();
                    start_excercise.setVisibility(View.INVISIBLE);
                    stop_excercise.setVisibility(View.INVISIBLE);
                    resume_excercise.setVisibility(View.VISIBLE);
                    log_excercise_title_layout.setVisibility(View.INVISIBLE);
                    date_activity_main_layout.setVisibility(View.INVISIBLE);
                    stop_timing_layout.setVisibility(View.VISIBLE);
                    btn_save.setVisibility(View.VISIBLE);
                    btn_exit.setVisibility(View.VISIBLE);


                }
            });
            resume_excercise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showTimer.StartTimer(System.currentTimeMillis(), 0);
                    start_excercise.setVisibility(View.INVISIBLE);
                    resume_excercise.setVisibility(View.INVISIBLE);
                    stop_excercise.setVisibility(View.VISIBLE);
                    log_excercise_title_layout.setVisibility(View.INVISIBLE);
                    date_activity_main_layout.setVisibility(View.INVISIBLE);
                    stop_timing_layout.setVisibility(View.VISIBLE);
                    btn_save.setVisibility(View.INVISIBLE);
                    btn_exit.setVisibility(View.INVISIBLE);
                }
            });


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


    private void showDatePicker() {

        try {
            final Calendar dateandtime = Calendar.getInstance(locale);

            DatePickerDailogExcercise dp = new DatePickerDailogExcercise(
                    LogExerciseDataActivity.this, dateandtime,
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

    private void showTimePicker(final String from) {

        try {
            commonUsedmethods.TimePicker(LogExerciseDataActivity.this, new CommonUsedmethods.TimePickerListner() {
                @Override
                public void OnDoneButton(Dialog datedialog, String hour, String min, String AM_PM) {

                    do_calculation_with_selected_time(from,hour,min,AM_PM);
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

    private void setBackground_for_ligh_intense_moderate(Button selected_button){

        try {
            light_title.setBackgroundResource(0);
            light_title.setTextColor(Color.BLACK);

            moderate_title.setBackgroundResource(0);
            moderate_title.setTextColor(Color.BLACK);

            intense_title.setBackgroundResource(0);
            intense_title.setTextColor(Color.BLACK);

            selected_button.setBackgroundResource(R.color.excersize_bg);
            selected_button.setTextColor(Color.WHITE);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void showList() {

        try {

            final Dialog exercise_dialog = new Dialog(LogExerciseDataActivity.this, android.R.style.Theme);
            exercise_dialog.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

            exercise_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            exercise_dialog.setContentView(R.layout.dialog_list);

            Button add_btn = exercise_dialog.findViewById(R.id.add_items);
            RecyclerView exercise_list = exercise_dialog.findViewById(R.id.list);
            final EditText search_fileld = exercise_dialog.findViewById(R.id.edit_items);
            Button close =  exercise_dialog.findViewById(R.id.close_btn);


            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(LogExerciseDataActivity.this);
            exercise_list.setLayoutManager(linearLayoutManager);
            final  ExerciseTypeAdapter exerciseTypeAdapter = new ExerciseTypeAdapter(arr_exercise_type,
                    LogExerciseDataActivity.this, new OnRecycleItemClicks() {
                @Override
                public void onClick(Object object) {

                    search_fileld.setText("");
                    set_ex_type((String) object);
                    exercise_dialog.dismiss();

                }
            });
            exercise_list.setAdapter(exerciseTypeAdapter);
            exerciseTypeAdapter.notifyDataSetChanged();

            close.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub

                    search_fileld.setText("");
                    exercise_dialog.dismiss();
                }
            });

            search_fileld.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {

                    String text = search_fileld.getText().toString().trim()
                            .toLowerCase(Locale.getDefault());
                    exerciseTypeAdapter.filter(text);

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            add_btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    try {
                        if (!search_fileld.getText().toString().trim().isEmpty()) {

                            if(!arr_exercise_type.contains(search_fileld.getText().toString().trim())){

                                arr_exercise_type.add(search_fileld.getText().toString().trim());
                                exerciseTypeAdapter.notifyDataSetChanged();
                                set_ex_type(search_fileld.getText().toString().trim());
                                search_fileld.setText("");
                                exercise_dialog.dismiss();
                            }

                        } else {

                            commonUsedmethods.show_Toast(LogExerciseDataActivity.this, getResources().getString(R.string.need_to_enter_exercise));

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });



            exercise_dialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void getExerciseType_array(){

        try {
            if (language.matches("français")) {

                arr_exercise_type.add("Cardiovasculaire");
                arr_exercise_type.add("Natation");
                arr_exercise_type.add("Vélo");
                arr_exercise_type.add("Course à pied");
                arr_exercise_type.add("Marche");

                arr_exercise_type.add("Football");
                arr_exercise_type.add("Hockey sur glace");
                arr_exercise_type.add("Le rugby");
                arr_exercise_type.add("Tennis");
                arr_exercise_type.add("Le golf");
                arr_exercise_type.add("Roller");
                arr_exercise_type.add("Volley-ball");
                arr_exercise_type.add("Zumba");
                arr_exercise_type.add("Faire de la planche à roulettes");
                arr_exercise_type.add("Baseball");

                arr_exercise_type.add("Fitness");
                arr_exercise_type.add("Basketball");
                arr_exercise_type.add("Cricket");
                arr_exercise_type.add("Skiing");
                arr_exercise_type.add("Lacrosse");
                arr_exercise_type.add("Snowboard");

                arr_exercise_type.add("Bowling");
                arr_exercise_type.add("Frisbee ultime");
            } else {

                arr_exercise_type.add("Aerobics");
                arr_exercise_type.add("Swimming");
                arr_exercise_type.add("Biking");
                arr_exercise_type.add("Running");
                arr_exercise_type.add("Walking");

                arr_exercise_type.add("Football");
                arr_exercise_type.add("Ice Hockey");
                arr_exercise_type.add("Rugby");
                arr_exercise_type.add("Tennis");
                arr_exercise_type.add("Golf");
                arr_exercise_type.add("Rollerskating");
                arr_exercise_type.add("Volleyball");
                arr_exercise_type.add("Zumba");
                arr_exercise_type.add("Skateboarding");
                arr_exercise_type.add("Baseball");


                arr_exercise_type.add("Gym");
                arr_exercise_type.add("Basketball");
                arr_exercise_type.add("Cricket");
                arr_exercise_type.add("Skiing");
                arr_exercise_type.add("Lacrosse");
                arr_exercise_type.add("Snowboarding");

                arr_exercise_type.add("Bowling");
                arr_exercise_type.add("Ultimate Frisbee");

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void set_ex_type(String exercise){

        try {
            exercise_type_text_btn.setText(exercise);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //Todo call exercise save api
    private void call_exercise_api() {

        try {
            String login_url = ApiConfig.EXERCISE_SAVE_URL;
            JSONObject jsonObject = make_json_for_exercise_save_request();

            new APIManager().Apicall(auth_token, login_url, jsonObject, new Apicallback() {
                @Override
                public void success(int code, String value) {

                    if (code == 200) {

                        //parse response
                        parse_exercise_save_response(value, new APIPasrsing() {

                            @Override
                            public void completed() {


                                exerciseEntries = databaseRepo.getExercise(exerciseEntries.getTimestamp());
                                databaseRepo.deleteExercise(exerciseEntries);
                                databaseRepo.updateFoodSleepExdata_(foodSleepExData.getActual_log_request());
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
                                        Toast.makeText(LogExerciseDataActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
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
                            Toast.makeText(LogExerciseDataActivity.this, value, Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject make_json_for_exercise_save_request() {

        JSONObject json = new JSONObject();
        try {
            json.put("participant_id", AESEncryption.encrypt(user_id));
            json.put("research_info_id",  AESEncryption.encrypt(research_info_id));
            json.put("exercise_data", AESEncryption.encrypt(makeJsonexercise()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    public String makeJsonexercise(){
        JSONObject jsonObject =null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("exercise_duration", ex_duration);
            jsonObject.put("exercise_level", ex_level);
            jsonObject.put("measured_date", commonUsedmethods.parse_date_in_this_format(measured_date,"yyyy-MM-dd"));
            jsonObject.put("measured_time", commonUsedmethods.parse_date_in_this_format(measured_date,"HH:mm"));
            jsonObject.put("exercise_name", exercise_type_text_btn.getText().toString().trim());
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    //Todo parse food save response for json
    private void parse_exercise_save_response(String response, APIPasrsing apiPasrsing) {

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

    private void do_calculation_with_selected_time(String from,String hour,String min, String AM_PM){

        try{

            String curr_date = commonUsedmethods.parse_date_in_this_format(measured_date,"dd-MM-yyyy");

            if(from.equalsIgnoreCase("start")){

                String ex_start_time = hour+":"+min+" "+AM_PM;


                if(AM_PM.equalsIgnoreCase("")){
                    measured_date = commonUsedmethods.convert_date_to_timestamp("dd-mm-yyy HH:mm",curr_date+" "+ex_start_time);
                }else{
                    measured_date = commonUsedmethods.convert_date_to_timestamp("dd-mm-yyy hh:mm a",curr_date+" "+ex_start_time);
                }

                exercise_start_time.setText(hour+":"+min+" "+AM_PM);

            }else{

                String ex_end_time = hour+":"+min+" "+AM_PM;
                exercise_end_time.setText(hour+":"+min+" "+AM_PM);

                if(AM_PM.equalsIgnoreCase("")){
                    ex_end_timeinmills = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy HH:mm",curr_date+" "+ex_end_time);
                }else{
                    ex_end_timeinmills = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy hh:mm a",curr_date+" "+ex_end_time);
                }

                if(ex_end_timeinmills!=0){

                    getduration(measured_date,ex_end_timeinmills);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getduration(Long ex_start_inmills, Long ex_end_timeinmills){

        try{

            if(ex_end_timeinmills>ex_start_inmills) {

                ex_duration_time = ex_end_timeinmills - ex_start_inmills;

                if (ex_duration_time > 60 * 1000) {

                    ex_duration = String.format(
                            "%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(ex_duration_time),
                            TimeUnit.MILLISECONDS.toMinutes(ex_duration_time)
                                    - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                                    .toHours(ex_duration_time)));

                } else {

                    commonUsedmethods.show_Toast(LogExerciseDataActivity.this,getResources().getString(R.string.exercise_time_you_entered_less));
                }

            }else{

                exercise_end_time.setText("");
                commonUsedmethods.show_Toast(LogExerciseDataActivity.this,getResources().getString(R.string.End_time_Should_greater));
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private boolean check_validation(){

        boolean check = false;
        try {

             if (ex_end_timeinmills==0) {

                check = true;
                commonUsedmethods.show_Toast(LogExerciseDataActivity.this, getResources().getString(R.string.Please_select_duration));

             }
             if (TextUtils.isEmpty(exercise_type_text_btn.getText().toString().trim())
                        || exercise_type_text_btn.getText().toString().trim().equalsIgnoreCase(getResources().getString(R.string.EXERCISE))) {

                check = true;
                commonUsedmethods.show_Toast(LogExerciseDataActivity.this, getResources().getString(R.string.Please_select_exercise_type));
             }

             if(ex_duration.equalsIgnoreCase("00:00")){
                 check = true;
                 commonUsedmethods.show_Toast(LogExerciseDataActivity.this,getResources().getString(R.string.exercise_time_you_entered_less));

             }
        }catch (Exception e){
            e.printStackTrace();
        }

        return check;
    }


    //Todo  Showing the Timer
    public class ShowTimer {

        long timeInMilliseconds = 0L;
        long updatedTime = 0L;
        private long startTime = 0L, endTime = 0L;
        private Handler customHandler = new Handler();

        private Runnable updateTimerThread = new Runnable() {

            public void run() {

                endTime = System.currentTimeMillis();
                timeInMilliseconds = endTime - startTime;
                updatedTime = updatedTime + timeInMilliseconds;
                startTime = endTime;

                int secs = (int) (updatedTime / (1000) % 60);
                int minutes = (int) ((updatedTime / (1000 * 60)) % 60);
                int hours = (int) ((updatedTime / (1000 * 60 * 60)) % 24);


                stop_timer.setText(String.format("%02d", hours) + " hrs:" +
                        String.format("%02d", minutes) + " mins:" + String.format("%02d", secs) + " secs");
                customHandler.postDelayed(this, 0);

                //tinylocalDb.put_data_in_shared(sharedPreferences_exercise_editor, "exercise_duration", String.valueOf(updatedTime));

            }

        };

        public void StartTimer(long time, long delaymills) {

            if (tinylocalDb.get_data_in_shared(sharedPreferences_exercise, "start_exercise_time").equalsIgnoreCase("")) {
                measured_date = time;
            } else {

                measured_date = Long.valueOf(
                        tinylocalDb.get_data_in_shared(sharedPreferences_exercise, "start_exercise_time"));
            }

            if (!tinylocalDb.get_data_in_shared(sharedPreferences_exercise, "is_paused").equalsIgnoreCase("")) {

                tinylocalDb.clear_key_from_shared_pref(sharedPreferences_exercise, "is_paused");
                tinylocalDb.put_data_in_shared(sharedPreferences_exercise, "resume_exercise", String.valueOf(time));
            }

            startTime = time;//
            customHandler.postDelayed(updateTimerThread, delaymills);
        }

        public void StopTimer() {

            tinylocalDb.put_data_in_shared(sharedPreferences_exercise_editor, "exercise_duration", String.valueOf(updatedTime));
            tinylocalDb.put_data_in_shared(sharedPreferences_exercise_editor, "is_paused", "true");

            customHandler.removeCallbacks(updateTimerThread);
            set_start_end_time(measured_date, measured_date + updatedTime);
        }

        public void setdata_for_pause(long updatetime) {

            updatedTime = updatetime;

        }

    }

    //Todo set start and end timer after pause the sleep timer
    private void set_start_end_time(long sleep_start_mills1, long sleep_end_mills1) {

        try {
            measured_date = sleep_start_mills1;
            ex_end_timeinmills = sleep_end_mills1;


            getduration(measured_date, ex_end_timeinmills);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUiforTimer(){

        start_excercise.setVisibility(View.INVISIBLE);
        stop_excercise.setVisibility(View.VISIBLE);
        log_excercise_title_layout.setVisibility(View.INVISIBLE);
        date_activity_main_layout.setVisibility(View.INVISIBLE);
        stop_timing_layout.setVisibility(View.VISIBLE);
        btn_save.setVisibility(View.INVISIBLE);
        btn_exit.setVisibility(View.INVISIBLE);
    }

    private void setImageAccordingTolanguage(){


        if(language.matches("français")){

            start_excercise.setImageResource(R.mipmap.start_excercise_french);
            stop_excercise.setImageResource(R.mipmap.stop_excercise_french);
            resume_excercise.setImageResource(R.mipmap.resume_excercise_french);
        }
        else
        {
            start_excercise.setImageResource(R.mipmap.start_ex);
            stop_excercise.setImageResource(R.mipmap.stop_ex);
            resume_excercise.setImageResource(R.mipmap.resume_ex);
        }
    }


    //Todo check for timer data and details if app will come from any other screen or from background
    private void check_exercise_start_pause() {

        try {
            long exercise_start_time = 0L, exercise_duration = 0L;
            String is_paused = "";


            if (!tinylocalDb.get_data_in_shared(sharedPreferences_exercise, "start_exercise_time").equalsIgnoreCase("")) {

                exercise_start_time = Long.valueOf(tinylocalDb.get_data_in_shared(sharedPreferences_exercise, "start_exercise_time"));
            }

            if (!tinylocalDb.get_data_in_shared(sharedPreferences_exercise, "exercise_duration").equalsIgnoreCase("")) {

                exercise_duration = Long.valueOf(tinylocalDb.get_data_in_shared(sharedPreferences_exercise, "exercise_duration"));
            }

            if (!tinylocalDb.get_data_in_shared(sharedPreferences_exercise, "is_paused").equalsIgnoreCase("")) {

                is_paused = tinylocalDb.get_data_in_shared(sharedPreferences_exercise, "is_paused");
            }

            if (exercise_start_time != 0) {

                if (is_paused.equalsIgnoreCase("true")) {

                    set_start_end_time(exercise_start_time, exercise_start_time + exercise_duration);
                    showTimer.setdata_for_pause(exercise_duration);

                    int secs = (int) (exercise_duration / (1000) % 60);
                    int minutes = (int) ((exercise_duration / (1000 * 60)) % 60);
                    int hours = (int) ((exercise_duration / (1000 * 60 * 60)) % 24);


                    stop_timer.setText(String.format("%02d", hours) + " hrs:" +
                            String.format("%02d", minutes) + " mins:" + String.format("%02d", secs) + " secs");

                    start_excercise.setVisibility(View.INVISIBLE);
                    stop_excercise.setVisibility(View.INVISIBLE);
                    resume_excercise.setVisibility(View.VISIBLE);
                    log_excercise_title_layout.setVisibility(View.INVISIBLE);
                    date_activity_main_layout.setVisibility(View.INVISIBLE);
                    stop_timing_layout.setVisibility(View.VISIBLE);
                    btn_save.setVisibility(View.VISIBLE);
                    btn_exit.setVisibility(View.VISIBLE);

                } else {

                    updateUiforTimer();

                    measured_date = System.currentTimeMillis();
                    if (tinylocalDb.get_data_in_shared(sharedPreferences_exercise, "resume_exercise").equalsIgnoreCase("")) {
                        showTimer.setdata_for_pause(measured_date - exercise_start_time);
                    }else{
                        showTimer.setdata_for_pause(exercise_duration+(measured_date - Long.valueOf(tinylocalDb.get_data_in_shared(sharedPreferences_exercise, "resume_exercise"))));
                    }
                    showTimer.StartTimer(measured_date, 0);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Todo store exercise entry data in local storage
    private void save_exercise_data_to_local(long original_request_time) {

        try {
            String exercise_json = makeJsonexercise();

            exerciseEntries = new ExerciseEntries();
            exerciseEntries.setExercise_details(exercise_json);
            exerciseEntries.setTimestamp(original_request_time);
            exerciseEntries.setStatus("pending");
            databaseRepo.insertExerciseEntries(exerciseEntries);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Todo save exercise data to FoodSleepExData table in local
    private void save_sleep_to_foodsleepex_table(long original_request_time) {

        try {
            foodSleepExData = new FoodSleepExData();
            foodSleepExData.setTimestamp(measured_date);
            foodSleepExData.setExercise_name(exercise_type_text_btn.getText().toString().trim());
            foodSleepExData.setExercise_duration(ex_duration);
            foodSleepExData.setType("exercise");
            foodSleepExData.setActual_log_request(original_request_time);
            databaseRepo.insertFoodSleepExdata(foodSleepExData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {

        showTimer.customHandler.removeCallbacks(showTimer.updateTimerThread);
        super.onDestroy();
    }
}
