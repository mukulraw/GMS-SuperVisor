package morgantech.com.gms.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 11-01-2017.
 */

public class HomePojo {

    @SerializedName("emp_id")
    @Expose
    private String empId;
    @SerializedName("login_id")
    @Expose
    private String loginId;
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
    @SerializedName("emp_code")
    @Expose
    private String empCode;
    @SerializedName("shift_id")
    @Expose
    private String shiftId;
    @SerializedName("today_attendance")
    @Expose
    private List<Object> todayAttendance = null;

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

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
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public List<Object> getTodayAttendance() {
        return todayAttendance;
    }

    public void setTodayAttendance(List<Object> todayAttendance) {
        this.todayAttendance = todayAttendance;
    }

}