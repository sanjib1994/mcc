package com.salk.mycircadianclock.localDatabase;

import android.content.Context;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

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

public class DatabaseRepo {

    private String DB_NAME = "Salk_db";

    private Database  salkDatabase;

    public DatabaseRepo(Context context) {
        salkDatabase = Room.databaseBuilder(context, Database.class, DB_NAME).build();
    }

    //Todo Food
    public void insert_Participant_Common_Food(final ArrayList<FetchCommonFood> arrayList) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().insertCommonFood(arrayList);

                return null;
            }
        }.execute();


    }

    //Todo Food
    public void insert_Participant_Common_Food(final FetchCommonFood fetchCommonFood) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().insertCommonFood(fetchCommonFood);

                return null;
            }
        }.execute();


    }
    //Todo Food
    public int insertFood(final FoodStuff note) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().insertFood(note);

                return null;
            }
        }.execute();

        return note.getId();
    }

    public void updateFood(final FoodStuff note) {
        note.setStatus("Completed");

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().updateFood_status(note);
                return null;
            }
        }.execute();
    }

    public void deleteFood(final FoodStuff note) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().deleteFood_Stuff(note);
                return null;
            }
        }.execute();
    }

    public FoodStuff getFood(Long time) {
        return salkDatabase.daoAccess().getFood(time);
    }

    public void clear_common_food() {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                 salkDatabase.daoAccess().clear_common_food_table();
                 return null;
            }
        }.execute();

    }

    public void get_common_food(final OnDataFetched onDataFetched) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                onDataFetched.data(salkDatabase.daoAccess().get_Participant_Food());
                return null;
            }
        }.execute();

    }
    public void get_common_food(final String food_name,final String food_type,final OnDataFetched onDataFetched) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                onDataFetched.data(salkDatabase.daoAccess().get_Participant_Food(food_name,food_type));
                return null;
            }
        }.execute();


    }

    public void updateCommonFood(final FetchCommonFood fetchCommonFood) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().update_common_food_count(fetchCommonFood);
                return null;
            }
        }.execute();
    }

    //Todo Taglater
    public void insertTaglaterImage(final Taglater taglater) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().insertTagLater(taglater);

                return null;
            }
        }.execute();

    }
    public void deleteTaglater(final Taglater taglater) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().deleteTagLater(taglater);
                return null;
            }
        }.execute();
    }

    public LiveData<List<Taglater>> getTaglaterImages() {
        return salkDatabase.daoAccess().getTaglaterImages();
    }

    public Taglater getTaglater(Long time) {
        return salkDatabase.daoAccess().getTagLater(time);
    }


    //Todo FoodSleepExercise common table
    public void insertFoodSleepExdata(final FoodSleepExData foodSleepExData) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().insertFoodSleepExdata(foodSleepExData);

                return null;
            }
        }.execute();

    }
    public void deleteFoodSleepEx(final FoodSleepExData foodSleepExData) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().deleteFoodSleepEx(foodSleepExData);
                return null;
            }
        }.execute();
    }

    public void updateFoodSleepExdata_(final Long timee) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                FoodSleepExData foodSleepExData = salkDatabase.daoAccess().getfoodsleepexdata_(timee);

                if(foodSleepExData!=null) {
                    foodSleepExData.setServer_status("success");
                    updateFoodSleepEx(foodSleepExData);
                }
                return null;
            }
        }.execute();

    }
    public LiveData<List<FoodSleepExData>> getFoodSleepExdata(Long timee) {
        return salkDatabase.daoAccess().getfoodsleepexdatat(timee);
    }

    public void fetch_delete_FoodSleepExdata(final Long time,final String type) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                FoodSleepExData foodSleepExData = salkDatabase.daoAccess().ftechfoodsleepexdatat(time, type);

                if(foodSleepExData!=null) {
                    salkDatabase.daoAccess().deleteFoodSleepEx(foodSleepExData);
                }
                return null;
            }
        }.execute();
    }

    public void fetch_delete_FoodSleepExdata_sync(final String status, final String type) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                List<FoodSleepExData> foodSleepExData = salkDatabase.daoAccess().fetch_sleep_data_(status, type);

                if(foodSleepExData!=null) {
                    salkDatabase.daoAccess().deleteFoodSleepEx_arr(foodSleepExData);
                }
                return null;
            }
        }.execute();
    }

    public void updateFoodSleepEx(final FoodSleepExData foodSleepExData) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().updateFoodSleepExdata(foodSleepExData);
                return null;
            }
        }.execute();
    }
    public LiveData<FoodSleepExData> getLastFoodTaken() {
        return salkDatabase.daoAccess().getLastTakenFood("food");
    }


    //Todo Sleep
    public void insertSleepEntries(final SleepEntries sleepEntries) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().insertSleep(sleepEntries);

                return null;
            }
        }.execute();

    }

    public void updateSleep(final SleepEntries sleepEntries) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().updateSleep_status(sleepEntries);
                return null;
            }
        }.execute();
    }

    public void deleteSleep(final Long time) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                SleepEntries sleepEntries = salkDatabase.daoAccess().getSleep(time);

                if(sleepEntries!=null){
                    salkDatabase.daoAccess().deleteSleep(sleepEntries);
                }

                return null;
            }
        }.execute();
    }

    public void deleteSleep(final SleepEntries sleepEntries) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().deleteSleep(sleepEntries);
                return null;
            }
        }.execute();
    }

    public SleepEntries getSleep(Long time) {
        return salkDatabase.daoAccess().getSleep(time);
    }

    public void getSleeps(final Long time,final String type,final OnDataFetched onDataFetched) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                List<FoodSleepExData> foodSleepExData =  salkDatabase.daoAccess().getSleeps(time,type);

                onDataFetched.data(foodSleepExData);
                return null;
            }
        }.execute();

    }

    public void getSleep_for_date(final Long time, final int i, final String type,final OnDataFetched onDataFetched) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                List<FoodSleepExData> list =  salkDatabase.daoAccess().getSleep_fordate(time,type);

                onDataFetched.data(list,i);
                return null;
            }
        }.execute();
    }

    //Todo Exercise
    public void insertExerciseEntries(final ExerciseEntries exerciseEntries) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().insertExercise(exerciseEntries);

                return null;
            }
        }.execute();

    }

    public void deleteExercise(final ExerciseEntries exerciseEntries) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().deleteExercise(exerciseEntries);
                return null;
            }
        }.execute();
    }

    public ExerciseEntries getExercise(Long time) {
        return salkDatabase.daoAccess().getExercise(time);
    }

    //Todo Health-Vitals
    public void insert_health_vitals(final HealthSaveRequest healthSaveRequest) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().insertHealthRequest(healthSaveRequest);

                return null;
            }
        }.execute();
    }

    public void deleteHealthRequest(final HealthSaveRequest healthSaveRequest) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().deleteHealthRequest(healthSaveRequest);
                return null;
            }
        }.execute();
    }

    public HealthSaveRequest getHealthRequest(Long time) {
        return salkDatabase.daoAccess().getHealthRequest(time);
    }

    public void check_health_data_existance_and_update(final Long time,final HealthData healthData_) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                HealthData healthData = salkDatabase.daoAccess().getHealthData(time,healthData_.getHealthData_type());

                if(healthData ==null){

                    insert_health_data(healthData_);
                }else{

                    deleteHealthData(healthData);
                    insert_health_data(healthData_);
                }
                return null;
            }
        }.execute();

    }

    public void insert_health_data(final HealthData healthData) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().insertHealthData(healthData);

                return null;
            }
        }.execute();
    }

    public void deleteHealthData(final HealthData healthData) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().deleteHealthData(healthData);
                return null;
            }
        }.execute();
    }


    //Todo settings operation
    public int insertSettingsData(final SettingsTable settingsTable) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().insertSettings(settingsTable);

                return null;
            }
        }.execute();

        return settingsTable.getId();
    }

    public void getSettingsData(final OnDataFetched onDataFetched) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                onDataFetched.data(salkDatabase.daoAccess().getSettingsdata());
                return null;
            }
        }.execute();
    }

    public void updateSettingsData(final SettingsTable settingsTable) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().updatesettingsTable(settingsTable);
                return null;
            }
        }.execute();
    }

    //Todo Pending food operation
    public void get_Pending_count(final OnDataFetched onDataFetched) {


        new AsyncTask<Void, Void, Void>() {

            ArrayList<Long> arrayList = new ArrayList<>();

            @Override
            protected Void doInBackground(Void... voids) {

                arrayList.add(salkDatabase.daoAccess().get_Pending_food_count());
                arrayList.add(salkDatabase.daoAccess().get_Pending_sleep_count());
                arrayList.add(salkDatabase.daoAccess().get_Pending_exercise_count());
                arrayList.add(salkDatabase.daoAccess().get_Pending_health_count());

                onDataFetched.data(arrayList);
                return null;
            }
        }.execute();
    }

    public void get_Pending_food(final OnDataFetched onDataFetched) {


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {


                onDataFetched.data(salkDatabase.daoAccess().get_pending_food());
                return null;
            }
        }.execute();
    }

    public void get_Pending_health(final OnDataFetched onDataFetched) {


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {


                onDataFetched.data(salkDatabase.daoAccess().get_pending_health());
                return null;
            }
        }.execute();
    }

    public void get_Pending_sleep(final OnDataFetched onDataFetched) {


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {


                onDataFetched.data(salkDatabase.daoAccess().get_pending_sleep());
                return null;
            }
        }.execute();
    }

    public void get_Pending_exercise(final OnDataFetched onDataFetched) {


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {


                onDataFetched.data(salkDatabase.daoAccess().get_pending_exercise());
                return null;
            }
        }.execute();
    }

    public void fetch_delete_healthdata(final Long time) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                HealthData healthData = salkDatabase.daoAccess().getHealthData(time);

                if(healthData!=null) {
                    salkDatabase.daoAccess().deleteHealthData(healthData);
                }
                return null;
            }
        }.execute();
    }

    //Todo Activity recogniser operation
    public int insertActivityRecogniserData(final ActivityRecogniser activityRecogniser) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().insertActivityRecogniser(activityRecogniser);

                return null;
            }
        }.execute();

        return activityRecogniser.getId();
    }
    public void getActivityRecogSum(final Long from_time, final Long to_time,final OnDataFetched onDataFetched) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                onDataFetched.data(salkDatabase.daoAccess().getActivityRecognizerSum(from_time,to_time));

                return null;
            }
        }.execute();

    }
    public void getActivityRecogdatas(final Long from_time, final Long to_time,final OnDataFetched onDataFetched) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                onDataFetched.data(salkDatabase.daoAccess().getActivityRecognizerdatas(from_time,to_time));

                return null;
            }
        }.execute();

    }

    public void getActivityRecogdata(final OnDataFetched onDataFetched) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                onDataFetched.data(salkDatabase.daoAccess().getActivityRecognizerdata());

                return null;
            }
        }.execute();

    }

    public void deleteActivityRecogdata(final List<ActivityRecogniser> activityRecognisers) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().deleteActivityRecogniser(activityRecognisers);

                return null;
            }
        }.execute();

    }

    public void get_week_ex(final long start_time, final long end_time, final String type, final OnDataFetched onDataFetched) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                onDataFetched.data(salkDatabase.daoAccess().getWeekExercise(start_time,end_time,type));
                return null;
            }
        }.execute();

    }

    public void getLastSleep(final OnDataFetched onDataFetched) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                onDataFetched.data( salkDatabase.daoAccess().getLastSleep("sleep"));
                return null;
            }
        }.execute();

    }

    public void insert_fit_step_count(final ArrayList<GFitStepCount> arrayList) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().insertFitStepCount(arrayList);

                return null;
            }
        }.execute();


    }

    public void clear_step_count() {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().clear_step_count_table();
                return null;
            }
        }.execute();

    }


    public void insert_local_noptifications(final ArrayList<ModelLocalNotification> arrayList) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                salkDatabase.daoAccess().insertlocalnotifications(arrayList);

                return null;
            }
        }.execute();


    }
    public void is_notification_on(final OnDataFetched onDataFetched) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                onDataFetched.data(salkDatabase.daoAccess().is_notification_on());

                return null;
            }
        }.execute();


    }
    public void check_food_dleep_ex_exist(final Long from_time, final Long to_time,
                                          final String type, final String food_type,final OnDataFetched onDataFetched) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                onDataFetched.data(salkDatabase.daoAccess().is_food_sleep_ex_exist(from_time, to_time, type, food_type));

                return null;
            }
        }.execute();


    }
}
