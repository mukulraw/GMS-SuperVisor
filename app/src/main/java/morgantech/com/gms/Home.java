package morgantech.com.gms;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.iid.FirebaseInstanceId;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import morgantech.com.gms.DbHelper.DbDataUpload;
import morgantech.com.gms.DbHelper.DbHelper;
import morgantech.com.gms.Pojo.HomePojo;
import morgantech.com.gms.Pojo.ShiftDetailPojo;
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

public class Home extends AppCompatActivity {

    ImageView ivProfilePic, check_offline;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    Drawable userImage, userBack;
    Bitmap scaledBitmapBk;

    LinearLayout tracker;

    boolean isFlashOn;
    TextView tv_online, tv_name;
    Prefs prefs;
    DbHelper dbHelper;
    ProgressDialog progressDialog;
    Camera camera;
    Camera.Parameters p;


    LocationFinder locationFinder;
    Calendar c;
    SimpleDateFormat df1;
    boolean flagscan = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        c = Calendar.getInstance();
        df1 = new SimpleDateFormat("HH:mm");
        locationFinder = new LocationFinder(this);

        dbHelper = new DbHelper(this);
        dbHelper.createDb(false);
        dbHelper.getLogin();
        ivProfilePic = (ImageView) findViewById(R.id.iv_profile);
        tv_name = (TextView) findViewById(R.id.tv_name);
        prefs = new Prefs();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");

