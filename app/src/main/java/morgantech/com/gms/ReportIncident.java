package morgantech.com.gms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUriExposedException;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.okhttp.OkHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import morgantech.com.gms.Adapter.GalleryImageAdapter;
import morgantech.com.gms.DbHelper.DbHelper;
import morgantech.com.gms.Utils.Constraints;
import morgantech.com.gms.Utils.Helper;
import morgantech.com.gms.Utils.LocationFinder;
import morgantech.com.gms.Utils.Prefs;
import morgantech.com.gms.Utils.ScalingUtilities;
import morgantech.com.gms.WebServices.API_Interface;
import morgantech.com.gms.WebServices.StringConverter;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class ReportIncident extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //DROP DOWN MENU SECTION
    Spinner spinnerOsversions;
    TextView selVersion;
    private List<String> state;
    Context context;
    Handler handler;
    Drawable userBack;
    Bitmap scaledBitmapBk;
    TextView seconds, minutes, tv2;
    MediaRecorder mRecorder;
    String fileName;
    Boolean isRecording = false;
    int recordTime, playTime;
    MediaPlayer mPlayer;
    long ctr;
    boolean flag = true;
    boolean flagpause = false;
    ProgressDialog progressDialog;
   // Button addsignature;
    private Gallery gallery;
    private ArrayList<Uri> imageurl;
    ImageView iv_del;

    int temp = 0;
    int count=0;

    //CAMERA SECTION
    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    static int filePathIncrementer = 0;
    static int copyOfIncrementer = 0;
    int togleDeleteSave = 3;
    boolean current = true;
    boolean imgstat = false;
    boolean audiostat = false;
    String stat;
    String statmsg;
   // boolean sign = false;
    private static final String IMAGE_DIRECTORY_NAME = "CAMERA_TESTER";
    private Uri fileUri = null; // file url to store image/video
    EditText text;

    LocationFinder gps;
    double latitude, longitude;
    byte[] imageBytes;
    byte[] soundBytes;
    int tmp1 = 0;
    int tmp2 = 0;
    String severity = "non critical";
    String type, remarks = null;
    Prefs prefs;
    private static final int PICK_FROM_GALLERY = 2;
    private int selectedImagePosition = 0;
    private List<Drawable> drawables;
    private GalleryImageAdapter galImageAdapter;
    TextView iv_counter;
    LinearLayout delete;
    ImageView iv_cam;
    private double lng, lat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_incident);

        state = new ArrayList<>();

        imageurl = new ArrayList<Uri>();
        drawables = new ArrayList<>();
        iv_counter = (TextView) findViewById(R.id.iv_counter);
     //   addsignature = (Button) findViewById(R.id.addsignature);
        final Button location = (Button) findViewById(R.id.location);
        Button send = (Button) findViewById(R.id.send);
        prefs = new Prefs();
        prefs.setPreferencesString(ReportIncident.this, "login", "App");
        context = ReportIncident.this;
        ImageView settings = (ImageView) findViewById(R.id.settings);
        text = (EditText) findViewById(R.id.text);
        gallery = (Gallery) findViewById(R.id.gallery);
        iv_del = (ImageView) findViewById(R.id.iv_del);
        iv_del.setImageResource(R.drawable.delete);
        delete = (LinearLayout) findViewById(R.id.delete);
        iv_cam = (ImageView) findViewById(R.id.iv_cam);
        mPlayer = new MediaPlayer();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");

        gps = new LocationFinder(this);
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();



        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);


        apiInterface.getIncidents(new Callback<incidentListBean>() {
            @Override
            public void success(incidentListBean incidentListBean, Response response) {

                state = incidentListBean.getIncidentName();

                ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(ReportIncident.this , android.R.layout.simple_spinner_item, state);
                adapter_state
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerOsversions.setAdapter(adapter_state);

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if (tmp2 == 0) {
                    location.setBackgroundColor(Color.parseColor("#019EEd"));
                    tmp2 = 1;*/
                    if (Helper.checkInternetConnection(ReportIncident.this)) {
                        Intent intent = new Intent(ReportIncident.this, MapsActivity.class);
                        intent.putExtra("lat", String.valueOf(gps.getLatitude()));
                        intent.putExtra("lng", String.valueOf(gps.getLongitude()));
                        intent.putExtra("flag", "pic");
                        startActivityForResult(intent, 1);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        return;
                    }

/*
                }
                if (tmp2 == 1) {
                    location.setBackgroundColor(Color.LTGRAY);
                    tmp2 = 0;
                    return;

                }*/

               /* if (gps.getLocation() != null) {*/
                else
                {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                }
                // }

            }
        });

        //saving the content of edit text in a string variable

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Helper.checkInternetConnection(ReportIncident.this)) {

                        remarks = text.getText().toString().trim();
                        if (!text.getText().toString().trim().matches("")) {

                            hitRoportIncidentApi();
                        } else {
                            Toast.makeText(ReportIncident.this, "Add Remark", Toast.LENGTH_SHORT).show();
                        }

                } else {

                        remarks = text.getText().toString().trim();
                        if (!text.getText().toString().trim().matches("")) {

                            storeDb();
                        } else {
                            Toast.makeText(ReportIncident.this, "Add Remark", Toast.LENGTH_SHORT).show();
                        }


                }

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ReportIncident.this, AppSetting.class);
                startActivity(in);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });


        ((LinearLayout) findViewById(R.id.ll_home)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportIncident.this, Home.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });


        ((LinearLayout) findViewById(R.id.ll_schedule)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportIncident.this, ScheduleActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_events)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ReportIncident.this, ViewEvent.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ReportIncident.this, AppSetting.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_about)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ReportIncident.this, InfoScreen.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((ImageView) findViewById(R.id.iv_img)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportIncident.this, Home.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });


        //**************************************************************************************************************************
        //DROP DOWN MENU SECTION
        System.out.println(state.size());
        selVersion = (TextView) findViewById(R.id.tv1);
        spinnerOsversions = (Spinner) findViewById(R.id.eventtype);

        spinnerOsversions.setOnItemSelectedListener(ReportIncident.this);
        //DROP DOWN SECTION ENDS


        //********************************************************************************************************************
        //CRITICAL AND NON CRITICAL SECTION STARTS
        final Button critical = (Button) findViewById(R.id.critical);


        critical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (temp == 0) {
                    critical.setBackgroundColor(Color.RED);
                    critical.setText("Critical");
                    temp = 1;
                    severity = "critical";
                    Toast.makeText(ReportIncident.this, severity + " Selected", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (temp == 1) {
                    critical.setBackgroundColor(Color.LTGRAY);

                    temp = 0;
                    severity = "non critical";
                    critical.setText("Non Critical");
                    Toast.makeText(ReportIncident.this, severity + " Selected", Toast.LENGTH_SHORT).show();
                    return;

                }

            }
        });

        //CRITICAL AND NON CRITICAL SECTION ENDS

        //************************************************************************************************************************


        //VOICE RECORDING SECTION
        seconds = (TextView) findViewById(R.id.seconds);
        tv2 = (TextView) findViewById(R.id.tv2);
        minutes = (TextView) findViewById(R.id.minutes);


        handler = new Handler();
        ctr = System.currentTimeMillis();
        fileName = Environment.getExternalStorageDirectory() + "/REPORT_INCIDENT_AUDIO " + ctr + ".ogg";

        isRecording = false;
        final LinearLayout stop = (LinearLayout) findViewById(R.id.stop);
        final ImageView iv_save = (ImageView) findViewById(R.id.iv_save);

        final Button mic = (Button) findViewById(R.id.mic);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isRecording) {
                    mic.setBackgroundResource(R.drawable.mic);
                    stopRecording();

                    stop.setEnabled(true);
                    iv_save.setImageResource(R.drawable.pause);
                    minutes.setText("00");
                    seconds.setText("00");
                    return;
                }

                if (flag) {

                    audiostat = true;
                    startRecording(v);
                    stop.setEnabled(true);
                    flag = false;
                    iv_save.setImageResource(R.drawable.pause);
                    mic.setBackgroundResource(R.drawable.play2);
                    flagpause = false;
                    stop.setEnabled(true);

                } else if (!flag) {
                    if (flagpause) {
                        playIt();
                        iv_save.setImageResource(R.drawable.delete);
                        flagpause = false;
                        mic.setBackgroundResource(R.drawable.play2);
                        stop.setEnabled(false);
                        delete.setEnabled(false);
                        iv_del.setImageResource(R.drawable.delete);
                        //  Log.e("Play Clicked", "Recording Clicked");
                    } else {
                        mic.setBackgroundResource(R.drawable.play3);
                        stopRecording();
                        flagpause = true;
                        delete.setEnabled(true);
                        iv_save.setImageResource(R.drawable.delete);
                        stop.setEnabled(false);
                        iv_del.setImageResource(R.drawable.offline_close);
                        // Log.e("Pause Clicked", "Recording Clicked");
                        stoprec();

                    }



                }


            }


        });

        stop.setEnabled(false);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;
                delete.setClickable(true);
                iv_del.setImageResource(R.drawable.offline_close);
                mic.setBackgroundResource(R.drawable.play3);
                stopRecording();
                stop.setEnabled(false);
                flagpause = true;
                iv_save.setImageResource(R.drawable.delete);

                // Toast.makeText(ReportIncident.this, "RECORDING STOPPED...", Toast.LENGTH_SHORT).show();
            }
        });


        delete.setClickable(false);
        iv_del.setImageResource(R.drawable.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(fileName);
                audiostat = false;
                iv_del.setImageResource(R.drawable.delete);
                boolean deleted = file.delete();
                mic.setBackgroundResource(R.drawable.mic);
                delete.setClickable(false);
                iv_del.setImageResource(R.drawable.delete);
                iv_save.setImageResource(R.drawable.stop);
                stop.setEnabled(false);
                flag = true;
                if (deleted) {
                    minutes.setText("00");
                    seconds.setText("00");
                    //Toast.makeText(ReportIncident.this, "RECORDING DELETED...", Toast.LENGTH_SHORT).show();
                    flag = true;

                } //else

            }
        });
        //RECORDING SECTION ENDS

