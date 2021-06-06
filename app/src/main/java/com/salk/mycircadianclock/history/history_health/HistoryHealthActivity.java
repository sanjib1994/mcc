package com.salk.mycircadianclock.history.history_health;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
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
import com.salk.mycircadianclock.api.ApiConfig;
import com.salk.mycircadianclock.history.history_intake.HistoryIntakeActivity;
import com.salk.mycircadianclock.history.history_intake.WeekListAdapter;
import com.salk.mycircadianclock.interfaces.OnRecycleItemClicks;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryHealthActivity extends AppCompatActivity {

    //Todo declaration of XML views
    private RelativeLayout tabbar, rel_main;
    private ImageView image_list,image_chart;
    private Button btn_days,btn_weeks,btn_month,btn_intake,btn_activity,vital_title,lab_title;
    private RecyclerView recycle_health_list;
    private TextView jump_to_date,text_no_data_found,text_unit;
    private Spinner health_list;
    private View view_week,view_month;

    //Todo declaration of common classes and variables
    private String user_id = "",research_info_id = "",auth_token = "",language = "",baseline_start_date = "",start_date = "",
            end_date = "",call_type = "chat",filter_type = "day",health_type = "";
    private SharedPreferences sharedPreferences;
    private TinylocalDb tinylocalDb;
    private CommonUsedmethods commonUsedmethods;
    private ArrayList<String> allWeekDates_array = new ArrayList<>();
    private ArrayList<HealthListModel> healthListModelArrayList = new ArrayList<>();
    private Locale locale;
    private KProgressHUD kProgressHUD;
    private ConnectionDetector connectionDetector;
    private WebView chart_view;
    private HistoryHealthAdapter historyHealthListAdapter;
    private String[] data_type_items;
    private String list_json = "{\"message\":\n" +
            "  {\n" +
            "  \"data\":[\n" +
            "  {\n" +
            "    \"date\":\"2020/04/14\",\n" +
            "    \"value\":\"100 Lb\",\n" +
            "    \"time\":\"04:22 PM\"\n" +
            "    \n" +
            "  },\n" +
            "   {\n" +
            "    \"date\":\"2020/04/13\",\n" +
            "    \"value\":\"100 Lb\",\n" +
            "    \"time\":\"04:22 PM\"\n" +
            "    \n" +
            "  },\n" +
            "   {\n" +
            "    \"date\":\"2020/04/12\",\n" +
            "    \"value\":\"100 Lb\",\n" +
            "    \"time\":\"04:22 PM\"\n" +
            "    \n" +
            "  },\n" +
            "   {\n" +
            "    \"date\":\"2020/04/11\",\n" +
            "    \"value\":\"100 Lb\",\n" +
            "    \"time\":\"04:22 PM\"\n" +
            "    \n" +
            "  },\n" +
            "   {\n" +
            "    \"date\":\"2020/04/10\",\n" +
            "    \"value\":\"100 Lb\",\n" +
            "    \"time\":\"04:22 PM\"\n" +
            "    \n" +
            "  }\n" +
            "]\n" +
            "}\n" +
            "}";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            //to make screen full screen
            new CommonUsedmethods().makeActivityFullscreen(HistoryHealthActivity.this);

            setContentView(R.layout.activity_history_health);

            init();

            intializacommonclass();

            click_function();

            weekViewForNewUser();

            setdropdown();

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    //Todo methods to intialize XML views
    private void init(){

        try{

            tabbar = findViewById(R.id.tab_bar);
            rel_main = findViewById(R.id.main);
            image_list = findViewById(R.id.list_icon);
            image_chart = findViewById(R.id.chat_icon);
            btn_days = findViewById(R.id.days_title);
            btn_weeks = findViewById(R.id.weeks_title);
            btn_month = findViewById(R.id.months_title);
            jump_to_date = findViewById(R.id.jump_date);
            chart_view = findViewById(R.id.chart_view);
            text_no_data_found = findViewById(R.id.no_data);
            btn_intake = findViewById(R.id.intake_title);
            btn_activity = findViewById(R.id.activity_title);
            recycle_health_list = findViewById(R.id.recycle_health_list);
            health_list = findViewById(R.id.health_list);
            text_unit = findViewById(R.id.type);
            vital_title = findViewById(R.id.vital_title);
            lab_title = findViewById(R.id.lab_title);
            view_week = findViewById(R.id.view_1);
            view_month = findViewById(R.id.view_2);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo add click functions
    private void click_function() {

        try {

            btn_activity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HistoryHealthActivity.this, HistoryHealthActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
            });

            btn_intake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HistoryHealthActivity.this, HistoryIntakeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
            });

            jump_to_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showDialog();
                }
            });

            image_chart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    intializewebviw();
                    update_ui_for_history_chart();
                    //filter_type = "day";
                    //update_ui_for_day_week_month_click(btn_days);

                    call_type = "chat";
                    filter_type = "day";
                    set_initial_start_end_date();


                }
            });

            image_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    update_ui_for_history_list();
                    //filter_type = "day";
                    //update_ui_for_day_week_month_click(btn_days);

                    call_type = "list";
                    set_initial_start_end_date();
                    call_health_list_api();

                }
            });

            btn_days.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    filter_type = "day";
                    update_ui_for_day_week_month_click(btn_days);
                    set_initial_start_end_date();

                    if (call_type.equalsIgnoreCase("chat")) {

                        chart_view.clearHistory();
                        intializewebviw();


                    } else {

                        healthListModelArrayList.clear();
                        if (historyHealthListAdapter != null) {
                            historyHealthListAdapter.notifyDataSetChanged();
                        }
                        call_health_list_api();
                    }
                }
            });

            btn_weeks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    filter_type = "week";
                    update_ui_for_day_week_month_click(btn_weeks);
                    set_initial_start_end_date();

                    if (call_type.equalsIgnoreCase("chat")) {

                        chart_view.clearHistory();
                        intializewebviw();

                    } else {

                        healthListModelArrayList.clear();
                        if (historyHealthListAdapter != null) {
                            historyHealthListAdapter.notifyDataSetChanged();
                        }
                        call_health_list_api();
                    }
                }
            });

            btn_month.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    filter_type = "month";
                    update_ui_for_day_week_month_click(btn_month);
                    set_initial_start_end_date();

                    if (call_type.equalsIgnoreCase("chat")) {
                        chart_view.clearHistory();
                        intializewebviw();


                    } else {

                        healthListModelArrayList.clear();
                        if (historyHealthListAdapter != null) {
                            historyHealthListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });

            vital_title.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    vital_title.setBackgroundResource(R.color.chart_filter_bg);
                    lab_title.setBackgroundResource(R.color.white);
                    vital_title.setTextColor(Color.parseColor("#FFFFFF"));
                    lab_title.setTextColor(Color.parseColor("#000000"));

                    data_type_items = new String[]{getResources().getString(R.string.WEIGHT), getResources().getString(R.string.HEIGHT),
                            getResources().getString(R.string.BMI), getResources().getString(R.string.HEART_RATE),
                            getResources().getString(R.string.BLOOD_PRESSURE), getResources().getString(R.string.TEMPERATURE),
                            getResources().getString(R.string.BOWEL_MOVEMENT)};

                    health_type = getResources().getString(R.string.WEIGHT).toLowerCase();
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            HistoryHealthActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            data_type_items);
                    health_list.setAdapter(adapter);
                }
            });
            lab_title.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    vital_title.setBackgroundResource(R.color.white);
                    lab_title.setBackgroundResource(R.color.chart_filter_bg);
                    vital_title.setTextColor(Color.parseColor("#000000"));
                    lab_title.setTextColor(Color.parseColor("#FFFFFF"));

                    data_type_items = new String[]{getResources().getString(R.string.BLOOD_GLUCOSE), getResources().getString(R.string.CHOLESTEROL),
                            getResources().getString(R.string.TRIGLYCERIDES), getResources().getString(R.string.HEMOGLOBIN_A1C),
                            getResources().getString(R.string.FIBRINOGEN), getResources().getString(R.string.C_REACTIVE_PROTEIN),
                            getResources().getString(R.string.HOMOCYSTEINE), getResources().getString(R.string.KETONES)};

                    health_type = getResources().getString(R.string.BLOOD_GLUCOSE).toLowerCase();
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            HistoryHealthActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            data_type_items);
                    health_list.setAdapter(adapter);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo to initialize common classes
    private void intializacommonclass(){
        try{

            new TabbarClick().click(HistoryHealthActivity.this, tabbar, rel_main, "history");
            tinylocalDb = new TinylocalDb();
            commonUsedmethods = new CommonUsedmethods();
            connectionDetector = new ConnectionDetector(HistoryHealthActivity.this);
            kProgressHUD = commonUsedmethods.show_progerssDialog(HistoryHealthActivity.this,"Loading....",false);

            sharedPreferences = tinylocalDb.get_shared_pref(HistoryHealthActivity.this);
            user_id = tinylocalDb.get_data_in_shared(sharedPreferences,"user_id");
            research_info_id = tinylocalDb.get_data_in_shared(sharedPreferences,"research_info_id");
            auth_token = tinylocalDb.get_data_in_shared(sharedPreferences,"auth_token");
            baseline_start_date = tinylocalDb.get_data_in_shared(sharedPreferences,"baseline_start");
            baseline_start_date = commonUsedmethods.parse_date_in_this_format(Long.valueOf(baseline_start_date),
                    "yyyy-MM-dd");
            end_date = commonUsedmethods.parse_date_in_this_format(System.currentTimeMillis(),"yyyy-MM-dd");
            start_date = commonUsedmethods.parse_date_in_this_format((System.currentTimeMillis()-7*24*60*60*1000),"yyyy-MM-dd");
            language = Locale.getDefault().getDisplayLanguage();

            if(language.matches("français")){
                locale = Locale.FRENCH;
            }else{
                locale = Locale.US;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo update UI for day weekk or month button click
    private void update_ui_for_day_week_month_click(Button button){

        try {
            btn_days.setBackground(null);
            btn_month.setBackground(null);
            btn_weeks.setBackground(null);

            btn_days.setTextColor(getResources().getColor(R.color.black));
            btn_month.setTextColor(getResources().getColor(R.color.black));
            btn_weeks.setTextColor(getResources().getColor(R.color.black));

            button.setBackgroundColor(getResources().getColor(R.color.chart_filter_bg));
            button.setTextColor(getResources().getColor(R.color.white));

            jump_to_date.setText(R.string.Jump_to_Date);
            text_no_data_found.setVisibility(View.GONE);
        }catch (Exception e){
            e.printStackTrace();

        }
    }

    //Todo set drop sown for vital or lab type helath issues
    private void setdropdown(){

        try {
            data_type_items = new String[]{getResources().getString(R.string.WEIGHT), getResources().getString(R.string.HEIGHT),
                    getResources().getString(R.string.BMI), getResources().getString(R.string.HEART_RATE),
                    getResources().getString(R.string.BLOOD_PRESSURE), getResources().getString(R.string.TEMPERATURE),
                    getResources().getString(R.string.BOWEL_MOVEMENT)};

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(HistoryHealthActivity.this,
                    android.R.layout.simple_spinner_dropdown_item, data_type_items);
            health_list.setAdapter(adapter);

            health_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int postion, long arg3) {
                    // TODO Auto-generated method stub
                    health_type = data_type_items[postion].toLowerCase();


                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //Todo to get week list array
    public void weekViewForNewUser() {
        try {
            allWeekDates_array = new ArrayList<String>();

            Calendar current_cal = Calendar.getInstance();
            Date currentDate = current_cal.getTime();

            Calendar baseline_date = Calendar.getInstance();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", locale);
            Date base_date = null;

            try {
                base_date = df.parse(baseline_start_date);

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Calendar start_date = Calendar.getInstance();
            start_date.setTime(base_date);

            while (start_date.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {

                start_date.add(Calendar.DATE, -1);
            }
            Date startDate = start_date.getTime();

            baseline_date.setTime(base_date);

            Date baselineDate = baseline_date.getTime();

            while (baseline_date.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {

                baseline_date.add(Calendar.DATE, 1);
            }
            baselineDate = baseline_date.getTime();
            int result = currentDate.compareTo(baselineDate);
            while (currentDate.after(baselineDate)) {

                Calendar end_date = Calendar.getInstance();
                end_date.setTime(baseline_date.getTime());
                end_date.add(Calendar.DATE, -1);

                Date endDate = end_date.getTime();
                startDate = start_date.getTime();

                SimpleDateFormat format = new SimpleDateFormat("dd - MMM - yyyy", locale);
                String startAt = format.format(startDate);
                String endAt = format.format(endDate);
                allWeekDates_array.add(startAt + " to " + endAt);

                start_date.setTime(baseline_date.getTime());

                end_date.add(Calendar.DATE, 8);

                baseline_date = end_date;

                baselineDate = baseline_date.getTime();
                result = currentDate.compareTo(baselineDate);
            }
            Calendar end_date = Calendar.getInstance();
            end_date.setTime(baseline_date.getTime());
            end_date.add(Calendar.DATE, -1);

            Date endDate = end_date.getTime();
            startDate = start_date.getTime();

            SimpleDateFormat format = new SimpleDateFormat("dd - MMM - yyyy", locale);
            String startAt = format.format(startDate);
            String endAt = format.format(endDate);
            allWeekDates_array.add(startAt + " to " + endAt);

            start_date.setTime(baseline_date.getTime());

            end_date.add(Calendar.DATE, 8);

            baseline_date = end_date;

            baselineDate = baseline_date.getTime();
            result = currentDate.compareTo(baselineDate);


            Collections.reverse(allWeekDates_array);

            Log.d("output", allWeekDates_array.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Todo to show dialog for jump to date
    public void showDialog() {

        try {

            final Dialog listDialog = new Dialog(HistoryHealthActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            listDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            listDialog.setCancelable(false);
            listDialog.setContentView(R.layout.dateholder);
            listDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));

            RecyclerView recyclerView = listDialog.findViewById(R.id.listView1);
            Button cancel = listDialog.findViewById(R.id.cancelbuttonfordata);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HistoryHealthActivity.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            WeekListAdapter weekListAdapter = new WeekListAdapter(allWeekDates_array,
                    HistoryHealthActivity.this, new OnRecycleItemClicks() {
                @Override
                public void onClick(Object object) {

                    listDialog.dismiss();

                    filter_type = "jump_date";
                    check_for_jumpto_date((String)object);

                }
            });
            recyclerView.setAdapter(weekListAdapter);
            weekListAdapter.notifyDataSetChanged();

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listDialog.dismiss();
                }
            });

            listDialog.show();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    //Todo to call API after clicked on week list arry from jump to date
    private void check_for_jumpto_date(String date){

        try{

            if(date.contains("to")){

                String[] dates = date.split(" to ");

                start_date = commonUsedmethods.parse_date_in_this_format(dates[0],"yyyy-MM-dd","dd - MMM - yyyy");
                end_date = commonUsedmethods.parse_date_in_this_format(dates[1],"yyyy-MM-dd","dd - MMM - yyyy");

                jump_to_date.setText(commonUsedmethods.parse_date_in_this_format(dates[0],"dd/MM","dd - MMM - yyyy")+" to "+
                        commonUsedmethods.parse_date_in_this_format(dates[1],"dd/MM","dd - MMM - yyyy"));

                if(call_type.equalsIgnoreCase("list")){

                    healthListModelArrayList.clear();
                    if (historyHealthListAdapter != null) {
                        historyHealthListAdapter.notifyDataSetChanged();
                    }
                    call_health_list_api();
                }else if(call_type.equalsIgnoreCase("chat")){

                    chart_view.clearHistory();

                }
            }

        }catch (Exception e){

            e.printStackTrace();
        }
    }

    //Todo update UI for chart icon click
    private void update_ui_for_history_chart(){

        try {

            image_chart.setImageResource(R.mipmap.graph_selected);
            image_list.setImageResource(R.mipmap.list_unselected);
            btn_weeks.setVisibility(View.GONE);
            btn_month.setVisibility(View.GONE);
            view_week.setVisibility(View.GONE);
            view_month.setVisibility(View.GONE);

            recycle_health_list.setVisibility(View.GONE);
            chart_view.setVisibility(View.VISIBLE);

            jump_to_date.setText(R.string.Jump_to_Date);
            text_no_data_found.setVisibility(View.GONE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo update UI for list icon click
    private void update_ui_for_history_list(){

        try {

            image_chart.setImageResource(R.mipmap.graph_unselected);
            image_list.setImageResource(R.mipmap.list_selected);
            btn_weeks.setVisibility(View.VISIBLE);
            btn_month.setVisibility(View.VISIBLE);
            view_week.setVisibility(View.VISIBLE);
            view_month.setVisibility(View.VISIBLE);

            chart_view.setVisibility(View.GONE);
            recycle_health_list.setVisibility(View.VISIBLE);

            if(historyHealthListAdapter!=null){
                healthListModelArrayList.clear();
                historyHealthListAdapter.notifyDataSetChanged();
            }

            jump_to_date.setText(R.string.Jump_to_Date);
            text_no_data_found.setVisibility(View.GONE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo set intial start and end date for day,week,month
    private void set_initial_start_end_date(){

        try {
            Calendar calendar = Calendar.getInstance();

            if (filter_type.equalsIgnoreCase("day")) {

                end_date = commonUsedmethods.parse_date_in_this_format(System.currentTimeMillis(), "yyyy-MM-dd");
                start_date = commonUsedmethods.parse_date_in_this_format((System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000), "yyyy-MM-dd");
            } else if (filter_type.equalsIgnoreCase("week")) {

                end_date = commonUsedmethods.parse_date_in_this_format(System.currentTimeMillis(), "yyyy-MM-dd");

                calendar.add(Calendar.WEEK_OF_MONTH, -10);
                start_date = commonUsedmethods.parse_date_in_this_format(calendar.getTimeInMillis(), "yyyy-MM-dd");

            } else if (filter_type.equalsIgnoreCase("month")) {

                end_date = commonUsedmethods.parse_date_in_this_format(System.currentTimeMillis(), "yyyy-MM-dd");

                calendar.add(Calendar.YEAR, -1);
                start_date = commonUsedmethods.parse_date_in_this_format(calendar.getTimeInMillis(), "yyyy-MM-dd");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo intialize webview settings
    private void intializewebviw(){

        try {
            chart_view.getSettings().setJavaScriptEnabled(true);
            // enable zoom controls
            chart_view.getSettings().setBuiltInZoomControls(true);
            // these settings speed up page load into the webview
            chart_view.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            chart_view.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            chart_view.requestFocus(View.FOCUS_DOWN);
            // Set whether the DOM storage API is enabled.
            chart_view.getSettings().setDomStorageEnabled(true);
            chart_view.getSettings().setPluginState(WebSettings.PluginState.ON);
            chart_view.getSettings().setAllowFileAccess(true);
            chart_view.getSettings().setUseWideViewPort(true);
            chart_view.getSettings().setLoadWithOverviewMode(true);
            chart_view.setInitialScale(1);
            // chart_view.setInitialScale(getScale());
            chart_view.setWebChromeClient(new WebChromeClient());
            // these settings speed up page load into the webview
            chart_view.requestFocus(View.FOCUS_DOWN);
            chart_view.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            chart_view.getSettings().setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
            // chart_view.setScrollbarFadingEnabled(false);
            chart_view.setScrollbarFadingEnabled(true);
            chart_view.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            chart_view.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            if (Build.VERSION.SDK_INT >= 19) {
                chart_view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                chart_view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    
    /** call health list API*/
    private void call_health_list_api(){

        try {

            if(connectionDetector.isConnectingToInternet()) {
                if (kProgressHUD != null && !kProgressHUD.isShowing()) {
                    kProgressHUD.show();
                }

                String url = ApiConfig.ACTIVITY_LIST_API;
                JSONObject jsonObject = make_health_list_request();

//                new APIManager().Apicall(auth_token, url, jsonObject, new Apicallback() {
//                    @Override
//                    public void success(int code, String value) {
//
//                        if (code == 200) {
//
//                            //parse response
//                            parse_activity_list_response(value, new ApiParse() {
//
//                                @Override
//                                public void onSuccess(final Object object,Object object1) {
//
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//
//                                            if (kProgressHUD != null && kProgressHUD.isShowing()) {
//                                                kProgressHUD.dismiss();
//                                            }
//
//                                            show_activity_list();
//                                        }
//                                    });
//
//
//                                }
//
//                                @Override
//                                public void onFailure() {
//
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//
//                                            if (kProgressHUD != null && kProgressHUD.isShowing()) {
//                                                kProgressHUD.dismiss();
//                                            }
//
//                                            Toast.makeText(HistoryHealthActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
//                                        }
//                                    });
//
//
//                                }
//                            });
//                        } else {
//
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    if (kProgressHUD != null && kProgressHUD.isShowing()) {
//                                        kProgressHUD.dismiss();
//                                    }
//
//                                    Toast.makeText(HistoryHealthActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
//                                }
//                            });
//
//                        }
//                    }
//
//                    @Override
//                    public void failure(final String value) {
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                if (kProgressHUD != null && kProgressHUD.isShowing()) {
//                                    kProgressHUD.dismiss();
//                                }
//
//                                Toast.makeText(HistoryHealthActivity.this, value, Toast.LENGTH_LONG).show();
//                            }
//                        });
//
//                    }
//                });

                parse_activity_list_response(list_json, new ApiParse() {

                    @Override
                    public void onSuccess(final Object object,Object object1) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (kProgressHUD != null && kProgressHUD.isShowing()) {
                                    kProgressHUD.dismiss();
                                }

                                show_activity_list();
                            }
                        });


                    }

                    @Override
                    public void onFailure() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (kProgressHUD != null && kProgressHUD.isShowing()) {
                                    kProgressHUD.dismiss();
                                }

                                Toast.makeText(HistoryHealthActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
                            }
                        });


                    }
                });
            }else{
                commonUsedmethods.show_Toast(HistoryHealthActivity.this,getResources().getString(R.string.Please_connect_to_internet));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /** to make JSON request for activity list*/
    private JSONObject make_health_list_request(){

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("participant_id", AESEncryption.encrypt(user_id));
            jsonObject.put("research_info_id", AESEncryption.encrypt(research_info_id));
            jsonObject.put("start_date", AESEncryption.encrypt(commonUsedmethods.parse_date_in_this_format(start_date,"MM-dd-yyyy","yyyy-MM-dd")));//"2020-01-01"
            jsonObject.put("end_date", AESEncryption.encrypt(commonUsedmethods.parse_date_in_this_format(end_date,"MM-dd-yyyy","yyyy-MM-dd")));
            jsonObject.put("filter", AESEncryption.encrypt(filter_type));
            jsonObject.put("health_type", AESEncryption.encrypt(health_type));
        }catch (Exception e){
            e.printStackTrace();
        }

        return jsonObject;
    }

    /** parse activity list response for json*/
    private void parse_activity_list_response(String response, HistoryHealthActivity.ApiParse apiPasrsing){

        try {
            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject!=null && jsonObject.has("message") && !jsonObject.isNull("message")) {

                JSONObject jsonObject1 = jsonObject.getJSONObject("message");


                if(jsonObject1.has("data") && !jsonObject1.isNull("data")){

                    String annotations = jsonObject1.getString("data");

                    if(annotations!=null && !annotations.equalsIgnoreCase("")){

                        //String value = AESEncryption.decrypt(annotations);

                        JsonParser parser = new JsonParser();
                        JsonElement jsonElement = parser.parse(annotations).getAsJsonArray();

                        healthListModelArrayList = new Gson().fromJson(jsonElement, new TypeToken<List<HealthListModel>>() {
                        }.getType());
                        apiPasrsing.onSuccess(healthListModelArrayList,"");

                    }
                }



            }else{

                apiPasrsing.onFailure();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /** show activity list view*/
    private void show_activity_list(){

        try {

            if(healthListModelArrayList!=null && healthListModelArrayList.size()>0) {

                text_no_data_found.setVisibility(View.GONE);
                recycle_health_list.setVisibility(View.VISIBLE);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HistoryHealthActivity.this);
                recycle_health_list.setLayoutManager(linearLayoutManager);
                historyHealthListAdapter = new HistoryHealthAdapter(healthListModelArrayList,
                        HistoryHealthActivity.this, new OnRecycleItemClicks() {
                    @Override
                    public void onClick(Object object) {

                    }
                },filter_type);
                recycle_health_list.setAdapter(historyHealthListAdapter);
                historyHealthListAdapter.notifyDataSetChanged();

            }else{
                recycle_health_list.setVisibility(View.GONE);
                text_no_data_found.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Todo interface for API parsing
    private interface ApiParse{

        void onSuccess(Object object,Object object1);
        void onFailure();
    }
}
