/**
 * api list
 *
 * CURRENCY CONVERTER API
 * -- grabs a list of every currency, their symbol, name, etc.
 * -- this would be great to run once and then save to a file, the only issue is this doesn't give you their current prices
 * https://free.currencyconverterapi.com/api/v6/currencies
 * source (https://free.currencyconverterapi.com/)
 * --use this to grab all the information about every country then save the information to a file
 *
 * OPEN RATES
 * -- another free currency api that is covered in this blog post
 * source (https://davidwalsh.name/openrates)
 * --its based on fixer.io but with more support and no API key required (yay!)
 * -- only returns 32 of the most popular currencies
 * https://api.exchangeratesapi.io/latest?base=USD
 * -- maybe don't use this API as its not great for anything as it doesn't return enough
 *
 * FIXER
 * -- has a free version that is very power and most other API's are based off it
 * -- can grab a range of different currencies with a single API call.
 * -- doesn't return the names of the currencies, only their symbol and price
 * http://data.fixer.io/api/latest?access_key=1f7cda60716271c7cb6863e321a65464
 * --has a limited amount of API called permitted per month
 * --this could be used in the main page activity as only the symbol and price is needed and can be done in one api call
 */


package com.example.david.tradingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button navigateCurrency;
    Button navigateConverter;

    CurrencyObject currencyObject;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeToolbar();

        navigateCurrency = findViewById(R.id.navigate_currency);
        navigateCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchSingleStock = new Intent(view.getContext(), ManageCurrenciesActivity.class);
                startActivity(launchSingleStock);
            }
        });
    }

    private void initializeToolbar() {
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setTitle(R.string.title_activity_manage_currencies);

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
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.currency_converter_nav:
                        Intent intentConverter = new Intent(MainActivity.this, CurrencyConverterActivity.class);
                        startActivity(intentConverter);
                        break;
                    case R.id.currency_tracker_nav:
                        Intent intentMain = new Intent(MainActivity.this, ManageCurrenciesActivity.class);
                        startActivity(intentMain);
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