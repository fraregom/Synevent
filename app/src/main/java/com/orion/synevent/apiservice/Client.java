package com.orion.synevent.apiservice;

import com.orion.synevent.models.CalendarBody;
import com.orion.synevent.models.Response;
import com.orion.synevent.models.Schedule;
import com.orion.synevent.models.User;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface Client {

    @POST("register")
    Observable<Response> register(@Body User user);

    @POST("login")
    Observable<Response> login(@Body User user);

    @GET("protected")
    Observable<Response> getInfo();

    @GET ("schedule/{id_schedule}/activities")
    Observable<CalendarBody> getCalendar(@Path("id_schedule") String id_schedule);

    @GET ("schedules")
    Observable<Schedule> getSchedule();
}