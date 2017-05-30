package morgantech.com.gms;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import morgantech.com.gms.Pojo.NFCDataPojo;
import morgantech.com.gms.Utils.Constraints;
import morgantech.com.gms.WebServices.API_Interface;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class DataNfc extends AppCompatActivity {

    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;
    private AlertDialog mDialog;
    String serial_no;
    ProgressDialog progressDialog;
    AutoCompleteTextView etLogin;
    ArrayList<String> emp_name;
    EditText etemp_code;
    ArrayList<NFCDataPojo> emp_code;
    TextView tv_code, tv_scan;
    Button btn_scantxt, btn_delete;
    ArrayList<String> empcodes;
    ArrayList<String> uidcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_nfc);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Registering NFC !");
        etLogin = (AutoCompleteTextView) findViewById(R.id.etLogin);
        emp_name = new ArrayList<>();
        emp_code = new ArrayList<>();
        uidcode = new ArrayList<>();
        etemp_code = (EditText) findViewById(R.id.etemp_code);
        tv_code = (TextView) findViewById(R.id.tv_code);
        tv_scan = (TextView) findViewById(R.id.tv_scan);
        btn_scantxt = (Button) findViewById(R.id.btn_scantxt);
        btn_delete = (Button) findViewById(R.id.btn_delete);

        empcodes = new ArrayList<>();

        resolveIntent(getIntent());
        mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null).create();
        mAdapter = NfcAdapter.getDefaultAdapter(this);

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNdefPushMessage = new NdefMessage(new NdefRecord[]{newTextRecord(
                "Message from NFC Reader :-)", Locale.ENGLISH, true)});

        callApi();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DataNfc.this, android.R.layout.simple_list_item_1, emp_name);
        etLogin.setAdapter(adapter);
        etLogin.setThreshold(0);


        etLogin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etemp_code.setText(emp_code.get(emp_name.indexOf(etLogin.getText().toString())).getEmpCode());
                tv_code.setText(emp_code.get(emp_name.indexOf(etLogin.getText().toString())).getUniqueCode());
                if (!tv_code.getText().toString().trim().matches("")) {
                    tv_scan.setText("TAP TO SCAN....");
                }

                if (emp_code.get(emp_name.indexOf(etLogin.getText().toString())).getUniqueCode().matches("")) {
                    ((LinearLayout) findViewById(R.id.ll_error)).setVisibility(View.INVISIBLE);
                    tv_scan.setText("TAP TO SCAN....");
                } else {
                    ((LinearLayout) findViewById(R.id.ll_error)).setVisibility(View.VISIBLE);
                    tv_scan.setText("DELETE OR RESET DATA....");
                }
            }
        });
        //  uidcode


        btn_scantxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(etemp_code.getText().toString().trim().matches("") || tv_code.getText().toString().trim().matches(""))) {
                    if (empcodes.contains(etemp_code.getText().toString().trim())) {

                        hitApi();
                    } else {
                        Toast.makeText(DataNfc.this, "Invalid Employee Code", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DataNfc.this, "Please Fill all Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(etemp_code.getText().toString().trim().matches("") || tv_code.getText().toString().trim().matches(""))) {
                    if (empcodes.contains(etemp_code.getText().toString().trim())) {


                        logoutPopUp("Back");
                    } else {
                        Toast.makeText(DataNfc.this, "Invalid Employee Code", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DataNfc.this, "Please Fill all Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });


        ((Button) findViewById(R.id.btn_restart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_code.setText("");
                etLogin.setText("");
                etemp_code.setText("");
                ((LinearLayout) findViewById(R.id.ll_error)).setVisibility(View.INVISIBLE);
                tv_scan.setText("SELECT EMPLOYEE OR TAP TO SCAN....");
            }
        });


        ((LinearLayout) findViewById(R.id.ll_schedule)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DataNfc.this, ScheduleActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        ((LinearLayout) findViewById(R.id.ll_events)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(DataNfc.this, ViewEvent.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(DataNfc.this, AppSetting.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_about)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(DataNfc.this, InfoScreen.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_home)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DataNfc.this, Home.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        etemp_code.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (empcodes.contains(s.toString())) {
                    tv_code.setText(emp_code.get(empcodes.indexOf(s.toString())).getUniqueCode());
                    etLogin.setText(emp_code.get(empcodes.indexOf(s.toString())).getFirst() + " " + emp_code.get(empcodes.indexOf(s.toString())).getLast());
                    if (emp_code.get(empcodes.indexOf(s.toString())).getUniqueCode().trim().matches("")) {
                        ((LinearLayout) findViewById(R.id.ll_error)).setVisibility(View.INVISIBLE);
                        tv_scan.setText("TAP TO SCAN....");
                    } else {
                        ((LinearLayout) findViewById(R.id.ll_error)).setVisibility(View.VISIBLE);
                        tv_scan.setText("DELETE OR RESET DATA....");
                    }
                } else {
                    ((LinearLayout) findViewById(R.id.ll_error)).setVisibility(View.INVISIBLE);
                    tv_scan.setText("TAP TO SCAN....");
                }

            }
        });

    }

    private void hitdelete() {


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        progressDialog.setTitle("Registering NFC !");
        progressDialog.show();


        apiInterface.sendData("delete", emp_code.get(empcodes.indexOf(etemp_code.getText().toString().trim())).getEmpId(), new Callback<String>() {
            @Override
            public void success(String buddypojo, Response response) {
                tv_code.setText("");
                etLogin.setText("");
                etemp_code.setText("");
                ((LinearLayout) findViewById(R.id.ll_error)).setVisibility(View.INVISIBLE);
                tv_scan.setText("SELECT EMPLOYEE OR TAP TO SCAN....");
                progressDialog.dismiss();
                try {
                    if (buddypojo.equals("0")) {
                        Toast.makeText(DataNfc.this, "Deletion  UnSuccessFul", Toast.LENGTH_SHORT).show();

                    } else if (buddypojo.equals("2")) {
                        Toast.makeText(DataNfc.this, "Delete SuccessFul", Toast.LENGTH_SHORT).show();
                        callApi();
                    } else if (buddypojo.equals("1")) {
                        Toast.makeText(DataNfc.this, "Deletion is UnSuccessFul", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Log.e("Something Went wrong", "!!");
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();

            }
        });
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
                tv_code.setText(serial_no);


                if (uidcode.contains(serial_no)) {
              /*  if (flagscan) {
                    hitApi(serial_no);
                }*/
                    etemp_code.setText(emp_code.get(uidcode.indexOf(serial_no)).getEmpCode());
                    etLogin.setText(emp_code.get(uidcode.indexOf(serial_no)).getFirst() + " " + emp_code.get(uidcode.indexOf(serial_no)).getLast());
                    ((LinearLayout) findViewById(R.id.ll_error)).setVisibility(View.VISIBLE);
                    tv_scan.setText("DELETE OR RESET DATA....");

                } else {
                    if (etLogin.getText().toString().trim().matches("")) {
                        ((LinearLayout) findViewById(R.id.ll_error)).setVisibility(View.INVISIBLE);
                        tv_scan.setText("ENTER NAME....");
                    } else {
                        tv_scan.setText("SAVE DATA....");
                        ((LinearLayout) findViewById(R.id.ll_error)).setVisibility(View.INVISIBLE);
                    }
                }
                //

            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);

    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
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

    private void callApi() {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);

        //  progressDialog.show();


        apiInterface.getNFcDetail(new Callback<JsonArray>() {
            @Override
            public void success(JsonArray buddypojo, Response response) {
                progressDialog.dismiss();
                try {

                    String jsonString = buddypojo.toString();
                    Log.i("onResponse", jsonString);
                    Type listType = new TypeToken<List<NFCDataPojo>>() {
                    }.getType();
                    emp_code = new Gson().fromJson(jsonString, listType);
                    emp_name.clear();
                    empcodes.clear();
                    uidcode.clear();
                    for (int i = 0; i < emp_code.size(); i++) {
                        emp_name.add(emp_code.get(i).getFirst() + " " + emp_code.get(i).getLast());
                    }
                    for (int i = 0; i < emp_code.size(); i++) {
                        empcodes.add(emp_code.get(i).getEmpCode());
                    }
                    for (int i = 0; i < emp_code.size(); i++) {
                        uidcode.add(emp_code.get(i).getUniqueCode());
                    }


                } catch (Exception e) {
                    Log.e("Something Went wrong", "!!");
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();

            }
        });
    }

    private void hitApi() {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);

        progressDialog.show();


        apiInterface.sendData(tv_code.getText().toString(), emp_code.get(empcodes.indexOf(etemp_code.getText().toString().trim())).getEmpId(), new Callback<String>() {
            @Override
            public void success(String buddypojo, Response response) {
                tv_code.setText("");
                etLogin.setText("");
                etemp_code.setText("");
                ((LinearLayout) findViewById(R.id.ll_error)).setVisibility(View.INVISIBLE);
                tv_scan.setText("SELECT EMPLOYEE OR TAP TO SCAN....");
                progressDialog.dismiss();
                try {
                    if (buddypojo.equals("0")) {
                        Toast.makeText(DataNfc.this, "Registration is UnSuccessFul", Toast.LENGTH_SHORT).show();


                    } else if (buddypojo.equals("1")) {
                        Toast.makeText(DataNfc.this, "Registration SuccessFul", Toast.LENGTH_SHORT).show();
                        callApi();

                    } else if (buddypojo.equals("2")) {
                        Toast.makeText(DataNfc.this, "Registration is UnSuccessFul", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Log.e("Something Went wrong", "!!");
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();

            }
        });
    }

    private void logoutPopUp(String data) {
        final Dialog logoutdialog = new Dialog(DataNfc.this);
        logoutdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logoutdialog.setContentView(R.layout.logout_lay);
        logoutdialog.setCancelable(true);


        if (data.equals("Back")) {

            ((TextView) logoutdialog.findViewById(R.id.header)).setText("Delete !!" + "?");
            // ((TextView) logoutdialog.findViewById(R.id.exit)).setText("Are you sure you want to Exit ?");
        } else {

            ((TextView) logoutdialog.findViewById(R.id.header)).setText("Delete" + "?");
            // ((TextView) logoutdialog.findViewById(R.id.exit)).setText("Are you sure you want to logout ?");
        }

        TextView logout = (TextView) logoutdialog.findViewById(R.id.logout);
        logout.setText("Delete");
        TextView cancel = (TextView) logoutdialog.findViewById(R.id.cancel);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitdelete();
                logoutdialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutdialog.dismiss();
            }
        });
        logoutdialog.show();
    }


}
