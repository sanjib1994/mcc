package com.salk.mycircadianclock.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.exercise.LogExerciseDataActivity;
import com.salk.mycircadianclock.food.FoodSendActivity;
import com.salk.mycircadianclock.health_vitals.LogHealthDataActivity;
import com.salk.mycircadianclock.history.history_intake.HistoryIntakeActivity;
import com.salk.mycircadianclock.information.InfoMainActivity;
import com.salk.mycircadianclock.settings.InterVentionSettingsActivity;
import com.salk.mycircadianclock.sleep.LogSleepDataActivity;


public class TabbarClick {

    private ImageView image_settings,image_home,image_history,image_info,plus_image;
    private RelativeLayout rel_main;
    private Context contexts;
    private String from = "";
    private SharedPreferences sharedPreferences;
    private TinylocalDb tinylocalDb;
    private String user_type = "";


    public void click(final Context context, RelativeLayout relativeLayout, RelativeLayout main, final String from){

        image_settings =  relativeLayout.findViewById(R.id.setting_img);
        image_home=  relativeLayout.findViewById(R.id.home_img);
        image_history =  relativeLayout.findViewById(R.id.faq_img);
        image_info =  relativeLayout.findViewById(R.id.info_img);
        plus_image =  relativeLayout.findViewById(R.id.center_image);
        contexts = context;
        rel_main = main;
        this.from = from;
        tinylocalDb = new TinylocalDb();
        sharedPreferences = tinylocalDb.get_shared_pref((Activity) context);
        user_type = tinylocalDb.get_data_in_shared(sharedPreferences,"user_type");

        if(from.equalsIgnoreCase("home")){

            image_home.setEnabled(false);
        }

        image_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();

                if(!from.equalsIgnoreCase("home")){
                    ((Activity)contexts).finish();
                }


                intent = new Intent(contexts, InterVentionSettingsActivity.class);
                contexts.startActivity(intent);
                ((Activity) contexts).overridePendingTransition(0, 0);

            }
        });

        image_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((Activity)contexts).finish();
                ((Activity)contexts).overridePendingTransition(0,0);
            }
        });

        image_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(user_type.equalsIgnoreCase("intervention")){
                    Intent intent = new Intent();

                    if(!from.equalsIgnoreCase("home")){
                        ((Activity)contexts).finish();
                    }


                    intent = new Intent(contexts, HistoryIntakeActivity.class);
                    contexts.startActivity(intent);
                    ((Activity) contexts).overridePendingTransition(0, 0);
                }else{
                    new CommonUsedmethods().showAlertHistory(contexts);
                }
            }
        });

        image_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();

                if(!from.equalsIgnoreCase("home")){
                    ((Activity)contexts).finish();
                }


                intent = new Intent(contexts, InfoMainActivity.class);
                contexts.startActivity(intent);
                ((Activity) contexts).overridePendingTransition(0, 0);
            }
        });


       add_circular_menu();

    }

    private void add_circular_menu(){

        // Add some items to the menu. They are regular views as well!
        ImageView health = new ImageView(contexts);
        ImageView exercise = new ImageView(contexts);
        ImageView food = new ImageView(contexts);
        ImageView sleep = new ImageView(contexts);

        health.setImageDrawable(contexts.getResources().getDrawable(
                R.mipmap.health_new));
        exercise.setImageDrawable(contexts.getResources().getDrawable(
                R.mipmap.excercise_new));
        food.setImageDrawable(contexts.getResources()
                .getDrawable(R.mipmap.food_new));
        sleep.setImageDrawable(contexts.getResources().getDrawable(
                R.mipmap.sleep_new));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                40, 40);
        health.setLayoutParams(layoutParams);
        exercise.setLayoutParams(layoutParams);
        food.setLayoutParams(layoutParams);
        sleep.setLayoutParams(layoutParams);

        FrameLayout.LayoutParams tvParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        health.setLayoutParams(tvParams);
        exercise.setLayoutParams(tvParams);
        food.setLayoutParams(tvParams);
        sleep.setLayoutParams(tvParams);


        rel_main.setEnabled(false);
        final FloatingActionMenu circleMenu = new FloatingActionMenu.Builder((Activity) contexts)
                .setStartAngle(0)
                // A whole circle!
                .setEndAngle(-180)
                .setRadius(contexts.getResources().getDimensionPixelSize(
                        R.dimen.radius_medium))
                .addSubActionView(health).addSubActionView(exercise)
                .addSubActionView(food).addSubActionView(sleep)
                .attachTo(plus_image).build();
        circleMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {

            @Override
            public void onMenuOpened(FloatingActionMenu arg0) {
                // TODO Auto-generated method stub
                rel_main.setVisibility(View.VISIBLE);
                rel_main.setBackgroundResource(R.mipmap.transparent_bg);
                // setImageBackAlpha(main_lay);
            }

            @Override
            public void onMenuClosed(FloatingActionMenu arg0) {
                // TODO Auto-generated method stub
                rel_main.setVisibility(View.GONE);
                rel_main.setBackgroundResource(0);
            }
        });

        health.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                image_settings.setEnabled(true);
                image_info.setEnabled(true);
                image_home.setEnabled(true);
                image_history.setEnabled(true);

                if(!from.equalsIgnoreCase("home")){
                    ((Activity)contexts).finish();
                }

                Intent intent = new Intent(contexts  , LogHealthDataActivity.class);
                contexts.startActivity(intent);
                ((Activity)contexts).overridePendingTransition(0,0);


                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rel_main.setVisibility(View.GONE);
                        rel_main.setBackgroundResource(0);
                        circleMenu.close(true);
                    }
                }, 500);


            }
        });
        exercise.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                image_settings.setEnabled(true);
                image_info.setEnabled(true);
                image_home.setEnabled(true);
                image_history.setEnabled(true);

                if(!from.equalsIgnoreCase("home")){
                    ((Activity)contexts).finish();
                }

                Intent intent = new Intent(contexts  , LogExerciseDataActivity.class);
                contexts.startActivity(intent);
                ((Activity)contexts).overridePendingTransition(0,0);




                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rel_main.setVisibility(View.GONE);
                        rel_main.setBackgroundResource(0);
                        circleMenu.close(true);
                    }
                }, 500);


            }
        });
        food.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                image_settings.setEnabled(true);
                image_info.setEnabled(true);
                image_home.setEnabled(true);
                image_history.setEnabled(true);


                if(!from.equalsIgnoreCase("home")){
                    ((Activity)contexts).finish();
                }

                Intent intent = new Intent(contexts  , FoodSendActivity.class);
                contexts.startActivity(intent);
                ((Activity)contexts).overridePendingTransition(0,0);




                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rel_main.setVisibility(View.GONE);
                        rel_main.setBackgroundResource(0);
                        circleMenu.close(true);
                    }
                }, 500);



            }
        });
        sleep.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                image_settings.setEnabled(true);
                image_info.setEnabled(true);
                image_home.setEnabled(true);
                image_history.setEnabled(true);

                if(!from.equalsIgnoreCase("home")){
                    ((Activity)contexts).finish();
                }

                Intent intent = new Intent(contexts, LogSleepDataActivity.class);
                contexts.startActivity(intent);
                ((Activity)contexts).overridePendingTransition(0,0);



                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rel_main.setVisibility(View.GONE);
                        rel_main.setBackgroundResource(0);
                        circleMenu.close(true);
                    }
                }, 500);


            }
        });
    }
}
