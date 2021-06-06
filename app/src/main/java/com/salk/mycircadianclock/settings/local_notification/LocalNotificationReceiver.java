package com.salk.mycircadianclock.settings.local_notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;

import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.activity.LuncherActivity;
import com.salk.mycircadianclock.localDatabase.DatabaseRepo;
import com.salk.mycircadianclock.localDatabase.OnDataFetched;

import androidx.core.app.NotificationCompat;

import static android.content.Context.ALARM_SERVICE;

public class LocalNotificationReceiver extends BroadcastReceiver {

    private NotificationChannel mChannel;
    private NotificationManager notifManager;
    Intent intent;
    boolean is_notify = false;
    String notification_message,notification_type,threshold,frequency;
    int current_frequency;
    Context context_;
    DatabaseRepo dbHelper;

    public LocalNotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent1) {

        context_ = context;
        notification_message = intent1.getStringExtra("notification_message");
        notification_type = intent1.getStringExtra("notification_type");
        threshold = intent1.getStringExtra("threshold");
        frequency = intent1.getStringExtra("frequency");
        current_frequency = intent1.getIntExtra("current_frequency",0);

        dbHelper = new DatabaseRepo(context);

        dbHelper.is_notification_on(new OnDataFetched() {
            @Override
            public void data(Object object, int i) {

            }

            @Override
            public void data(Object object) {

                is_notify = (boolean) object;

                if(notification_type.equalsIgnoreCase("food")){
                    check_food_sleep_ex_exist("f");
                }else if(notification_type.equalsIgnoreCase("medicine")){
                    check_food_sleep_ex_exist("m");
                }else if(notification_type.equalsIgnoreCase("water")){
                    check_food_sleep_ex_exist("w");
                }else if(notification_type.equalsIgnoreCase("sleep")){
                    check_food_sleep_ex_exist("");
                }else if(notification_type.equalsIgnoreCase("exercise")){
                    check_food_sleep_ex_exist("");
                }
            }
        });

    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;
    }

    private void show_notification(Context context,String body,String title){

        if (notifManager == null) {
            notifManager = (NotificationManager) context.getSystemService
                    (Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder;

            intent = new Intent(context, LuncherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (mChannel == null) {
                mChannel = new NotificationChannel
                        ("0", title, importance);
                mChannel.setDescription(body);
                mChannel.enableVibration(true);
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, "0");

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
            builder.setContentTitle(title)
                    .setSmallIcon(getNotificationIcon()) // required
                    .setContentText(body)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource
                            (context.getResources(), R.mipmap.ic_launcher))
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri
                            (RingtoneManager.TYPE_NOTIFICATION));
            Notification notification = builder.build();
            notifManager.notify((int)System.currentTimeMillis(), notification);
        } else {

            intent = new Intent(context, LuncherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = null;

            pendingIntent = PendingIntent.getActivity(context, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(getNotificationIcon())
                    .setContentTitle(title)// required
                    .setContentText(body)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource
                            (context.getResources(), R.mipmap.ic_launcher))
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri
                            (RingtoneManager.TYPE_NOTIFICATION));


            notifManager.notify((int)System.currentTimeMillis(), notificationBuilder.build());
        }
    }

    private void set_local_notification(Context context,String notification_message,String notification_type,String threshold,String frequency){

        try{

            Intent notifyIntent = new Intent(context, LocalNotificationReceiver.class);
            notifyIntent.putExtra("notification_message",notification_message);
            notifyIntent.putExtra("notification_type",notification_type);
            notifyIntent.putExtra("threshold",threshold);
            notifyIntent.putExtra("frequency",frequency);
            notifyIntent.putExtra("current_frequency",0);
            PendingIntent pendingIntent = PendingIntent.getBroadcast
                    (context, (int)System.currentTimeMillis(), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis()+300000, pendingIntent);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void set_local_notification_next(Context context,String notification_message,String notification_type,String threshold,String frequency,int current_frequency){

        try{

            Intent notifyIntent = new Intent(context, LocalNotificationReceiver.class);
            notifyIntent.putExtra("notification_message",notification_message);
            notifyIntent.putExtra("notification_type",notification_type);
            notifyIntent.putExtra("threshold",threshold);
            notifyIntent.putExtra("frequency",frequency);
            notifyIntent.putExtra("current_frequency",current_frequency);
            PendingIntent pendingIntent = PendingIntent.getBroadcast
                    (context, (int)System.currentTimeMillis(), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis()+20000, pendingIntent);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void check_food_sleep_ex_exist(String food_type){

        long start_timestamp = System.currentTimeMillis()-Integer.valueOf(threshold)*60*60*1000;
        long end_timestamp = System.currentTimeMillis();


        dbHelper.check_food_dleep_ex_exist(start_timestamp, end_timestamp, notification_type, food_type, new OnDataFetched() {
            @Override
            public void data(Object object, int i) {

            }

            @Override
            public void data(Object object) {

                boolean is_exist = (boolean) object;
                if (!is_exist && is_notify) {
                    show_notification(context_, notification_message, notification_type);

                    if (current_frequency < Integer.valueOf(frequency)) {

                        current_frequency++;

                        if (current_frequency == Integer.valueOf(frequency)) {
                            set_local_notification(context_, notification_message, notification_type, threshold, frequency);
                        } else {
                            set_local_notification_next(context_, notification_message, notification_type, threshold, frequency, current_frequency);
                        }
                    }
                } else if (is_exist) {

                    set_local_notification(context_, notification_message, notification_type, threshold, frequency);
                }


            }
        });


    }



}
