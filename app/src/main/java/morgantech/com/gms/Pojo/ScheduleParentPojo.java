package morgantech.com.gms.Pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14-01-2017.
 */

public class ScheduleParentPojo {

    private String parent_org;
    private String org;
    private String site_name;
    private String shift_time;
    private List<ScheduleChildPojo> emp_list = new ArrayList<>();

    public String getParentOrg() {
        return parent_org;
    }

    public void setParentOrg(String parentOrg) {
        this.parent_org = parentOrg;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getSiteName() {
        return site_name;
    }

    public void setSiteName(String siteName) {
        this.site_name = siteName;
    }

    public String getShiftTime() {
        return shift_time;
    }

    public void setShiftTime(String shiftTime) {
        this.shift_time = shiftTime;
    }

    public List<ScheduleChildPojo> getEmpList() {
        return emp_list;
    }

    public void setEmpList(List<ScheduleChildPojo> empList) {
        this.emp_list = empList;
    }



}