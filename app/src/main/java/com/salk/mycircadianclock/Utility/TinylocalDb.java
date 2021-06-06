package com.salk.mycircadianclock.Utility;

import android.app.Activity;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;


public class TinylocalDb {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public SharedPreferences.Editor create_shared_pref(Activity activity){
        pref = activity.getSharedPreferences("mcc_Pref", MODE_PRIVATE);
        editor = pref.edit();

        return  editor;
    }

    public SharedPreferences.Editor create_shared_pref(Activity activity,String name){
        pref = activity.getSharedPreferences(name, MODE_PRIVATE);
        editor = pref.edit();

        return  editor;
    }

    public SharedPreferences get_shared_pref(Activity activity){
        pref = activity.getSharedPreferences("mcc_Pref", MODE_PRIVATE);

        return pref;
    }

    public SharedPreferences get_shared_pref_sleep(Activity activity){
        pref = activity.getSharedPreferences("sleep_Pref", MODE_PRIVATE);

        return pref;
    }

    public SharedPreferences get_shared_pref_exercise(Activity activity){
        pref = activity.getSharedPreferences("exercise_Pref", MODE_PRIVATE);

        return pref;
    }

    public void put_data_in_shared(SharedPreferences.Editor editor,String key, String value){

        editor.putString(key,value);
        editor.commit();

    }
    public void put_data_in_shared(SharedPreferences sharedPreferences,String key, String value){

        editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();

    }
    public String get_data_in_shared(SharedPreferences pref,String key){

        String value = "";
        if(pref.contains(key)){
            value = pref.getString(key, "");
        }else{
            value = "";
        }

        return value;
    }

    public void clear_shared_pref_data(SharedPreferences pref){

        pref.edit().clear().commit();

    }

    public void clear_key_from_shared_pref(SharedPreferences pref,String key){

        pref.edit().remove(key).commit();

    }
}
