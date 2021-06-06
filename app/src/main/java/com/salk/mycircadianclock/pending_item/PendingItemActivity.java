package com.salk.mycircadianclock.pending_item;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.Utility.TabbarClick;
import com.salk.mycircadianclock.Utility.TinylocalDb;
import com.salk.mycircadianclock.api.AESEncryption;
import com.salk.mycircadianclock.api.APIManager;
import com.salk.mycircadianclock.api.APIPasrsing;
import com.salk.mycircadianclock.api.ApiConfig;
import com.salk.mycircadianclock.api.Apicallback;
import com.salk.mycircadianclock.exercise.ExerciseEntries;
import com.salk.mycircadianclock.exercise.LogExerciseDataActivity;
import com.salk.mycircadianclock.food.FoodSendActivity;
import com.salk.mycircadianclock.food.FoodStuff;
import com.salk.mycircadianclock.health_vitals.HealthSaveRequest;
import com.salk.mycircadianclock.health_vitals.LogHealthDataActivity;
import com.salk.mycircadianclock.interfaces.OnRecycleItemClicks;
import com.salk.mycircadianclock.localDatabase.DatabaseRepo;
import com.salk.mycircadianclock.localDatabase.OnDataFetched;
import com.salk.mycircadianclock.sleep.LogSleepDataActivity;
import com.salk.mycircadianclock.sleep.PreviousSleepEntry;
import com.salk.mycircadianclock.sleep.PreviousdaySleepEntryAdapter;
import com.salk.mycircadianclock.sleep.SleepEntries;
import com.salk.mycircadianclock.wheel.WheelScroller;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PendingItemActivity extends AppCompatActivity {

    private RelativeLayout tabbar, rel_main;
    private RecyclerView recycle_pending_list;

    private DatabaseRepo databaseRepo;
    private ArrayList<PendingCount> pendingCountArrayList = new ArrayList<>();
    private ArrayList<FoodStuff> foodStuffArrayList = new ArrayList<>();
    private ArrayList<SleepEntries> sleepEntriesArrayList = new ArrayList<>();
    private ArrayList<ExerciseEntries> exerciseEntriesArrayList = new ArrayList<>();
    private ArrayList<HealthSaveRequest> healthSaveRequestArrayList = new ArrayList<>();
    private String user_id = "", research_info_id = "", auth_token = "";
    private FoodStuff foodStuff;
    private SleepEntries sleepEntries;
    private ExerciseEntries exerciseEntries;
    private HealthSaveRequest healthSaveRequest;
    private Dialog list_dialog;
    private SharedPreferences sharedPreferences;
    private TinylocalDb tinylocalDb;
    private CommonUsedmethods commonUsedmethods;
    private KProgressHUD kProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            //to make screen full screen
            new CommonUsedmethods().makeActivityFullscreen(PendingItemActivity.this);

            setContentView(R.layout.activity_pending_item);

            init();

            initializecommonclass();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo lifecycle method
    @Override
    protected void onResume() {
        super.onResume();

        try {
            get_data_from_local();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //Todo initialization of all XML views
    private void init(){

        try{

            tabbar = findViewById(R.id.tab_bar);
            rel_main = findViewById(R.id.main);
            recycle_pending_list = findViewById(R.id.pending_recycle);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo initialization of common classes
    private void initializecommonclass(){

        try{

            new TabbarClick().click(PendingItemActivity.this, tabbar, rel_main, "pending");
            databaseRepo = new DatabaseRepo(PendingItemActivity.this);
            tinylocalDb = new TinylocalDb();
            commonUsedmethods = new CommonUsedmethods();
            sharedPreferences = tinylocalDb.get_shared_pref(PendingItemActivity.this);
            user_id = tinylocalDb.get_data_in_shared(sharedPreferences,"user_id");
            research_info_id = tinylocalDb.get_data_in_shared(sharedPreferences,"research_info_id");
            auth_token = tinylocalDb.get_data_in_shared(sharedPreferences,"auth_token");
            kProgressHUD = commonUsedmethods.show_progerssDialog(PendingItemActivity.this,
                                getResources().getString(R.string.Synchronizing_data_with_server),false);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo fetch pending count data from local
    private void get_data_from_local(){

        try{

            pendingCountArrayList = new ArrayList<>();

           databaseRepo.get_Pending_count(new OnDataFetched() {
                @Override
                public void data(Object object, int i) {
                }

                @Override
                public void data(Object object) {

                    ArrayList<Long> arrayList = (ArrayList<Long>) object;

                    pendingCountArrayList.add(new PendingCount(getResources().getString(R.string.My_Food),arrayList.get(0)));
                    pendingCountArrayList.add(new PendingCount(getResources().getString(R.string.Sleep_Analysis_pending),arrayList.get(1)));
                    pendingCountArrayList.add(new PendingCount(getResources().getString(R.string.Exercise),arrayList.get(2)));
                    pendingCountArrayList.add(new PendingCount(getResources().getString(R.string.health),arrayList.get(3)));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            set_data_in_list();
                        }
                    });
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo set pending data count in list
    private void set_data_in_list(){

        try {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PendingItemActivity.this);
            recycle_pending_list.setLayoutManager(linearLayoutManager);
            PendingItemAdapter pendingItemAdapter = new PendingItemAdapter(pendingCountArrayList,
                    PendingItemActivity.this, new OnRecycleItemClicks() {
                @Override
                public void onClick(Object object) {

                    PendingCount pendingCount = (PendingCount) object;
                    if(pendingCount.getCount()>0) {
                        get_pending_item_detail(pendingCount.getFoodName());
                    }

                }
            });
            recycle_pending_list.setAdapter(pendingItemAdapter);
            pendingItemAdapter.notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo fetch pending item detail
    private void get_pending_item_detail(String food_name){

        try{

        if(food_name.equalsIgnoreCase((getResources().getString(R.string.My_Food)))){

            databaseRepo.get_Pending_food(new OnDataFetched() {
                @Override
                public void data(Object object, int i) {

                }

                @Override
                public void data(final Object object) {

                    if(object!=null){

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                show_pending_detail(object,"food");
                            }
                        });
                    }

                }
            });
        }else if(food_name.equalsIgnoreCase((getResources().getString(R.string.Sleep_Analysis_pending)))){

                databaseRepo.get_Pending_sleep(new OnDataFetched() {
                    @Override
                    public void data(Object object, int i) {

                    }

                    @Override
                    public void data(final Object object) {

                        if(object!=null){

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    show_pending_detail(object,"sleep");
                                }
                            });
                        }

                    }
                });


        }else if(food_name.equalsIgnoreCase((getResources().getString(R.string.Exercise)))){

                databaseRepo.get_Pending_exercise(new OnDataFetched() {
                    @Override
                    public void data(Object object, int i) {

                    }

                    @Override
                    public void data(final Object object) {

                        if(object!=null){

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    show_pending_detail(object,"exercise");
                                }
                            });
                        }

                    }
                });


        }else if(food_name.equalsIgnoreCase((getResources().getString(R.string.health)))){

                databaseRepo.get_Pending_health(new OnDataFetched() {
                    @Override
                    public void data(Object object, int i) {

                    }

                    @Override
                    public void data(final Object object) {

                        if(object!=null){

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    show_pending_detail(object,"health");
                                }
                            });
                        }

                    }
                });

        }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo show peending item detail in list
    private void show_pending_detail(final Object object, final String type){

        try {
            list_dialog = new Dialog(PendingItemActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            list_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            list_dialog.setContentView(R.layout.pending_list_dialog);

            RecyclerView list_food =  list_dialog.findViewById(R.id.allfoodwithcount);
            Button close_btn = list_dialog.findViewById(R.id.close_btn);
            Button clear_btn = list_dialog.findViewById(R.id.clear_btn);
            Button send_btn = list_dialog.findViewById(R.id.send_btn);


            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PendingItemActivity.this);
            list_food.setLayoutManager(linearLayoutManager);
            PendingListAdapter pendingItemAdapter = new PendingListAdapter(object,
                    PendingItemActivity.this, new OnRecycleItemClicks() {
                @Override
                public void onClick(Object object) {


                }
            },type);
            list_food.setAdapter(pendingItemAdapter);
            pendingItemAdapter.notifyDataSetChanged();

            close_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    list_dialog.dismiss();
                }
            });
            send_btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    getPending_Array(type,object);

                }
            });
            clear_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    clear_from_local(type,object);

                }
            });
            list_dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //Todo fetch pending detail array for sync
    private void getPending_Array(String type,Object object) {

        try {

            if (type.equalsIgnoreCase("food")) {

                foodStuffArrayList = (ArrayList<FoodStuff>) object;

                call_food_api_sync();

            } else if (type.equalsIgnoreCase("sleep")) {
                sleepEntriesArrayList = (ArrayList<SleepEntries>) object;

                call_sleep_api_sync();

            } else if (type.equalsIgnoreCase("health")) {
                healthSaveRequestArrayList = (ArrayList<HealthSaveRequest>) object;

                call_health_api_sync();

            } else if (type.equalsIgnoreCase("exercise")) {

                exerciseEntriesArrayList = (ArrayList<ExerciseEntries>) object;

                call_exercise_api_sync();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo call food api for sync
    private void call_food_api_sync(){

        if(kProgressHUD!=null && !kProgressHUD.isShowing()){
            kProgressHUD.show();
        }
        for (int i=0;i<foodStuffArrayList.size();i++){

            foodStuff = foodStuffArrayList.get(i);
            call_food_save_api(i);
        }
    }

    //Todo call sleep api for sync
    private void call_sleep_api_sync(){

        if(kProgressHUD!=null && !kProgressHUD.isShowing()){
            kProgressHUD.show();
        }
        for (int i=0;i<sleepEntriesArrayList.size();i++){

            sleepEntries = sleepEntriesArrayList.get(i);
            call_sleep_api(i);
        }
    }

    //Todo call exercise api sync
    private void call_exercise_api_sync(){

        if(kProgressHUD!=null && !kProgressHUD.isShowing()){
            kProgressHUD.show();
        }
        for (int i=0;i<exerciseEntriesArrayList.size();i++){

            exerciseEntries = exerciseEntriesArrayList.get(i);
            call_exercise_api(i);
        }
    }

    //Todo call health api for sync
    private void call_health_api_sync(){

        if(kProgressHUD!=null && !kProgressHUD.isShowing()){
            kProgressHUD.show();
        }
        for (int i=0;i<healthSaveRequestArrayList.size();i++){

            healthSaveRequest = healthSaveRequestArrayList.get(i);
            call_health_save_api(i);
        }
    }

    //Todo call food save api
    private void call_food_save_api(final int i){

        try {
            String login_url = ApiConfig.FOOD_SAVE_URL;
            JSONObject jsonObject = make_food_save_request_json();

            new APIManager().Apicall(login_url, jsonObject, new Apicallback() {
                @Override
                public void success(int code, String value) {

                    if (code == 200) {

                        //parse response
                        parse_food_save_response(value, new APIPasrsing() {

                            @Override
                            public void completed() {

                                if(!foodStuffArrayList.get(i).getImage_file_name().equalsIgnoreCase("")){

                                    try {
                                        File file = new File(foodStuffArrayList.get(i).getImage_file_name());

                                        if(file.exists()) {
                                            boolean deleted = file.delete();
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }

                                databaseRepo.deleteFood(databaseRepo.getFood(foodStuffArrayList.get(i).getTimestamp()));
                                update_after_sync(i,"food");

                            }

                            @Override
                            public void error() {

                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(list_dialog!=null && !isDestroyed()){

                                    kProgressHUD.dismiss();
                                    list_dialog.dismiss();
                                }
                            }
                        });

                    }
                }

                @Override
                public void failure(String value) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(list_dialog!=null && !isDestroyed()){

                                kProgressHUD.dismiss();
                                list_dialog.dismiss();
                            }
                        }
                    });

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo make food save request for json
    private JSONObject make_food_save_request_json(){

        JSONObject json = new JSONObject();

        try {

            json.put("participant_id", AESEncryption.encrypt(user_id));
            json.put("research_info_id", AESEncryption.encrypt(research_info_id));
            json.put("food_description_file",AESEncryption.encrypt(foodStuff.getFood_description()));
            json.put("food_location_file",AESEncryption.encrypt(foodStuff.getFood_location()));

            if(!foodStuff.getImage_file_name().equalsIgnoreCase("")) {

                json.put("food_camerainfo_file", AESEncryption.encrypt(foodStuff.getFood_camerainfo()));
                json.put("food_image_file", AESEncryption.encrypt(foodStuff.getFood_image()));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    //Todo parse food save response for json
    private void parse_food_save_response(String response,APIPasrsing apiPasrsing){

        try {
            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject.has("message") && !jsonObject.isNull("message")) {
                String status = jsonObject.getString("message");

                if(status.equalsIgnoreCase("success")){
                    apiPasrsing.completed();
                }else{
                    apiPasrsing.error();
                }

            }else{

                apiPasrsing.error();
            }

        }catch (Exception e){
            apiPasrsing.error();
            e.printStackTrace();
        }
    }

    //Todo call sleep save api
    private void call_sleep_api(final int i) {

        try {
            String login_url = ApiConfig.SLEEP_SAVE_URL;
            JSONObject jsonObject = make_sleep_save_request_json();

            new APIManager().Apicall(auth_token, login_url, jsonObject, new Apicallback() {
                @Override
                public void success(int code, String value) {

                    if (code == 200) {

                        //parse response
                        parse_food_save_response(value, new APIPasrsing() {

                            @Override
                            public void completed() {

                                databaseRepo.deleteSleep(databaseRepo.getSleep(sleepEntriesArrayList.get(i).getTimestamp()));
                                update_after_sync(i,"sleep");

                            }

                            @Override
                            public void error() {

                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(list_dialog!=null && !isDestroyed()){

                                    kProgressHUD.dismiss();
                                    list_dialog.dismiss();
                                }
                            }
                        });
                    }
                }

                @Override
                public void failure(final String value) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(list_dialog!=null && !isDestroyed()){

                                kProgressHUD.dismiss();
                                list_dialog.dismiss();
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo make food save request for json
    private JSONObject make_sleep_save_request_json() {

        JSONObject json = new JSONObject();

        try {
            try {
                json.put("participant_id", AESEncryption.encrypt(user_id));
                json.put("research_info_id", AESEncryption.encrypt(research_info_id));
                json.put("sleep_data", AESEncryption.encrypt(sleepEntries.getSleep_details()));

                if(!sleepEntries.getPrevious_sleep_entries().equalsIgnoreCase("")){
                    json.put("prev_sleep_data",AESEncryption.encrypt(sleepEntries.getPrevious_sleep_entries()));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }


    //Todo call exercise save api
    private void call_exercise_api(final int i) {

        try {
            String login_url = ApiConfig.EXERCISE_SAVE_URL;
            JSONObject jsonObject = make_json_for_exercise_save_request();

            new APIManager().Apicall(auth_token, login_url, jsonObject, new Apicallback() {
                @Override
                public void success(int code, String value) {

                    if (code == 200) {

                        //parse response
                        parse_food_save_response(value, new APIPasrsing() {

                            @Override
                            public void completed() {
                                databaseRepo.deleteExercise(databaseRepo.getExercise(exerciseEntriesArrayList.get(i).getTimestamp()));
                                update_after_sync(i,"exercise");
                            }

                            @Override
                            public void error() {

                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(list_dialog!=null && !isDestroyed()){

                                    kProgressHUD.dismiss();
                                    list_dialog.dismiss();
                                }
                            }
                        });
                    }
                }

                @Override
                public void failure(final String value) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(list_dialog!=null && !isDestroyed()){

                                kProgressHUD.dismiss();
                                list_dialog.dismiss();
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo make json for exercise save request
    public JSONObject make_json_for_exercise_save_request() {

        JSONObject json = new JSONObject();
        try {
            json.put("participant_id", AESEncryption.encrypt(user_id));
            json.put("research_info_id",  AESEncryption.encrypt(research_info_id));
            json.put("exercise_data", AESEncryption.encrypt(exerciseEntries.getExercise_details()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }


    //Todo call health save api
    private void call_health_save_api(final int i) {

        try {
            String login_url = ApiConfig.HEALTH_SAVE_URL;
            JSONObject jsonObject = make_json_for_health_save_request();

            new APIManager().Apicall(auth_token, login_url, jsonObject, new Apicallback() {
                @Override
                public void success(int code, String value) {

                    if (code == 200) {

                        //parse response
                        parse_food_save_response(value, new APIPasrsing() {

                            @Override
                            public void completed() {

                                databaseRepo.deleteHealthRequest(databaseRepo.getHealthRequest(
                                        healthSaveRequestArrayList.get(i).getTimestamp()));
                                update_after_sync(i,"health");

                            }

                            @Override
                            public void error() {
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(list_dialog!=null && !isDestroyed()){

                                    kProgressHUD.dismiss();
                                    list_dialog.dismiss();
                                }
                            }
                        });
                    }
                }

                @Override
                public void failure(final String value) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(list_dialog!=null && !isDestroyed()){

                                kProgressHUD.dismiss();
                                list_dialog.dismiss();
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo to make JSON file for health save API request
    public JSONObject make_json_for_health_save_request() {

        JSONObject json = new JSONObject();
        try {
            json.put("participant_id", AESEncryption.encrypt(user_id));
            json.put("research_info_id",  AESEncryption.encrypt(research_info_id));
            json.put("health_data", AESEncryption.encrypt(healthSaveRequest.getHealth_description()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    //Todo update ui after sync
    private void update_after_sync(int i,String type){

        int size =0;

        if(type.equalsIgnoreCase("food")){
            size = foodStuffArrayList.size()-1;
        }else if(type.equalsIgnoreCase("sleep")){
            size = sleepEntriesArrayList.size()-1;
        }else if(type.equalsIgnoreCase("health")){
            size = healthSaveRequestArrayList.size()-1;
        }else if(type.equalsIgnoreCase("exercise")){
            size = exerciseEntriesArrayList.size()-1;
        }

        if(i == size){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(list_dialog!=null && !isDestroyed()){

                        kProgressHUD.dismiss();
                        list_dialog.dismiss();
                        get_data_from_local();
                    }
                }
            });
        }
    }

    //Todo update ui after clear data
    private void update_after_sync(){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(list_dialog!=null && !isDestroyed()){

                        kProgressHUD.dismiss();
                        list_dialog.dismiss();
                        get_data_from_local();
                    }
                }
            });

    }

    //Todo to clear data from local
    private void clear_from_local(String type,Object object){
        try {

            if (type.equalsIgnoreCase("food")) {

                foodStuffArrayList = (ArrayList<FoodStuff>) object;

                for (int i =0;i<foodStuffArrayList.size();i++){

                    databaseRepo.deleteFood(foodStuffArrayList.get(i));
                    databaseRepo.fetch_delete_FoodSleepExdata(foodStuffArrayList.get(i).getTimestamp(),
                            "food");
                }

                update_after_sync();

            } else if (type.equalsIgnoreCase("sleep")) {
                sleepEntriesArrayList = (ArrayList<SleepEntries>) object;

                for (int i =0;i<sleepEntriesArrayList.size();i++){

                    databaseRepo.deleteSleep(sleepEntriesArrayList.get(i));
                    databaseRepo.fetch_delete_FoodSleepExdata(sleepEntriesArrayList.get(i).getTimestamp(),
                            "sleep");
                }

                update_after_sync();

            } else if (type.equalsIgnoreCase("health")) {
                healthSaveRequestArrayList = (ArrayList<HealthSaveRequest>) object;

                for (int i =0;i<healthSaveRequestArrayList.size();i++){

                    databaseRepo.deleteHealthRequest(healthSaveRequestArrayList.get(i));
                    databaseRepo.fetch_delete_healthdata(healthSaveRequestArrayList.get(i).getTimestamp());
                }

                update_after_sync();

            } else if (type.equalsIgnoreCase("exercise")) {

                exerciseEntriesArrayList = (ArrayList<ExerciseEntries>) object;

                for (int i =0;i<exerciseEntriesArrayList.size();i++){

                    databaseRepo.deleteExercise(exerciseEntriesArrayList.get(i));
                    databaseRepo.fetch_delete_FoodSleepExdata(exerciseEntriesArrayList.get(i).getTimestamp(),
                            "exercise");
                }

                update_after_sync();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
