package morgantech.com.gms;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.okhttp.OkHttpClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

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

public class Qr2 extends AppCompatActivity {

    double lat = 0.00;
    double lang = 0.000;
    LocationFinder locationFinder;
    Prefs prefs;
    ProgressDialog progressDialog;
    TextView textView2;
    Calendar c;
    ImageView iv_stat;
    LinearLayout ll_lower;
    String actId = "";
    ImageView scanqrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr2);

        actId = getIntent().getStringExtra("data");

        Log.d("actId" , actId);

        scanqrcode = (ImageView) findViewById(R.id.iv_nfc);
        ll_lower = (LinearLayout) findViewById(R.id.ll_lower);
        ((TextView) findViewById(R.id.tv_header)).setText("QR Code Scan");
        locationFinder = new LocationFinder(this);
        progressDialog = new ProgressDialog(this);
        prefs = new Prefs();
        prefs.setPreferencesString(Qr2.this, "login", "App");
        c = Calendar.getInstance();
        textView2 = (TextView) findViewById(R.id.textView2);
        iv_stat = (ImageView) findViewById(R.id.iv_stat);

        ((ImageView) findViewById(R.id.iv_img)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Qr2.this, Home.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });

        scanqrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator integrator = new IntentIntegrator(Qr2.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("SCAN");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_schedule)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Qr2.this, ScheduleActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_events)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Qr2.this, ViewEvent.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Qr2.this, AppSetting.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_about)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Qr2.this, InfoScreen.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((Button) findViewById(R.id.btn_scantxt)).setText("Scan Another QR Code");
        ((Button) findViewById(R.id.btn_scantxt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ll_lower.setVisibility(View.INVISIBLE);
                scanqrcode.setClickable(true);
                scanqrcode.setImageResource(R.drawable.qrscan);

            }
        });




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(Qr2.this, "YOU CANCELLED THE SCANNING", Toast.LENGTH_SHORT).show();
            } else {
                String value = result.getContents();

                // HARD CODED PART STARTS - WILL CHANGE ACCORDING TO THE NEEDS OF THE CUSTOMER

                //  Log.e("Value", value);

                if (Helper.checkInternetConnection(this)) {
                    callAPi(value);
                } else {
                    storeDb(value);
                }

                // HARD CODED PART ENDS

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void storeDb(String value) {
        lat = locationFinder.getLatitude();
        lang = locationFinder.getLongitude();
        DbHelper dbHelper = new DbHelper(this);
        dbHelper.createDb(false);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
        final String formattedDate = df.format(c.getTime());
        final String formattedTime = df1.format(c.getTime());
        dbHelper.insertIncident("QrScan", prefs.getPreferencesString(Qr2.this, "mail_id"), lat, lang, value, formattedDate + prefs.getPreferencesString(Qr2.this, "emp_code").toString(), formattedTime);
        dbHelper.insertEvent(1, "Severe", "QR\nScan", formattedDate + "\n" + formattedTime, "QR\nScan", "Offline", String.valueOf(lat), "app", String.valueOf(lang));
        Toast.makeText(Qr2.this, "Data Saved in Database", Toast.LENGTH_SHORT).show();
        Qr2.this.finish();

    }

    private void callAPi(final String value) {

        lat = locationFinder.getLatitude();
        lang = locationFinder.getLongitude();

        final SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        final SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");

        final String formattedDate = df.format(c.getTime());
        final String formattedTime = df1.format(c.getTime());

        Log.d("asdasdQR" , value);

        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource")
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);



        apiInterface.validate(actId , value , "" ,  String.valueOf(locationFinder.getLatitude()), String.valueOf(locationFinder.getLongitude()), new Callback<Integer>() {
            @Override
            public void success(Integer integer, Response response) {

                progressDialog.dismiss();

                if (integer == 0)
                {
                    finish();
                }
                else if (integer == -99999)
                {

                    Dialog dialog1 = new Dialog(Qr2.this);
                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog1.setCancelable(true);
                    dialog1.setContentView(R.layout.location_dialog);
                    dialog1.show();

                    dialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                            finish();

                        }
                    });

                }
                else if (integer>0)
                {
                    Dialog dialog1 = new Dialog(Qr2.this);
                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog1.setCancelable(true);
                    dialog1.setContentView(R.layout.done_dialog);
                    dialog1.show();

                    TextView text = (TextView)dialog1.findViewById(R.id.text);

                    text.setText("Incorrect Sequence : Missed Activity " + String.valueOf(integer));


                    dialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                            finish();

                        }
                    });

                }else if (integer<0 && integer > -99999)
                {
                    Dialog dialog1 = new Dialog(Qr2.this);
                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog1.setCancelable(true);
                    dialog1.setContentView(R.layout.not_done_dialog);
                    dialog1.show();

                    TextView text = (TextView)dialog1.findViewById(R.id.text);

                    text.setText("Incorrect Sequence : Missed Activity " + String.valueOf(integer));

                    dialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                            finish();

                        }
                    });

                }

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
            }
        });



        /*apiInterface.getQRdata(prefs.getPreferencesString(Qr2.this, "mail_id"), String.valueOf(lat), String.valueOf(lang),
                value, new Callback<String>() {
                    @Override
                    public void success(String buddypojo, Response response) {

                        progressDialog.dismiss();
                        ll_lower.setVisibility(View.VISIBLE);
                        scanqrcode.setClickable(false);
                        //  Toast.makeText(QrScan.this, buddypojo, Toast.LENGTH_SHORT).show();

                        if (buddypojo.equals("0")) {
                            hitEventApi();

                            iv_stat.setImageResource(R.drawable.unnamed);
                            textView2.setText("Tag - " + value + "\n" + "Status - Success\nDate - " + formattedDate + "\nTime - " + formattedTime);
                        } else {
                            iv_stat.setImageResource(R.drawable.wrong);
                            textView2.setText("Tag - " + value + "\n" + "Status - Success\nDate - " + df + "\nTime - " + df1);
                        }



                        RestAdapter restAdapter = new RestAdapter.Builder()
                                .setEndpoint("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource")
                                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
                        API_Interface apiInterface = restAdapter.create(API_Interface.class);




                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                        scanqrcode.setClickable(false);
                        ll_lower.setVisibility(View.VISIBLE);
                        iv_stat.setImageResource(R.drawable.wrong);
                        textView2.setText("Tag - " + value + "\n" + "Status - Success\nDate - " + df + "\nTime - " + df1);

                    }
                });*/
    }

    private void hitEventApi() {
        progressDialog.show();


        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");

        String formattedDate = df.format(c.getTime());
        String formattedTime = df1.format(c.getTime());
        if (locationFinder != null) {
            lat = locationFinder.getLatitude();
            lang = locationFinder.getLongitude();
        }


        RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource")
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.getReportEvent(prefs.getPreferencesString(Qr2.this, "emp_code").toString(),
                formattedDate, formattedTime, "QR Scan", "QR Scan", "high", "open", "app",
                String.valueOf(lat), String.valueOf(lang), new Callback<String>() {
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

    // QR CODE SCANNING PART ENDS
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);

    }


}
