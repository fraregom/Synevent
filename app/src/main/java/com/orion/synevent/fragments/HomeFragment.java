package com.orion.synevent.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.orion.synevent.R;

import java.util.ArrayList;
import java.util.HashMap;

import rx.subscriptions.CompositeSubscription;

public class HomeFragment extends Fragment {


    public static final String TAG = HomeFragment.class.getSimpleName();


    private ArrayList<HashMap<String,String>> days = new ArrayList<>();
    private ArrayList<HashMap<String,HashMap<String,String>>> Mdays = new ArrayList<>();
    private String[] task_per_day = new String[]{"Trabajo"};

    private ArrayList<HashMap<String,String>> one_class;

    private ListView lv_day_of_week;
    private ListView lv_task_per_day;
    private TextView tv_name_class;
    private TextView tv_class_room;

    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        one_class = new ArrayList<HashMap<String, String>>();
        HashMap<String,String> myhash = new HashMap<>();
        myhash.put("tv_name_class","Lenguajes de Programacion");
        myhash.put("tv_class_room","P 201");
        one_class.add(myhash);


        View root = inflater.inflate(R.layout.fragment_list,container,false);
        View schedule = inflater.inflate(R.layout.fragment_schedule_view,container,true);
        View day = inflater.inflate(R.layout.working_day,container,false);
        View task = inflater.inflate(R.layout.one_task,container,false);

        mSubscriptions = new CompositeSubscription();
        initViews(root,schedule,day,task);
        initSharedPreferences();

/*        ListAdapter adapter = new SimpleAdapter(
                getActivity(), one_class,
                R.layout.fragment_list, new String[]{"tv_name_class","tv_class_room"},
                new int[]{R.id.tv_name_class, R.id.tv_class_room});

        lv_task_per_day.setAdapter(adapter);

        HashMap<String,HashMap<String,String>> myhash2 = new HashMap<>();
        myhash2.put("Lunes",myhash);
        Mdays.add(myhash2);

        ListAdapter adapter2 = new SimpleAdapter(
                getActivity(), Mdays,
                R.layout.working_day, new String[]{"lv_tasks_per_day"},
                new int[]{R.id.tv_day});

        lv_day_of_week.setAdapter(adapter2);
*/
        return root;
    }

    public void initViews(View root, View schedule, View day, View task){
        lv_day_of_week = schedule.findViewById(R.id.lv_a_day_of_week);
        lv_task_per_day = day.findViewById(R.id.lv_tasks_per_day);
        tv_name_class = task.findViewById(R.id.tv_name_class);
        tv_class_room = task.findViewById(R.id.tv_class_room);
    }

    private void initSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }


}
