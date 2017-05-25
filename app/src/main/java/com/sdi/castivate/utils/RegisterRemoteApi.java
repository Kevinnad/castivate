package com.sdi.castivate.utils;

/**
 * Created by twilightuser on 6/10/16.
 */


import android.annotation.SuppressLint;
import android.content.Context;

import com.sdi.castivate.BuildConfig;
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
import com.sdi.castivate.model.RemoveCastingInput;
import com.sdi.castivate.model.RemoveCastingOutput;
import com.sdi.castivate.model.ThankCommentInput;
import com.sdi.castivate.model.ThankCommentOutput;
import com.sdi.castivate.model.UpdateAutoSubmitInput;
import com.sdi.castivate.model.UpdateAutoSubmitOutput;
import com.sdi.castivate.model.UpdateNotificationInput;
import com.sdi.castivate.model.UpdateNotificationOutput;
import com.sdi.castivate.model.UpdatePersonInput;
import com.squareup.okhttp.OkHttpClient;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;


public class RegisterRemoteApi {

    public static RegisterInput registerInput;
    public static LoginInput loginInput;
    public static ChangePassInput changePassInput;
    public static UpdatePersonInput updatePersonInput;
    public static ForgotPassInput forgotPassInput;
    public static PurchaseInput purchaseInput;
    //by nijam
    public static PersonNotificationInput personNotificationInput;
    public static UpdateNotificationInput updateNotificationInput;
    public static AutoSubmit autoSubmit;
    public static UpdateAutoSubmitInput updateAutoSubmitInput;
    public static RemoveCastingInput removeCastingInput;
    public static MatchedCastingInput matchedCastingInput;
    public static CommentsInput commentsInput;
    public static ThankCommentInput thankCommentInput;

    public static ThankCommentInput getThankCommentInput() {
        return thankCommentInput;
    }

    public static void setThankCommentInput(ThankCommentInput thankCommentInput) {
        RegisterRemoteApi.thankCommentInput = thankCommentInput;
    }

    public static CommentsInput getCommentsInput() {
        return commentsInput;
    }

    public static void setCommentsInput(CommentsInput commentsInput) {
        RegisterRemoteApi.commentsInput = commentsInput;
    }

    public static NewDeleteFileInput input;

    public static NewDeleteFileInput getInput() {
        return input;
    }

    public static void setInput(NewDeleteFileInput input) {
        RegisterRemoteApi.input = input;
    }


    public static void setNewDeleteInput(NewDeleteFileInput newDeleteInput) {
        RegisterRemoteApi.input = newDeleteInput;
    }

    public void getNewDeleteInput(Context context, Callback<DeleteFileOutput> callback)
            throws IllegalStateException {
        // TODO Auto-generated method stub
        // make the service call

        getRemoteApi().postNewDeleteFile(input, callback);
        // getRemoteApi().getLogin(login, callback);
    }


    public static MatchedCastingInput getMatchedCastingInput() {
        return matchedCastingInput;
    }

    public static void setMatchedCastingInput(MatchedCastingInput matchedCastingInput) {
        RegisterRemoteApi.matchedCastingInput = matchedCastingInput;
    }

    public static RemoveCastingInput getRemoveCastingInput() {
        return removeCastingInput;
    }

    public static void setRemoveCastingInput(RemoveCastingInput removeCastingInput) {
        RegisterRemoteApi.removeCastingInput = removeCastingInput;
    }

    public static UpdateAutoSubmitInput getUpdateAutoSubmitInput() {
        return updateAutoSubmitInput;
    }

    public static void setUpdateAutoSubmitInput(UpdateAutoSubmitInput updateAutoSubmitInput) {
        RegisterRemoteApi.updateAutoSubmitInput = updateAutoSubmitInput;
    }

    public static AutoSubmit getAutoSubmit() {
        return autoSubmit;
    }

