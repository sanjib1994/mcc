package com.salk.mycircadianclock.Utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.wheel.WheelView;
import com.salk.mycircadianclock.wheel.adapters.ArrayWheelAdapter;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.appcompat.app.AlertDialog;

public class CommonUsedmethods {

    public static Locale locale = Locale.US;
    String[] hours_array = null;
    String language = "";

    public CommonUsedmethods(){

        language = Locale.getDefault().getDisplayLanguage();

        if(language.matches("français")){
            locale = Locale.FRENCH;
        }else{
            locale = Locale.US;
        }
    }

    public  void makeActivityFullscreen(Activity activity){

        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public  int getAppVersioncode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("RegisterActivity",
                    "I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }

    public  String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return "Version " +packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("RegisterActivity",
                    "I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }



    public File create_directory(){

        File Directory=null;
        try {
             Directory = new File(Environment.getExternalStorageDirectory() + "/Salk");

            if (!Directory.exists()) {
                Directory.mkdirs();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return Directory;
    }

    public void set_Image_Using_Glide(Context context, ImageView imageView,String file){

        Glide.with(context).load(file).into(imageView);
    }

    public void set_Image_Using_Glide(Context context, ImageView imageView,Integer file){

        Glide.with(context).load(file).into(imageView);
    }

    public String  get_current_date(){

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy",locale);
        String formattedDate = df.format(c);
        return  formattedDate;
    }
    public String get_current_time(){

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("hh:mma",locale);
        String formattedDate = df.format(c);
        return  formattedDate;
    }

    public String get_current_time_in_24hr(){

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm",locale);
        String formattedDate = df.format(c);
        return  formattedDate;
    }


    public String getTimezoneGMT(){

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("Z", Locale.getDefault());
        String localTime = date.format(currentLocalTime);
        String finalTimezone = String.format("GMT%s:%s", localTime.substring(0, 3), localTime.substring(3));


        return finalTimezone;
    }

    public String getTimezone(){

        TimeZone tz = TimeZone.getDefault();
        return tz.getDisplayName(false, TimeZone.SHORT);
    }

    public String substracttime(int hour){

        String time = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mma",locale);
            String currentDateandTime = sdf.format(new Date());

            Date date = sdf.parse(currentDateandTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            if(hour==10 || hour==30){
                calendar.add(Calendar.MINUTE, -hour);
            }else {
                calendar.add(Calendar.HOUR, -hour);
            }

            time = sdf.format(calendar.getTime());
        }catch (Exception e){
            e.printStackTrace();
        }
       return time;

    }

    public String addtime(long start,int hour,int min){

        String time = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",locale);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(start);

            calendar.add(Calendar.MINUTE, min);
            calendar.add(Calendar.HOUR, hour);


            time = sdf.format(calendar.getTime());
        }catch (Exception e){
            e.printStackTrace();
        }
        return time;

    }


    public void Toast(Context context,String message) {

        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();

    }

    public String parse_date_in_this_format(long timestamp,String format){

        try{
            SimpleDateFormat sdf = new SimpleDateFormat(format,locale);
            Date netDate = (new Date(timestamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return ex.getMessage();
        }

    }

    public String parse_date_in_this_format(String timestamp,String output_format,String input_format){

        SimpleDateFormat date12Format;
        try {

            date12Format = new SimpleDateFormat(input_format,locale);
            SimpleDateFormat date24Format = new SimpleDateFormat(output_format,locale);
            return date24Format.format(date12Format.parse(timestamp));
        } catch (Exception ex) {
            return ex.getMessage();
        }

    }


    public void setAlphaForView(View v, float alpha) {

        AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
        animation.setDuration(0);
        animation.setFillAfter(true);
        v.startAnimation(animation);

    }

    public Long convert_date_to_timestamp(String inputformat,String dates){

        try {
            DateFormat formatter = new SimpleDateFormat(inputformat,locale);
            Date date = formatter.parse(dates);
            return date.getTime();
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public void show_Toast(Context context,String message){

        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    public KProgressHUD show_progerssDialog(Context context,String message,boolean isCancelable){

       KProgressHUD kProgressHUD =  KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(message)
                .setCancellable(isCancelable)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

       return kProgressHUD;

    }

    public void TimePicker(Context context, final TimePickerListner timePickerListner) {

        String hour, min, ampm;

        final Calendar calender = Calendar.getInstance();

        hour = String.valueOf(calender.get(Calendar.HOUR));
        min = String.valueOf(calender.get(Calendar.MINUTE));
        ampm = String.valueOf(calender.get(Calendar.AM_PM));

        final Dialog set_time_target_alert = new Dialog(context);
        set_time_target_alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        set_time_target_alert.setContentView(R.layout.excercise_time_set);
        set_time_target_alert.getWindow().setGravity(Gravity.BOTTOM);
        set_time_target_alert.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        set_time_target_alert.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        set_time_target_alert.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        set_time_target_alert.setCancelable(false);

        TextView Cancel_wheel = set_time_target_alert.findViewById(R.id.cancelbuttonslpwh);
        TextView Done_wheel = set_time_target_alert.findViewById(R.id.donebuttonslpwh);
        TextView title_text = set_time_target_alert.findViewById(R.id.titleslw);
        title_text.setText("");

        // set hour wheel
        final WheelView hours = set_time_target_alert.findViewById(R.id.hours);
        if(language.equalsIgnoreCase("français")){
            hours_array = new String[] { "00","01", "02", "03", "04", "05", "06", "07",
                    "08", "09", "10", "11", "12","13", "14", "15", "16", "17", "18", "19",
                    "20", "21", "22", "23" };

        }else {

            hours_array = new String[]{"01", "02", "03", "04", "05", "06", "07",
                    "08", "09", "10", "11", "12"};
        }
        ArrayWheelAdapter<String> amp = new ArrayWheelAdapter<String>(
                context, hours_array);
        hours.setViewAdapter(amp);

        // set min wheel
        final WheelView mins = set_time_target_alert.findViewById(R.id.minutes);
        final String [] mins_array = new String[] { "00", "01", "02", "03", "04", "05", "06",
                "07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
                "17", "18", "19", "20", "21", "22", "23", "24", "25", "26",
                "27", "28", "29", "30", "31", "32", "33", "34", "35", "36",
                "37", "38", "39", "40", "41", "42", "43", "44", "45", "46",
                "47", "48", "49", "50", "51", "52", "53", "54", "55", "56",
                "57", "58", "59" };
        ArrayWheelAdapter<String> mins_adapter = new ArrayWheelAdapter<String>(
                context, mins_array);
        mins.setViewAdapter(mins_adapter);

        final WheelView ampm_wheel = set_time_target_alert
                .findViewById(R.id.ampmwheel);
        final String[] ampm_array = new String[] { "AM", "PM" };
        ArrayWheelAdapter<String> ampm_adapter = new ArrayWheelAdapter<String>(
                context, ampm_array);
        ampm_wheel.setViewAdapter(ampm_adapter);
        if(language.equalsIgnoreCase("français")){

            ampm_wheel.setVisibility(View.GONE);
        }else{

            ampm_wheel.setVisibility(View.VISIBLE);
        }

        set_time_target_alert.show();
        Cancel_wheel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //set_time_target_alert.dismiss();

                timePickerListner.OnCancelButton(set_time_target_alert);
            }
        });
        Done_wheel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String selected_AMPM = "";
                String selected_mins = mins_array[mins.getCurrentItem()];
                String selected_hours = hours_array[hours.getCurrentItem()];

                if(ampm_wheel.getVisibility()==View.VISIBLE) {

                    selected_AMPM = ampm_array[ampm_wheel.getCurrentItem()];
                }

                timePickerListner.OnDoneButton(set_time_target_alert,selected_hours,selected_mins,selected_AMPM);

               // set_time_target_alert.dismiss();

            }
        });
        MoveWheeel(hour, min, ampm,hours,mins,ampm_wheel);

    }


    public void MoveWheeel(final String HH, final String mm, final String ampm,final WheelView hours,final WheelView mins,
                           final WheelView ampm_wheel) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            public void run() {

                if(Integer.parseInt(HH)==0){
                    hours.setCurrentItem(11, true);

                }else{
                    hours.setCurrentItem(Integer.parseInt(HH) - 1, true);

                }

                mins.setCurrentItem(Integer.parseInt(mm), true);
                ampm_wheel.setCurrentItem(Integer.parseInt(ampm), true);

            }
        });
    }

    public void showAlertHistory(Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder
                .setMessage(context.getResources().getString(R.string.You_need_to_complete_baseline))
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        dialog.cancel();
                    }
                });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // show it
        alertDialog.show();
    }

    public long getWeekStartDate() {
        Calendar calendar = Calendar.getInstance();
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DATE, -1);
        }
        return calendar.getTimeInMillis();
    }

    public long getWeekEndDate() {
        Calendar calendar = Calendar.getInstance();
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DATE, 1);
        }
        calendar.add(Calendar.DATE, -1);
        return calendar.getTimeInMillis();
    }


    public interface TimePickerListner {
        public void OnDoneButton(Dialog datedialog,String hour,String min,String AM_PM);

        public void OnCancelButton(Dialog datedialog);
    }

}
