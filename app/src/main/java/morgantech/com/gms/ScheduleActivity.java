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

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import morgantech.com.gms.Adapter.ScheduleAdapter;
import morgantech.com.gms.Pojo.ScheduleParentPojo;
import morgantech.com.gms.Utils.Constraints;
import morgantech.com.gms.Utils.Helper;
import morgantech.com.gms.Utils.Prefs;
import morgantech.com.gms.WebServices.API_Interface;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class ScheduleActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private int year;
    private int month;
    private int day;
    private ImageView check_offline;
    private TextView tv_online, tv_name;
    Prefs prefs;
    private int dateval = 1;
    private Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        recyclerView = (RecyclerView) findViewById(R.id.rv_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tv_name = (TextView) findViewById(R.id.tv_name);
        // ;
        c = Calendar.getInstance();
       /* ll1 = (LinearLayout) findViewById(R.id.ll1);
        ll2 = (LinearLayout) findViewById(R.id.ll2);
        ll3 = (LinearLayout) findViewById(R.id.ll3);
        ll4 = (LinearLayout) findViewById(R.id.ll4);
        ll5 = (LinearLayout) findViewById(R.id.ll5);
        ll6 = (LinearLayout) findViewById(R.id.ll6);
        ll7 = (LinearLayout) findViewById(R.id.ll7);
        ll8 = (LinearLayout) findViewById(R.id.ll8);*/


        prefs = new Prefs();
        prefs.setPreferencesString(ScheduleActivity.this, "login", "App");
        check_offline = (ImageView) findViewById(R.id.check_offline);
        tv_online = (TextView) findViewById(R.id.tv_online);
        tv_online.setText(prefs.getPreferencesString(this, "getTimeFrom") + " to " + prefs.getPreferencesString(this, "getTimeTo"));
        tv_name.setText(prefs.getPreferencesString(ScheduleActivity.this, "namedata"));

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait.....");

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate1 = df1.format(c.getTime());

        ((TextView) findViewById(R.id.tv_date)).setText(formattedDate.substring(0, 2));
        ((TextView) findViewById(R.id.tv_month)).setText(formattedDate.substring(3, 6));


        if (prefs.getPreferencesString(ScheduleActivity.this, "colors").equals("green")) {
            tv_online.setText(prefs.getPreferencesString(this, "getTimeFrom") + " to " + prefs.getPreferencesString(this, "getTimeTo"));
            check_offline.setImageResource(R.drawable.circle_green);
        } else {
            tv_online.setText("Shift Over");
            check_offline.setImageResource(R.drawable.circle_red);
        }

        if (Helper.checkInternetConnection(this)) {
            hitScheduleAPI(formattedDate1);
            String root = Environment.getExternalStorageDirectory()
                    .toString();
            File myDir = new File("file:///" + root + "/morgan");
            Uri myUri1 = Uri.parse(myDir + "/image.png");
            Log.e("Photo", myUri1.toString());
            Picasso.with(ScheduleActivity.this).
                    load(myUri1)//.
              /*  placeholder(R.drawable.profile_pic) // optional
                .error(R.drawable.profile_pic)*/
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(((ImageView) findViewById(R.id.iv_profile)));

        } else {
            tv_online.setText("Offline");
            check_offline.setImageResource(R.drawable.circle_red);
        }


        ((LinearLayout) findViewById(R.id.ll_picker)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog(999);
            }
        });


        ((LinearLayout) findViewById(R.id.ll_schedule)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScheduleActivity.this, ScheduleActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_home)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScheduleActivity.this, Home.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_events)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ScheduleActivity.this, ViewEvent.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ScheduleActivity.this, AppSetting.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_about)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ScheduleActivity.this, InfoScreen.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((ImageView) findViewById(R.id.iv_img)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScheduleActivity.this, Home.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });


        //  hitScheduleAPI(new StringBuilder().append(year).append("/").append(month).append("/").append(day));

    }


    private void hitScheduleAPI(String append) {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource")
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);

        final Prefs prefs = new Prefs();
        progressDialog.show();


        apiInterface.getshiftSchudule(prefs.getPreferencesString(ScheduleActivity.this, "shift_id").toString()
                , append, new Callback<ScheduleParentPojo>() {
                    @Override
                    public void success(ScheduleParentPojo buddypojo, Response response) {
                        progressDialog.dismiss();
                        try {
                            ((LinearLayout) findViewById(R.id.llgrp)).setVisibility(View.VISIBLE);
                            ((LinearLayout) findViewById(R.id.ll_cust)).setVisibility(View.VISIBLE);
                            ((LinearLayout) findViewById(R.id.ll_site)).setVisibility(View.VISIBLE);
                            ((LinearLayout) findViewById(R.id.ll_shift)).setVisibility(View.VISIBLE);

                            ((TextView) findViewById(R.id.textView3)).setText(buddypojo.getParentOrg());
                            ((TextView) findViewById(R.id.tv_chain)).setText(buddypojo.getOrg());
                            ((TextView) findViewById(R.id.tv_store)).setText(buddypojo.getSiteName());
                            ((TextView) findViewById(R.id.tv_shiftid)).setText(buddypojo.getShiftTime());
                            if (buddypojo.getParentOrg().trim().matches("")) {


                                ((LinearLayout) findViewById(R.id.llgrp)).setVisibility(View.GONE);
                            }
                            if(buddypojo.getOrg()==null || buddypojo.getOrg().trim().equals("") )
                            {
                                ((LinearLayout) findViewById(R.id.llgrp)).setVisibility(View.GONE);
                                ((TextView) findViewById(R.id.tv_chain)).setText(buddypojo.getParentOrg());
                            }

                            if(buddypojo.getEmpList().size()>0) {
                                recyclerView.setAdapter(new ScheduleAdapter(ScheduleActivity.this, (buddypojo.getEmpList()), dateval));
                            }else
                            {
                                recyclerView.setAdapter(null);
                            }


                        } catch (Exception e) {
                            Log.e("Something Went wrong", "!!");
                            recyclerView.setAdapter(null);
                            ((LinearLayout) findViewById(R.id.llgrp)).setVisibility(View.GONE);
                            ((LinearLayout) findViewById(R.id.ll_cust)).setVisibility(View.GONE);
                            ((LinearLayout) findViewById(R.id.ll_site)).setVisibility(View.GONE);
                            ((LinearLayout) findViewById(R.id.ll_shift)).setVisibility(View.GONE);

                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ScheduleActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                joinDate = String.valueOf(selectedYear) + "-" + "0" +
                        String.valueOf(selectedMonth + 1) + "-" + String.valueOf(selectedDay);
            } else {
                joinDate = String.valueOf(selectedYear) + "-" +
                        String.valueOf(selectedMonth + 1) + "-" + String.valueOf(selectedDay);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date date = null;
            try {
                date = sdf.parse(joinDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar c = Calendar.getInstance();
            dateval = c.getTime().compareTo(date);
            //  Log.e("Coming data",String.valueOf( c.getTime().compareTo(date)));

          /*  if (dateval == -1) {
                ll1.setVisibility(View.INVISIBLE);
                ll2.setVisibility(View.INVISIBLE);
                ll3.setVisibility(View.INVISIBLE);
                ll4.setVisibility(View.INVISIBLE);
                ll5.setVisibility(View.INVISIBLE);
                ll6.setVisibility(View.INVISIBLE);
                ll7.setVisibility(View.INVISIBLE);
                ll8.setVisibility(View.INVISIBLE);

            } else {
                ll1.setVisibility(View.VISIBLE);
                ll2.setVisibility(View.VISIBLE);
                ll3.setVisibility(View.VISIBLE);
                ll4.setVisibility(View.VISIBLE);
                ll5.setVisibility(View.VISIBLE);
                ll6.setVisibility(View.VISIBLE);
                ll7.setVisibility(View.VISIBLE);
                ll8.setVisibility(View.VISIBLE);
            }*/

            if (Helper.checkInternetConnection(ScheduleActivity.this)) {
                hitScheduleAPI(joinDate);
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


        }
    };

    @Override
    protected void onResume() {
        super.onResume();

     /*   if (Helper.checkInternetConnection(ScheduleActivity.this)) {
            check_offline.setImageResource(R.drawable.circle_green);
        } else {
            check_offline.setImageResource(R.drawable.circle_red);
            tv_online.setText("Offline");
        }*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);

    }
}