    public static void setAutoSubmit(AutoSubmit autoSubmit) {
        RegisterRemoteApi.autoSubmit = autoSubmit;
    }

    public static PersonNotificationInput getPersonNotificationInput() {
        return personNotificationInput;
    }

    public static void setPersonNotificationInput(PersonNotificationInput personNotificationInput) {
        RegisterRemoteApi.personNotificationInput = personNotificationInput;
    }

    public static UpdateNotificationInput getUpdateNotificationInput() {
        return updateNotificationInput;
    }

    public static void setUpdateNotificationInput(UpdateNotificationInput updateNotificationInput) {
        RegisterRemoteApi.updateNotificationInput = updateNotificationInput;
    }


    public static ApplyCastingInput getApplyCastingInput() {
        return applyCastingInput;
    }

    public static void setApplyCastingInput(ApplyCastingInput applyCastingInput) {
        RegisterRemoteApi.applyCastingInput = applyCastingInput;
    }

    public static ApplyCastingInput applyCastingInput;

    public static DeleteFileInput getDeleteFileInput() {
        return deleteFileInput;
    }

    public static void setDeleteFileInput(DeleteFileInput deleteFileInput) {
        RegisterRemoteApi.deleteFileInput = deleteFileInput;
    }

    public static DeleteFileInput deleteFileInput;

    public static ProfileinfoInput getProfileinfoInput() {
        return profileinfoInput;
    }

    public static void setProfileinfoInput(ProfileinfoInput profileinfoInput) {
        RegisterRemoteApi.profileinfoInput = profileinfoInput;
    }

    public static ProfileinfoInput profileinfoInput;
    private static RegisterRemoteApi sInstance = new RegisterRemoteApi();
    private RemoteApi mRemoteApi;

    public static void setPurchaseInput(PurchaseInput purchaseInput) {
        RegisterRemoteApi.purchaseInput = purchaseInput;
    }

    public static void setForgotPassInput(ForgotPassInput forgotPassInput) {
        RegisterRemoteApi.forgotPassInput = forgotPassInput;
    }

    public static void setUpdatePersonInput(UpdatePersonInput updatePersonInput) {
        RegisterRemoteApi.updatePersonInput = updatePersonInput;
    }

    public static void setChangePassInput(ChangePassInput changePassInput) {
        RegisterRemoteApi.changePassInput = changePassInput;
    }

    public static void setLoginInput(LoginInput loginInput) {
        RegisterRemoteApi.loginInput = loginInput;
    }

    public static RegisterRemoteApi getInstance() {
        return sInstance;
    }

    public static RegisterInput getRegisterInput() {
        return registerInput;
    }

    public static void setRegisterInput(RegisterInput registerInput) {
        RegisterRemoteApi.registerInput = registerInput;
    }

    public void getSignUpData(Context context, Callback<LoginResponse> callback)
            throws IllegalStateException {
        // TODO Auto-generated method stub
        // make the service call

        getRemoteApi().getRegister(registerInput, callback);
        // getRemoteApi().getLogin(login, callback);
    }

    public void getLoginData(Context context, Callback<LoginResponse> callback)
            throws IllegalStateException {
        // TODO Auto-generated method stub
        // make the service call

        getRemoteApi().getLogin(loginInput, callback);
        // getRemoteApi().getLogin(login, callback);
    }

    public void getChanPassData(Context context, Callback<ChangePassResponse> callback)
            throws IllegalStateException {
        // TODO Auto-generated method stub
        // make the service call

        getRemoteApi().postChangePass(changePassInput, callback);
        // getRemoteApi().getLogin(login, callback);
    }

    public void getUpdatePersonData(Context context, Callback<LoginResponse> callback)
            throws IllegalStateException {
        // TODO Auto-generated method stub
        // make the service call

        getRemoteApi().postUpdatePerson(updatePersonInput, callback);
        // getRemoteApi().getLogin(login, callback);
    }

