package com.salk.mycircadianclock.survey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.Utility.ConnectionDetector;
import com.salk.mycircadianclock.Utility.FancyButton;
import com.salk.mycircadianclock.Utility.TinylocalDb;
import com.salk.mycircadianclock.api.AESEncryption;
import com.salk.mycircadianclock.api.APIManager;
import com.salk.mycircadianclock.api.ApiConfig;
import com.salk.mycircadianclock.api.Apicallback;
import com.salk.mycircadianclock.camera.CustomCameraActivity;
import com.salk.mycircadianclock.interfaces.OnRecycleItemClicks;
import com.salk.mycircadianclock.localDatabase.FoodSleepExData;
import com.salk.mycircadianclock.sleep.LogSleepDataActivity;
import com.salk.mycircadianclock.sleep.SleepDataEditAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SurveyActivity extends AppCompatActivity {

    private FancyButton btn_exit,btn_next;
    private RelativeLayout rel_top,rel_survey_questions;
    private LinearLayout lin_main;
    private ImageView image_back;
    private RecyclerView recyclerView;
    private TextView text_survey_header,text_survey_title;

    private SurveyAnswerAdapter surveyAnswerAdapter;
    private SurveyModel surveyModel;
    private TinylocalDb tinylocalDb;
    private ConnectionDetector connectionDetector;
    private CommonUsedmethods commonUsedmethods;
    private SharedPreferences sharedPreferences;
    private String user_type = "",auth_token = "",research_info_id = "",user_id="",survey_id ="",survey_header="",survey_title="";
    private int index = 0,no_of_baseline_day,no_of_intervention_day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            new CommonUsedmethods().makeActivityFullscreen(SurveyActivity.this);

            setContentView(R.layout.activity_survey);

            init();

            click_function();

            no_of_baseline_day = getIntent().getIntExtra("no_baseline_day",0);
            no_of_intervention_day = getIntent().getIntExtra("no_intervention_day",0);

            call_survey_question_API();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void init(){

        try{

            btn_exit = findViewById(R.id.exitQusbutton);
            btn_next = findViewById(R.id.nextQusbutton);
            rel_top = findViewById(R.id.top_lay);
            rel_survey_questions = findViewById(R.id.rel_survey_question);
            lin_main = findViewById(R.id.Mainlyout);
            image_back = findViewById(R.id.image_back);
            recyclerView = findViewById(R.id.recycle_answers);
            text_survey_header = findViewById(R.id.testhd);
            text_survey_title = findViewById(R.id.testsubhd);

            tinylocalDb = new TinylocalDb();
            sharedPreferences = tinylocalDb.get_shared_pref(SurveyActivity.this);
            connectionDetector = new ConnectionDetector(SurveyActivity.this);
            commonUsedmethods = new CommonUsedmethods();
            user_type = tinylocalDb.get_data_in_shared(sharedPreferences,"user_type");
            user_id = tinylocalDb.get_data_in_shared(sharedPreferences,"user_id");
            research_info_id = tinylocalDb.get_data_in_shared(sharedPreferences,"research_info_id");
            auth_token = tinylocalDb.get_data_in_shared(sharedPreferences,"auth_token");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void click_function(){

        try{

            btn_exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(btn_next.getText().toString().equalsIgnoreCase("next")){

                        set_answer_lay();
                    }else if(btn_next.getText().toString().equalsIgnoreCase("save")){

                        if(surveyModel.getSurveyQuestionAnswerModelArrayList().
                                get(surveyModel.getSurveyQuestionAnswerModelArrayList().size()-1).getGiven_answer().size()>0){

                            if(connectionDetector!=null && connectionDetector.isConnectingToInternet()) {
                                call_survey_answer_API();
                            }else{

                                commonUsedmethods.show_Toast(SurveyActivity.this,
                                        getResources().getString(R.string.Please_connect_to_internet));
                            }

                        }
                    }

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void prepare_question_array(JSONArray jsonArray){

        try {
            surveyModel = new SurveyModel();
            surveyModel.setSurvey_title(survey_title);
            ArrayList<SurveyQuestionAnswerModel> arrayList = new ArrayList<>();

            SurveyQuestionAnswerModel surveyQuestionAnswerModel = new SurveyQuestionAnswerModel();
            for (int i=0;i<jsonArray.length();i++) {

                surveyQuestionAnswerModel = new SurveyQuestionAnswerModel();
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                surveyQuestionAnswerModel.setAnswer_type(jsonObject.getString("question_type"));
                ArrayList<Options> arrayList1 = new ArrayList<>();
                JSONArray jsonArray1 = jsonObject.getJSONArray("options");
                for(int j=0; j< jsonArray1.length();j++){

                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                    Options options = new Options();
                    options.setOpt_id(jsonObject1.getString("option_id"));
                    options.setOpt_value(jsonObject1.getString("option_value"));
                    arrayList1.add(options);
                }
                surveyQuestionAnswerModel.setAnswers(arrayList1);
                surveyQuestionAnswerModel.setSurvey_question(jsonObject.getString("question"));
                surveyQuestionAnswerModel.setQ_id(jsonObject.getString("question_id"));
                if(surveyQuestionAnswerModel.getAnswer_type().equalsIgnoreCase("check_box")){
                    surveyQuestionAnswerModel.setSize(arrayList1.size());
                }else {
                    surveyQuestionAnswerModel.setSize(1);
                }
                arrayList.add(surveyQuestionAnswerModel);
            }

            surveyModel.setSurveyQuestionAnswerModelArrayList(arrayList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void set_answer_lay(){

        try {
            if (lin_main.getVisibility() == View.VISIBLE) {

                //Log.d("surveyModel",surveyModel.getSurveyQuestionAnswerModelArrayList().get(index).getGiven_answer().toString());

                if (index < surveyModel.getSurveyQuestionAnswerModelArrayList().size() &&
                        surveyModel.getSurveyQuestionAnswerModelArrayList().get(index).getGiven_answer().size()>0) {

                    if (index+1 == surveyModel.getSurveyQuestionAnswerModelArrayList().size()) {

                        btn_next.setText("SAVE");
                    }

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SurveyActivity.this);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    surveyAnswerAdapter = new SurveyAnswerAdapter(surveyModel.getSurveyQuestionAnswerModelArrayList().get(index).getAnswers(), SurveyActivity.this,
                            surveyModel.getSurveyQuestionAnswerModelArrayList().get(index).getAnswer_type(),
                            surveyModel.getSurveyQuestionAnswerModelArrayList().get(index).getSize(), surveyModel.getSurveyQuestionAnswerModelArrayList().get(index));
                    recyclerView.setAdapter(surveyAnswerAdapter);
                    surveyAnswerAdapter.notifyDataSetChanged();

                }else if( surveyModel.getSurveyQuestionAnswerModelArrayList().get(index).getGiven_answer().size()==0){
                    Toast.makeText(SurveyActivity.this,"Please give your answer to proceed.",Toast.LENGTH_LONG).show();
                }

            } else {

                rel_survey_questions.setVisibility(View.GONE);
                rel_top.setVisibility(View.VISIBLE);
                lin_main.setVisibility(View.VISIBLE);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SurveyActivity.this);
                recyclerView.setLayoutManager(linearLayoutManager);
                surveyAnswerAdapter = new SurveyAnswerAdapter(surveyModel.getSurveyQuestionAnswerModelArrayList().get(index).getAnswers(), SurveyActivity.this,
                        surveyModel.getSurveyQuestionAnswerModelArrayList().get(index).getAnswer_type(),
                        surveyModel.getSurveyQuestionAnswerModelArrayList().get(index).getSize(), surveyModel.getSurveyQuestionAnswerModelArrayList().get(index));
                recyclerView.setAdapter(surveyAnswerAdapter);
                surveyAnswerAdapter.notifyDataSetChanged();

                if (index+1 == surveyModel.getSurveyQuestionAnswerModelArrayList().size()) {

                    btn_next.setText("SAVE");
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void call_survey_question_API(){

        try {
            String url = ApiConfig.SURVEY_QUESTION_LIST;
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("participant_id", AESEncryption.encrypt(user_id));
            jsonObject.put("research_info_id", AESEncryption.encrypt(research_info_id));
            if(no_of_baseline_day==0 && no_of_intervention_day==0){
                jsonObject.put("baseline_day_count", AESEncryption.encrypt("0"));
                jsonObject.put("intervention_day_count", AESEncryption.encrypt("0"));
            }else if(no_of_intervention_day==0){
                jsonObject.put("baseline_day_count", AESEncryption.encrypt(String.valueOf(no_of_baseline_day)));
                jsonObject.put("intervention_day_count", AESEncryption.encrypt("0"));
            }else{
                jsonObject.put("baseline_day_count", AESEncryption.encrypt("0"));
                jsonObject.put("intervention_day_count", AESEncryption.encrypt(String.valueOf(no_of_intervention_day)));
            }

            new APIManager().Apicall(auth_token,url, jsonObject, new Apicallback() {
                @Override
                public void success(int code, String value) {

                    if (code == 200) {

                        //parse response
                        parse_survey_question_response(value, new ApiParse() {

                            @Override
                            public void onSuccess(final Object object) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        text_survey_header.setText(survey_header);
                                        text_survey_title.setText(survey_title);
                                    }
                                });

                            }

                            @Override
                            public void onFailure() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SurveyActivity.this,R.string.something_wrong,Toast.LENGTH_LONG).show();
                                    }
                                });


                            }
                        });
                    } else {

                    }
                }

                @Override
                public void failure(final String value) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SurveyActivity.this, value, Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void parse_survey_question_response(String response, ApiParse apiPasrsing){

        try {
            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject!=null && jsonObject.has("message") && !jsonObject.isNull("message")) {

                JSONObject jsonObject1 = jsonObject.getJSONObject("message");

                String visit_questions = AESEncryption.decrypt(jsonObject1.getString("visit_questions"));
                survey_id = AESEncryption.decrypt(jsonObject1.getString("survey_id"));
                survey_header = AESEncryption.decrypt(jsonObject1.getString("survey_header"));
                survey_title = AESEncryption.decrypt(jsonObject1.getString("survey_title"));

                JSONObject jsonObject2 = new JSONObject(visit_questions);
                JSONArray jsonArray = jsonObject2.getJSONArray("questions");
                prepare_question_array(jsonArray);


                apiPasrsing.onSuccess("");

            }else{

                apiPasrsing.onFailure();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void call_survey_answer_API(){

        try {
            String url = ApiConfig.SURVEY_COUNT;
            JSONObject jsonObject = make_answer_json();

            new APIManager().Apicall(auth_token,url, jsonObject, new Apicallback() {
                @Override
                public void success(int code, String value) {

                    if (code == 200) {


                    } else {

                    }
                }

                @Override
                public void failure(final String value) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SurveyActivity.this, "Failure", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private JSONObject make_answer_json(){

        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            if (surveyModel != null && surveyModel.getSurveyQuestionAnswerModelArrayList() != null &&
                    surveyModel.getSurveyQuestionAnswerModelArrayList().size() > 0) {

                for (int i = 0; i < surveyModel.getSurveyQuestionAnswerModelArrayList().size(); i++) {

                    SurveyQuestionAnswerModel surveyQuestionAnswerModel = surveyModel.getSurveyQuestionAnswerModelArrayList().get(i);
                    JSONObject jsonObject1 = new JSONObject();
                    StringBuilder stringBuilder = new StringBuilder();
                    jsonObject1.put("questionId",surveyQuestionAnswerModel.getQ_id());
                    for(String va : surveyQuestionAnswerModel.getGiven_answer() ){

                        if(stringBuilder.toString().length()==0){
                            stringBuilder.append(va);
                        }else{
                            stringBuilder.append(",");
                            stringBuilder.append(va);
                        }
                    }
                    jsonObject1.put("answer",stringBuilder.toString());
                    jsonArray.put(jsonObject1);
                }
                jsonObject.put("visit_answer",AESEncryption.encrypt(jsonArray.toString()));
                jsonObject.put("survey_id",AESEncryption.encrypt(survey_id));
                jsonObject.put("participant_id", AESEncryption.encrypt(user_id));

                jsonObject.put("research_info_id", AESEncryption.encrypt(research_info_id));

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    private interface ApiParse{

        void onSuccess(Object object);
        void onFailure();
    }


//    visit_answer": [{
//            "answer": "7305",
//            "questionId": "2471"
//}],
//        "survey_id": "928"
//        "participant_id": "1"
//        "research_id": "2"

}
