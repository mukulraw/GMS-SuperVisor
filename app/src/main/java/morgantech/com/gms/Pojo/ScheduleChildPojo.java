package morgantech.com.gms.Pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14-01-2017.
 */

public class ScheduleChildPojo {
    private String first;
    private String last;
    private String role;
    private String sex;
    private String emp_code;
    private String shift_id;
    private List<String> today_attendance = new ArrayList<>();

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
        return emp_code;
    }

    public void setEmpCode(String empCode) {
        this.emp_code = empCode;
    }

    public String getShiftId() {
        return shift_id;
    }

    public void setShiftId(String shiftId) {
        this.shift_id = shiftId;
    }

    public List<String> getTodayAttendance() {
        return today_attendance;
    }

    public void setTodayAttendance(List<String> todayAttendance) {
        this.today_attendance = todayAttendance;
    }



}