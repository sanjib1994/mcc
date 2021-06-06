package com.salk.mycircadianclock.camera;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.SessionsClient;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.SessionReadResponse;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.Utility.ConnectionDetector;
import com.salk.mycircadianclock.Utility.TabbarClick;
import com.salk.mycircadianclock.Utility.TinylocalDb;
import com.salk.mycircadianclock.activity.BaseActivity;
import com.salk.mycircadianclock.activity.TutorialActivity;
import com.salk.mycircadianclock.activityrecognizer.ActivityRecogniser;
import com.salk.mycircadianclock.activityrecognizer.BackgroundDetectedActivitiesService;
import com.salk.mycircadianclock.activityrecognizer.Constants;
import com.salk.mycircadianclock.api.AESEncryption;
import com.salk.mycircadianclock.api.APIManager;
import com.salk.mycircadianclock.api.APIPasrsing;
import com.salk.mycircadianclock.api.ApiConfig;
import com.salk.mycircadianclock.api.Apicallback;
import com.salk.mycircadianclock.food.TaglaterListAdpter;
import com.salk.mycircadianclock.food.FoodSendActivity;
import com.salk.mycircadianclock.food.Taglater;
import com.salk.mycircadianclock.history.history_activity.ActivityHistory;
import com.salk.mycircadianclock.interfaces.OnRecycleItemClicks;
import com.salk.mycircadianclock.localDatabase.DatabaseRepo;
import com.salk.mycircadianclock.localDatabase.FoodSleepExData;
import com.salk.mycircadianclock.localDatabase.OnDataFetched;
import com.salk.mycircadianclock.settings.InterVentionSettingsActivity;
import com.salk.mycircadianclock.settings.SettingsTable;
import com.salk.mycircadianclock.survey.SurveyActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static android.webkit.WebSettings.LOAD_NO_CACHE;
import static com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_CUMULATIVE;
import static com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA;
import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;


public class CustomCameraActivity extends AppCompatActivity implements Camera.PictureCallback {


    public static final String TAG = CustomCameraActivity.class.getSimpleName();
    public static final String CAMERA_ID_KEY = "camera_id";
    public static final String CAMERA_FLASH_KEY = "flash_mode";
    public static final String IMAGE_INFO = "image_info";
    String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private int mCameraID,no_of_baseline_day,no_of_intervention_day;
    private String mFlashMode = Camera.Parameters.FLASH_MODE_OFF;
    private Camera mCamera;
    private SquareCameraPreview mPreviewView;
    private SurfaceHolder mSurfaceHolder;
    private boolean mIsSafeToTakePhoto = false;
    private ImageParameters mImageParameters;
    private CameraOrientationListener mOrientationListener;
    private Bundle savedInstanceState1;
    private String cameraissue = "";
    private SavePicTask savePicTask;
    private File file,mFile;

    private TinylocalDb tinylocalDb;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private DatabaseRepo databaseRepo;
    private CommonUsedmethods commonUsedmethods;
    private SettingsTable settingsTable;
    private ConnectionDetector connectionDetector;

    private String camera_desc,user_id ="",user_type ="",auth_token = "",research_info_id ="",current_day_steps = "0";
    private List<Taglater> taglaterArrayList = new ArrayList<>();
    private Observer<List<Taglater>> observer;
    private Observer<List<FoodSleepExData>> observer_foodsleeEx;
    private Observer<FoodSleepExData> observer_lastfodTaken;
    private Handler handler,handler_fasting;
    private boolean is_clicked = false;

    private TextView tag_later_count,last_taken_food,text_fastingtarget,text_step_count,text_excercise_status,intervention_status,
                    text_sleep_status,last_taken_food1,text_fastingtarget2,baseline_time_line,baseline_time_line_into,textservey_count,text_allow_permission;
    private RelativeLayout tabbar,rel_main,tag_later_lay,rel_since_last_food,rel_till_fasting,rel_sleep,rel_exercise,rel_full_sincefood,
                           rel_full_tiill_fast,rel_intervention,rel_baseline_layout,baseline_layout_not_started,rel_servey_lay,rel_gfit,
                            rel_steps_count;
    private WebView onedayfeedo;
    private ImageView info_icon,imgFlashOnOff;
    private Button imgCapture;
    private LinearLayout lin_last_food_taken,lin_steps;
    private ScrollView scroll_view;
    private ProgressBar fasting_progressBar,step_statusprogressBar,excercise_minprogressBar,intervention_minprogressBar,fasting_progressBar2,
            baseline_progressBar;

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 999;

    private BroadcastReceiver broadcastReceiver;
    private ArrayList<GFitStepCount> gFitStepCountArrayList = new ArrayList<>();
    private JSONArray json_arr_steps = new JSONArray();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            new CommonUsedmethods().makeActivityFullscreen(CustomCameraActivity.this);

            setContentView(R.layout.activity_base);

            store_camera_state(savedInstanceState);

            initControls();

            click_function();

            get_Data_from_local();

            start_observres();

            check_for_camera_permission(savedInstanceState);

            //Todo call google fit API
            call_google_fit();

            getActivityStatus();


            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                        int type = intent.getIntExtra("type", -1);
                        int confidence = intent.getIntExtra("confidence", 0);

