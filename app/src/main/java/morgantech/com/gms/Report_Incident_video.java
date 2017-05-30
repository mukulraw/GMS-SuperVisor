package morgantech.com.gms;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import morgantech.com.gms.DbHelper.DbHelper;
import morgantech.com.gms.Utils.Constraints;
import morgantech.com.gms.Utils.Helper;
import morgantech.com.gms.Utils.LocationFinder;
import morgantech.com.gms.Utils.Prefs;
import morgantech.com.gms.WebServices.API_Interface;
import morgantech.com.gms.WebServices.StringConverter;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class Report_Incident_video extends AppCompatActivity implements OnItemSelectedListener {


    //DROP DOWN MENU SECTION
    Spinner spinnerOsversions;
    TextView selVersion;
    private String[] state = {"Fire", "Maintenance", "Suspicious", "SafetyHazard", "Crime"};
    int temp = 0;

    boolean sign = false;
    double latitude;
    double longitude;
    ProgressDialog progressDialog;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_VIDEO = 2;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "GMS_VIDEOS";
    private Uri fileUri; // file url to store image/video
    VideoView videoPreview;
    private Button video;

    static File mediaFile;
    static File mediaStorageDir;
    static String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
            Locale.getDefault()).format(new Date());
    int tmp1 = 0;
    int tmp2 = 0;
    String severity = "non critical";
    String type, remarks = null;
    Prefs prefs;
    LocationFinder gps;
    //  Button addsignature;
    EditText text;
    String stat;
    boolean flag = false;
    Button delete;
    boolean flagrecord = true;
    boolean flagplay = false;
    double lat, lng;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report__incident_video);

        //DECLERATION OF "OTHERS"
        //  addsignature = (Button) findViewById(R.id.addsignature);
        final Button location = (Button) findViewById(R.id.location);
        Button send = (Button) findViewById(R.id.send);
        LinearLayout settings = (LinearLayout) findViewById(R.id.ll_settings);
        text = (EditText) findViewById(R.id.text);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");

        gps = new LocationFinder(Report_Incident_video.this);
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();


        prefs = new Prefs();
        prefs.setPreferencesString(Report_Incident_video.this, "login", "App");

      /*  Intent in2 = getIntent();
        if (in2.getStringExtra("lng") != null) {
            lng = Double.parseDouble(in2.getStringExtra("lng"));
            lat = Double.parseDouble(in2.getStringExtra("lat"));
        }*/

        ((LinearLayout) findViewById(R.id.ll_schedule)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Report_Incident_video.this, ScheduleActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_home)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Report_Incident_video.this, Home.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_events)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Report_Incident_video.this, ViewEvent.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Report_Incident_video.this, AppSetting.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_about)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Report_Incident_video.this, Home.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((ImageView) findViewById(R.id.iv_img)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Report_Incident_video.this, Home.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });


     /*   addsignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (tmp1 == 0) {
                    SignPopUp();
                }
                if (tmp1 == 1) {
                    addsignature.setBackgroundColor(Color.LTGRAY);
                    tmp1 = 0;
                    return;

                }
            }
        });*/


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                if (tmp2 == 0) {
                    location.setBackgroundColor(Color.parseColor("#019EEd"));

                    tmp2 = 1;*/

                if (Helper.checkInternetConnection(Report_Incident_video.this)) {

                    Intent intent = new Intent(Report_Incident_video.this, MapsActivity.class);
                    intent.putExtra("lat", String.valueOf(gps.getLatitude()));
                    intent.putExtra("lng", String.valueOf(gps.getLongitude()));
                    intent.putExtra("flag", "vid");
                    startActivityForResult(intent, 2);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }

                return;

               /* }
                if (tmp2 == 1) {
                  //  location.setBackgroundColor(Color.LTGRAY);
                    tmp2 = 0;
                    return;

                }


            }*/
            }
        });

        remarks = text.getText().toString(); //saving the content of edit text in a string variable
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                   /* if (sign) {*/
                    remarks = text.getText().toString().trim();
                    if (!text.getText().toString().trim().matches("")) {

                        if (Helper.checkInternetConnection(Report_Incident_video.this)) {
                            hitRoportIncidentApi();
                        } else {
                            storeDb();
                        }

                    } else {
                        Toast.makeText(Report_Incident_video.this, "Add Remark", Toast.LENGTH_SHORT).show();
                    }


                   /* } else {
                        Toast.makeText(Report_Incident_video.this, "Add Signature First", Toast.LENGTH_SHORT).show();
                    }*/
                } else {
                    Toast.makeText(Report_Incident_video.this, "Record Video", Toast.LENGTH_SHORT).show();
                }
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Report_Incident_video.this, AppSetting.class);
                startActivity(in);
            }
        });


        //**************************************************************************************************************************
        //DROP DOWN MENU SECTION
        // System.out.println(state.length);
        selVersion = (TextView) findViewById(R.id.tv1);
        spinnerOsversions = (Spinner) findViewById(R.id.eventtype);
        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, state);
        adapter_state
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOsversions.setAdapter(adapter_state);
        spinnerOsversions.setOnItemSelectedListener(this);
        //DROP DOWN SECTION ENDS


        //********************************************************************************************************************
        //CRITICAL AND NON CRITICAL SECTION STARTS
        final Button critical = (Button) findViewById(R.id.critical);

        critical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (temp == 0) {
                    critical.setBackgroundColor(Color.RED);

                    temp = 1;
                    severity = "critical";
                    Toast.makeText(Report_Incident_video.this, severity + " Selected", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (temp == 1) {
                    critical.setBackgroundColor(Color.LTGRAY);

                    temp = 0;
                    severity = "non critical";
                    Toast.makeText(Report_Incident_video.this, severity + " Selected", Toast.LENGTH_SHORT).show();
                    return;

                }

            }
        });

        //CRITICAL AND NON CRITICAL SECTION ENDS

