package com.salk.mycircadianclock.survey;

import java.util.ArrayList;

public class SurveyModel {

    private String survey_title = "";
    private ArrayList<SurveyQuestionAnswerModel> surveyQuestionAnswerModelArrayList;

    public String getSurvey_title() {
        return survey_title;
    }

    public void setSurvey_title(String survey_title) {
        this.survey_title = survey_title;
    }

    public ArrayList<SurveyQuestionAnswerModel> getSurveyQuestionAnswerModelArrayList() {
        return surveyQuestionAnswerModelArrayList;
    }

    public void setSurveyQuestionAnswerModelArrayList(ArrayList<SurveyQuestionAnswerModel> surveyQuestionAnswerModelArrayList) {
        this.surveyQuestionAnswerModelArrayList = surveyQuestionAnswerModelArrayList;
    }
}

class SurveyQuestionAnswerModel {

    private String survey_question = "";
    private ArrayList<Options> answers = new ArrayList<>();
    private String answer_type = "";
    private String q_id = "";
    private int size;
    private ArrayList<String> given_answer = new ArrayList<>();

    public String getSurvey_question() {
        return survey_question;
    }

    public void setSurvey_question(String survey_question) {
        this.survey_question = survey_question;
    }

    public ArrayList<Options> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Options> answers) {
        this.answers = answers;
    }

    public String getAnswer_type() {
        return answer_type;
    }

    public void setAnswer_type(String answer_type) {
        this.answer_type = answer_type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ArrayList<String> getGiven_answer() {
        return given_answer;
    }

    public void setGiven_answer(ArrayList<String> given_answer) {
        this.given_answer = given_answer;
    }

    public String getQ_id() {
        return q_id;
    }

    public void setQ_id(String q_id) {
        this.q_id = q_id;
    }
}

class Options{

    String opt_id ="";
    String opt_value = "";

    public String getOpt_id() {
        return opt_id;
    }

    public void setOpt_id(String opt_id) {
        this.opt_id = opt_id;
    }

    public String getOpt_value() {
        return opt_value;
    }

    public void setOpt_value(String opt_value) {
        this.opt_value = opt_value;
    }
}
