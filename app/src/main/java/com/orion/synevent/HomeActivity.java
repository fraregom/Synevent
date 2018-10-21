package com.orion.synevent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.orion.synevent.fragments.HomeFragment;
import com.orion.synevent.fragments.LoginFragment;

import java.util.ArrayList;
import java.util.HashMap;

import rx.subscriptions.CompositeSubscription;

public class HomeActivity extends AppCompatActivity {

    public static final String TAG = HomeFragment.class.getSimpleName();


    private ArrayList<HashMap<String,String>> days = new ArrayList<>();
    private String[] task_per_day = new String[]{"Trabajo"};

    private ArrayList<HashMap<String,String>> one_class;

    private ListView lv_day_of_week;
    private ListView lv_task_per_day;
    private TextView tv_name_class;
    private TextView tv_class_room;

    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;



    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {

            loadFragment();
        }
    }

    private void loadFragment(){

        if (homeFragment == null) {

            homeFragment = new HomeFragment();
        }
        getFragmentManager().beginTransaction().replace(R.id.fragmentFrame,homeFragment,LoginFragment.TAG).commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String data = intent.getData().getLastPathSegment();
        Log.d(TAG, "onNewIntent: "+data);
    }


    public void initViews(View root, View schedule, View day, View task){
        lv_day_of_week = schedule.findViewById(R.id.lv_a_day_of_week);
        lv_task_per_day = day.findViewById(R.id.lv_tasks_per_day);
        tv_name_class = task.findViewById(R.id.tv_name_class);
        tv_class_room = task.findViewById(R.id.tv_class_room);
    }

    private void initSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }
}