//******************************************************************************************************************************

//video section starts

        videoPreview = (VideoView) findViewById(R.id.videoPreview);

        video = (Button) findViewById(R.id.video);
        delete = (Button) findViewById(R.id.delete);
        delete.setEnabled(false);
        final Button Save = (Button) findViewById(R.id.save);

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    videoPreview.setVisibility(View.VISIBLE);
                    videoPreview.setVideoPath(fileUri.getPath());
                    flag = true;
                    videoPreview.stopPlayback();
                    flagplay = true;
                    delete.setBackgroundResource(R.drawable.offline_close);
                    delete.setEnabled(true);
                    Save.setBackgroundResource(R.drawable.stop);
                    Save.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(String.valueOf(fileUri));
                file.delete();
                Save.setEnabled(false);
                video.setBackgroundResource(R.drawable.mic);
                delete.setBackgroundResource(R.drawable.delete);
                delete.setEnabled(false);
                videoPreview.setVideoURI(null);
                videoPreview.setVisibility(View.INVISIBLE);
                videoPreview.setVisibility(View.VISIBLE);
                Save.setBackgroundResource(R.drawable.stop);
                flagrecord = true;
            }
        });
        video.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (flagrecord) {
                    recordVideo();
                    flagrecord = false;
                    video.setBackgroundResource(R.drawable.play3);
                    delete.setEnabled(false);
                    Save.setBackgroundResource(R.drawable.pause);
                    Save.setEnabled(true);
                    // flagplay = false;
                } else {
                    if (flagplay) {
                        previewVideo();
                        flagplay = false;
                        video.setBackgroundResource(R.drawable.play2);
                        delete.setBackgroundResource(R.drawable.offline_close);
                        Save.setBackgroundResource(R.drawable.stop);
                        Save.setEnabled(false);
                        delete.setEnabled(true);

                    } else {
                        flagplay = true;
                        try {
                            videoPreview.setVisibility(View.VISIBLE);
                            videoPreview.setVideoPath(fileUri.getPath());
                            flag = true;
                            videoPreview.stopPlayback();
                            video.setBackgroundResource(R.drawable.play3);
                            Save.setEnabled(false);
                            delete.setBackgroundResource(R.drawable.offline_close);
                            Save.setBackgroundResource(R.drawable.stop);
                            delete.setEnabled(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }
    }  //ON CREATE ENDS

    //**************************************************************************************************************************


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
        }
    }


    /**
     * Recording video
     */
    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (data != null) {
                lng = data.getDoubleExtra("lng", 0);
                lat = data.getDoubleExtra("lat", 0);

            }
        }

        if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // video successfully recorded
                // preview the recorded video

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


    /**
     * Previewing recorded video
     */
    private void previewVideo() {
        try {

            videoPreview.setVisibility(View.VISIBLE);
            videoPreview.setVideoPath(fileUri.getPath());
            flag = true;

            videoPreview.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        mediaStorageDir = new File(Environment
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

        if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }


//****************************************************************************************************************************

    //DROP DOWN MENU SECTION
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        spinnerOsversions.setSelection(position);
        String selState = (String) spinnerOsversions.getSelectedItem();
        type = selState;
        //   Toast.makeText(Report_Incident_video.this, type + " Selected", Toast.LENGTH_SHORT).show();
        //selVersion.setText("Selected Android OS:" + selState);
        //Toast.makeText(uploadIncident.this, selState+" SELECTED", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }
    // DROP DOWN MENU SECTION ENDS


    //***********************************************************************************************************************************

    //UPLOAD PART STARTS

    //PART 1 - RECEIVING INCIDENT ID FROM SERVER


    //***************************************************************************************************************************************


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
        dbHelper.insertIncident("VideoUploadReport", prefs.getPreferencesString(Report_Incident_video.this, "emp_code"),
                latitude, longitude, text.getText().toString().trim(), severity, prefs.getPreferencesString(Report_Incident_video.this, "shift_id").toString());
        dbHelper.insertIncident("VideoUpload", formattedDate + type, latitude, longitude, text.getText().toString().trim(), (new File(fileUri.getPath())).toString(), formattedTime);
        dbHelper.insertEvent(1, severity, text.getText().toString().trim(), formattedDate + "\n" + formattedTime, "Video\nUpload", "Offline", String.valueOf(latitude), "app", String.valueOf(longitude));
        Toast.makeText(Report_Incident_video.this, "Data Saved in Database", Toast.LENGTH_SHORT).show();
        Report_Incident_video.this.finish();
    }


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
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.NONE).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.getReportIncedent(prefs.getPreferencesString(Report_Incident_video.this, "mail_id").toString(),
                prefs.getPreferencesString(Report_Incident_video.this, "shift_id").toString(), type, severity, text.getText().toString().trim(), latitude, longitude, new Callback<String>() {
                    @Override
                    public void success(String buddypojo, Response response) {
                        if (buddypojo.equals("0")) {
                            Toast.makeText(Report_Incident_video.this, "Problem persist Contact your Administrator!!", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!fileUri.equals(null)) {
                                hitImageApi(buddypojo);
                                stat = buddypojo;
                            }
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                        Toast.makeText(Report_Incident_video.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void hitImageApi(String id) {

        TypedFile typedFile = new TypedFile("multipart/form-data", new File(fileUri.getPath()));
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Constraints.Base_Address).setConverter(new StringConverter())
                .setClient(new OkClient(new OkHttpClient())).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.uploadImage(id, fileUri.getLastPathSegment(), typedFile, "mp4", text.getText().toString().trim(),
                new Callback<String>() {
                    @Override
                    public void success(String data, Response response) {
                        progressDialog.dismiss();
                        // Log.e("Result", data);
                        if (data.equals("0")) {
                            Toast.makeText(Report_Incident_video.this, "Video Uploaded Successfully !!", Toast.LENGTH_SHORT).show();
                            hitEventApi();
                            logoutPopUp();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                        Toast.makeText(Report_Incident_video.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void hitEventApi() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");

        String formattedDate = df.format(c.getTime());
        String formattedTime = df1.format(c.getTime());

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
        apiInterface.getReportEvent(prefs.getPreferencesString(Report_Incident_video.this, "emp_code").toString(),
                formattedDate, formattedTime, "Video Uploading", text.getText().toString().trim(), severity, "open", "app",
                String.valueOf(latitude), String.valueOf(longitude), new Callback<String>() {
                    @Override
                    public void success(String buddypojo, Response response) {


                        progressDialog.dismiss();

                        if (buddypojo.equals("1")) {
                            Toast.makeText(Report_Incident_video.this, "Event Generated", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(Report_Incident_video.this, "Event Not Generated", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                        Toast.makeText(Report_Incident_video.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void SignPopUp() {
        final Dialog logoutdialog = new Dialog(Report_Incident_video.this);
        logoutdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logoutdialog.setContentView(R.layout.sign_layout);

        logoutdialog.setCancelable(false);
        TextView logout = (TextView) logoutdialog.findViewById(R.id.logout);
        TextView cancel = (TextView) logoutdialog.findViewById(R.id.cancel);
        final EditText tv_title = (EditText) logoutdialog.findViewById(R.id.tv_title);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (prefs.getPreferencesString(Report_Incident_video.this, "pass").equals(tv_title.getText().toString().trim())) {
                    sign = true;

                    //  addsignature.setBackgroundColor(Color.parseColor("#019EEd"));
                    tmp1 = 1;
                    logoutdialog.dismiss();

                } else {
                    Toast.makeText(Report_Incident_video.this, "Enter right Signature!", Toast.LENGTH_SHORT).show();
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

    private void logoutPopUp() {
        final Dialog logoutdialog = new Dialog(Report_Incident_video.this);
        logoutdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logoutdialog.setContentView(R.layout.logout_layout);

        logoutdialog.setCancelable(false);
        TextView logout = (TextView) logoutdialog.findViewById(R.id.logout);
        TextView tv_title = (TextView) logoutdialog.findViewById(R.id.tv_title);
        //TextView cancel = (TextView) logoutdialog.findViewById(R.id.cancel);

        tv_title.setText("Video Uploaded Successfully!" + "\n" + "Your Incident id is " + stat);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                logoutdialog.dismiss();
                Intent in = new Intent(Report_Incident_video.this, Home.class);
                startActivity(in);
                Report_Incident_video.this.finish();
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                Report_Incident_video.this.finish();

            }
        });

        logoutdialog.show();
    }


}

