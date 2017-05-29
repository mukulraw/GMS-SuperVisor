package morgantech.com.gms;

import android.*;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import morgantech.com.gms.DbHelper.DbDataUpload;
import morgantech.com.gms.DbHelper.DbHelper;
import morgantech.com.gms.Pojo.HomePojo;
import morgantech.com.gms.Pojo.ShiftDetailPojo;
import morgantech.com.gms.Utils.Constraints;
import morgantech.com.gms.Utils.Helper;
import morgantech.com.gms.Utils.Prefs;
import morgantech.com.gms.WebServices.API_Interface;
import morgantech.com.gms.WebServices.StringConverter;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;



public class Activity extends AppCompatActivity {


    TextView tv_online , tv_name;
    ImageView ivProfilePic, check_offline;
    ProgressDialog progressDialog;
    DbHelper dbHelper;
    Calendar c;
    Prefs prefs;
    boolean flagscan = false;
    SimpleDateFormat df1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_);
        check_offline = (ImageView) findViewById(R.id.check_offline);

        df1 = new SimpleDateFormat("HH:mm");
        prefs = new Prefs();
        c = Calendar.getInstance();
        dbHelper = new DbHelper(this);
        dbHelper.createDb(false);
        dbHelper.getLogin();

        tv_online = (TextView) findViewById(R.id.tv_online);
        tv_name = (TextView) findViewById(R.id.tv_name);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(Activity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 2900);
            } else {


            }
            if (ActivityCompat.checkSelfPermission(Activity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, 2905);
            }
            if (ActivityCompat.checkSelfPermission(Activity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(Activity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE}, 2902);
            } else {
                if (Helper.checkInternetConnection(Activity.this)) {
                    callApi();
                    DbDataUpload dbDataUpload = new DbDataUpload(this);
                    dbHelper.Events();
                    progressDialog.show();
                } else {
                    tv_name.setText("Hi\n" + dbHelper.getEmployee(prefs.getPreferencesString(Activity.this, "mail_id")).get(0));

                    if (prefs.getPreferencesString(Activity.this, "login").equals("login")) {
                        storeDb();
                    }
                    flagscan = true;
                    Log.e("Login", "loggg");
                }
            }
        } else {

            if (Helper.checkInternetConnection(Activity.this)) {
                callApi();
                DbDataUpload dbDataUpload = new DbDataUpload(this);
                dbHelper.Events();
                progressDialog.show();
            } else {

                if (prefs.getPreferencesString(Activity.this, "login").equals("login")) {
                    storeDb();
                }
                tv_name.setText("Hi\n" + dbHelper.getEmployee(prefs.getPreferencesString(Activity.this, "mail_id")).get(0));
                flagscan = true;
                Log.e("Login", "loggg");
            }

        }

    }


    private void callShiftDetailApi(String shiftId) {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);

        apiInterface.getshiftDetail(shiftId, new Callback<ShiftDetailPojo>() {
            @Override
            public void success(ShiftDetailPojo buddypojo, Response response) {

                try {
                    progressDialog.dismiss();
                    tv_online.setText("Active Shift \n" + buddypojo.getTimeFrom() + " - " + buddypojo.getTimeTo());
                    prefs.setPreferencesString(Activity.this, "getTimeFrom", buddypojo.getTimeFrom());
                    prefs.setPreferencesString(Activity.this, "getTimeTo", buddypojo.getTimeTo());

                    df1 = new SimpleDateFormat("HH:mm");
                    String ctime = df1.format(c.getTime());
//                Log.e("ctime", ctime.substring(0, 2) + buddypojo.getTimeFrom().substring(0, 2) + buddypojo.getTimeTo().substring(0, 2));


                    if (Integer.parseInt(ctime.substring(0, 2)) - Integer.parseInt(buddypojo.getTimeFrom().substring(0, 2)) > 0 &&
                            Integer.parseInt(buddypojo.getTimeTo().substring(0, 2)) - Integer.parseInt(ctime.substring(0, 2)) > 0) {
                        tv_online.setText("Active Shift \n" + buddypojo.getTimeFrom() + " - " + buddypojo.getTimeTo());
                        check_offline.setImageResource(R.drawable.circle_green);
                        flagscan = true;
                        prefs.setPreferencesString(Activity.this, "colors", "green");

                    } else {

                        tv_online.setText("Shift Over");
                        check_offline.setImageResource(R.drawable.circle_red);
                        flagscan = false;
                        prefs.setPreferencesString(Activity.this, "colors", "red");


                    }

                } catch (Exception e) {

                    tv_online.setText("Shift Not Scheduled");
                    check_offline.setImageResource(R.drawable.circle_red);
                    flagscan = false;
                    prefs.setPreferencesString(Activity.this, "colors", "red");
                    if (prefs.getPreferencesString(Activity.this, "role").equals("Admin")) {
                        tv_online.setText("Register NFC");
                        check_offline.setImageResource(R.drawable.nfclogo);
                        ((LinearLayout) findViewById(R.id.ll_off)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Activity.this, DataNfc.class));
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


    private void callApi() {


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);

        apiInterface.getProfile(prefs.getPreferencesString(Activity.this, "mail_id"), new Callback<HomePojo>() {
            @Override
            public void success(HomePojo buddypojo, Response response) {

                prefs.setPreferencesString(Activity.this, "emp_code", buddypojo.getEmpCode());

                prefs.setPreferencesString(Activity.this, "shift_id", buddypojo.getShiftId());
                if (prefs.getPreferencesString(Activity.this, "login").equals("login")) {
                    hitEventApi();
                }

                tv_name.setText("Hi\n" + buddypojo.getFirst() + " " + buddypojo.getLast());
                prefs.setPreferencesString(Activity.this, "namedata", buddypojo.getFirst() + " " + buddypojo.getLast());
                dbHelper.employee_tab(buddypojo.getFirst(), buddypojo.getLast(), buddypojo.getEmpCode(), prefs.getPreferencesString(Activity.this, "mail_id"));
                new DownloadMusicfromInternet().execute("http://115.118.242.137:5000/GuardIT-RWS/rest/myresource/profilepic?emp_id=" + buddypojo.getEmpCode());
                callShiftDetailApi(buddypojo.getShiftId());

                prefs.setPreferencesString(Activity.this, "role", buddypojo.getRole());


            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                //Toast.makeText(Home.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storeDb() {
        DbHelper dbHelper = new DbHelper(this);
        dbHelper.createDb(false);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
        final String formattedDate = df.format(c.getTime());
        final String formattedTime = df1.format(c.getTime());
        dbHelper.insertIncident("Login", prefs.getPreferencesString(Activity.this, "mail_id"), Double.parseDouble(prefs.getPreferencesString(Activity.this, "lat")), Double.parseDouble(prefs.getPreferencesString(Activity.this, "lang")), "Login/nSuccessfull", formattedDate + prefs.getPreferencesString(Activity.this, "emp_code").toString(), formattedTime);

        dbHelper.insertEvent(1, "Severe", "Login", formattedDate + "\n" + formattedTime, "Login", "Offline", prefs.getPreferencesString(Activity.this, "lat"), "app", prefs.getPreferencesString(Activity.this, "lang"));
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
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.getReportEvent(prefs.getPreferencesString(Activity.this, "emp_code").toString(),
                formattedDate, formattedTime, "Login", "Login Success", "high", "open", "app",
                prefs.getPreferencesString(Activity.this, "lat"), prefs.getPreferencesString(Activity.this, "lang"), new Callback<String>() {
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

        private void showImage() {

            String root = Environment.getExternalStorageDirectory()
                    .toString();
            File myDir = new File("file:///" + root + "/morgan");
            Uri myUri1 = Uri.parse(myDir + "/image.png");
            Log.e("Photo", myUri1.toString());
            Picasso.with(Activity.this).
                    load(myUri1)//.
              /*  placeholder(R.drawable.profile_pic) // optional
                .error(R.drawable.profile_pic)*/
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(ivProfilePic);
        }

    }

}
