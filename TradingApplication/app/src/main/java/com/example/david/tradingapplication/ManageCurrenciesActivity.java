package com.example.david.tradingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class ManageCurrenciesActivity extends AppCompatActivity {

    public static final String CURRENCY = "CURRENCY_KEY";
    public static final int KEY = 0x01;
    public static final int KEY_SELECTOR = 0x02;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private Toolbar mToolbar;

    //this list is used to display the current watch list to the user
    List<CurrencyObject> currencyList = new ArrayList<CurrencyObject>();
    //this list is used to save the updated changes to memory
    List<CurrencyObject> entireList = new ArrayList<CurrencyObject>();

    //recyclerView variables
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    RecyclerView rvCurrency;

    ReadWriteCurrencies readWriteCurrencies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("debugMode", "onCreate launched");
        setContentView(R.layout.activity_manage_currencies);

        initializeToolbar();

        readWriteCurrencies = new ReadWriteCurrencies(this);

        //clear the lists to ensure information is not dirtied and accidently erased
        currencyList.clear();
        entireList.clear();

        createCurrencyLists(readWriteCurrencies.ReadFile());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchListStock = new Intent(view.getContext(), CurrencySelectionActivity.class);
                startActivityForResult(launchListStock, KEY_SELECTOR);
                Snackbar.make(view, "Launching currency selection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        InitalizeRecyclerViewUI();
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
                        Intent intentMain = new Intent(ManageCurrenciesActivity.this, MainActivity.class);
                        startActivity(intentMain);
                        break;
                    case R.id.currency_converter_nav:
                        Intent intentConverter = new Intent(ManageCurrenciesActivity.this, CurrencyConverterActivity.class);
                        startActivity(intentConverter);
                        break;
                    case R.id.currency_tracker_nav:
                        drawerLayout.closeDrawer(GravityCompat.START);
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

    public void InitalizeRecyclerViewUI() {
        rvCurrency = (RecyclerView) findViewById(R.id.rv_manage_currencies);

        //linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        rvCurrency.setLayoutManager(mLayoutManager);

        //specifying the adapters
        //this passes in the list of items
        mAdapter = new ManageCurrenciesRVAdapter(currencyList);
        rvCurrency.setAdapter(mAdapter);
    }

    //read currencies from the lists gotten from the ReadWrite class
    private void createCurrencyLists(ArrayList<String> currencies) {
        for (String line : currencies) {
            String[] entry = line.split("/");
            if (entry.length == 6) {
                if (entry[5].equals("true")) {
                    if (entry[0].equals("null")) {
                        //creating a new global currency entity to display
                        currencyList.add(new CurrencyObject(entry[1], entry[2], Integer.parseInt(entry[3]), Double.parseDouble(entry[4]), entry[5]));
                        //creating a new global currency entity to save
                        entireList.add(new CurrencyObject(entry[1], entry[2], Integer.parseInt(entry[3]), Double.parseDouble(entry[4]), entry[5]));
                    } else {
                        //creating a new crypto currency entity to display
                        currencyList.add(new CurrencyObject(entry[0], entry[1], entry[2], Integer.parseInt(entry[3]), Double.parseDouble(entry[4]), entry[5]));
                        //create a new crypto currency entity to save
                        entireList.add(new CurrencyObject(entry[0], entry[1], entry[2], Integer.parseInt(entry[3]), Double.parseDouble(entry[4]), entry[5]));
                    }
                } else {
                    if (entry[0].equals("null")) {
                        //creating a new global currency entity to save
                        entireList.add(new CurrencyObject(entry[1], entry[2], Integer.parseInt(entry[3]), Double.parseDouble(entry[4]), entry[5]));
                    } else {
                        //create a new crypto currency entity to save
                        entireList.add(new CurrencyObject(entry[0], entry[1], entry[2], Integer.parseInt(entry[3]), Double.parseDouble(entry[4]), entry[5]));
                    }
                }
            }
        }
    }

    //gets the changes made from the currency view and saves them to the file accordingly
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("debugMode", "onActivityResult launched");
        switch (requestCode) {
            case KEY:
                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        Log.i("INTENT", "Intent empty");
                    } else {
                        final CurrencyObject currencyObject = data.getParcelableExtra(CurrencyViewActivity.RETURN_CURRENCY);
                        //searching for and replacing the updated object within the list
                        for (int i = 0; i < currencyList.size(); i++) {
                            if (currencyList.get(i).getSymbol().equals(currencyObject.getSymbol())) {
                                currencyList.set(i, currencyObject);
                            }
                        }
                        //searching for and replacing the updated object with the list, this list will be saved to memory
                        for (int i = 0; i < entireList.size(); i++) {
                            if (entireList.get(i).getSymbol().equals(currencyObject.getSymbol())) {
                                entireList.set(i, currencyObject);
                            }
                        }
                        mAdapter.notifyDataSetChanged();

                        readWriteCurrencies.ClearFile();
                        readWriteCurrencies.WriteListToFile(entireList);
                    }
                } else {
                    Log.i("INTENT", "Result not okay, code: " + resultCode);
                    Log.d("debugMode", "Result not okay, code: " + resultCode);
                }
                break;
            case KEY_SELECTOR:
                //navigating back from the selection activity
                if (resultCode == RESULT_OK) {
                    Log.d("debugMode", "returning from currency selector");
                    //clearing both lists
                    currencyList.clear();
                    entireList.clear();

                    //recreating lists with updated list
                    createCurrencyLists(readWriteCurrencies.ReadFile());
                    mAdapter.notifyDataSetChanged();
                } else {
                    Log.i("INTENT", "Result not okay, code: " + resultCode);
                    Log.d("debugMode", "Result not okay, code: " + resultCode);
                }
                break;
            default:
                Log.i("INTENT", "Key doesn't match, key: " + requestCode);
                Log.d("debugMode", "Key doesn't match, key: " + requestCode);
        }
    }

    //the user has returned to the activity and everything is still visible.
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debugMode", "onResume launched");
//        mAdapter.notifyDataSetChanged();
    }

    //recycler view class handler
    public class ManageCurrenciesRVAdapter extends RecyclerView.Adapter<ManageCurrenciesRVAdapter.ViewHolder> {
        List<CurrencyObject> mValues;

        //constructed used to pass in the values to display
        public ManageCurrenciesRVAdapter(List<CurrencyObject> mValues) {
            this.mValues = mValues;
        }

        @NonNull
        @Override
        public ManageCurrenciesRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_currencies_row, parent, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        //replaces the contents of the view, invoked by the layout manager
        @Override
        public void onBindViewHolder(@NonNull ManageCurrenciesRVAdapter.ViewHolder holder, final int position) {
            holder.mCurrencySymbol.setText(mValues.get(position).getSymbol());
            holder.mCurrencyName.setText(mValues.get(position).getName());

            holder.mSelectCurrency.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCurrencyDetails(mValues.get(position));
                }
            });
        }

        private void showCurrencyDetails(CurrencyObject currencyObject) {
            final Intent detailsIntent = new Intent(getApplicationContext(), CurrencyViewActivity.class);
            Log.d("debugMode", "Selected item: " + currencyObject.ReturnFull());
            detailsIntent.putExtra(CURRENCY, currencyObject);
            startActivityForResult(detailsIntent, KEY);
        }

        //returns the number of items in the view
        @Override
        public int getItemCount() {
            return mValues.size();
        }

        //creates the new views, invoked by the layout manager
        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mCurrencySymbol;
            public TextView mCurrencyName;
            public Button mSelectCurrency;

            public ViewHolder(View view) {
                super(view);
                mCurrencySymbol = view.findViewById(R.id.manage_currencies_symbol_tv);
                mCurrencyName = view.findViewById(R.id.manage_currencies_name_tv);
                mSelectCurrency = view.findViewById(R.id.manage_currencies_row_button);
            }
        }
    }

}
