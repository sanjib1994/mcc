package com.salk.mycircadianclock.localDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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

import java.util.ArrayList;
import java.util.List;

@Dao
public interface DaoAccess {

    @Insert
    Long insertFood(FoodStuff foodStuff);

    @Insert
    void insertCommonFood(List<FetchCommonFood> fetchCommonFoodList);

    @Insert
    void insertFitStepCount(List<GFitStepCount> gFitStepCountList);

    @Insert
    void insertCommonFood(FetchCommonFood fetchCommonFood);


    @Insert
    Long insertTagLater(Taglater taglater);

    @Insert
    Long insertFoodSleepExdata(FoodSleepExData foodSleepExData);

    @Insert
    Long insertSleep(SleepEntries sleepEntries);

    @Insert
    Long insertSettings(SettingsTable settingsTable);

    @Insert
    Long insertExercise(ExerciseEntries exerciseEntries);

    @Insert
    void insertHealthRequest(HealthSaveRequest healthSaveRequest);

    @Insert
    void insertHealthData(HealthData healthData);

    @Insert
    void insertActivityRecogniser(ActivityRecogniser activityRecogniser);

    @Query("SELECT * FROM FoodStuff WHERE status =:status")
    LiveData<List<FoodStuff>> getFood(String status);

    @Update
    void updateFood_status(FoodStuff note);

    @Update
    void update_common_food_count(FetchCommonFood fetchCommonFood);

    @Update
    void updateSleep_status(SleepEntries sleepEntries);
    @Update
    void updateFoodSleepExdata(FoodSleepExData foodSleepExData);

    @Update
    void updatesettingsTable(SettingsTable settingsTable);

    @Delete
    void deleteFood_Stuff(FoodStuff note);
    @Delete
    void deleteFoodSleepEx(FoodSleepExData foodSleepExData);
    @Delete
    void deleteFoodSleepEx_arr(List<FoodSleepExData> foodSleepExData);

    @Delete
    void deleteTagLater(Taglater taglater);

    @Delete
    void deleteSleep(SleepEntries sleepEntries);

    @Delete
    void deleteHealthRequest(HealthSaveRequest healthSaveRequest);

    @Delete
    void deleteHealthData(HealthData healthData);

    @Delete
    void deleteActivityRecogniser(List<ActivityRecogniser> activityRecognisers);

    @Query("DELETE FROM GFitStepCount")
    void clear_step_count_table();

    @Query("DELETE FROM FetchCommonFood")
    void clear_common_food_table();

    @Query("SELECT * FROM FetchCommonFood")
    List<FetchCommonFood> get_Participant_Food();

    @Query("SELECT * FROM FetchCommonFood WHERE annotation_text=:food_name AND food_type=:food_type")
    FetchCommonFood get_Participant_Food(String food_name,String food_type);

    @Delete
    void deleteExercise(ExerciseEntries exerciseEntries);

    @Query("SELECT * FROM FoodStuff WHERE timestamp =:time")
    FoodStuff getFood(Long time);

    @Query("SELECT * FROM Taglater")
    LiveData<List<Taglater>> getTaglaterImages();

    @Query("SELECT * FROM Taglater WHERE timestamp =:time")
    Taglater getTagLater(Long time);

    @Query("SELECT * FROM FoodSleepExData WHERE timestamp >=:time")
    LiveData<List<FoodSleepExData>> getfoodsleepexdatat(Long time);

    @Query("SELECT * FROM FoodSleepExData WHERE actual_log_request =:time")
    FoodSleepExData getfoodsleepexdata_(Long time);

    @Query("SELECT * FROM FoodSleepExData WHERE actual_log_request ==:time AND type =:type")
    FoodSleepExData ftechfoodsleepexdatat(Long time,String type);

    @Query("SELECT * FROM FoodSleepExData WHERE server_status=:status AND type =:type")
    List<FoodSleepExData> fetch_sleep_data_(String status,String type);

    @Query("SELECT * FROM FoodSleepExData WHERE sleep_actual_date =:time AND type =:type")
    List<FoodSleepExData> getSleeps(Long time,String type);

    @Query("SELECT * FROM FoodSleepExData WHERE type=:type ORDER BY timestamp DESC LIMIT 1")
    LiveData<FoodSleepExData> getLastTakenFood(String type);
    @Query("SELECT * FROM FoodSleepExData WHERE type=:type ORDER BY timestamp DESC LIMIT 1")
    FoodSleepExData getLastSleep(String type);

    @Query("SELECT * FROM SleepEntries WHERE timestamp =:time")
    SleepEntries getSleep(Long time);

    @Query("SELECT * FROM FoodSleepExData WHERE timestamp =:time AND type=:type")
    List<FoodSleepExData> getSleep_fordate(Long time,String type);

    @Query("SELECT * FROM ExerciseEntries WHERE timestamp =:time")
    ExerciseEntries getExercise(Long time);

    @Query("SELECT * FROM HealthSaveRequest WHERE timestamp =:time")
    HealthSaveRequest getHealthRequest(Long time);

    @Query("SELECT * FROM HealthData WHERE timestamp =:time AND healthData_type=:type")
    HealthData getHealthData(Long time,String type);

    @Query("SELECT * FROM SettingsTable")
    SettingsTable getSettingsdata();

    @Query("SELECT COUNT(id) FROM FoodStuff")
    long get_Pending_food_count();

    @Query("SELECT COUNT(id) FROM SleepEntries")
    long get_Pending_sleep_count();

    @Query("SELECT COUNT(id) FROM ExerciseEntries")
    long get_Pending_exercise_count();

    @Query("SELECT COUNT(id) FROM HealthSaveRequest")
    long get_Pending_health_count();

    @Query("SELECT * FROM FoodStuff")
    List<FoodStuff> get_pending_food();

    @Query("SELECT * FROM SleepEntries")
    List<SleepEntries> get_pending_sleep();

    @Query("SELECT * FROM ExerciseEntries")
    List<ExerciseEntries> get_pending_exercise();

    @Query("SELECT * FROM HEALTHSAVEREQUEST")
    List<HealthSaveRequest> get_pending_health();

    @Query("SELECT * FROM HealthData WHERE actual_log_time =:time")
    HealthData getHealthData(Long time);

    @Query("SELECT *FROM ActivityRecogniser ORDER BY id ASC LIMIT 1")
    ActivityRecogniser getActivityRecognizerdata();

    @Query("SELECT SUM(activity_count) FROM ActivityRecogniser WHERE activity_time BETWEEN :from_time AND :to_time")
    int getActivityRecognizerSum(Long from_time, Long to_time);

    @Query("SELECT *FROM ActivityRecogniser WHERE activity_time BETWEEN :from_time AND :to_time")
    List<ActivityRecogniser> getActivityRecognizerdatas(Long from_time, Long to_time);

    @Query("SELECT *FROM FoodSleepExData WHERE type =:type AND timestamp BETWEEN :from_time AND :to_time")
    List<FoodSleepExData> getWeekExercise(Long from_time, Long to_time,String type);

    @Insert
    void insertlocalnotifications(List<ModelLocalNotification> modelLocalNotificationList);

    @Query("SELECT is_notify from ModelLocalNotification LIMIT 1")
    boolean is_notification_on();

    @Query("SELECT COUNT()>0 FROM FoodSleepExData WHERE (timestamp BETWEEN :from_time AND :to_time) AND type=:type AND  food_type = :food_type")
    boolean is_food_sleep_ex_exist(Long from_time, Long to_time,String type,String food_type);
}
