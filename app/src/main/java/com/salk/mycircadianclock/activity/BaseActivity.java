package com.salk.mycircadianclock.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.salk.mycircadianclock.R;
import com.salk.mycircadianclock.Utility.CommonUsedmethods;
import com.salk.mycircadianclock.Utility.TabbarClick;
import com.salk.mycircadianclock.Utility.TinylocalDb;
import com.salk.mycircadianclock.food.TaglaterListAdpter;
import com.salk.mycircadianclock.food.FoodSendActivity;
import com.salk.mycircadianclock.interfaces.OnRecycleItemClicks;
import com.salk.mycircadianclock.localDatabase.DatabaseRepo;
import com.salk.mycircadianclock.localDatabase.FoodSleepExData;
import com.salk.mycircadianclock.food.Taglater;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.webkit.WebSettings.LOAD_NO_CACHE;
import static com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_CUMULATIVE;
import static com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA;

public class BaseActivity extends AppCompatActivity {

    public static BaseActivity baseActivity;
    TinylocalDb tinylocalDb;
    SharedPreferences sharedPreferences;
    DatabaseRepo databaseRepo;
    CommonUsedmethods commonUsedmethods;

    String camera_desc,user_id ="";
    String clorie_count;
    List<Taglater> taglaterArrayList = new ArrayList<>();
    Observer<List<Taglater>> observer;
    Observer<List<FoodSleepExData>> observer_foodsleeEx;
    Observer<FoodSleepExData> observer_lastfodTaken;
    Handler handler;

    TextView tag_later_count,last_taken_food;
    RelativeLayout tabbar,rel_main,tag_later_lay;
    WebView onedayfeedo;
    ImageView info_icon;

    //Zooming
    protected float fingerSpacing = 0;
    protected float zoomLevel = 1f;
    protected float maximumZoomLevel;
    protected Rect zoom;



