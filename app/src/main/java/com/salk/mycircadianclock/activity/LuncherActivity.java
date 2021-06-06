package com.salk.mycircadianclock.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.facebook.stetho.Stetho;
import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.Utility.ProgressHUD;
import com.salk.mycircadianclock.Utility.TinylocalDb;
import com.salk.mycircadianclock.camera.CustomCameraActivity;

public class LuncherActivity extends AppCompatActivity {

    ProgressHUD HUDdialog;
    TextView version_txt;
    TinylocalDb tinylocalDb;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

        //to make luncher screen full screen
        new CommonUsedmethods().makeActivityFullscreen(LuncherActivity.this);

        setContentView(R.layout.activity_luncher);

        databaseScreening();

        //run clock animation
        clock_animation();

        //get reference for all views
        init();

        //set version name
        setversion_name();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //clock animation function
    private void clock_animation(){

        try {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    // Code here will run in UI thread
                    if (!isDestroyed()) {
                        HUDdialog = ProgressHUD.show(LuncherActivity.this, "");
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //get reference for all views
    private void init(){

        try {
            version_txt = findViewById(R.id.version_txt);
            tinylocalDb = new TinylocalDb();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //set version name
    private void setversion_name(){

        try {
            if (version_txt != null) {
                version_txt.setText(new CommonUsedmethods().getAppVersionName(LuncherActivity.this));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //check wheather user is logged in or not
                    check_is_login();
                }
            },5000);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void check_is_login(){

        try {

            sharedPreferences = tinylocalDb.get_shared_pref(LuncherActivity.this);

            if(tinylocalDb.get_data_in_shared(sharedPreferences,"isLogin").equalsIgnoreCase("")){

                if(HUDdialog!=null && HUDdialog.isShowing()){
                    HUDdialog.dismiss();
                }
                Intent intent = new Intent(LuncherActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }else if(tinylocalDb.get_data_in_shared(sharedPreferences,"isLogin").equalsIgnoreCase("true")){

                Intent intent = new Intent(LuncherActivity.this, CustomCameraActivity.class);
                startActivity(intent);
                finish();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    /**
     * Following methods are used during the development purpose only.
     * Need to be commented out during production build
     */
    public void databaseScreening(){
        // Create an InitializerBuilder
        Stetho.InitializerBuilder initializerBuilder = Stetho
                .newInitializerBuilder(this);
        // Enable Chrome DevTools
        initializerBuilder.enableWebKitInspector(Stetho
                .defaultInspectorModulesProvider(LuncherActivity.this));
        // Enable command line interface
        initializerBuilder.enableDumpapp(Stetho
                .defaultDumperPluginsProvider(LuncherActivity.this));
        // Use the InitializerBuilder to generate an Initializer
        Stetho.Initializer initializer = initializerBuilder.build();
        // Initialize Stetho with the Initializer
        Stetho.initialize(initializer);

        /**
         * Development tool ends here
         */
    }
}
