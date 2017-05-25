package com.sdi.castivate.utils;

import com.sdi.castivate.model.ApplyCastingInput;
import com.sdi.castivate.model.ApplyCastingOutput;
import com.sdi.castivate.model.AutoSubmit;
import com.sdi.castivate.model.AutoSubmitOutput;
import com.sdi.castivate.model.ChangePassInput;
import com.sdi.castivate.model.ChangePassResponse;
import com.sdi.castivate.model.CommentsInput;
import com.sdi.castivate.model.CommentsOutput;
import com.sdi.castivate.model.DeleteFileInput;
import com.sdi.castivate.model.DeleteFileOutput;
import com.sdi.castivate.model.ForgotPassInput;
import com.sdi.castivate.model.LoginInput;
import com.sdi.castivate.model.LoginResponse;
import com.sdi.castivate.model.MatchedCastingInput;
import com.sdi.castivate.model.MatchedCastingOutput;
import com.sdi.castivate.model.NewDeleteFileInput;
import com.sdi.castivate.model.NewProfileinfoOutput;
import com.sdi.castivate.model.PersonNotificationInput;
import com.sdi.castivate.model.PersonNotificationOutput;
import com.sdi.castivate.model.ProfileinfoInput;
import com.sdi.castivate.model.ProfileinfoOutput;
import com.sdi.castivate.model.PurchaseInput;
import com.sdi.castivate.model.RegisterInput;
import com.sdi.castivate.model.RegisterResponse;
import com.sdi.castivate.model.RemoveCastingInput;
import com.sdi.castivate.model.RemoveCastingOutput;
import com.sdi.castivate.model.ThankCommentInput;
import com.sdi.castivate.model.ThankCommentOutput;
import com.sdi.castivate.model.UpdateAutoSubmitInput;
import com.sdi.castivate.model.UpdateAutoSubmitOutput;
import com.sdi.castivate.model.UpdateNotificationInput;
import com.sdi.castivate.model.UpdateNotificationOutput;
import com.sdi.castivate.model.UpdatePersonInput;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.mime.MultipartTypedOutput;

/**
 * Created by twilightuser on 6/10/16.
 */

public interface RemoteApi {

    @POST("/insertPerson")
    public void getRegister(@Body RegisterInput registerInput,
                            Callback<LoginResponse> callback);

    @POST("/personlogin")
    public void getLogin(@Body LoginInput loginInput,
                         Callback<LoginResponse> callback);

    @POST("/changePassword")
    public void postChangePass(@Body ChangePassInput changePassInput,
                               Callback<ChangePassResponse> callback);

    @POST("/updatePerson")
    public void postUpdatePerson(@Body UpdatePersonInput changePassInput,
                                 Callback<LoginResponse> callback);

    @POST("/forgotPassword")
    public void postForgotPass(@Body ForgotPassInput changePassInput,
                               Callback<ChangePassResponse> callback);

    @POST("/appPurchase")
    public void posAppPurchase(@Body PurchaseInput purchaseInput,
                               Callback<ChangePassResponse> callback);

    @POST("/personProfile")
    public void postPersonProfile(@Body ProfileinfoInput purchaseInput,
                                  Callback<ProfileinfoOutput> callback);

    @POST("/personProfile")
    public void postNewPersonProfile(@Body ProfileinfoInput purchaseInput,
                                     Callback<NewProfileinfoOutput> callback);

    @POST("/deleteFile")
    public void postDeleteFile(@Body DeleteFileInput purchaseInput,
                               Callback<DeleteFileOutput> callback);

    @POST("/deleteFile")
    public void postNewDeleteFile(@Body NewDeleteFileInput purchaseInput,
                                  Callback<DeleteFileOutput> callback);

    @POST("/applyCasting")
    public void getApplyCasting(@Body ApplyCastingInput purchaseInput,
                                Callback<ApplyCastingOutput> callback);

    @POST("/fileupload")
    void createPostWithAttachments(@Query("userid") String userid, @Query("role_id") String role_id, @Query("enthicity") String enthicity,
                                   @Query("age_range") String age_range, @Query("gender") String gender, @Body MultipartTypedOutput attachments, Callback<RegisterResponse> response);


    // by nijam

    @POST("/personNotification")
    public void personNotification(@Body PersonNotificationInput purchaseInput,
                                   Callback<PersonNotificationOutput> callback);

    @POST("/updateNotification")
    public void updateNotification(@Body UpdateNotificationInput purchaseInput,
                                   Callback<UpdateNotificationOutput> callback);

    //new workflow on 28 Feb 2017
    @POST("/getAutoSubmitStatus")
    public void autoSubmit(@Body AutoSubmit autoSubmit, Callback<AutoSubmitOutput> callback);

    @POST("/updateAutoSubmitStatus")
    public void updateAutoSubmit(@Body UpdateAutoSubmitInput input, Callback<UpdateAutoSubmitOutput> callback);

    @POST("/removeCasting")
    public void removeCasting(@Body RemoveCastingInput castingInput, Callback<RemoveCastingOutput> callback);


    @POST("/matchedCasting")
    public void matchedCasting(@Body MatchedCastingInput matchedCastingInput, Callback<MatchedCastingOutput> callback);

    @POST("/commentsAndCompliments")
    public void comments(@Body CommentsInput commentsInput, Callback<CommentsOutput> callback);


    @POST("/setImageFav")
    public void getImageFav(@Body ThankCommentInput thankCommentInput, Callback<ThankCommentOutput> callback);




   /* @POST("/getLikeStatus")
    public void getLikeStatus(@Body GetLikeStatusInput input, Callback<>)

    @POST("/updateUnlike")
    public void unLike(@Body GetLikeStatusInput input, Callback<>)*/
}


