package morgantech.com.gms.Pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 11-01-2017.
 */

public class ShiftDetailPojo {

    private String store_name;
    private String address;
    private String time_from;
    private String time_to;
    private String date_from;
    private String date_to;
    private Integer contract_id;
    private Integer site_id;
    private List<Object> empdetails_list = new ArrayList<>();
    private List<Object> emp_id_list = new ArrayList<>();

    public String getStoreName() {
        return store_name;
    }

    public void setStoreName(String storeName) {
        this.store_name = storeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTimeFrom() {
        return time_from;
    }

    public void setTimeFrom(String timeFrom) {
        this.time_from = timeFrom;
    }

    public String getTimeTo() {
        return time_to;
    }

    public void setTimeTo(String timeTo) {
        this.time_to = timeTo;
    }

    public String getDateFrom() {
        return date_from;
    }

    public void setDateFrom(String dateFrom) {
        this.date_from = dateFrom;
    }

    public String getDateTo() {
        return date_to;
    }

    public void setDateTo(String dateTo) {
        this.date_to = dateTo;
    }

    public Integer getContractId() {
        return contract_id;
    }

    public void setContractId(Integer contractId) {
        this.contract_id = contractId;
    }

    public Integer getSiteId() {
        return site_id;
    }

    public void setSiteId(Integer siteId) {
        this.site_id = siteId;
    }

    public List<Object> getEmpdetailsList() {
        return empdetails_list;
    }

    public void setEmpdetailsList(List<Object> empdetailsList) {
        this.empdetails_list = empdetailsList;
    }

    public List<Object> getEmpIdList() {
        return emp_id_list;
    }

    public void setEmpIdList(List<Object> empIdList) {
        this.emp_id_list = empIdList;
    }


}
