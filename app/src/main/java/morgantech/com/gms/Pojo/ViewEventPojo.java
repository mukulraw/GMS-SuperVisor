package morgantech.com.gms.Pojo;

/**
 * Created by Administrator on 16-01-2017.
 */

public class ViewEventPojo {

    private Integer event_id;
    private String type;
    private String severity;
    private String remarks;
    private String datetime;
    private String status;
    private String source;
    private Double latitude;
    private Double longitude;

    public Integer getEventId() {
        return event_id;
    }

    public void setEventId(Integer eventId) {
        this.event_id = eventId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }



}