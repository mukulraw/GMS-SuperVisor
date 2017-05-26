package morgantech.com.gms.DbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import morgantech.com.gms.Pojo.DbPojo;
import morgantech.com.gms.Pojo.ViewEventPojo;

/**
 * Created by Administrator
 */

public class DbHelper extends SQLiteOpenHelper {


    private String DB_FULL_PATH = "";
    public static final String DB_NAME = "gms.sqlite";
    public static String DB_PATH;
    public static volatile SQLiteDatabase db;
    Context context;
    public int count = 0;
    String data;


    public DbHelper(Context context) {
        super(context, DB_NAME, null, 2);
        this.context = context;

        DB_PATH = "/data/data/" + this.context.getPackageName() + "/databases/";
        DB_FULL_PATH = DB_PATH + DB_NAME;
        Log.v("DB PATH", DB_FULL_PATH);

    }


    public void deleteDb() {

        db = openDataBase();

        boolean boolDeleteStatus;
        try {
            File file = new File(DB_PATH + DB_NAME);
            boolDeleteStatus = file.delete();
            Log.d("Database :", "Old Data Base Deleted" + boolDeleteStatus);
        } catch (Exception e) {
            Log.e("Error:", e.getMessage());
        }

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createDb(boolean versionChange) {
        boolean chkverres;
        chkverres = checkversion();

        if (!checkDb() || chkverres) {
            // Not Exist. So we have to copy the database
            copyDataBase();

        }

        db = openDataBase();

    }

    public boolean checkversion() {
        String MISC_PREFS = "MiscPrefsFile";
        String Versionname, currentVersion;
        // Checking for database existence.
        PackageManager manager = this.context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(
                    this.context.getPackageName(), 0);
            Versionname = info.versionName;
            SharedPreferences miscPrefs = context.getSharedPreferences(
                    MISC_PREFS, Context.MODE_PRIVATE);
            currentVersion = miscPrefs.getString("Current Version", null);
            Log.v("getting version name", "getAppVersionToPrefs: got "
                    + currentVersion);
            miscPrefs.edit().putString("Current Version", Versionname).commit();
            Log.v("settinf version name",
                    "setAppVersionToPrefs: set app version to prefs"
                            + Versionname);
            if (Versionname.equals(currentVersion)) {
                return false;
            } else {
                return true;
            }

        } catch (NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return false;

        }

    }

    public boolean checkDb() {
        db = null;

        try {
            db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null,
                    SQLiteDatabase.OPEN_READWRITE
                            | SQLiteDatabase.NO_LOCALIZED_COLLATORS);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (db != null) {
            db.close();
        }

        return (db == null) ? false : true;
    }