        /*Calendar c = Calendar.getInstance();*/
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());

        ((TextView) findViewById(R.id.tv_date)).setText(formattedDate.substring(0, 2));
        ((TextView) findViewById(R.id.tv_month)).setText(formattedDate.substring(3, 6));





        ((Button) findViewById(R.id.btn_logout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutPopUp("Logout");
            }
        });

        check_offline = (ImageView) findViewById(R.id.check_offline);
        tv_online = (TextView) findViewById(R.id.tv_online);
        //check location and sms permission


        ((LinearLayout) findViewById(R.id.ll_camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //callCameraCode();
                onPickFromGaleryClicked(Home.this);
            }
        });

        ((LinearLayout) findViewById(R.id.ll_event)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Home.this, ViewEvent.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        ((LinearLayout) findViewById(R.id.ll_msg)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Home.this, Messaging.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });


        ((LinearLayout) findViewById(R.id.ta)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, ScheduleActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        ((LinearLayout) findViewById(R.id.ll_setting)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, AppSetting.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        ((LinearLayout) findViewById(R.id.ll_scan)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagscan) {
                    if (prefs.getPreferencesString(Home.this, "scan").equals("nfc")) {
                        startActivity(new Intent(Home.this, ScanGuard.class));
                    } else if (prefs.getPreferencesString(Home.this, "scan").equals("face")) {
                        startActivity(new Intent(Home.this, ImageScan.class));
                    } else {
                        startActivity(new Intent(Home.this, QrScan.class));
                    }
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            }
        });

        ((LinearLayout) findViewById(R.id.ll_abt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, InfoScreen.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        ((LinearLayout) findViewById(R.id.ll_report)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in2 = getIntent();
                int vidpic = in2.getIntExtra("vidpic", 0);
                if (vidpic == 1) {
                    startActivity(new Intent(Home.this, Report_Incident_video.class));

                } else {
                    startActivity(new Intent(Home.this, ReportIncident.class));
                }
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });


        ((LinearLayout) findViewById(R.id.ll_map)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* Intent intent = new Intent(Home.this, MapActivity.class);
                intent.putExtra("lat", String.valueOf(locationFinder.getLatitude()));
                intent.putExtra("lng", String.valueOf(locationFinder.getLongitude()));
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);*/

                Intent intent = new Intent(Home.this, MapsActivity.class);
                intent.putExtra("lat", String.valueOf(locationFinder.getLatitude()));
                intent.putExtra("lng", String.valueOf(locationFinder.getLongitude()));
                intent.putExtra("flag", "home");
                startActivityForResult(intent, 3);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                return;
            }
        });


        ((LinearLayout) findViewById(R.id.ll_torch)).setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {

                                                                                /*try {
                                                                                    camera = Camera.open();
                                                                                    p = camera.getParameters();
                                                                                } catch (Exception e) {

                                                                                }

                                                                                boolean hasFlash = getApplicationContext().getPackageManager()
                                                                                        .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                                                                                if (!hasFlash) {
                                                                                    AlertDialog alert = new AlertDialog.Builder(Home.this)
                                                                                            .create();
                                                                                    alert.setTitle("Error");
                                                                                    alert.setMessage("Sorry, your device doesn't support flash light!");
                                                                                    alert.setButton("OK", new DialogInterface.OnClickListener() {
                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                            finish();
                                                                                        }
                                                                                    });
                                                                                    alert.show();
                                                                                    return;
                                                                                } else {
                                                                                    if (isFlashOn) {
                                                                                        turnOffFlash();
                                                                                        Log.e("turnOffFlash", "turnOffFlash");
                                                                                    } else {
                                                                                        turnOnFlash();
                                                                                    }

                                                                                }*/



                                                                                Intent intent = new Intent(Home.this , morgantech.com.gms.Activity.class);
                                                                                startActivity(intent);

                                                                            }
                                                                        }

        );


        /*tracker = (LinearLayout)findViewById(R.id.tracker);

        tracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this , morgantech.com.gms.Activity.class);
                startActivity(intent);
            }
        });*/


    }









    private void callCameraCode() {

        final CharSequence[] item = {"Take a photo", "Import", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i == 0)
                    onPickFromGaleryClicked(Home.this);
                else if (i == 1)
                    onTakePhotoClicked(Home.this);
                else if (item[i].equals("Cancel"))
                    dialog.dismiss();
            }
        });
        builder.show();
    }

    /*..........................Photo clicked from camera......................*/
    public void onTakePhotoClicked(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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

    /*....................import image from galary..........................*/
    public void onPickFromGaleryClicked(Context context) {
        // EasyImage.openGallery(this, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 2900);
            } else {
                // if (!Settings.System.canWrite(CameraUpload.this)) {
                Intent captureIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                // we will handle the returned data in onActivityResult
                startActivityForResult(captureIntent, PICK_FROM_CAMERA);
            }
        } else {
            Intent captureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            // we will handle the returned data in onActivityResult
            startActivityForResult(captureIntent, PICK_FROM_CAMERA);
        }
    }

    /*......................Runtime Permission in Android M....................*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case 2909: {

                try {
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
                }catch (ArrayIndexOutOfBoundsException e)
                {
                    e.printStackTrace();
                }


                return;
            }
            case 2900: {
                try {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.e("Permission", "Granted");
                  /*  camera = Camera.open();
                    p = camera.getParameters();*/
                    } else {
                        Log.e("Permission", "Denied");
                    }
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    e.printStackTrace();
                }

                return;
            }
            case 2905: {

                try {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.e("Permission", "Granted");

                  /*  Intent captureIntent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    // we will handle the returned data in onActivityResult
                    startActivityForResult(captureIntent, PICK_FROM_CAMERA);*/
                    } else {

                    }
                }catch (ArrayIndexOutOfBoundsException e)
                {
                    e.printStackTrace();
                }


                return;
            }

            case 2902: {

                try {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        callApi();
                    } else {
                        Log.e("Permission", "Denied");
                    }
                }catch (ArrayIndexOutOfBoundsException e)
                {
                    e.printStackTrace();
                }


                return;
            }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == PICK_FROM_GALLERY) {

                Uri selectedImage = data.getData();
                //  String[] filePathColumn = {MediaStore.Images.Media.DATA};
                String filePathColumn = getRealPathFromURI(selectedImage, this);
                if (filePathColumn != null) {

                    String root = Environment.getExternalStorageDirectory()
                            .toString();
                    File myDir = new File(root + "/morgan_images");
                    if (myDir.exists()) {
                        myDir.delete();
                    }
                    Random generator = new Random();
                    int n = 10000;
                    n = generator.nextInt(n);
                    String image_name = "Image-" + n + ".jpg";
                    File fil = new File(myDir, image_name);

                    try {
                        File file = new File(filePathColumn);
                        ExifInterface exif = new ExifInterface(file.getPath());
                        int orientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL);

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

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 75, stream);

                        if (yourSelectedImage != null) {
                        /*    .......................setting image in ImageView..........................*/
                            scaledBitmapBk = ScalingUtilities
                                    .createScaledBitmap(yourSelectedImage, 900,
                                            900, ScalingUtilities.ScalingLogic.CROP);
                            userBack = new BitmapDrawable(getResources(),
                                    scaledBitmapBk);
                            //   ivProfilePic.setImageDrawable(userBack);

                            try {
                                fil.createNewFile();
                                FileOutputStream fo = new FileOutputStream(fil);
                                //5
                                fo.write(stream.toByteArray());
                                fo.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();

                            }
                     /*       .............................Uploading image to server........................................*/
                            // hitImageAPI(file);
                        }
                    } catch (Exception e) {
                        //Toast.makeText(Home.this, e.toString(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }

            } else if (requestCode == PICK_FROM_CAMERA) {
                Bundle extras = data.getExtras();

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");

                    String root = Environment.getExternalStorageDirectory()
                            .toString();
                    File myDir = new File(root + "/morgan_images");
                    if (myDir.exists()) {
                        myDir.delete();
                    }
                    myDir.mkdirs();
                    Random generator = new Random();
                    int n = 10000;
                    n = generator.nextInt(n);
                    String image_name = "Image-" + n + ".jpg";
                    File file = new File(myDir, image_name);
                    if (file.exists())
                        file.delete();
                    photo = (Bitmap) data.getExtras().get("data");
                    Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(
                            photo, 600, 600, ScalingUtilities.ScalingLogic.CROP);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                            stream);
                    try {
                        file.createNewFile();
                        FileOutputStream fo = new FileOutputStream(file);
                        //5
                        fo.write(stream.toByteArray());
                        fo.close();
                    } catch (IOException e) {

                        // TODO Auto-generated catch block
                        e.printStackTrace();

                    }
                    userImage = new BitmapDrawable(getResources(), scaledBitmap);
                    //ivProfilePic.setImageDrawable(userImage);
                    // hitImageAPI(file);
                    Bitmap scaledBitmapBk = ScalingUtilities.createScaledBitmap(
                            photo, 900, 900, ScalingUtilities.ScalingLogic.CROP);
                    ByteArrayOutputStream streamBk = new ByteArrayOutputStream();
                    scaledBitmapBk.compress(Bitmap.CompressFormat.JPEG, 100,
                            streamBk);

                }
            }
        }

    }


    private void turnOnFlash() {

        try {

            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(p);
            camera.startPreview();
            isFlashOn = true;
        } catch (RuntimeException e) {
            Log.e("Camera Error.", e.getMessage());
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 2900);
            } else {


            }
            if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 2905);
            }
            if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE}, 2902);
            } else {
                if (Helper.checkInternetConnection(Home.this)) {
                    callApi();
                    DbDataUpload dbDataUpload = new DbDataUpload(this);
                    dbHelper.Events();
                    progressDialog.show();
                } else {
                    tv_name.setText("Hi\n" + dbHelper.getEmployee(prefs.getPreferencesString(Home.this, "mail_id")).get(0));

                    if (prefs.getPreferencesString(Home.this, "login").equals("login")) {
                        storeDb();
                    }
                    flagscan = true;
                    Log.e("Login", "loggg");
                }
            }
        } else {

            if (Helper.checkInternetConnection(Home.this)) {
                callApi();
                DbDataUpload dbDataUpload = new DbDataUpload(this);
                dbHelper.Events();
                progressDialog.show();
            } else {

                if (prefs.getPreferencesString(Home.this, "login").equals("login")) {
                    storeDb();
                }
                tv_name.setText("Hi\n" + dbHelper.getEmployee(prefs.getPreferencesString(Home.this, "mail_id")).get(0));
                flagscan = true;
                Log.e("Login", "loggg");
            }

        }

    }

    private void turnOffFlash() {

        try {

            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(p);
            camera.stopPreview();
            isFlashOn = false;
            camera.release();

        } catch (RuntimeException e) {
            Log.e("Camera Error.", e.getMessage());
        }
    }


    private class DownloadMusicfromInternet extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... audioUrl) {
            int count;
            try {
                URL url = new URL(audioUrl[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                int lenghtOfFile = conection.getContentLength();
                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 10 * 1024);
                // Output stream to write file in SD card
                String root = Environment.getExternalStorageDirectory()
                        .toString();
                File myDir = new File(root + "/morgan");
                if (myDir.exists()) {
                    myDir.delete();
                }
                myDir.mkdir();
                OutputStream output = new FileOutputStream(myDir.getPath() + "/image.png");
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // Publish the progress which triggers onProgressUpdate method
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // Write data to file
                    output.write(data, 0, count);
                }
                // Flush output
                output.flush();
                // Close streams
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {

            Log.e("imgname: ", "imgname");
            showImage();
        }


    }

    private void showImage() {

        String root = Environment.getExternalStorageDirectory()
                .toString();
        File myDir = new File("file:///" + root + "/morgan");
        Uri myUri1 = Uri.parse(myDir.getPath() + "/image.png");
        Log.e("Photo", myUri1.toString());
        Picasso.with(Home.this).
                load(myUri1).
                placeholder(R.drawable.avatar) // optional
                .error(R.drawable.avatar)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(ivProfilePic);
    }


    private void callApi() {


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource")
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);

        apiInterface.getProfile(prefs.getPreferencesString(Home.this, "mail_id"), new Callback<HomePojo>() {
            @Override
            public void success(HomePojo buddypojo, Response response) {

                prefs.setPreferencesString(Home.this, "emp_code", buddypojo.getEmpId());



                prefs.setPreferencesString(Home.this, "shift_id", buddypojo.getShiftId());
                if (prefs.getPreferencesString(Home.this, "login").equals("login")) {
                    hitEventApi();
                }

                Constraints.LoginId = buddypojo.getLoginId();

                tv_name.setText("Hi\n" + buddypojo.getFirst() + " " + buddypojo.getLast());
                prefs.setPreferencesString(Home.this, "namedata", buddypojo.getFirst() + " " + buddypojo.getLast());
                dbHelper.employee_tab(buddypojo.getFirst(), buddypojo.getLast(), buddypojo.getEmpCode(), prefs.getPreferencesString(Home.this, "mail_id"));
                /*new DownloadMusicfromInternet().execute("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource" + "/profilepic?emp_id=" + buddypojo.getEmpCode());*/

                ImageLoader loader = ImageLoader.getInstance();

                Log.d("adsasd" , "http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource" + "/profilepic?emp_id=" + buddypojo.getEmpId());

                loader.displayImage("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource" + "/profilepic?emp_id=" + buddypojo.getEmpId() , ivProfilePic);

                callShiftDetailApi(buddypojo.getShiftId());

                prefs.setPreferencesString(Home.this, "role", buddypojo.getRole());


            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                //Toast.makeText(Home.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callShiftDetailApi(String shiftId) {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource")
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);

        apiInterface.getshiftDetail(shiftId, new Callback<ShiftDetailPojo>() {
            @Override
            public void success(ShiftDetailPojo buddypojo, Response response) {

                try {
                    progressDialog.dismiss();
                    tv_online.setText("Active Shift \n" + buddypojo.getTimeFrom() + " - " + buddypojo.getTimeTo());
                    prefs.setPreferencesString(Home.this, "getTimeFrom", buddypojo.getTimeFrom());
                    prefs.setPreferencesString(Home.this, "getTimeTo", buddypojo.getTimeTo());

                    df1 = new SimpleDateFormat("HH:mm");
                    String ctime = df1.format(c.getTime());
//                Log.e("ctime", ctime.substring(0, 2) + buddypojo.getTimeFrom().substring(0, 2) + buddypojo.getTimeTo().substring(0, 2));


                    if (Integer.parseInt(ctime.substring(0, 2)) - Integer.parseInt(buddypojo.getTimeFrom().substring(0, 2)) > 0 &&
                            Integer.parseInt(buddypojo.getTimeTo().substring(0, 2)) - Integer.parseInt(ctime.substring(0, 2)) > 0) {
                        tv_online.setText("Active Shift \n" + buddypojo.getTimeFrom() + " - " + buddypojo.getTimeTo());
                        check_offline.setImageResource(R.drawable.circle_green);
                        flagscan = true;
                        prefs.setPreferencesString(Home.this, "colors", "green");

                    } else {

                        tv_online.setText("Shift Over");
                        check_offline.setImageResource(R.drawable.circle_red);
                        flagscan = false;
                        prefs.setPreferencesString(Home.this, "colors", "red");


                    }

                } catch (Exception e) {

                    tv_online.setText("Shift Not Scheduled");
                    check_offline.setImageResource(R.drawable.circle_red);
                    flagscan = false;
                    prefs.setPreferencesString(Home.this, "colors", "red");
                    if (prefs.getPreferencesString(Home.this, "role").equals("Admin")) {
                        tv_online.setText("Register NFC");
                        check_offline.setImageResource(R.drawable.nfclogo);
                        ((LinearLayout) findViewById(R.id.ll_off)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Home.this, DataNfc.class));
                                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                            }
                        });

                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                //Toast.makeText(Home.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void hitEventApi() {
        progressDialog.show();

        //  Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");

        String formattedDate = df.format(c.getTime());
        String formattedTime = df1.format(c.getTime());


        //  Log.e("Lat", String.valueOf(lat) + String.valueOf(lang));

        RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource")
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.getReportEvent(prefs.getPreferencesString(Home.this, "emp_code").toString(),
                formattedDate, formattedTime, "Login", "Login Success", "high", "open", "app",
                prefs.getPreferencesString(Home.this, "lat"), prefs.getPreferencesString(Home.this, "lang"), new Callback<String>() {
                    @Override
                    public void success(String buddypojo, Response response) {


                        progressDialog.dismiss();

                        if (buddypojo.equals("1")) {
                            //  Toast.makeText(Home.this, "Event Generated", Toast.LENGTH_SHORT).show();

                        } else {
                            //  Toast.makeText(Home.this, "Event Not Generated", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                        //   Toast.makeText(Home.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logoutPopUp(String data) {
        final Dialog logoutdialog = new Dialog(Home.this);
        logoutdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logoutdialog.setContentView(R.layout.logout_lay);
        logoutdialog.setCancelable(true);


        if (data.equals("Back")) {

            ((TextView) logoutdialog.findViewById(R.id.header)).setText("Exit !!" + "?");
            // ((TextView) logoutdialog.findViewById(R.id.exit)).setText("Are you sure you want to Exit ?");
        } else {

            ((TextView) logoutdialog.findViewById(R.id.header)).setText("Logout" + "?");
            // ((TextView) logoutdialog.findViewById(R.id.exit)).setText("Are you sure you want to logout ?");
        }

        TextView logout = (TextView) logoutdialog.findViewById(R.id.logout);
        TextView cancel = (TextView) logoutdialog.findViewById(R.id.cancel);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callLogApi();
                Intent splashIntent = new Intent(Home.this, Splash.class);
                splashIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(splashIntent);
                Home.this.finish();
                //android.os.Process.killProcess(android.os.Process.myPid());


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

        logoutPopUp("Back");

    }

    private void storeDb() {
        DbHelper dbHelper = new DbHelper(this);
        dbHelper.createDb(false);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
        final String formattedDate = df.format(c.getTime());
        final String formattedTime = df1.format(c.getTime());
        dbHelper.insertIncident("Login", prefs.getPreferencesString(Home.this, "mail_id"), Double.parseDouble(prefs.getPreferencesString(Home.this, "lat")), Double.parseDouble(prefs.getPreferencesString(Home.this, "lang")), "Login/nSuccessfull", formattedDate + prefs.getPreferencesString(Home.this, "emp_code").toString(), formattedTime);

        dbHelper.insertEvent(1, "Severe", "Login", formattedDate + "\n" + formattedTime, "Login", "Offline", prefs.getPreferencesString(Home.this, "lat"), "app", prefs.getPreferencesString(Home.this, "lang"));
    }


    private void callLogApi() {

        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource")
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.getLogout(prefs.getPreferencesString(this, "mail_id"), new Callback<String>() {
            @Override
            public void success(String buddypojo, Response response) {
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();


            }
        });
    }

}



