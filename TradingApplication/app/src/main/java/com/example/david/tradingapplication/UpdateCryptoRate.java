package com.example.david.tradingapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;


import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//This uses the coin market cap api exclusively to grab information related to a crypto
public class UpdateCryptoRate extends AsyncTask <String,Void,HashMap<String, String>> {             // Parameters - Progress unit - Result

    ProgressDialog bar;

    public void setProgressBar(ProgressDialog bar) {
        this.bar = bar;
    }

    @Override
    protected void onPreExecute() {
        bar.show();
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected HashMap<String, String> doInBackground(String... str_url) {
        //TODO 5: add a delay to ensure that the full api pull is completed
        //TODO 6: add a progress dial to show that the information is being pulled
        HttpHandler httpHandler = new HttpHandler();

        String jsonStr = httpHandler.makeServiceCall(str_url[0]);

        HashMap<String, String> crypto;
        crypto = new HashMap<>();

        Log.i("JSON_result", "pulled json_data " + jsonStr);
        if (jsonStr != null) {
            try {
                //creating new json object, this will be pulled apart for its values
                JSONObject jsonObject = new JSONObject(jsonStr);

                // Getting JSON object node
                JSONObject coin_finance = jsonObject.getJSONObject("data");

                //pulling apart each object for the given key
                String id = coin_finance.getString("id");
                String name = coin_finance.getString("name");
                String symbol = coin_finance.getString("symbol");
                String lastUpdated = coin_finance.getString("last_updated");
                JSONObject quotes = coin_finance.getJSONObject("quotes");
                JSONObject currency = quotes.getJSONObject("USD");
                String price = currency.getString("price");
                Log.d("testingIterator", "id: " + id + ", name: " + name + ", symbol: " + symbol + ", last_updated: " + lastUpdated + ", price: " + price);
                crypto.put("id", id);
                crypto.put("name", name);
                crypto.put("symbol", symbol);
                crypto.put("last_updated", lastUpdated);
                crypto.put("price", price);
                //send back the hashmap
                return crypto;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return crypto;
    }

    @Override
    protected void onPostExecute(HashMap<String, String> crypto)
    {
        bar.dismiss();
        super.onPostExecute(crypto);
    }
}