package com.example.david.tradingapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

public class CurrencyConverterActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private Toolbar mToolbar;

    ArrayList<CurrencyObject> currencyList = new ArrayList<CurrencyObject>();

    ReadWriteCurrencies readWriteCurrencies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);

        readWriteCurrencies = new ReadWriteCurrencies(this);

        initializeToolbar();

        //getting the list of currency objects from the file
        CreateWatchCurrencyList(readWriteCurrencies.ReadFile());

        //this is redundant
        ArrayList<String>tempList = new ArrayList<String>();
        for (int i = 0; i < currencyList.size(); i++) {
            tempList.add(currencyList.get(i).getName());
        }

        StableArrayAdapter adapter = new StableArrayAdapter(this, R.layout.dynamic_view_row, currencyList);
        DynamicListView listView = (DynamicListView) findViewById(R.id.dynamic_listview);

        listView.setCurrencyList(currencyList);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        Log.d("debugMode", Long.toString(adapter.getItemId(0)));
    }

    public void CreateWatchCurrencyList(ArrayList<String> currencies) {
        for (String line : currencies) {
            String[] entry = line.split("/");
            if (entry.length == 6) {
                if (entry[5].equals("true")) {
                    if (entry[0].equals("null")) {
                        //creating a global currency object and adding it to the list
                        currencyList.add(new CurrencyObject(entry[1], entry[2], Integer.parseInt(entry[3]), Double.parseDouble(entry[4]), entry[5]));
                    } else {
                        //creating a crypto currency object and adding it to the list
                        currencyList.add(new CurrencyObject(entry[0], entry[1], entry[2], Integer.parseInt(entry[3]), Double.parseDouble(entry[4]), entry[5]));
                    }
                }
            }
        }
    }

    private void initializeToolbar() {
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setTitle(R.string.title_activity_currency_converter);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.main_menu_button:
                        Intent intentMain = new Intent(CurrencyConverterActivity.this, MainActivity.class);
                        startActivity(intentMain);
                        break;
                    case R.id.currency_converter_nav:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.currency_tracker_nav:
                        Intent intentConverter = new Intent(CurrencyConverterActivity.this, ManageCurrenciesActivity.class);
                        startActivity(intentConverter);
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
