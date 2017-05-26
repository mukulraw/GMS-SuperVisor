package morgantech.com.gms.Pojo;

import java.util.List;

/**
 * Created by Administrator on 12-01-2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScanNFCPojo {


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

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    @SerializedName("emp_id")
    @Expose
    private String emp_id;
    @SerializedName("shift_id")
    @Expose
    private String shiftId;
    @SerializedName("today_attendance")
    @Expose
    private List<String> todayAttendance = null;

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

    public List<String> getTodayAttendance() {
        return todayAttendance;
    }

    public void setTodayAttendance(List<String> todayAttendance) {
        this.todayAttendance = todayAttendance;
    }


}
