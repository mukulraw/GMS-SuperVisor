package morgantech.com.gms;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import morgantech.com.gms.Adapter.MessageParentAdapter;
import morgantech.com.gms.Pojo.MessageParentPojo;
import morgantech.com.gms.Utils.Constraints;
import morgantech.com.gms.Utils.Prefs;
import morgantech.com.gms.WebServices.API_Interface;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class Messaging extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        recyclerView = (RecyclerView) findViewById(R.id.rv_main_msg);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait!");
        progressDialog.show();


        ((LinearLayout) findViewById(R.id.ll_schedule)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Messaging.this, ScheduleActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_events)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Messaging.this, ViewEvent.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Messaging.this, AppSetting.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_about)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Messaging.this, InfoScreen.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_home)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Messaging.this, Home.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((ImageView) findViewById(R.id.iv_img)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Messaging.this, Home.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });

        getEventList();

    }

    private void getEventList() {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);
        Prefs prefs = new Prefs();
        prefs.setPreferencesString(Messaging.this, "login", "App");

        apiInterface.getMessageList(prefs.getPreferencesString(Messaging.this, "mail_id"), new Callback<JsonArray>() {
            @Override
            public void success(JsonArray buddypojo, Response response) {

                String jsonString = buddypojo.toString();

                Type listType = new TypeToken<List<MessageParentPojo>>() {
                }.getType();
                List<MessageParentPojo> yourList = new Gson().fromJson(jsonString, listType);
                Collections.reverse(yourList);

                recyclerView.setAdapter(new MessageParentAdapter(Messaging.this, yourList));
                progressDialog.dismiss();

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                Toast.makeText(Messaging.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();

    }
}
