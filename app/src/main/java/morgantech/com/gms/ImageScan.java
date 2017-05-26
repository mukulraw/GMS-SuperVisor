package morgantech.com.gms;

import android.Manifest;
import android.app.Activity;
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
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import morgantech.com.gms.DbHelper.DbHelper;
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
import retrofit.mime.TypedFile;

public class ImageScan extends AppCompatActivity {

    double lat = 0.00;
    double lang = 0.000;
    LocationFinder locationFinder;
    Prefs prefs;
    ProgressDialog progressDialog;
    TextView textView2;
    Calendar c;
    ImageView iv_stat;
    LinearLayout ll_lower;
    ImageView scanqrcode;
    Context context;
    EditText txt;


    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    Drawable userImage, userBack;
    Bitmap scaledBitmapBk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

        txt = (EditText) findViewById(R.id.txt);
        scanqrcode = (ImageView) findViewById(R.id.iv_nfc);
        scanqrcode.setImageResource(R.drawable.facescan);
        ((TextView) findViewById(R.id.tv_header)).setText("Face Scan");
        ll_lower = (LinearLayout) findViewById(R.id.ll_lower);
        context = this;

        locationFinder = new LocationFinder(this);
        progressDialog = new ProgressDialog(this);
        prefs = new Prefs();
        c = Calendar.getInstance();
        textView2 = (TextView) findViewById(R.id.textView2);
        iv_stat = (ImageView) findViewById(R.id.iv_stat);
        txt.setVisibility(View.VISIBLE);

        prefs.setPreferencesString(ImageScan.this, "login", "App");
        ((ImageView) findViewById(R.id.iv_img)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ImageScan.this, Home.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
            }
        });

        scanqrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txt.getText().toString().trim().matches("")) {
                    Toast.makeText(ImageScan.this, "Add Your Name First", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    selectImage();

                }
            }
        });

        ((LinearLayout) findViewById(R.id.ll_schedule)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ImageScan.this, ScheduleActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_events)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ImageScan.this, ViewEvent.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ImageScan.this, AppSetting.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_about)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ImageScan.this, InfoScreen.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_home)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ImageScan.this, Home.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((Button) findViewById(R.id.btn_scantxt)).setText("Scan Another Face");
        ((Button) findViewById(R.id.btn_scantxt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ll_lower.setVisibility(View.INVISIBLE);
                scanqrcode.setClickable(true);
                scanqrcode.setImageResource(R.drawable.facescan);
                txt.setVisibility(View.VISIBLE);
                txt.setText("");

            }
        });


    }

    public void selectImage() {
        final CharSequence[] item = {"Take a photo", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i == 0)
                    onPickClicked(context);
                else if (item[i].equals("Cancel"))
                    dialog.dismiss();
            }
        });
        builder.show();
    }

    private void storeDb(File file) {
        lat = locationFinder.getLatitude();
        lang = locationFinder.getLongitude();
        DbHelper dbHelper = new DbHelper(this);
        dbHelper.createDb(false);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
        final String formattedDate = df.format(c.getTime());
        final String formattedTime = df1.format(c.getTime());
        dbHelper.insertIncident("ImageScan",String.valueOf(file), lat, lang, prefs.getPreferencesString(ImageScan.this, "emp_code").toString(), formattedDate, formattedTime);

        dbHelper.insertEvent(1,"Severe","Image\nScan",formattedDate+"\n"+formattedTime,"Image\nScan","Offline",String.valueOf(lat),"app",String.valueOf(lang));
        Toast.makeText(ImageScan.this, "Data Saved in Database", Toast.LENGTH_SHORT).show();
        ImageScan.this.finish();
    }

    private void callAPi(File value) {


        lat = locationFinder.getLatitude();
        lang = locationFinder.getLongitude();
        TypedFile typedFile = new TypedFile("multipart/form-data", new File(value.getPath()));

        final SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        final SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");

        final String formattedDate = df.format(c.getTime());
        final String formattedTime = df1.format(c.getTime());

        progressDialog.show();
        RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.uploadScanImage(prefs.getPreferencesString(ImageScan.this, "emp_code").toString(), typedFile,
                String.valueOf(lat), String.valueOf(lang),
                new Callback<String>() {
                    @Override
                    public void success(String buddypojo, Response response) {
                        txt.setVisibility(View.GONE);
                        progressDialog.dismiss();
                        ll_lower.setVisibility(View.VISIBLE);
                        scanqrcode.setClickable(false);
                        //  Toast.makeText(QrScan.this, buddypojo, Toast.LENGTH_SHORT).show();

                        if (buddypojo.equals("0")) {
                            hitEventApi();

                            iv_stat.setImageResource(R.drawable.unnamed);
                            textView2.setText("Name  - " + txt.getText() + "\n" + "Status - Success\nDate - " + formattedDate + "\nTime - " + formattedTime);
                        } else {
                            iv_stat.setImageResource(R.drawable.wrong);
                            textView2.setText("Name  - " + txt.getText() + "\n" + "Status - Success\nDate - " + df + "\nTime - " + df1);
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                        txt.setVisibility(View.GONE);
                        scanqrcode.setClickable(false);
                        ll_lower.setVisibility(View.VISIBLE);
                        iv_stat.setImageResource(R.drawable.wrong);
                        textView2.setText("Name  - " + txt.getText() + "\n" + "Status - Success\nDate - " + df + "\nTime - " + df1);

                    }
                });
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
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.getReportEvent(prefs.getPreferencesString(ImageScan.this, "emp_code").toString(),
                formattedDate, formattedTime, "Image Scan", "Image Scan", "high", "open", "app",
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


    /*....................import image from galary..........................*/
    public void onPickClicked(Context context) {
        // EasyImage.openGallery(this, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(ImageScan.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                return;
            }
            case 2900: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");

                    Intent captureIntent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    // we will handle the returned data in onActivityResult
                    startActivityForResult(captureIntent, PICK_FROM_CAMERA);
                } else {
                    Log.e("Permission", "Denied");
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
                    File myDir = new File(root + "/canbrand_images");
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
                            scanqrcode.setImageDrawable(userBack);

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
                            if (Helper.checkInternetConnection(this)) {
                                callAPi(file);
                            } else {
                                storeDb(file);
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }

            } else if (requestCode == PICK_FROM_CAMERA) {
                Bundle extras = data.getExtras();

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");

                    String root = Environment.getExternalStorageDirectory()
                            .toString();
                    File myDir = new File(root + "/morgan");

                    Random generator = new Random();
                    int n = 10000;
                    n = generator.nextInt(n);
                    String image_name = "Imagescan-" + n + ".jpg";
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
                    scanqrcode.setImageDrawable(userImage);
                    if (Helper.checkInternetConnection(this)) {
                        callAPi(file);
                    } else {
                        storeDb(file);
                    }
                    Bitmap scaledBitmapBk = ScalingUtilities.createScaledBitmap(
                            photo, 900, 900, ScalingUtilities.ScalingLogic.CROP);
                    ByteArrayOutputStream streamBk = new ByteArrayOutputStream();
                    scaledBitmapBk.compress(Bitmap.CompressFormat.JPEG, 100,
                            streamBk);

                }
            }
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);

    }

}
