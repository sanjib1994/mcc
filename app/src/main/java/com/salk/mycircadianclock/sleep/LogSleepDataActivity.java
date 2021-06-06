package com.salk.mycircadianclock.sleep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.Utility.DatePickerCommonFood;
import com.salk.mycircadianclock.Utility.DatePickerSetPreviousTime;
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
import com.salk.mycircadianclock.localDatabase.OnDataFetched;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LogSleepDataActivity extends AppCompatActivity {

    private RelativeLayout tabbar, rel_main, disablelayforcheckbox, rel_sleep_dificulty, date_activity_main_layout, log_sleep_title_layout, stop_timing_layout, sleep_pending_lay, containor3;
    private Button sleep_start_time, sleep_end_time, selected_duration;
    private FancyButton btn_save_sleep, btn_cancel;
    private CheckBox check_difficulty_in_sleep, check_woke_up_early, check_insufficient_sleep;
    private EditText other_difficulty;
    private ImageView smile_happy, smile_sad, img_start_sleep, img_stop_sleep, img_resume_sleep;
    private ScrollView scroll_view;
    private RecyclerView previousdatelist;
    private TextView stop_sleep_timing_title, stop_actual_timer, pending_sleep_timing_title, editbedtime, editrisetime, editsleptfor;

    private Calendar dateandtime;
    private CommonUsedmethods commonUsedmethods;
    private String user_id = "", auth_token = "",research_info_id = "";
    private SharedPreferences sharedPreferences, sharedPreferences_sleep;
    private SharedPreferences.Editor sharedPreferences_sleep_editor;
    private String sleep_duartion = "00:00";
    private long measured_date, sleep_duration_time, sleep_start_mills, sleep_end_mills,original_request_time = 0L;
    private boolean status_sleep_well = true;
    private ArrayList<String> add_problem_array = new ArrayList<>();
    private ArrayList<PreviousSleepEntry> previousSleepEntryArrayList = new ArrayList<>();
    private String language = "";
    private ShowTimer showTimer = new ShowTimer();
    private Handler handler = new Handler();
    private Runnable runnable;
    private TinylocalDb tinylocalDb;
    private SleepEntries sleepEntries;
    private DatabaseRepo databaseRepo;
    private ArrayList<Long> arrayList = new ArrayList<>();
    private int i = 0;
    private PreviousdaySleepEntryAdapter sleep_previous_adapter;
    private FoodSleepExData update_food_sleep_exdaat,foodSleepExData;
    private Long next_slepp_date = 0L;
    private ArrayList<FoodSleepExData> deletedSleepDataList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            //to make screen full screen
            new CommonUsedmethods().makeActivityFullscreen(LogSleepDataActivity.this);

            setContentView(R.layout.activity_log_sleep_dat);

            //initialization of all view
            init();

            //click function for required views
            click_function();

            setimages_accordingto_language();

            setPreviousSleepData();

            check_sleep_start_pause();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //Todo intialize all view
    private void init() {

        try {
            tabbar = findViewById(R.id.tab_bar);
            rel_main = findViewById(R.id.main);
            sleep_start_time = findViewById(R.id.sleep_start_time);
            sleep_end_time = findViewById(R.id.sleep_end_time);
            selected_duration = findViewById(R.id.selected_duration);
            btn_save_sleep = findViewById(R.id.btn_save_spotify_sleep);
            btn_cancel = findViewById(R.id.btn_exit_spotify_sleep);
            rel_sleep_dificulty = findViewById(R.id.rel_sleep_dificulty);
            check_difficulty_in_sleep = findViewById(R.id.difficultCHECK);
            check_insufficient_sleep = findViewById(R.id.NotCHECK);
            check_woke_up_early = findViewById(R.id.WokeCHECK);
            other_difficulty = findViewById(R.id.otheredt);
            smile_happy = findViewById(R.id.smile_happy);
            smile_sad = findViewById(R.id.smile_sad);
            scroll_view = findViewById(R.id.scroll_view);
            disablelayforcheckbox = findViewById(R.id.disablelayforcheckbox);
            previousdatelist = findViewById(R.id.previousdatelist);
            img_start_sleep = findViewById(R.id.start_sleep);
            img_stop_sleep = findViewById(R.id.stop_sleep);
            img_resume_sleep = findViewById(R.id.resume_sleep);
            date_activity_main_layout = findViewById(R.id.date_activity_main_layout);
            log_sleep_title_layout = findViewById(R.id.log_sleep_title_layout);
            stop_timing_layout = findViewById(R.id.stop_timing_layout);
            sleep_pending_lay = findViewById(R.id.sleep_pending_lay);
            containor3 = findViewById(R.id.containor3);
            stop_sleep_timing_title = findViewById(R.id.stop_sleep_timing_title);
            stop_actual_timer = findViewById(R.id.stop_actual_timer);
            pending_sleep_timing_title = findViewById(R.id.pending_sleep_timing_title);


            new TabbarClick().click(LogSleepDataActivity.this, tabbar, rel_main, "sleep");
            commonUsedmethods = new CommonUsedmethods();
            tinylocalDb = new TinylocalDb();
            databaseRepo = new DatabaseRepo(LogSleepDataActivity.this);
            sharedPreferences = tinylocalDb.get_shared_pref(LogSleepDataActivity.this);
            sharedPreferences_sleep_editor = tinylocalDb.create_shared_pref(LogSleepDataActivity.this, "sleep_Pref");
            sharedPreferences_sleep = tinylocalDb.get_shared_pref_sleep(LogSleepDataActivity.this);
            language = Locale.getDefault().getLanguage();
            user_id = tinylocalDb.get_data_in_shared(sharedPreferences, "user_id");
            auth_token = tinylocalDb.get_data_in_shared(sharedPreferences, "auth_token");
            research_info_id = tinylocalDb.get_data_in_shared(sharedPreferences, "research_info_id");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Todo all click functions
    private void click_function() {


        try {
            sleep_start_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    show_date_time_picker("start", false);
                }
            });

            sleep_end_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (sleep_start_mills != 0) {
                        show_date_time_picker("end", false);
                    } else {

                        show_date_time_picker("start", false);
                    }
                }
            });

            btn_save_sleep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (sleep_start_mills != 0 && sleep_end_mills != 0 && !auth_token.equalsIgnoreCase("")) {

                        tinylocalDb.clear_shared_pref_data(sharedPreferences_sleep);
                        getdata_for_naptime(sleep_start_mills, sleep_end_mills, "");
                    }
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    tinylocalDb.clear_shared_pref_data(sharedPreferences_sleep);

                    onBackPressed();
                }
            });

            smile_happy.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    smile_happy.setImageResource(R.mipmap.smile_happy_selected);
                    smile_sad.setImageResource(R.mipmap.smile_sad);

                    check_difficulty_in_sleep.setEnabled(false);
                    check_woke_up_early.setEnabled(false);
                    check_insufficient_sleep.setEnabled(false);
                    other_difficulty.setEnabled(false);
                    status_sleep_well = true;
                    rel_sleep_dificulty.setVisibility(View.GONE);
                    disablelayforcheckbox.setVisibility(View.VISIBLE);
                    scroll_view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scroll_view.fullScroll(View.FOCUS_UP);
                        }
                    }, 100);

                }
            });

            smile_sad.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub

                    smile_happy.setImageResource(R.mipmap.smile_happy);
                    smile_sad.setImageResource(R.mipmap.smile_sad_selected);

                    check_difficulty_in_sleep.setEnabled(true);
                    check_woke_up_early.setEnabled(true);
                    check_insufficient_sleep.setEnabled(true);

                    if (add_problem_array.size() != 0
                            && add_problem_array
                            .contains("Difficult falling asleep")) {
                        check_difficulty_in_sleep.setChecked(true);
                    }

                    if (add_problem_array.size() != 0
                            && add_problem_array.contains("Woke up once or more")) {
                        check_woke_up_early.setChecked(true);
                    }
                    if (add_problem_array.size() != 0
                            && add_problem_array.contains("Insufficient sleep")) {
                        check_insufficient_sleep.setChecked(true);

                    }

                    other_difficulty.setEnabled(true);
                    status_sleep_well = false;
                    rel_sleep_dificulty.setVisibility(View.VISIBLE);
                    disablelayforcheckbox.setVisibility(View.GONE);
                    scroll_view.fullScroll(View.FOCUS_DOWN);

                    scroll_view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scroll_view.fullScroll(View.FOCUS_DOWN);
                        }
                    }, 100);

                }
            });

            other_difficulty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i,
                                              int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2,
                                          int i3) {
                    if (charSequence.toString().startsWith(" ")) {

                        other_difficulty.setText("");
                    } else {

                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            check_difficulty_in_sleep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean status) {
                    // TODO Auto-generated method stub
                    if (status) {
                        // diff_checkbox.setButtonDrawable(R.drawable.chk_box_on);
                        add_problem_array.add("Difficult falling asleep");
                    } else {

                        // diff_checkbox.setButtonDrawable(R.drawable.chk_box);
                        if (add_problem_array.size() != 0
                                && add_problem_array
                                .contains("Difficult falling asleep")) {
                            int index = add_problem_array
                                    .indexOf("Difficult falling asleep");
                            add_problem_array.remove(index);
                        }
                    }
                }
            });
            check_woke_up_early
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton arg0,
                                                     boolean status) {
                            // TODO Auto-generated method stub
                            if (status) {

                                add_problem_array.add("Woke up once or more");
                            } else {

                                if (add_problem_array.size() != 0
                                        && add_problem_array
                                        .contains("Woke up once or more")) {
                                    int index = add_problem_array
                                            .indexOf("Woke up once or more");
                                    add_problem_array.remove(index);
                                }
                            }
                        }
                    });
            check_insufficient_sleep
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton arg0,
                                                     boolean status) {
                            // TODO Auto-generated method stub
                            if (status) {

                                add_problem_array.add("Insufficient sleep");
                            } else {

                                if (add_problem_array.size() != 0
                                        && add_problem_array
                                        .contains("Insufficient sleep")) {
                                    int index = add_problem_array
                                            .indexOf("Insufficient sleep");
                                    add_problem_array.remove(index);
                                }
                            }
                        }
                    });


            img_start_sleep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showList();
                }
            });
            img_stop_sleep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showTimer.StopTimer();
                    btn_cancel.setVisibility(View.VISIBLE);
                    btn_save_sleep.setVisibility(View.VISIBLE);
                    img_start_sleep.setVisibility(View.GONE);
                    img_stop_sleep.setVisibility(View.GONE);
                    img_resume_sleep.setVisibility(View.VISIBLE);

                    log_sleep_title_layout.setVisibility(View.GONE);
                    stop_timing_layout.setVisibility(View.VISIBLE);
                    stop_sleep_timing_title.setText("You slept for");
                    date_activity_main_layout.setVisibility(View.VISIBLE);

                    containor3.setVisibility(View.VISIBLE);
                    smile_happy.setEnabled(true);
                    smile_sad.setEnabled(true);

                }
            });
            img_resume_sleep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showTimer.StartTimer(System.currentTimeMillis(), 0);
                    btn_cancel.setVisibility(View.INVISIBLE);
                    btn_save_sleep.setVisibility(View.INVISIBLE);
                    img_start_sleep.setVisibility(View.GONE);
                    img_resume_sleep.setVisibility(View.INVISIBLE);
                    img_stop_sleep.setVisibility(View.VISIBLE);

                    log_sleep_title_layout.setVisibility(View.GONE);
                    stop_timing_layout.setVisibility(View.VISIBLE);
                    stop_sleep_timing_title.setText(getResources().getString(R.string.You_have_been_sleeping_for));
                    date_activity_main_layout.setVisibility(View.INVISIBLE);
                    smile_happy.setEnabled(false);
                    containor3.setVisibility(View.INVISIBLE);


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Todo start stop resume image according default language of device
    private void setimages_accordingto_language() {

        try {
            if (language.matches("français")) {
                img_start_sleep.setImageResource(R.mipmap.start_spl_french);
                img_stop_sleep.setImageResource(R.mipmap.stop_spl_french);
                img_resume_sleep.setImageResource(R.mipmap.resume_spl_french);
            } else {
                img_start_sleep.setImageResource(R.mipmap.start_slp);
                img_stop_sleep.setImageResource(R.mipmap.stop_slp);
                img_resume_sleep.setImageResource(R.mipmap.resume_slp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo show dateTime picker
    private void show_date_time_picker(final String from, final boolean is_from_edit) {

        try {

            if (language.matches("français")) {
                dateandtime = Calendar.getInstance(Locale.FRENCH);
            } else {
                dateandtime = Calendar.getInstance(Locale.US);
            }

            DatePickerCommonFood dp = new DatePickerCommonFood(LogSleepDataActivity.this,
                    dateandtime, new DatePickerCommonFood.DatePickerListner() {

                @Override
                public void OnDoneButton(Dialog datedialog, Calendar c) {

                    datedialog.dismiss();

                    do_calculation_withslected_time(from, c, is_from_edit);

                }

                @Override
                public void OnCancelButton(Dialog datedialog) {
                    // TODO Auto-generated method stub

                    datedialog.dismiss();


                }
            });
            dp.setCancelable(false);
            dp.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Todo calculate duration from sleep start and end time
    private void getDuration_of_sleep(long sleep_start_mills, long sleep_end_mills) {

        try {
            sleep_duration_time = sleep_end_mills - sleep_start_mills;

            if (sleep_duration_time > 24 * 60 * 60 * 1000) {

                resetValues(R.string.you_entered_is_more);

            } else if (sleep_duration_time < 60 * 1000) {

                resetValues(R.string.you_entered_is_less);
            } else {
                sleep_duartion = String.format(
                        "%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(sleep_duration_time),
                        TimeUnit.MILLISECONDS.toMinutes(sleep_duration_time)
                                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                                .toHours(sleep_duration_time)));

                selected_duration.setText(sleep_duartion);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Todo calculation after date selection from calender for sleep entry
    private void do_calculation_withslected_time(String from, Calendar c, boolean is_from_edit) {

        try {
            String time;

            if (from.equalsIgnoreCase("start")) {

                String selected_date = commonUsedmethods.parse_date_in_this_format(c.getTimeInMillis(), "dd MMM yyyy");

                Calendar current_date = Calendar.getInstance();

                if (c.after(current_date) && selected_date.contains("31 Dec")) {

                    c.add(Calendar.YEAR, -1);
                }

                if (c.after(current_date)) {

                    sleep_start_mills = 0;
                    sleep_start_time.setText(getResources().getString(R.string.Set_Date));
                    commonUsedmethods.show_Toast(LogSleepDataActivity.this, getResources().getString(R.string.bed_time_shouldnot_greater));

                } else {

                    sleep_start_mills = c.getTimeInMillis();
                    time = commonUsedmethods.parse_date_in_this_format(c.getTimeInMillis(), "dd MMM yyyy hh:mm a");
                    sleep_start_time.setText(time);
                    commonUsedmethods.show_Toast(LogSleepDataActivity.this, getResources().getString(R.string.Bed_time_Set_Successfully));

                    int hr_status = c.get(Calendar.HOUR_OF_DAY);
                    int mm_status = c.get(Calendar.MINUTE);

                    if (hr_status >= 4 && mm_status >= 0) {

                        measured_date = c.getTimeInMillis();

                    } else {

                        c.add(Calendar.DATE, -1);

                        measured_date = c.getTimeInMillis();
                    }

                    if (is_from_edit) {

                        show_date_time_picker("end", true);
                    }

                }


            } else if (from.equalsIgnoreCase("end")) {

                String selected_date = commonUsedmethods.parse_date_in_this_format(sleep_start_mills, "dd MMM yyyy");

                if (selected_date.contains("Dec")) {

                    String year = commonUsedmethods.parse_date_in_this_format(sleep_start_mills, "yyyy");

                    c.set(Calendar.YEAR, Integer.valueOf(year) + 1);
                }

                sleep_end_mills = c.getTimeInMillis();

                time = commonUsedmethods.parse_date_in_this_format(sleep_end_mills, "dd MMM yyyy hh:mm a");
                sleep_end_time.setText(time);

            }

            if (sleep_start_mills != 0 && sleep_end_mills != 0) {

                if (sleep_start_mills < sleep_end_mills) {
                    getDuration_of_sleep(sleep_start_mills, sleep_end_mills);
                } else {

                    resetValues(R.string.End_time_Should_be);
                }
            }
        } catch (Exception e) {
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

    //Todo call sleep save api
    private void call_sleep_api(String sleep_diificulty) {

        try {
            String login_url = ApiConfig.SLEEP_SAVE_URL;
            JSONObject jsonObject = make_sleep_save_request_json(sleep_diificulty);

            new APIManager().Apicall(auth_token, login_url, jsonObject, new Apicallback() {
                @Override
                public void success(int code, String value) {

                    if (code == 200) {

                        //parse response
                        parse_sleep_save_response(value, new APIPasrsing() {

                            @Override
                            public void completed() {

                                databaseRepo.updateFoodSleepExdata_(foodSleepExData.getActual_log_request());
                                sleepEntries = databaseRepo.getSleep(sleepEntries.getTimestamp());
                                databaseRepo.deleteSleep(sleepEntries);

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
                                        Toast.makeText(LogSleepDataActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
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
                            Toast.makeText(LogSleepDataActivity.this, value, Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo make food save request for json
    private JSONObject make_sleep_save_request_json(String sleep_diificulty) {

        JSONObject json = new JSONObject();

        try {

            try {

                json.put("participant_id", AESEncryption.encrypt(user_id));
                json.put("research_info_id", AESEncryption.encrypt(research_info_id));
                json.put("sleep_data", AESEncryption.encrypt(makeJsonsleep(sleep_diificulty)));

                if(deletedSleepDataList!=null && deletedSleepDataList.size()>0){
                    json.put("prev_sleep_data",AESEncryption.encrypt(makeJsonPrevSleep()));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    //Todo parse food save response for json
    private void parse_sleep_save_response(String response, APIPasrsing apiPasrsing) {

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

    //Todo make json for sleep entry details
    public String makeJsonsleep(String sleep_diificulty) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("sleep_time", commonUsedmethods.parse_date_in_this_format(sleep_start_mills, "HH:mm"));  //Time is kept in 24 hours of format
            jsonObject.put("wake_up_time", commonUsedmethods.parse_date_in_this_format(sleep_end_mills, "HH:mm"));
            jsonObject.put("sleep_duration", sleep_duartion);
            jsonObject.put("measured_date", commonUsedmethods.parse_date_in_this_format(measured_date, "yyy-MM-dd"));

            if (sleep_diificulty.equalsIgnoreCase("")) {
                //if user have good sleep and there is no problem during sleep then bleow 2 parameter will be like this
                jsonObject.put("enough_sleep", "yes");
                jsonObject.put("sleep_problems", "");
            } else {
                //if user have not good sleep and there is  problems during sleep then bleow 2 parameter will be like this
                jsonObject.put("enough_sleep", "no");
                jsonObject.put("sleep_problems", sleep_diificulty);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    //Todo reset start and end time
    private void resetValues(int id) {

        try {
            //sleep_start_mills = 0;
            sleep_end_mills = 0;
            sleep_duartion = "";
            //sleep_start_time.setText(getResources().getString(R.string.Set_Date));
            sleep_end_time.setText(getResources().getString(R.string.Set_Date));
            selected_duration.setText("-");
            commonUsedmethods.show_Toast(LogSleepDataActivity.this, getResources().getString(id));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Todo make sleep difficulties string using separator
    private void generate_difficult_reason_string() {

        try {
            StringBuffer stringBuffer = new StringBuffer();
            if (add_problem_array != null && add_problem_array.size() > 0) {

                for (int i = 0; i < add_problem_array.size(); i++) {

                    if (i == 0) {

                        stringBuffer.append(add_problem_array.get(i));

                    } else {

                        stringBuffer.append("--separator--" + add_problem_array.get(i));
                    }
                }

            }

            if (other_difficulty.getText().toString().trim().length() > 0) {

                if (stringBuffer.toString().trim().length() == 0) {

                    stringBuffer.append(other_difficulty.getText().toString().trim());
                } else {

                    stringBuffer.append("--separator--" + other_difficulty.getText().toString().trim());
                }

            }

            original_request_time = System.currentTimeMillis();
            save_sleep_to_foodsleepex_table(sleep_start_mills, sleep_end_mills, sleep_duration_time, measured_date,stringBuffer.toString(),original_request_time);
            save_sleep_data_to_local(stringBuffer.toString(),original_request_time);

            call_sleep_api(stringBuffer.toString());
            onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo fetch and set previous 3 days sleep data from current date
    private void setPreviousSleepData() {

        try {
            previousSleepEntryArrayList = new ArrayList<>();
            previousSleepEntryArrayList = getPreviousday_sleep();
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(LogSleepDataActivity.this);
            previousdatelist.setLayoutManager(linearLayoutManager);
            sleep_previous_adapter = new PreviousdaySleepEntryAdapter(previousSleepEntryArrayList,
                    LogSleepDataActivity.this, new OnRecycleItemClicks() {
                @Override
                public void onClick(Object object) {

                    PreviousSleepEntry previousSleepEntry = (PreviousSleepEntry) object;

                    show_dialog_for_edit_sleep(previousSleepEntry);
                }
            });
            previousdatelist.setAdapter(sleep_previous_adapter);
            previousdatelist.setNestedScrollingEnabled(false);
            sleep_previous_adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo show dialog for timer option
    public void showList() {
        try {
            final Dialog list_dialog = new Dialog(LogSleepDataActivity.this,
                    R.style.DialogAnimationTag);
            list_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            list_dialog.setContentView(R.layout.sleep_start_timer_lay);
            list_dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
            ImageView now_image = list_dialog.findViewById(R.id.sleepnow);
            ImageView five_image = list_dialog.findViewById(R.id.sleepfive_mins);
            ImageView ten_image = list_dialog.findViewById(R.id.sleepten_mins);
            ImageView fifteen_image = list_dialog.findViewById(R.id.sleepfifteen_mins);
            ImageView thirty_image = (ImageView) list_dialog
                    .findViewById(R.id.sleepgreaterfifteen_mins);


            if (language.matches("français")) {

                now_image.setImageResource(R.mipmap.now_select_french);
            } else {
                now_image.setImageResource(R.mipmap.now_selected);
            }

            now_image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    list_dialog.dismiss();

                    if (runnable != null && handler != null) {
                        handler.removeCallbacks(runnable);
                    }

                    update_ui_for_sleep_now();
                    sleep_start_mills = System.currentTimeMillis();
                    tinylocalDb.put_data_in_shared(sharedPreferences_sleep_editor, "start_sleep_time", String.valueOf(sleep_start_mills));
                    showTimer.StartTimer(sleep_start_mills, 0);


                }
            });
            five_image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    list_dialog.dismiss();

                    if (runnable != null && handler != null) {
                        handler.removeCallbacks(runnable);
                    }

                    long starttime = System.currentTimeMillis() + 5 * 60 * 1000;
                    UpdateUi_for_sleep(starttime, 5 * 60 * 1000);
                    showTimer.StartTimer(starttime, 5 * 60 * 1000);


                }
            });
            ten_image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    list_dialog.dismiss();

                    if (runnable != null && handler != null) {
                        handler.removeCallbacks(runnable);
                    }
                    long starttime = System.currentTimeMillis() + 10 * 60 * 1000;
                    UpdateUi_for_sleep(starttime, 10 * 60 * 1000);
                    showTimer.StartTimer(starttime, 10 * 60 * 1000);


                }
            });
            fifteen_image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    list_dialog.dismiss();

                    if (runnable != null && handler != null) {
                        handler.removeCallbacks(runnable);
                    }
                    long starttime = System.currentTimeMillis() + 15 * 60 * 1000;
                    UpdateUi_for_sleep(starttime, 15 * 60 * 1000);
                    showTimer.StartTimer(starttime, 15 * 60 * 1000);


                }
            });
            thirty_image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    list_dialog.dismiss();

                    if (runnable != null && handler != null) {
                        handler.removeCallbacks(runnable);
                    }
                    long starttime = System.currentTimeMillis() + 30 * 60 * 1000;
                    UpdateUi_for_sleep(starttime, 30 * 60 * 1000);
                    showTimer.StartTimer(starttime, 30 * 60 * 1000);


                }
            });
            list_dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo update Ui after clicking any value from timer dialog
    private void UpdateUi_for_sleep(long startTime, long delaymills) {

        try {
            img_stop_sleep.setEnabled(false);
            img_start_sleep.setVisibility(View.VISIBLE);
            img_stop_sleep.setVisibility(View.INVISIBLE);
            log_sleep_title_layout.setVisibility(View.GONE);
            btn_save_sleep.setVisibility(View.INVISIBLE);
            btn_cancel.setVisibility(View.INVISIBLE);
            stop_timing_layout.setVisibility(View.INVISIBLE);
            sleep_pending_lay.setVisibility(View.VISIBLE);
            containor3.setVisibility(View.INVISIBLE);
            date_activity_main_layout.setVisibility(View.INVISIBLE);
            smile_happy.setEnabled(false);
            smile_sad.setEnabled(false);


            tinylocalDb.put_data_in_shared(sharedPreferences_sleep_editor, "start_sleep_time", String.valueOf(startTime));

            pending_sleep_timing_title.setText(getResources().getString(R.string.Sleep_will_start_at) + commonUsedmethods.parse_date_in_this_format(startTime, "hh:mm a"));

            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {

                    update_ui_for_sleep_now();
                }
            }, delaymills);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Todo update Ui after clicking now from timer dialog
    private void update_ui_for_sleep_now() {

        try {
            img_stop_sleep.setEnabled(true);
            img_start_sleep.setVisibility(View.GONE);
            img_stop_sleep.setVisibility(View.VISIBLE);
            log_sleep_title_layout.setVisibility(View.GONE);
            btn_save_sleep.setVisibility(View.INVISIBLE);
            btn_cancel.setVisibility(View.INVISIBLE);
            stop_timing_layout.setVisibility(View.VISIBLE);
            sleep_pending_lay.setVisibility(View.INVISIBLE);
            stop_sleep_timing_title
                    .setText(getResources().getString(R.string.You_have_been_sleeping_for));
            date_activity_main_layout.setVisibility(View.INVISIBLE);
            smile_happy.setEnabled(false);
            containor3.setVisibility(View.INVISIBLE);
            smile_sad.setEnabled(false);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //Todo  Showing the Timer
    public class ShowTimer {

        long timeInMilliseconds = 0L;
        long updatedTime = 0L;
        private long startTime = 0L, endTime = 0L;
        public Handler customHandler = new Handler();

        private Runnable updateTimerThread = new Runnable() {

            public void run() {

                endTime = System.currentTimeMillis();
                timeInMilliseconds = endTime - startTime;
                updatedTime = updatedTime + timeInMilliseconds;
                startTime = endTime;

                int secs = (int) (updatedTime / (1000) % 60);
                int minutes = (int) ((updatedTime / (1000 * 60)) % 60);
                int hours = (int) ((updatedTime / (1000 * 60 * 60)) % 24);


                stop_actual_timer.setText(String.format("%02d", hours) + " hrs:" +
                        String.format("%02d", minutes) + " mins:" + String.format("%02d", secs) + " secs");
                customHandler.postDelayed(this, 0);

               // tinylocalDb.put_data_in_shared(sharedPreferences_sleep_editor, "sleep_duration", String.valueOf(updatedTime));

            }

        };

        public void StartTimer(long time, long delaymills) {

            if (tinylocalDb.get_data_in_shared(sharedPreferences_sleep, "start_sleep_time").equalsIgnoreCase("")) {
                sleep_start_mills = time;
            } else {

                sleep_start_mills = Long.valueOf(
                        tinylocalDb.get_data_in_shared(sharedPreferences_sleep, "start_sleep_time"));
            }

            if (!tinylocalDb.get_data_in_shared(sharedPreferences_sleep, "is_paused").equalsIgnoreCase("")) {

                tinylocalDb.clear_key_from_shared_pref(sharedPreferences_sleep, "is_paused");
                tinylocalDb.put_data_in_shared(sharedPreferences_sleep_editor, "resume_sleep", String.valueOf(time));
            }

            startTime = time;//
            customHandler.postDelayed(updateTimerThread, delaymills);
        }

        public void StopTimer() {

            tinylocalDb.put_data_in_shared(sharedPreferences_sleep_editor, "sleep_duration", String.valueOf(updatedTime));
            tinylocalDb.put_data_in_shared(sharedPreferences_sleep_editor, "is_paused", "true");

            customHandler.removeCallbacks(updateTimerThread);
            set_start_end_time(sleep_start_mills, sleep_start_mills + updatedTime);
        }

        public void setdata_for_pause(long updatetime) {

            updatedTime = updatetime;

        }


    }

    //Todo set start and end timer after pause the sleep timer
    private void set_start_end_time(long sleep_start_mills1, long sleep_end_mills1) {

        try {
            sleep_start_mills = sleep_start_mills1;
            sleep_end_mills = sleep_end_mills1;

            sleep_start_time.setText(commonUsedmethods.parse_date_in_this_format(sleep_start_mills, "dd MMM yyyy hh:mm a"));
            sleep_end_time.setText(commonUsedmethods.parse_date_in_this_format(sleep_end_mills, "dd MMM yyyy hh:mm a"));

            getDuration_of_sleep(sleep_start_mills, sleep_end_mills);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo check for timer data and details if app will come from any other screen or from background
    private void check_sleep_start_pause() {

        try {
            long sleep_start_time = 0L, sleep_suration = 0L, current_time = System.currentTimeMillis();
            String is_paused = "";


            if (!tinylocalDb.get_data_in_shared(sharedPreferences_sleep, "start_sleep_time").equalsIgnoreCase("")) {

                sleep_start_time = Long.valueOf(tinylocalDb.get_data_in_shared(sharedPreferences_sleep, "start_sleep_time"));
            }

            if (!tinylocalDb.get_data_in_shared(sharedPreferences_sleep, "sleep_duration").equalsIgnoreCase("")) {

                sleep_suration = Long.valueOf(tinylocalDb.get_data_in_shared(sharedPreferences_sleep, "sleep_duration"));
            }

            if (!tinylocalDb.get_data_in_shared(sharedPreferences_sleep, "is_paused").equalsIgnoreCase("")) {

                is_paused = tinylocalDb.get_data_in_shared(sharedPreferences_sleep, "is_paused");
            }

            if (sleep_start_time != 0) {

                if (sleep_start_time > current_time) {

                    if (runnable != null && handler != null) {
                        handler.removeCallbacks(runnable);
                    }

                    showTimer.StartTimer(sleep_start_time, sleep_start_time - current_time);
                    UpdateUi_for_sleep(sleep_start_time, sleep_start_time - current_time);

                } else if (is_paused.equalsIgnoreCase("true")) {

                    set_start_end_time(sleep_start_time, sleep_start_time + sleep_suration);
                    showTimer.setdata_for_pause(sleep_suration);

                    int secs = (int) (sleep_suration / (1000) % 60);
                    int minutes = (int) ((sleep_suration / (1000 * 60)) % 60);
                    int hours = (int) ((sleep_suration / (1000 * 60 * 60)) % 24);


                    stop_actual_timer.setText(String.format("%02d", hours) + " hrs:" +
                            String.format("%02d", minutes) + " mins:" + String.format("%02d", secs) + " secs");

                    btn_cancel.setVisibility(View.VISIBLE);
                    btn_save_sleep.setVisibility(View.VISIBLE);
                    img_start_sleep.setVisibility(View.GONE);
                    img_stop_sleep.setVisibility(View.GONE);
                    img_resume_sleep.setVisibility(View.VISIBLE);

                    log_sleep_title_layout.setVisibility(View.GONE);
                    stop_timing_layout.setVisibility(View.VISIBLE);
                    stop_sleep_timing_title.setText("You slept for");
                    date_activity_main_layout.setVisibility(View.VISIBLE);

                    containor3.setVisibility(View.VISIBLE);
                    smile_happy.setEnabled(true);
                    smile_sad.setEnabled(true);

                } else {

                    if (runnable != null && handler != null) {
                        handler.removeCallbacks(runnable);
                    }
                    update_ui_for_sleep_now();

                    sleep_start_mills = System.currentTimeMillis();
                    if (tinylocalDb.get_data_in_shared(sharedPreferences_sleep, "resume_sleep").equalsIgnoreCase("")) {
                        showTimer.setdata_for_pause(sleep_start_mills - sleep_start_time);
                    }else{
                        showTimer.setdata_for_pause(sleep_suration+(sleep_start_mills - Long.valueOf(tinylocalDb.get_data_in_shared(sharedPreferences_sleep, "resume_sleep"))));
                    }
                    showTimer.StartTimer(sleep_start_mills, 0);


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Todo store sleep entry data in local storage
    private void save_sleep_data_to_local(String sleep_difficulty,long original_request_time) {

        try {
            String sleep_json = makeJsonsleep(sleep_difficulty);

            sleepEntries = new SleepEntries();
            sleepEntries.setSleep_details(sleep_json);
            sleepEntries.setTimestamp(original_request_time);
            if(deletedSleepDataList!=null && deletedSleepDataList.size()>0){
                sleepEntries.setPrevious_sleep_entries(makeJsonPrevSleep());
            }
            sleepEntries.setStatus("pending");
            databaseRepo.insertSleepEntries(sleepEntries);



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Todo save sleep data to FoodSleepExData table in local
    private void save_sleep_to_foodsleepex_table(long sleep_start_mills, long sleep_end_mills, long sleep_duration_time,
                                                 long measured_date,String sleep_difficulties,long original_request_time) {

        try {
            foodSleepExData = new FoodSleepExData();
            foodSleepExData.setTimestamp(commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy",
                    commonUsedmethods.parse_date_in_this_format(measured_date, "dd-MM-yyyy")));
            foodSleepExData.setSleep_actual_date(commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy",
                    commonUsedmethods.parse_date_in_this_format(sleep_start_mills, "dd-MM-yyyy")));
            foodSleepExData.setSleep_duration(sleep_duration_time);
            foodSleepExData.setSleep_start_time(sleep_start_mills);
            foodSleepExData.setSleep_end_time(sleep_end_mills);
            foodSleepExData.setType("sleep");
            foodSleepExData.setEnough_sleep(status_sleep_well);
            foodSleepExData.setSleep_difficulties(sleep_difficulties);
            foodSleepExData.setActual_log_request(original_request_time);
            databaseRepo.insertFoodSleepExdata(foodSleepExData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo fetch previous 3 days data from local dataabse
    private ArrayList<PreviousSleepEntry> getPreviousday_sleep() {

        try {

            arrayList = new ArrayList<>();
            previousSleepEntryArrayList = new ArrayList<>();

            for (int i = 1; i <= 3; i++) {

                Calendar current_cal = Calendar.getInstance();
                current_cal.add(Calendar.DATE, -i);

                arrayList.add(commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy",
                        commonUsedmethods.parse_date_in_this_format(current_cal.getTimeInMillis(), "dd-MM-yyy")));

            }
            Collections.reverse(arrayList);

            for (i = 0; i < arrayList.size(); i++) {

                databaseRepo.getSleep_for_date(arrayList.get(i), i, "sleep",
                        new OnDataFetched() {

                            @Override
                            public void data(Object object) {

                            }

                            @Override
                            public void data(Object object, int i) {

                                try {

                                    List<FoodSleepExData> foodSleepExData = (List<FoodSleepExData>) object;

                                    PreviousSleepEntry previousSleepEntry = new PreviousSleepEntry();
                                    previousSleepEntry.setSleepEntries(foodSleepExData);
                                    previousSleepEntry.setMeasuredate(arrayList.get(i));
                                    previousSleepEntry.setDate(commonUsedmethods.parse_date_in_this_format(arrayList.get(i), "EEE, MMM dd"));

                                    if (foodSleepExData != null && foodSleepExData.size() > 0) {

                                        Long sleep_duration = 0L;

                                        for (int j = 0; j < foodSleepExData.size(); j++) {

                                            sleep_duration = sleep_duration + foodSleepExData.get(j).getSleep_duration();

                                        }
                                        previousSleepEntry.setSleep_duration(sleep_duration);


                                    }
                                    previousSleepEntryArrayList.add(previousSleepEntry);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return previousSleepEntryArrayList;
    }

    //Todo show dialog for sleep edit
    private void show_dialog_for_edit_sleep(PreviousSleepEntry previousSleepEntry) {

        try {
            if (previousSleepEntry.getSleepEntries() != null && previousSleepEntry.getSleepEntries().size() > 0) {

                showDialogForallDateDetails(previousSleepEntry);
            } else {

                show_date_time_picker("start", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Todo shw dialog for sleep data details to edit
    public void showDialogForallDateDetails(PreviousSleepEntry previousSleepEntry) {

        try {
            List<FoodSleepExData> foodSleepExDataList = previousSleepEntry.getSleepEntries();
            final Dialog dialog = new Dialog(LogSleepDataActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.choosedatedialog);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent);
            dialog.setCancelable(true);


            final TextView txtcreated_at = dialog.findViewById(R.id.createddatetext);
            final RecyclerView recyclerView = dialog.findViewById(R.id.recycleview);


            txtcreated_at.setText(previousSleepEntry.getDate());

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(LogSleepDataActivity.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            SleepDataEditAdapter sleepDataEditAdapter = new SleepDataEditAdapter(foodSleepExDataList,
                    LogSleepDataActivity.this, new OnRecycleItemClicks() {
                @Override
                public void onClick(Object object) {

                    dialog.dismiss();
                    showEditSleepConfirmationDialog((FoodSleepExData) object);
                }
            });
            recyclerView.setAdapter(sleepDataEditAdapter);
            sleepDataEditAdapter.notifyDataSetChanged();

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Todo show dialog for edit confirmation
    public void showEditSleepConfirmationDialog(final FoodSleepExData foodSleepExData) {

        try {
            sleep_start_mills = foodSleepExData.getSleep_start_time();
            sleep_end_mills = foodSleepExData.getSleep_end_time();
            update_food_sleep_exdaat = foodSleepExData;

            final Dialog dialog = new Dialog(LogSleepDataActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.edit_cofirmation);


            TextView editable_date = dialog.findViewById(R.id.editable_date);
            TextView confirmButton = dialog.findViewById(R.id.update);
            TextView cancelButton = dialog.findViewById(R.id.cancel);
            editsleptfor = dialog.findViewById(R.id.sleptfor);
            editbedtime = dialog.findViewById(R.id.bedtime);
            editrisetime = dialog.findViewById(R.id.risetime);

            editable_date.setText(getResources().getString(R.string.Edit_sleep_on) +
                    commonUsedmethods.parse_date_in_this_format(foodSleepExData.getTimestamp(), "EEE, MMM dd"));
            editbedtime.setText(commonUsedmethods.parse_date_in_this_format(foodSleepExData.getSleep_start_time(), "hh:mma"));
            editrisetime.setText(commonUsedmethods.parse_date_in_this_format(foodSleepExData.getSleep_end_time(), "hh:mma"));


            if (foodSleepExData.getSleep_duration() != 0) {
                int minutes = (int) ((foodSleepExData.getSleep_duration() / (1000 * 60)) % 60);
                int hours = (int) ((foodSleepExData.getSleep_duration() / (1000 * 60 * 60)) % 24);


                editsleptfor.setText(getResources().getString(R.string.Slept_for) + String.format("%02d", hours) + ":" +
                        String.format("%02d", minutes) + " h");// 03h 01

            }


            editbedtime.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub

                    show_date_time_picker_for_edit_sleep("start", foodSleepExData.getSleep_actual_date(), sleep_start_mills, "Select your sleep time");
                }
            });
            editrisetime.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub

                    show_date_time_picker_for_edit_sleep("end", foodSleepExData.getSleep_actual_date(), sleep_end_mills, "Woke up at");
                }
            });


            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();

                    getdata_for_naptime(sleep_start_mills, sleep_end_mills, "update");

                }
            });


            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    //Todo show date time picker for edit sleep
    private void show_date_time_picker_for_edit_sleep(final String from, final Long actual_date, Long sllep_time, String title) {

        try {

            if (language.matches("français")) {
                dateandtime = Calendar.getInstance(Locale.FRENCH);
            } else {
                dateandtime = Calendar.getInstance(Locale.US);
            }


            dateandtime.setTimeInMillis(sllep_time);

            DatePickerSetPreviousTime dp = new DatePickerSetPreviousTime(LogSleepDataActivity.this, title,
                    dateandtime, new DatePickerSetPreviousTime.DatePickerListner() {

                @Override
                public void OnDoneButton(Dialog datedialog, Calendar c) {


                    do_calculation_withslected_time_edit(c, from, actual_date, datedialog);

                }

                @Override
                public void OnCancelButton(Dialog datedialog) {
                    // TODO Auto-generated method stub

                    datedialog.dismiss();


                }
            });
            dp.setCancelable(false);
            dp.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Todo calculate duratin of edit sleep
    private void getDuration_of_sleep_edit(long sleep_start_mills, long sleep_end_mills, Dialog dp) {

        try {
            sleep_duration_time = sleep_end_mills - sleep_start_mills;

            if (sleep_duration_time > 24 * 60 * 60 * 1000) {

                resetValues(R.string.you_entered_is_more);

            } else if (sleep_duration_time < 60 * 1000) {

                resetValues(R.string.you_entered_is_less);
            } else {

                String time = commonUsedmethods.parse_date_in_this_format(sleep_start_mills, "hh:mma");
                editbedtime.setText(time);
                time = commonUsedmethods.parse_date_in_this_format(sleep_end_mills, "hh:mma");
                editrisetime.setText(time);

                sleep_duartion = String.format(
                        "%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(sleep_duration_time),
                        TimeUnit.MILLISECONDS.toMinutes(sleep_duration_time)
                                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                                .toHours(sleep_duration_time)));

                int minutes = (int) ((sleep_duration_time / (1000 * 60)) % 60);
                int hours = (int) ((sleep_duration_time / (1000 * 60 * 60)) % 24);

                editsleptfor.setText(getResources().getString(R.string.Slept_for) + String.format("%02d", hours) + ":" +
                        String.format("%02d", minutes) + " h");

                dp.dismiss();


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Todo check and calulate after selection from calender for edit
    private void do_calculation_withslected_time_edit(Calendar c, String from, Long actual_date, Dialog dp) {

        try {

            if (from.equalsIgnoreCase("start")) {

                actual_date = update_food_sleep_exdaat.getTimestamp();

                String start_sleep_date = commonUsedmethods.parse_date_in_this_format(actual_date, "dd-MM-yyyy");
                String end_sleep_date = commonUsedmethods.parse_date_in_this_format(actual_date+(24*60*60*1000)
                        , "dd-MM-yyyy");

                Long start_value = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyy HH:mm",start_sleep_date+" "+
                        "04:00");
                Long end_value = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyy HH:mm",end_sleep_date+" "+
                        "04:00");

                if (start_value<=c.getTimeInMillis() && c.getTimeInMillis()<=end_value) {

                    sleep_start_mills = c.getTimeInMillis();
                    measured_date = update_food_sleep_exdaat.getTimestamp();

                } else {
                    commonUsedmethods.show_Toast(LogSleepDataActivity.this, getResources().getString(R.string.count_as_sleep_for_this_day));

                }

            } else if (from.equalsIgnoreCase("end")) {

                String selected_date = commonUsedmethods.parse_date_in_this_format(sleep_start_mills, "dd MMM yyyy");

                if (selected_date.contains("Dec")) {

                    String year = commonUsedmethods.parse_date_in_this_format(sleep_start_mills, "yyyy");

                    c.set(Calendar.YEAR, Integer.valueOf(year) + 1);
                }

                sleep_end_mills = c.getTimeInMillis();

            }


            if (sleep_start_mills != 0 && sleep_end_mills != 0) {

                if (sleep_start_mills < sleep_end_mills) {
                    getDuration_of_sleep_edit(sleep_start_mills, sleep_end_mills, dp);
                } else {

                    resetValues(R.string.End_time_Should_be);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo update sleep data to FoodSleepEx table
    private void update_sleep_to_foodsleepex_table(FoodSleepExData foodSleepExDatas, long sleep_start_mills,
                                                   long sleep_end_mills, long sleep_duration_time, boolean toUpdate) {

        try {
            foodSleepExData = foodSleepExDatas;
            foodSleepExData.setSleep_duration(sleep_duration_time);
            foodSleepExData.setSleep_start_time(sleep_start_mills);
            foodSleepExData.setSleep_end_time(sleep_end_mills);
            foodSleepExData.setServer_status("pending");
            foodSleepExData.setActual_log_request(original_request_time);

            if (toUpdate) {

                databaseRepo.updateFoodSleepEx(foodSleepExData);
            } else {
                databaseRepo.insertFoodSleepExdata(foodSleepExData);
            }


            previousSleepEntryArrayList = getPreviousday_sleep();
            sleep_previous_adapter.notifyDataSetChanged();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo fetch all sleep for a date to check naptime
    private void getdata_for_naptime(final Long sleep_start_time_, final Long sleep_end_time_, final String from) {


        try {
            final ArrayList<FoodSleepExData> arrayList = new ArrayList<>();
            Long sleep_date = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyy",
                    commonUsedmethods.parse_date_in_this_format(sleep_start_time_, "dd-MM-yyyy"));

            String sleep_time = commonUsedmethods.parse_date_in_this_format(sleep_start_time_, "hh mm a");
            String[] arr = sleep_time.split(" ");

            if (from.equalsIgnoreCase("update")) {

                sleep_date = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyy",
                        commonUsedmethods.parse_date_in_this_format(update_food_sleep_exdaat.getSleep_actual_date(), "dd-MM-yyyy"));
            } else {

                if (arr[2].equalsIgnoreCase("AM")) {

                    next_slepp_date = sleep_date - (24 * 60 * 60 * 1000);

                } else {
                    next_slepp_date = sleep_date + (24 * 60 * 60 * 1000);
                }

            }


            databaseRepo.getSleeps(sleep_date, "sleep", new OnDataFetched() {
                @Override
                public void data(Object object, int i) {

                }

                @Override
                public void data(Object object) {

                    final List<FoodSleepExData> foodSleepExDataList = (List<FoodSleepExData>) object;


                    if (next_slepp_date != 0) {

                        databaseRepo.getSleeps(next_slepp_date, "sleep", new OnDataFetched() {
                            @Override
                            public void data(Object object, int i) {

                            }

                            @Override
                            public void data(Object object) {

                                final List<FoodSleepExData> foodSleepExDataList1 = (List<FoodSleepExData>) object;

                                foodSleepExDataList.addAll(foodSleepExDataList1);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        check_nap_data(foodSleepExDataList, sleep_start_time_, sleep_end_time_, from);
                                    }
                                });

                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                check_nap_data(foodSleepExDataList, sleep_start_time_, sleep_end_time_, from);
                            }
                        });
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo to check naptime
    private void check_nap_data(List<FoodSleepExData> foodSleepExDataList1,
                                final Long sleep_start_time_, final Long sleep_end_time_, String from) {

        try {

            final ArrayList<FoodSleepExData> arrayList = new ArrayList<>();
            List<FoodSleepExData> foodSleepExDataList = foodSleepExDataList1;
            deletedSleepDataList = new ArrayList<>();

            for (int i = 0; i < foodSleepExDataList.size(); i++) {

                FoodSleepExData foodSleepExData = foodSleepExDataList.get(i);

                if (sleep_start_time_ <= foodSleepExData.getSleep_start_time()) {

                    if (sleep_end_time_ > foodSleepExData.getSleep_start_time()) {

                        arrayList.add(foodSleepExData);
                    }
                }

                if (sleep_start_time_ > foodSleepExData.getSleep_start_time() && sleep_start_time_ < foodSleepExData.getSleep_end_time()) {

                    arrayList.add(foodSleepExData);

                }
            }

            if (arrayList.size() > 0) {

                deletedSleepDataList.addAll(arrayList);

                if (from != null && from.equalsIgnoreCase("update")) {

                    for (int i = 0; i < arrayList.size(); i++) {

                        if (update_food_sleep_exdaat.getId() == arrayList.get(i).getId()) {

                            arrayList.remove(arrayList.get(i));
                        }
                    }

                    if (arrayList.size() > 0) {

                        deleteConfirmationForEdit(arrayList, from);

                    } else {

                        original_request_time = System.currentTimeMillis();
                        databaseRepo.deleteSleep(update_food_sleep_exdaat.getActual_log_request());
                        update_sleep_to_foodsleepex_table(update_food_sleep_exdaat,
                                sleep_start_mills, sleep_end_mills, sleep_duration_time, true);
                        call_sleep_api_for_update();
                    }

                } else {

                    deleteConfirmationForEdit(arrayList, from);
                }

            } else {

                if (from != null && !from.equalsIgnoreCase("update")) {

                    call_sleep_api();

                } else {

                    original_request_time = System.currentTimeMillis();
                    databaseRepo.deleteSleep(update_food_sleep_exdaat.getActual_log_request());
                    update_sleep_to_foodsleepex_table(update_food_sleep_exdaat,
                            sleep_start_mills, sleep_end_mills, sleep_duration_time, true);
                    call_sleep_api_for_update();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo show list of naptime for replcement
    public void deleteConfirmationForEdit(final ArrayList<FoodSleepExData> arrayList, final String from) {

        try {
            final Dialog dialog = new Dialog(LogSleepDataActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.deleteddetailsdialog);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent);
            dialog.setCancelable(false);
            dialog.show();

            TextView txtMessage = dialog.findViewById(R.id.messagefordelete);
            RecyclerView recyclerView = dialog.findViewById(R.id.listdeletedates);
            Button yes_btn = dialog.findViewById(R.id.yes_btn);
            Button no_button = dialog.findViewById(R.id.no_btn);


            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(LogSleepDataActivity.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            NapSleepDetailAdapter napSleepDetailAdapter = new NapSleepDetailAdapter(arrayList,
                    LogSleepDataActivity.this, new OnRecycleItemClicks() {
                @Override
                public void onClick(Object object) {

                    dialog.dismiss();
                    showEditSleepConfirmationDialog((FoodSleepExData) object);
                }
            });
            recyclerView.setAdapter(napSleepDetailAdapter);
            napSleepDetailAdapter.notifyDataSetChanged();

            yes_btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub

                    dialog.dismiss();
                    for (int i = 0; i < arrayList.size(); i++) {

                        databaseRepo.deleteFoodSleepEx(arrayList.get(i));
                        databaseRepo.deleteSleep(arrayList.get(i).getActual_log_request());
                    }

                    if (!from.equalsIgnoreCase("update")) {
                        call_sleep_api();
                    } else {

                        original_request_time = System.currentTimeMillis();

                        databaseRepo.deleteSleep(update_food_sleep_exdaat.getActual_log_request());
                        update_sleep_to_foodsleepex_table(update_food_sleep_exdaat,
                                sleep_start_mills, sleep_end_mills, sleep_duration_time, true);
                        call_sleep_api_for_update();

                    }


                }
            });
            no_button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo common function to call sleep API
    private void call_sleep_api() {

        try {
            if (!check_difficulty_in_sleep.isChecked() && !check_woke_up_early.isChecked()
                    && !check_insufficient_sleep.isChecked()
                    && other_difficulty.getText().length() == 0
                    && status_sleep_well == false) {

                commonUsedmethods.show_Toast(LogSleepDataActivity.this, getResources().getString(R.string.Please_choose_sleep));

            } else {

                if (status_sleep_well) {

                    original_request_time = System.currentTimeMillis();
                    save_sleep_to_foodsleepex_table(sleep_start_mills, sleep_end_mills, sleep_duration_time, measured_date,"",original_request_time);
                    save_sleep_data_to_local("",original_request_time);
                    call_sleep_api("");
                    onBackPressed();

                } else {

                    generate_difficult_reason_string();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo call sleep API for update sleep data
    private void call_sleep_api_for_update() {

        try {

            save_sleep_data_to_local(update_food_sleep_exdaat.getSleep_difficulties(),original_request_time);
            call_sleep_api(update_food_sleep_exdaat.getSleep_difficulties());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo make json of sleep which will be repalaced
    public String makeJsonPrevSleep(){

        JSONArray jsonArray = new JSONArray();

        try{

            for(int i =0;i<deletedSleepDataList.size();i++) {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("sleep_time", commonUsedmethods.parse_date_in_this_format(deletedSleepDataList.get(i).getSleep_start_time(),"HH:mm"));
                jsonObject.put("wake_up_time", commonUsedmethods.parse_date_in_this_format(deletedSleepDataList.get(i).getSleep_end_time(),"HH:mm"));
                jsonObject.put("measured_date", commonUsedmethods.parse_date_in_this_format(deletedSleepDataList.get(i).getTimestamp(),"yyyy-MM-dd"));

                jsonArray.put(jsonObject);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return jsonArray.toString();
    }

    @Override
    protected void onDestroy() {

        showTimer.customHandler.removeCallbacks(showTimer.updateTimerThread);
        super.onDestroy();
    }
}
