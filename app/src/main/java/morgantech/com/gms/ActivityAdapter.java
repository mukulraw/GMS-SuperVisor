package morgantech.com.gms;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import morgantech.com.gms.Utils.Constraints;
import morgantech.com.gms.Utils.LocationFinder;
import morgantech.com.gms.Utils.Prefs;
import morgantech.com.gms.WebServices.API_Interface;
import morgantech.com.gms.WebServices.StringConverter;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder>{

    private Context context;
    LocationFinder locationFinder;
    double lat = 0.00;
    double lang = 0.000;
    Prefs prefs;
    Calendar c;
    List<activityBean> list = new ArrayList<>();

    ActivityAdapter(Context context, List<activityBean> list)
    {
        this.context = context;
        this.list = list;
    }

    void setGridData(List<activityBean> list)
    {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_list_model , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final activityBean item = list.get(position);
        c = Calendar.getInstance();
        locationFinder = new LocationFinder(context);

        lat = locationFinder.getLatitude();
        lang = locationFinder.getLongitude();

        prefs = new Prefs();
        prefs.setPreferencesString(context, "login", "App");

        holder.sno.setText(item.getSeqNo());
        holder.activity.setText(item.getLocation());
        holder.type.setText(item.getValidationType());

        if (item.getStatus() == 1)
        {
            holder.check.setBackground(context.getResources().getDrawable(R.drawable.check_red));
        }else if (item.getStatus() == 0)
        {
            holder.check.setBackground(context.getResources().getDrawable(R.drawable.check_green));
        }

        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.info_dialog);

                TextView name = (TextView)dialog.findViewById(R.id.location);
                TextView desc = (TextView)dialog.findViewById(R.id.desc);

                name.setText(item.getActivityName());
                desc.setText(item.getActivityDesc());

                dialog.show();

            }
        });

        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("click" , "Clicked");

                if (item.getValidationType().trim().equals("QR Code"))
                {
                    Log.d("click" , "QR Code");


                    Intent intent = new Intent(context , Qr2.class);
                    intent.putExtra("data" , item.getActivityId());
                    context.startActivity(intent);
                }
                else if (item.getValidationType().trim().equals("NFC"))
                {
                    Log.d("click" , "NFC");

                    Intent intent = new Intent(context , Scan2.class);
                    intent.putExtra("data" , item.getActivityId());
                    context.startActivity(intent);
                }
                else if (item.getValidationType().trim().equals("Check"))
                {

                    Log.d("click" , "Check");

                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.check_dialog);
                    dialog.show();

                    Button ok = (Button)dialog.findViewById(R.id.ok);
                    Button cancel = (Button)dialog.findViewById(R.id.cancel);
                    TextView name = (TextView)dialog.findViewById(R.id.name);

                    name.setText("Confirm completion of " + item.getActivityName());

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialog.dismiss();

                        }
                    });

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            RestAdapter restAdapter = new RestAdapter.Builder()
                                    .setEndpoint("http://" + Constraints.Base_Address + ":5000/GuardIT-RWS/rest/myresource")
                                    .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();
                            API_Interface apiInterface = restAdapter.create(API_Interface.class);

                            apiInterface.validate(item.getActivityId(), "", String.valueOf(lat), String.valueOf(lang), new Callback<Integer>() {
                                @Override
                                public void success(Integer integer, Response response) {

                                    dialog.dismiss();

                                }

                                @Override
                                public void failure(RetrofitError error) {

                                }
                            });


                        }
                    });




                }

            }
        });




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{


        TextView sno , activity , type;
        ImageButton info , check;


        ViewHolder(View itemView) {
            super(itemView);

            sno = (TextView) itemView.findViewById(R.id.sno);
            activity = (TextView)itemView.findViewById(R.id.activity);
            type = (TextView)itemView.findViewById(R.id.type);

            info = (ImageButton) itemView.findViewById(R.id.info);
            check = (ImageButton)itemView.findViewById(R.id.perform);

        }
    }




}
