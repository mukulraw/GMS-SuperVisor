package morgantech.com.gms;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import morgantech.com.gms.Adapter.MessageListAdapter;
import morgantech.com.gms.FCMs.MyFirebaseMessagingService;
import morgantech.com.gms.Interfaces.Updatemsg;
import morgantech.com.gms.Pojo.MessageListPojo;
import morgantech.com.gms.Utils.Constraints;
import morgantech.com.gms.Utils.Prefs;
import morgantech.com.gms.WebServices.API_Interface;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class MessagingList extends AppCompatActivity {

    ProgressDialog progress;
    Prefs prefs;
    EditText msg_send;
    int incedent_id;
    String sub;
    TextView tv_header;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging_list);

        prefs = new Prefs();
        Prefs prefs = new Prefs();
        prefs.setPreferencesString(MessagingList.this, "login", "App");
        msg_send = (EditText) findViewById(R.id.msg_send);
        tv_header = (TextView) findViewById(R.id.tv_header);

        recyclerView = (RecyclerView) findViewById(R.id.rv_chat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progress = new ProgressDialog(this);
        progress.setTitle("Please Wait!");
        // progress.show();

        ((ImageView) findViewById(R.id.iv_img)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessagingList.this, Home.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });

        msg_send.clearFocus();
        hideSoftKeyboard();

        Intent in2 = getIntent();
        incedent_id = Integer.parseInt(in2.getStringExtra("incident_id"));
        sub = in2.getStringExtra("subject");
        tv_header.setText(sub);
        prefs.setPreferencesString(MessagingList.this, "incident_id", String.valueOf(incedent_id));

        getListApi(String.valueOf(incedent_id));

        ((Button) findViewById(R.id.btn_send)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!msg_send.getText().toString().trim().matches("")) {
                    hitSendmsgApi();
                } else {
                    Toast.makeText(MessagingList.this, "Please Write Message", Toast.LENGTH_SHORT).show();
                }
            }
        });

        MyFirebaseMessagingService.bindListener(new Updatemsg() {
            @Override
            public void update() {
                getListApi(String.valueOf(incedent_id));
                Log.e("Data", "data");
            }
        });


    }

    private void hitSendmsgApi() {

        progress.show();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate1 = df1.format(c.getTime());

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);

        Log.e("EMp Code", prefs.getPreferencesString(MessagingList.this, "emp_code") + prefs.getPreferencesString(MessagingList.this, "mail_id"));
        apiInterface.getSendMsg(String.valueOf(incedent_id), prefs.getPreferencesString(MessagingList.this, "emp_code"),
                prefs.getPreferencesString(MessagingList.this, "mail_id")
                , sub, msg_send.getText().toString(), formattedDate1
                , new Callback<JsonArray>() {
                    @Override
                    public void success(final JsonArray buddypojo, Response response) {
                        progress.dismiss();
                        msg_send.setText("");

                        String jsonString = buddypojo.toString();
                        Type listType = new TypeToken<List<MessageListPojo>>() {
                        }.getType();
                        List<MessageListPojo> yourList = new Gson().fromJson(jsonString, listType);

                        recyclerView.setAdapter(new MessageListAdapter(MessagingList.this, yourList));
                        recyclerView.scrollToPosition(yourList.size() - 1);
                        hideSoftKeyboard();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progress.dismiss();
                        Toast.makeText(MessagingList.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void getListApi(String incedent_id) {
        //  progress.show();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constraints.Base_Address)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
        API_Interface apiInterface = restAdapter.create(API_Interface.class);

        apiInterface.getMessage(incedent_id, prefs.getPreferencesString(MessagingList.this, "mail_id"), new Callback<JsonArray>() {
            @Override
            public void success(final JsonArray buddypojo, Response response) {
                // progress.dismiss();
                String jsonString = buddypojo.toString();

                Type listType = new TypeToken<List<MessageListPojo>>() {
                }.getType();
                List<MessageListPojo> yourList = new Gson().fromJson(jsonString, listType);

                recyclerView.setAdapter(new MessageListAdapter(MessagingList.this, yourList));
                recyclerView.scrollToPosition(yourList.size() - 1);

            }

            @Override
            public void failure(RetrofitError error) {
                // progress.dismiss();
                Toast.makeText(MessagingList.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(msg_send.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();

    }


}
