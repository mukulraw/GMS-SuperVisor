package morgantech.com.gms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class activityBean {

    @SerializedName("activity_id")
    @Expose
    private Integer activityId;
    @SerializedName("seq_no")
    @Expose
    private Integer seqNo;
    @SerializedName("activity_name")
    @Expose
    private String activityName;
    @SerializedName("activity_desc")
    @Expose
    private String activityDesc;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("validation_type")
    @Expose
    private String validationType;
    @SerializedName("status")
    @Expose
    private Integer status;

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Integer getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityDesc() {
        return activityDesc;
    }

    public void setActivityDesc(String activityDesc) {
        this.activityDesc = activityDesc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getValidationType() {
        return validationType;
    }

    public void setValidationType(String validationType) {
        this.validationType = validationType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
