
package morgantech.com.gms.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import morgantech.com.gms.ImageScan;
import morgantech.com.gms.Pojo.ScheduleChildPojo;
import morgantech.com.gms.QrScan;
import morgantech.com.gms.R;
import morgantech.com.gms.ScanGuard;
import morgantech.com.gms.Utils.Prefs;

/**
 * Created by CAN on 12/24/2016.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    View view;
    Context context;
    List<ScheduleChildPojo> empList;
    int dateval;
    Prefs prefs;
    // String dataArray[] = {"Loream Ipsum", "Loream Ipsum", "Loream Ipsum", "Loream Ipsum", "Loream Ipsum", "Loream Ipsum", "Loream Ipsum", "Loream Ipsum", "Loream Ipsum"};

    public ScheduleAdapter(Context context, List<ScheduleChildPojo> empList, int dateval) {
        this.context = context;
        this.empList = empList;
        this.dateval = dateval;
        prefs = new Prefs();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(context).inflate(R.layout.row_layout, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mholder, final int position) {

        DataObjectHolder holder = (DataObjectHolder) mholder;
        holder.textItemName.setText(empList.get(position).getFirst() + " " + empList.get(position).getLast()+"  ("+empList.get(position).getEmpCode().trim()+")");
        holder.textItemRole.setText(empList.get(position).getRole());

        if (empList.get(position).getTodayAttendance().size() > 0) {
            holder.iv_icon.setImageResource(R.drawable.circle_green);
        }
        holder.ll_img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (empList.get(position).getTodayAttendance().size() == 0) {
                    if (prefs.getPreferencesString(context, "scan").equals("nfc")) {
                        context.startActivity(new Intent(context, ScanGuard.class));
                    } else if (prefs.getPreferencesString(context, "scan").equals("face")) {
                        context.startActivity(new Intent(context, ImageScan.class));
                    } else {
                        context.startActivity(new Intent(context, QrScan.class));
                    }

                }
            }
        });

    }


    public class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView textItemName, textItemRole;
        ImageView iv_icon;
        LinearLayout ll_img2;


        public DataObjectHolder(final View itemView) {
            super(itemView);
            textItemName = (TextView) itemView.findViewById(R.id.textItemName);
            textItemRole = (TextView) itemView.findViewById(R.id.textItemRole);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            ll_img2 = (LinearLayout) itemView.findViewById(R.id.ll_img2);


        }
    }


    @Override
    public int getItemCount() {
        return empList.size(); //+1 is for the footer as it's an extra item
    }


}