//**************************************************************************************************************************************

        //CAMERA SECTION STARTS
        //  final Button preview = (Button) findViewById(R.id.preview);
        ImageButton camera = (ImageButton) this.findViewById(R.id.camera);
        LinearLayout deletecamera = (LinearLayout) findViewById(R.id.deletecamera);
        LinearLayout save = (LinearLayout) findViewById(R.id.save);


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
                togleDeleteSave = 0;
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTakePhotoClicked(ReportIncident.this);

            }
        });

        deletecamera.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                imgstat = false;
                                                drawables.clear();

                                                setupUI();
                                            }

                                        }
        );


        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }
    }


    /**
     * Checking device has camera hardware or not
     */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }//CAMERA PART ENDS*****************************************************************************************************
    }


    //ON CREATE ENDS

    //**************************************************************************************************************************

    //CAMERA SECTION

    @SuppressLint("WrongConstant")
    private void captureImage() {
        /*fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);*/




        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = null;

        try
        {
            // place where to store camera taken picture
            photo = this.createTemporaryFile("picture", ".jpg");
            photo.delete();
        }
        catch(Exception e)
        {
            Log.v("TAG", "Can't create file to take picture!");
            Toast.makeText(ReportIncident.this , "Please check SD card! Image shot is impossible!", 10000);
        }


        fileUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        //start camera intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

        imgstat = true;




        /*try
        {*/

        /*}catch (FileUriExposedException e)
        {
            e.printStackTrace();
        }*/

        if (!imageurl.contains(fileUri)) {
            imageurl.add(fileUri);
        }
    }


    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        if(!tempDir.exists())
        {
            tempDir.mkdirs();
        }
        return File.createTempFile(part, ext, tempDir);
    }


    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
        if (!imageurl.contains(fileUri)) {
            imageurl.add(fileUri);
        }

    }

    /**
     * Receiving activity result METHOD1 will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (data != null) {
                //  String message = data.getStringExtra("MESSAGE");
                lng = data.getDoubleExtra("lng", 0);
                lat = data.getDoubleExtra("lat", 0);

            }
        }


        if (requestCode == PICK_FROM_GALLERY) {

            if (data != null) {

                Log.d("asdasd" , "1");

                Uri selectedImage = data.getData();
                //  String[] filePathColumn = {MediaStore.Images.Media.DATA};
                String filePathColumn = getRealPathFromURI(selectedImage, this);
                if (filePathColumn != null) {

                    Log.d("asdasd" , "2");

                    String root = Environment.getExternalStorageDirectory()
                            .toString();
                    File myDir = new File(root + "/morgan");
                    if (myDir.exists()) {
                        myDir.delete();

                        Log.d("asdasd" , "3");

                    }
                    Random generator = new Random();
                    int n = 10000;
                    n = generator.nextInt(n);
                    String image_name = "Image-" + n + ".jpg";
                    File fil = new File(myDir, image_name);

                    try {

                        Log.d("asdasd" , "4");

                        File file = new File(filePathColumn);
                        ExifInterface exif = new ExifInterface(file.getPath());
                        int orientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL);

                        Log.d("asdasd" , "5");

                        int angle = 0;

                        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                            angle = 90;
                        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                            angle = 180;
                        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                            angle = 270;
                        }

                        Matrix mat = new Matrix();
                        mat.postRotate(angle);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;

                        Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePathColumn);

                        Bitmap bmp = BitmapFactory.decodeStream(
                                new FileInputStream(file), null, options);
                        yourSelectedImage = Bitmap.createBitmap(bmp, 0, 0,
                                bmp.getWidth(), bmp.getHeight(), mat, true);

                        Log.d("asdasd" , "6");

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 75, stream);

                        if (yourSelectedImage != null) {
                        /*    .......................setting image in ImageView..........................*/

                            Log.d("asdasd" , "7");

                        scaledBitmapBk = ScalingUtilities
                                    .createScaledBitmap(yourSelectedImage, 900,
                                            900, ScalingUtilities.ScalingLogic.CROP);
                            userBack = new BitmapDrawable(getResources(),
                                    scaledBitmapBk);
                            //ivProfilePic.setImageDrawable(userBack);
                            drawables.add(userBack);
                            setupUI();
                            imgstat = true;
                            if (!imageurl.contains(Uri.parse(file.toString()))) {

                                Log.d("asdasd" , "8");

                                imageurl.add(Uri.parse(file.toString()));

                            }


                          /*  try {
                                fil.createNewFile();
                                FileOutputStream fo = new FileOutputStream(fil);
                                //5
                                fo.write(stream.toByteArray());
                                fo.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();

                            }*/

                        }
                    } catch (Exception e) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }

        }
        // if the result is capturing Image
        else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                previewCapturedImage(data);

            }
        }
    }


    /**
     * Display image from a path to ImageView
     */

    private void previewCapturedImage(Intent data1) {
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);




            //CONVERTING IMAGE INTO BYTE ARRAY
            if (bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                imageBytes = baos.toByteArray();
                drawables.add(new BitmapDrawable(getResources(), bitmap));
                setupUI();
            } else
                Toast.makeText(ReportIncident.this, "null", Toast.LENGTH_SHORT).show();


        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    /**
     * ------------ Helper METHOD1s ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private File getOutputMediaFile(int type) {

        String currentDateString = DateFormat.getDateInstance().format(new Date());

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        File mediaFile;

        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + currentDateString + "_" + filePathIncrementer + ".jpg");
            copyOfIncrementer = filePathIncrementer;
            filePathIncrementer++;
        } else {
            return null;
        }

        return mediaFile;
    }


    //CAMERA SECTION ENDS

//****************************************************************************************************************************

    //DROP DOWN MENU SECTION
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        spinnerOsversions.setSelection(position);
        type = (String) spinnerOsversions.getSelectedItem();
        //Toast.makeText(ReportIncident.this, type + " Selected", Toast.LENGTH_SHORT).show();
        //selVersion.setText("Selected Android OS:" + selState);
        //Toast.makeText(Report_Incident.this, selState+" SELECTED", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated METHOD1 stub

    }
    // DROP DOWN MENU SECTION ENDS

    //************************************************************************************************************************

    //RECORDIND SECTION
    public void startRecording(View view) {
        if (!isRecording) {
            //Create MediaRecorder and initialize audio source, output format, and audio encoder
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(fileName);
            audiostat = true;
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // Starting record time
            recordTime = 0;
            // Show TextView that displays record time
            seconds.setVisibility(TextView.VISIBLE);
            tv2.setVisibility(TextView.VISIBLE);
            minutes.setVisibility(TextView.VISIBLE);
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("LOG_TAG", "prepare failed");
            }
            // Start record job
            mRecorder.start();
            // Change isRecroding flag to true
            isRecording = true;
            // Post the record progress
            handler.post(UpdateRecordTime);

            //CONVERTING RECORDED SOUND INTO BYTE ARRAY
            try {
                InputStream inputStream = getContentResolver().openInputStream(Uri.fromFile(new File(fileName)));
                soundBytes = new byte[inputStream.available()];

                soundBytes = toByteArray(inputStream);


            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    public void stopRecording() {
        if (isRecording) {
            // Stop recording and release resource
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            // Change isRecording flag to false
            isRecording = false;

        }
    }

    private void stoprec() {
        if (isRecording) {
            // Stop recording and release resource
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;

            // Change isRecording flag to false
            isRecording = false;
        }
        //   mPlayer = new MediaPlayer();
        mPlayer.stop();
        mPlayer.reset();
    }

    public void playIt() {
        // Create MediaPlayer object

        seconds.setVisibility(TextView.VISIBLE);
        tv2.setVisibility(TextView.VISIBLE);
        minutes.setVisibility(TextView.VISIBLE);
        // set start time
        playTime = 0;

        // Reset max and progress of the SeekBar
        //seekBar.setMax(recordTime);
        //seekBar.setProgress(0);
        try {
            // Initialize the player and start playing the audio
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.start();
            // Post the play progress
            handler.post(UpdatePlayTime);
        } catch (IOException e) {
            Log.e("LOG_TAG", "prepare failed");
        }
    }

    Runnable UpdateRecordTime = new Runnable() {
        public void run() {
            if (isRecording) {
                if (recordTime < 60) {
                    int s = recordTime;
                    //  seconds.setText(String.valueOf(s));

                    if (s < 10) {
                        seconds.setText("0" + String.valueOf(s));
                    } else {
                        seconds.setText(String.valueOf(s));
                    }

                } else if (recordTime >= 60) {
                    int m = recordTime / 60;

                    int s2 = recordTime - (60 * m);
                    minutes.setText(String.valueOf(m));
                    if (s2 < 10) {
                        seconds.setText("0" + String.valueOf(s2));
                    } else {
                        seconds.setText(String.valueOf(s2));
                    }


                }
                recordTime += 1;
                // Delay 1s before next call
                handler.postDelayed(this, 1000);
            }
        }
    };
    Runnable UpdatePlayTime = new Runnable() {
        public void run() {
            if (mPlayer.isPlaying()) {
                if (playTime < 60) {
                    int s = playTime;
                    if (playTime < 10) {
                        seconds.setText("0" + String.valueOf(s));
                    } else {
                        seconds.setText(String.valueOf(s));
                    }
                } else if (playTime >= 60) {
                    int m = playTime / 60;

                    int s2 = playTime - (60 * m);
                    minutes.setText(String.valueOf(m));

                    if (playTime < 10) {
                        seconds.setText("0" + String.valueOf(s2));
                    } else {
                        seconds.setText(String.valueOf(s2));
                    }


                }

                // Update play time and SeekBar
                playTime += 1;
                //seekBar.setProgress(playTime);
                // Delay 1s before next call
                handler.postDelayed(this, 1000);
            }
        }
    };


    public byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = 0;
        byte[] buffer = new byte[1024];
        while (read != -1) {
            read = in.read(buffer);
            if (read != -1)
                out.write(buffer, 0, read);
        }
        out.close();
        return out.toByteArray();
    }

    //RECORDING SECTION ENDS
    //******************************************************************************************************************************

    private void hitRoportIncidentApi() {
        progressDialog.show();

        if (lng != 0.0 & lat != 0.0) {
            latitude = lat;
            longitude = lng;
        } else {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }

        RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.getReportIncedent(prefs.getPreferencesString(ReportIncident.this, "mail_id").toString(),
                prefs.getPreferencesString(ReportIncident.this, "shift_id").toString(), type, severity, text.getText().toString().trim(), latitude, longitude, new Callback<String>() {
                    @Override
                    public void success(String buddypojo, Response response) {

                        //if (buddypojo.equals("0")) {
                            //Toast.makeText(ReportIncident.this, "Problem persist Contact your Asministrator!!", Toast.LENGTH_SHORT).show();
                        //} else {
                            progressDialog.dismiss();




                            Log.d("asdasd" , buddypojo);



                            stat = buddypojo;
                            if (imgstat == true & audiostat) {
                                for (int i = 0; i < imageurl.size(); i++) {
                                    hitImageApi(buddypojo, imageurl.get(i));

                                }
                                hitApi(buddypojo);

                                statmsg = "Audio And Image Uploaded Successfully!";
                                return;
                            } else if (audiostat) {
                                hitApi(buddypojo);
                                statmsg = "Audio Uploaded Successfully!";
                                return;
                            } else if (imgstat) {
                                for (int i = 0; i < imageurl.size(); i++) {
                                    hitImageApi(buddypojo, imageurl.get(i));
                                    Log.e("Uploading", "Uploading");
                                }
                                statmsg = "Image Uploaded Successfully!";
                                return;
                            }else
                            {
                                hitEventApi("Incident\nReported");
                                statmsg = "Incident Reported Successfully!";
                            }


                        //}

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                        // Toast.makeText(ReportIncident.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void hitImageApi(String id, Uri uri) {

        progressDialog.show();

       // Log.e("Data",uri.toString() );
        TypedFile typedFile = new TypedFile("multipart/form-data", new File(uri.getPath()));
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Constraints.Base_Address).setConverter(new StringConverter())
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.uploadImage(id, uri.getLastPathSegment(), typedFile, "jpg", text.getText().toString().trim(),
                new Callback<String>() {
                    @Override
                    public void success(String data, Response response) {

                        progressDialog.dismiss();
                        imgstat = false;
                        if (data.equals("0")) {
                            hitEventApi("Image Uploading");
                        } else {
                            if (data.equals("0")) {
                                // Toast.makeText(ReportIncident.this, "Something went wrong!! ", Toast.LENGTH_SHORT).show();
                            }
                        }
                       // Log.e("Result", data);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        progressDialog.dismiss();
                        // Toast.makeText(context, , Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void hitApi(String id) {
        progressDialog.show();

        TypedFile typedFile = new TypedFile("multipart/form-data", new File(fileName));
        Log.e("File",fileName.toString());
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Constraints.Base_Address).setConverter(new StringConverter())
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.uploadImage(id, (new File(fileName)).getName(), typedFile, ".ogg", text.getText().toString().trim(),
                new Callback<String>() {
                    @Override
                    public void success(String data, Response response) {
                        if (data.equals("0")) {
                            progressDialog.dismiss();
                            audiostat = false;

                            hitEventApi("Audio Uploading");
                        } else {
                            if (data.equals("0")) {
                                Toast.makeText(ReportIncident.this, "Something went wrong!! ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                        // Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void hitEventApi(String data) {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");

        String formattedDate = df.format(c.getTime());
        String formattedTime = df1.format(c.getTime());
        Prefs prefs = new Prefs();

        if (lng != 0.0 & lat != 0.0) {
            latitude = lat;
            longitude = lng;
        } else {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }

        RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.getReportEvent(prefs.getPreferencesString(ReportIncident.this, "emp_code").toString(),
                formattedDate, formattedTime, data, text.getText().toString().trim(), severity, "open", "app",
                String.valueOf(latitude), String.valueOf(longitude), new Callback<String>() {
                    @Override
                    public void success(String buddypojo, Response response) {


                        progressDialog.dismiss();

                        if (buddypojo.equals("1")) {
                            if (current) {
                                logoutPopUp();
                                current = false;
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ReportIncident.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logoutPopUp() {
        final Dialog logoutdialog = new Dialog(ReportIncident.this);
        logoutdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logoutdialog.setContentView(R.layout.logout_layout);

        logoutdialog.setCancelable(false);
        TextView logout = (TextView) logoutdialog.findViewById(R.id.logout);
        TextView tv_title = (TextView) logoutdialog.findViewById(R.id.tv_title);
        //TextView cancel = (TextView) logoutdialog.findViewById(R.id.cancel);

        tv_title.setText("Your Incident id is " + stat + "\n" + statmsg);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                logoutdialog.dismiss();
                Intent in = new Intent(ReportIncident.this, MessagingList.class);
                in.putExtra("incident_id" , stat);
                startActivity(in);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                ReportIncident.this.finish();

            }
        });

        logoutdialog.show();
    }

    private void SignPopUp() {
        final Dialog logoutdialog = new Dialog(ReportIncident.this);
        logoutdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logoutdialog.setContentView(R.layout.sign_layout);

        logoutdialog.setCancelable(false);
        TextView logout = (TextView) logoutdialog.findViewById(R.id.logout);
        TextView cancel = (TextView) logoutdialog.findViewById(R.id.cancel);
        final EditText tv_title = (EditText) logoutdialog.findViewById(R.id.tv_title);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (prefs.getPreferencesString(ReportIncident.this, "pass").equals(tv_title.getText().toString().trim())) {
                  /*  sign = true;
*/
                  //  addsignature.setBackgroundColor(Color.parseColor("#019EEd"));
                    tmp1 = 1;
                    logoutdialog.dismiss();

                } else {
                    Toast.makeText(ReportIncident.this, "Enter right Signature!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutdialog.dismiss();
            }
        });
        logoutdialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
       /* mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        isRecording = false;*/
        ReportIncident.this.finish();
    }


    public void onTakePhotoClicked(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(ReportIncident.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(ReportIncident.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // if (!Settings.System.canWrite(CameraUpload.this)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
            } else {
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(
                        Intent.createChooser(intent, "Select File"),
                        PICK_FROM_GALLERY);
            }
        } else {
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(
                    Intent.createChooser(intent, "Select File"),
                    PICK_FROM_GALLERY);
        }
    }

    /*.......................fetching image from internal resources..................................*/
    public String getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            // cursor.close();
            return s;
        }
        // cursor.close();
        return null;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case 2909: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");

                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            PICK_FROM_GALLERY);
                } else {
                    Log.e("Permission", "Denied");
                }
                return;
            }

        }
    }

    private void setupUI() {

        Log.d("asdasd" , "9");

        if (drawables.size() == 0) {
            iv_counter.setText("No image selected.");
            iv_cam.setImageResource(R.drawable.delete);
        } else {
            iv_counter.setText(drawables.size() + " " + "image selected.");
            iv_cam.setImageResource(R.drawable.offline_close);
        }

        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                selectedImagePosition = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        try {
            galImageAdapter = new GalleryImageAdapter(this, drawables);
            gallery.setAdapter(galImageAdapter);
            if (drawables.size() > 0) {
                gallery.setSelection(selectedImagePosition, false);
            }
        }catch (Exception e)
        {
            Log.e("drawable",String.valueOf(drawables.size()));
        }
    }

    private void storeDb() {

        DbHelper dbHelper = new DbHelper(this);
        dbHelper.createDb(false);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
        final String formattedDate = df.format(c.getTime());
        final String formattedTime = df1.format(c.getTime());
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();


        if (imgstat == true & audiostat) {
            for (int i = 0; i < imageurl.size(); i++) {

                dbHelper.insertIncident("ImageUploadReport", prefs.getPreferencesString(ReportIncident.this, "emp_code"),
                        latitude, longitude, text.getText().toString().trim(), severity, prefs.getPreferencesString(ReportIncident.this, "shift_id").toString());
                dbHelper.insertIncident("ImageUpload", formattedDate+type, latitude, longitude, text.getText().toString().trim(), (new File(imageurl.get(i).getPath())).toString(), formattedTime);
            }
            dbHelper.insertIncident("AudioUploadReport", prefs.getPreferencesString(ReportIncident.this, "emp_code"),
                    latitude, longitude, text.getText().toString().trim(), severity, prefs.getPreferencesString(ReportIncident.this, "shift_id").toString());
            dbHelper.insertIncident("AudioUpload", formattedDate+type, latitude, longitude, text.getText().toString().trim(), (new File(fileName)).toString(), formattedTime);

            dbHelper.insertEvent(1, severity, text.getText().toString().trim(), formattedDate + "\n" + formattedTime, "Audio\nUpload", "Offline", String.valueOf(latitude), "app", String.valueOf(longitude));

            dbHelper.insertEvent(1, severity, text.getText().toString().trim(), formattedDate + "\n" + formattedTime, "Image\nUpload", "Offline", String.valueOf(latitude), "app", String.valueOf(longitude));
            Toast.makeText(ReportIncident.this, "Data Saved in Database", Toast.LENGTH_SHORT).show();
            ReportIncident.this.finish();
            return;
        } else if (audiostat) {
            dbHelper.insertIncident("AudioUploadReport", prefs.getPreferencesString(ReportIncident.this, "emp_code"),
                    latitude, longitude, text.getText().toString().trim(), severity, prefs.getPreferencesString(ReportIncident.this, "shift_id").toString());
            dbHelper.insertIncident("AudioUpload", formattedDate+type, latitude, longitude, text.getText().toString().trim(), (new File(fileName)).toString(), formattedTime);

            dbHelper.insertEvent(1, severity, text.getText().toString().trim(), formattedDate + "\n" + formattedTime, "Audio\nUpload", "Offline", String.valueOf(latitude), "app", String.valueOf(longitude));
            Toast.makeText(ReportIncident.this, "Data Saved in Database", Toast.LENGTH_SHORT).show();
            ReportIncident.this.finish();
            return;
        } else if (imgstat) {
            for (int i = 0; i < imageurl.size(); i++) {

                dbHelper.insertIncident("ImageUploadReport", prefs.getPreferencesString(ReportIncident.this, "emp_code"),
                        latitude, longitude, text.getText().toString().trim(), severity, prefs.getPreferencesString(ReportIncident.this, "shift_id").toString());
                dbHelper.insertIncident("ImageUpload", formattedDate+type, latitude, longitude, text.getText().toString().trim(), (new File(imageurl.get(i).getPath())).toString(), formattedTime);
            }
            dbHelper.insertEvent(1, severity, text.getText().toString().trim(), formattedDate + "\n" + formattedTime, "Image\nUpload", "Offline", String.valueOf(latitude), "app", String.valueOf(longitude));
            Toast.makeText(ReportIncident.this, "Data Saved in Database", Toast.LENGTH_SHORT).show();
            ReportIncident.this.finish();
            return;

        }


    }


}