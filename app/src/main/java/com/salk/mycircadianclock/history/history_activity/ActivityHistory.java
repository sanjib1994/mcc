package com.salk.mycircadianclock.history.history_activity;

import android.app.Dialog;
import android.content.DialogInterface;
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
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.salk.mycircadianclock.api.ApiConfig;
import com.salk.mycircadianclock.api.Apicallback;
import com.salk.mycircadianclock.history.history_health.HistoryHealthActivity;
import com.salk.mycircadianclock.history.history_intake.HistoryIntakeActivity;
import com.salk.mycircadianclock.history.history_intake.WeekListAdapter;
import com.salk.mycircadianclock.interfaces.OnRecycleItemClicks;

import org.json.JSONArray;
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

public class ActivityHistory extends AppCompatActivity {

    /** declaration of XML views*/
    private RelativeLayout tabbar, rel_main,rel_alltags;
    private ImageView image_list,image_chart,info_icon;
    private Button btn_days,btn_weeks,btn_month,btn_intake,btn_helath;
    private View view_week,view_month;
    private RecyclerView recycle_activity_list;
    private TextView jump_to_date,text_no_data_found,text_average;

    /**declaration of common classes and variables*/
    private String user_id = "",research_info_id = "",auth_token = "",language = "",baseline_start_date = "",start_date = "",
            end_date = "",call_type = "chat",filter_type = "day";
    private SharedPreferences sharedPreferences;
    private TinylocalDb tinylocalDb;
    private CommonUsedmethods commonUsedmethods;
    private ArrayList<String> allWeekDates_array = new ArrayList<>();
    private ArrayList<ActivityListModel> activityListModelArrayList = new ArrayList<>();
    private Locale locale;
    private KProgressHUD kProgressHUD;
    private ConnectionDetector connectionDetector;
    private WebView chart_view;
    private HistoryActivityListAdapter historyActivityListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //to make screen full screen
            new CommonUsedmethods().makeActivityFullscreen(ActivityHistory.this);

            setContentView(R.layout.activity_history);

            init();

            initialize_common_class();

            click_function();

            weekViewForNewUser();

            image_chart.performClick();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /** methods to initialize XML views*/
    private void init(){
        try{

            tabbar = findViewById(R.id.tab_bar);
            rel_main = findViewById(R.id.main);
            image_list = findViewById(R.id.list_icon);
            image_chart = findViewById(R.id.chat_icon);
            btn_days = findViewById(R.id.days_title);
            btn_weeks = findViewById(R.id.weeks_title);
            btn_month = findViewById(R.id.months_title);
            view_week = findViewById(R.id.view_1);
            view_month = findViewById(R.id.view_2);
            recycle_activity_list = findViewById(R.id.listview_history);
            jump_to_date = findViewById(R.id.jump_date);
            chart_view = findViewById(R.id.chart_view);
            text_no_data_found = findViewById(R.id.no_data);
            btn_intake = findViewById(R.id.intake_title);
            btn_helath = findViewById(R.id.health_title);
            text_average = findViewById(R.id.blankview);
            info_icon = findViewById(R.id.info_icon);
            rel_alltags = findViewById(R.id.tags);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /** to initialize common classes*/
    private void initialize_common_class(){

        try{

            new TabbarClick().click(ActivityHistory.this, tabbar, rel_main, "history");
            tinylocalDb = new TinylocalDb();
            commonUsedmethods = new CommonUsedmethods();
            connectionDetector = new ConnectionDetector(ActivityHistory.this);
            kProgressHUD = commonUsedmethods.show_progerssDialog(ActivityHistory.this,"Loading....",false);

            sharedPreferences = tinylocalDb.get_shared_pref(ActivityHistory.this);
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

    /** to add click functions to views*/
    private void click_function(){
        try{

            btn_intake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ActivityHistory.this, HistoryIntakeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    finish();
                }
            });

            btn_helath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ActivityHistory.this, HistoryHealthActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
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

                    call_type = "chat";
                    set_initial_start_end_date();
                    call_activity_chat_api();

                }
            });

            image_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    update_ui_for_history_list();

