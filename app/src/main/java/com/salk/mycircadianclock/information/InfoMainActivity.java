package com.salk.mycircadianclock.information;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.Utility.TabbarClick;
import com.salk.mycircadianclock.information.contact.ContactActivity;
import com.salk.mycircadianclock.information.faq.FaqActivity;
import com.salk.mycircadianclock.information.getting_strated.GettingStartedActivity;
import com.salk.mycircadianclock.information.how_to_videos.HowToVideosActivity;
import com.salk.mycircadianclock.information.tips.TipsActivity;

public class InfoMainActivity extends AppCompatActivity {

    //Todo declaration of XML views
    private RelativeLayout tabbar, rel_main;
    private Button getting_started_btn,btn_videos,faqs_btn,tips_btn,contact_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            //to make screen full screen
            new CommonUsedmethods().makeActivityFullscreen(InfoMainActivity.this);

            setContentView(R.layout.activity_info_main);

            init();

            intializacommonclass();

            click_function();


        }catch (Exception e){
            e.printStackTrace();
        }


    }


    //Todo methos to intialize XML views
    private void init(){
        try{

            tabbar = findViewById(R.id.tab_bar);
            rel_main = findViewById(R.id.main);
            getting_started_btn = findViewById(R.id.getting_started_btn);
            btn_videos = findViewById(R.id.btn_videos);
            faqs_btn = findViewById(R.id.faqs_btn);
            tips_btn = findViewById(R.id.tips_btn);
            contact_btn = findViewById(R.id.contact_btn);



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo to initialize common classes
    private void intializacommonclass(){

        try{

            new TabbarClick().click(InfoMainActivity.this, tabbar, rel_main, "info");


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo to add click functions to views
    private void click_function(){
        try{

            getting_started_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(InfoMainActivity.this, GettingStartedActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    finish();
                }
            });

            btn_videos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(InfoMainActivity.this, HowToVideosActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    finish();
                }
            });

            faqs_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(InfoMainActivity.this, FaqActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    finish();
                }
            });

            tips_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(InfoMainActivity.this, TipsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    finish();
                }
            });

            contact_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(InfoMainActivity.this, ContactActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    finish();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
