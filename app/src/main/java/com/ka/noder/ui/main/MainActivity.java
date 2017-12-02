package com.ka.noder.ui.main;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ka.noder.R;
import com.ka.noder.provider.Contract;
import com.ka.noder.ui.login.LoginActivity;
import com.ka.noder.utils.AccountUtil;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String PREFERENCES_KEY = "Noder_preferences";

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private Account account;

    private void routTo(){
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);
        boolean isRegistered = preferences.getBoolean("isRegistered", false);
        if (!isRegistered){
            Log.e("TAG_Main", "No, I`m not register: " + isRegistered);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            Log.e("TAG_Main", "Yes, I`m registered: " + isRegistered);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        routTo();
        setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_open_drawer, R.string.navigation_close_drawer
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        account = AccountUtil.getInstance(this);

        View view = navigationView.getHeaderView(0);
        TextView accName = (TextView) view.findViewById(R.id.account_name);
        accName.setText(account.name);

        Log.e("TAG_Main", "oC metka 1");
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void navSync(View view) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        Log.e("TAG_Frg1", "Sync notes, Account name: " + account.name + ", hash: " + account.hashCode());

        ContentResolver.requestSync(account, Contract.CONTENT_AUTHORITY, bundle);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
