package com.salk.mycircadianclock.history.history_intake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
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
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.salk.mycircadianclock.api.APIManager;
import com.salk.mycircadianclock.api.ApiConfig;
import com.salk.mycircadianclock.api.Apicallback;
import com.salk.mycircadianclock.history.history_activity.ActivityHistory;
import com.salk.mycircadianclock.history.history_health.HistoryHealthActivity;
import com.salk.mycircadianclock.interfaces.OnRecycleItemClicks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static android.webkit.WebSettings.LOAD_NO_CACHE;


public class HistoryIntakeActivity extends AppCompatActivity {

    //Todo declaration of XML views
    private RelativeLayout tabbar, rel_main;
    private ImageView image_foodCollage,image_list,image_chart;
    private Button btn_days,btn_weeks,btn_month,btn_activity,btn_helath;
    private View view_week,view_month;
    private RecyclerView recycle_food_collage,recycle_food_list;
    private TextView jump_to_date,text_no_data_found;

    //Todo declaration of common classes and variables
    private String user_id = "",research_info_id = "",auth_token = "",language = "",baseline_start_date = "",start_date = "",
                    end_date = "",call_type = "chat",filter_type = "day";
    private SharedPreferences sharedPreferences;
    private TinylocalDb tinylocalDb;
    private CommonUsedmethods commonUsedmethods;
    private boolean call_next = false;
    private int page_number = 1;
    private FoodCollageAdpter foodCollageAdpter;
    private ArrayList<FoodCollageModel> foodCollages_images = new ArrayList<>();
    private ArrayList<HistoryIntakeListModel> historyIntakeListModelArrayList = new ArrayList<>();
    private ArrayList<String> allWeekDates_array = new ArrayList<>();
    private Locale locale;
    private HistoryIntakeListAdapter historyIntakeListAdapter;
    private KProgressHUD kProgressHUD;
    private ConnectionDetector connectionDetector;
    private WebView chart_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            //to make screen full screen
            new CommonUsedmethods().makeActivityFullscreen(HistoryIntakeActivity.this);

            setContentView(R.layout.activity_history_intake);

            init();

            intializacommonclass();

            click_function();

            call_OnScroll_listener();

            weekViewForNewUser();

            image_chart.performClick();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo methos to intialize XML views
    private void init(){

        try{

            tabbar = findViewById(R.id.tab_bar);
            rel_main = findViewById(R.id.main);
            image_foodCollage = findViewById(R.id.grid_icon);
            image_list = findViewById(R.id.list_icon);
            image_chart = findViewById(R.id.chat_icon);
            btn_days = findViewById(R.id.days_title);
            btn_weeks = findViewById(R.id.weeks_title);
            btn_month = findViewById(R.id.months_title);
            view_week = findViewById(R.id.view_1);
            view_month = findViewById(R.id.view_2);
            recycle_food_collage = findViewById(R.id.recycle_food_collage);
            recycle_food_list = findViewById(R.id.recycle_food_list);
            jump_to_date = findViewById(R.id.jump_date);
            chart_view = findViewById(R.id.chart_view);
            text_no_data_found = findViewById(R.id.no_data);
            btn_activity = findViewById(R.id.activity_title);
            btn_helath = findViewById(R.id.health_title);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo to initialize common classes
    private void intializacommonclass(){
        try{

            new TabbarClick().click(HistoryIntakeActivity.this, tabbar, rel_main, "history");
            tinylocalDb = new TinylocalDb();
            commonUsedmethods = new CommonUsedmethods();
            connectionDetector = new ConnectionDetector(HistoryIntakeActivity.this);
            kProgressHUD = commonUsedmethods.show_progerssDialog(HistoryIntakeActivity.this,"Loading....",false);

            sharedPreferences = tinylocalDb.get_shared_pref(HistoryIntakeActivity.this);
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

    //Todo to add click functions to views
    private void click_function(){
        try{

            image_foodCollage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    update_ui_for_food_collage();
                    set_initial_start_end_date();

                    call_type = "food_collage";
                    page_number = 1;
                    call_food_collage_API();
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
                    set_initial_start_end_date();
                    call_food_chat_api();

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
                    call_food_list_API();
                }
            });

            btn_days.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    filter_type = "day";
                    update_ui_for_day_week_month_click(btn_days);
                    set_initial_start_end_date();

                    if(call_type.equalsIgnoreCase("chat")){

                        chart_view.clearHistory();
                        intializewebviw();
                        call_food_chat_api();

                    }else{

                        historyIntakeListModelArrayList.clear();
                        if(historyIntakeListAdapter!=null){
                            historyIntakeListAdapter.notifyDataSetChanged();
                        }
                        call_food_list_API();
                    }
                }
            });

