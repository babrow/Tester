package com.babrow.tester.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.babrow.tester.App;
import com.babrow.tester.R;
import com.babrow.tester.model.Account;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int selectedMenuId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState != null) {
            selectedMenuId = savedInstanceState.getInt("selectedMenuId", -1);
        }

        MenuItem item = navigationView.getMenu().findItem(R.id.nav_account);
        Account account = App.getAccount();
        if (account != null) {
            item.setTitle(account.getEmail());
        }

        setTestDescription();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectedMenuId = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        setTestDescription();
        return true;
    }

    private void setTestDescription() {
        String testDescription = null;
        switch (selectedMenuId) {
            case R.id.nav_test1_tapping:
                testDescription = getResources().getString(R.string.test1_tapping_description);
                break;
            case R.id.nav_test2_traffic:
                testDescription = getResources().getString(R.string.test2_traffic_description);
                break;
            case R.id.nav_share:
            case R.id.nav_send:
            case R.id.nav_logout:
                logout();
                break;
        }

        TextView label = (TextView) findViewById(R.id.main_caption);
        label.setText(testDescription == null ?
                getResources().getString(R.string.hello_caption) : testDescription);

        Button button = (Button) findViewById(R.id.test_start);
        button.setVisibility(testDescription != null ? View.VISIBLE : View.INVISIBLE);
    }

    private void logout() {
        App.flushAccount();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt("selectedMenuId", selectedMenuId);
    }

    public void runTest(View view) {
        Intent intent = null;
        switch (selectedMenuId) {
            case R.id.nav_test1_tapping:
                intent = new Intent(this, Test1Activity.class);
                break;
            case R.id.nav_test2_traffic:
                intent = new Intent(this, Test2Activity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
