package com.salk.mycircadianclock.api;

import com.salk.mycircadianclock.ApiBaseUrl;

public class ApiConfig {

    public static final String YOUR_SERVER_URL = ApiBaseUrl.YOUR_SERVER_URL;

    //login url
    public static final String LOGIN_URL = ApiBaseUrl.YOUR_SERVER_URL+"user-login/";
    public static final String FOOD_SAVE_URL = ApiBaseUrl.YOUR_SERVER_URL+"participant-eating-info/";
    public static final String SLEEP_SAVE_URL = ApiBaseUrl.YOUR_SERVER_URL+"participant-sleep-info/";
    public static final String EXERCISE_SAVE_URL = ApiBaseUrl.YOUR_SERVER_URL+"participant-exercise-info/";
    public static final String HEALTH_SAVE_URL = ApiBaseUrl.YOUR_SERVER_URL+"participant-health-vital/";
    public static final String FETCH_COMMON_FOOD_URL = ApiBaseUrl.YOUR_SERVER_URL+"participant-common-food/";
    public static final String FETCH_PARTICIPANT_SETTINGS = ApiBaseUrl.YOUR_SERVER_URL+"fetch-participant-settings/";
    public static final String SETTINGS_SAVE = ApiBaseUrl.YOUR_SERVER_URL+"save-participant-settings/";
    public static final String FOOD_COLLAGE_API = ApiBaseUrl.YOUR_SERVER_URL+"participant-food-collage/";
    public static final String FOOD_LIST_API = ApiBaseUrl.YOUR_SERVER_URL+"participant-food-list/";
    public static final String FOOD_CHAT_API = ApiBaseUrl.YOUR_SERVER_URL+"participant-food-chart/";
    public static final String ACTIVITY_LIST_API = ApiBaseUrl.YOUR_SERVER_URL+"participant-activity-list/";
    public static final String ACTIVITY_CHART_API = ApiBaseUrl.YOUR_SERVER_URL+"participant-activity-chart/";
    public static final String ACTIVITY_SAVE_API = ApiBaseUrl.YOUR_SERVER_URL+"participant-activity-info/";
    public static final String SYNC_SLEEP_EXERCISE = ApiBaseUrl.YOUR_SERVER_URL+"sync-sleep-exercise-data/";
    public static final String SURVEY_COUNT = ApiBaseUrl.YOUR_SERVER_URL+"fetch-survey/";
    public static final String SURVEY_QUESTION_LIST = ApiBaseUrl.YOUR_SERVER_URL+"fetch-survey-question-list/";
    public static final String TIPS = ApiBaseUrl.YOUR_SERVER_URL+"fetch-research-notes/";
    public static final String CONTACT = ApiBaseUrl.YOUR_SERVER_URL+"participant-feedback/";

}
