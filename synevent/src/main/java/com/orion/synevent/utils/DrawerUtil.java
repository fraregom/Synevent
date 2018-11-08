package com.orion.synevent.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialize.color.Material;
import com.orion.synevent.CreateAgendaActivity;
import com.orion.synevent.CreateScheduleActivity;
import com.orion.synevent.ListAgendaActivity;
import com.orion.synevent.ListEventActivity;
import com.orion.synevent.MainActivity;
import com.orion.synevent.MenuActivity;
import com.orion.synevent.R;
import com.orion.synevent.fragments.LoginFragment;

import androidx.appcompat.widget.Toolbar;


public class DrawerUtil {

    public static Drawer getDrawer(final Activity activity, Toolbar toolbar) {

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        //if you want to update the items at a later time it is recommended to keep it in a variable

        PrimaryDrawerItem drawerItemManageCalendar = new PrimaryDrawerItem().withIdentifier(1)
                .withName("Calendar").withIcon(GoogleMaterial.Icon.gmd_event);
        PrimaryDrawerItem drawerItemManageEvents = new PrimaryDrawerItem().withIdentifier(2)
                .withName("Events").withIcon(GoogleMaterial.Icon.gmd_book);
        /*PrimaryDrawerItem drawerItemManageGroups = new PrimaryDrawerItem()
                .withIdentifier(3).withName("Group").withIcon(R.drawable.ic_group);*/
        PrimaryDrawerItem drawerItemManageSchedule = new PrimaryDrawerItem()
                .withIdentifier(3).withName("My Agendas").withIcon(GoogleMaterial.Icon.gmd_view_agenda);

        SecondaryDrawerItem drawerItemAbout = new SecondaryDrawerItem().withIdentifier(4)
                .withName("About").withIcon(GoogleMaterial.Icon.gmd_info);
        SecondaryDrawerItem drawerItemSettings = new SecondaryDrawerItem().withIdentifier(5)
                .withName("Settings").withIcon(GoogleMaterial.Icon.gmd_settings);
        SecondaryDrawerItem itemLogOut = new SecondaryDrawerItem().withIdentifier(6)
                                                .withName("Log Out").withIcon(GoogleMaterial.Icon.gmd_exit_to_app);



        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.mb_purple_09)
                .withTextColorRes(R.color.white)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName(mSharedPreferences.getString(Constants.USERNAME,"Username"))
                                .withEmail(mSharedPreferences.getString(Constants.EMAIL,"user@user.com"))
                                .withIcon(R.drawable.ic_person)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();




        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(activity)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withCloseOnClick(true)
                .withSelectedItem(-1)
                .addDrawerItems(
                        drawerItemManageCalendar,
                        drawerItemManageEvents,
                        //drawerItemManageGroups,
                        drawerItemManageSchedule,
                        new DividerDrawerItem(),
                        drawerItemSettings,
                        drawerItemAbout,
                        itemLogOut
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if ( drawerItem.getIdentifier() == 2 ) {
                            if (!activity.getLocalClassName().equals("ListEventActivity")) {
                                Intent intent = new Intent(activity, ListEventActivity.class);
                                view.getContext().startActivity(intent);
                            }
                        }else if ( drawerItem.getIdentifier() == 3 ) {
                            if (!activity.getLocalClassName().equals("CreateAgendaActivity")) {
                                Intent intent = new Intent(activity, CreateAgendaActivity.class);
                                view.getContext().startActivity(intent);
                            }
                        }else if(drawerItem.getIdentifier() == 1){
                            if (!activity.getLocalClassName().equals("MenuActivity"))
                            {
                                Intent intent = new Intent(activity, MenuActivity.class);
                                view.getContext().startActivity(intent);
                            }
                        }else if ( drawerItem.getIdentifier() == 6) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(activity).edit();
                            editor.clear().commit();
                            Intent intent = new Intent(activity, MainActivity.class);
                            view.getContext().startActivity(intent);
                            activity.finish();

                        }
                        return true;
                    }
                })
                .build();
        return result;
    }
}
