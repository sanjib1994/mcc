package com.salk.mycircadianclock.information.tips;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.Utility.TabbarClick;

public class TipsActivity extends AppCompatActivity {

    //Todo declaration of XML views
    private RelativeLayout tabbar, rel_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            //to make screen full screen
            new CommonUsedmethods().makeActivityFullscreen(TipsActivity.this);

            setContentView(R.layout.activity_tips);

            init();

            initializecommonclass();



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo methods to initialize XML views
    private void init(){
        try{

            tabbar = findViewById(R.id.tab_bar);
            rel_main = findViewById(R.id.main);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo to initialize common classes
    private void initializecommonclass(){

        try{

            new TabbarClick().click(TipsActivity.this, tabbar, rel_main, "info");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
