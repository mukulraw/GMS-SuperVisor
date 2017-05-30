package morgantech.com.gms.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import morgantech.com.gms.MessagingList;
import morgantech.com.gms.Pojo.MessageParentPojo;
import morgantech.com.gms.R;


public class MessageParentAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    View view;
    Context context;

    List<MessageParentPojo> msgparentPojos=new ArrayList<>();
    public MessageParentAdapter(Context context, List<MessageParentPojo> yourList) {
        this.context=context;
        this.msgparentPojos=yourList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.row_msg, parent, false);
        MessageParentAdapter.DataObjectHolder dataObjectHolder = new MessageParentAdapter.DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mholder, final int position) {
        MessageParentAdapter.DataObjectHolder holder = (MessageParentAdapter.DataObjectHolder) mholder;
        holder.tv_msghead.setText(msgparentPojos.get(position).getSubject());
        holder.tv_time_date.setText(msgparentPojos.get(position).getDateTime());
        holder.tv_content.setText(msgparentPojos.get(position).getContent());


        ImageLoader loader = ImageLoader.getInstance();


        loader.displayImage("http://115.118.242.137:5000/GuardIT-RWS/rest/myresource/getincidentLogo?inci_type=" + msgparentPojos.get(position).getLogoType() , holder.img);



        holder.ll_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, MessagingList.class);
                in.putExtra("incident_id",msgparentPojos.get(position).getIncidentId().toString());
                in.putExtra("subject",msgparentPojos.get(position).getSubject());
                context.startActivity(in);
            }
        });


    }

    @Override
    public int getItemCount() {
        return msgparentPojos.size();
    }


    public class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView tv_msghead, tv_time_date,tv_content;
        ImageView img;
        LinearLayout ll_parent;

        public DataObjectHolder(final View itemView) {
            super(itemView);
            tv_msghead = (TextView) itemView.findViewById(R.id.tv_msghead);
            tv_time_date = (TextView) itemView.findViewById(R.id.tv_time_date);
            tv_content= (TextView) itemView.findViewById(R.id.tv_content);
            ll_parent=(LinearLayout)itemView.findViewById(R.id.ll_parent);
            img=(ImageView) itemView.findViewById(R.id.img);

        }
    }
}

