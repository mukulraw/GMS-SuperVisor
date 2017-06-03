package morgantech.com.gms.WebServices;

import com.google.gson.JsonArray;
import com.squareup.okhttp.Call;

import java.util.List;

import morgantech.com.gms.Pojo.HomePojo;
import morgantech.com.gms.Pojo.ScanNFCPojo;
import morgantech.com.gms.Pojo.ScheduleParentPojo;
import morgantech.com.gms.Pojo.ShiftDetailPojo;
import morgantech.com.gms.activityBean;
import morgantech.com.gms.incidentListBean;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;

public interface API_Interface {

    @GET("/login")
    void getLogin(@Query("username") String username, @Query("password") String password, @Query("mobile_id") String mobile_id,
                  Callback<String> callback);

    @GET("/logout")
    void getLogout(@Query("username") String username, Callback<String> callback);


    @GET("/forgotPassword")
    void getPassword(@Query("email_id") String email_id, Callback<String> callback);

    @GET("/isServerUp")
    void getPingpong(Callback<String> callback);


    @GET("/reportEvent")
    void getReportEvent(@Query("emp_id") String emp_id, @Query("date") String date,
                        @Query("time") String time,
                        @Query("type") String type,
                        @Query("remarks") String remarks,
                        @Query("severity") String severity,
                        @Query("status") String status,
                        @Query("source") String source,
                        @Query("latitude") String latitude,
                        @Query("longitude") String longitude, Callback<String> callback);


    @GET("/employeedetails")
    void getProfile(@Query("email_id") String abc, Callback<HomePojo> callback);


    @GET("/readMessage")
    void getMessage(@Query("incident_id") String abc, @Query("email_id") String email_id, Callback<JsonArray> callback);


    @GET("/sendMessage")
    void getSendMsg(@Query("incident_id") String abc, @Query("emp_id") String emp_id,
                    @Query("email_id") String email_id,
                    @Query("subject") String subject,
                    @Query("content") String content,
                    @Query("datetime") String datetime,
                    Callback<JsonArray> callback);

    //  void getSendMsg(String incident_id, String emp_code, String mail_id, String sub, String hii, String formattedDate1);

    @GET("/getshiftDetails")
    void getshiftDetail(@Query("shift_id") String abc, Callback<ShiftDetailPojo> callback);


    @GET("/viewEvent")
    void getEventDetail(@Query("emp_id") String abc, @Query("date") String date, Callback<JsonArray> callback);

    @GET("/getmessageList")
    void getMessageList(@Query("email_id") String abc, Callback<JsonArray> callback);

    @GET("/reportIncident")
    void getReportIncedent(@Query("email_id") String abc, @Query("shift_id") String shift_id, @Query("type") String type,
                           @Query("severity") String severity, @Query("remarks") String remarks,
                           @Query("latitude") double latitude, @Query("longitude") double longitude, Callback<String> callback);

    @GET("/employeedetails")
    void getUpdatefile(@Body TypedInput bytes, Callback<String> cb);

    @GET("/reportNFCScan")
    void getNFCScan(@Query("unique_id") String unique_id, @Query("date") String date,
                    @Query("time") String time, @Query("latitude") String latitude, @Query("longitude") String longitude, @Query("shift_id") String shift_id, Callback<ScanNFCPojo> cb);


    @Multipart
    @POST("/uploadIncident")
    void uploadImage(@Part("incident_id") String incident_id,
                     @Part("fileName") String fileName,
                     @Part("imageBytes") TypedFile file,
                     @Part("format") String format,
                     @Part("remarks") String remarks, Callback<String> callback);

    @GET("/viewshiftSchedule")
    void getshiftSchudule(@Query("shift_id") String shift_id, @Query("date") String date,
                          Callback<ScheduleParentPojo> cb);

    @Multipart
    @POST("/uploadPickAttendance")
    void uploadScanImage(@Part("NameorId") String incident_id,
                         @Part("imageBytes") TypedFile file,
                         @Part("latitude") String latitude,
                         @Part("longitude") String longitude, Callback<String> callback);


    @GET("/uploadQR")
    void getQRdata(@Query("email_id") String email_id,
                   @Query("latitude") String latitude, @Query("longitude") String longitude, @Query("remarks") String remarks, Callback<String> cb);

    @GET("/getEmployeesNFC")
    void getNFcDetail(Callback<JsonArray> cb);

    @GET("/setEmployeeNFC")
    void sendData(@Query("unique_code") String unique_code, @Query("id") String id, Callback<String> callback);

    @GET("/getincidentypeList")
    void getIncidents(Callback<incidentListBean> callback);

    @GET("/viewactivityList")
    void getActivityList(@Query("email_id") String abc , @Query("date") String date , Callback<List<activityBean>> callback);

    @GET("/validateactivity")
    void validate(@Query("activity_id") String actiId, @Query("validate_code") String calidateCode, @Query("latitude") String lat,
                           @Query("longitude") String lng , Callback<Integer> callback);

}
