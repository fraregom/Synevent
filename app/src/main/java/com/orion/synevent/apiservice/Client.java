package com.orion.synevent.apiservice;

import com.orion.synevent.models.Response;
import com.orion.synevent.models.User;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

public interface Client {

    @POST("register")
    Observable<Response> register(@Body User user);

    @POST("login")
    Observable<Response> login(@Body User user);

    @GET("protected")
    Observable<Response> getInfo();
}