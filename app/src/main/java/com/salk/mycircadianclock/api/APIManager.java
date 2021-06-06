package com.salk.mycircadianclock.api;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class APIManager {

    String response = "";

    public void Apicall(String url, JSONObject json, final Apicallback apicallback){

        OkHttpClient client = okHttpClientBuilder();

        Request requestt = makerequest(url, json);

        client.newCall(requestt).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                response = "Failure";
                apicallback.failure(response);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response responses) throws IOException {
                try {
                    response = responses.body().string();
                    apicallback.success(responses.code(),response);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }


    public void Apicall(String token,String url, JSONObject json, final Apicallback apicallback){

        OkHttpClient client = okHttpClientBuilder();

        Request requestt = makerequest(token,url, json);

        client.newCall(requestt).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                response = "Failure";
                apicallback.failure(response);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response responses) throws IOException {
                try {
                    response = responses.body().string();
                    apicallback.success(responses.code(),response);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }
    //Login request parameter added here
    public Request makerequest(String url,JSONObject json) {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(json));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return request;
    }

    //Login request parameter added here
    public Request makerequest(String token,String url,JSONObject json) {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, String.valueOf(json));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization","Token "+token)
                .post(body)
                .build();

        return request;
    }

    public OkHttpClient okHttpClientBuilder()
    {
        return new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
    }

}