            btn_weeks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    filter_type = "week";
                    update_ui_for_day_week_month_click(btn_weeks);
                    set_initial_start_end_date();

                    if(call_type.equalsIgnoreCase("chat")){

                        chart_view.clearHistory();
                        intializewebviw();
                        call_food_chat_api();
                    }else{

                        historyIntakeListModelArrayList.clear();
                        if(historyIntakeListAdapter!=null){
                            historyIntakeListAdapter.notifyDataSetChanged();
                        }
                        call_food_list_API();
                    }
                }
            });

            btn_month.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    filter_type = "month";
                    update_ui_for_day_week_month_click(btn_month);
                    set_initial_start_end_date();

                    if(call_type.equalsIgnoreCase("chat")){
                        chart_view.clearHistory();
                        intializewebviw();
                        call_food_chat_api();

                    }else{

                        historyIntakeListModelArrayList.clear();
                        if(historyIntakeListAdapter!=null){
                            historyIntakeListAdapter.notifyDataSetChanged();
                        }
                        call_food_list_API();
                    }
                }
            });

            btn_activity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HistoryIntakeActivity.this, ActivityHistory.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    finish();
                }
            });

            btn_helath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HistoryIntakeActivity.this, HistoryHealthActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    finish();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo call food collage API
    private void call_food_collage_API(){

        try {

            if(connectionDetector.isConnectingToInternet()) {
                if (kProgressHUD != null && !kProgressHUD.isShowing()) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            kProgressHUD.show();
                        }
                    });

                }

                String url = ApiConfig.FOOD_COLLAGE_API;
                JSONObject jsonObject = make_food_collage_request();

                new APIManager().Apicall(auth_token, url, jsonObject, new Apicallback() {
                    @Override
                    public void success(int code, String value) {

                        if (code == 200) {

                            //parse response
                            parse_food_collage_response(value, new ApiParse() {

                                @Override
                                public void onSuccess(final Object object,Object object1) {

                                    if (call_next) {
                                        call_food_collage_API();
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (kProgressHUD != null && kProgressHUD.isShowing()) {
                                                kProgressHUD.dismiss();
                                            }
                                            show_food_collage(object);
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
                                            Toast.makeText(HistoryIntakeActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
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
                                    Toast.makeText(HistoryIntakeActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    }

                    @Override
                    public void failure(final String value) {

                        call_next = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (kProgressHUD != null && kProgressHUD.isShowing()) {
                                    kProgressHUD.dismiss();
                                }
                                Toast.makeText(HistoryIntakeActivity.this, value, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
            }else{
                commonUsedmethods.show_Toast(HistoryIntakeActivity.this,getResources().getString(R.string.Please_connect_to_internet));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Todo to make JSON request for food collage
    private JSONObject make_food_collage_request(){

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("participant_id", AESEncryption.encrypt(user_id));
            jsonObject.put("research_info_id", AESEncryption.encrypt(research_info_id));
            jsonObject.put("start_date", AESEncryption.encrypt(start_date));//"2020-01-01"
            jsonObject.put("end_date", AESEncryption.encrypt(end_date));
            jsonObject.put("page_no", AESEncryption.encrypt(String.valueOf(page_number)));
        }catch (Exception e){
            e.printStackTrace();
        }

        return jsonObject;
    }

    //Todo parse food collage response for json
    private void parse_food_collage_response(String response,ApiParse apiPasrsing){

        try {
            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject!=null && jsonObject.has("message") && !jsonObject.isNull("message")) {

                JSONObject jsonObject1 = jsonObject.getJSONObject("message");

                if(jsonObject1.has("next_page_number") && !jsonObject1.isNull("next_page_number")){

                    call_next = true;
                    page_number = Integer.valueOf(AESEncryption.decrypt(jsonObject1.getString("next_page_number")));
                }else{
                    call_next = false;
                }

                if(jsonObject1.has("annotations") && !jsonObject1.isNull("annotations")){

                    String annotations = jsonObject1.getString("annotations");

                    if(annotations!=null && !annotations.equalsIgnoreCase("")){

                        String value = AESEncryption.decrypt(annotations);

                        JsonParser parser = new JsonParser();
                        JsonElement jsonElement = parser.parse(value).getAsJsonArray();

                        ArrayList<FoodCollageModel> foodCollageModelArrayList = new Gson().fromJson(jsonElement, new TypeToken<List<FoodCollageModel>>() {
                        }.getType());

                        for(int i=0;i<foodCollageModelArrayList.size();i++){

                            foodCollageModelArrayList.get(i).setOriginal_time(commonUsedmethods.
                                    convert_date_to_timestamp("yyyy-MM-dd'T'HH:mm:ssZ",foodCollageModelArrayList.get(i).getOriginal_logtime()));
                        }

                        //Collections.sort(foodCollageModelArrayList,new FoodcollageTimeComparator());
                        apiPasrsing.onSuccess(foodCollageModelArrayList,"");
                    }
                }



            }else{

                apiPasrsing.onFailure();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo to set data in recycleview
    private void show_food_collage(Object object){

        try {
            ArrayList<FoodCollageModel> arrayList = (ArrayList<FoodCollageModel>) object;

            if (foodCollages_images.size() == 0) {

                foodCollages_images.addAll(arrayList);

                GridLayoutManager mLayoutManager = new GridLayoutManager(HistoryIntakeActivity.this, 3);
                recycle_food_collage.setLayoutManager(mLayoutManager);
                foodCollageAdpter = new FoodCollageAdpter(foodCollages_images, HistoryIntakeActivity.this, new OnRecycleItemClicks() {
                    @Override
                    public void onClick(Object object) {

                        show_food_pager_dialog((int) object);
                    }
                });
                recycle_food_collage.setAdapter(foodCollageAdpter);

            } else if (page_number > 1) {

                foodCollages_images.addAll(arrayList);

                if (foodCollageAdpter != null && foodCollages_images.size() > 0) {
                    foodCollageAdpter.notifyItemInserted(foodCollages_images.size());
                }
            }

            if(foodCollages_images!=null && foodCollages_images.size()==0){
                recycle_food_collage.setVisibility(View.GONE);
                text_no_data_found.setVisibility(View.VISIBLE);
            }else{
                text_no_data_found.setVisibility(View.GONE);
                recycle_food_collage.setVisibility(View.VISIBLE);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

   //Todo to add onscroll listener on recycleview
    public void call_OnScroll_listener() {

        try {
            recycle_food_collage.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int scrollY) {
                    super.onScrolled(recyclerView, dx, scrollY);


                    if (scrollY > 0) { // only when scrolling up

                        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                        int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();


                        if (lastItem == foodCollages_images.size() - 1) {

                            if (call_next) {

                                call_food_collage_API();
                            }


                        }
                    }


                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo to show food collage view pager dialog
    private void show_food_pager_dialog(final int i){

        try {
            final Dialog list_dialog = new Dialog(HistoryIntakeActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            list_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            list_dialog.setContentView(R.layout.food_view_pager_dialog);

            ImageView close_btn = list_dialog.findViewById(R.id.close_btn);
            final ViewPager viewPager = list_dialog.findViewById(R.id.viewpager);

            FoodViewPagerAdapter foodViewPagerAdapter = new FoodViewPagerAdapter(HistoryIntakeActivity.this, foodCollages_images);
            viewPager.setAdapter(foodViewPagerAdapter);


            close_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    list_dialog.dismiss();
                }
            });


            list_dialog.show();

            viewPager.postDelayed(new Runnable() {

                @Override
                public void run() {
                    viewPager.setCurrentItem(i, false);
                }
            }, 1);

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

            final Dialog listDialog = new Dialog(HistoryIntakeActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            listDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            listDialog.setCancelable(false);
            listDialog.setContentView(R.layout.dateholder);
            listDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));

            RecyclerView recyclerView = listDialog.findViewById(R.id.listView1);
            Button cancel = listDialog.findViewById(R.id.cancelbuttonfordata);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HistoryIntakeActivity.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            WeekListAdapter weekListAdapter = new WeekListAdapter(allWeekDates_array,
                    HistoryIntakeActivity.this, new OnRecycleItemClicks() {
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

    //Todo to call API after clicked on week list arry from jump to date
    private void check_for_jumpto_date(String date){

        try{

            if(date.contains("to")){

                String[] dates = date.split(" to ");

                start_date = commonUsedmethods.parse_date_in_this_format(dates[0],"yyyy-MM-dd","dd - MMM - yyyy");
                end_date = commonUsedmethods.parse_date_in_this_format(dates[1],"yyyy-MM-dd","dd - MMM - yyyy");

                if(call_type.equalsIgnoreCase("food_collage")) {

                    foodCollages_images.clear();
                    if(foodCollageAdpter!=null) {
                        foodCollageAdpter.notifyDataSetChanged();
                    }
                    page_number = 1;
                    call_food_collage_API();

                }else if(call_type.equalsIgnoreCase("list")){

                    historyIntakeListModelArrayList.clear();

                    if(historyIntakeListAdapter!=null) {
                        historyIntakeListAdapter.notifyDataSetChanged();
                    }
                    call_food_list_API();
                }else if(call_type.equalsIgnoreCase("chat")){

                    chart_view.clearHistory();
                    call_food_chat_api();
                }
            }

        }catch (Exception e){

            e.printStackTrace();
        }
    }

    //Todo update UI for food collage button click
    private void update_ui_for_food_collage(){

        try {
            btn_month.setVisibility(View.GONE);
            btn_weeks.setVisibility(View.GONE);
            view_week.setVisibility(View.GONE);
            view_month.setVisibility(View.GONE);

            image_chart.setImageResource(R.mipmap.graph_unselected);
            image_list.setImageResource(R.mipmap.list_unselected);
            image_foodCollage.setImageResource(R.mipmap.grid_selected);

            btn_days.setBackgroundColor(getResources().getColor(R.color.chart_filter_bg));
            btn_days.setTextColor(getResources().getColor(R.color.white));

            recycle_food_list.setVisibility(View.GONE);
            chart_view.setVisibility(View.GONE);
            recycle_food_collage.setVisibility(View.VISIBLE);


            if(foodCollageAdpter!=null){
                foodCollages_images.clear();
                foodCollageAdpter.notifyDataSetChanged();
            }

            jump_to_date.setText(R.string.Jump_to_Date);
            text_no_data_found.setVisibility(View.GONE);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Todo update UI for chart icon click
    private void update_ui_for_history_chart(){

        try {
            btn_month.setVisibility(View.VISIBLE);
            btn_weeks.setVisibility(View.VISIBLE);
            view_week.setVisibility(View.VISIBLE);
            view_month.setVisibility(View.VISIBLE);

            image_chart.setImageResource(R.mipmap.graph_selected);
            image_list.setImageResource(R.mipmap.list_unselected);
            image_foodCollage.setImageResource(R.mipmap.grid_unselected);

            recycle_food_collage.setVisibility(View.GONE);
            recycle_food_list.setVisibility(View.GONE);
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
            btn_month.setVisibility(View.VISIBLE);
            btn_weeks.setVisibility(View.VISIBLE);
            view_week.setVisibility(View.VISIBLE);
            view_month.setVisibility(View.VISIBLE);

            image_chart.setImageResource(R.mipmap.graph_unselected);
            image_list.setImageResource(R.mipmap.list_selected);
            image_foodCollage.setImageResource(R.mipmap.grid_unselected);

            recycle_food_collage.setVisibility(View.GONE);
            chart_view.setVisibility(View.GONE);
            recycle_food_list.setVisibility(View.VISIBLE);

            if(historyIntakeListAdapter!=null){
                historyIntakeListModelArrayList.clear();
                historyIntakeListAdapter.notifyDataSetChanged();
            }

            jump_to_date.setText(R.string.Jump_to_Date);
            text_no_data_found.setVisibility(View.GONE);
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

    //Todo call food list API
    private void call_food_list_API(){

        try {

            if(connectionDetector.isConnectingToInternet()) {
                if (kProgressHUD != null && !kProgressHUD.isShowing()) {
                    kProgressHUD.show();
                }

                String url = ApiConfig.FOOD_LIST_API;
                JSONObject jsonObject = make_food_list_request();

                new APIManager().Apicall(auth_token, url, jsonObject, new Apicallback() {
                    @Override
                    public void success(int code, String value) {

                        if (code == 200) {

                            //parse response
                            parse_food_list_response(value, new ApiParse() {

                                @Override
                                public void onSuccess(final Object object,Object object1) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (kProgressHUD != null && kProgressHUD.isShowing()) {
                                                kProgressHUD.dismiss();
                                            }

                                            show_food_list();
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

                                            Toast.makeText(HistoryIntakeActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
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

                                    Toast.makeText(HistoryIntakeActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
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

                                Toast.makeText(HistoryIntakeActivity.this, value, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
            }else{
                commonUsedmethods.show_Toast(HistoryIntakeActivity.this,getResources().getString(R.string.Please_connect_to_internet));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Todo to make JSON request for food list
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

    //Todo parse food list response for json
    private void parse_food_list_response(String response,ApiParse apiPasrsing){

        try {
            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject!=null && jsonObject.has("message") && !jsonObject.isNull("message")) {

                JSONObject jsonObject1 = jsonObject.getJSONObject("message");


                if(jsonObject1.has("data") && !jsonObject1.isNull("data")){

                    String annotations = jsonObject1.getString("data");

                    if(annotations!=null && !annotations.equalsIgnoreCase("")){

                        String value = AESEncryption.decrypt(annotations);
                        JSONObject jsonObject2 = new JSONObject(value);

                        Iterator<String> iter = jsonObject2.keys();
                        while (iter.hasNext()) {

                            HistoryIntakeListModel historyIntakeListModel = new HistoryIntakeListModel();
                            StringBuffer stringBuffer = new StringBuffer();
                            String key = iter.next();
                            try {

                                if(key.contains(":")){

                                    String[] arr = key.split(":");
                                    String date = commonUsedmethods.parse_date_in_this_format(
                                            arr[0],"MM/dd","yyyy-MM-dd")+"-"+
                                            commonUsedmethods.parse_date_in_this_format(
                                                    arr[1],"MM/dd","yyyy-MM-dd");

                                    historyIntakeListModel.setDate(date);

                                }else{

                                    historyIntakeListModel.setDate(commonUsedmethods.parse_date_in_this_format(
                                            key,"MM/dd","yyyy/MM/dd"
                                    ));
                                }

                                JSONObject jsonObject3 = jsonObject2.getJSONObject(key);

                                if(jsonObject3.length()!=0) {
                                    Iterator<String> iter1 = jsonObject3.keys();
                                    while (iter1.hasNext()) {

                                        String key1 = iter1.next();
                                        stringBuffer.append(key1);
                                        try {

                                            String value1 = jsonObject3.getString(key1);
                                            stringBuffer.append(" (" + value1 + ") ");

                                        } catch (JSONException e) {

                                        }
                                    }
                                }else{
                                    stringBuffer.append(getResources().getString(R.string.No_Food_Log_Found));
                                }

                                historyIntakeListModel.setValue(stringBuffer.toString());
                                historyIntakeListModelArrayList.add(historyIntakeListModel);

                            } catch (JSONException e) {

                            }
                        }

                        apiPasrsing.onSuccess(historyIntakeListModelArrayList,"");

                    }
                }



            }else{

                apiPasrsing.onFailure();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo show foo list view
    private void show_food_list(){

        try {

            if(historyIntakeListModelArrayList!=null && historyIntakeListModelArrayList.size()>0) {

                text_no_data_found.setVisibility(View.GONE);
                recycle_food_list.setVisibility(View.VISIBLE);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HistoryIntakeActivity.this);
                recycle_food_list.setLayoutManager(linearLayoutManager);
                historyIntakeListAdapter = new HistoryIntakeListAdapter(historyIntakeListModelArrayList,
                        HistoryIntakeActivity.this, new OnRecycleItemClicks() {
                    @Override
                    public void onClick(Object object) {

                    }
                });
                recycle_food_list.setAdapter(historyIntakeListAdapter);
                historyIntakeListAdapter.notifyDataSetChanged();

            }else{
                recycle_food_list.setVisibility(View.GONE);
                text_no_data_found.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Todo call food chat API
    private void call_food_chat_api(){

        try {

            if(connectionDetector.isConnectingToInternet()) {
                if (kProgressHUD != null && !kProgressHUD.isShowing()) {
                    kProgressHUD.show();
                }

                String url = ApiConfig.FOOD_CHAT_API;
                JSONObject jsonObject = make_food_list_request();

                new APIManager().Apicall(auth_token, url, jsonObject, new Apicallback() {
                    @Override
                    public void success(int code, String value) {

                        if (code == 200) {

                            //parse response
                            parse_food_chat_response(value, new ApiParse() {

                                @Override
                                public void onSuccess(final Object object, final Object object1) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (kProgressHUD != null && kProgressHUD.isShowing()) {
                                                kProgressHUD.dismiss();
                                            }

                                            load_intake_day_chart(object,object1);

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

                                            Toast.makeText(HistoryIntakeActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
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

                                    Toast.makeText(HistoryIntakeActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
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

                                Toast.makeText(HistoryIntakeActivity.this, value, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
            }else{
                commonUsedmethods.show_Toast(HistoryIntakeActivity.this,getResources().getString(R.string.Please_connect_to_internet));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Todo parse food list response for json
    private void parse_food_chat_response(String response,ApiParse apiPasrsing){

        try {
            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject!=null && jsonObject.has("message") && !jsonObject.isNull("message")) {

                JSONObject jsonObject1 = jsonObject.getJSONObject("message");

                if(filter_type.equalsIgnoreCase("day") || filter_type.equalsIgnoreCase("jump_date")){

                    if(jsonObject1.has("data") && !jsonObject1.isNull("data")){

                        String annotations = jsonObject1.getString("data");

                        if(annotations!=null && !annotations.equalsIgnoreCase("")){

                            String value = AESEncryption.decrypt(annotations);
                            JSONArray jsonArray = new JSONArray(value);
                            JSONObject jsonObject3 = new JSONObject();
                            jsonObject3.put("day_data",jsonArray);
                            jsonObject3.put("target_start_time",AESEncryption.decrypt(jsonObject1.getString("target_start_time")));
                            jsonObject3.put("target_end_time",AESEncryption.decrypt(jsonObject1.getString("target_end_time")));
                            jsonObject3.put("actual_start_time",AESEncryption.decrypt(jsonObject1.getString("actual_start_time")));
                            jsonObject3.put("actual_end_time",AESEncryption.decrypt(jsonObject1.getString("actual_end_time")));

                            apiPasrsing.onSuccess(jsonObject3,"");

                        }
                    }
                }else{

                    JSONObject json_history = null;
                    JSONObject json_data = null;

                    if (jsonObject1.has("history") && !jsonObject1.isNull("history")) {

                        String history = jsonObject1.getString("history");

                        if (history != null && !history.equalsIgnoreCase("")) {

                            String value = AESEncryption.decrypt(history);
                            json_history = new JSONObject(value);
                        }
                    }
                    if (jsonObject1.has("data") && !jsonObject1.isNull("data")) {

                        String annotations = jsonObject1.getString("data");

                        if (annotations != null && !annotations.equalsIgnoreCase("")) {

                            String value = AESEncryption.decrypt(annotations);
                            json_data = new JSONObject(value);
                        }
                    }

                    apiPasrsing.onSuccess(json_history, json_data);


                }


            }else{

                apiPasrsing.onFailure();
            }

        }catch (Exception e){
            e.printStackTrace();
            apiPasrsing.onFailure();
        }
    }

    //Todo to show chart in webview
    private void load_intake_day_chart(Object object,Object object1){

        try {
            if (filter_type.equalsIgnoreCase("day") || filter_type.equalsIgnoreCase("jump_date")) {
                final JSONObject jsonObject = (JSONObject) object;
                chart_view.setWebViewClient(new WebViewClient() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onPageFinished(WebView view, String url) {

                        chart_view.loadUrl("javascript: feedogramData('"
                                + jsonObject + "');");
                    }
                });

                if (language.matches("français")) {

                    chart_view.loadUrl("file:///android_asset/feedograms/feed_french.html");
                } else {
                    chart_view.loadUrl("file:///android_asset/feedograms/feed.html");
                }
            } else {

                final JSONObject jsonObject = (JSONObject) object;
                final JSONObject jsonObject1 = (JSONObject) object1;

                chart_view.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {

                        chart_view.loadUrl("javascript: historyDisplay('"
                                + jsonObject1 + "','" + jsonObject
                                + "');");

                    }
                });
                if (language.matches("français")) {
                    chart_view.loadUrl("file:///android_asset/chart_history/chart_history_french.html");
                } else {
                    chart_view.loadUrl("file:///android_asset/chart_history/chart_history.html");
                }

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

    //Todo interface for API parsing
    private interface ApiParse{

       void onSuccess(Object object,Object object1);
       void onFailure();
    }
}
