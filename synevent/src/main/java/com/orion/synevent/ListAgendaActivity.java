package com.orion.synevent;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orion.synevent.apiservice.NetworkUtil;
import com.orion.synevent.models.Response;
import com.orion.synevent.models.Schedule;
import com.orion.synevent.utils.Constants;
import com.orion.synevent.utils.ListViewItemCheckboxBaseAdapter;
import com.orion.synevent.utils.ListViewItemDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ListAgendaActivity extends AppCompatActivity {

    public Boolean selected = false;
    public Integer checkedItem = -1;

    public static final String TAG = MenuActivity.class.getSimpleName();
    public Map<String, Boolean> items = new HashMap<String, Boolean>();
    private SharedPreferences mSharedPreferences;
    private CompositeSubscription mSubscriptions;
    private Toolbar mToolbar;
    private String mSchedule;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_with_checkbox);
        mSubscriptions = new CompositeSubscription();
        initSharedPreferences();

        mToolbar = findViewById(R.id.activity_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("My Schedules");
        mToolbar.setTitle("My Schedules");
        mToolbar.setTitleTextColor(Color.WHITE);
        // Get listview checkbox.
        final ListView listViewWithCheckbox = (ListView)findViewById(R.id.list_view_with_checkbox);

        // Initiate listview data.
        final List<ListViewItemDTO> initItemList = this.getInitViewItemDtoList();

        // Create a custom list view adapter with checkbox control.
        final ListViewItemCheckboxBaseAdapter listViewDataAdapter = new ListViewItemCheckboxBaseAdapter(getApplicationContext(), initItemList);

        listViewDataAdapter.notifyDataSetChanged();

        // Set data adapter to list view.
        listViewWithCheckbox.setAdapter(listViewDataAdapter);

        // When list view item is clicked.
        listViewWithCheckbox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long l) {
                // Get user selected item.
                Object itemObject = adapterView.getAdapter().getItem(itemIndex);

                // Translate the selected item to DTO object.
                ListViewItemDTO itemDto = (ListViewItemDTO)itemObject;

                // Get the checkbox.
                CheckBox itemCheckbox = (CheckBox) view.findViewById(R.id.list_view_item_checkbox);

                // Reverse the checkbox and clicked item check state.
                if(itemDto.isChecked())
                {
                    itemCheckbox.setChecked(false);
                    itemDto.setChecked(false);
                }else{
                    itemCheckbox.setChecked(true);
                    itemDto.setChecked(true);
                }

                //Toast.makeText(getApplicationContext(), "select item text : " + itemDto.getItemText(), Toast.LENGTH_SHORT).show();
            }
        });

        // Click this button to select all listview items with checkbox checked.
        /*Button selectAllButton = (Button)findViewById(R.id.list_select_all);
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CreateAgendaActivity.class);
                startActivity(intent);
            }
        });

        /*

        // Click this button to disselect all listview items with checkbox unchecked.
        Button selectNoneButton = (Button)findViewById(R.id.list_select_none);
        selectNoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int size = initItemList.size();
                for(int i=0;i<size;i++)
                {
                    ListViewItemDTO dto = initItemList.get(i);
                    dto.setChecked(false);
                }

                listViewDataAdapter.notifyDataSetChanged();
            }
        });

        // Click this button to reverse select listview items.
        //Button selectReverseButton = (Button)findViewById(R.id.list_select_reverse);
        selectReverseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int size = initItemList.size();
                for(int i=0;i<size;i++)
                {
                    ListViewItemDTO dto = initItemList.get(i);

                    if(dto.isChecked())
                    {
                        dto.setChecked(false);
                    }else {
                        dto.setChecked(true);
                    }
                }

                listViewDataAdapter.notifyDataSetChanged();
            }
        });*/

        // Click this button to remove selected items from listview.
        Button selectRemoveButton = (Button)findViewById(R.id.list_remove_selected_rows);
        selectRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog alertDialog = new AlertDialog.Builder(ListAgendaActivity.this).create();
                alertDialog.setMessage("Are you sure to remove selected schedule?");

                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        int size = initItemList.size();
                        for(int i=0;i<size;i++)
                        {
                            ListViewItemDTO dto = initItemList.get(i);

                            if(dto.isChecked())
                            {
                                initItemList.remove(i);
                                i--;
                                size = initItemList.size();
                            }
                        }

                        listViewDataAdapter.notifyDataSetChanged();





                    }
                });

                alertDialog.show();
            }
        });

    }

    private void initSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSchedule = mSharedPreferences.getString(Constants.ID_SCHEDULE, "");
        mToken = mSharedPreferences.getString(Constants.TOKEN, "");

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getSchedule()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleSchedule,this::handleError));
    }

    public void handleSchedule(List<Schedule> body){
        for (int i = 0; i <= body.size(); i++) {
            items.put(body.get(i).getName() , body.get(i).getSelected());
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

    // Return an initialize list of ListViewItemDTO.
    private List<ListViewItemDTO> getInitViewItemDtoList()
    {

        int i = 0;
        List<ListViewItemDTO> ret = new ArrayList<ListViewItemDTO>();

        for(Map.Entry<String, Boolean> item : items.entrySet()) {
            String key = item.getKey();
            Boolean value = item.getValue();

            ListViewItemDTO dto = new ListViewItemDTO();
            dto.setItemText(key);
            if(value){
                dto.setChecked(true);
            }else{
                dto.setChecked(false);
            }

            ret.add(dto);
            i++;
        }

        return ret;
    }


    private void showToastMessage(String message) {
        Toast.makeText(getBaseContext(),message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

}
