package com.orion.synevent.apiservice;

import com.orion.synevent.models.Activities;
import com.orion.synevent.models.InvitationBody;
import com.orion.synevent.models.Invitations;
import com.orion.synevent.models.Joined;
import com.orion.synevent.models.Participants;
import com.orion.synevent.models.Response;
import com.orion.synevent.models.Schedule;
import com.orion.synevent.models.User;
import com.orion.synevent.models.UserInvitation;

import java.util.Collection;
import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Completable;
import rx.Observable;

public interface Client {

    @POST("register")
    Observable<Response> register(@Body User user);

    @POST("login")
    Observable<Response> login(@Body User user);

    @GET("protected")
    Observable<Response> getInfo();

    @GET ("schedule/{id_schedule}/activities/raw")
    Observable<List<Activities>> getCalendar(@Path("id_schedule") String id_schedule);

    @GET ("schedules")
    Observable<List<Schedule>> getSchedule();

    @POST("invitation")
    Observable<Response> newInvitation(@Body InvitationBody invitation);

    @POST("schedule/{id_schedule}/activity")
    Observable<Activities> newActivity(@Path("id_schedule") String id_schedule, @Body Activities activity);

    @GET ("invitations")
    Observable<List<Invitations>> invitations();


    @GET ("invitation/{id_invitation}")
    Observable<List<Participants>> InvitationParticipants(@Path("id_invitation") String id_invitation);

    @GET ("invitation/{short_code}/join")
    Observable<Collection<Joined>> joinToInvitation(@Path("short_code") String short_code);

    @POST("schedule")
    Observable<Schedule> setSchedule(@Body Schedule schedule);

    @DELETE("schedule/{id_schedule}")
    Observable<Response> DeleteSchedule(String id_schedule);
}