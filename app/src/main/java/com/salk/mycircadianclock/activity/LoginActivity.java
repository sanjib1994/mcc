package com.salk.mycircadianclock.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.Utility.ConnectionDetector;
import com.salk.mycircadianclock.Utility.TinylocalDb;
import com.salk.mycircadianclock.api.AESEncryption;
import com.salk.mycircadianclock.api.APIManager;
import com.salk.mycircadianclock.api.APIPasrsing;
import com.salk.mycircadianclock.api.ApiConfig;
import com.salk.mycircadianclock.api.Apicallback;
import com.salk.mycircadianclock.camera.CustomCameraActivity;
import com.salk.mycircadianclock.localDatabase.DatabaseRepo;
import com.salk.mycircadianclock.settings.SettingsTable;
import com.salk.mycircadianclock.settings.local_notification.ModelLocalNotification;
import com.salk.mycircadianclock.settings.local_notification.SetLocalNotification;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity {

    //view declaration
    private ImageView user_icon;
    private Button button_login;
    private EditText et_TextEmail,et_accesscode;
    private TextView textView_web_link,txt_fogot_password;

    //variable declaration
    private String language = "";

    //email pattern
    private String EMAIL_PATTERN ="^[A-Za-z0-9,!#\\$%&'\\*\\+/=\\?\\^_`\\{\\|}~-]+(\\.[A-Za-z0-9,!#\\$%&'\\*\\+/=\\?\\^_`\\{\\|}~-]+)*@[A-Za-z0-9,!#\\$%&'\\*\\+/=\\?\\^_`\\{\\|}~-]+(\\.[A-Za-z0-9-]+)*\\.([A-Za-z]{2,})$";
    private ConnectionDetector connectionDetector;
    private TinylocalDb tinylocalDb;
    private SharedPreferences.Editor editor;
    private KProgressHUD kProgressHUD;
    private String token ="",user_id ="",research_info_id ="",baseline_start = "",
            baseline_end = "",intervention_start = "",intervention_end =" ";
    private DatabaseRepo databaseRepo;
    private CommonUsedmethods commonUsedmethods;
    private Long base_end =0L, intenvention_start_ = 0L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            //to make luncher screen full screen
            new CommonUsedmethods().makeActivityFullscreen(LoginActivity.this);

            setContentView(R.layout.activity_login);

            //initialization of all view
            init();

            //click function for required views
            click_function();

            //set image according to language
            set_user_login_image();

            storedata_local_notifications();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //get reference for all view
    private void init(){

        try {
            tinylocalDb = new TinylocalDb();
            editor = tinylocalDb.create_shared_pref(LoginActivity.this);
            connectionDetector = new ConnectionDetector(LoginActivity.this);
            databaseRepo = new DatabaseRepo(LoginActivity.this);
            commonUsedmethods = new CommonUsedmethods();

            user_icon = findViewById(R.id.user_icon);
            button_login = findViewById(R.id.buttonOk);
            et_TextEmail = findViewById(R.id.editTextEmail);
            et_accesscode = findViewById(R.id.editTextAccessCode);
            textView_web_link = findViewById(R.id.textView_L);
            txt_fogot_password = findViewById(R.id.Forget_txt);

            kProgressHUD = new CommonUsedmethods().show_progerssDialog(LoginActivity.this,"Please wait",false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //put click function on views
    private void click_function(){

        try {
            //click on login button
            button_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    check_login_validation(et_TextEmail.getText().toString(), et_accesscode.getText().toString());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    //set image and web url according default language either english or french
    private void set_user_login_image(){
        try {
            language = Locale.getDefault().getDisplayLanguage();

            if (!language.equalsIgnoreCase("")) {
                if (language.matches("français")) {
                    user_icon.setImageResource(R.mipmap.login_french_image);
                    textView_web_link.setText(Html.fromHtml("<a href='www.mycircadianclock.org'> www.swisschronofood.ch </a>"));
                } else {
                    user_icon.setImageResource(R.mipmap.user_icon_new);
                    textView_web_link.setText(Html.fromHtml("<a href='www.mycircadianclock.org'> www.mycircadianclock.org </a>"));
                }
            }else{
                user_icon.setImageResource(R.mipmap.user_icon_new);
                textView_web_link.setText(Html.fromHtml("<a href='www.mycircadianclock.org'> www.mycircadianclock.org </a>"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //check validation before caling login api
    private void check_login_validation(String email,String accesscode){

        try {
            //variable to check wheather validation pass/fail
            boolean check = false;

            if (email.trim().length() == 0) {

                check = true;
                et_TextEmail.setError(getResources().getString(R.string.Email_not_empty));
            } else if (!isValidEmail(email)) {

                check = true;
                et_TextEmail.setError(getResources().getString(R.string.Email_not_proper_format));
            }
            if (accesscode.trim().length() == 0) {
                check = true;
                et_accesscode.setError(getResources().getString(R.string.Enter_access_code));
            }

            if (!check) {

                //checking for internet availiability
                if (connectionDetector != null && connectionDetector.isConnectingToInternet()) {
                    //call login api

                    if(kProgressHUD!=null){
                        kProgressHUD.show();
                    }
                    call_login_api(email, accesscode);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Function to check wheather email is valid or not
    public boolean isValidEmail(String Email) {

        boolean val = false;
        try {
            Pattern p = Pattern.compile(EMAIL_PATTERN);
            Matcher m = p.matcher(Email);

            if (m.matches()) {
                val = true;
            } else {
                val = false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return val;
    }

    //call login request
    private void call_login_api(String email,String accesscode){

        try {
            String login_url = ApiConfig.LOGIN_URL;
            JSONObject jsonObject = make_login_request_json(email, accesscode);

            new APIManager().Apicall(login_url, jsonObject, new Apicallback() {
                @Override
                public void success(int code, String value) {

                    if (code == 200) {

                        //parse response
                        parse_login_response(value, new APIPasrsing() {

                            @Override
                            public void completed() {

                                call_settings_api();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        //store data in shared preference
                                        tinylocalDb.put_data_in_shared(editor,"isLogin","true");
                                        check_user_type();


                                    }
                                });


                            }

                            @Override
                            public void error() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if(kProgressHUD.isShowing()){
                                            kProgressHUD.dismiss();
                                        }
                                        Toast.makeText(LoginActivity.this,"Something went wrong.Please try after some time.",Toast.LENGTH_LONG).show();

                                    }
                                });


                            }
                        });
                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(kProgressHUD.isShowing()){
                                    kProgressHUD.dismiss();
                                }
                                Toast.makeText(LoginActivity.this,R.string.Empty_fields,Toast.LENGTH_LONG).show();

                            }
                        });


                    }
                }

                @Override
                public void failure(final String value) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(kProgressHUD.isShowing()){
                                kProgressHUD.dismiss();
                            }
                            Toast.makeText(LoginActivity.this, value, Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //make login request for json
    private JSONObject make_login_request_json(String email,String accesscode){

        JSONObject json = new JSONObject();

        try {
            json.put("username", AESEncryption.encrypt(email));
            json.put("password", AESEncryption.encrypt(accesscode));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    //parse login response for json
    private void parse_login_response(String response,APIPasrsing apiPasrsing){


        try {
            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject.has("message") && !jsonObject.isNull("message")) {
                JSONObject jsonObject1 = jsonObject.getJSONObject("message");

                if(jsonObject1.has("token") && !jsonObject1.isNull("token")){
                    token = AESEncryption.decrypt(jsonObject1.getString("token"));

                    tinylocalDb.put_data_in_shared(editor,"auth_token",token);
                }

                if(jsonObject1.has("user_id") && !jsonObject1.isNull("user_id")){
                    user_id = AESEncryption.decrypt(jsonObject1.getString("user_id"));

                    tinylocalDb.put_data_in_shared(editor,"user_id",user_id);

                }

                if(jsonObject1.has("research_info_id") && !jsonObject1.isNull("research_info_id")){
                    research_info_id = AESEncryption.decrypt(jsonObject1.getString("research_info_id"));

                    tinylocalDb.put_data_in_shared(editor,"research_info_id",research_info_id);

                }else{
                    tinylocalDb.put_data_in_shared(editor,"research_info_id","1");
                }

                if(jsonObject1.has("baseline_start") && !jsonObject1.isNull("baseline_start")){
                    baseline_start = AESEncryption.decrypt(jsonObject1.getString("baseline_start"));

                    tinylocalDb.put_data_in_shared(editor,"baseline_start",
                            commonUsedmethods.convert_date_to_timestamp("yyyy-MM-dd HH:mm:ss.SSSSSSZ",baseline_start).toString());

                }

                if(jsonObject1.has("baseline_end") && !jsonObject1.isNull("baseline_end")){
                    baseline_end = AESEncryption.decrypt(jsonObject1.getString("baseline_end"));

                    base_end =  commonUsedmethods.convert_date_to_timestamp("yyyy-MM-dd HH:mm:ss.SSSSSSZ",baseline_end);
                    tinylocalDb.put_data_in_shared(editor,"baseline_end",
                            commonUsedmethods.convert_date_to_timestamp("yyyy-MM-dd HH:mm:ss.SSSSSSZ",baseline_end).toString());

                }

                if(jsonObject1.has("intervention_start") && !jsonObject1.isNull("intervention_start")){
                    intervention_start = AESEncryption.decrypt(jsonObject1.getString("intervention_start"));

                    intenvention_start_ = commonUsedmethods.convert_date_to_timestamp("yyyy-MM-dd HH:mm:ss.SSSSSSZ",intervention_start);
                    tinylocalDb.put_data_in_shared(editor,"intervention_start",
                            commonUsedmethods.convert_date_to_timestamp("yyyy-MM-dd HH:mm:ss.SSSSSSZ",intervention_start).toString());

                }
                if(jsonObject1.has("intervention_end") && !jsonObject1.isNull("intervention_end")){
                    intervention_end = AESEncryption.decrypt(jsonObject1.getString("intervention_end"));

                    tinylocalDb.put_data_in_shared(editor,"intervention_end",
                            commonUsedmethods.convert_date_to_timestamp("yyyy-MM-dd HH:mm:ss.SSSSSSZ",intervention_end).toString());

                }


                apiPasrsing.completed();
            }else{

                apiPasrsing.error();
            }

        }catch (Exception e){
            apiPasrsing.error();
            e.printStackTrace();
        }
    }

    //send to home screen
    private void send_to_tutorial_screen(){

        try {
            Intent intent = new Intent(LoginActivity.this, TutorialActivity.class);
            intent.putExtra("login",true);
            startActivity(intent);
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo call settings API
    private void call_settings_api(){

        try {
            String login_url = ApiConfig.FETCH_PARTICIPANT_SETTINGS;
            JSONObject jsonObject = make_settings_request_json(user_id);

            new APIManager().Apicall(token,login_url, jsonObject, new Apicallback() {
                @Override
                public void success(int code, String value) {

                    if (code == 200) {

                        //parse response
                        parse_settings_response(value, new APIPasrsing() {

                            @Override
                            public void completed() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if(kProgressHUD.isShowing()){
                                            kProgressHUD.dismiss();
                                        }
                                        send_to_tutorial_screen();
                                    }
                                });


                            }

                            @Override
                            public void error() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if(kProgressHUD.isShowing()){
                                            kProgressHUD.dismiss();
                                        }
                                        Toast.makeText(LoginActivity.this,"Something went wrong.Please try after some time.",Toast.LENGTH_LONG).show();

                                    }
                                });


                            }
                        });
                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(kProgressHUD.isShowing()){
                                    kProgressHUD.dismiss();
                                }
                                Toast.makeText(LoginActivity.this,R.string.Empty_fields,Toast.LENGTH_LONG).show();

                            }
                        });


                    }
                }

                @Override
                public void failure(final String value) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(kProgressHUD.isShowing()){
                                kProgressHUD.dismiss();
                            }
                            Toast.makeText(LoginActivity.this, value, Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Todo make json request for settings API call
    private JSONObject make_settings_request_json(String user_id){

        JSONObject json = new JSONObject();
        try {

            json.put("participant_id", AESEncryption.encrypt(user_id));
            json.put("research_info_id", AESEncryption.encrypt("1"));


        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    //Todo parse Settings response
    private void parse_settings_response(String response,APIPasrsing apiPasrsing){


        try {
            JSONObject jsonObject = new JSONObject(response);

            SettingsTable settingsTable = new SettingsTable();

            if(jsonObject.has("message") && !jsonObject.isNull("message")) {

                JSONObject jsonObject1 = jsonObject.getJSONObject("message");

                if(jsonObject1!=null){

                    if(jsonObject1.has("is_begin_fast_active") && !jsonObject1.isNull("is_begin_fast_active")){

                        String is_begin_fast_active = AESEncryption.decrypt(jsonObject1.getString("is_begin_fast_active"));

                        if(is_begin_fast_active!=null && !is_begin_fast_active.equalsIgnoreCase("")){

                            settingsTable.setIs_begin_fast_active(is_begin_fast_active);
                        }

                    }

                    if(jsonObject1.has("is_fast_complete_active") && !jsonObject1.isNull("is_fast_complete_active")){

                        String is_fast_complete_active = AESEncryption.decrypt(jsonObject1.getString("is_fast_complete_active"));

                        if(is_fast_complete_active!=null && !is_fast_complete_active.equalsIgnoreCase("")){

                            settingsTable.setIs_fast_complete_active(is_fast_complete_active);
                        }

                    }


                    if(jsonObject1.has("reminder_hr") && !jsonObject1.isNull("reminder_hr")){

                        String reminder_hr = AESEncryption.decrypt(jsonObject1.getString("reminder_hr"));

                        if(reminder_hr!=null && !reminder_hr.equalsIgnoreCase("")){

                            settingsTable.setReminder_hr(reminder_hr);
                        }

                    }


                    if(jsonObject1.has("is_reminder_active") && !jsonObject1.isNull("is_reminder_active")){

                        String is_reminder_active = AESEncryption.decrypt(jsonObject1.getString("is_reminder_active"));

                        if(is_reminder_active!=null && !is_reminder_active.equalsIgnoreCase("")){

                            settingsTable.setIs_reminder_active(is_reminder_active);
                        }

                    }

                    if(jsonObject1.has("is_sleep_duration_active") && !jsonObject1.isNull("is_sleep_duration_active")){

                        String is_sleep_duration_active = AESEncryption.decrypt(jsonObject1.getString("is_sleep_duration_active"));

                        if(is_sleep_duration_active!=null && !is_sleep_duration_active.equalsIgnoreCase("")){

                            settingsTable.setIs_sleep_duration_active(is_sleep_duration_active);
                        }

                    }

                    if(jsonObject1.has("sleep_duration") && !jsonObject1.isNull("sleep_duration")){

                        String sleep_duration = AESEncryption.decrypt(jsonObject1.getString("sleep_duration"));

                        if(sleep_duration!=null && !sleep_duration.equalsIgnoreCase("")){

                            settingsTable.setSleep_duration(sleep_duration);
                        }

                    }


                    if(jsonObject1.has("sleep_start_time") && !jsonObject1.isNull("sleep_start_time")){

                        String sleep_start_time = AESEncryption.decrypt(jsonObject1.getString("sleep_start_time"));

                        if(sleep_start_time!=null && !sleep_start_time.equalsIgnoreCase("")){

                            settingsTable.setSleep_start_time(sleep_start_time);
                        }

                    }

                    if(jsonObject1.has("eating_window_target_start") && !jsonObject1.isNull("eating_window_target_start")){

                        String eating_window_target_start = AESEncryption.decrypt(jsonObject1.getString("eating_window_target_start"));

                        if(eating_window_target_start!=null && !eating_window_target_start.equalsIgnoreCase("")){

                            settingsTable.setEating_window_target_start(eating_window_target_start);
                        }

                    }

                    if(jsonObject1.has("eating_window_target_end") && !jsonObject1.isNull("eating_window_target_end")){

                        String eating_window_target_end = AESEncryption.decrypt(jsonObject1.getString("eating_window_target_end"));

                        if(eating_window_target_end!=null && !eating_window_target_end.equalsIgnoreCase("")){

                            settingsTable.setEating_window_target_end(eating_window_target_end);
                        }

                    }

                    if(jsonObject1.has("is_eating_window_target_active") && !jsonObject1.isNull("is_eating_window_target_active")){

                        String is_eating_window_target_active = AESEncryption.decrypt(jsonObject1.getString("is_eating_window_target_active"));

                        if(is_eating_window_target_active!=null && !is_eating_window_target_active.equalsIgnoreCase("")){

                            settingsTable.setIs_eating_window_target_active(is_eating_window_target_active);
                        }

                    }

                    if(jsonObject1.has("activity_target_count_step") && !jsonObject1.isNull("activity_target_count_step")){

                        String activity_target_count_step = AESEncryption.decrypt(jsonObject1.getString("activity_target_count_step"));

                        if(activity_target_count_step!=null && !activity_target_count_step.equalsIgnoreCase("")){

                            settingsTable.setActivity_target_count_step(activity_target_count_step);
                        }

                    }

                    if(jsonObject1.has("is_activity_target_step_active") && !jsonObject1.isNull("is_activity_target_step_active")){

                        String is_activity_target_step_active = AESEncryption.decrypt(jsonObject1.getString("is_activity_target_step_active"));

                        if(is_activity_target_step_active!=null && !is_activity_target_step_active.equalsIgnoreCase("")){

                            settingsTable.setIs_activity_target_step_active(is_activity_target_step_active);
                        }

                    }

                    if(jsonObject1.has("is_time_till_fast_active") && !jsonObject1.isNull("is_time_till_fast_active")){

                        String is_time_till_fast_active = AESEncryption.decrypt(jsonObject1.getString("is_time_till_fast_active"));

                        if(is_time_till_fast_active!=null && !is_time_till_fast_active.equalsIgnoreCase("")){

                            settingsTable.setIs_time_till_fast_active(is_time_till_fast_active);
                        }

                    }

                    if(jsonObject1.has("is_time_since_food_active") && !jsonObject1.isNull("is_time_since_food_active")){

                        String is_time_since_food_active = AESEncryption.decrypt(jsonObject1.getString("is_time_since_food_active"));

                        if(is_time_since_food_active!=null && !is_time_since_food_active.equalsIgnoreCase("")){

                            settingsTable.setIs_time_since_food_active(is_time_since_food_active);
                        }

                    }

                    if(jsonObject1.has("is_days_exercise_active") && !jsonObject1.isNull("is_days_exercise_active")){

                        String is_days_exercise_active = AESEncryption.decrypt(jsonObject1.getString("is_days_exercise_active"));

                        if(is_days_exercise_active!=null && !is_days_exercise_active.equalsIgnoreCase("")){

                            settingsTable.setIs_days_exercise_active(is_days_exercise_active);
                        }

                    }

                    if(jsonObject1.has("is_steps_active") && !jsonObject1.isNull("is_steps_active")){

                        String is_steps_active = AESEncryption.decrypt(jsonObject1.getString("is_steps_active"));

                        if(is_steps_active!=null && !is_steps_active.equalsIgnoreCase("")){

                            settingsTable.setIs_steps_active(is_steps_active);
                        }

                    }

                    if(jsonObject1.has("is_hours_sleep_active") && !jsonObject1.isNull("is_hours_sleep_active")){

                        String is_hours_sleep_active = AESEncryption.decrypt(jsonObject1.getString("is_hours_sleep_active"));

                        if(is_hours_sleep_active!=null && !is_hours_sleep_active.equalsIgnoreCase("")){

                            settingsTable.setIs_hours_sleep_active(is_hours_sleep_active);
                        }

                    }

                    if(jsonObject1.has("is_display_medicine_active") && !jsonObject1.isNull("is_display_medicine_active")){

                        String is_display_medicine_active = AESEncryption.decrypt(jsonObject1.getString("is_display_medicine_active"));

                        if(is_display_medicine_active!=null && !is_display_medicine_active.equalsIgnoreCase("")){

                            settingsTable.setIs_display_medicine_active(is_display_medicine_active);
                        }

                    }

                    databaseRepo.insertSettingsData(settingsTable);

                }else{

                    databaseRepo.insertSettingsData(settingsTable);
                }


                apiPasrsing.completed();
            }else{

                apiPasrsing.error();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo to check weather user is under baseline or intervention
    private void check_user_type(){

        if(baseline_start.equalsIgnoreCase("")){
            tinylocalDb.put_data_in_shared(editor,"user_type","baseline_not_started");
        }else{

            if(System.currentTimeMillis()>=intenvention_start_){
                tinylocalDb.put_data_in_shared(editor,"user_type","intervention");
            }else{
                tinylocalDb.put_data_in_shared(editor,"user_type","baseline");
            }
        }
    }

    private void storedata_local_notifications(){

        ArrayList<ModelLocalNotification> arrayList = new ArrayList<>();

        ModelLocalNotification modelLocalNotification = new ModelLocalNotification();
        modelLocalNotification.setNotification_type("Sleep");
        modelLocalNotification.setNotification_message("You have not logged your sleep for some days");
        modelLocalNotification.setFrequency(2);
        modelLocalNotification.setThreshold(2);
        modelLocalNotification.setIs_notify(true);
        arrayList.add(modelLocalNotification);

        modelLocalNotification = new ModelLocalNotification();
        modelLocalNotification.setNotification_type("Food");
        modelLocalNotification.setNotification_message("You have not logged your food for some days");
        modelLocalNotification.setFrequency(2);
        modelLocalNotification.setThreshold(2);
        modelLocalNotification.setIs_notify(true);

        arrayList.add(modelLocalNotification);

        modelLocalNotification = new ModelLocalNotification();
        modelLocalNotification.setNotification_type("Exercise");
        modelLocalNotification.setNotification_message("You have not logged your exercise for some days");
        modelLocalNotification.setFrequency(2);
        modelLocalNotification.setThreshold(2);
        modelLocalNotification.setIs_notify(true);

        arrayList.add(modelLocalNotification);

        modelLocalNotification = new ModelLocalNotification();
        modelLocalNotification.setNotification_type("Medicine");
        modelLocalNotification.setNotification_message("You have not logged your medicine for some days");
        modelLocalNotification.setFrequency(2);
        modelLocalNotification.setThreshold(2);
        modelLocalNotification.setIs_notify(true);

        arrayList.add(modelLocalNotification);


        databaseRepo.insert_local_noptifications(arrayList);

        for(int i=0;i<arrayList.size();i++){

            new SetLocalNotification().set_local_notification(LoginActivity.this,arrayList.get(i).getNotification_message(),
                    arrayList.get(i).getNotification_type(),String.valueOf(arrayList.get(i).getThreshold()),
                    String.valueOf(arrayList.get(i).getFrequency()));
        }

    }

}
