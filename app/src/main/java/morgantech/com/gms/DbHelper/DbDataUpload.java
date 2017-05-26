package morgantech.com.gms.DbHelper;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.ArrayList;

import morgantech.com.gms.Pojo.DbPojo;
import morgantech.com.gms.Pojo.ScanNFCPojo;
import morgantech.com.gms.Utils.Constraints;
import morgantech.com.gms.WebServices.API_Interface;
import morgantech.com.gms.WebServices.StringConverter;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by Administrator on 06-02-2017.
 */

public class DbDataUpload {
    Context con;
    DbHelper dbHelper;
    ArrayList<DbPojo> dataupload;
    ArrayList<DbPojo> datauploadQr;
    ArrayList<DbPojo> datauploadNFC;
    ArrayList<DbPojo> videouploadreport;
    ArrayList<DbPojo> videoupload;
    ArrayList<DbPojo> audiouploadreport;
    ArrayList<DbPojo> audioupload;
    ArrayList<DbPojo> imageuploadreport;
    ArrayList<DbPojo> imageupload;
    ArrayList<DbPojo> datauploadLogin;

    public DbDataUpload(Context context) {
        this.con = context;
        dbHelper = new DbHelper(con);
        dataupload = dbHelper.getInsertdata("ImageScan");
        datauploadQr = dbHelper.getInsertdata("QrScan");
        datauploadLogin = dbHelper.getInsertdata("Login");
        datauploadNFC = dbHelper.getInsertdata("NFCScan");
        videouploadreport = dbHelper.getInsertdata("VideoUploadReport");
        videoupload = dbHelper.getInsertdata("VideoUpload");

        audiouploadreport = dbHelper.getInsertdata("AudioUploadReport");
        audioupload = dbHelper.getInsertdata("AudioUpload");

        imageupload = dbHelper.getInsertdata("ImageUpload");
        imageuploadreport = dbHelper.getInsertdata("ImageUploadReport");


        for (int i = 0; i < imageupload.size(); i++) {
            hitReportIncidentApi(imageuploadreport.get(i).getEmail()
                    , imageuploadreport.get(i).getLat(), imageuploadreport.get(i).getLang(), imageuploadreport.get(i).getRemark()
                    , imageuploadreport.get(i).getFormattedDate(), imageuploadreport.get(i).getFormattedTime(), imageupload.get(i).getEmail()
                    , imageupload.get(i).getFormattedTime(), imageupload.get(i).getFormattedDate(), imageupload.get(i).getRemark(), "Image\nUpload", "jpg");

            if (imageupload.size() == 0) {
                imageuploadreport.clear();
                dbHelper.deleteAPi("ImageUploadReport");
            }

        }


        for (int i = 0; i < audioupload.size(); i++) {

            hitReportIncidentApi(audiouploadreport.get(i).getEmail()
                    , audiouploadreport.get(i).getLat(), audiouploadreport.get(i).getLang(), audiouploadreport.get(i).getRemark()
                    , audiouploadreport.get(i).getFormattedDate(), audiouploadreport.get(i).getFormattedTime(), audioupload.get(i).getEmail()
                    , audioupload.get(i).getFormattedTime(), audioupload.get(i).getFormattedDate(), audioupload.get(i).getRemark(), "Audio\nUpload", "mp3");
        }
        if (audioupload.size() == 0) {
            audiouploadreport.clear();
            dbHelper.deleteAPi("AudioUploadReport");
        }


        if (videoupload.size() == 0) {
            videouploadreport.clear();
            dbHelper.deleteAPi("VideoUploadReport");
        }
        for (int i = 0; i < videoupload.size(); i++) {

            hitReportIncidentApi(videouploadreport.get(i).getEmail()
                    , videouploadreport.get(i).getLat(), videouploadreport.get(i).getLang(), videouploadreport.get(i).getRemark()
                    , videouploadreport.get(i).getFormattedDate(), videouploadreport.get(i).getFormattedTime(), videoupload.get(i).getEmail()
                    , videoupload.get(i).getFormattedTime(), videoupload.get(i).getFormattedDate(), videoupload.get(i).getRemark(), "Video\nUpload", "mp4");

        }


        for (int i = 0; i < dataupload.size(); i++) {
            //  Log.e("dataup", dataupload.get(i).getEmail() + dataupload.get(i).getRemark());
            callAPi(dataupload.get(i).getEmail(), dataupload.get(i).getRemark(), dataupload.get(i).getLang(), dataupload.get(i).getLat()
                    , dataupload.get(i).getFormattedDate(), dataupload.get(i).getFormattedTime());
        }

        for (int i = 0; i < datauploadQr.size(); i++) {

            callQrAPi(datauploadQr.get(i).getEmail(), datauploadQr.get(i).getLang(), datauploadQr.get(i).getLat(), datauploadQr.get(i).getRemark()
                    , datauploadQr.get(i).getFormattedDate().substring(0, 10), datauploadQr.get(i).getFormattedTime(), datauploadQr.get(i).getFormattedDate().substring(10, datauploadQr.get(i).getFormattedDate().length()), "QR\n Scan");
        }

        for (int i = 0; i < datauploadLogin.size(); i++) {

            callQrAPi(datauploadLogin.get(i).getEmail(), datauploadLogin.get(i).getLat(), datauploadLogin.get(i).getLang(), datauploadLogin.get(i).getRemark()
                    , datauploadLogin.get(i).getFormattedDate().substring(0, 10), datauploadLogin.get(i).getFormattedTime(), datauploadLogin.get(i).getFormattedDate().substring(10, datauploadLogin.get(i).getFormattedDate().length()), "Login");
        }

        for (int i = 0; i < datauploadNFC.size(); i++) {

            //serial_no, formattedDate, formattedTime, String.valueOf(lat), String.valueOf(lang), prefs.getPreferencesString(ScanGuard.this, "shift_id").toString()

            hitNFCApi(datauploadNFC.get(i).getEmail()
                    , datauploadNFC.get(i).getFormattedDate().substring(0, 10),
                    datauploadNFC.get(i).getFormattedTime(),
                    datauploadNFC.get(i).getLat(),
                    datauploadNFC.get(i).getLang(),
                    datauploadNFC.get(i).getRemark()
                    , datauploadNFC.get(i).getFormattedDate().substring(10, datauploadNFC.get(i).getFormattedDate().length()));


        }

      /*  for (int i = 0; i < dbHelper.getPassword().size(); i++) {
            callLoginApi(dbHelper.getLogin().get(i), dbHelper.getPassword().get(i));
        }*/
    }