                        handleUserActivity(type, confidence);
                    }
                }
            };

            startTracking();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void initControls() {

        try {

            mPreviewView = findViewById(R.id.texture);
            imgCapture = findViewById(R.id.picture);
            imgFlashOnOff = findViewById(R.id.flash);
            tabbar = findViewById(R.id.tab_bar);
            rel_main = findViewById(R.id.main);
            tag_later_count = findViewById(R.id.tag_later_count);
            tag_later_lay = findViewById(R.id.tag_later_lay);
            onedayfeedo = findViewById(R.id.onedayfeedo);
            last_taken_food = findViewById(R.id.last_taken_food);
            info_icon = findViewById(R.id.info_icon);

            text_fastingtarget = findViewById(R.id.fastingtarget);
            text_step_count = findViewById(R.id.step_status);
            text_excercise_status = findViewById(R.id.excercise_status);
            intervention_status = findViewById(R.id.intervention_status);
            text_sleep_status = findViewById(R.id.sleep_status);
            rel_since_last_food = findViewById(R.id.relativelayout1);
            rel_till_fasting = findViewById(R.id.relativelayout2);
            rel_sleep = findViewById(R.id.label4);
            rel_exercise = findViewById(R.id.label2);
            lin_last_food_taken = findViewById(R.id.label1);
            lin_steps = findViewById(R.id.label3);
            fasting_progressBar = findViewById(R.id.fasting_progressBar);
            step_statusprogressBar = findViewById(R.id.step_statusprogressBar);
            excercise_minprogressBar = findViewById(R.id.excercise_minprogressBar);
            intervention_minprogressBar = findViewById(R.id.intervention_minprogressBar);
            rel_full_sincefood = findViewById(R.id.relativelayout11);
            rel_full_tiill_fast = findViewById(R.id.relativelayout22);
            text_fastingtarget2 = findViewById(R.id.fastingtarget2);
            last_taken_food1 = findViewById(R.id.last_taken_food1);
            fasting_progressBar2 = findViewById(R.id.fasting_progressBar2);
            rel_intervention = findViewById(R.id.label222);
            rel_baseline_layout = findViewById(R.id.baseline_layout);
            baseline_layout_not_started = findViewById(R.id.baseline_layout_not_started);
            baseline_time_line = findViewById(R.id.baseline_time_line);
            baseline_time_line_into = findViewById(R.id.baseline_time_line_into);
            baseline_progressBar = findViewById(R.id.baseline_progressBar);
            rel_servey_lay = findViewById(R.id.servey_lay);
            scroll_view = findViewById(R.id.scroll_view);
            textservey_count = findViewById(R.id.servey_count);
            text_allow_permission = findViewById(R.id.text_allow_permission);
            rel_gfit = findViewById(R.id.rel_gfit);
            rel_steps_count = findViewById(R.id.relativelayout4);

            tinylocalDb = new TinylocalDb();
            databaseRepo = new DatabaseRepo(getApplicationContext());
            sharedPreferences = tinylocalDb.get_shared_pref(CustomCameraActivity.this);
            editor = sharedPreferences.edit();
            commonUsedmethods = new CommonUsedmethods();
            connectionDetector = new ConnectionDetector(CustomCameraActivity.this);
            handler = new Handler();
            handler_fasting = new Handler();

            user_type = tinylocalDb.get_data_in_shared(sharedPreferences,"user_type");
            research_info_id = tinylocalDb.get_data_in_shared(sharedPreferences,"research_info_id");
            auth_token = tinylocalDb.get_data_in_shared(sharedPreferences,"auth_token");

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void click_function(){

        try {
            imgCapture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mPreviewView.getVisibility() == View.VISIBLE) {
                        mFile = new CommonUsedmethods().create_directory();
                        if (!mFile.mkdir()) {
                            mFile.mkdir();
                        }
                        file = new File(mFile, System.currentTimeMillis() + ".jpg");
                        takePicture();
                    } else {

                        is_clicked = true;
                        get_runtime_permissions(savedInstanceState1);
                    }
                }
            });

            imgFlashOnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mFlashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_AUTO)) {
                        mFlashMode = Camera.Parameters.FLASH_MODE_ON;
                    } else if (mFlashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_ON)) {
                        mFlashMode = Camera.Parameters.FLASH_MODE_OFF;
                    } else if (mFlashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_OFF)) {
                        mFlashMode = Camera.Parameters.FLASH_MODE_AUTO;
                    }


                    setupFlashMode();
                    setupCamera();
                }
            });


            tag_later_lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (taglaterArrayList != null && taglaterArrayList.size() > 0) {

                        showTagFoodDetails(taglaterArrayList);
                    }
                }
            });

            info_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CustomCameraActivity.this, TutorialActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });

            rel_servey_lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!textservey_count.getText().toString().trim().equalsIgnoreCase("0")) {

                        if(connectionDetector!=null && connectionDetector.isConnectingToInternet()) {
                            Intent intent = new Intent(CustomCameraActivity.this, SurveyActivity.class);
                            intent.putExtra("no_baseline_day",no_of_baseline_day);
                            intent.putExtra("no_intervention_day",no_of_intervention_day);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }else{

                            commonUsedmethods.show_Toast(CustomCameraActivity.this,
                                    getResources().getString(R.string.Please_connect_to_internet));
                        }

                    }
                }
            });

            rel_gfit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    call_google_fit();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void start_setup_for_cammera(Bundle savedInstanceState){

        try {
            mPreviewView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {

                    mSurfaceHolder = surfaceHolder;

                    getCamera(mCameraID);
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

                }
            });

            mFlashMode = Camera.Parameters.FLASH_MODE_OFF;
            setupFlashMode();

            mImageParameters.mIsPortrait =
                    getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

            if (savedInstanceState == null) {
                ViewTreeObserver observer = mPreviewView.getViewTreeObserver();
                observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mImageParameters.mPreviewWidth = mPreviewView.getWidth();
                        mImageParameters.mPreviewHeight = mPreviewView.getHeight();


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mPreviewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            mPreviewView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void store_camera_state(Bundle savedInstanceState){

        try {
            if (savedInstanceState == null) {

                mCameraID = getBackCameraID();
                mFlashMode = CameraSettingPreferences.getCameraFlashMode(CustomCameraActivity.this);
                mImageParameters = new ImageParameters();

            } else {
                mCameraID = savedInstanceState.getInt(CAMERA_ID_KEY);
                mFlashMode = savedInstanceState.getString(CAMERA_FLASH_KEY);
                mImageParameters = savedInstanceState.getParcelable(IMAGE_INFO);

            }
            savedInstanceState1 = savedInstanceState;
            mOrientationListener = new CameraOrientationListener(CustomCameraActivity.this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void get_runtime_permissions(final Bundle savedInstanceState) {

        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
                @Override
                public void onGranted() {

                    text_allow_permission.setVisibility(View.GONE);
                    mPreviewView.setVisibility(View.VISIBLE);
                    start_setup_for_cammera(savedInstanceState);

                }

                @Override
                public boolean onBlocked(Context context, ArrayList<String> blockedList) {

                    text_allow_permission.setVisibility(View.VISIBLE);
                    if (is_clicked) {

                        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                        startActivity(i);
                    }

                    return true;
                }

                @Override
                public void onDenied(Context context, ArrayList<String> deniedPermissions) {

                    text_allow_permission.setVisibility(View.VISIBLE);

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        try {

            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                    new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));

            if (is_clicked) {

                is_clicked = false;
                get_runtime_permissions(savedInstanceState1);

            } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                text_allow_permission.setVisibility(View.GONE);
                if (mCamera == null) {
                    restartPreview();
                }

            }else{
                text_allow_permission.setVisibility(View.VISIBLE);
            }

            new TabbarClick().click(CustomCameraActivity.this, tabbar, rel_main, "home");

            check_user_type();
            check_for_tagater_count();
            check_for_oneday_feedo();


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private class SavePicTask extends AsyncTask<Void, Void, String> {
        private byte[] data;
        private int rotation = 0;


        public SavePicTask(byte[] data, int rotation) {
            this.data = data;
            this.rotation = rotation;
        }

        protected void onPreExecute() {


        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                return saveToSDCard(data, rotation);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {

            try {

                Bundle bundle = new Bundle();
                bundle.putString("image", file.getAbsolutePath());
                bundle.putString("camera_desc", camera_desc);


                Intent intent = new Intent(CustomCameraActivity.this, FoodSendActivity.class);
                if (bundle != null) {
                    intent.putExtras(bundle);
                }
                startActivity(intent);
                overridePendingTransition(0, 0);

                startCameraPreview();
                imgCapture.setEnabled(true);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    public String saveToSDCard(byte[] data, int rotation) throws IOException {

        String imagePath = "";
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, options);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            int reqHeight = metrics.heightPixels;
            int reqWidth = metrics.widthPixels;

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            if (rotation != 0) {
                Matrix mat = new Matrix();
                mat.postRotate(rotation);
                Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
                if (bitmap != bitmap1) {
                    bitmap.recycle();
                }
                imagePath = getSavePhotoLocal(bitmap1);
                if (bitmap1 != null) {
                    bitmap1.recycle();
                }
            } else {
                imagePath = getSavePhotoLocal(bitmap);
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;

    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        try {
            if (height > reqHeight || width > reqWidth) {
                if (width > height) {
                    inSampleSize = Math.round((float) height / (float) reqHeight);
                } else {
                    inSampleSize = Math.round((float) width / (float) reqWidth);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return inSampleSize;
    }

    private String getSavePhotoLocal(Bitmap bitmap) {
        String path = "";
        try {
            OutputStream output;
          
            try {
                output = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                output.flush();
                output.close();
                path = file.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    private void setupFlashMode() {

        try {
            if (Camera.Parameters.FLASH_MODE_AUTO.equalsIgnoreCase(mFlashMode)) {
                imgFlashOnOff.setImageResource(R.mipmap.flash_auto);
            } else if (Camera.Parameters.FLASH_MODE_ON.equalsIgnoreCase(mFlashMode)) {
                imgFlashOnOff.setImageResource(R.mipmap.flash_on);
            } else if (Camera.Parameters.FLASH_MODE_OFF.equalsIgnoreCase(mFlashMode)) {
                imgFlashOnOff.setImageResource(R.mipmap.flash_off);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        try {
            outState.putInt(CAMERA_ID_KEY, mCameraID);
            outState.putString(CAMERA_FLASH_KEY, mFlashMode);
            outState.putParcelable(IMAGE_INFO, mImageParameters);
            super.onSaveInstanceState(outState);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void getCamera(int cameraID) {
        try {
            mCamera = Camera.open(cameraID);
            mPreviewView.setCamera(mCamera);

            Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
                @Override
                public void onGranted() {

                    startCameraPreview();

                }
            });

        } catch (Exception e) {
            Log.d(TAG, "Can't open camera with id " + cameraID);
            e.printStackTrace();
            cameraissue = "issue";

        }
    }


    /**
     * Start the camera preview
     */
    private void startCameraPreview() {

        try {
            if (mCamera != null) {
                determineDisplayOrientation();
                setupCamera();
                makeJsonCamera();

                try {
                    mCamera.setPreviewDisplay(mSurfaceHolder);
                    mCamera.startPreview();

                    setSafeToTakePhoto(true);
                    setCameraFocusReady(true);
                } catch (IOException e) {
                    Log.d(TAG, "Can't start camera preview due to IOException " + e);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            cameraissue = "issue";
        }
    }

    /**
     * Stop the camera preview
     */
    private void stopCameraPreview() {

        try {
            setSafeToTakePhoto(false);
            setCameraFocusReady(false);

            // Nulls out callbacks, stops face detection
            mCamera.stopPreview();

            if (mPreviewView != null) {
                mPreviewView.setCamera(null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void restartPreview() {

        try {
            if (mCamera != null) {
                stopCameraPreview();
                mCamera.release();
                mCamera = null;
            }

            getCamera(mCameraID);
        }catch (Exception e){
            e.printStackTrace();

        }

    }

    private void setSafeToTakePhoto(final boolean isSafeToTakePhoto) {

        try {
            mIsSafeToTakePhoto = isSafeToTakePhoto;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setCameraFocusReady(final boolean isFocusReady) {

        try {
            if (this.mPreviewView != null) {
                mPreviewView.setIsFocusReady(isFocusReady);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Determine the current display orientation and rotate the camera preview
     * accordingly
     */
    private void determineDisplayOrientation() {

        try {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(mCameraID, cameraInfo);

            // Clockwise rotation needed to align the window display to the natural position
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;

            switch (rotation) {
                case Surface.ROTATION_0: {
                    degrees = 0;
                    break;
                }
                case Surface.ROTATION_90: {
                    degrees = 90;
                    break;
                }
                case Surface.ROTATION_180: {
                    degrees = 180;
                    break;
                }
                case Surface.ROTATION_270: {
                    degrees = 270;
                    break;
                }
            }

            int displayOrientation;

            // CameraInfo.Orientation is the angle relative to the natural position of the device
            // in clockwise rotation (angle that is rotated clockwise from the natural position)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                // Orientation is angle of rotation when facing the camera for
                // the camera image to match the natural orientation of the device
                displayOrientation = (cameraInfo.orientation + degrees) % 360;
                displayOrientation = (360 - displayOrientation) % 360;
            } else {
                displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
            }

            mImageParameters.mDisplayOrientation = displayOrientation;
            mImageParameters.mLayoutOrientation = degrees;

            mCamera.setDisplayOrientation(mImageParameters.mDisplayOrientation);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private Camera.Size determineBestPictureSize(Camera.Parameters parameters) {

        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        }catch (Exception e){
            e.printStackTrace();
        }
        return determineBestSize(parameters.getSupportedPictureSizes(), 110);
    }

    /**
     * Setup the camera parameters
     */
    private void setupCamera() {

        try {
            // Never keep a global parameters
            Camera.Parameters parameters = mCamera.getParameters();


            //Camera.Size bestPreviewSize = determineBestPreviewSize(parameters);
            Camera.Size bestPictureSize = determineBestPictureSize(parameters);
            Camera.Size bestPreviewSize = getOptimalPreviewSize(parameters.getSupportedPreviewSizes(), bestPictureSize.width, bestPictureSize.height);

            parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
            parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);


            // Set continuous picture focus, if it's supported
            if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }

            List<String> flashModes = parameters.getSupportedFlashModes();
            if (flashModes != null && flashModes.contains(mFlashMode)) {
                parameters.setFlashMode(mFlashMode);
                imgFlashOnOff.setVisibility(View.VISIBLE);
            } else {
                imgFlashOnOff.setVisibility(View.INVISIBLE);
            }

            // Lock in the changes
            mCamera.setParameters(parameters);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private Camera.Size determineBestSize(List<Camera.Size> sizes, int widthThreshold) {

        Camera.Size bestSize = null;
        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

            int width = displaymetrics.widthPixels;
            int height = (4 * width) / 3;
            long diff = (height * 1000 / width);
            long cdistance = Integer.MAX_VALUE;
            for (int i = 0; i < sizes.size(); i++) {
                long value = (long) (sizes.get(i).width * 1000) / sizes.get(i).height;
                if (value >= diff && value < cdistance) {

                    bestSize = sizes.get(i);
                    break;
                }

            }

            if (bestSize == null) {
                Log.d(TAG, "cannot find the best camera size");
                return sizes.get(sizes.size() - 1);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return bestSize;
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {

        final double ASPECT_TOLERANCE = 0.05;
        if (sizes == null) return null;
        Camera.Size optimalSize = null;

        try {
            double targetRatio = (double) w / h;
            double minDiff = Double.MAX_VALUE;

            int targetHeight = h;

            // Find size
            for (Camera.Size size : sizes) {
                double ratio = (double) size.width / size.height;
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }

            if (optimalSize == null) {
                minDiff = Double.MAX_VALUE;
                for (Camera.Size size : sizes) {
                    if (Math.abs(size.height - targetHeight) < minDiff) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - targetHeight);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return optimalSize;
    }


    private int getBackCameraID() {
        return Camera.CameraInfo.CAMERA_FACING_BACK;
    }

    /**
     * Take a picture
     */
    private void takePicture() {

        try {
            if (mIsSafeToTakePhoto) {
                setSafeToTakePhoto(false);

                mOrientationListener.rememberOrientation();

                // Shutter callback occurs after the image is captured. This can
                // be used to trigger a sound to let the user know that image is taken
                Camera.ShutterCallback shutterCallback = null;

                // Raw callback occurs when the raw image data is available
                Camera.PictureCallback raw = null;

                // postView callback occurs when a scaled, fully processed
                // postView image is available.
                Camera.PictureCallback postView = null;

                // jpeg callback occurs when the compressed image is available
                mCamera.takePicture(shutterCallback, raw, postView, this);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onStop() {

        try {
            mOrientationListener.disable();

            // stop the preview
            if (mCamera != null) {
                stopCameraPreview();
                mCamera.release();
                mCamera = null;
            }

            CameraSettingPreferences.saveCameraFlashMode(CustomCameraActivity.this, mFlashMode);

            super.onStop();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * A picture has been taken
     *
     * @param data
     * @param camera
     */
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        try {
            savePicTask = new SavePicTask(data, getPhotoRotation());
            savePicTask.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private int getPhotoRotation() {
        int rotation = 0;

        try {
            int orientation = mOrientationListener.getRememberedNormalOrientation();
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(mCameraID, info);

            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                rotation = (info.orientation - orientation + 360) % 360;
            } else {
                rotation = (info.orientation + orientation) % 360;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return rotation;
    }

    private static class CameraOrientationListener extends OrientationEventListener {

        private int mCurrentNormalizedOrientation;
        private int mRememberedNormalOrientation;

        public CameraOrientationListener(Context context) {
            super(context, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation != ORIENTATION_UNKNOWN) {
                mCurrentNormalizedOrientation = normalize(orientation);
            }
        }

        /**
         * @param degrees Amount of clockwise rotation from the device's natural position
         * @return Normalized degrees to just 0, 90, 180, 270
         */
        private int normalize(int degrees) {
            if (degrees > 315 || degrees <= 45) {
                return 0;
            }

            if (degrees > 45 && degrees <= 135) {
                return 90;
            }

            if (degrees > 135 && degrees <= 225) {
                return 180;
            }

            if (degrees > 225 && degrees <= 315) {
                return 270;
            }

            throw new RuntimeException("The physics as we know them are no more. Watch out for anomalies.");
        }

        public void rememberOrientation() {
            mRememberedNormalOrientation = mCurrentNormalizedOrientation;
        }

        public int getRememberedNormalOrientation() {
            rememberOrientation();
            return mRememberedNormalOrientation;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                    fetch_sleep_form_fit();
                    subscribe();
                    tinylocalDb.put_data_in_shared(sharedPreferences, "allow_google_fit_permission", "true");
                }
            } else {
                if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                    tinylocalDb.put_data_in_shared(sharedPreferences, "allow_google_fit_permission", "false");
                    rel_steps_count.setVisibility(View.GONE);
                    rel_gfit.setVisibility(View.VISIBLE);
                    new CommonUsedmethods().Toast(CustomCameraActivity.this, "Error");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /** Records step data by requesting a subscription to background step data. */
    public void subscribe() {

        try {
            Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .subscribe(TYPE_STEP_COUNT_CUMULATIVE)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "Successfully subscribed!");
                            readData();
                            readData_1();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "There was a problem subscribing.");
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    private void readData() {

        try {
            Calendar cal = Calendar.getInstance();
            Date now = new Date();
            cal.setTime(now);
            long endTime = cal.getTimeInMillis();
            cal.add(Calendar.MONTH, -1);
            long startTime = cal.getTimeInMillis();

            java.text.DateFormat dateFormat = getDateInstance();
            Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
            Log.i(TAG, "Range End: " + dateFormat.format(endTime));

            DataReadRequest readRequest =
                    new DataReadRequest.Builder()
                            // The data request can specify multiple data types to return, effectively
                            // combining multiple data queries into one call.
                            // In this example, it's very unlikely that the request is for several hundred
                            // datapoints each consisting of a few steps and a timestamp.  The more likely
                            // scenario is wanting to see how many steps were walked per day, for 7 days.
                            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                            // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                            // bucketByTime allows for a time span, whereas bucketBySession would allow
                            // bucketing by "sessions", which would need to be defined in code.
                            .bucketByTime(1, TimeUnit.DAYS)
                            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                            .build();

            Task<DataReadResponse> response = Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this)).
                    readData(readRequest).addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                @Override
                public void onSuccess(DataReadResponse dataReadResponse) {
                    List<Bucket> dataSets = dataReadResponse.getBuckets();
                    for(int i=0;i<dataSets.size();i++){
                        List<DataSet> dataSets1 = dataSets.get(i).getDataSets();
                        for(int j=0;j<dataSets1.size();j++){
                            dumpDataSet(dataSets1.get(j));
                        }
                    }

                    if(gFitStepCountArrayList.size()>0){
                       databaseRepo.insert_fit_step_count(gFitStepCountArrayList);
                       mak_JSON_for_steps_sync(gFitStepCountArrayList);
                    }
                }
            });



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private  void dumpDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {

            GFitStepCount gFitStepCount = new GFitStepCount();

            gFitStepCount.setStarttime(dp.getStartTime(TimeUnit.MILLISECONDS));
            gFitStepCount.setEndtime(dp.getEndTime(TimeUnit.MILLISECONDS));
            int value =0;
            Log.i(TAG, "Data point:");
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                gFitStepCount.setStep_count(dp.getValue(field).asInt());
                Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
            }
            gFitStepCountArrayList.add(gFitStepCount);

        }
    }

    private void call_google_fit(){

        try {

           // Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {

                    FitnessOptions fitnessOptions = FitnessOptions.builder()
                            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                            .build();
                    GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(CustomCameraActivity.this, fitnessOptions);


                        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(CustomCameraActivity.this), fitnessOptions)) {
                            GoogleSignIn.requestPermissions(
                                    CustomCameraActivity.this,
                                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                                    GoogleSignIn.getLastSignedInAccount(CustomCameraActivity.this),
                                    fitnessOptions);
                        } else {
                            subscribe_1();
                        }
               // }
            //}, 15 * 1000);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /** Records step data by requesting a subscription to background step data. */
    public void subscribe_1() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "Successfully subscribed!");
                                    readData_1();
                                } else {
                                    Log.w(TAG, "There was a problem subscribing.", task.getException());
                                }
                            }
                        });
    }

    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    private void readData_1() {
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                final long total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                                Log.i(TAG, "Total steps: " + total);

                                current_day_steps = String.valueOf(total);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(settingsTable!=null) {

                                            rel_gfit.setVisibility(View.GONE);
                                            rel_steps_count.setVisibility(View.VISIBLE);
                                            text_step_count.setText(current_day_steps + "/" + settingsTable.getActivity_target_count_step() + " steps");
                                            step_statusprogressBar.setMax(Integer.valueOf(settingsTable.getActivity_target_count_step()));
                                            step_statusprogressBar.setProgress(Integer.valueOf(current_day_steps));
                                        }
                                    }
                                });


                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "There was a problem getting the step count.", e);
                            }
                        });
    }

    public void makeJsonCamera(){

        Float appreture = 0.0f, focalLength = 0.0f;
        int curBrightnessValue =0,FrameNumber = 0;
        long Exposure = 0;
        String FlashMode = "";

        String user_id = new TinylocalDb().get_data_in_shared(sharedPreferences,"user_id");

        Camera.Parameters  params = mCamera.getParameters();


        try {

            if(params!=null) {
                focalLength = params.getFocalLength();
                FlashMode = params.getFlashMode();
                FrameNumber = params.getPreviewFrameRate();
                Exposure = params.getExposureCompensation();
            }

            curBrightnessValue = Settings.System
                    .getInt(getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS);

        } catch (Exception e) {

            curBrightnessValue = 0;
        }


        JSONObject jsonObject =null;
        try {
            jsonObject = new JSONObject();

            jsonObject.put("ApertureValue", appreture);
            jsonObject.put("BrightnessValue", curBrightnessValue);
            jsonObject.put("ExposureTime", Exposure);
            jsonObject.put("FNumber", FrameNumber);
            jsonObject.put("Flash", FlashMode);//filename created by partitpant_id and date
            jsonObject.put("FocalLength", focalLength);
            jsonObject.put("LensModel", "NA");
            jsonObject.put("Software", "NA");
            jsonObject.put("file_name", user_id+"_"+"Camera"+"_"+System.currentTimeMillis());//filename created by partitpant_id and date
            jsonObject.put("file_type", "text/plain");
        }catch (Exception e){
            e.printStackTrace();
        }

        camera_desc =  jsonObject.toString();
    }

    private void get_Data_from_local(){

        try {
            user_id = new TinylocalDb().get_data_in_shared(sharedPreferences, "user_id");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void check_for_tagater_count(){

        try {

            databaseRepo.getTaglaterImages().observe(this,observer);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void check_for_oneday_feedo(){

        try {

            handler.removeCallbacksAndMessages(null);
            //databaseRepo.getLastFoodTaken().observe(this,observer_lastfodTaken);
            databaseRepo.getFoodSleepExdata(commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy",commonUsedmethods.get_current_date())).
                    observe(this,observer_foodsleeEx);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showTagFoodDetails(List<Taglater> tag_later_list) {

        try {
            final Dialog list_dialog = new Dialog(CustomCameraActivity.this);
            list_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            list_dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
            list_dialog.setContentView(R.layout.tag_later_list_dialog);
            list_dialog.getWindow().setGravity(Gravity.BOTTOM);
            list_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            list_dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTag;
            list_dialog.setCancelable(false);

            RecyclerView list_food = list_dialog.findViewById(R.id.allfoodwithcount);
            Button close_btn = list_dialog.findViewById(R.id.close_btn);

            TaglaterListAdpter adapter = new TaglaterListAdpter(tag_later_list, CustomCameraActivity.this, new OnRecycleItemClicks() {

                @Override
                public void onClick(Object object) {

                    list_dialog.dismiss();
                    Taglater taglater = (Taglater) object;

                    Bundle bundle = new Bundle();
                    bundle.putString("image", taglater.getImage());
                    bundle.putString("camera_desc", taglater.getFood_camerainfo());
                    bundle.putLong("time", taglater.getTimestamp());
                    bundle.putBoolean("is_tag_later", true);
                    Intent intent = new Intent(CustomCameraActivity.this, FoodSendActivity.class);
                    if (bundle != null) {
                        intent.putExtras(bundle);
                    }
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });
            list_food.setHasFixedSize(true);
            list_food.setLayoutManager(new LinearLayoutManager(this));
            list_food.setAdapter(adapter);

            close_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    list_dialog.dismiss();

                }
            });
            list_dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void set_one_day_fedoogram(List<FoodSleepExData> arrayList){

        try {
            onedayfeedo.setVisibility(View.VISIBLE);
            onedayfeedo.getSettings().setJavaScriptEnabled(true);
            // enable zoom controls
            onedayfeedo.getSettings().setBuiltInZoomControls(false);
            // Set whether the DOM storage API is enabled.
            onedayfeedo.getSettings().setDomStorageEnabled(true);
            onedayfeedo.getSettings().setPluginState(WebSettings.PluginState.ON);
            onedayfeedo.getSettings().setAllowFileAccess(true);
            onedayfeedo.setScrollbarFadingEnabled(false);
            onedayfeedo.getSettings().setUseWideViewPort(true);
            onedayfeedo.getSettings().setLoadWithOverviewMode(true);
            onedayfeedo.setWebChromeClient(new WebChromeClient());
            // these settings speed up page load into the webview
            onedayfeedo.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            onedayfeedo.getSettings().setCacheMode(LOAD_NO_CACHE);
            onedayfeedo.requestFocus(View.FOCUS_DOWN);
            // onedayfeedo.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            onedayfeedo.setVerticalScrollBarEnabled(false);


            final JSONObject jsonObject = getJson_For_One_day_feedo(arrayList);

            onedayfeedo.setWebViewClient(new WebViewClient() {
                @SuppressWarnings("deprecation")
                @Override
                public void onPageFinished(WebView view, String url) {

                    onedayfeedo.loadUrl("javascript: activeDataDisplay('"
                            + jsonObject + "');");

                }
            });


            onedayfeedo.loadUrl("file:///android_asset/one_day_feedo/activity-chart.html");

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private JSONObject getJson_For_One_day_feedo(List<FoodSleepExData> arrayList){

        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObject_main = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            JSONArray food_array = new JSONArray();
            JSONArray sleep_array = new JSONArray();
            JSONArray exercise_array = new JSONArray();

            for (int i = 0; i < arrayList.size(); i++) {

                if(arrayList.get(i).getType().equalsIgnoreCase("food")) {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("type", arrayList.get(i).getFood_type());
                    jsonObject1.put("time", arrayList.get(i).getFood_taken_time());

                    food_array.put(jsonObject1);
                }else if(arrayList.get(i).getType().equalsIgnoreCase("sleep")){
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("start_time", commonUsedmethods.parse_date_in_this_format(arrayList.get(i).getSleep_start_time(),"HH:mm"));
                    jsonObject1.put("end_time", commonUsedmethods.parse_date_in_this_format(arrayList.get(i).getSleep_end_time(),"HH:mm"));

                    sleep_array.put(jsonObject1);
                }else if(arrayList.get(i).getType().equalsIgnoreCase("exercise")){
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("start_time", commonUsedmethods.parse_date_in_this_format(arrayList.get(i).getTimestamp(),"HH:mm"));
                    if(arrayList.get(i).getExercise_duration().equalsIgnoreCase("")){
                        arrayList.get(i).setExercise_duration("00:00");
                    }
                    String ar[] = arrayList.get(i).getExercise_duration().split(":");
                    jsonObject1.put("end_time", commonUsedmethods.addtime(arrayList.get(i).getTimestamp(),Integer.valueOf(ar[0]),Integer.valueOf(ar[1])));

                    exercise_array.put(jsonObject1);
                }
            }
            jsonObject_main.put("values_for_food_mark", food_array);
            if(settingsTable!=null){
                jsonObject_main.put("target_start_time",settingsTable.getEating_window_target_start());
                jsonObject_main.put("target_end_time",settingsTable.getEating_window_target_end());
            }else{
                jsonObject_main.put("target_start_time","09:00");
                jsonObject_main.put("target_end_time","18:00");
            }

            jsonObject_main.put("show_date",commonUsedmethods.parse_date_in_this_format(commonUsedmethods.get_current_date(),
                    "MM/dd","dd-MM-yyyy"));
            jsonObject_main.put("current_time",commonUsedmethods.parse_date_in_this_format(commonUsedmethods.get_current_time(),
                    "HH:mm","hh:mma"));

            jsonObject_main.put("values_for_activity_mark",new JSONArray());
            jsonObject_main.put("values_for_sleep_mark",sleep_array);
            jsonObject_main.put("values_for_exercise_mark",exercise_array);
            jsonArray.put(jsonObject_main);


            return jsonObject.put("data", jsonArray);
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    private void start_observres(){

        try {
            observer = new Observer<List<Taglater>>() {
                @Override
                public void onChanged(@Nullable List<Taglater> taglaters) {
                    // Update the UI, in this case, a TextView.

                    if (taglaters != null) {
                        tag_later_count.setText(String.valueOf(taglaters.size()));
                        taglaterArrayList.clear();
                        taglaterArrayList.addAll(taglaters);
                    }
                }
            };

            observer_foodsleeEx = new Observer<List<FoodSleepExData>>() {
                @Override
                public void onChanged(@Nullable List<FoodSleepExData> foodSleepExDataList) {

                    if (foodSleepExDataList != null) {
                        set_one_day_fedoogram(foodSleepExDataList);
                    }
                }
            };

            observer_lastfodTaken = new Observer<FoodSleepExData>() {
                @Override
                public void onChanged(FoodSleepExData foodSleepExData) {
                    if (foodSleepExData != null) {
                        show_last_taken_food_time(foodSleepExData.getTimestamp());
                    }else{
                        if(rel_full_sincefood.getVisibility()==View.VISIBLE){
                            last_taken_food1.setText(" since last food");
                        }else {
                            last_taken_food.setText(" since last food");
                        }
                    }

                }
            };
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void show_last_taken_food_time(final Long time){

        try {
            Long diff_time = System.currentTimeMillis() - time;
            String hms = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(diff_time),
                    TimeUnit.MILLISECONDS.toMinutes(diff_time)
                            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                            .toHours(diff_time)));


            if (hms.contains(":")) {

                String ar[] = hms.split(":");

                if (ar.length > 1) {

                    String hours = "0", minute = "0";

                    if (Integer.valueOf(ar[0]) < 10) {

                        hours = Integer.valueOf(ar[0]).toString();
                    } else {
                        hours = ar[0];
                    }

                    if (Integer.valueOf(ar[1]) < 10) {

                        minute = Integer.valueOf(ar[1]).toString();
                    } else {
                        minute = ar[1];
                    }

                    if(rel_full_sincefood.getVisibility()==View.VISIBLE){
                        last_taken_food1.setText(hours + " hours " + minute + " min since last food");
                    }else {
                        last_taken_food.setText(hours + " hours " + minute + " min since last food");
                    }

                }
            }

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    show_last_taken_food_time(time);
                }
            }, 1000 * 60);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void check_for_camera_permission(Bundle savedInstanceState1){

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {

                is_clicked = false;
                get_runtime_permissions(savedInstanceState1);

            } else {


                is_clicked = false;
                mPreviewView.setVisibility(View.VISIBLE);
                if (mCamera == null) {
                    start_setup_for_cammera(savedInstanceState1);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void get_settings_value(){
        try{
            databaseRepo.getSettingsData(new OnDataFetched() {
                @Override
                public void data(Object object, int i) {

                }

                @Override
                public void data(Object object) {

                    settingsTable = (SettingsTable) object;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            set_data_in_UI(settingsTable);
                        }
                    });

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void set_data_in_UI(SettingsTable settingsTable){

        try {
            if (settingsTable.getIs_time_since_food_active().equalsIgnoreCase("1") &&
                    settingsTable.getIs_time_till_fast_active().equalsIgnoreCase("1")) {

                rel_since_last_food.setVisibility(View.VISIBLE);
                rel_till_fasting.setVisibility(View.VISIBLE);
                lin_last_food_taken.setVisibility(View.VISIBLE);
                rel_full_tiill_fast.setVisibility(View.GONE);
                rel_full_sincefood.setVisibility(View.GONE);

                databaseRepo.getLastFoodTaken().observe(this, observer_lastfodTaken);

                if(!settingsTable.getEating_window_target_start().equalsIgnoreCase("0")
                       && !settingsTable.getEating_window_target_end().equalsIgnoreCase("0")) {

                    handler_fasting.removeCallbacksAndMessages(null);
                    show_fast_target_time(settingsTable.getEating_window_target_start(),settingsTable.getEating_window_target_end());
                }else {

                    if(handler_fasting!=null){
                        handler_fasting.removeCallbacksAndMessages(null);
                    }
                }

            } else {
                if (!settingsTable.getIs_time_since_food_active().equalsIgnoreCase("1") &&
                        !settingsTable.getIs_time_till_fast_active().equalsIgnoreCase("1")) {

                    lin_last_food_taken.setVisibility(View.GONE);

                } else if (!settingsTable.getIs_time_since_food_active().equalsIgnoreCase("1")) {

                    rel_since_last_food.setVisibility(View.GONE);
                    rel_till_fasting.setVisibility(View.GONE);
                    lin_last_food_taken.setVisibility(View.VISIBLE);
                    rel_full_tiill_fast.setVisibility(View.VISIBLE);
                    rel_full_sincefood.setVisibility(View.GONE);

                    if(!settingsTable.getEating_window_target_start().equalsIgnoreCase("0")
                            && !settingsTable.getEating_window_target_end().equalsIgnoreCase("0")) {

                        handler_fasting.removeCallbacksAndMessages(null);
                        show_fast_target_time(settingsTable.getEating_window_target_start(),settingsTable.getEating_window_target_end());
                    }else {
                        if(handler_fasting!=null){
                            handler_fasting.removeCallbacksAndMessages(null);
                        }
                    }

                } else {
                    rel_till_fasting.setVisibility(View.GONE);
                    rel_since_last_food.setVisibility(View.GONE);
                    lin_last_food_taken.setVisibility(View.VISIBLE);
                    rel_full_sincefood.setVisibility(View.VISIBLE);
                    rel_full_tiill_fast.setVisibility(View.GONE);

                    databaseRepo.getLastFoodTaken().observe(this, observer_lastfodTaken);
                }
            }


            if(settingsTable.getIs_days_exercise_active().equalsIgnoreCase("1")){

                rel_exercise.setVisibility(View.VISIBLE);
                set_exercise_data();
            }else{
                rel_exercise.setVisibility(View.GONE);
            }

            if(settingsTable.getIs_steps_active().equalsIgnoreCase("1")){

                lin_steps.setVisibility(View.VISIBLE);

                if(tinylocalDb.get_data_in_shared(sharedPreferences,"allow_google_fit_permission").equalsIgnoreCase("true")) {
                    text_step_count.setText(current_day_steps+"/" + settingsTable.getActivity_target_count_step() + " steps");
                    step_statusprogressBar.setMax(Integer.valueOf(settingsTable.getActivity_target_count_step()));
                    step_statusprogressBar.setProgress(Integer.valueOf(current_day_steps));
                }else{
                    rel_steps_count.setVisibility(View.GONE);
                    rel_gfit.setVisibility(View.VISIBLE);
                }
            }else{
                lin_steps.setVisibility(View.GONE);
            }

            if(settingsTable.getIs_hours_sleep_active().equalsIgnoreCase("1")){

                rel_sleep.setVisibility(View.VISIBLE);
                set_sleep_data();
            }else{
                rel_sleep.setVisibility(View.GONE);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void show_fast_target_time(final String eating_start_time, final String eating_end_time){

        try {

            Long eating_start_time_ = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy HH:mm",
                       commonUsedmethods.get_current_date()+" "+eating_start_time);
            Long eating_end_time_ = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy HH:mm",
                    commonUsedmethods.get_current_date()+" "+eating_end_time);

            Long current_time_ = System.currentTimeMillis();

                /**
                 * checking StartTime is before
                 */
                if (current_time_ < eating_start_time_) {

                    long diffMs1 = eating_start_time_ - current_time_;

                    int minutes = (int) ((diffMs1 / (1000 * 60)) % 60);
                    int hours = (int) ((diffMs1 / (1000 * 60 * 60)) % 24);

                    if(rel_full_tiill_fast.getVisibility()==View.VISIBLE){

                        text_fastingtarget2.setText(hours + " " + getResources().getString(R.string.hours) + " " + minutes + " "
                                + getResources().getString(R.string.till_breakfast));
                        fasting_progressBar2.setProgress(Math.round(hours));
                    }else{
                        text_fastingtarget.setText(hours + " " + getResources().getString(R.string.hours) + " " + minutes + " "
                                + getResources().getString(R.string.till_breakfast));
                        fasting_progressBar.setProgress(Math.round(hours));
                    }




                } else if (current_time_> eating_start_time_ && current_time_ < eating_end_time_) {

                    long diffMs2 = eating_end_time_ - current_time_;

                    int minutes = (int) ((diffMs2 / (1000 * 60)) % 60);
                    int hours = (int) ((diffMs2 / (1000 * 60 * 60)) % 24);

                    if(rel_full_tiill_fast.getVisibility()==View.VISIBLE){
                        text_fastingtarget2.setText(hours + " " + getResources().getString(R.string.hours) + " " + minutes + " "
                                + getResources().getString(R.string.till_fasting));
                        fasting_progressBar2.setProgress(Math.round(hours));
                    }else{
                        text_fastingtarget.setText(hours + " " + getResources().getString(R.string.hours) + " " + minutes + " "
                                + getResources().getString(R.string.till_fasting));
                        fasting_progressBar.setProgress(Math.round(hours));
                    }


                } else if (current_time_ > eating_start_time_ && current_time_ > eating_end_time_) {


                    long diffMs3 = (eating_start_time_+ (24*60*60*1000)) - current_time_;

                    int minutes = (int) ((diffMs3 / (1000 * 60)) % 60);
                    int hours = (int) ((diffMs3 / (1000 * 60 * 60)) % 24);

                    if(rel_full_tiill_fast.getVisibility()==View.VISIBLE){

                        text_fastingtarget2.setText(hours + " " + getResources().getString(R.string.hours) + " " + minutes + " "
                                + getResources().getString(R.string.till_breakfast));
                        fasting_progressBar2.setProgress(Math.round(hours));
                    }else {
                        text_fastingtarget.setText(hours + " " + getResources().getString(R.string.hours) + " " + minutes + " "
                                + getResources().getString(R.string.till_breakfast));
                        fasting_progressBar.setProgress(Math.round(hours));
                    }

                }



            handler_fasting.postDelayed(new Runnable() {
                @Override
                public void run() {
                    show_fast_target_time(eating_start_time,eating_end_time);
                }
            }, 1000 * 60);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void handleUserActivity(int type, int confidence) {

        try {

            int activity_count = 0;

            switch (type) {
                case DetectedActivity.STILL: {

                    if (confidence > 50) {
                        activity_count = 0;
                        insertActivityData(activity_count);
                    }
                    break;
                }

                default:

                    if (confidence > 1) {
                        activity_count = activity_count + 1;
                        insertActivityData(activity_count);
                    }
                    break;
            }



        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void startTracking() {

        try {
            Intent intent = new Intent(CustomCameraActivity.this, BackgroundDetectedActivitiesService.class);
            startService(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void insertActivityData(int count){

        try {
            ActivityRecogniser activityRecogniser = new ActivityRecogniser();
            activityRecogniser.setActivity_count(count);
            activityRecogniser.setActivity_time(commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy HH:mm",
                    commonUsedmethods.get_current_date()+" "+commonUsedmethods.get_current_time_in_24hr()));
            databaseRepo.insertActivityRecogniserData(activityRecogniser);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getActivityStatus() {
        try{

             databaseRepo.getActivityRecogdata(new OnDataFetched() {
                 @Override
                 public void data(Object object, int i) {

                 }

                 @Override
                 public void data(Object object) {

                     final ActivityRecogniser activityRecogniser = (ActivityRecogniser) object;

                     if(activityRecogniser!=null){

                             String startTime = commonUsedmethods.parse_date_in_this_format(activityRecogniser.getActivity_time(),
                                     "HH:mm");
                             String currentTime = getTime();

                             String between_time = getBetweenTime(startTime);
                             if (getTimeStatusToPush(startTime, currentTime)) {

                                 String[] temp = between_time.split("to");

                                 String fromTime = temp[0];
                                 String toTime = temp[1];

                                 final long from_time = commonUsedmethods.convert_date_to_timestamp(
                                    "dd-MM-yyyy HH:mm",
                                         commonUsedmethods.parse_date_in_this_format(activityRecogniser.getActivity_time(),"dd-MM-yyyy")+" "+
                                         fromTime);
                                 final long to_time = commonUsedmethods.convert_date_to_timestamp(
                                         "dd-MM-yyyy HH:mm",
                                         commonUsedmethods.parse_date_in_this_format(activityRecogniser.getActivity_time(),"dd-MM-yyyy")+" "+
                                                 toTime);

                                 databaseRepo.getActivityRecogSum(from_time, to_time, new OnDataFetched() {
                                     @Override
                                     public void data(Object object, int i) {

                                     }

                                     @Override
                                     public void data(Object object) {

                                         int acivity_count = (int) object;

                                         String measured_date = commonUsedmethods.parse_date_in_this_format(activityRecogniser.getActivity_time(),
                                                 "yyyy-MM-dd");
                                         String measured_time = commonUsedmethods.parse_date_in_this_format(to_time,
                                                 "HH:mm");
                                         call_activity_save_api(measured_date,measured_time,acivity_count,from_time,to_time);
                                     }
                                 });

                             }

                     }

                 }
             });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getBetweenTime(String sTime) {

        try {
            String between_time = null;
            String[] splitStartTime = sTime.split(":");
            String time = splitStartTime[1];
            if (Integer.valueOf(time) >= 0 && Integer.valueOf(time) <= 5) {
                between_time = splitStartTime[0] + ":00" + "to" + splitStartTime[0]
                        + ":05";
            } else if (Integer.valueOf(time) >= 5 && Integer.valueOf(time) <= 10) {
                // between_time="5to10";
                between_time = splitStartTime[0] + ":05" + "to" + splitStartTime[0]
                        + ":10";
            } else if (Integer.valueOf(time) >= 10 && Integer.valueOf(time) <= 15) {
                // between_time="10to15";
                between_time = splitStartTime[0] + ":10" + "to" + splitStartTime[0]
                        + ":15";
            } else if (Integer.valueOf(time) >= 15 && Integer.valueOf(time) <= 20) {
                // between_time="15to20";
                between_time = splitStartTime[0] + ":15" + "to" + splitStartTime[0]
                        + ":20";
            } else if (Integer.valueOf(time) >= 20 && Integer.valueOf(time) <= 25) {
                // between_time="20to25";
                between_time = splitStartTime[0] + ":20" + "to" + splitStartTime[0]
                        + ":25";
            } else if (Integer.valueOf(time) >= 25 && Integer.valueOf(time) <= 30) {
                // between_time="25to30";
                between_time = splitStartTime[0] + ":25" + "to" + splitStartTime[0]
                        + ":30";
            } else if (Integer.valueOf(time) >= 30 && Integer.valueOf(time) <= 35) {
                // between_time="30to35";
                between_time = splitStartTime[0] + ":30" + "to" + splitStartTime[0]
                        + ":35";
            } else if (Integer.valueOf(time) >= 35 && Integer.valueOf(time) <= 40) {
                // between_time="35to40";
                between_time = splitStartTime[0] + ":35" + "to" + splitStartTime[0]
                        + ":40";
            } else if (Integer.valueOf(time) >= 40 && Integer.valueOf(time) <= 45) {
                // between_time="40to45";
                between_time = splitStartTime[0] + ":40" + "to" + splitStartTime[0]
                        + ":45";
            } else if (Integer.valueOf(time) >= 45 && Integer.valueOf(time) <= 50) {
                // between_time="45to50";
                between_time = splitStartTime[0] + ":45" + "to" + splitStartTime[0]
                        + ":50";
            } else if (Integer.valueOf(time) >= 50 && Integer.valueOf(time) <= 55) {
                // between_time="50to55";
                between_time = splitStartTime[0] + ":50" + "to" + splitStartTime[0]
                        + ":55";
            } else if (Integer.valueOf(time) >= 50 && Integer.valueOf(time) <= 59) {
                // between_time="55to59";
                between_time = splitStartTime[0] + ":55" + "to" + splitStartTime[0]
                        + ":59";
            }
            return between_time;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private String getTime() {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date();
            return dateFormat.format(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private boolean getTimeStatusToPush(String startTime, String endTime) {
        boolean status = false;
        String DATE_FORMAT_NOW = "HH:mm";
        Date startdate = null;
        Date enddate = null;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);

        try {

            try {
                startdate = sdf.parse(startTime);
                enddate = sdf.parse(endTime);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            long diffMs = startdate.getTime() - enddate.getTime();
            long diffSec = diffMs / 1000;
            long min = diffSec / 60;

            if (Math.abs(min) > 4) {
                status = true;
            } else {
                status = false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return status;
    }

    private void call_activity_save_api(String date,String time,int activity_count,final long from_time,final long to_time){
        try {

            if (connectionDetector.isConnectingToInternet()) {

                String url = ApiConfig.ACTIVITY_SAVE_API;
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("participant_id", AESEncryption.encrypt(user_id));
                jsonObject.put("research_info_id", AESEncryption.encrypt(research_info_id));
                jsonObject.put("activity_count", AESEncryption.encrypt(String.valueOf(activity_count)));
                jsonObject.put("measured_date", AESEncryption.encrypt(date));
                jsonObject.put("measured_time", AESEncryption.encrypt(time));

                new APIManager().Apicall(auth_token, url, jsonObject, new Apicallback() {
                    @Override
                    public void success(int code, String value) {

                        if (code == 200) {

                            databaseRepo.getActivityRecogdatas(from_time, to_time, new OnDataFetched() {
                                @Override
                                public void data(Object object, int i) {

                                }

                                @Override
                                public void data(Object object) {

                                    List<ActivityRecogniser> list = (List<ActivityRecogniser>) object;
                                    databaseRepo.deleteActivityRecogdata(list);
                                    getActivityStatus();
                                }
                            });

                        }
                    }

                    @Override
                    public void failure(final String value) {

                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void set_exercise_data(){

        long start_timestamp = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy",
                                          commonUsedmethods.parse_date_in_this_format(commonUsedmethods.getWeekStartDate(),"dd-MM-yyyy"));

        long end_timestamp = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy",
                commonUsedmethods.parse_date_in_this_format(commonUsedmethods.getWeekEndDate(),"dd-MM-yyyy"));

        databaseRepo.get_week_ex(start_timestamp, end_timestamp, "exercise", new OnDataFetched() {
            @Override
            public void data(Object object, int i) {

            }

            @Override
            public void data(Object object) {

                List<FoodSleepExData> foodSleepExDataList = (List<FoodSleepExData>) object;

                if(foodSleepExDataList!=null && foodSleepExDataList.size()>0) {
                    final ArrayList<Long> arrayList = new ArrayList<>();

                    for (int i = 0; i < foodSleepExDataList.size();i++){

                        long time = commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy",
                                commonUsedmethods.parse_date_in_this_format(foodSleepExDataList.get(i).getTimestamp(),"dd-MM-yyyy"));
                        if(i==0){
                            arrayList.add(time);
                        }else if(!arrayList.contains(time)){
                            arrayList.add(time);
                        }
                    }

                    final String no_day = String.valueOf(arrayList.size());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text_excercise_status.setText("You have excercised "+no_day+"/7 days this week");
                            excercise_minprogressBar.setProgress(arrayList.size());
                        }
                    });

                }

            }
        });


    }

    private void set_sleep_data(){

        databaseRepo.getLastSleep(new OnDataFetched() {
            @Override
            public void data(Object object, int i) {

            }

            @Override
            public void data(Object object) {

                FoodSleepExData foodSleepExData = (FoodSleepExData) object;
                if(foodSleepExData!=null){

                   final int minutes = (int) ((foodSleepExData.getSleep_duration() / (1000 * 60)) % 60);
                   final int hours = (int) ((foodSleepExData.getSleep_duration() / (1000 * 60 * 60)) % 24);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text_sleep_status.setText(String.format("%02d", hours) + ":" +
                                    String.format("%02d", minutes) + "/"+settingsTable.getSleep_duration() +" hours of sleep last night");
                        }
                    });


                }
            }
        });

    }

    private void call_survey_count_API(){

        try {
            String url = ApiConfig.SURVEY_COUNT;
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
                        parse_sleep_sync_response(value, new ApiParse() {

                            @Override
                            public void onSuccess(final Object object) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textservey_count.setText((String)object);
                                    }
                                });

                            }

                            @Override
                            public void onFailure() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(CustomCameraActivity.this,R.string.something_wrong,Toast.LENGTH_LONG).show();
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
                            Toast.makeText(CustomCameraActivity.this, value, Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void parse_sleep_sync_response(String response,ApiParse apiPasrsing){

        try {
            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject!=null && jsonObject.has("message") && !jsonObject.isNull("message")) {

                JSONObject jsonObject1 = jsonObject.getJSONObject("message");

                String survey_left = AESEncryption.decrypt(jsonObject1.getString("survey_left"));

                apiPasrsing.onSuccess(survey_left);

            }else{

                apiPasrsing.onFailure();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void calculation_for_baseline_intervention(){

        if(user_type.equalsIgnoreCase("baseline_not_started")){
            scroll_view.setVisibility(View.GONE);
            rel_baseline_layout.setVisibility(View.GONE);
            baseline_layout_not_started.setVisibility(View.VISIBLE);
        }else if(user_type.equalsIgnoreCase("baseline")){
            scroll_view.setVisibility(View.GONE);
            baseline_layout_not_started.setVisibility(View.GONE);
            rel_baseline_layout.setVisibility(View.VISIBLE);

            String val = tinylocalDb.get_data_in_shared(sharedPreferences,"baseline_start");
            no_of_baseline_day =(int) ((System.currentTimeMillis() -  Long.valueOf(val))/(24*60*60*1000));
            baseline_time_line.setText( getResources().getString(R.string.You_are_on_day)+" "+
                    String.valueOf(no_of_baseline_day)+" "+getResources().getString(R.string.of_the)+getResources().getString(R.string.days_baseline));
            intervention_minprogressBar.setProgress(no_of_baseline_day);

        }else{
            baseline_layout_not_started.setVisibility(View.GONE);
            rel_baseline_layout.setVisibility(View.GONE);
            scroll_view.setVisibility(View.VISIBLE);

            String val = tinylocalDb.get_data_in_shared(sharedPreferences,"intervention_start");
            no_of_intervention_day =(int) ((System.currentTimeMillis() -  Long.valueOf(val))/(24*60*60*1000));

            get_settings_value();
            intervention_status.setText(getResources().getString(R.string.have_completed)+" "+
                    String.valueOf(no_of_intervention_day/7)+" "+getResources().getString(R.string.weeks_of_study));
            intervention_minprogressBar.setProgress(no_of_intervention_day/7);
        }

        call_survey_count_API();

    }

    //Todo to check weather user is under baseline or intervention
    private void check_user_type(){

        if(!user_type.equalsIgnoreCase("baseline_not_started")){

            long intenvention_start_ = Long.valueOf(tinylocalDb.get_data_in_shared(sharedPreferences,"intervention_start"));
            if(System.currentTimeMillis()>=intenvention_start_){

                user_type = "intervention";
                tinylocalDb.put_data_in_shared(editor,"user_type","intervention");
            }else{
                user_type = "baseline";
                tinylocalDb.put_data_in_shared(editor,"user_type","baseline");
            }
        }
        calculation_for_baseline_intervention();
    }

    private void fetch_sleep_form_fit(){

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, -1);
        long startTime = cal.getTimeInMillis();

        SessionsClient sessionsClient = Fitness.getSessionsClient(this, GoogleSignIn.getLastSignedInAccount(this));

        SessionReadRequest request = new SessionReadRequest.Builder()
                .readSessionsFromAllApps()
                // Activity segment data is required for details of the fine-
                // granularity sleep, if it is present.
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Task<SessionReadResponse> task = sessionsClient.readSession(request);

        task.addOnSuccessListener(new OnSuccessListener<SessionReadResponse>() {
            @Override
            public void onSuccess(SessionReadResponse sessionReadResponse) {

                List<Session> sessions = sessionReadResponse.getSessions();
                for (int i=0;i<sessions.size();i++){

                    Session session = sessions.get(i);
                    if(sessions.get(i).getActivity().equals(FitnessActivities.SLEEP)){

                        long start_sleep = session.getStartTime(TimeUnit.MILLISECONDS);
                        long end_sleep = session.getEndTime(TimeUnit.MILLISECONDS);


                        FoodSleepExData foodSleepExData = new FoodSleepExData();
                        foodSleepExData.setTimestamp(commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy",
                                commonUsedmethods.parse_date_in_this_format(start_sleep, "dd-MM-yyyy")));
                        foodSleepExData.setSleep_actual_date(commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy",
                                commonUsedmethods.parse_date_in_this_format(start_sleep, "dd-MM-yyyy")));
                        foodSleepExData.setSleep_duration(end_sleep-start_sleep);
                        foodSleepExData.setSleep_start_time(start_sleep);
                        foodSleepExData.setSleep_end_time(end_sleep);
                        foodSleepExData.setType("sleep");
                        foodSleepExData.setEnough_sleep(true);
                        foodSleepExData.setSleep_difficulties("");
                        foodSleepExData.setActual_log_request(0L);
                        databaseRepo.insertFoodSleepExdata(foodSleepExData);
                    }
                }

            }
        });

    }

    private void mak_JSON_for_steps_sync(ArrayList<GFitStepCount> gFitStepCountArrayList){

        try {
            for (GFitStepCount gfit : gFitStepCountArrayList) {


                JSONObject jsonObject = new JSONObject();
                jsonObject.put("start_time", gfit.getStarttime());
                jsonObject.put("end_time", gfit.getEndtime());
                jsonObject.put("step_count", gfit.getStep_count());

                json_arr_steps.put(jsonObject);

            }
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("data", json_arr_steps);

            Log.d("step_json", jsonObject1.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Todo interface for API parsing
    private interface ApiParse{

        void onSuccess(Object object);
        void onFailure();
    }


}
