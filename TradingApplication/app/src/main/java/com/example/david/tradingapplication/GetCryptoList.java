package com.example.david.tradingapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//THIS IS STILL WORK IN PROGRESS, CORRECT DOCUMENTATION STILL TO COME
public class GetCryptoList extends AsyncTask <String,Void,List<Map<String, String>>> {             // Parameters - Progress unit - Result

    //TODO 5: add a delay to ensure that the full api pull is completed
    //TODO 6: add a progress dial to show that the information is being pulled

    @Override
    protected List<Map<String, String>> doInBackground(String... str_url) {
        HttpHandler httpHandler = new HttpHandler();

        String jsonStr = httpHandler.makeServiceCall(str_url[0]);
        int indexKey = 0;

        HashMap<String, String> crypto;
        List<Map<String, String>> cryptoList = new ArrayList<Map<String, String>>();

        Log.i("JSON_result", "pulled json_data " + jsonStr);
        if (jsonStr != null) {
            try {
                //creating new json object, this will be pulled apart for its values
                JSONObject jsonObject = new JSONObject(jsonStr);

                // Getting JSON object node
                JSONObject coin_finance = jsonObject.getJSONObject("data");

                //iterating through all of the objects within the json file
                Iterator<String> iter = coin_finance.keys();
                while(iter.hasNext()) {
                    //getting the id key, which contains all the objects
                    String key = (String) iter.next();
                    JSONObject jsonCryptoObject = coin_finance.getJSONObject(key);
                    //pulling apart each object for the given key
                    String id = jsonCryptoObject.getString("id");
                    String name = jsonCryptoObject.getString("name");
                    String symbol = jsonCryptoObject.getString("symbol");
                    String lastUpdated = jsonCryptoObject.getString("last_updated");
                    JSONObject quotes = jsonCryptoObject.getJSONObject("quotes");
                    JSONObject currency = quotes.getJSONObject("USD");
                    String price = currency.getString("price");
                    Log.d("testingIterator", "id: " + id + ", name: " + name + ", symbol: " + symbol + ", last_updated: " + lastUpdated + ", price: " + price);
                    //creating a new hashmap to put the contents of the pulled apart json
                    crypto = new HashMap<>();
                    crypto.put("id", id);
                    crypto.put("name", name);
                    crypto.put("symbol", symbol);
                    crypto.put("last_updated", lastUpdated);
                    crypto.put("price", price);
                    //adding the hashmap to the list of hashmaps with the index
                    cryptoList.add(indexKey, crypto);
                    //increment the number of items and index within the hashmap
                    indexKey++;
                }
                //send back the hashmap
                return cryptoList;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return cryptoList;
    }

    @Override
    protected void onPostExecute(List<Map<String, String>> cryptoList){
        super.onPostExecute(cryptoList);
    }
}