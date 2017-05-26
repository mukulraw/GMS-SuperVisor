package morgantech.com.gms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import morgantech.com.gms.Utils.Prefs;

public class Splash extends AppCompatActivity {


    android.os.Handler handler = new android.os.Handler();
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        FirebaseMessaging.getInstance();
//      Log.e("FireBase", FirebaseInstanceId.getInstance().getToken());
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
             //   Log.e("Key: ", key + " Value: " + value);
            }
        }
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                Log.e("Key: ", key + " Value: " + value);
            }
        }


        runnable = new Runnable() {
            @Override
            public void run() {

                Prefs prefs = new Prefs();

                String demo = prefs.getPreferencesString(Splash.this, "USER_STATUS");
                if (demo.equals("FALSE")) {
                    startActivity(new Intent(Splash.this, Home.class));
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    finish();
                } else {

                    startActivity(new Intent(Splash.this, Login.class));
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    finish();

                }


            }
        };
        handler.postDelayed(runnable, 3000);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);

    }
}

