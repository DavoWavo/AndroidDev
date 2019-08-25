package au.edu.swin.sdmd.suncalculatorjava;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

public class CustomPagerAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 2;

    public CustomPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    //returns the total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    //returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: //fragment #0 - this will return the first fragment
                return sunFragment.newInstance(0, "sun time picker");
            case 1: //fragment #1 - this will return the second fragment
                return dateFragment.newInstance(1, "sun time list");
            default:
                return null;
        }
    }

    //returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }
}
