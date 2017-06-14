package morgantech.com.gms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import morgantech.com.gms.Utils.Prefs;

public class InfoScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_screen);

        TextView txtbody = (TextView) findViewById(R.id.txtbody);

        String first = " Guard";
        String last = " powered by World Security is a simple " +
                "and easy to use application to manage security\n" +
                "operations. It allows to stay connected with\n" +
                "your guards all the time, track location and\n" +
                "capture incidents in real time. It’s interactive\n" +
                "system is a user friendly platform that\n" +
                "connects field staff to management and\n" +
                "customers via multiple tools, smart reports and dashboards." + "<br/>" + "<br/>" + "Start to ";
        String third = " today! \n The solution is powered by World Security.\n The entire work is the copyright of World Security.\n Copying or reproducing this solution in any form without prior consent of the company is strictly prohibited" + "<br/>" + "<br/>" + "For more information on this product";

        String next = "<font color='#019EEd'>IT</font>";

        txtbody.setText(Html.fromHtml(first + next + last + first + next + third));

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        TextView textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setClickable(true);
        textView1.setMovementMethod(LinkMovementMethod.getInstance());

        ((LinearLayout) findViewById(R.id.ll_schedule)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InfoScreen.this, ScheduleActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_events)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(InfoScreen.this, ViewEvent.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(InfoScreen.this, AppSetting.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_about)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(InfoScreen.this, InfoScreen.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

        ((LinearLayout) findViewById(R.id.ll_home)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InfoScreen.this, Home.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });

       /* String txt = "<html><body>"
                         + "<p align=\"justify\">"
                         + getString(R.string.textviewdata)
                         + "</p> "
                        + "</body></html>";*/

        // txtbody.setText(Html.fromHtml(txt));

        Prefs prefs = new Prefs();
        prefs.setPreferencesString(InfoScreen.this, "login", "App");

        String text = "<a href='http://www.guardit.ae'>www.guardit.ae</a>";
        textView.setText(Html.fromHtml(text));
        String text1 = "<a href='http://www.worldsecurity.ae'>www.worldsecurity.ae</a>";
        textView1.setText(Html.fromHtml(text1));


        ((ImageView) findViewById(R.id.iv_img)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InfoScreen.this, Home.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
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
