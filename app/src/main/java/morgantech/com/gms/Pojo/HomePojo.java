package morgantech.com.gms.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 11-01-2017.
 */

public class HomePojo {

    @SerializedName("first")
    @Expose
    private String first;
    @SerializedName("last")
    @Expose
    private String last;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("sex")
    @Expose
    private String sex;
    @SerializedName("emp_id")
    @Expose
    private String emp_id;
    @SerializedName("shift_id")
    @Expose
    private String shift_id;
    @SerializedName("today_attendance")
    @Expose
    private List<Object> today_attendance = new ArrayList<>();

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEmpCode() {
        return emp_id;
    }

    public void setEmpCode(String empCode) {
        this.emp_id = empCode;
    }

    public String getShiftId() {
        return shift_id;
    }

    public void setShiftId(String shiftId) {
        this.shift_id = shiftId;
    }

    public List<Object> getTodayAttendance() {
        return today_attendance;
    }

    public void setTodayAttendance(List<Object> todayAttendance) {
        this.today_attendance = todayAttendance;
    }

}