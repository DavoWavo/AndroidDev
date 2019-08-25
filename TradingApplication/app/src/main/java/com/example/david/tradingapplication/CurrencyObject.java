package com.example.david.tradingapplication;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import javax.xml.transform.dom.DOMLocator;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class CurrencyObject implements Parcelable {
    private String id, name, symbol, type;
    private int lastUpdated;
    private double price;
    private boolean watching;

    private static String cryptoType = "CRYPTO_CURRENCY";
    private static String globalType = "GLOBAL_CURRENCY";

    //api raw
    //this grabs just the relevent crypto from coinMarketCap using the id
    final static String coin_market_cap_api = "https://api.coinmarketcap.com/v2/ticker/";
    //grabs the currency price in EUR of both the objects currency symbol and the currency USD price in EUR
    final static String fixer_io_api = "http://data.fixer.io/api/latest?access_key=1f7cda60716271c7cb6863e321a65464&symbols=USD,";


    //constructor for crypto currencies
    public CurrencyObject(String id, String name, String symbol, int lastUpdated, double price, String watching) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.type = cryptoType;
        this.lastUpdated = lastUpdated;
        this.price = price;
        this.watching = Boolean.parseBoolean(watching);
    }

    //constructor for global currencies
    public CurrencyObject(String name, String symbol, int lastUpdated, double price, String watching) {
        this.id = "null";
        this.name = name;
        this.symbol = symbol;
        this.type = globalType;
        this.lastUpdated = lastUpdated;
        this.price = price;
        this.watching = Boolean.parseBoolean(watching);
    }

    //parcelable constructor
    protected CurrencyObject(Parcel in) {
        id = in.readString();
        name = in.readString();
        symbol = in.readString();
        type = in.readString();
        lastUpdated = in.readInt();
        price = in.readDouble();
        watching = in.readByte() != 0;
    }

    //parcelable Creator
    public static final Creator<CurrencyObject> CREATOR = new Creator<CurrencyObject>() {
        @Override
        public CurrencyObject createFromParcel(Parcel in) {
            return new CurrencyObject(in);
        }

        @Override
        public CurrencyObject[] newArray(int size) {
            return new CurrencyObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(symbol);
        dest.writeString(type);
        dest.writeInt(lastUpdated);
        dest.writeDouble(price);
        dest.writeByte((byte) (watching ? 1 : 0));
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public String getPriceString() {
        return "$" + String.valueOf(this.price) + " USD";
    }

    public double getPrice() {
        return this.price;
    }

    public Boolean watching() {
        return this.watching;
    }

    //used to simplify the coding process of writing to file
    public String ReturnFull() {
        String tempWatchString;
        if (this.watching)
            tempWatchString = "true";
        else
            tempWatchString = "false";
        return this.id+"/"+this.name+"/"+this.symbol+"/"+Integer.toString(this.lastUpdated)+"/"+Double.toString(this.price)+"/"+tempWatchString;
    }

    //toggles whether the user is watching a currency or not
    public void toggleWatching() {
        this.watching = !this.watching;
    }

    public String getLastUpdatedTime() {
        Date date = new Date(this.lastUpdated*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+11"));
        return sdf.format(date);
    }

    public int getLastUpdated() {
        return this.lastUpdated;
    }

    //updates the information related to a currency
    public void Update(ProgressDialog progressDialog) {
        String typeCall = this.type;
        switch (typeCall) {
            case "CRYPTO_CURRENCY":
                updateCrypto(progressDialog);
                break;
            case "GLOBAL_CURRENCY":
                updateGlobal();
                break;
            default:
                Log.e("Error", "undefined type");
        }
    }

    private void updateCrypto(ProgressDialog progressDialog) {
        String apiUrl = coin_market_cap_api + this.id + "/";
        try {
            UpdateCryptoRate task = new UpdateCryptoRate();
            task.setProgressBar(progressDialog);
            HashMap<String, String> updateMap = task.execute(apiUrl).get();

            Log.d("testingUpdate", updateMap.get("id") + ", " + updateMap.get("name"));
            this.lastUpdated = Integer.parseInt(updateMap.get("last_updated"));
            this.price = Double.parseDouble(updateMap.get("price"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void updateGlobal() {
        String apiURL = fixer_io_api + this.symbol;
        try {
            HashMap<String, String> updateMap = new UpdateCurrencyRates().execute(apiURL).get();
            this.lastUpdated = Integer.parseInt(updateMap.get("last_updated"));
            this.price = Double.parseDouble(updateMap.get("price"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    //yahoo finance api call to get the historical information
    public LinkedHashMap<Integer, Double> GetHistoricalInfo(String range) {
        LinkedHashMap<Integer, Double> historicalClosing = new LinkedHashMap<Integer, Double>();
        try {
            //checking whether currency is of type crypto
            if (this.type.equals(cryptoType)) {
                historicalClosing = new GetHistoricalInfo(this.symbol+"-USD", range).execute().get();
            } else {
                historicalClosing = new GetHistoricalInfo(this.symbol+"USD=X", range).execute().get();
            }
            return historicalClosing;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return historicalClosing;
    }
}
