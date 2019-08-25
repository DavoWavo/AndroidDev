package com.example.david.tradingapplication;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class UpdateCurrencyRates extends AsyncTask<String, Void, HashMap<String, String>> {
    @Override
    protected HashMap<String, String> doInBackground(String... str_url) {
        HttpHandler httpHandler = new HttpHandler();

        String jsonStr = httpHandler.makeServiceCall(str_url[0]);

        HashMap<String, String> currency;
        currency = new HashMap<>();

        Log.i("JSON_result", "pulled json_data " + jsonStr);
        if (jsonStr != null) {
            try {
                //Don't initialize as zero!
                Double rateUSD = 1.0;
                Double rateOther = 1.0;
                String symbol = "";
                //creating new json object, this will be pulled apart for its values
                JSONObject jsonObject = new JSONObject(jsonStr);
                String timestamp = jsonObject.getString("timestamp");
                JSONObject rates = jsonObject.getJSONObject("rates");
                Iterator<String> iter = rates.keys();
                //iterating through json from fixer.io
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    if (key.equals("USD")) {
                        //getting the current USD to EUR rate
                        rateUSD = rates.getDouble(key);
                    } else {
                        //getting the current query rate to EUR
                        rateOther = rates.getDouble(key);
                        symbol = key;
                    }
                }
                Double queryRateInUSD = rateOther / rateUSD;
                currency.put("symbol", symbol);
                currency.put("last_updated", timestamp);
                currency.put("price", queryRateInUSD.toString());
                //send back the hashmap
                return currency;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return currency;
    }
}
