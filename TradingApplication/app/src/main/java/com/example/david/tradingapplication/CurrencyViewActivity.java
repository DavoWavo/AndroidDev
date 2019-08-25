package com.example.david.tradingapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class CurrencyViewActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    public static final String RETURN_CURRENCY = "RETURN_KEY";

    private Toolbar mToolbar;

    GraphView graphView;

    TextView currencyNameTV;
    TextView currencySymbolTV;
    TextView currencyLastUpdatedTV;
    TextView currencyPriceTv;

    Button historicalRates;

    Spinner ratesSpinner;

    CurrencyObject currencyObject;

    private ProgressDialog progressDialog;

    private Handler handler = new Handler();

    //minutes in day
    //?range=1d&interval=1m

    //hours in 5 days
    //?range=5d&interval=1h

    //days in month
    //?range=1mo&interval=1d

    //5 days in 6 months
    //?range=6mo&interval=5d

    //months in year
    //?range=1y&interval=1mo
    private String ratesRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_view);

        initializeUI();
        initializeToolbar();

        //initalize the range to being every minute in the last day
        ratesRange = "?range=1d&interval=1m";

        historicalRates = findViewById(R.id.historical_rates);
        historicalRates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                historicalRates();
            }
        });
    }

    private void initializeToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle(currencyObject.getSymbol() + " details");

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    //creating/ inflating the toolbar into view
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    //handling the user interation with the toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_buton:
                refresh();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initializeUI() {
        Intent intent = getIntent();

        graphView = (GraphView) findViewById(R.id.graph);
        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);

        currencyObject = intent.getParcelableExtra(ManageCurrenciesActivity.CURRENCY);

        currencyNameTV = (TextView) findViewById(R.id.currency_name_tv);
        currencySymbolTV = (TextView) findViewById(R.id.currency_symbol_tv);
        currencyLastUpdatedTV = (TextView) findViewById(R.id.currency_lastUpdated_tv);
        currencyPriceTv = (TextView) findViewById(R.id.currency_price_tv);

        //Checking if currency object has been refreshed in the past
        if (currencyObject.getPrice() == 0.0) {
            refresh();
        } else {
            currencyNameTV.setText(currencyObject.getName());
            currencySymbolTV.setText(currencyObject.getSymbol());
            currencyLastUpdatedTV.setText(currencyObject.getLastUpdatedTime());
            currencyPriceTv.setText(currencyObject.getPriceString());
        }

        ratesSpinner = (Spinner) findViewById(R.id.rates_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.rates_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ratesSpinner.setAdapter(adapter);
        ratesSpinner.setOnItemSelectedListener(this);

    }


    //setting the range which the historical rate range will be read from
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        switch (text) {
            case "1D":
                ratesRange = "?range=1d&interval=1m";
            break;
            case "5D":
                ratesRange = "?range=5d&interval=1h";
                break;
            case "1M":
                ratesRange = "?range=1mo&interval=1d";
                break;
            case "6M":
                ratesRange = "?range=6mo&interval=5d";
                break;
            case "1Y":
                ratesRange = "?range=1y&interval=1mo";
                break;
            default:
                ratesRange = "?range=1d&interval=1m";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void refresh() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        if(ni != null && ni.isConnected()){
            if (currencyObject.getPrice() == 0.0) {
                Toast.makeText(this, "Performing initial currency pull", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Performing currency pull", Toast.LENGTH_SHORT).show();
            }
            currencyObject.Update(progressDialog); //this will update the current parameters for the currency object

            //updating the view elements when currencyObject is refreshed
            currencyNameTV.setText(currencyObject.getName());
            currencySymbolTV.setText(currencyObject.getSymbol());
            currencyLastUpdatedTV.setText(currencyObject.getLastUpdatedTime());
            currencyPriceTv.setText(currencyObject.getPriceString());
        } else {
            Toast.makeText(getApplicationContext(),"Network Connection failed. Currency rates not updated!",Toast.LENGTH_LONG).show();
        }
    }

    //performing the pull from the api and assigning it to the graph
    public void historicalRates() {
        LinkedHashMap<Integer, Double> historicalData = currencyObject.GetHistoricalInfo(ratesRange);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

        //getting the first entry in the LinkedHashMap
        Map.Entry<Integer, Double> firstHistoricalDataEntry = historicalData.entrySet().iterator().next();
        Date firstDate = new Date(firstHistoricalDataEntry.getKey()*1000L);

        //setting up for getting the last entry in the LinkedHashMap
        int lastHistoricalDataEntry = 0;

        //setting up variables to hold min and max values
        double minPrice = 1000.0;
        double maxPrice = 1.0;

        Iterator iter = historicalData.entrySet().iterator();
        while(iter.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) iter.next();

            //setting the date for the graph
            int dateUnix = Integer.parseInt(pair.getKey().toString());
            Date date = new Date(dateUnix*1000L);

            double value = Double.parseDouble(pair.getValue().toString());
            Log.d("debugMode", "X: " + date + ", Y: " + value);
            if (value != 0.0) {
                series.appendData(new DataPoint(date, value), true, 1000);

                //if the value is greater than the previous minPrice, then new value set
                if (value > maxPrice) {
                    maxPrice = value;
                } else if (value < minPrice) {
                    minPrice = value;
                }
            }

            //holding the value of the last updated date
            lastHistoricalDataEntry = dateUnix;
        }

        //setting the last date
        Date lastDate = new Date(lastHistoricalDataEntry*1000L);
        //clearing the previous series that gets stored
        graphView.removeAllSeries();
        graphView.clearFocus();

        //adding new serires and setting the range of the graph
        graphView.addSeries(series);

        graphView.getViewport().setMinX(firstDate.getTime());
        graphView.getViewport().setMaxX(lastDate.getTime());

        graphView.getViewport().setMinY(minPrice);
        graphView.getViewport().setMaxY(maxPrice);

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setYAxisBoundsManual(true);

        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
    }

    @Override
    public void onBackPressed() {
        returnChanges();

        super.finish();
    }

    private void returnChanges() {
        final Intent intent = new Intent();
        Log.d("debugMode", "last updated: " + currencyObject.getLastUpdatedTime() + ", price: " + currencyObject.getPrice());
        intent.putExtra(RETURN_CURRENCY, currencyObject);
        setResult(Activity.RESULT_OK, intent);
    }
}
