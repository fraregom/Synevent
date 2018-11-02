package com.orion.synevent.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
import com.orion.synevent.ListEventActivity;
import com.orion.synevent.MenuActivity;
import com.orion.synevent.R;

import androidx.appcompat.widget.Toolbar;


public class DrawerUtil {

    public static void getDrawer(final Activity activity, Toolbar toolbar) {
        //if you want to update the items at a later time it is recommended to keep it in a variable

        PrimaryDrawerItem drawerItemManageCalendar = new PrimaryDrawerItem().withIdentifier(1)
                .withName("My Calendar").withIcon(R.drawable.calendar_clock);
        PrimaryDrawerItem drawerItemManageEvents = new PrimaryDrawerItem().withIdentifier(2)
                .withName("Events").withIcon(R.drawable.ic_event);
        PrimaryDrawerItem drawerItemManageGroups = new PrimaryDrawerItem()
                .withIdentifier(3).withName("Group").withIcon(R.drawable.ic_group);
        PrimaryDrawerItem drawerItemManageSchedule = new PrimaryDrawerItem()
                .withIdentifier(4).withName("Schedule").withIcon(R.drawable.ic_schedule);


        SecondaryDrawerItem drawerItemSettings = new SecondaryDrawerItem().withIdentifier(4)
                .withName("Settings").withIcon(GoogleMaterial.Icon.gmd_settings);
        SecondaryDrawerItem drawerItemAbout = new SecondaryDrawerItem().withIdentifier(5)
                .withName("About").withIcon(GoogleMaterial.Icon.gmd_info);


        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.mb_purple_09)
                .withTextColorRes(R.color.white)
                .addProfiles(
                        new ProfileDrawerItem().withName("Synevent User").withEmail("syvent@email.com").withIcon(R.drawable.ic_person)
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
                        drawerItemManageGroups,
                        drawerItemManageSchedule,
                        new DividerDrawerItem(),
                        drawerItemAbout,
                        drawerItemSettings
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if ( drawerItem.getIdentifier() == 2 ) {
                            // load tournament screen
                            Intent intent = new Intent(activity, ListEventActivity.class);
                            view.getContext().startActivity(intent);
                            activity.finish();
                        }else if(drawerItem.getIdentifier() == 1){
                            if (!activity.getLocalClassName().equals("MenuActivity"))
                            {
                                Intent intent = new Intent(activity, MenuActivity.class);
                                view.getContext().startActivity(intent);
                                activity.finish();
                            }
                        }
                        return true;
                    }
                })
                .build();
    }
}
