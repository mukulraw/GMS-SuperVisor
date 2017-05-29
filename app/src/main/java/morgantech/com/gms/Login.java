package morgantech.com.gms;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.okhttp.OkHttpClient;

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

public class Login extends AppCompatActivity {

    DbHelper dbHelper;
    EditText et_email, et_pin, et_email_offline, et_pin_offline;
    ProgressDialog progressDialog;
    private int REQUEST_LOCATION = 1;
    private String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    LocationFinder locationFinder;
    boolean statusOfGPS;
    LocationManager manager;
    Prefs prefs;
    EditText emailideditext;
    Dialog ipdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_email = (EditText) findViewById(R.id.et_email);
        et_pin = (EditText) findViewById(R.id.et_pin);
        et_email_offline = (EditText) findViewById(R.id.et_email_offline);
        et_pin_offline = (EditText) findViewById(R.id.et_pin_offline);
        dbHelper = new DbHelper(this);
        dbHelper.createDb(false);
        prefs = new Prefs();

        locationFinder = new LocationFinder(this);
        prefs.setPreferencesString(Login.this, "role", "abc");
        prefs.setPreferencesString(Login.this, "scan", "nfc");
        prefs.setPreferencesString(Login.this, "audio", "audio");
        prefs.setPreferencesString(Login.this, "login", "login");

