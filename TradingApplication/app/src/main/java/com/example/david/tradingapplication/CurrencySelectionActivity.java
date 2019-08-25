package com.example.david.tradingapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.icu.util.Currency;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CurrencySelectionActivity extends AppCompatActivity {
    public static final String RETURN_REFRESH = "RETURN_REFRESH_KEY";

    CurrencyPagerAdapter currencyPagerAdapter;
    FragmentPagerAdapter adapterViewPager;

    List<CurrencyObject> cryptoList = new ArrayList<CurrencyObject>();
    List<CurrencyObject> currencyList = new ArrayList<CurrencyObject>();

    //api http query that grabs all the information relating to crypto currencies
    final static String coin_market_cap_api = "https://api.coinmarketcap.com/v2/ticker/";

    //api http query that grabs all the information relating to global currencies
    final static String currency_converter_api = "https://free.currencyconverterapi.com/api/v6/currencies";

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_selection);

        //clear the lists to ensure information is not dirtied and accidently erased
        cryptoList.clear();
        currencyList.clear();

        createCurrencyLists(CurrenciesInputStream());

        //creating the viewPager adapter and the fragments
        ViewPager viewPager = (ViewPager)findViewById(R.id.currency_pager);
        adapterViewPager = new CurrencyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);

        initializeToolbar();
    }

    public void initializeToolbar() {
        //implementing the toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Currency Selector");

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    //getting the array of strings from currencies input stream and parcing them and creating objects
    private void createCurrencyLists(ArrayList<String> currencies) {
        for (String line : currencies) {
            String[] entry = line.split("/");
            if (entry.length == 6) {
                //determining whether the currency being read is crypto or not
                if (entry[0].equals("null")) {
                    //creating a new global currency entity
                    currencyList.add(new CurrencyObject(entry[1], entry[2], Integer.parseInt(entry[3]), Double.parseDouble(entry[4]), entry[5]));
                } else {
                    //creating a new crypto currency entity
                    cryptoList.add(new CurrencyObject(entry[0], entry[1], entry[2], Integer.parseInt(entry[3]), Double.parseDouble(entry[4]), entry[5]));
                }
            }
        }
    }

    //reading the input stream and creating array of currencies from api
    public ArrayList<String> CurrenciesInputStream() {
        ArrayList<String> currencies = new ArrayList<String>();
        AssetManager assetManager = getAssets();

        File file = new File(getFilesDir(), "Currencies.txt");

        //if the file doesn't exist, make one with the contents of the api call
        if (!file.exists()) {
            Log.i("currencyFile", "Currencies.txt doesn't exist, creating new file");
            try {
                List<Map<String, String>> CryptoList = new GetCryptoList().execute(coin_market_cap_api).get();
                Map<String, String> CryptoMap;

                List<Map<String, String>> CurrencyList = new GetCurrencyList().execute(currency_converter_api).get();
                Map<String, String> CurrencyMap;

                ArrayList<String> cryptoReturnList = ReadCurrenciesFromAPI(CryptoList);
                currencies.addAll(cryptoReturnList);

                ArrayList<String> currencyReturnList = ReadCurrenciesFromAPI(CurrencyList);
                currencies.addAll(currencyReturnList);
                return currencies;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            //the file has been found, therefore just read from the file the values
            Log.i("currencyFile", "Currencies.txt exist, reading from file");

            currencies = new ReadWriteCurrencies(this).ReadFile();

            return currencies;
        }
        return currencies;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        saveChangesToFile();
        returnNotification();
        super.finish();
    }

    public void saveChangesToFile() {
        ReadWriteCurrencies readWriteCurrencies = new ReadWriteCurrencies(this);

        //deleting the previous contents from the file
        readWriteCurrencies.ClearFile();

        readWriteCurrencies.WriteListToFile(cryptoList);
        readWriteCurrencies.WriteListToFile(currencyList);
    }

    private void returnNotification() {
        final Intent intent = new Intent();
        intent.putExtra(RETURN_REFRESH, 0);
        setResult(Activity.RESULT_OK, intent);
    }

    //this reads the information gained from the api and then returns it to be used in the creation of the file
    public ArrayList<String> ReadCurrenciesFromAPI(List<Map<String, String>> saveCurrencyList) {
        Map<String, String> saveCurrencyMap;
        ArrayList<String> currencies = new ArrayList<String>();
        String line;

        //getting all the crypto rates from the web using an api
        for(int i = 0; i < saveCurrencyList.size(); i++) {
            //assigning the crypto to that map
            saveCurrencyMap = saveCurrencyList.get(i);

            FileOutputStream outputStream = null;
            try {
                outputStream = openFileOutput("Currencies.txt", MODE_APPEND);

                //creating a line in the currencies file.
                line =  saveCurrencyMap.get("id")+"/"
                        +saveCurrencyMap.get("name")+"/"
                        +saveCurrencyMap.get("symbol")+"/"
                        +saveCurrencyMap.get("last_updated")+"/"
                        +saveCurrencyMap.get("price")+"/"
                        +"false"+"\n";

                byte[] bytes = line.getBytes();

                outputStream.write(bytes);
                outputStream.close();
                currencies.add(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return currencies;
    }
}