    public void getApplyCasting(Context context, Callback<ApplyCastingOutput> callback)
            throws IllegalStateException {
        // TODO Auto-generated method stub
        // make the service call

        getRemoteApi().getApplyCasting(applyCastingInput, callback);
        // getRemoteApi().getLogin(login, callback);
    }

    public void postPersonProfile(Context context, Callback<ProfileinfoOutput> callback)
            throws IllegalStateException {
        // TODO Auto-generated method stub
        // make the service call

        getRemoteApi().postPersonProfile(profileinfoInput, callback);
        // getRemoteApi().getLogin(login, callback);
    }

    public void postnewPersonProfile(Context context, Callback<NewProfileinfoOutput> callback)
            throws IllegalStateException {
        // TODO Auto-generated method stub
        // make the service call

        getRemoteApi().postNewPersonProfile(profileinfoInput, callback);
        // getRemoteApi().getLogin(login, callback);
    }

    public void postDeleteFile(Context context, Callback<DeleteFileOutput> callback)
            throws IllegalStateException {
        // TODO Auto-generated method stub
        // make the service call

        getRemoteApi().postDeleteFile(deleteFileInput, callback);
        // getRemoteApi().getLogin(login, callback);
    }

    public void getForgotPassData(Context context, Callback<ChangePassResponse> callback)
            throws IllegalStateException {
        // TODO Auto-generated method stub
        // make the service call

        getRemoteApi().postForgotPass(forgotPassInput, callback);
        // getRemoteApi().getLogin(login, callback);
    }

    public void posAppPurchase(Context context, Callback<ChangePassResponse> callback)
            throws IllegalStateException {
        // TODO Auto-generated method stub
        // make the service call

        getRemoteApi().posAppPurchase(purchaseInput, callback);
        // getRemoteApi().getLogin(login, callback);
    }

    public void personNotification(Context context, Callback<PersonNotificationOutput> callback)
            throws IllegalStateException {

        getRemoteApi().personNotification(personNotificationInput, callback);
    }

    public void updateNotification(Context context, Callback<UpdateNotificationOutput> callback)
            throws IllegalStateException {

        getRemoteApi().updateNotification(updateNotificationInput, callback);
    }

    public void autoSubmit(Context context, Callback<AutoSubmitOutput> callback) throws IllegalStateException {
        getRemoteApi().autoSubmit(autoSubmit, callback);
    }


    public void updateAutoSubmit(Context context, Callback<UpdateAutoSubmitOutput> callback) throws IllegalStateException {
        getRemoteApi().updateAutoSubmit(updateAutoSubmitInput, callback);
    }

    public void removeCasting(Context context, Callback<RemoveCastingOutput> callback) throws IllegalStateException {
        getRemoteApi().removeCasting(removeCastingInput, callback);
    }

    public void matchedCasting(Context context, Callback<MatchedCastingOutput> callback) throws IllegalStateException {
        getRemoteApi().matchedCasting(matchedCastingInput, callback);
    }

    public void comments(Context context, Callback<CommentsOutput> callback) throws IllegalStateException {
        getRemoteApi().comments(commentsInput, callback);
    }

    public void submitComments(Context context, Callback<ThankCommentOutput> callback) throws IllegalStateException {
        getRemoteApi().getImageFav(thankCommentInput, callback);
    }


    private RemoteApi getRemoteApi() {
        if (mRemoteApi == null) {

            mRemoteApi = create();
        }
        return mRemoteApi;
    }

    @SuppressLint("NewApi")
    private RemoteApi create() throws IllegalStateException {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(HttpUri.CastingURL);

        if (BuildConfig.DEBUG) {
            builder.setLogLevel((RestAdapter.LogLevel.FULL));
        }

        builder.setRequestInterceptor(new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {

            }
        });
        OkHttpClient client = new OkHttpClient();
        builder.setClient(new OkClient(client));
        RestAdapter adapter = builder.build();

        return adapter.create(RemoteApi.class);
    }

}



