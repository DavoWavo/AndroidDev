/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.david.tradingapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class StableArrayAdapter extends ArrayAdapter<CurrencyObject> {

    final int INVALID_ID = -1;
    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

    //Saving these references to the layout and string data
    int mRowLayout;
    List<CurrencyObject> mCurrencies;

    public final class ViewHolder {
        TextView tvSymbol;
        TextView tvName;
        TextView tvPrice;
        TextView tvConversion;
    }

    public StableArrayAdapter(Context context, int textViewResourceId, List<CurrencyObject> objects) {
        super(context, textViewResourceId, objects);

        for (int i = 0; i < objects.size(); ++i) {
            //unique string to identify each object in row
            String uniqueString = objects.get(i).getSymbol();

            //putting unique string in list
            mIdMap.put(uniqueString, i);
        }
        //setting the view resource
        mRowLayout = textViewResourceId;
        //setting the list resource
        mCurrencies = objects;
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        String item = getItem(position).getSymbol();
//        Log.d("debugMode", item + " : " + position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the data item for this position
        final CurrencyObject currCurrencyObject = mCurrencies.get(position);

        //check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(mRowLayout, parent, false);

            viewHolder.tvSymbol = (TextView)convertView.findViewById(R.id.dynamic_currency_symbol_tv);
            viewHolder.tvName = (TextView)convertView.findViewById(R.id.dynamic_currency_name_tv);
            viewHolder.tvPrice = (TextView)convertView.findViewById(R.id.dynamic_currency_price_tv);
            viewHolder.tvConversion = (TextView)convertView.findViewById(R.id.dynamic_currency_conversion_tv);
            //cache the viewholder object inside a fresh view
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        //getting the base price of the top cell
        double topBaseRate = mCurrencies.get(0).getPrice();

        //setting colour of the top cell
        if (position != 0) {
            convertView.setBackgroundColor(0xFFFFFFFF);
        } else {
            convertView.setBackgroundColor(0xFF4BAFD1);
        }

        //setting the text within the fields
        viewHolder.tvSymbol.setText(mCurrencies.get(position).getSymbol());
        viewHolder.tvName.setText(mCurrencies.get(position).getName());
        viewHolder.tvPrice.setText(calculateRateFromTopToRate(topBaseRate, mCurrencies.get(position).getPrice()));
        viewHolder.tvConversion.setText(calculateRateFromRateToTop(mCurrencies.get(position).getSymbol(), topBaseRate, mCurrencies.get(position).getPrice()));

        return convertView;
    }

    //calculating the rate to put into the price text field
    public String calculateRateFromTopToRate(double topBaseRate, double baseRate) {
        if (topBaseRate == 0.0) {
            return "Error: refresh in manager";
        } else if (baseRate == 0.0) {
            return "Error: refresh in manager";
        } else {
            double calcRate = (baseRate / topBaseRate);
            DecimalFormat numberFormat = new DecimalFormat("#.00");
            return numberFormat.format(calcRate);
        }
    }

    //calculating the rate to put into the conversion text field
    public String calculateRateFromRateToTop(String baseSymbol, double topBaseRate, double baseRate) {
        if (topBaseRate == 0.0) {
            return "Invalid top base";
        } else if (baseRate == 0.0) {
            return "Invalid rate";
        } else {
            double calcRate = (topBaseRate / baseRate);
            DecimalFormat numberFormat = new DecimalFormat("#.00");
            return "1 " + baseSymbol + " = " + numberFormat.format(calcRate);
        }
    }
}