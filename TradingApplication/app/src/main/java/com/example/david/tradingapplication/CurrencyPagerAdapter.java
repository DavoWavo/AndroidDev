package com.example.david.tradingapplication;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CurrencyPagerAdapter extends FragmentPagerAdapter{

    private static int NUM_ITMES = 2;

    public CurrencyPagerAdapter(FragmentManager fragmentManagern) {
        super(fragmentManagern);
    }

    //returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITMES;
    }

    //returns the fragment to display
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return CurrencyFragment.newInstance(0, "currency picker fragment");
            case 1:
                return CryptoFragment.newInstance(0, "crypto picker fragment");
            default:
                return null;
        }
    }

    //returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Currency selection";
            case 1:
                return "Crypto selection";
            default:
                return "Page " + position;
        }
    }
}
