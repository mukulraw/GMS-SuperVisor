package morgantech.com.gms;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class incidentListBean {

    @SerializedName("incident_name")
    @Expose
    private List<String> incidentName = null;

    public List<String> getIncidentName() {
        return incidentName;
    }

    public void setIncidentName(List<String> incidentName) {
        this.incidentName = incidentName;
    }

}
