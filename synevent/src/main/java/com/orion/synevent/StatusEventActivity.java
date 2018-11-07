package com.orion.synevent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orion.synevent.apiservice.NetworkUtil;
import com.orion.synevent.models.Invitations;
import com.orion.synevent.models.Participants;
import com.orion.synevent.models.Response;
import com.orion.synevent.models.UserInvitation;
import com.orion.synevent.utils.Constants;
import com.orion.synevent.utils.DrawerUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class StatusEventActivity extends AppCompatActivity {
    private static final String TAG = StatusEventActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private TextView tv_name;
    private TextView tv_finish;
    private TextView tv_code;
    private ListView lv;
    private ArrayList<HashMap<String, String>> list_participants;
    private CompositeSubscription mSubscriptions;
    private String mToken;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_events);

        Intent myIntent = getIntent(); // gets the previously created intent
        String name_event = myIntent.getStringExtra("name_event"); // will return "name_event"
        String number_participants = myIntent.getStringExtra("number_participants"); // will return "number_participants"
        String id_invitation = myIntent.getStringExtra("id_invitation"); // will return "number_participants"
        String finish= myIntent.getStringExtra("finish");
        String code= myIntent.getStringExtra("id_short");

        initializeVars(name_event,finish,code);
        setToolbar();
        setParticipantsEvent(id_invitation);
    }

    private void setParticipantsEvent(String id_invitation) {

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken)
                .InvitationParticipants(id_invitation)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleParticipants, this::handleError));

    }

    private void handleParticipants(List<Participants> invitations) {
        lv = findViewById(R.id.lv_participants);
        list_participants = new ArrayList<>();


        for(int i = 0; i < invitations.size() ; i++) {
            HashMap<String, String> ayu = new HashMap<>();
            String username = invitations.get(i).getUserName();
            ayu.put("name_participant", username);
            list_participants.add(ayu);
        }

        ListAdapter adapter;
        adapter = new SimpleAdapter(this, list_participants, R.layout.one_participant,
                new String[]{"name_participant"},
                new int[]{R.id.name_participant});

        lv.setAdapter(adapter);
    }

    private void initializeVars(String name,String finished,String short_id) {
        mToolbar = findViewById(R.id.toolbar_one_event);
        tv_name = findViewById(R.id.name_event);
        tv_finish = findViewById(R.id.finish_by);
        tv_code = findViewById(R.id.code_event);
        tv_name.append(name);
        tv_finish.append(finished);
        tv_code.append(short_id);

        mSubscriptions = new CompositeSubscription();
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Estado Evento");
        mToolbar.setTitle("Estado Evento");
        mToolbar.setTitleMargin(180, 2, 15, 2);
        mToolbar.setTitleTextColor(Color.WHITE);
        DrawerUtil.getDrawer(this, mToolbar);
        mToolbar.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.share));
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_status, menu);

        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_event:
                // Send to server event syncronize
                Intent refresh = new Intent(this, MenuActivity.class);
                startActivity(refresh);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
