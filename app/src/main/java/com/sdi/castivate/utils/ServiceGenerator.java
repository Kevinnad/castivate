package com.sdi.castivate.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.sdi.castivate.BuildConfig;
import com.sdi.castivate.model.LoginResponse;
import com.sdi.castivate.model.RegisterResponse;

import okhttp3.OkHttpClient;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.mime.MultipartTypedOutput;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by androidusr1 on 19/10/16.
 */
public class ServiceGenerator {

    private RemoteApi mRemoteApi;


    private static ServiceGenerator sInstance = new ServiceGenerator();


    public void getSignUpData(MultipartTypedOutput multipartTypedOutput, Callback<RegisterResponse> callback)
            throws IllegalStateException {
        // TODO Auto-generated method stub
        // make the service call

        getRemoteApi().createPostWithAttachments("8", "77", "African American", "10 - 30", "male",multipartTypedOutput, callback);
        // getRemoteApi().getLogin(login, callback);
    }

    public static ServiceGenerator getInstance() {
        return sInstance;
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
        com.squareup.okhttp.OkHttpClient client = new com.squareup.okhttp.OkHttpClient();
        builder.setClient(new OkClient(client));
        RestAdapter adapter = builder.build();

        return adapter.create(RemoteApi.class);
    }
}