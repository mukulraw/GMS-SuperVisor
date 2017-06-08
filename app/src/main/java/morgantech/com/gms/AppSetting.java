package morgantech.com.gms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import morgantech.com.gms.Utils.Constraints;
import morgantech.com.gms.Utils.Prefs;

public class AppSetting extends AppCompatActivity {

    int vidPicStatus = 0;
    Prefs prefs;
    EditText et_ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_setting);
        Switch s1 = (Switch) findViewById(R.id.s1);
        Switch s2 = (Switch) findViewById(R.id.s2);
        Switch s3 = (Switch) findViewById(R.id.s3);
        Switch s4 = (Switch) findViewById(R.id.s4);
        et_ip = (EditText) findViewById(R.id.et_ip);
        et_ip.clearFocus();
        prefs = new Prefs();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        et_ip.setText(Constraints.Base_Address);


        Button submit = (Button) findViewById(R.id.submit);

        final RadioGroup rg3 = (RadioGroup) findViewById(R.id.rg3);
        final RadioButton video = (RadioButton) findViewById(R.id.video);
        final RadioButton picture = (RadioButton) findViewById(R.id.picture);


        final RadioButton nfc = (RadioButton) findViewById(R.id.nfc);
        final RadioButton face = (RadioButton) findViewById(R.id.face);
        final RadioButton qrscan = (RadioButton) findViewById(R.id.qrscan);


        prefs.setPreferencesString(AppSetting.this, "login", "App");
        if (prefs.getPreferencesString(AppSetting.this, "audio") != null) {
            if (prefs.getPreferencesString(AppSetting.this, "audio").equals("audio")) {

                picture.setChecked(true);
            } else {
                video.setChecked(true);

            }
        }

        if (prefs.getPreferencesString(AppSetting.this, "scan") != null) {
            if (prefs.getPreferencesString(AppSetting.this, "scan").equals("qrscan")) {
                qrscan.setChecked(true);
            } else if (prefs.getPreferencesString(AppSetting.this, "scan").equals("face")) {
                face.setChecked(true);

            } else {
                nfc.setChecked(true);
            }
        }


        ((ImageView) findViewById(R.id.iv_img)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AppSetting.this, Home.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
            }
        });

        s1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(AppSetting.this, "YES IS CHECKED", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AppSetting.this, "NO IS CHECKED", Toast.LENGTH_SHORT).show();
                }
            }
        });

        s2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(AppSetting.this, "YES IS CHECKED", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AppSetting.this, "NO IS CHECKED", Toast.LENGTH_SHORT).show();
                }
            }
        });

        s3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(AppSetting.this, "YES IS CHECKED", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AppSetting.this, "NO IS CHECKED", Toast.LENGTH_SHORT).show();
                }
            }
        });

        s4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(AppSetting.this, "YES IS CHECKED", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AppSetting.this, "NO IS CHECKED", Toast.LENGTH_SHORT).show();
                }
            }
        });



        ((LinearLayout) findViewById(R.id.ll_schedule)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AppSetting.this, ScheduleActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_events)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(AppSetting.this, ViewEvent.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(AppSetting.this, AppSetting.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_about)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(AppSetting.this, InfoScreen.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_home)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AppSetting.this, Home.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                //video picture
                int status = rg3.getCheckedRadioButtonId();
                RadioButton extra = (RadioButton) findViewById(status);
                if (video.isChecked()) {
                    vidPicStatus = 1;
                    prefs.setPreferencesString(AppSetting.this, "audio", "video");
                }
                if (picture.isChecked()) {
                    vidPicStatus = 2;
                    prefs.setPreferencesString(AppSetting.this, "audio", "audio");
                }

                if (qrscan.isChecked()) {
                    prefs.setPreferencesString(AppSetting.this, "scan", "qrscan");
                } else if (face.isChecked()) {
                    prefs.setPreferencesString(AppSetting.this, "scan", "face");
                }
                if (nfc.isChecked()) {
                    prefs.setPreferencesString(AppSetting.this, "scan", "nfc");
                }

                if (!et_ip.getText().toString().trim().matches("")) {
                    Constraints.Base_Address = et_ip.getText().toString().trim();
                    //prefs.setPreferencesString(AppSetting.this, "IP", et_ip.getText().toString().trim());

                    Toast.makeText(AppSetting.this, "Settings Updated", Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(AppSetting.this, Home.class);
                    in.putExtra("vidpic", vidPicStatus);
                    startActivity(in);
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);

                }
                else {

                    Toast.makeText(AppSetting.this, "Invalid IP", Toast.LENGTH_SHORT).show();

                }


            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
