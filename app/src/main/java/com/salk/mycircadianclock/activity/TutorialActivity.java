package com.salk.mycircadianclock.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.Utility.OnSwipeTouchListener;
import com.salk.mycircadianclock.adapter.TutorialsAdapter;
import com.salk.mycircadianclock.camera.CustomCameraActivity;

import java.util.ArrayList;
import java.util.Locale;

import me.relex.circleindicator.CircleIndicator;

public class TutorialActivity extends AppCompatActivity {

    ViewPager viewPager;
    CircleIndicator circleIndicator;
    ImageView image_dummy;

    String language;
    ArrayList<Integer> slider_image_list;
    TutorialsAdapter tutorialsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            //to make luncher screen full screen
            new CommonUsedmethods().makeActivityFullscreen(TutorialActivity.this);

            setContentView(R.layout.activity_tutorial);

            //initialization of all view
            init();

            //click function for required views
            click_function();

            //set image for adapter
            set_image_in_adpter();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void init(){

        viewPager = findViewById(R.id.viewpager);
        circleIndicator = findViewById(R.id.indicator);
        image_dummy = findViewById(R.id.rel_dummy);

        language = Locale.getDefault().getDisplayLanguage();
        slider_image_list = new ArrayList<>();
    }

    private void click_function(){

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(position==9){
                    image_dummy.setVisibility(View.VISIBLE);
                }else{
                    image_dummy.setVisibility(View.GONE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });

        image_dummy.setOnTouchListener(new OnSwipeTouchListener(TutorialActivity.this) {

            @Override
            public void onSwipeLeft() {

                if(getIntent().hasExtra("login") && getIntent().getBooleanExtra("login",false)){

                    Intent intent = new Intent(TutorialActivity.this, CustomCameraActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    finish();
                    overridePendingTransition(0, 0);
                }
            }

            @Override
            public void onSwipeRight() {
                image_dummy.setVisibility(View.GONE);
                viewPager.setCurrentItem(slider_image_list.size()-2);
            }
        });

    }

    private void set_image_in_adpter(){

        if (language.matches("français")) {

            slider_image_list.add(R.mipmap.new_user_french_slide_1);
            slider_image_list.add(R.mipmap.new_user_french_slide_2);
            slider_image_list.add(R.mipmap.new_user_french_slide_3);
            slider_image_list.add(R.mipmap.new_user_french_slide_4);
            slider_image_list.add(R.mipmap.new_user_french_slide_5);
            slider_image_list.add(R.mipmap.new_user_french_slide_6);
            slider_image_list.add(R.mipmap.new_user_french_slide_7);
            slider_image_list.add(R.mipmap.new_user_french_slide_8);
            slider_image_list.add(R.mipmap.new_user_french_slide_9);
            slider_image_list.add(R.mipmap.new_user_french_slide_10);

        } else {

            slider_image_list.add(R.mipmap.new_user_slide_1);
            slider_image_list.add(R.mipmap.new_user_slide_2);
            slider_image_list.add(R.mipmap.new_user_slide_3);
            slider_image_list.add(R.mipmap.new_user_slide_4);
            slider_image_list.add(R.mipmap.new_user_slide_5);
            slider_image_list.add(R.mipmap.new_user_slide_6);
            slider_image_list.add(R.mipmap.new_user_slide_7);
            slider_image_list.add(R.mipmap.new_user_slide_8);
            slider_image_list.add(R.mipmap.new_user_slide_9);
            slider_image_list.add(R.mipmap.new_user_slide_10);

        }

        tutorialsAdapter = new TutorialsAdapter(TutorialActivity.this,
                slider_image_list);
        viewPager.setAdapter(tutorialsAdapter);
        circleIndicator.setViewPager(viewPager);

    }

    @Override
    public void onBackPressed() {

        finish();
        overridePendingTransition(0,0);
    }
}
