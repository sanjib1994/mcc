package com.salk.mycircadianclock.settings.local_notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import static android.content.Context.ALARM_SERVICE;

public class SetLocalNotification {


    public void startAlarmBroadcastReceiver(Context context, long timeStamp,String body) {

        Intent _intent = new Intent(context, ReminderNotificationReceiver.class);
        _intent.putExtra("body",body);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)timeStamp, _intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        // Remove any previous pending intent.
        //alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeStamp, pendingIntent);
    }

    public void set_local_notification(Context context,String notification_message,String notification_type,String threshold,String frequency){

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
            alarmManager.set(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis()+60000, pendingIntent);

        }catch (Exception e){
            e.printStackTrace();
        }

    }


}