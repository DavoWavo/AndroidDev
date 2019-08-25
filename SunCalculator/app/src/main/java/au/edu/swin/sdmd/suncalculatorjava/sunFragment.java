//sending information to a fragment from an activity
//https://www.codexpedia.com/android/passing-data-to-activity-and-fragment-in-android/

//creating the slider fragment thing
//https://guides.codepath.com/android/viewpager-with-fragmentpageradapter

package au.edu.swin.sdmd.suncalculatorjava;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TextView;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import au.edu.swin.sdmd.suncalculatorjava.calc.AstronomicalCalendar;
import au.edu.swin.sdmd.suncalculatorjava.calc.GeoLocation;

public class sunFragment extends Fragment {
    //core instance variables associated with the page
    private String pageTitle;
    private int page;


    int year;
    int monthOfYear;
    int dayOfMonth;
    private Location location;

    private TextView sunriseTV;
    private TextView sunsetTV;
    private TextView locationTVsun;
    private TextView dateHolder;
    private DatePicker dp;

    public sunFragment() {
        // Required empty public constructor
    }

    //Required for the CustomPagerAdapter, assigning page details
    public static sunFragment newInstance(int page, String pageTitle) {
        sunFragment sunFragment = new sunFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("pageNumber", page);
        bundle.putString("pageTitle", pageTitle);
        sunFragment.setArguments(bundle);
        return sunFragment;
    }

    //initializing the information associated with the page
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.page = getArguments().getInt("pageNumber", 0);
        this.pageTitle = getArguments().getString("pageTitle");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sun, container, false);

        //getting initial location from the main activity
        MainActivity activity = (MainActivity) getActivity();
        location = activity.getLocation();

        //listening for the changes in the location variable and grabbing them
        ((MainActivity) getActivity()).passSunVal(new MainActivity.LocationSunChangeListener() {
            @Override
            public void changeSunLocation(Location location) {
                setLocation(location);
            }
        });

        //initalising the view items
        dp = (DatePicker) view.findViewById(R.id.datePicker);
        sunriseTV = (TextView) view.findViewById(R.id.sunriseTimeTV);
        sunsetTV = (TextView) view.findViewById(R.id.sunsetTimeTV);
        locationTVsun= (TextView) view.findViewById(R.id.locationTV_sun);
        dateHolder = (TextView) view.findViewById(R.id.dateHolder_sun);

        initialiseUI();

        return view;
    }

    //initialsing the view values for date and location
    private void initialiseUI() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        dp.init(year,month,day,dateChangeHandler);
        locationTVsun.setText(location.getName());
        setTime(year, month, day);
    }

    //updating the location variable
    public void setLocation(Location location) {
        this.location = location;
        locationTVsun.setText(this.location.getName());

        updateView();
    }

    //updating the time variables
    public void setTime(int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;

        updateView();
    }

    //updating/refreshing the view elements
    private void updateView() {
        TimeZone tz = TimeZone.getTimeZone(location.getTimezone());
        GeoLocation geolocation = new GeoLocation(location.getName(), location.getLatitude(), location.getLongitude(), tz);
        AstronomicalCalendar ac = new AstronomicalCalendar(geolocation);
        ac.getCalendar().set(this.year, this.monthOfYear, this.dayOfMonth);
        Date srise = ac.getSunrise();
        Date sset = ac.getSunset();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        Log.d("SUNRISE Unformatted", srise+"");

        sunriseTV.setText(sdf.format(srise));
        sunsetTV.setText(sdf.format(sset));
        locationTVsun.setText(location.getName() + ", AU");
        dateHolder.setText(this.dayOfMonth + "/" + this.monthOfYear + "/" + this.year);
    }

    //listening for changes in the date wheel
    DatePicker.OnDateChangedListener dateChangeHandler = new DatePicker.OnDateChangedListener()
    {
        public void onDateChanged(DatePicker dp, int year, int monthOfYear, int dayOfMonth)
        {
            setTime(year, monthOfYear, dayOfMonth);
        }
    };
}
