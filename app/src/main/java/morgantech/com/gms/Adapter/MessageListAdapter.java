package morgantech.com.gms.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import morgantech.com.gms.Pojo.MessageListPojo;
import morgantech.com.gms.R;
import morgantech.com.gms.Utils.Prefs;

/**
 * Created by Administrator on 20-01-2017.
 */

public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    View view;
    Context context;
    Prefs prefs;
    List<MessageListPojo> list = new ArrayList<>();

    public MessageListAdapter(Context mainActivity, List<MessageListPojo> yourList) {
        context = mainActivity;
        this.list = yourList;
        prefs = new Prefs();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(context).inflate(R.layout.leftrow_msg, parent, false);
            DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
            return dataObjectHolder;
        }
        if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.messageline_row, parent, false);
            return new FooterViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mholder, int position) {
        if (mholder instanceof DataObjectHolder) {
            DataObjectHolder holder = (DataObjectHolder) mholder;
            holder.tv_attaendence.setText(list.get(position).getContent());
            holder.tv_time.setText(list.get(position).getDateTime());
        } else {

            FooterViewHolder holder = (FooterViewHolder) mholder;
            holder.tv_attaendence.setText(list.get(position).getContent());
            holder.tv_time.setText(list.get(position).getDateTime());
            if (list.get(position).getStatus().equals("read")) {
                holder.iv_stat.setImageResource(R.drawable.read);
            } else if (list.get(position).getStatus().equals("new")) {
                holder.iv_stat.setVisibility(View.INVISIBLE);
            } else {
                holder.iv_stat.setImageResource(R.drawable.delivered);
            }


        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        TextView tv_attaendence, tv_time;
        ImageView iv_stat;

        public FooterViewHolder(View itemView) {
            super(itemView);

            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_attaendence = (TextView) itemView.findViewById(R.id.tv_attaendence);
            iv_stat = (ImageView) itemView.findViewById(R.id.iv_stat);
        }
    }

    public class DataObjectHolder extends RecyclerView.ViewHolder {

        TextView tv_attaendence, tv_time;

        public DataObjectHolder(final View itemView) {
            super(itemView);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_attaendence = (TextView) itemView.findViewById(R.id.tv_attaendence);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (Integer.parseInt(prefs.getPreferencesString(context, "emp_code")) == list.get(position).getFromId()) {

            return TYPE_FOOTER;

        } else {

            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return list.size(); //+1 is for the footer as it's an extra item
    }

}