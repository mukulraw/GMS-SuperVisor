package morgantech.com.gms;

import android.*;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import morgantech.com.gms.DbHelper.DbDataUpload;
import morgantech.com.gms.DbHelper.DbHelper;
import morgantech.com.gms.Pojo.HomePojo;
import morgantech.com.gms.Pojo.ShiftDetailPojo;
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


public class Activity extends AppCompatActivity {


    TextView tv_online, tv_name;
    ImageView ivProfilePic, check_offline;
    ProgressDialog progressDialog;
    DbHelper dbHelper;
    Calendar c;
    Prefs prefs;
    LinearLayout date;
    RecyclerView grid;
    GridLayoutManager manager;
    ActivityAdapter adapter;
    List<activityBean> list;

    List<String> mont;

    TextView tvDate, tvMonth;

    String dd = "";

    boolean flagscan = false;
    SimpleDateFormat df1;

    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_);
        check_offline = (ImageView) findViewById(R.id.check_offline);

        tvDate = (TextView) findViewById(R.id.tv_date);
        tvMonth = (TextView) findViewById(R.id.tv_month);

        submit = (Button) findViewById(R.id.submit);

        mont = new ArrayList<>();

        mont.add("Jan");
        mont.add("Feb");
        mont.add("Mar");
        mont.add("Apr");
        mont.add("May");
        mont.add("Jun");
        mont.add("Jul");
        mont.add("Aug");
        mont.add("Sep");
        mont.add("Oct");
        mont.add("Nov");
        mont.add("Dec");

        list = new ArrayList<>();
        adapter = new ActivityAdapter(this, list);

        date = (LinearLayout) findViewById(R.id.date);


        ivProfilePic = (ImageView) findViewById(R.id.iv_profile);

        grid = (RecyclerView) findViewById(R.id.recycler);

        manager = new GridLayoutManager(this, 1);

        dd = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        grid.setAdapter(adapter);
        grid.setLayoutManager(manager);


        Calendar cal = Calendar.getInstance();

        int dayofyear = cal.get(Calendar.DAY_OF_YEAR);
        int year = cal.get(Calendar.YEAR);
        int dayofweek = cal.get(Calendar.MONTH);
        int dayofmonth = cal.get(Calendar.DAY_OF_MONTH);


        tvDate.setText(String.valueOf(dayofmonth));
        tvMonth.setText(mont.get(dayofweek));


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



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource")
                        .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
                API_Interface apiInterface = restAdapter.create(API_Interface.class);


                apiInterface.submitActivity(prefs.getPreferencesString(Activity.this, "mail_id"), prefs.getPreferencesString(Activity.this, "shift_id") , "" , "" , new Callback<Integer>() {
                    @Override
                    public void success(Integer list, Response response) {

                        if (list == 0)
                        {
                            Toast.makeText(Activity.this , "Activity submitted successfully" , Toast.LENGTH_SHORT).show();
                        }
                        else if (list == 1)
                        {
                            Toast.makeText(Activity.this , "Something went wrong" , Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });



            }
        });



        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(Activity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.date_filter);
                dialog.show();

                final DatePicker dp = (DatePicker) dialog.findViewById(R.id.date_picker);
                Button submit = (Button) dialog.findViewById(R.id.submit);

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int da = dp.getDayOfMonth();
                        int month = dp.getMonth();

                        tvDate.setText(String.valueOf(dp.getDayOfMonth()));
                        tvMonth.setText(mont.get(dp.getMonth()-1));

                        String d = "", m = "";

                        if (da < 10) {
                            d = "0" + String.valueOf(da);
                        } else {
                            d = String.valueOf(da);
                        }
                        if (month < 10) {
                            m = "0" + String.valueOf(month);
                        } else {
                            m = String.valueOf(month);
                        }

                        //getLocations(String.valueOf(dp.getYear()) + "-" + m + "-" + d);

                        dd = String.valueOf(dp.getYear()) + "-" + m + "-" + d;

                        RestAdapter restAdapter = new RestAdapter.Builder()
                                .setEndpoint("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource")
                                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
                        API_Interface apiInterface = restAdapter.create(API_Interface.class);


                        apiInterface.getActivityList(prefs.getPreferencesString(Activity.this, "mail_id"), String.valueOf(dp.getYear()) + "-" + m + "-" + d, new Callback<List<activityBean>>() {
                            @Override
                            public void success(List<activityBean> list, Response response) {

                                adapter.setGridData(list);

                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        });


                        dialog.dismiss();

                    }
                });

            }
        });


        ((LinearLayout) findViewById(R.id.ll_events)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Activity.this, ViewEvent.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });


        ((LinearLayout) findViewById(R.id.ll_schedule)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Activity.this, ScheduleActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_home)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Activity.this, Home.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Activity.this, AppSetting.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_about)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Activity.this, InfoScreen.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource")
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);


        apiInterface.getActivityList(prefs.getPreferencesString(Activity.this, "mail_id"), dd, new Callback<List<activityBean>>() {
            @Override
            public void success(List<activityBean> list, Response response) {

                adapter.setGridData(list);

            }

            @Override
            public void failure(RetrofitError error) {

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
                .setEndpoint("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource")
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
                new DownloadMusicfromInternet().execute("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource" + "/profilepic?emp_id=" + buddypojo.getEmpCode());
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
                .setEndpoint("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource")
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

                Log.d("asdasd", audioUrl[0]);

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
                    load(myUri1).

                    placeholder(R.drawable.avatar) // optional
                    .error(R.drawable.avatar)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(ivProfilePic);
        }

    }


    public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

        private Context context;
        LocationFinder locationFinder;
        double lat = 0.00;
        double lang = 0.000;
        Prefs prefs;
        Calendar c;
        List<activityBean> list = new ArrayList<>();

        ActivityAdapter(Context context, List<activityBean> list) {
            this.context = context;
            this.list = list;
        }

        void setGridData(List<activityBean> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.activity_list_model, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final activityBean item = list.get(position);
            c = Calendar.getInstance();
            locationFinder = new LocationFinder(context);

            lat = locationFinder.getLatitude();
            lang = locationFinder.getLongitude();

            prefs = new Prefs();
            prefs.setPreferencesString(context, "login", "App");

            holder.sno.setText(item.getSeqNo());
            holder.activity.setText(item.getLocation());
            holder.type.setText(item.getValidationType());

            if (item.getStatus() == 1) {
                holder.check.setBackground(context.getResources().getDrawable(R.drawable.check_red));
            } else if (item.getStatus() == 0) {
                holder.check.setBackground(context.getResources().getDrawable(R.drawable.check_green));
            }

            holder.info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(true);
                    dialog.setContentView(R.layout.info_dialog);

                    TextView name = (TextView) dialog.findViewById(R.id.location);
                    TextView desc = (TextView) dialog.findViewById(R.id.desc);

                    name.setText(item.getActivityName());
                    desc.setText(item.getActivityDesc());

                    dialog.show();

                }
            });

            holder.check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("click", new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "  " + dd);


                    if (Objects.equals(dd, new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                        if (item.getStatus() == 1) {
                            if (item.getValidationType().trim().equals("QR Code")) {
                                Log.d("click", "QR Code");


                                Intent intent = new Intent(context, Qr2.class);
                                intent.putExtra("data", item.getActivityId());
                                context.startActivity(intent);
                            } else if (item.getValidationType().trim().equals("NFC")) {
                                Log.d("click", "NFC");

                                Intent intent = new Intent(context, Scan2.class);
                                intent.putExtra("data", item.getActivityId());
                                context.startActivity(intent);
                            } else if (item.getValidationType().trim().equals("Check")) {

                                Log.d("click", "Check");

                                final Dialog dialog = new Dialog(context);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setCancelable(false);
                                dialog.setContentView(R.layout.check_dialog);
                                dialog.show();

                                Button yes = (Button) dialog.findViewById(R.id.yes);
                                Button no = (Button) dialog.findViewById(R.id.no);
                                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                                TextView name = (TextView) dialog.findViewById(R.id.location);
                                TextView desc = (TextView) dialog.findViewById(R.id.desc);
                                final EditText remarks = (EditText) dialog.findViewById(R.id.remarks);

                                desc.setText(item.getActivityDesc());

                                name.setText(item.getActivityName());

                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        dialog.dismiss();

                                    }
                                });






                                no.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {


                                        RestAdapter restAdapter = new RestAdapter.Builder()
                                                .setEndpoint("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource")
                                                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
                                        API_Interface apiInterface = restAdapter.create(API_Interface.class);


                                        if (!remarks.getText().toString().trim().equals(""))
                                        {
                                            apiInterface.validate(item.getActivityId(), "0", remarks.getText().toString(), String.valueOf(lat), String.valueOf(lang), new Callback<Integer>() {
                                                @Override
                                                public void success(Integer integer, Response response) {

                                                    if (integer == 0) {

                                                        RestAdapter restAdapter = new RestAdapter.Builder()
                                                                .setEndpoint("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource")
                                                                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
                                                        API_Interface apiInterface = restAdapter.create(API_Interface.class);


                                                        apiInterface.getActivityList(prefs.getPreferencesString(Activity.this, "mail_id"), dd, new Callback<List<activityBean>>() {
                                                            @Override
                                                            public void success(List<activityBean> list, Response response) {

                                                                adapter.setGridData(list);

                                                            }

                                                            @Override
                                                            public void failure(RetrofitError error) {

                                                            }
                                                        });

                                                        dialog.dismiss();
                                                    } else if (integer == -99999) {

                                                        Dialog dialog1 = new Dialog(Activity.this);
                                                        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                        dialog1.setCancelable(true);
                                                        dialog1.setContentView(R.layout.location_dialog);
                                                        dialog1.show();
                                                        dialog.dismiss();


                                                    } else if (integer > 0) {
                                                        Dialog dialog1 = new Dialog(Activity.this);
                                                        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                        dialog1.setCancelable(true);
                                                        dialog1.setContentView(R.layout.done_dialog);
                                                        dialog1.show();

                                                        TextView text = (TextView) dialog1.findViewById(R.id.text);

                                                        text.setText("Incorrect Sequence : Missed Activity " + String.valueOf(integer));

                                                        dialog.dismiss();
                                                    } else if (integer < 0 && integer > -99999) {
                                                        Dialog dialog1 = new Dialog(Activity.this);
                                                        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                        dialog1.setCancelable(true);
                                                        dialog1.setContentView(R.layout.not_done_dialog);
                                                        dialog1.show();

                                                        TextView text = (TextView) dialog1.findViewById(R.id.text);

                                                        text.setText("Incorrect Sequence : Missed Activity " + String.valueOf(integer));

                                                        dialog.dismiss();
                                                    }


                                                }

                                                @Override
                                                public void failure(RetrofitError error) {

                                                }
                                            });
                                        }
                                        else
                                        {
                                            remarks.setError("This field is mandatory");
                                        }





                                    }
                                });






                                yes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {


                                        RestAdapter restAdapter = new RestAdapter.Builder()
                                                .setEndpoint("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource")
                                                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
                                        API_Interface apiInterface = restAdapter.create(API_Interface.class);

                                        Log.d("Asd" , String.valueOf(lat) +  String.valueOf(lang));

                                        apiInterface.validate(item.getActivityId(), "1", remarks.getText().toString(), String.valueOf(lat), String.valueOf(lang), new Callback<Integer>() {
                                            @Override
                                            public void success(Integer integer, Response response) {

                                                if (integer == 0) {

                                                    RestAdapter restAdapter = new RestAdapter.Builder()
                                                            .setEndpoint("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource")
                                                            .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
                                                    API_Interface apiInterface = restAdapter.create(API_Interface.class);


                                                    apiInterface.getActivityList(prefs.getPreferencesString(Activity.this, "mail_id"), dd, new Callback<List<activityBean>>() {
                                                        @Override
                                                        public void success(List<activityBean> list, Response response) {

                                                            adapter.setGridData(list);

                                                        }

                                                        @Override
                                                        public void failure(RetrofitError error) {

                                                        }
                                                    });

                                                    dialog.dismiss();
                                                } else if (integer == -99999) {

                                                    Dialog dialog1 = new Dialog(Activity.this);
                                                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                    dialog1.setCancelable(true);
                                                    dialog1.setContentView(R.layout.location_dialog);
                                                    dialog1.show();
                                                    dialog.dismiss();


                                                } else if (integer > 0) {
                                                    Dialog dialog1 = new Dialog(Activity.this);
                                                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                    dialog1.setCancelable(true);
                                                    dialog1.setContentView(R.layout.done_dialog);
                                                    dialog1.show();

                                                    TextView text = (TextView) dialog1.findViewById(R.id.text);

                                                    text.setText("Incorrect Sequence : Missed Activity " + String.valueOf(integer));

                                                    dialog.dismiss();
                                                } else if (integer < 0 && integer > -99999) {
                                                    Dialog dialog1 = new Dialog(Activity.this);
                                                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                    dialog1.setCancelable(true);
                                                    dialog1.setContentView(R.layout.not_done_dialog);
                                                    dialog1.show();

                                                    TextView text = (TextView) dialog1.findViewById(R.id.text);

                                                    text.setText("Incorrect Sequence : Missed Activity " + String.valueOf(integer));

                                                    dialog.dismiss();
                                                }


                                            }

                                            @Override
                                            public void failure(RetrofitError error) {

                                            }
                                        });


                                    }
                                });


                            }
                        }
                    }


                }
            });


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {


            TextView sno, activity, type;
            ImageButton info, check;


            ViewHolder(View itemView) {
                super(itemView);

                sno = (TextView) itemView.findViewById(R.id.sno);
                activity = (TextView) itemView.findViewById(R.id.activity);
                type = (TextView) itemView.findViewById(R.id.type);

                info = (ImageButton) itemView.findViewById(R.id.info);
                check = (ImageButton) itemView.findViewById(R.id.perform);

            }
        }


    }


}
