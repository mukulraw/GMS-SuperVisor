package morgantech.com.gms;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import morgantech.com.gms.Adapter.EventViewAdapter;
import morgantech.com.gms.DbHelper.DbHelper;
import morgantech.com.gms.Pojo.HomePojo;
import morgantech.com.gms.Pojo.ViewEventPojo;
import morgantech.com.gms.Utils.Constraints;
import morgantech.com.gms.Utils.Helper;
import morgantech.com.gms.Utils.Prefs;
import morgantech.com.gms.WebServices.API_Interface;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class ViewEvent extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    private int year;
    private int month;
    private int day;
    ImageView check_offline;
    TextView tv_online, tv_name;
    Prefs prefs;
    ImageView iv_profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait!");
        progressDialog.show();

        iv_profile = (ImageView) findViewById(R.id.iv_profile);


        ((LinearLayout) findViewById(R.id.ll_schedule)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewEvent.this, ScheduleActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_home)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewEvent.this, Home.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_events)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ViewEvent.this, ViewEvent.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ViewEvent.this, AppSetting.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_about)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ViewEvent.this, InfoScreen.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });


        tv_name = (TextView) findViewById(R.id.tv_name);
        // ;

        prefs = new Prefs();
        prefs.setPreferencesString(ViewEvent.this, "login", "App");
        check_offline = (ImageView) findViewById(R.id.check_offline);
        tv_online = (TextView) findViewById(R.id.tv_online);
        tv_name.setText(prefs.getPreferencesString(ViewEvent.this, "namedata"));
        if (prefs.getPreferencesString(ViewEvent.this, "colors").equals("green")) {
            tv_online.setText(prefs.getPreferencesString(this, "getTimeFrom") + " to " + prefs.getPreferencesString(this, "getTimeTo"));
            check_offline.setImageResource(R.drawable.circle_green);
        } else {
            tv_online.setText("Shift Over");
            check_offline.setImageResource(R.drawable.circle_red);
        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        /*if (Helper.checkInternetConnection(this)) {


            *//*String root = Environment.getExternalStorageDirectory()
                    .toString();
            File myDir = new File("file:///" + root + "/morgan");
            Uri myUri1 = Uri.parse(myDir + "/image.png");
            Log.e("Photo", myUri1.toString());
            Picasso.with(ViewEvent.this).
                    load(myUri1).
                    placeholder(R.drawable.avatar) // optional
                    .error(R.drawable.avatar)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(iv_profile);*//*
        } else {
            iv_profile.setImageResource(R.drawable.avatar);
        }*/


        ((TextView) findViewById(R.id.tv_date)).setText(formattedDate.substring(0, 2));
        ((TextView) findViewById(R.id.tv_month)).setText(formattedDate.substring(3, 6));

        recyclerView = (RecyclerView) findViewById(R.id.rv_view_event);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate1 = df1.format(c.getTime());
        DbHelper dbHelper = new DbHelper(this);
        dbHelper.createDb(false);
        if (Helper.checkInternetConnection(this)) {
            callEventApi(formattedDate1);
        } else {

            recyclerView.setAdapter(new EventViewAdapter(ViewEvent.this, dbHelper.getAllEventdata()));
            progressDialog.dismiss();
            tv_online.setText("Offline");
            check_offline.setImageResource(R.drawable.circle_red);
        }

        ((LinearLayout) findViewById(R.id.ll_picker)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog(999);
            }
        });


        ((ImageView) findViewById(R.id.iv_img)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewEvent.this, Home.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
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


        apiInterface.getProfile(prefs.getPreferencesString(ViewEvent.this, "mail_id"), new Callback<HomePojo>() {
            @Override
            public void success(HomePojo buddypojo, Response response) {








                tv_name.setText("Hi\n" + buddypojo.getFirst() + " " + buddypojo.getLast());


                /*new DownloadMusicfromInternet().execute("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource" + "/profilepic?emp_id=" + buddypojo.getEmpCode());*/

                ImageLoader loader = ImageLoader.getInstance();

                Log.d("adsasd", "http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource" + "/profilepic?emp_id=" + buddypojo.getEmpId());

                loader.displayImage("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource" + "/profilepic?emp_id=" + buddypojo.getEmpId(), iv_profile);






            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                //Toast.makeText(Home.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


      /*  if (Helper.checkInternetConnection(ViewEvent.this)) {
            check_offline.setImageResource(R.drawable.circle_green);

        } else {
            check_offline.setImageResource(R.drawable.circle_red);
            tv_online.setText("Offline");
        }*/
    }

    private void callEventApi(String formattedDate1) {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource")
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);

        apiInterface.getEventDetail(prefs.getPreferencesString(ViewEvent.this, "emp_code"), formattedDate1, new Callback<JsonArray>() {
            @Override
            public void success(JsonArray buddypojo, Response response) {

                String jsonString = buddypojo.toString();
                Type listType = new TypeToken<List<ViewEventPojo>>() {
                }.getType();
                List<ViewEventPojo> yourList = new Gson().fromJson(jsonString, listType);


                recyclerView.setAdapter(new EventViewAdapter(ViewEvent.this, yourList));
                progressDialog.dismiss();

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                Toast.makeText(ViewEvent.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener, year, month, day);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {


            String joinDate;
            String month = "Jan";

            if (String.valueOf(selectedMonth + 1).length() == 1) {
                joinDate = String.valueOf(selectedYear) + "-" + "0" + String.valueOf(selectedMonth + 1) + "-" + String.valueOf(selectedDay);
                if (String.valueOf(selectedDay).length() == 1) {
                    joinDate = String.valueOf(selectedYear) + "-" + "0" +
                            String.valueOf(selectedMonth + 1) + "-" + "0" + String.valueOf(selectedDay);
                }
            } else if (String.valueOf(selectedMonth + 1).length() == 2) {
                joinDate = String.valueOf(selectedYear) + "-" +
                        String.valueOf(selectedMonth + 1) + "-" + String.valueOf(selectedDay);
                if (String.valueOf(selectedDay).length() == 1) {
                    joinDate = String.valueOf(selectedYear) + "-" +
                            String.valueOf(selectedMonth + 1) + "-" + "0" + String.valueOf(selectedDay);
                }
            } else {
                joinDate = String.valueOf(selectedYear) + "-" +
                        String.valueOf(selectedMonth + 1) + "-" + String.valueOf(selectedDay);
            }

            if (Helper.checkInternetConnection(ViewEvent.this)) {
                callEventApi(joinDate);
            }

            if (selectedMonth == 0) {
                month = "Jan";
            } else if (selectedMonth == 1) {
                month = "Feb";
            } else if (selectedMonth == 2) {
                month = "Mar";
            } else if (selectedMonth == 3) {
                month = "Apr";
            } else if (selectedMonth == 4) {
                month = "May";
            } else if (selectedMonth == 5) {
                month = "Jun";
            } else if (selectedMonth == 6) {
                month = "Jul";
            } else if (selectedMonth == 7) {
                month = "Aug";
            } else if (selectedMonth == 8) {
                month = "Sep";
            } else if (selectedMonth == 9) {
                month = "Oct";
            } else if (selectedMonth == 10) {
                month = "Nov";
            } else if (selectedMonth == 11) {
                month = "Dec";
            }

            ((TextView) findViewById(R.id.tv_date)).setText(String.valueOf(selectedDay));
            ((TextView) findViewById(R.id.tv_month)).setText(month);


          /*  Log.e("Date", String.valueOf(selectedYear) + "-" +
                    String.valueOf(selectedMonth + 1) + "-" + String.valueOf(selectedDay));*/
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);

    }

}

