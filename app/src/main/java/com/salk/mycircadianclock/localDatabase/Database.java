package com.salk.mycircadianclock.localDatabase;

import androidx.room.RoomDatabase;

import com.salk.mycircadianclock.activityrecognizer.ActivityRecogniser;
import com.salk.mycircadianclock.camera.GFitStepCount;
import com.salk.mycircadianclock.exercise.ExerciseEntries;
import com.salk.mycircadianclock.food.FetchCommonFood;
import com.salk.mycircadianclock.food.FoodStuff;
import com.salk.mycircadianclock.food.Taglater;
import com.salk.mycircadianclock.health_vitals.HealthData;
import com.salk.mycircadianclock.health_vitals.HealthSaveRequest;
import com.salk.mycircadianclock.settings.SettingsTable;
import com.salk.mycircadianclock.settings.local_notification.ModelLocalNotification;
import com.salk.mycircadianclock.sleep.SleepEntries;

@androidx.room.Database(entities = {FoodStuff.class, Taglater.class,FoodSleepExData.class, SleepEntries.class,
        ExerciseEntries.class, FetchCommonFood.class, HealthSaveRequest.class, HealthData.class,
        SettingsTable.class, ActivityRecogniser.class, GFitStepCount.class, ModelLocalNotification.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {

    public abstract DaoAccess daoAccess();
}