    //variables realted to textureview
    private Button takePictureButton;
    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file,mFile;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    public static final String TAG = "StepCounter";
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {


            //to make luncher screen full screen
            new CommonUsedmethods().makeActivityFullscreen(BaseActivity.this);

            setContentView(R.layout.activity_base);

            //initialization of all view
            init();

            //click function for required views
            click_function();


            //Todo call google fit API
            call_google_fit();


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //initialization of all view
    private void init(){

        baseActivity = this;
        tinylocalDb = new TinylocalDb();
        databaseRepo = new DatabaseRepo(getApplicationContext());
        sharedPreferences = tinylocalDb.get_shared_pref(BaseActivity.this);
        commonUsedmethods = new CommonUsedmethods();
        handler = new Handler();

        get_Data_from_local();
        start_observres();


        tabbar = findViewById(R.id.tab_bar);
        rel_main = findViewById(R.id.main);
        textureView = findViewById(R.id.texture);
        tag_later_count = findViewById(R.id.tag_later_count);
        tag_later_lay = findViewById(R.id.tag_later_lay);
        onedayfeedo = findViewById(R.id.onedayfeedo);
        last_taken_food = findViewById(R.id.last_taken_food);
        info_icon = findViewById(R.id.info_icon);

        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);
        takePictureButton = findViewById(R.id.picture);
        assert takePictureButton != null;


    }
    //click function for required views
    private void click_function(){

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFile = new CommonUsedmethods().create_directory();
                if(!mFile.mkdir()){
                    mFile.mkdir();
                }
                file = new File(mFile, System.currentTimeMillis()+".jpg");
                takePicture();
            }
        });

        tag_later_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(taglaterArrayList!=null && taglaterArrayList.size()>0){

                    showTagFoodDetails(taglaterArrayList);
                }
            }
        });

        info_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaseActivity.this, TutorialActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                subscribe();
            }
        }else{
            new CommonUsedmethods().Toast(BaseActivity.this,"Error");
        }
    }

    /** Records step data by requesting a subscription to background step data. */
    public void subscribe() {
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
                                    readData();
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
    private void readData() {
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                long total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                                Log.i(TAG, "Total steps: " + total);

                                new CommonUsedmethods().Toast(BaseActivity.this,String.valueOf(total));
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

    private void call_google_fit(){


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                FitnessOptions fitnessOptions =
                        FitnessOptions.builder()
                                .addDataType(TYPE_STEP_COUNT_CUMULATIVE)
                                .addDataType(TYPE_STEP_COUNT_DELTA)
                                .build();

                if(sharedPreferences!=null && tinylocalDb.get_data_in_shared(sharedPreferences,"google_fit").equalsIgnoreCase("")) {
                    if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(BaseActivity.this), fitnessOptions)) {
                        GoogleSignIn.requestPermissions(
                                BaseActivity.this,
                                REQUEST_OAUTH_REQUEST_CODE,
                                GoogleSignIn.getLastSignedInAccount(BaseActivity.this),
                                fitnessOptions);
                        tinylocalDb.put_data_in_shared(sharedPreferences,"google_fit","true");
                    } else {
                        tinylocalDb.put_data_in_shared(sharedPreferences,"google_fit","true");
                        subscribe();
                    }
                }
            }
        },15*1000);

    }
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            openCamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }
        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }
        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };
    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);

            createCameraPreview();
        }
    };
    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }
    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    protected void takePicture() {
        if(null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());

            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));


            makeJsonCamera(captureBuilder);
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }
                private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(BaseActivity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();

                    createCameraPreview();

                    Bundle bundle = new Bundle();
                    bundle.putString("image", file.getAbsolutePath());
                    bundle.putString("camera_desc", camera_desc);


                    Intent intent = new Intent(BaseActivity.this, FoodSendActivity.class);
                    if(bundle!=null) {
                        intent.putExtras(bundle);
                    }
                    startActivity(intent);
                    overridePendingTransition(0,0);

                }

                @Override
                public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
                    super.onCaptureFailed(session, request, failure);

                    Toast.makeText(BaseActivity.this, failure.getReason(), Toast.LENGTH_SHORT).show();
                }
            };
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback(){
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(BaseActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(BaseActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }
    protected void updatePreview() {
        if(null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(BaseActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        new TabbarClick().click(BaseActivity.this,tabbar,rel_main,"home");
        check_for_tagater_count();
        check_for_oneday_feedo();



        //related to camera
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }
    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        //closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    public void makeJsonCamera(CaptureRequest.Builder mPreviewRequestBuilder){

        Float appreture = 0.0f, fovallength = 0.0f;
        int FlashMode = 0, curBrightnessValue =0;
        long Exposure = 0;

        String user_id = new TinylocalDb().get_data_in_shared(sharedPreferences,"user_id");
        if (mPreviewRequestBuilder.get(CaptureRequest.LENS_APERTURE) != null) {
            appreture = mPreviewRequestBuilder.get(CaptureRequest.LENS_APERTURE);
        }
        if (mPreviewRequestBuilder.get(CaptureRequest.LENS_FOCAL_LENGTH) != null) {

            fovallength = mPreviewRequestBuilder.get(CaptureRequest.LENS_FOCAL_LENGTH);
        }

        if (mPreviewRequestBuilder.get(CaptureRequest.FLASH_MODE) != null) {

            try {

                FlashMode = mPreviewRequestBuilder.get(CaptureRequest.FLASH_MODE);
            } catch (Exception e) {

                FlashMode = 0;
            }

        }

        try {
            curBrightnessValue = Settings.System
                    .getInt(getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS);

        } catch (Exception e) {

            curBrightnessValue = 0;
        }

        if (mPreviewRequestBuilder.get(CaptureRequest.SENSOR_EXPOSURE_TIME) != null) {
            Exposure = mPreviewRequestBuilder.get(CaptureRequest.SENSOR_EXPOSURE_TIME);
        }


        JSONObject jsonObject =null;
        try {
            jsonObject = new JSONObject();

            jsonObject.put("ApertureValue", appreture);
            jsonObject.put("BrightnessValue", curBrightnessValue);
            jsonObject.put("ExposureTime", Exposure);
            jsonObject.put("FNumber", "NA");
            jsonObject.put("Flash", FlashMode);//filename created by partitpant_id and date
            jsonObject.put("FocalLength", fovallength);
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

        user_id = new TinylocalDb().get_data_in_shared(sharedPreferences,"user_id");
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
            databaseRepo.getLastFoodTaken().observe(this,observer_lastfodTaken);
            databaseRepo.getFoodSleepExdata(commonUsedmethods.convert_date_to_timestamp("dd-MM-yyyy",commonUsedmethods.get_current_date())).
                    observe(this,observer_foodsleeEx);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showTagFoodDetails(List<Taglater> tag_later_list) {

        final Dialog list_dialog = new Dialog(BaseActivity.this);
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

        TaglaterListAdpter adapter = new TaglaterListAdpter(tag_later_list,BaseActivity.this, new OnRecycleItemClicks(){

            @Override
            public void onClick(Object object) {

                list_dialog.dismiss();
                Taglater taglater = (Taglater) object;

                Bundle bundle = new Bundle();
                bundle.putString("image", taglater.getImage());
                bundle.putString("camera_desc",taglater.getFood_camerainfo());
                bundle.putLong("time",taglater.getTimestamp());
                bundle.putBoolean("is_tag_later",true);
                Intent intent = new Intent(BaseActivity.this, FoodSendActivity.class);
                if(bundle!=null) {
                    intent.putExtras(bundle);
                }
                startActivity(intent);
                overridePendingTransition(0,0);
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

            for (int i = 0; i < arrayList.size(); i++) {

                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("type", arrayList.get(i).getFood_type());
                jsonObject1.put("time", arrayList.get(i).getFood_taken_time());

                food_array.put(jsonObject1);
            }
            jsonObject_main.put("values_for_food_mark", food_array);
            jsonObject_main.put("target_start_time","09:00");
            jsonObject_main.put("show_date",commonUsedmethods.parse_date_in_this_format(commonUsedmethods.get_current_date(),
                    "MM/dd","dd-MM-yyyy"));
            jsonObject_main.put("target_end_time","19:00");
            jsonObject_main.put("current_time",commonUsedmethods.parse_date_in_this_format(commonUsedmethods.get_current_time(),
                    "HH:mm","hh:mma"));

            jsonObject_main.put("values_for_activity_mark",new JSONArray());
            jsonObject_main.put("values_for_sleep_mark",new JSONArray());
            jsonObject_main.put("values_for_exercise_mark",new JSONArray());
            jsonArray.put(jsonObject_main);


            return jsonObject.put("data", jsonArray);
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    private void start_observres(){

        observer = new Observer<List<Taglater>>() {
            @Override
            public void onChanged(@Nullable List<Taglater> taglaters) {
                // Update the UI, in this case, a TextView.

                if(taglaters!=null) {
                    tag_later_count.setText(String.valueOf(taglaters.size()));
                    taglaterArrayList.clear();
                    taglaterArrayList.addAll(taglaters);
                }
            }
        };


        observer_foodsleeEx = new Observer<List<FoodSleepExData>>() {
            @Override
            public void onChanged(@Nullable List<FoodSleepExData> foodSleepExDataList) {

                if(foodSleepExDataList!=null) {
                    set_one_day_fedoogram(foodSleepExDataList);
                }
            }
        };

        observer_lastfodTaken = new Observer<FoodSleepExData>() {
            @Override
            public void onChanged(@Nullable FoodSleepExData foodSleepExData) {
                if(foodSleepExData!=null) {
                    show_last_taken_food_time(foodSleepExData.getTimestamp());
                }

            }
        };

    }


    private void show_last_taken_food_time(final Long time){

        Long diff_time = System.currentTimeMillis() - time;


        String hms = String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(diff_time),
                TimeUnit.MILLISECONDS.toMinutes(diff_time)
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                        .toHours(diff_time)));


        if(hms.contains(":")){

            String ar[] = hms.split(":");

            if(ar.length>1){

                String hours = "0",minute = "0";

                if(Integer.valueOf(ar[0])<10){

                    hours = Integer.valueOf(ar[0]).toString();
                }
                else{
                    hours = ar[0];
                }

                if(Integer.valueOf(ar[1])<10){

                    minute = Integer.valueOf(ar[1]).toString();
                }else{
                    minute = ar[1];
                }

                last_taken_food.setText(hours+" hours "+minute+" min since last food");

            }
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                show_last_taken_food_time(time);
            }
        },1000*60);

    }

}