                    call_type = "list";
                    set_initial_start_end_date();
                    call_activity_list_API();

                }
            });

            btn_days.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    filter_type = "day";
                    text_average.setVisibility(View.INVISIBLE);
                    update_ui_for_day_week_month_click(btn_days);
                    set_initial_start_end_date();

                    if(call_type.equalsIgnoreCase("chat")){

                        chart_view.clearHistory();
                        intializewebviw();
                        call_activity_chat_api();

                    }else{

                        call_activity_list_API();
                    }
                }
            });

            btn_weeks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    filter_type = "week";
                    text_average.setVisibility(View.VISIBLE);
                    update_ui_for_day_week_month_click(btn_weeks);
                    set_initial_start_end_date();

                    if(call_type.equalsIgnoreCase("chat")){

                        chart_view.clearHistory();
                        intializewebviw();
                        call_activity_chat_api();

                    }else{
                        call_activity_list_API();

                    }
                }
            });

            btn_month.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    filter_type = "month";
                    text_average.setVisibility(View.VISIBLE);
                    update_ui_for_day_week_month_click(btn_month);
                    set_initial_start_end_date();

                    if(call_type.equalsIgnoreCase("chat")){
                        chart_view.clearHistory();
                        intializewebviw();
                        call_activity_chat_api();
                    }else{

                        call_activity_list_API();
                    }
                }
            });

            info_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(call_type.equalsIgnoreCase("list")){
                       showInfo_dialog(getResources().getString(R.string.average_daily_duration_sleep),getResources().getString(R.string.average_daily_duration_sleep));
                    }else{
                        showInfo_dialog(getResources().getString(R.string.How_sleep_is_plotted),getResources().getString(R.string.All_sleep_logged));
                    }
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /** to get week list array*/
    private void weekViewForNewUser() {
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

    /** to show dialog for jump to date*/
    private void showDialog() {

        try {

            final Dialog listDialog = new Dialog(ActivityHistory.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            listDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            listDialog.setCancelable(false);
            listDialog.setContentView(R.layout.dateholder);
            listDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));

            RecyclerView recyclerView = listDialog.findViewById(R.id.listView1);
            Button cancel = listDialog.findViewById(R.id.cancelbuttonfordata);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActivityHistory.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            WeekListAdapter weekListAdapter = new WeekListAdapter(allWeekDates_array,
                    ActivityHistory.this, new OnRecycleItemClicks() {
                @Override
                public void onClick(Object object) {

                    listDialog.dismiss();

                    jump_to_date.setText((String)object);
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

    /** to call API after clicked on week list arry from jump to date*/
    private void check_for_jumpto_date(String date){

        try{

            if(date.contains("to")){

                String[] dates = date.split(" to ");

                start_date = commonUsedmethods.parse_date_in_this_format(dates[0],"yyyy-MM-dd","dd - MMM - yyyy");
                end_date = commonUsedmethods.parse_date_in_this_format(dates[1],"yyyy-MM-dd","dd - MMM - yyyy");

                if(call_type.equalsIgnoreCase("list")){

                    text_average.setVisibility(View.INVISIBLE);
                    activityListModelArrayList.clear();

                    if(historyActivityListAdapter!=null) {
                        historyActivityListAdapter.notifyDataSetChanged();
                    }
                    call_activity_list_API();

                }else if(call_type.equalsIgnoreCase("chat")){

                    chart_view.clearHistory();
                    call_activity_chat_api();
                }

            }

        }catch (Exception e){

            e.printStackTrace();
        }
    }

    /** update UI for chart icon click*/
    private void update_ui_for_history_chart(){

        try {
            btn_month.setVisibility(View.VISIBLE);
            btn_weeks.setVisibility(View.VISIBLE);
            view_week.setVisibility(View.VISIBLE);
            view_month.setVisibility(View.VISIBLE);

            image_chart.setImageResource(R.mipmap.graph_selected);
            image_list.setImageResource(R.mipmap.list_unselected);

            recycle_activity_list.setVisibility(View.GONE);
            chart_view.setVisibility(View.VISIBLE);

            jump_to_date.setText(R.string.Jump_to_Date);
            text_no_data_found.setVisibility(View.GONE);
            rel_alltags.setVisibility(View.GONE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /** update UI for list icon click*/
    private void update_ui_for_history_list(){

        try {
            btn_month.setVisibility(View.VISIBLE);
            btn_weeks.setVisibility(View.VISIBLE);
            view_week.setVisibility(View.VISIBLE);
            view_month.setVisibility(View.VISIBLE);

            image_chart.setImageResource(R.mipmap.graph_unselected);
            image_list.setImageResource(R.mipmap.list_selected);

            chart_view.setVisibility(View.GONE);
            recycle_activity_list.setVisibility(View.VISIBLE);

            jump_to_date.setText(R.string.Jump_to_Date);
            text_no_data_found.setVisibility(View.GONE);
            rel_alltags.setVisibility(View.VISIBLE);

            if(filter_type.equalsIgnoreCase("day") || filter_type.equalsIgnoreCase("jump_date")){

                text_average.setVisibility(View.INVISIBLE);
            }else{
                text_average.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /** update UI for day weekk or month button click*/
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

    /** set intial start and end date for day,week,month*/
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

    /** intialize webview settings*/
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

    /** call activity chat API*/
    private void call_activity_chat_api(){

        try {

            if(connectionDetector.isConnectingToInternet()) {
                if (kProgressHUD != null && !kProgressHUD.isShowing()) {
                    kProgressHUD.show();
                }

                String url = ApiConfig.ACTIVITY_CHART_API;
                JSONObject jsonObject = make_food_list_request();

                new APIManager().Apicall(auth_token, url, jsonObject, new Apicallback() {
                    @Override
                    public void success(int code, String value) {

                        if (code == 200) {

                            //parse response
                            parse_activity_chat_response(value, new ApiParse() {

                                @Override
                                public void onSuccess(final Object object, final Object object1) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (kProgressHUD != null && kProgressHUD.isShowing()) {
                                                kProgressHUD.dismiss();
                                            }

                                            load_activity_chart(object,object1);

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

                                            Toast.makeText(ActivityHistory.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
                                        }
                                    });


                                }
                            });
                        } else {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (kProgressHUD != null && kProgressHUD.isShowing()) {
                                        kProgressHUD.dismiss();
                                    }

                                    Toast.makeText(ActivityHistory.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    }

                    @Override
                    public void failure(final String value) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (kProgressHUD != null && kProgressHUD.isShowing()) {
                                    kProgressHUD.dismiss();
                                }

                                Toast.makeText(ActivityHistory.this, value, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
            }else{
                commonUsedmethods.show_Toast(ActivityHistory.this,getResources().getString(R.string.Please_connect_to_internet));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /** parse activity list response for json*/
    private void parse_activity_chat_response(String response, ApiParse apiPasrsing){

        try {
            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject!=null && jsonObject.has("message") && !jsonObject.isNull("message")) {

                JSONObject jsonObject1 = jsonObject.getJSONObject("message");

                if(jsonObject1.has("data") && !jsonObject1.isNull("data")){

                    String annotations = jsonObject1.getString("data");

                    if(annotations!=null && !annotations.equalsIgnoreCase("")){

                        String value = AESEncryption.decrypt(annotations);
                        JSONArray jsonArray = new JSONArray(value);
                        JSONObject jsonObject2 = new JSONObject();
                        jsonObject2.put("data",jsonArray);

                        apiPasrsing.onSuccess(jsonObject2,"");

                    }
                }
            }else{

                apiPasrsing.onFailure();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /** to show chart in web view*/
    private void load_activity_chart(final Object object,Object object1){

        try {

            chart_view.setWebViewClient(new WebViewClient() {
                @SuppressWarnings("deprecation")
                @Override
                public void onPageFinished(WebView view, String url) {
                    if (filter_type.equalsIgnoreCase("day") || filter_type.equalsIgnoreCase("jump_date")) {
                        chart_view.loadUrl("javascript: activityDisplay('"
                                + object + "');");
                    }else{
                        chart_view.loadUrl("javascript: activeDataDisplay('"
                                + object + "');");
                    }
                }
            });
            if (filter_type.equalsIgnoreCase("day") || filter_type.equalsIgnoreCase("jump_date")) {

                if (language.matches("français")) {

                    chart_view.loadUrl("file:///android_asset/activity_day_chart/activity-chart_french.html");
                } else {
                    chart_view.loadUrl("file:///android_asset/activity_day_chart/activity-chart.html");
                }

            } else {

                if (language.matches("français")) {

                    chart_view.loadUrl("file:///android_asset/actvity_week_month_chart/activity_sleep_french.html");
                } else {
                    chart_view.loadUrl("file:///android_asset/actvity_week_month_chart/activity_sleep.html");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /** call activity list API*/
    private void call_activity_list_API(){

        try {

            if(connectionDetector.isConnectingToInternet()) {
                if (kProgressHUD != null && !kProgressHUD.isShowing()) {
                    kProgressHUD.show();
                }

                String url = ApiConfig.ACTIVITY_LIST_API;
                JSONObject jsonObject = make_food_list_request();

                new APIManager().Apicall(auth_token, url, jsonObject, new Apicallback() {
                    @Override
                    public void success(int code, String value) {

                        if (code == 200) {

                            //parse response
                            parse_activity_list_response(value, new ApiParse() {

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

                                            Toast.makeText(ActivityHistory.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
                                        }
                                    });


                                }
                            });
                        } else {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (kProgressHUD != null && kProgressHUD.isShowing()) {
                                        kProgressHUD.dismiss();
                                    }

                                    Toast.makeText(ActivityHistory.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    }

                    @Override
                    public void failure(final String value) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (kProgressHUD != null && kProgressHUD.isShowing()) {
                                    kProgressHUD.dismiss();
                                }

                                Toast.makeText(ActivityHistory.this, value, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
            }else{
                commonUsedmethods.show_Toast(ActivityHistory.this,getResources().getString(R.string.Please_connect_to_internet));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /** to make JSON request for activity list*/
    private JSONObject make_food_list_request(){

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("participant_id", AESEncryption.encrypt(user_id));
            jsonObject.put("research_info_id", AESEncryption.encrypt(research_info_id));
            jsonObject.put("start_date", AESEncryption.encrypt(commonUsedmethods.parse_date_in_this_format(start_date,"MM-dd-yyyy","yyyy-MM-dd")));//"2020-01-01"
            jsonObject.put("end_date", AESEncryption.encrypt(commonUsedmethods.parse_date_in_this_format(end_date,"MM-dd-yyyy","yyyy-MM-dd")));
            jsonObject.put("filter", AESEncryption.encrypt(filter_type));
        }catch (Exception e){
            e.printStackTrace();
        }

        return jsonObject;
    }

    /** parse activity list response for json*/
    private void parse_activity_list_response(String response, ApiParse apiPasrsing){

        try {
            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject!=null && jsonObject.has("message") && !jsonObject.isNull("message")) {

                JSONObject jsonObject1 = jsonObject.getJSONObject("message");

                if(jsonObject1.has("data") && !jsonObject1.isNull("data")){

                    String annotations = jsonObject1.getString("data");

                    if(annotations!=null && !annotations.equalsIgnoreCase("")){

                        String value = AESEncryption.decrypt(annotations);

                        JsonParser parser = new JsonParser();
                        JsonElement jsonElement = parser.parse(value).getAsJsonArray();

                        activityListModelArrayList = new Gson().fromJson(jsonElement, new TypeToken<List<ActivityListModel>>() {
                        }.getType());
                        apiPasrsing.onSuccess(activityListModelArrayList,"");

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

            if(activityListModelArrayList!=null && activityListModelArrayList.size()>0) {

                text_no_data_found.setVisibility(View.GONE);
                recycle_activity_list.setVisibility(View.VISIBLE);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActivityHistory.this);
                recycle_activity_list.setLayoutManager(linearLayoutManager);
                historyActivityListAdapter = new HistoryActivityListAdapter(activityListModelArrayList,
                        ActivityHistory.this, new OnRecycleItemClicks() {
                    @Override
                    public void onClick(Object object) {

                    }
                },filter_type);
                recycle_activity_list.setAdapter(historyActivityListAdapter);
                historyActivityListAdapter.notifyDataSetChanged();

            }else{
                recycle_activity_list.setVisibility(View.GONE);
                text_no_data_found.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /** to show dialog on information icon click*/
    public void showInfo_dialog(String title,String message) {
        try {
            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /** interface for API parsing*/
    private interface ApiParse{

        void onSuccess(Object object,Object object1);
        void onFailure();
    }
}
