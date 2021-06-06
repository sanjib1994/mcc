package com.salk.mycircadianclock.information.how_to_videos;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.Utility.ConnectionDetector;
import com.salk.mycircadianclock.Utility.TabbarClick;
import java.util.Locale;

public class HowToVideosActivity extends AppCompatActivity {

    //Todo declaration of XML views
    private RelativeLayout tabbar, rel_main;
    private WebView chart_view;
    private TextView simple_text;

    private String language = "";
    private ConnectionDetector connectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            //to make screen full screen
            new CommonUsedmethods().makeActivityFullscreen(HowToVideosActivity.this);

            setContentView(R.layout.activity_how_to_videos);

            init();

            initializecommonclasses();

            if(connectionDetector.isConnectingToInternet()){

                getVideo();
            }else
            {
                chart_view.setVisibility(View.GONE);
                simple_text.setVisibility(View.VISIBLE);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo initialize XML views
    private void init(){

        try {
            tabbar = findViewById(R.id.tab_bar);
            rel_main = findViewById(R.id.main);
            chart_view = findViewById(R.id.chart_view);
            simple_text = findViewById(R.id.simple_text);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Todo initialize common classes
    private void initializecommonclasses(){

        try {
            new TabbarClick().click(HowToVideosActivity.this, tabbar, rel_main, "info");
            connectionDetector = new ConnectionDetector(HowToVideosActivity.this);
            language = Locale.getDefault().getDisplayLanguage();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getVideo(){

        try {
            simple_text.setVisibility(View.GONE);
            chart_view.setVisibility(View.VISIBLE);

            chart_view.getSettings().setJavaScriptEnabled(true);
            // enable zoom controls
            chart_view.getSettings().setBuiltInZoomControls(true);
            // these settings speed up page load into the webview
            chart_view.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            chart_view.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            chart_view.requestFocus(View.FOCUS_DOWN);
            // Set whether the DOM storage API is enabled.
            chart_view.getSettings().setDomStorageEnabled(true);
            chart_view.getSettings().setPluginState(WebSettings.PluginState.ON);
            chart_view.getSettings().setAllowFileAccess(true);
            chart_view.getSettings().setUseWideViewPort(true);
            chart_view.getSettings().setLoadWithOverviewMode(true);
            chart_view.setInitialScale(1);
            chart_view.setWebChromeClient(new WebChromeClient());
            chart_view.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            chart_view.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            chart_view.requestFocus(View.FOCUS_DOWN);
            chart_view.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            chart_view.getSettings().setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
            chart_view.setScrollbarFadingEnabled(true);

            if (language.matches("français")) {
                chart_view.loadUrl("file:///android_asset/how_to_videos/French_How_to_video.html");
            } else {
                chart_view.loadUrl("file:///android_asset/how_to_videos/How_to_video.html");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
