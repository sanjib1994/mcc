package com.salk.mycircadianclock.information.contact;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RelativeLayout;

import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.Utility.ConnectionDetector;
import com.salk.mycircadianclock.Utility.TabbarClick;
import com.salk.mycircadianclock.information.tips.TipsActivity;

public class ContactActivity extends AppCompatActivity {

    //Todo declaration of XML views
    private RelativeLayout tabbar, rel_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            //to make screen full screen
            new CommonUsedmethods().makeActivityFullscreen(ContactActivity.this);

            setContentView(R.layout.activity_contact);

            init();

            intializacommonclass();



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo methos to intialize XML views
    private void init(){
        try{

            tabbar = findViewById(R.id.tab_bar);
            rel_main = findViewById(R.id.main);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo to initialize common classes
    private void intializacommonclass(){

        try{

            new TabbarClick().click(ContactActivity.this, tabbar, rel_main, "info");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