  /*  private void callLoginApi(String s, String s1) {

        RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint(Constraints.Base_Address+"/login?username=" + s + "&password=" + s1)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.getLogin(new Callback<String>() {
            @Override
            public void success(String buddypojo, Response response) {

                if (buddypojo.equals("1")) {

                    hitEventApi()
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
*/

    private void callAPi(String pathval, final String empcode, final String lang, final String lat, final String formatteddate, final String formattedtim) {
        File filev = new File(pathval);
        TypedFile typedFile = new TypedFile("multipart/form-data", new File(filev.getPath()));

        RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.uploadScanImage(empcode, typedFile,
                lat, lang,
                new Callback<String>() {
                    @Override
                    public void success(String buddypojo, Response response) {
                        if (buddypojo.equals("0")) {
                            hitEventApi(formatteddate, formattedtim, empcode, lat, lang, "Image\n Scan", "Image\n Scan");

                        } else {
                            /// Toast.makeText(con, "Event Not Generated", Toast.LENGTH_SHORT).show();

                            Log.e("Event", "Event Not Generated");
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(con, "Event Not Generated", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void hitEventApi(String formattedDate, final String formattedTime, String emp_code, String lat, String lang, String scanname, String vtext) {

        Log.e(scanname, lat + " " + lang);
        RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.getReportEvent(emp_code,
                formattedDate, formattedTime, scanname, vtext, "high", "open", "app",
                String.valueOf(lat), String.valueOf(lang), new Callback<String>() {
                    @Override
                    public void success(String buddypojo, Response response) {


                        if (buddypojo.equals("1")) {
                            Log.e("Event", "Event3");
                            dbHelper.deleteRecords(formattedTime);
                        } else {
                            //  Toast.makeText(Home.this, "Event Not Generated", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }


    private void callQrAPi(String mail_id, final String lat, final String lang, final String value, final String formatteddate, final String formattedtim, final String empcode, final String banner) {

        RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.getQRdata(mail_id, lat, lang,
                value, new Callback<String>() {
                    @Override
                    public void success(String buddypojo, Response response) {


                        if (buddypojo.equals("0")) {
                            hitEventApi(formatteddate, formattedtim, empcode, lat, lang, banner, value);

                        } else {

                        }
                        if (banner.equals("Login")) {

                            if (buddypojo.equals("1")) {
                                hitEventApi(formatteddate, formattedtim, empcode, lat, lang, banner, banner);

                            } else {

                            }
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }

    private void hitNFCApi(String serial_no, final String formattedDate, final String formattedTime, final String lat, final String lang, String shift_id, final String emp_code) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);

        apiInterface.getNFCScan(serial_no, formattedDate, formattedTime, lat, lang, shift_id, new Callback<ScanNFCPojo>() {
            @Override
            public void success(ScanNFCPojo buddypojo, Response response) {

                hitEventApi(formattedDate, formattedTime, emp_code, lat, lang, "Nfc\n Scan", "Nfc\n Scan");
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


    private void hitReportIncidentApi(final String emp_code, final String latitude, final String longitude, String text, String severity, String shift_id, final String getLastPathSegment
            , final String fileUri, final String vtext, final String formatteddate, final String banner, final String datatype) {

        Log.e("Date", getLastPathSegment.substring(0, 10));
        Log.e("Date2", getLastPathSegment.substring(10, getLastPathSegment.length()));
        RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.NONE).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.getReportIncedent(emp_code, shift_id, getLastPathSegment.substring(10, getLastPathSegment.length()), severity, text, Double.parseDouble(latitude), Double.parseDouble(longitude), new Callback<String>() {
            @Override
            public void success(String buddypojo, Response response) {
                if (buddypojo.equals("0")) {

                } else {

                    hitImageApi(buddypojo, getLastPathSegment.substring(0, 10), fileUri, vtext, formatteddate, emp_code, latitude, longitude, banner, datatype);
                    Log.e("BuddyPojo", buddypojo);
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


    private void hitImageApi(String id, final String getLastPathSegment, final String vtext, String fileUri, final String fomattedtime, final String emp, final String lat, final String longitude, final String banner, final String datatype) {

        TypedFile typedFile = new TypedFile("multipart/form-data", new File(fileUri));
        Log.e("File Url",fileUri.toString());
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Constraints.Base_Address).setConverter(new StringConverter())
                .setClient(new OkClient(new OkHttpClient())).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.uploadImage(id, Uri.parse(fileUri).getLastPathSegment(), typedFile, datatype, vtext,
                new Callback<String>() {
                    @Override
                    public void success(String data, Response response) {
                        if (data.equals("0")) {

                            hitEventApi(getLastPathSegment, vtext, emp, lat, longitude, banner, fomattedtime);

                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("Event5", "Event");
                    }
                });
    }

}