        if (Helper.checkInternetConnection(Login.this)) {
            ((LinearLayout) findViewById(R.id.ll_banner)).setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.ll_offline)).setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.ll_internet)).setVisibility(View.VISIBLE);

        } else {
            ((LinearLayout) findViewById(R.id.ll_offline)).setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.ll_banner)).setVisibility(View.VISIBLE);
            ((LinearLayout) findViewById(R.id.ll_internet)).setVisibility(View.GONE);
        }

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            } else {

                if (!statusOfGPS) {
                    displayPromptForEnablingGPS(this);
                } else {

                    // Log.e("Location", String.valueOf(locationFinder.getLocation().getLongitude()) + String.valueOf(locationFinder.getLocation().getLatitude()));

                }
            }

        } else {
            if (!statusOfGPS) {
                displayPromptForEnablingGPS(this);
            } else {
                LocationFinder locationFinder = new LocationFinder(this);
//                Log.e("Location", String.valueOf(locationFinder.getLocation().getLongitude()) + String.valueOf(locationFinder.getLocation().getLatitude()));

            }
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");

        if (prefs.getPreferencesString(Login.this, "IP").matches("")) {
            //forgotIp("Ip");
            callPingPongAPi("115.118.242.137");
        } else {
            Constraints.Base_Address = prefs.getPreferencesString(Login.this, "IP");

        }

        ((TextView) findViewById(R.id.tv_forgotpass)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotpass("Forgot");
            }
        });

        ((Button) findViewById(R.id.btn_banner)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout) findViewById(R.id.ll_banner)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.ll_offline)).setVisibility(View.VISIBLE);
            }
        });
        ((Button) findViewById(R.id.btn_sign)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                prefs.setPreferencesString(Login.this, "lat", String.valueOf(locationFinder.getLatitude()));
                prefs.setPreferencesString(Login.this, "lang", String.valueOf(locationFinder.getLongitude()));
                //  Log.e("Loggg", String.valueOf(locationFinder.getLatitude()) + " " + String.valueOf(locationFinder.getLongitude()));
                if (Helper.checkInternetConnection(Login.this)) {
                    if (!(et_email.getText().toString().trim().matches("") || et_pin.getText().toString().trim().matches(""))) {
                        callApi();
                    } else {
                        Toast.makeText(Login.this, "Enter Complete Value !! ", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    if (!(et_email.getText().toString().trim().matches("") || et_pin.getText().toString().trim().matches(""))) {
                        if (dbHelper.getLogin().contains(et_email.getText().toString().trim()) & dbHelper.getPassword().contains(et_pin.getText().toString().trim())) {

                            Intent intent = new Intent(Login.this, Home.class);
                            startActivity(intent);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        }
                    } else {
                        Toast.makeText(Login.this, "Enter Complete Value !! ", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });


        ((Button) findViewById(R.id.btn_sign_offline)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Helper.checkInternetConnection(Login.this)) {
                    if (!(et_email_offline.getText().toString().matches("") || et_pin_offline.getText().toString().matches(""))) {
                        callApiOffline();
                    } else {
                        Toast.makeText(Login.this, "Enter Complete Value !! ", Toast.LENGTH_SHORT).show();
                    }
                    prefs.setPreferencesString(Login.this, "lat", String.valueOf(locationFinder.getLatitude()));
                    prefs.setPreferencesString(Login.this, "lang", String.valueOf(locationFinder.getLongitude()));

                } else {
                    prefs.setPreferencesString(Login.this, "lat", String.valueOf(locationFinder.getLatitude()));
                    prefs.setPreferencesString(Login.this, "lang", String.valueOf(locationFinder.getLongitude()));

                    if (et_email_offline.getText() != null || et_pin_offline.getText() != null) {
                        if (dbHelper.getLogin().contains(et_email_offline.getText().toString().trim()) & dbHelper.getPassword().contains(et_pin_offline.getText().toString().trim())) {
                            prefs.setPreferencesString(Login.this, "mail_id", et_email_offline.getText().toString().trim());
                            prefs.setPreferencesString(Login.this, "pass", et_pin_offline.getText().toString().trim());
                            Intent intent = new Intent(Login.this, Home.class);
                            startActivity(intent);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        } else {
                            Toast.makeText(Login.this, "Enter Correct id and password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Login.this, "Enter Complete Value !! ", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
    }

    private void callApiOffline() {

        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.getLogin(et_email_offline.getText().toString().trim(), et_pin.getText().toString().trim(),
                FirebaseInstanceId.getInstance().getToken(), new Callback<String>() {
                    public void success(String buddypojo, Response response) {

                        progressDialog.dismiss();

                        prefs.setPreferencesString(Login.this, "mail_id", et_email_offline.getText().toString().trim());


                        if (buddypojo.equals("0")) {

                            if (!dbHelper.getLogin().contains(et_email_offline.getText().toString().trim())) {
                                dbHelper.Login(et_email_offline.getText().toString().trim(), et_pin_offline.getText().toString().trim());
                            }
                            prefs.setPreferencesString(Login.this, "pass", et_pin_offline.getText().toString().trim());
                            Intent intent = new Intent(Login.this, Home.class);
                            startActivity(intent);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        } else if (buddypojo.equals("2")) {
                            Toast.makeText(Login.this, "Email Id or Password is not Valid !!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this, "User Doesn't exist", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, "Something went Wrong !!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void callApi() {

        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint(Constraints.Base_Address)//+"&mobile_id="+ FirebaseInstanceId.getInstance().getToken())
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.getLogin(et_email.getText().toString().trim(), et_pin.getText().toString().trim(), FirebaseInstanceId.getInstance().getToken(), new Callback<String>() {
            @Override
            public void success(String buddypojo, Response response) {

                progressDialog.dismiss();

                prefs.setPreferencesString(Login.this, "mail_id", et_email.getText().toString().trim());


                if (buddypojo.equals("0")) {

                    if (!dbHelper.getLogin().contains(et_email.getText().toString().trim())) {
                        dbHelper.Login(et_email.getText().toString().trim(), et_pin.getText().toString().trim());
                    }
                    prefs.setPreferencesString(Login.this, "pass", et_pin.getText().toString().trim());
                    Intent intent = new Intent(Login.this, Home.class);
                    startActivity(intent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                } else if (buddypojo.equals("2")) {
                    Toast.makeText(Login.this, "Email Id or Password is not Valid !!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Login.this, "User Doesn't exist", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();

                Toast.makeText(Login.this, "Something went Wrong !!", Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
    }

    public void displayPromptForEnablingGPS(final Activity activity) {

        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Do you want open GPS setting?";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                                prefs.deletePrefrence(Login.this);
                                Intent splashIntent = new Intent(Login.this, Splash.class);
                                splashIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(splashIntent);
                                Login.this.finish();
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        });
        builder.create().show();
    }


    private void forgotpass(String data) {
        final Dialog logoutdialog = new Dialog(Login.this);
        logoutdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logoutdialog.setContentView(R.layout.forget_password);
        logoutdialog.setCancelable(true);

        TextView header = (TextView) logoutdialog.findViewById(R.id.header);
        emailideditext = (EditText) logoutdialog.findViewById(R.id.emailideditext);
        TextView footertext = (TextView) logoutdialog.findViewById(R.id.footertext);
        TextView tv_frgtxt = (TextView) logoutdialog.findViewById(R.id.tv_frgtxt);

        if (data.equals("Ip")) {

            header.setText("Configure IP of Server");
            tv_frgtxt.setVisibility(View.GONE);
            emailideditext.setHint("Enter Server IP");
            footertext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (emailideditext.getText().toString().trim().matches("")) {
                        Toast.makeText(Login.this, "Enter Server IP", Toast.LENGTH_SHORT).show();
                    } else {
                        Constraints.Base_Address = "http://" + emailideditext.getText().toString().trim() + "/GuardIT-RWS/rest/myresource";
                        prefs.setPreferencesString(Login.this, "IP", "http://" + emailideditext.getText().toString().trim() + "/GuardIT-RWS/rest/myresource");
                        // prefs.setPreferencesString(Login.this,"IPcon", emailideditext.getText().toString().trim());
                        logoutdialog.dismiss();
                    }

                }
            });

        } else {

            header.setText("Forgot Password");
            tv_frgtxt.setVisibility(View.GONE);
            emailideditext.setHint("Enter your email Id");

            footertext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!emailideditext.getText().toString().trim().matches("")) {
                        callForgotPassApi(emailideditext.getText().toString().trim());
                        logoutdialog.dismiss();
                    } else {
                        Toast.makeText(Login.this, "Please enter your mail id", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }


        logoutdialog.show();
    }


    private void forgotIp(String data) {
        ipdialog = new Dialog(Login.this);
        ipdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ipdialog.setContentView(R.layout.forget_password);
        ipdialog.setCancelable(false);

        TextView header = (TextView) ipdialog.findViewById(R.id.header);
        emailideditext = (EditText) ipdialog.findViewById(R.id.emailideditext);
        TextView footertext = (TextView) ipdialog.findViewById(R.id.footertext);
        TextView tv_frgtxt = (TextView) ipdialog.findViewById(R.id.tv_frgtxt);

        if (data.equals("Ip")) {

/*
            header.setText("Configure IP of Server");
            tv_frgtxt.setVisibility(View.GONE);
            emailideditext.setHint("Enter Server IP");
            footertext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (emailideditext.getText().toString().trim().matches("")) {
                        Toast.makeText(Login.this, "Enter Server IP", Toast.LENGTH_SHORT).show();
                    } else {
*/

                        //callPingPongAPi(emailideditext.getText().toString().trim());
                        callPingPongAPi("115.118.242.137");
/*
                    }

                }
            });
*/

        }

        ipdialog.show();
    }

    private void callPingPongAPi(final String apiname) {

        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint("http://" + apiname + ":5000/GuardIT-RWS/rest/myresource")
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.getPingpong(new Callback<String>() {
            @Override
            public void success(String buddypojo, Response response) {

                progressDialog.dismiss();

                if (buddypojo.equals("555")) {
                    Constraints.Base_Address = "http://" + apiname + ":5000/GuardIT-RWS/rest/myresource";
                    prefs.setPreferencesString(Login.this, "IP", "http://" + apiname + ":5000/GuardIT-RWS/rest/myresource");
                    // prefs.setPreferencesString(Login.this,"IPcon", emailideditext.getText().toString().trim());
                } else {
                    Toast.makeText(Login.this, "Server IP is not Valid !!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                Toast.makeText(Login.this, "Server IP is not Valid !!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void callForgotPassApi(String trim) {

        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.getPassword(trim, new Callback<String>() {
            @Override
            public void success(String buddypojo, Response response) {

                progressDialog.dismiss();

                if (buddypojo.equals("1")) {
                    Toast.makeText(Login.this, "Email has been to account Kindly check your mail", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Login.this, "Email Id is not Valid !!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                Toast.makeText(Login.this, "Something went Wrong !!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
