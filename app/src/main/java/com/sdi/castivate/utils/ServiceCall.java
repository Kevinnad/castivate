package com.sdi.castivate.utils;

import com.sdi.castivate.BuildConfig;
import com.sdi.castivate.model.ContactFormRequest;
import com.sdi.castivate.model.Users;
import com.sdi.castivate.model.UsersRes;
import com.squareup.okhttp.OkHttpClient;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit2.Call;

/**
 * Created by Sugumaran on 30-Oct-15.
 */
public class ServiceCall {





    //Host beta
    public static String API = "http://php.twilightsoftwares.com:9998/castivateDev/castingNewVer1/public";

    public static RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ServiceCall.API).build();

    public static RestAdapter initRestADapters() {

        OkHttpClient client = new OkHttpClient();
        RestAdapter.Builder builder = new RestAdapter.Builder().setEndpoint(API);

        if (BuildConfig.DEBUG) {
            builder.setLogLevel((RestAdapter.LogLevel.FULL));
        }
        builder.setClient(new OkClient(client));
        ServiceCall.restAdapter = builder.build();

        return ServiceCall.restAdapter;
    }


    //Update Profile Image
//    public interface ProfileImageUpdate {
//        @FormUrlEncoded
//        @POST("/gbservice/gb_update_user")
//        public void update(@Field("user_id") int user_id, @Field("my_photo") String my_photo, Callback<ProfileUpdate> response);
//    }
    //Get Announcements
//    public interface GetAnnouncements {
//        @GET("/gbannouncements/get_announcements")
//        public void update(@Query("user_id") int user_id, Callback<GetAnnouncementResponse> response);
//    }

//    "userId=28&casting_id=5141";
    public interface UpdateReportAubuse{
//        @FormUrlEncoded
        @POST("/reportAbuse")
        public void update(@Field("userId") String userId, @Field("casting_id") String casting_id, Callback<String> response);
    }

  public interface UpdateReportAubuseBody{
      @POST("/reportAbuse")
      public void createUser(@Body Users user,Callback<UsersRes> response);
    }

 public interface UpdateContactFormBody{
      @POST("/contactTalent")
      public void createUser(@Body ContactFormRequest user, Callback<UsersRes> response);
    }




    //    {"success":1,"my_photo_url":"http:\/\/v.gentlebirth.com\/wp-content\/uploads\/1475672715833.png"}
    public class UpdateFullNameResponse {
        public int success;
        public String my_photo_url;
    }


}
