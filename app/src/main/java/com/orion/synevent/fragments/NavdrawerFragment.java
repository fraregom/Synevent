package com.orion.synevent.fragments;

import android.os.Bundle;

import com.orion.synevent.R;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

public class NavdrawerFragment extends AppCompatActivity {

    private DrawerLayout drawer;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawer = findViewById(R.id.drawer_layout);

        mToolbar  = findViewById(R.id.activity_toolbar);
        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }
}
