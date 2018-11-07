package com.orion.synevent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orion.synevent.apiservice.NetworkUtil;
import com.orion.synevent.models.Invitations;
import com.orion.synevent.models.Joined;
import com.orion.synevent.models.Participants;
import com.orion.synevent.models.Response;
import com.orion.synevent.models.UserInvitation;
import com.orion.synevent.utils.Constants;
import com.orion.synevent.utils.DrawerUtil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ListEventActivity extends AppCompatActivity implements TabHost.TabContentFactory{

    public static final String TAG = ListEventActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private ListView lv;
    private TabHost tabHost;
    private static ArrayList<HashMap<String, String>> list_events;
    private String mToken;
    private CompositeSubscription mSubscriptions;
    private Integer number_participants;
    private SimpleAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_events);
        setTabhost();
        initializeVars();
        setToolbar();
        setListEvents();
    }

    private void setListEvents() {
        mSubscriptions.add(NetworkUtil.getRetrofit(mToken)
                .invitations()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleList, this::handleError));
    }

    private void handleList(List<Invitations> invitations) {
        int number_invitations = invitations.size();

        for(int i = 0; i<number_invitations;i++){
            Invitations inv = invitations.get(i);
            UserInvitation user_inv = inv.getUserInvitation();

            getInvitationParticipants(inv);

        }


    }

    private void getInvitationParticipants(Invitations inv) {
        mSubscriptions.add(NetworkUtil.getRetrofit(mToken)
                .InvitationParticipants(inv.getUserInvitation().getInvitationId().toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleParticipants, this::handleError));

        HashMap<String, String> ayu = new HashMap<>();

        ayu.put("id",inv.getId().toString());
        ayu.put("text1", inv.getName());
        ayu.put("number_of_users_in_event", number_participants+" participantes");

        Log.d(TAG, "onNewIntent: "+inv.getName());

        SimpleDateFormat dateFormatParse = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.ssss'Z'");
        if(inv.getFinishAt() != null)
        {
            String targetDate = inv.getFinishAt().toString();
            Date dateString;
            String end_event = "";
            Calendar calendar;
            try {
                dateString = dateFormatParse.parse(targetDate);
                calendar = new GregorianCalendar();
                calendar.setTime(dateString);

                end_event = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "-" + String.valueOf(calendar.get(Calendar.MONTH)+1 )+ "-"+
                        String.valueOf(calendar.get(Calendar.YEAR))+
                        " at "+ String.valueOf(calendar.get(Calendar.HOUR_OF_DAY) )+":"+String.valueOf(calendar.get(Calendar.MINUTE));
            } catch (ParseException e) {
                end_event = "Your events not founded";
                e.printStackTrace();
            }

            ayu.put("end_event", end_event);
            ayu.put("short_id", inv.getShortId());
            list_events.add(ayu);
        }else{
            ayu.put("end_event", "Por Autor");
            ayu.put("short_id", inv.getShortId());
            list_events.add(ayu);
        }


    }

    private void handleParticipants(List<Participants> invitations) {
        number_participants = invitations.size();
       // this.number_participants = String.valueOf(invitations.size());

//        this.lv.getAdapter().
        for(int j = 0; j < number_participants; j++) {
            for(int i = 0; i < list_events.size(); i++) {
                if(list_events.get(i).get("id") == invitations.get(j).getUserInvitation().getInvitationId().toString())
                {

                    list_events.get(i).put("id_event",invitations.get(j).getUserInvitation().getInvitationId().toString());
                    list_events.get(i).put("number_of_users_in_event",number_participants.toString()+" Participantes");
                }
            }
        }
        adapter = new SimpleAdapter(this, list_events, R.layout.item_event_frame,
                new String[]{"text1", "number_of_users_in_event", "end_event","id_event", "short_id"},
                new int[]{R.id.text1, R.id.number_of_users_in_event, R.id.end_event, R.id.id_event, R.id.short_id});

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                ListView lv = (ListView) arg0;
                LinearLayout tv;
                try {
                    tv = (LinearLayout) lv.getChildAt(arg2);
                }catch(Exception e) {
                    tv = (LinearLayout) lv.getChildAt(arg2-3);
                }
                if(tv == null){
                    arg2 = arg2-3;
                    tv= (LinearLayout) lv.getChildAt(arg2);
                }
                //tv.getChildCount();
                TextView tv_name_event = (TextView)tv.getChildAt(1);
                String name_eve = tv_name_event.getText().toString();
                TextView tt_id_invitation = (TextView) ((RelativeLayout) tv.getChildAt(2)).getChildAt(0);
                String id_invitation = tt_id_invitation.getText().toString();

                TextView tt_short_id = (TextView) ((RelativeLayout) tv.getChildAt(2)).getChildAt(1);
                String id_short = tt_short_id.getText().toString();

                LinearLayout ll_status= (LinearLayout) tv.getChildAt(3);
                TextView tv_number_participants = (TextView) ll_status.getChildAt(0);
                String number_pa = tv_number_participants.getText().toString();
                TextView tv_finish = (TextView) ll_status.getChildAt(1);
                String finish = tv_finish.getText().toString();

                Intent myIntent = new Intent(ListEventActivity.this, StatusEventActivity.class);
                myIntent.putExtra("name_event",name_eve);
                myIntent.putExtra("number_participants",number_pa);
                myIntent.putExtra("id_invitation",id_invitation);
                myIntent.putExtra("id_short",id_short);
                myIntent.putExtra("finish",finish);
                startActivity(myIntent);
                finish();
            } });


        Log.d(TAG, "onNewIntent: largeeeeeeeeee"+invitations.size());
    }

    private void setTabhost() {
        tabHost = findViewById(R.id.tabhost);
        tabHost.setup();
        tabHost.addTab(getTabSpec1(tabHost));
        tabHost.addTab(getTabSpec2(tabHost));
        tabHost.addTab(getTabSpec3(tabHost));
    }

    public void initializeVars() {
        mToolbar = findViewById(R.id.activity_toolbar_list);
        lv = findViewById(R.id.mlistactive);
        mSubscriptions = new CompositeSubscription();
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        list_events = new ArrayList<>();

    }

    private TabHost.TabSpec getTabSpec1(TabHost tabHost) {
        return tabHost.newTabSpec("First Tab")
                .setIndicator("Active")
                .setContent(this);
    }

    private TabHost.TabSpec getTabSpec2(TabHost tabHost) {
        return tabHost.newTabSpec("Second Tab")
                .setIndicator("Finished")
                .setContent(this);
    }

    private TabHost.TabSpec getTabSpec3(TabHost tabHost) {
        TabHost.TabSpec aa =  tabHost.newTabSpec("Third Tab")
                .setIndicator("Join It")
                .setContent(this);
        //getJoinedEvents();
        return aa;
    }

    @Override
    public View createTabContent(String tag) {
        // Full ArrayAdapters to list the events
        //getJoinedEvents();
        if(tag == "Second Tab")
        {
            return  LayoutInflater.from(this).inflate(R.layout.finished_events,null);
        }else if(tag == "Third Tab"){
            //LinearLayout ll = (LinearLayout)getLayoutInflater().inflate(R.layout.join_event, null, true);
            View aa = LayoutInflater.from(this).inflate(R.layout.join_event,null);
            //setContentView(aa);
            //getJoinedEvents();
            return aa;
        }else{
            return LayoutInflater.from(this).inflate(R.layout.actives_events, null);
        }
    }


    public void setToolbar(){
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("My Events");
        mToolbar.setTitle("My Events");
        mToolbar.setTitleMargin(200, 2, 15, 2);
        mToolbar.setTitleTextColor(Color.WHITE);
        DrawerUtil.getDrawer(this, mToolbar);
    }

    public void joinToEvent(View view) {
        // connect to server to suscribe to event
        EditText et_code = findViewById(R.id.code_join_event);

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken)
                .joinToInvitation(et_code.getText().toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleRequest, this::handleError));

        Toast.makeText(this,et_code.getText().toString(), Toast.LENGTH_LONG).show();
    }

    private void handleRequest(Collection<Joined> joineds) {
        joineds.size();
    }

    private void handleError(Throwable error) {

        Log.e(TAG , String.valueOf(error));
        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showToastMessage(response.getMsg());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showToastMessage("An unknown error has occurred!");
        }
    }

    private void showToastMessage(String message) {
        Toast.makeText(getBaseContext(),message, Toast.LENGTH_LONG).show();
    }
}
