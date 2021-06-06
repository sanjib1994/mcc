package com.salk.mycircadianclock.food;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.nex3z.flowlayout.FlowLayout;
import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.Utility.CustomComparator;
import com.salk.mycircadianclock.Utility.DatePickerCommonFood;
import com.salk.mycircadianclock.Utility.GPSTracker;
import com.salk.mycircadianclock.Utility.TabbarClick;
import com.salk.mycircadianclock.Utility.TinylocalDb;
import com.salk.mycircadianclock.api.AESEncryption;
import com.salk.mycircadianclock.api.APIManager;
import com.salk.mycircadianclock.api.APIPasrsing;
import com.salk.mycircadianclock.api.ApiConfig;
import com.salk.mycircadianclock.api.Apicallback;
import com.salk.mycircadianclock.imageCompression.CompressImage;
import com.salk.mycircadianclock.imageCompression.OnCompressComplted;
import com.salk.mycircadianclock.localDatabase.FoodSleepExData;
import com.salk.mycircadianclock.localDatabase.DatabaseRepo;
import com.salk.mycircadianclock.localDatabase.OnDataFetched;

import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class FoodSendActivity extends AppCompatActivity {

    //Todo Intilalization of XML views
    private CircleImageView image_preview;
    private MultiAutoCompleteTextView addfoodname_common_food;
    private ImageView plusbutton,now_button,ten_button,thirty_button,one_button,two_button;
    private TextView food_exclusive_btn,beverage_exclusive_btn,water_exclusive_btn,medicine_exclusive_btn;
    private FlowLayout lay_foods,lay_send_food;
    private RelativeLayout showthefoodpart,timeloglay,rel_dateTime,tabbar,rel_main;
    private Button btn_save,btn_tag_later,todaydate;


    //Todo Initialization of Global variables
    private String food_image = "",language ="",food_send_ime ="",food_send_time_24hr = "",
            user_id = "",food_type = "f",location_desc = "",auth_token = "",research_info_id = "";
    private Long food_send_timestamp=0L,tag_later_time=0L,original_request_time =0L;
    private boolean is_tag_later = false;
    private JSONObject camera_desc = null;
    private Geocoder geocoder;
    private List<Address> addresses;
    private SharedPreferences sharedPreferences;
    private ArrayList<String> send_food = new ArrayList<>();
    private ArrayList<String> food_default = new ArrayList<>();
    private ArrayList<String> water_default = new ArrayList<>();
    private ArrayList<String> medicine_default = new ArrayList<>();
    private ArrayList<String> breverage_default = new ArrayList<>();
    private ArrayList<FetchCommonFood> fetchCommonFoodArrayList = new ArrayList<>();
    private CommonUsedmethods commonUsedmethods;
    private Calendar dateandtime;
    private DatabaseRepo databaseRepo;
    private FoodStuff foodStuff;
    private FoodSleepExData foodSleepExData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            //Todo make screen full screen
            new CommonUsedmethods().makeActivityFullscreen(FoodSendActivity.this);

            setContentView(R.layout.activity_food_send);


            //Todo Method to get all reference of views
            init();

            //Todo Method used to ask location permission
            permission();

            //Todo Methos used to put
            click_function();

            fetch_participant_common_food();

            //Todo methos to ge dta from intent
            getData_from_intent();

            //Todo methos to set image
            set_data();

            //Todo methos tp convert image to BAse64 and make json request
            if(!food_image.equalsIgnoreCase("")){

                compress_image();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Todo Method to get all reference of views
    private void init(){

        image_preview = findViewById(R.id.image_preview);
        plusbutton = findViewById(R.id.plusbutton);
        addfoodname_common_food = findViewById(R.id.addfoodname_common_food);
        food_exclusive_btn = findViewById(R.id.food_exclusive_btn);
        beverage_exclusive_btn = findViewById(R.id.beverage_exclusive_btn);
        water_exclusive_btn = findViewById(R.id.water_exclusive_btn);
        medicine_exclusive_btn = findViewById(R.id.medicine_exclusive_btn);
        lay_foods = findViewById(R.id.lay_foods);
        lay_send_food = findViewById(R.id.lay_send_food);
        showthefoodpart = findViewById(R.id.showthefoodpart);
        btn_save = findViewById(R.id.btn_save);
        btn_tag_later = findViewById(R.id.btn_tag_later);
        timeloglay = findViewById(R.id.timeloglay);
        now_button = findViewById(R.id.now_button);
        ten_button = findViewById(R.id.ten_button);
        thirty_button = findViewById(R.id.thirty_button);
        one_button = findViewById(R.id.one_button);
        two_button = findViewById(R.id.two_button);
        rel_dateTime = findViewById(R.id.rel_dateTime);
        tabbar = findViewById(R.id.tab_bar);
        rel_main = findViewById(R.id.main);
        todaydate = findViewById(R.id.todaydate);

        new TabbarClick().click(FoodSendActivity.this,tabbar,rel_main,"food");
        sharedPreferences = new TinylocalDb().get_shared_pref(FoodSendActivity.this);
        user_id = new TinylocalDb().get_data_in_shared(sharedPreferences,"user_id");
        auth_token = new TinylocalDb().get_data_in_shared(sharedPreferences,"auth_token");
        research_info_id =  new TinylocalDb().get_data_in_shared(sharedPreferences,"research_info_id");
        geocoder = new Geocoder(this, Locale.getDefault());
        language = Locale.getDefault().getDisplayLanguage();
        commonUsedmethods = new CommonUsedmethods();
        commonUsedmethods.setAlphaForView(btn_save, 0.35f);
        btn_save.setEnabled(false);
        databaseRepo = new DatabaseRepo(getApplicationContext());

        food_send_ime = commonUsedmethods.parse_date_in_this_format(commonUsedmethods.get_current_date()+" "+
                commonUsedmethods.get_current_time(),"yyyy-MM-dd HH:mm:ss.SSSZ","dd-MM-yyyy hh:mma");
        food_send_time_24hr = commonUsedmethods.parse_date_in_this_format(commonUsedmethods.get_current_time(),"HH:mm","hh:mma");

        food_send_timestamp = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy hh:mma",commonUsedmethods.get_current_date()+" "+
                commonUsedmethods.get_current_time());

    }

    //Todo Methos used to put
    private void click_function(){


        now_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                food_send_ime = commonUsedmethods.parse_date_in_this_format(commonUsedmethods.get_current_date()+" "+
                        commonUsedmethods.get_current_time(),"yyyy-MM-dd HH:mm:ss.SSSZ","dd-MM-yyyy hh:mma");
                food_send_time_24hr = commonUsedmethods.parse_date_in_this_format(commonUsedmethods.get_current_time(),"HH:mm","hh:mma");
                food_send_timestamp = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy hh:mma",commonUsedmethods.get_current_date()+" "+
                        commonUsedmethods.get_current_time());


                set_Image_on_click();

                if ((language.equalsIgnoreCase("français"))){

                    now_button.setImageResource(R.mipmap.now_select_french);
                }else{
                    now_button.setImageResource(R.mipmap.now_selected);
                }

            }
        });

        ten_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                food_send_ime = commonUsedmethods.parse_date_in_this_format(commonUsedmethods.substracttime(10),"yyyy-MM-dd HH:mm:ss.SSSZ","dd-MM-yyyy hh:mma");
                food_send_time_24hr = commonUsedmethods.parse_date_in_this_format(commonUsedmethods.substracttime(10),"HH:mm","dd-MM-yyyy hh:mma");
                food_send_timestamp = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy hh:mma",commonUsedmethods.substracttime(10));

                set_Image_on_click();

                if ((language.equalsIgnoreCase("français"))){

                    ten_button.setImageResource(R.mipmap.ten_min_select_french);
                }else{
                    ten_button.setImageResource(R.mipmap.ten_min_sel);
                }
            }
        });

        thirty_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                food_send_ime = commonUsedmethods.parse_date_in_this_format(commonUsedmethods.substracttime(30),"yyyy-MM-dd HH:mm:ss.SSSZ","dd-MM-yyyy hh:mma");
                food_send_time_24hr = commonUsedmethods.parse_date_in_this_format(commonUsedmethods.substracttime(30),"HH:mm","dd-MM-yyyy hh:mma");
                food_send_timestamp = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy hh:mma",commonUsedmethods.substracttime(30));
                set_Image_on_click();

                if ((language.equalsIgnoreCase("français"))){

                    thirty_button.setImageResource(R.mipmap.thirty_min_select_french);
                }else{
                    thirty_button.setImageResource(R.mipmap.thirty_min_sel);
                }
            }
        });

        one_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                food_send_ime = commonUsedmethods.parse_date_in_this_format(commonUsedmethods.substracttime(1),"yyyy-MM-dd HH:mm:ss.SSSZ","dd-MM-yyyy hh:mma");
                food_send_time_24hr = commonUsedmethods.parse_date_in_this_format(commonUsedmethods.substracttime(1),"HH:mm","dd-MM-yyyy hh:mma");
                food_send_timestamp = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy hh:mma",commonUsedmethods.substracttime(1));

                set_Image_on_click();

                if ((language.equalsIgnoreCase("français"))){

                    one_button.setImageResource(R.mipmap.one_hr_selected_french);
                }else{
                    one_button.setImageResource(R.mipmap.one_hr_sel);
                }
            }
        });

        two_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                food_send_ime = commonUsedmethods.parse_date_in_this_format(commonUsedmethods.substracttime(2),"yyyy-MM-dd HH:mm:ss.SSSZ","dd-MM-yyyy hh:mma");
                food_send_time_24hr = commonUsedmethods.parse_date_in_this_format(commonUsedmethods.substracttime(2),"HH:mm","dd-MM-yyyy hh:mma");
                food_send_timestamp = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy hh:mma",commonUsedmethods.substracttime(2));

                set_Image_on_click();

                if ((language.equalsIgnoreCase("français"))){

                    two_button.setImageResource(R.mipmap.two_hr_selected_french);
                }else{
                    two_button.setImageResource(R.mipmap.two_hr_sel);
                }
            }
        });

        plusbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String clicked_food = addfoodname_common_food.getText().toString().trim();
                addfoodname_common_food.setText("");
                if(clicked_food.length()>0) {

                    if(btn_tag_later.getVisibility() == View.VISIBLE){
                       commonUsedmethods.setAlphaForView(btn_tag_later,0.35f);
                       btn_tag_later.setEnabled(false);
                    }

                    check_for_food_existence(clicked_food);
                    commonUsedmethods.setAlphaForView(btn_save,1.25f);
                    btn_save.setEnabled(true);

                }
                if(clicked_food.length()>0 && !send_food.contains(clicked_food)) {
                    send_food.add(clicked_food);
                    design_send_food_lay(send_food);

                }

            }
        });

        food_exclusive_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!food_type.equalsIgnoreCase("f")){
                    addfoodname_common_food.setText("");
                    send_food = new ArrayList<>();
                    design_send_food_lay(send_food);
                }
                showthefoodpart.setBackgroundResource(R.color.food_bg);
                food_type = "f";
                design_flow_lay(food_default,"");
            }
        });
        beverage_exclusive_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!food_type.equalsIgnoreCase("b")){
                    addfoodname_common_food.setText("");
                    send_food = new ArrayList<>();
                    design_send_food_lay(send_food);
                }
                showthefoodpart.setBackgroundResource(R.color.bev_bg);
                food_type = "b";
                design_flow_lay(breverage_default,"");
            }
        });
        water_exclusive_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!food_type.equalsIgnoreCase("w")){
                    addfoodname_common_food.setText("");
                    send_food = new ArrayList<>();
                    design_send_food_lay(send_food);
                }
                showthefoodpart.setBackgroundResource(R.color.water_bg);
                food_type = "w";
                design_flow_lay(water_default,"");
            }
        });
        medicine_exclusive_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!food_type.equalsIgnoreCase("m")){
                    addfoodname_common_food.setText("");
                    send_food = new ArrayList<>();
                    design_send_food_lay(send_food);
                }
                showthefoodpart.setBackgroundResource(R.color.med_bg);
                food_type = "m";
                design_flow_lay(medicine_default,"");
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if(send_food!=null && send_food.size()>0) {

                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                if(is_tag_later){
                                    Taglater taglater = databaseRepo.getTaglater(tag_later_time);
                                    databaseRepo.deleteTaglater(taglater);
                                }
                                return null;
                            }
                        }.execute();

                        update_food_count(send_food);

                        original_request_time = System.currentTimeMillis();
                        save_food_to_foodsleepex_table(make_send_food_string(send_food),food_type,original_request_time);
                        save_food_to_local_database(make_send_food_string(send_food), food_type, food_image,original_request_time);

                        call_food_save_api(make_send_food_string(send_food), food_type, food_image);
                        finish();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        todaydate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                set_Image_on_click();
                show_date_time_picker();
            }
        });

        btn_tag_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Taglater taglater = new Taglater();
                taglater.setImage(food_image);
                taglater.setTimestamp(System.currentTimeMillis());
                taglater.setFood_camerainfo(camera_desc.toString());
                databaseRepo.insertTaglaterImage(taglater);

                try {
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


    }

    //Todo methos to ge dta from intent
    private void getData_from_intent(){

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null) {

            if (bundle.containsKey("image")) {

                food_image = bundle.getString("image");
            }
            if (bundle.containsKey("camera_desc")) {

                try {
                    camera_desc = new JSONObject(bundle.getString("camera_desc"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(bundle.containsKey("time")){

                tag_later_time = bundle.getLong("time");
                food_send_ime = commonUsedmethods.parse_date_in_this_format(tag_later_time,"yyyy-MM-dd HH:mm:ss.SSSZ");
                food_send_time_24hr = commonUsedmethods.parse_date_in_this_format(tag_later_time,"HH:mm");
                food_send_timestamp = tag_later_time;
            }

            if(bundle.containsKey("is_tag_later")){

                is_tag_later = bundle.getBoolean("is_tag_later");
                btn_tag_later.setVisibility(View.GONE);
            }
        }else {

            image_preview.setVisibility(View.GONE);
            timeloglay.setVisibility(View.VISIBLE);
            btn_tag_later.setVisibility(View.GONE);

            set_Image_according_to_language();
        }
    }

    //Todo method to set image
    private void set_data(){

        if(!food_image.equalsIgnoreCase("")){

            new CommonUsedmethods().set_Image_Using_Glide(FoodSendActivity.this,image_preview,food_image);
        }
    }


    //Todo call food save api
    private void call_food_save_api(String food, String food_type, final String food_image){

        try {
            String login_url = ApiConfig.FOOD_SAVE_URL;
            JSONObject jsonObject = make_food_save_request_json(food,food_type);

            new APIManager().Apicall(login_url, jsonObject, new Apicallback() {
                @Override
                public void success(int code, String value) {

                    if (code == 200) {

                        //parse response
                        parse_food_save_response(value, new APIPasrsing() {

                            @Override
                            public void completed() {

                                if(!food_image.equalsIgnoreCase("")){

                                    try {
                                        File file = new File(food_image);
                                        boolean deleted = file.delete();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }

                                foodStuff = databaseRepo.getFood(foodStuff.getTimestamp());
                                databaseRepo.deleteFood(foodStuff);
                                databaseRepo.updateFoodSleepExdata_(foodSleepExData.getActual_log_request());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),R.string.Data_saved_successfully_food,Toast.LENGTH_LONG).show();
                                    }
                                });


                            }

                            @Override
                            public void error() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(FoodSendActivity.this,R.string.something_wrong,Toast.LENGTH_LONG).show();
                                    }
                                });


                            }
                        });
                    } else {

                    }
                }

                @Override
                public void failure(String value) {


                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo make food save request for json
    private JSONObject make_food_save_request_json(String food,String food_image){

        JSONObject json = new JSONObject();

        try {

            json.put("participant_id", AESEncryption.encrypt(user_id));
            json.put("research_info_id", AESEncryption.encrypt(research_info_id));
            json.put("food_description_file",AESEncryption.encrypt(makeJsonfood(food,food_type)));
            json.put("food_location_file",AESEncryption.encrypt(location_desc));
            if(!food_image.equalsIgnoreCase("")) {
                json.put("food_camerainfo_file", AESEncryption.encrypt(camera_desc.toString()));
                json.put("food_image_file", AESEncryption.encrypt(make_json_image()));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    //Todo make location detail json file
    private void make_json_loaction(double latitude,double longitude){


        JSONObject jsonObject = new JSONObject();
        try {

            //addresses = geocoder.getFromLocation(latitude, longitude, 1);

            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            if(addresses!=null && addresses.size()>0) {
                jsonObject.put("location_name", addresses.get(0).getAddressLine(0));
            }else{
                jsonObject.put("location_name", "");
            }
            jsonObject.put("file_name", user_id+"_"+"Location"+"_"+System.currentTimeMillis());//filename created by partitpant_id and date
            jsonObject.put("file_type", "application/octet-stream");
        }catch (Exception e){
            e.printStackTrace();
        }

        location_desc =  jsonObject.toString();
    }

    //Todo methos tp convert image to BAse64 and make json request
    private String make_json_image(){

        final JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("file_name", user_id + "_" + "Image" + "_" + System.currentTimeMillis());//filename created by partitpant_id and date
            jsonObject.put("file_type", "image/jpeg");
            jsonObject.put("image", convert_image_to_base64());

        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private void compress_image(){

        try {
            new CompressImage(FoodSendActivity.this, Uri.fromFile(new File(food_image)), food_image, new OnCompressComplted() {

                @Override
                public void success(String image) {

                    try {

                        food_image = image;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void error() {

                }
            });
        }catch (Exception E){
            E.printStackTrace();
        }
    }

    //Todo make json file for food
    private String makeJsonfood(String food,String food_type){

        CommonUsedmethods commonUsedmethods = new CommonUsedmethods();
        JSONObject jsonObject =null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("food_name", food);
            jsonObject.put("food_type", food_type);
            jsonObject.put("date_time", food_send_ime);//"01-07-2019 03:14PM GMT+05:30"
            jsonObject.put("timezone", commonUsedmethods.getTimezone());
            jsonObject.put("file_name", user_id+"_"+"Food"+"_"+System.currentTimeMillis());//filename created by partitpant_id and date
            jsonObject.put("file_type", "text/plain");
        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println(jsonObject.toString());
        return jsonObject.toString();
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

    //Todo get current location
    private void current_loaction(){

        try {
            GPSTracker gps = new GPSTracker(FoodSendActivity.this);

            // Check if GPS enabled
            if (gps.canGetLocation()) {

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                make_json_loaction(latitude, longitude);

            } else {
                // Can't get location.
                // GPS or network is not enabled.
                // Ask user to enable GPS/network in settings.
                gps.showSettingsAlert();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo convert image file to Base 64
    private String convert_image_to_base64(){

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(new File(food_image).getAbsolutePath());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            return encoded;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    //Todo Method used to ask location permission
    public void permission(){

        try {

            int location = ContextCompat.checkSelfPermission(FoodSendActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

            ArrayList<String> arrayList = new ArrayList<>();
            if (location != PackageManager.PERMISSION_GRANTED) {
                arrayList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                current_loaction();
            }

            if (arrayList.size() != 0) {
                ActivityCompat.requestPermissions(FoodSendActivity.this, arrayList.toArray(new String[arrayList.size()]), 10);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo override method permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        try {
            if (requestCode == 10) {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    current_loaction();
                } else {

                    make_json_loaction(0.0, 0.0);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo add foods under four tabs like food,brev.,water,medicine
    private void add_food_to_flow_lay(String food_name,String clicked_food){

        try {
            final TextView textView = buildLabel(food_name);
            if (clicked_food.equalsIgnoreCase(food_name)) {
                textView.setBackground(getResources().getDrawable(R.drawable.buttonbg));
            } else {
                textView.setBackground(getResources().getDrawable(R.drawable.button_bg));
            }
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String food = view.getTag().toString();
                    if (send_food.contains(food)) {
                        send_food.remove(food);
                        textView.setBackground(getResources().getDrawable(R.drawable.button_bg));
                        design_send_food_lay(send_food);

                        if (send_food.size() == 0) {
                            if (btn_tag_later.getVisibility() == View.VISIBLE) {
                                commonUsedmethods.setAlphaForView(btn_tag_later, 1.25f);
                                btn_tag_later.setEnabled(true);
                            }
                            commonUsedmethods.setAlphaForView(btn_save, 0.35f);
                            btn_save.setEnabled(false);
                        }
                    } else {

                        send_food.add(food);
                        textView.setBackground(getResources().getDrawable(R.drawable.buttonbg));
                        design_send_food_lay(send_food);

                        if (btn_tag_later.getVisibility() == View.VISIBLE) {
                            commonUsedmethods.setAlphaForView(btn_tag_later, 0.35f);
                            btn_tag_later.setEnabled(false);
                        }
                        commonUsedmethods.setAlphaForView(btn_save, 1.25f);
                        btn_save.setEnabled(true);
                    }
                }
            });
            lay_foods.addView(textView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo add foods to sending layout
    private void add_food_send_lay(String food_name){
        try {
            final TextView textView = buildLabel(food_name);
            lay_send_food.addView(textView);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private TextView buildLabel(String text) {

        try {

            final TextView textView = new TextView(this);
            textView.setText(text);
            textView.setTag(text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textView.setPadding((int) dpToPx(16), (int) dpToPx(8), (int) dpToPx(16), (int) dpToPx(8));
            textView.setBackground(getResources().getDrawable(R.drawable.buttonbg));

            return textView;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private float dpToPx(float dp){
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    //Todo method to add all intial food if user has not inserted any food
    private void setInitialfood(){

        try {
            if (fetchCommonFoodArrayList.size() == 0) {

                food_default.add("Apple");
                food_default.add("Bannana");
                food_default.add("Orange");
                food_default.add("Juice");
                food_default.add("Milk");
                food_default.add("Water");

                breverage_default.add("Suji");
                breverage_default.add("Wheats");

                water_default.add("Water");
                medicine_default.add("Namcold");

                design_flow_lay(food_default, "");

            } else {

                Collections.sort(fetchCommonFoodArrayList, new CustomComparator());
                Collections.reverse(fetchCommonFoodArrayList);

                make_list_of_foods();

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void design_flow_lay(ArrayList<String> arrayList,String food){

        try {
            if (lay_foods != null) {
                lay_foods.removeAllViews();
            }
            for (int i = 0; i < arrayList.size(); i++) {

                add_food_to_flow_lay(arrayList.get(i), food);

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void design_send_food_lay(ArrayList<String> arrayList){

        try {
            if (lay_send_food != null) {
                lay_send_food.removeAllViews();
            }
            for (int i = 0; i < arrayList.size(); i++) {

                add_food_send_lay(arrayList.get(i));

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void check_for_food_existence(String food){

        try {
            if (food_type.equalsIgnoreCase("f")) {

                if (food_default.contains(food)) {
                    design_flow_lay(food_default, food);
                } else {
                    food_default.add(food);
                    design_flow_lay(food_default, food);
                }


            } else if (food_type.equalsIgnoreCase("b")) {

                if (breverage_default.contains(food)) {
                    design_flow_lay(breverage_default, food);
                } else {
                    breverage_default.add(food);
                    design_flow_lay(breverage_default, food);
                }

            } else if (food_type.equalsIgnoreCase("w")) {

                if (water_default.contains(food)) {
                    design_flow_lay(water_default, food);
                } else {
                    water_default.add(food);
                    design_flow_lay(water_default, food);
                }
            } else if (food_type.equalsIgnoreCase("m")) {

                if (medicine_default.contains(food)) {
                    design_flow_lay(medicine_default, food);
                } else {
                    medicine_default.add(food);
                    design_flow_lay(medicine_default, food);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String make_send_food_string(ArrayList<String> arrayList){

        String food = "";
        try {

            for (int i = 0; i < arrayList.size(); i++) {

                if (i == 0) {
                    food = arrayList.get(i);
                } else {
                    food = food + "," + arrayList.get(i);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return food;

    }

    private void set_Image_according_to_language(){

        try {
            language = Locale.getDefault().getDisplayLanguage();

            if (language.matches("français")) {

                now_button.setImageResource(R.mipmap.now_select_french);
                ten_button.setImageResource(R.mipmap.ten_min_notselect_french);
                thirty_button.setImageResource(R.mipmap.thirty_min_notselect_french);
                one_button.setImageResource(R.mipmap.one_hr_notselected_french);
                two_button.setImageResource(R.mipmap.two_hr_notselected_french);

            } else {
                now_button.setImageResource(R.mipmap.now_selected);
                ten_button.setImageResource(R.mipmap.ten_min_nsel);
                thirty_button.setImageResource(R.mipmap.thirty_min_nsel);
                one_button.setImageResource(R.mipmap.one_hr_nsel);
                two_button.setImageResource(R.mipmap.two_hr_nsel);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void set_Image_on_click(){

        try {
            language = Locale.getDefault().getDisplayLanguage();

            if (language.matches("français")) {

                now_button.setImageResource(R.mipmap.now_notselect_french);
                ten_button.setImageResource(R.mipmap.ten_min_notselect_french);
                thirty_button.setImageResource(R.mipmap.thirty_min_notselect_french);
                one_button.setImageResource(R.mipmap.one_hr_notselected_french);
                two_button.setImageResource(R.mipmap.two_hr_notselected_french);

            } else {
                now_button.setImageResource(R.mipmap.now);
                ten_button.setImageResource(R.mipmap.ten_min_nsel);
                thirty_button.setImageResource(R.mipmap.thirty_min_nsel);
                one_button.setImageResource(R.mipmap.one_hr_nsel);
                two_button.setImageResource(R.mipmap.two_hr_nsel);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void show_date_time_picker(){


        try {
            dateandtime = Calendar.getInstance(Locale.US);
            DatePickerCommonFood dp = new DatePickerCommonFood(FoodSendActivity.this,
                    dateandtime, new DatePickerCommonFood.DatePickerListner() {

                @Override
                public void OnDoneButton(Dialog datedialog, Calendar c) {

                    datedialog.dismiss();
                    Calendar current_date = Calendar.getInstance();

                    if (c.after(current_date)) {
                        commonUsedmethods.Toast(FoodSendActivity.this, getResources().getString(R.string.Selected_time_shouldnot));

                    } else {

                        String time = commonUsedmethods.parse_date_in_this_format(c.getTimeInMillis(),"yyyy-MM-dd HH:mm:ss.SSSZ");
                        System.out.println(time);

                        food_send_ime = time;
                        food_send_time_24hr = commonUsedmethods.parse_date_in_this_format(c.getTimeInMillis(),"HH:mm");
                        food_send_timestamp = c.getTimeInMillis();

                        todaydate.setText(commonUsedmethods.parse_date_in_this_format(c.getTimeInMillis(),"dd MMM yyyy hh:mm a"));
                    }
                }

                @Override
                public void OnCancelButton(Dialog datedialog) {
                    // TODO Auto-generated method stub

                    datedialog.dismiss();
                    now_button.performClick();
                }
            });
            dp.setCancelable(false);
            dp.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void save_food_to_local_database(String food, String food_type, final String food_image,long request_time){

        try {
            foodStuff = new FoodStuff();
            foodStuff.setFood_description(makeJsonfood(food, food_type));
            foodStuff.setFood_location(location_desc);
            foodStuff.setTimestamp(request_time);
            foodStuff.setStatus("pending");

            if (!food_image.equalsIgnoreCase("")) {

                foodStuff.setFood_image(make_json_image());
                foodStuff.setFood_camerainfo(camera_desc.toString());
                foodStuff.setImage_file_name(food_image);

                databaseRepo.insertFood(foodStuff);
            } else {

                databaseRepo.insertFood(foodStuff);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void save_food_to_foodsleepex_table(String food, String food_type,long request_time){

        try {
            foodSleepExData = new FoodSleepExData();
            foodSleepExData.setTimestamp(food_send_timestamp);
            foodSleepExData.setFood_name(food);
            foodSleepExData.setFood_type(food_type);
            foodSleepExData.setFood_taken_time(food_send_time_24hr);
            foodSleepExData.setActual_log_request(request_time);
            foodSleepExData.setType("food");
            databaseRepo.insertFoodSleepExdata(foodSleepExData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        finish();
        overridePendingTransition(0,0);
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
                                        Toast.makeText(FoodSendActivity.this,R.string.something_wrong,Toast.LENGTH_LONG).show();
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
                            Toast.makeText(FoodSendActivity.this, value, Toast.LENGTH_LONG).show();
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setInitialfood();
                    }
                });
                databaseRepo.clear_common_food();
                databaseRepo.insert_Participant_Common_Food(fetchCommonFoodArrayList);
            }

        }catch (Exception e){
            e.printStackTrace();

        }

    }

    private void fetch_participant_common_food(){

        try {
            databaseRepo.get_common_food(new OnDataFetched() {
                @Override
                public void data(Object object, int i) {

                }

                @Override
                public void data(Object object) {


                    fetchCommonFoodArrayList = (ArrayList<FetchCommonFood>) object;
                    //Todo method to add all intial food if user has not inserted any food
                    setInitialfood();

                    if(fetchCommonFoodArrayList==null || fetchCommonFoodArrayList.size()==0){

                        //Todo call fetch common food API
                        call_fetch_common_food_API();
                    }

                }
            });
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

    private void make_list_of_foods(){

        try {
            food_default = new ArrayList<>();
            breverage_default = new ArrayList<>();
            medicine_default = new ArrayList<>();

            for (int i = 0; i < fetchCommonFoodArrayList.size(); i++) {

                if (fetchCommonFoodArrayList.get(i).getFood_type().equalsIgnoreCase("f")) {

                    food_default.add(fetchCommonFoodArrayList.get(i).getAnnotation_text());

                } else if (fetchCommonFoodArrayList.get(i).getFood_type().equalsIgnoreCase("b")) {

                    breverage_default.add(fetchCommonFoodArrayList.get(i).getAnnotation_text());

                } else if (fetchCommonFoodArrayList.get(i).getFood_type().equalsIgnoreCase("w")) {
                    water_default.add(fetchCommonFoodArrayList.get(i).getAnnotation_text());

                } else if (fetchCommonFoodArrayList.get(i).getFood_type().equalsIgnoreCase("m")) {

                    medicine_default.add(fetchCommonFoodArrayList.get(i).getAnnotation_text());
                }
            }

            if (food_default.size() == 0) {

                food_default.add("Apple");
                food_default.add("Bannana");
                food_default.add("Orange");
                food_default.add("Juice");
                food_default.add("Milk");
                food_default.add("Water");
            }
            if (breverage_default.size() == 0) {

                breverage_default.add("Suji");
                breverage_default.add("Wheats");
            }
            if (water_default.size() == 0) {
                water_default.add("Water");
            }
            if (medicine_default.size() == 0) {
                medicine_default.add("Namcold");
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    design_flow_lay(food_default, "");
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void update_food_count(ArrayList<String> arrayList){

        try {

            for (int i = 0; i < arrayList.size(); i++) {

                FetchCommonFood fetchCommonFood = check_food_name_is_exist(fetchCommonFoodArrayList, arrayList.get(i), food_type);

                if (fetchCommonFood.getAnnotation_text().equalsIgnoreCase("")) {

                    fetchCommonFood.setFood_type(food_type);
                    fetchCommonFood.setCount(1);
                    fetchCommonFood.setAnnotation_text(arrayList.get(i));

                    databaseRepo.insert_Participant_Common_Food(fetchCommonFood);
                } else {

                    databaseRepo.get_common_food(arrayList.get(i), food_type, new OnDataFetched() {
                        @Override
                        public void data(Object object, int i) {

                        }

                        @Override
                        public void data(Object object) {

                            FetchCommonFood fetchCommonFood1 = (FetchCommonFood) object;
                            fetchCommonFood1.setCount(fetchCommonFood1.getCount() + 1);
                            databaseRepo.updateCommonFood(fetchCommonFood1);
                        }
                    });


                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
