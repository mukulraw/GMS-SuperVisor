package morgantech.com.gms;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
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
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import morgantech.com.gms.DbHelper.DbHelper;
import morgantech.com.gms.Pojo.ScanNFCPojo;
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


public class ScanGuard extends AppCompatActivity {
    private LinearLayout ll_inflate, ll_lower, ll_user;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;
    private AlertDialog mDialog;
    String serial_no;
    double lat = 0.00;
    double lang = 0.000;
    LocationFinder locationFinder;
    private TextView tv_name, tv_role, tv_sex, tv_empcode, textView2, tv_scan;
    ProgressDialog progressDialog;
    ImageView iv_profimg;
    boolean flagscan = true;
    Prefs prefs;
    Calendar c;
    HorizontalScrollView horiz;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_guard);
        prefs = new Prefs();
        c = Calendar.getInstance();

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_role = (TextView) findViewById(R.id.tv_role);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_empcode = (TextView) findViewById(R.id.tv_empcode);
        ll_inflate = (LinearLayout) findViewById(R.id.ll_inflate);
        ll_lower = (LinearLayout) findViewById(R.id.ll_lower);
        iv_profimg = (ImageView) findViewById(R.id.iv_profimg);
        textView2 = (TextView) findViewById(R.id.textView2);
        ll_user = (LinearLayout) findViewById(R.id.ll_user);
        tv_scan = (TextView) findViewById(R.id.tv_scan);
        horiz = (HorizontalScrollView) findViewById(R.id.horiz);
        locationFinder = new LocationFinder(this);

        ((ImageView) findViewById(R.id.iv_img)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScanGuard.this, Home.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });

        if (locationFinder != null) {
          /*  lat = locationFinder.getLocation().getLatitude();
            lang = locationFinder.getLocation().getLongitude();*/
        }
       /* String root = Environment.getExternalStorageDirectory()
                .toString();
        File myDir = new File("file:///" + root + "/morgan");
        Uri myUri1 = Uri.parse(myDir + "/image.png");
        Log.e("Photo", myUri1.toString());
        Picasso.with(ScanGuard.this).
                load(myUri1)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)*/
        //  .into(iv_profimg);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");


        ((LinearLayout) findViewById(R.id.ll_schedule)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScanGuard.this, ScheduleActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_home)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScanGuard.this, Home.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_events)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ScanGuard.this, ViewEvent.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ScanGuard.this, AppSetting.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_about)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ScanGuard.this, InfoScreen.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });


        resolveIntent(getIntent());
        mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null).create();
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            showMessage("error", "You Dont Have NFC in Your Mobile ");
            finish();
            return;
        }
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNdefPushMessage = new NdefMessage(new NdefRecord[]{newTextRecord(
                "Message from NFC Reader :-)", Locale.ENGLISH, true)});


        ((Button) findViewById(R.id.btn_scantxt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_lower.setVisibility(View.INVISIBLE);
                tv_scan.setText("TAP TO SCAN");
                flagscan = true;

            }
        });
    }

    private void showMessage(String title, String message) {
        mDialog.setTitle(title);
        mDialog.setMessage(message);
        mDialog.show();
    }

    private NdefRecord newTextRecord(String text, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            if (!mAdapter.isEnabled()) {
                showWirelessSettingsDialog();
            }
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            mAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
            mAdapter.disableForegroundNdefPush(this);
        }
    }

    private void showWirelessSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("NFC is Disabled");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
        return;
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
                msgs = new NdefMessage[]{msg};
                serial_no = ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));
                Log.e("EXTRA_ID", serial_no);

                if (flagscan) {

                    if (Helper.checkInternetConnection(this)) {
                        hitApi(serial_no);
                        tv_scan.setText("SCAN NEXT TAG");
                    } else {
                        storeDb(serial_no);
                    }

                }
            }
            // Setup the views
            //buildTagViews(msgs);
        }
    }


    private String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }


    private String dumpTagData(Parcelable p) {
        StringBuilder sb = new StringBuilder();
        Tag tag = (Tag) p;
        byte[] id = tag.getId();
        sb.append("Tag ID (hex): ").append(getHex(id)).append("\n");
        sb.append("Tag ID (dec): ").append(getDec(id)).append("\n");
        sb.append("ID (reversed): ").append(getReversed(id)).append("\n");

        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                MifareClassic mifareTag = MifareClassic.get(tag);
                String type = "Unknown";
                switch (mifareTag.getType()) {
                    case MifareClassic.TYPE_CLASSIC:
                        type = "Classic";
                        break;
                    case MifareClassic.TYPE_PLUS:
                        type = "Plus";
                        break;
                    case MifareClassic.TYPE_PRO:
                        type = "Pro";
                        break;
                }
                sb.append("Mifare Classic type: ");
                sb.append(type);
                sb.append('\n');

                sb.append("Mifare size: ");
                sb.append(mifareTag.getSize() + " bytes");
                sb.append('\n');

                sb.append("Mifare sectors: ");
                sb.append(mifareTag.getSectorCount());
                sb.append('\n');

                sb.append("Mifare blocks: ");
                sb.append(mifareTag.getBlockCount());
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }

        return sb.toString();
    }

    private String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long getReversed(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

  /*  void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout content = mTagContent;

        // Parse the first message in the list
        // Build views for all of the sub records
        Date now = new Date();
        List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
        final int size = records.size();
        for (int i = 0; i < size; i++) {
            TextView timeView = new TextView(this);
            timeView.setText(TIME_FORMAT.format(now));
            content.addView(timeView, 0);
            ParsedNdefRecord record = records.get(i);
            content.addView(record.getView(this, inflater, content, i), 1 + i);
            content.addView(inflater.inflate(R.layout.tag_divider, content, false), 2 + i);
        }
    }

*/


    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }

    private void storeDb(String serial_no) {
        lat = locationFinder.getLatitude();
        lang = locationFinder.getLongitude();
        DbHelper dbHelper = new DbHelper(this);
        dbHelper.createDb(false);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
        final String formattedDate = df.format(c.getTime());
        final String formattedTime = df1.format(c.getTime());

        // String apiname,String file, double lat, double lang, String emp_code, String formattedDate, String formattedTime

        dbHelper.insertIncident("NFCScan", serial_no, lat, lang, prefs.getPreferencesString(ScanGuard.this, "shift_id").toString(), formattedDate + prefs.getPreferencesString(ScanGuard.this, "emp_code").toString(), formattedTime);

        dbHelper.insertEvent(1, "Severe", "NFC\nScan", formattedDate + "\n" + formattedTime, "NFC\nScan", "Offline", String.valueOf(lat), "app", String.valueOf(lang));

        dbHelper.getAllEventdata();

        Toast.makeText(ScanGuard.this, "Data Saved in Database", Toast.LENGTH_SHORT).show();
        ScanGuard.this.finish();

    }


    private void hitApi(String serial_no) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(" http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource/")
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        iv_profimg.setVisibility(View.INVISIBLE);


        progressDialog.show();

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");

        String formattedDate = df.format(c.getTime());
        String formattedTime = df1.format(c.getTime());


        lat = locationFinder.getLatitude();
        lang = locationFinder.getLongitude();


        apiInterface.getNFCScan(serial_no, formattedDate, formattedTime, String.valueOf(lat), String.valueOf(lang), prefs.getPreferencesString(ScanGuard.this, "shift_id").toString(), new Callback<ScanNFCPojo>() {
            @Override
            public void success(ScanNFCPojo buddypojo, Response response) {
                progressDialog.dismiss();
                try {
                    flagscan = false;
                    horiz.setVisibility(View.INVISIBLE);
                    if (!prefs.getPreferencesString(ScanGuard.this, "shift_id").equals(buddypojo.getShiftId())) {
                        ll_lower.setVisibility(View.VISIBLE);
                        Toast.makeText(ScanGuard.this, " Scheduled Resource", Toast.LENGTH_SHORT);
                        ((ImageView) findViewById(R.id.iv_stat)).setImageResource(R.drawable.circle_red);
                        new DownloadMusicfromInternet().execute("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource" + "/profilepic?emp_id=" + buddypojo.getEmp_id());
                        ll_lower.setVisibility(View.VISIBLE);
                        textView2.setText("TAG Validated\nUnscheduled Resource");
                        tv_scan.setText("NOT SCHEDULED FOR DUTY");
                        ll_user.setVisibility(View.VISIBLE);
                        tv_name.setText(buddypojo.getFirst() + " " + buddypojo.getLast());
                        tv_role.setText(buddypojo.getRole());
                        tv_sex.setText(buddypojo.getSex());
                        tv_empcode.setText(buddypojo.getEmpCode());
                        ll_inflate.removeAllViews();
                    } else {
                        horiz.setVisibility(View.VISIBLE);
                        ll_lower.setVisibility(View.VISIBLE);
                        textView2.setText("TAG \nValidated");
                        ll_user.setVisibility(View.VISIBLE);
                        tv_name.setText(buddypojo.getFirst() + " " + buddypojo.getLast());
                        tv_role.setText(buddypojo.getRole());
                        tv_sex.setText(buddypojo.getSex());
                        tv_empcode.setText(buddypojo.getEmpCode());
                        hitEventApi();
                        ((ImageView) findViewById(R.id.iv_stat)).setImageResource(R.drawable.circle_green);
                        new DownloadMusicfromInternet().execute("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource/profilepic?emp_id=" + buddypojo.getEmp_id());
                        if (!buddypojo.getTodayAttendance().isEmpty()) {

                            ll_inflate.removeAllViews();

                            for (int i = 0; i < buddypojo.getTodayAttendance().size(); i++) {
                                LinearLayout layoutInflater = (LinearLayout) ScanGuard.this.getLayoutInflater().inflate(R.layout.rowinflater, null);
                                ((TextView) layoutInflater.findViewById(R.id.tv_attaendence)).setText(buddypojo.getTodayAttendance().get(i));
                                ll_inflate.addView(layoutInflater);
                                tv_scan.setText("SCAN SUCCESSFULL");
                            }

                        }

                    }

                } catch (Exception e) {
                    flagscan = false;
                    Log.e("Something Went wrong", "!!");
                    ll_lower.setVisibility(View.VISIBLE);
                    iv_profimg.setVisibility(View.VISIBLE);
                    horiz.setVisibility(View.GONE);
                    ll_inflate.removeAllViews();
                    Toast.makeText(ScanGuard.this, "Not Scheduled Resource", Toast.LENGTH_SHORT);
                    textView2.setText("TAG not Validated \n Unknown Person");
                    ((ImageView) findViewById(R.id.iv_stat)).setImageResource(R.drawable.circle_red);
                    iv_profimg.setImageResource(R.drawable.unauthorised);
                    ll_user.setVisibility(View.GONE);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                Toast.makeText(ScanGuard.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hitEventApi() {

        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");

        String formattedDate = df.format(c.getTime());
        String formattedTime = df1.format(c.getTime());
        Prefs prefs = new Prefs();
        prefs.setPreferencesString(ScanGuard.this, "login", "App");

        RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                .setEndpoint("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource")
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        apiInterface.getReportEvent(prefs.getPreferencesString(ScanGuard.this, "emp_code").toString(),
                formattedDate, formattedTime, "NFC Scan", "Scan Success", "high", "open", "app",
                String.valueOf(lat), String.valueOf(lang), new Callback<String>() {
                    @Override
                    public void success(String buddypojo, Response response) {


                        progressDialog.dismiss();

                        if (buddypojo.equals("1")) {
                            //   Toast.makeText(ScanGuard.this, "Event Generated", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(ScanGuard.this, "Event Not Generated", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ScanGuard.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                OutputStream output = new FileOutputStream(myDir.getPath() + "/imageg.png");
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
        iv_profimg.setVisibility(View.VISIBLE);

        String root = Environment.getExternalStorageDirectory()
                .toString();
        File myDir = new File("file:///" + root + "/morgan");
        Uri myUri1 = Uri.parse(myDir.getPath() + "/imageg.png");
        Log.e("Photo", myUri1.toString());
        Picasso.with(ScanGuard.this).
                load(myUri1)//.
              /*  placeholder(R.drawable.profile_pic) // optional
                .error(R.drawable.profile_pic)*/
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(iv_profimg);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);

    }
}