    public static SQLiteDatabase openDataBase() throws SQLException {
        try {
            if (db == null) {

                db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null,
                        SQLiteDatabase.OPEN_READWRITE
                                | SQLiteDatabase.CREATE_IF_NECESSARY
                                | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            } else if (!db.isOpen()) {
                db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null,
                        SQLiteDatabase.OPEN_READWRITE
                                | SQLiteDatabase.CREATE_IF_NECESSARY
                                | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return db;
    }


    public void copyDataBase() {
        InputStream in;
        OutputStream os;
        byte arrByte[] = new byte[1024];

        try {
            in = context.getAssets().open(DB_NAME);

            // Making directory
            File dbFolder = new File(DB_PATH);
            if (!dbFolder.exists())
                dbFolder.mkdir();

            os = new FileOutputStream(DB_PATH + DB_NAME);
            int length;

            while ((length = in.read(arrByte)) > 0) {
                os.write(arrByte, 0, length);
            }

            // Closing the streams
            os.flush();
            in.close();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void copyDBToPhoneSD1() {
        InputStream in;
        OutputStream os;
        byte arrByte[] = new byte[1024];

        try {
            in = new FileInputStream(DB_PATH + DB_NAME);

            os = new FileOutputStream(Environment.getExternalStorageDirectory()
                    + "/" + DB_NAME);
            int length;

            while ((length = in.read(arrByte)) > 0) {
                os.write(arrByte, 0, length);
            }

            // Closing the streams
            os.flush();
            in.close();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*.............................Login List.............................................*/

    public void Login(String username, String pswrd) {
        try {
            db = openDataBase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("email", username);
            contentValues.put("password", pswrd);
            contentValues.put("role", "role");
            contentValues.put("status", "status");
            contentValues.put("last_login", "last_login");
            Cursor cursor = null;
            String query = "select * from public_login";
            cursor = db.rawQuery(query, null);
            db.insert("public_login", null, contentValues);


            copyDBToPhoneSD1();
            if (db != null)
                db.close();
        } catch (Exception e) {
            Log.e("Exception ", e.toString());
        }
    }


    public void employee_tab(String first_name, String last_name, String emp_code, String mail_id) {
        try {

            db = openDataBase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("first_name", first_name);
            contentValues.put("last_name", last_name);
            contentValues.put("role", "role");
            contentValues.put("sex", "sex");
            contentValues.put("emp_code", emp_code);

            contentValues.put("dob", "dob");
            contentValues.put("contact", "contact");
            contentValues.put("email", mail_id);
            contentValues.put("add1", "add1");
            contentValues.put("add2", "add2");

            contentValues.put("city", "city");
            contentValues.put("pin", "pin");
            contentValues.put("country", "country");
            contentValues.put("img_url", "img_url");
            contentValues.put("signature_url", "signature_url");
            contentValues.put("status", "status");

            Cursor cursor = null;
            String query = "select * from public_employee";
            cursor = db.rawQuery(query, null);
            db.insert("public_employee", null, contentValues);


            copyDBToPhoneSD1();
            if (db != null)
                db.close();
        } catch (Exception e) {
            Log.e("Exception ", e.toString());
        }
    }


    public ArrayList<String> getEmployee(String mail) {
        db = openDataBase();

        String countQuery = "SELECT * FROM public_employee where email = " + "'" + mail + "'";
        ArrayList<String> arrayList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(countQuery, null);
            count = cursor.getCount();

            if (cursor.moveToNext()) {
                do {

                    arrayList.add(cursor.getString(cursor.getColumnIndexOrThrow("first_name")) + " " + cursor.getString(cursor.getColumnIndexOrThrow("last_name")));
                } while (cursor.moveToNext());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (db != null)
                db.close();

            if (cursor != null)
                cursor.close();
        }
        return arrayList;
    }


    public ArrayList<String> getLogin() {
        db = openDataBase();

        ArrayList<String> arrayList = new ArrayList<>();

        String countQuery = "SELECT * FROM public_login";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(countQuery, null);
            count = cursor.getCount();

            if (cursor.moveToNext()) {
                do {

                    arrayList.add(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                    Log.e("data", cursor.getString(cursor.getColumnIndexOrThrow("email")));

                } while (cursor.moveToNext());
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (db != null)
                db.close();

            if (cursor != null)
                cursor.close();
        }
        return arrayList;
    }


    public ArrayList<String> getPassword() {
        db = openDataBase();

        ArrayList<String> arrayList = new ArrayList<>();

        String countQuery = "SELECT * FROM public_login";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(countQuery, null);
            count = cursor.getCount();

            if (cursor.moveToNext()) {
                do {

                    arrayList.add(cursor.getString(cursor.getColumnIndexOrThrow("password")));

                } while (cursor.moveToNext());
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (db != null)
                db.close();

            if (cursor != null)
                cursor.close();
        }
        return arrayList;
    }
/*.........................Incident Data...............................................*/

    public void insertIncident(String apiname, String email, double lat, double lang, String remark, String severity, String shift) {
        try {
            db = openDataBase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("email", email);
            contentValues.put("apiname", apiname);
            contentValues.put("lat", lat);
            contentValues.put("lang", lang);
            contentValues.put("remark", remark);
            contentValues.put("formattedDate", severity);
            contentValues.put("formattedTime", shift);


            Cursor cursor = null;
            String query = "select * from upload_pic";
            cursor = db.rawQuery(query, null);
            db.insert("upload_pic", null, contentValues);


            copyDBToPhoneSD1();
            if (db != null)
                db.close();
        } catch (Exception e) {
            Log.e("Exception ", e.toString());
        }
    }


    public ArrayList<DbPojo> getInsertdata(String apiname) {


        db = openDataBase();

        ArrayList<DbPojo> arrayList = new ArrayList<>();
        DbPojo cartClass;
        String countQuery = "SELECT * FROM upload_pic where apiname = " + "'" + apiname + "'";
        int cnt = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(countQuery, null);
            cnt = cursor.getCount();

            if (cursor.moveToNext()) {
                do {
                    cartClass = new DbPojo();
                    cartClass.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                    cartClass.setLang(cursor.getString(cursor.getColumnIndexOrThrow("lang")));
                    cartClass.setFormattedDate(cursor.getString(cursor.getColumnIndexOrThrow("formattedDate")));
                    cartClass.setLat(cursor.getString(cursor.getColumnIndexOrThrow("lat")));
                    cartClass.setFormattedTime(cursor.getString(cursor.getColumnIndexOrThrow("formattedTime")));
                    cartClass.setRemark(cursor.getString(cursor.getColumnIndexOrThrow("remark")));

                    arrayList.add(cartClass);

                } while (cursor.moveToNext());
            }


        } catch (Exception ex) {

        } finally {
            if (db != null)
                db.close();

            if (cursor != null)
                cursor.close();
        }
        return arrayList;
    }


    public ArrayList<DbPojo> getAlldata() {


        db = openDataBase();

        ArrayList<DbPojo> arrayList = new ArrayList<>();
        DbPojo cartClass;
        String countQuery = "SELECT * FROM upload_pic";
        int cnt = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(countQuery, null);
            cnt = cursor.getCount();

            if (cursor.moveToNext()) {
                do {
                    cartClass = new DbPojo();
                    cartClass.setApiname(cursor.getString(cursor.getColumnIndexOrThrow("apiname")));
                    cartClass.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                    cartClass.setLang(cursor.getString(cursor.getColumnIndexOrThrow("lang")));
                    cartClass.setFormattedDate(cursor.getString(cursor.getColumnIndexOrThrow("formattedDate")));
                    cartClass.setLat(cursor.getString(cursor.getColumnIndexOrThrow("lat")));
                    cartClass.setFormattedTime(cursor.getString(cursor.getColumnIndexOrThrow("formattedTime")));
                    cartClass.setRemark(cursor.getString(cursor.getColumnIndexOrThrow("remark")));

                    arrayList.add(cartClass);

                } while (cursor.moveToNext());
            }


        } catch (Exception ex) {

        } finally {
            if (db != null)
                db.close();

            if (cursor != null)
                cursor.close();
        }
        return arrayList;
    }


    public void deleteRecords(String time) {

        try {
            db = openDataBase();
            db.execSQL("DELETE FROM upload_pic WHERE formattedTime = " + "'" + time + "'");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (db != null)
                db.close();


        }
    }


    public void deleteAPi(String apiname) {

        try {
            db = openDataBase();
            db.execSQL("DELETE FROM upload_pic WHERE apiname = " + "'" + apiname + "'");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (db != null)
                db.close();


        }
    }


    public void insertEvent(int event_id, String severity, String remarks, String datetime, String type, String status, String latitude, String source, String longitude) {
        try {
            db = openDataBase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("event_id", event_id);
            contentValues.put("severity", severity);
            contentValues.put("remarks", remarks);
            contentValues.put("datetime", datetime);
            contentValues.put("type", type);
            contentValues.put("status", status);
            contentValues.put("source", source);
            contentValues.put("latitude", latitude);
            contentValues.put("longitude", longitude);

            Cursor cursor = null;
            String query = "select * from event_tab";
            cursor = db.rawQuery(query, null);
            db.insert("event_tab", null, contentValues);


            copyDBToPhoneSD1();
            if (db != null)
                db.close();
        } catch (Exception e) {
            Log.e("Exception ", e.toString());
        }
    }


    public void Events() {

        try {
            db = openDataBase();
            db.execSQL("DELETE FROM event_tab");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (db != null)
                db.close();


        }
    }
    //...............................Product Table.............................


    public ArrayList<ViewEventPojo> getAllEventdata() {


        db = openDataBase();

        ArrayList<ViewEventPojo> arrayList = new ArrayList<>();
        ViewEventPojo eventPojo;
        String countQuery = "SELECT * FROM event_tab";
        int cnt = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(countQuery, null);
            cnt = cursor.getCount();

            if (cursor.moveToNext()) {
                do {
                    eventPojo = new ViewEventPojo();
                    eventPojo.setEventId(cursor.getInt(cursor.getColumnIndexOrThrow("event_id")));
                    eventPojo.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
                    eventPojo.setSeverity(cursor.getString(cursor.getColumnIndexOrThrow("severity")));
                    eventPojo.setRemarks(cursor.getString(cursor.getColumnIndexOrThrow("remarks")));
                    eventPojo.setDatetime(cursor.getString(cursor.getColumnIndexOrThrow("datetime")));
                    eventPojo.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                    eventPojo.setSource(cursor.getString(cursor.getColumnIndexOrThrow("source")));
                    eventPojo.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("latitude"))));
                    eventPojo.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("longitude"))));

                    arrayList.add(eventPojo);

                } while (cursor.moveToNext());
            }


        } catch (Exception ex) {

        } finally {
            if (db != null)
                db.close();

            if (cursor != null)
                cursor.close();
        }
        return arrayList;
    }


}
