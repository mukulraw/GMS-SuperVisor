package morgantech.com.gms.Pojo;

/**
 * Created by Administrator on 18-01-2017.
 */

public class MessageListPojo {
    private Integer id;
    private Integer from_id;
    private Integer to_id;
    private Integer incidentId;
    private String subject;
    private String content;
    private String date_time;
    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromId() {
        return from_id;
    }

    public void setFromId(Integer fromId) {
        this.from_id = fromId;
    }

    public Integer getToId() {
        return to_id;
    }

    public void setToId(Integer toId) {
        this.to_id = toId;
    }

    public Integer getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(Integer incidentId) {
        this.incidentId = incidentId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateTime() {
        return date_time;
    }

    public void setDateTime(String dateTime) {
        this.date_time = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



}
