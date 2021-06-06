package com.salk.mycircadianclock.information.getting_strated;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.Utility.TabbarClick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GettingStartedActivity extends AppCompatActivity {

    //Todo declaration of XML views
    private RelativeLayout tabbar, rel_main;
    private ExpandableListView getting_strated_list;

    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private GettingStartedAdapter gettingStartedAdapter;
    private int lastExpandedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            //to make screen full screen
            new CommonUsedmethods().makeActivityFullscreen(GettingStartedActivity.this);

            setContentView(R.layout.activity_getting_started);

            init();

            intializacommonclass();

            set_list_dat();


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Todo methods to initialize XML views
    private void init(){
        try{

            tabbar = findViewById(R.id.tab_bar);
            rel_main = findViewById(R.id.main);
            getting_strated_list = findViewById(R.id.faqlist);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo to initialize common classes
    private void intializacommonclass(){

        try{

            new TabbarClick().click(GettingStartedActivity.this, tabbar, rel_main, "info");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo set dat in expandable list
    private void set_list_dat(){

        prepareListData();

        gettingStartedAdapter = new GettingStartedAdapter(GettingStartedActivity.this, listDataHeader, listDataChild);

        // setting list adapter
        getting_strated_list.setAdapter(gettingStartedAdapter);

        getting_strated_list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {


                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    getting_strated_list.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;

            }
        });

    }

    //Todo set data in arraylist
    private void prepareListData() {

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listDataHeader.add(getResources().getString(R.string.Getting_Started));
        listDataHeader.add(getResources().getString(R.string.food_beverages));
        listDataHeader.add(getResources().getString(R.string.sleep));
        listDataHeader.add(getResources().getString(R.string.How_to_log_exercise));
        listDataHeader.add(getResources().getString(R.string.add_reminders));

        // Adding child data

        List<String> sub_question1 = new ArrayList<String>();
        sub_question1.add(getResources().getString(R.string.most_of_the_benefits));

        List<String> sub_question2 = new ArrayList<String>();
        sub_question2.add(getResources().getString(R.string.about_to_eat_or_drink));

        List<String> sub_question3 = new ArrayList<String>();
        sub_question3.add(getResources().getString(R.string.sleep_duration_and_quality));

        List<String> sub_question4 = new ArrayList<String>();
        sub_question4.add(getResources().getString(R.string.Record_your_daily_exercise));

        List<String> sub_question5 = new ArrayList<String>();
        sub_question5.add(getResources().getString(R.string.You_can_add_reminders));


        listDataChild.put(listDataHeader.get(0), sub_question1); // Header, Child data
        listDataChild.put(listDataHeader.get(1), sub_question2);
        listDataChild.put(listDataHeader.get(2), sub_question3);
        listDataChild.put(listDataHeader.get(3), sub_question4);
        listDataChild.put(listDataHeader.get(4), sub_question5);


    }
}
