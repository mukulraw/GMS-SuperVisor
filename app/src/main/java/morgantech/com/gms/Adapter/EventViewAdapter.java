package morgantech.com.gms.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import morgantech.com.gms.MapActivity;
import morgantech.com.gms.Pojo.ViewEventPojo;
import morgantech.com.gms.R;

/**
 * Created by CAN on 12/27/2016.
 */

public class EventViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    View view;
    Context context;

    List<ViewEventPojo> viewEventPojos;

    public EventViewAdapter(Context context, List<ViewEventPojo> yourList) {
        this.context = context;
        this.viewEventPojos = yourList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.event_row, parent, false);
        EventViewAdapter.DataObjectHolder dataObjectHolder = new EventViewAdapter.DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mholder, final int position) {
        EventViewAdapter.DataObjectHolder holder = (EventViewAdapter.DataObjectHolder) mholder;
        holder.tv_event.setText(viewEventPojos.get(position).getType());
        holder.tv_severty.setText(viewEventPojos.get(position).getSeverity());
        holder.tv_loc.setText(viewEventPojos.get(position).getRemarks());
        holder.tv_time.setText(viewEventPojos.get(position).getDatetime().toString());

        if (viewEventPojos.get(position).getStatus().equals("Offline")) {
            holder.tv_status.setImageResource(R.drawable.circle_red);

        } else {
            holder.tv_status.setImageResource(R.drawable.circle_green);
        }

        holder.tv_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, MapActivity.class);
                intent.putExtra("lat", String.valueOf(viewEventPojos.get(position).getLatitude().toString()));
                intent.putExtra("lng", String.valueOf(viewEventPojos.get(position).getLongitude().toString()));
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return viewEventPojos.size();
    }


    public class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView tv_event, tv_severty, tv_loc, tv_time;
        ImageView tv_status, tv_view;

        public DataObjectHolder(final View itemView) {
            super(itemView);
            tv_event = (TextView) itemView.findViewById(R.id.tv_event);
            tv_severty = (TextView) itemView.findViewById(R.id.tv_severty);
            tv_loc = (TextView) itemView.findViewById(R.id.tv_loc);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_status = (ImageView) itemView.findViewById(R.id.tv_status);
            tv_view = (ImageView) itemView.findViewById(R.id.tv_view);
        }
    }
}
