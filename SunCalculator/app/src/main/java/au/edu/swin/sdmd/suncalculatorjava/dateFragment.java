package au.edu.swin.sdmd.suncalculatorjava;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class dateFragment extends Fragment {
    //core instance variables associated with the page
    private String pageTitle;
    private int page;

    //location variable
    Location location;

    //Ui variables
    private Button startDateButton;
    private Button endDateButton;
    private TextView viewStartDate;
    private TextView viewEndDate;
    private TextView locationTVdate;

    //Date variables
    private int startYear;
    private int startMonth;
    private int startDay;
    private int endYear;
    private int endMonth;
    private int endDay;

    //recyclerView variables
    sunTime sunTime;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    RecyclerView rvSunTime;
    List<sunTime> sunTimes;

    public dateFragment() {
        // Required empty public constructor
    }

    //Required for the CustomPagerAdapter, assigning page details
    public static dateFragment newInstance(int page, String pageTitle) {
        dateFragment dateFragment = new dateFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("pageNumber", page);
        bundle.putString("pageTitle", pageTitle);
        dateFragment.setArguments(bundle);
        return dateFragment;
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
        View view = inflater.inflate(R.layout.fragment_date, container, false);


        //getting the initial location
        MainActivity activity = (MainActivity) getActivity();
        location = activity.getLocation();

        //listening for the changes in the location variable and grabbing them
        ((MainActivity) getActivity()).passDateVal(new MainActivity.LocationDateChangeListener() {
            @Override
            public void changeDateLocation(Location location) {
                setLocation(location);
            }
        });

        //initalizing the suntimes list to fill
        sunTimes = new ArrayList<>();

        initalizeUI(view);
        handleButtonsPressed(view);
        initalizeRecyclerViewUI(view);


        return view;
    }

    //Initialising the UI components
    public void initalizeUI(View view) {
        viewStartDate = (TextView) view.findViewById(R.id.showStartDate);
        viewEndDate = (TextView) view.findViewById(R.id.showEndDate);
        locationTVdate = (TextView) view.findViewById(R.id.locationTV_date);
    }

    //handling the user clicking on the different show dialog buttons to display the datePicker
    public void handleButtonsPressed(View view) {
        startDateButton = (Button) view.findViewById(R.id.picStartDate);
        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStartDatePicker();
            }
        });

        endDateButton = (Button) view.findViewById(R.id.picEndDate);
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEndDatePicker();
            }
        });
    }

    //handling the start date dialog picker
    public void showStartDatePicker() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setCallBack(onStartDate);
        datePickerFragment.show(getChildFragmentManager(), "date picker");
    }
    DatePickerDialog.OnDateSetListener onStartDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            startYear = year;
            startMonth = monthOfYear;
            startDay = dayOfMonth;
            viewStartDate.setText(startDay + "/" + startMonth + "/" + startYear);
            setSunTimeRange();
        }
    };

    //handling the end date dialog picker
    public void showEndDatePicker() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setCallBack(onEndDate);
        datePickerFragment.show(getChildFragmentManager(), "date picker");
    }
    DatePickerDialog.OnDateSetListener onEndDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            endYear = year;
            endMonth = monthOfYear;
            endDay = dayOfMonth;
            viewEndDate.setText(endDay + "/" + endMonth + "/" + endYear);
            setSunTimeRange();
        }
    };

    //initializing the variables associated with the recycler view
    public void initalizeRecyclerViewUI(View view) {
        rvSunTime = (RecyclerView) view.findViewById(R.id.rv_suntimes);

        //linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        Log.d("debugMode", "The application created the layoutManager");
        rvSunTime.setLayoutManager(mLayoutManager);

        //specifying the adapters
        //this passes in the list of items
        mAdapter = new RVAdapter(sunTimes);
        rvSunTime.setAdapter(mAdapter);
    }

    //checks the range of dates set and calling the creation of list items
    private void setSunTimeRange() {
        int tempYear, tempMonth, tempDay;
        long numberOfDays = 0;
        long numberOfMillis = 0;
        Calendar cal = Calendar.getInstance();

        //checking to ensure both date settings are set
        if (startYear != 0 || startMonth != 0 || startDay != 0) {
            if (endYear != 0 || endMonth != 0 || endDay != 0) {

                //getting the number of milliseconds between the two dates
                Calendar startDateValue = Calendar.getInstance();
                startDateValue.set(startYear, startMonth, startDay);
                Calendar endDateValue = Calendar.getInstance();
                endDateValue.set(endYear, endMonth, endDay);

                //finding difference in time between two dates
                numberOfMillis = endDateValue.getTimeInMillis() - startDateValue.getTimeInMillis();
                //converting the amount of time in millis to days
                numberOfDays = TimeUnit.MILLISECONDS.toDays(numberOfMillis);

                //set a maximum number of days to 30
                if (numberOfDays >= 0) {
                    if (numberOfDays > 30) {
                        numberOfDays = 30;
                        Toast.makeText(getActivity(), Long.toString(numberOfDays) + " days selected, maximum reached", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), Long.toString(numberOfDays + 1) + " day(s) selected", Toast.LENGTH_SHORT).show();
                    }

                    //clearing the previous list of all entities
                    sunTimes.clear();

                    tempDay = startDay;
                    tempMonth = startMonth;
                    tempYear = startYear;

                    //need to find the value for the maximum date
                    for (int i = 0; i <= numberOfDays; i++) {
                        //if the current month is within the year
                        if (tempMonth < 13) {
                            //if the current day is within the month
                            if (tempDay < cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                                createSunTimesItem(tempYear, tempMonth, tempDay);
                                tempDay++;
                            } else {
                                tempDay = 1;
                                tempMonth++;
                                createSunTimesItem(tempYear, tempMonth, tempDay);
                            }
                        } else {
                            tempDay = 1;
                            tempMonth = 0;
                            tempYear++;
                            createSunTimesItem(tempYear, tempMonth, tempDay);
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "Error: cannot go backwards", Toast.LENGTH_SHORT).show();
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    //creates a new sunTime item and adds it to the list
    private void createSunTimesItem(int year, int month, int day) {
        sunTime = new sunTime(this.location, year, month, day);
        sunTimes.add(sunTime);
    }

    //updating the location variable
    public void setLocation(Location location) {
        this.location = location;
        locationTVdate.setText(this.location.getName() + ", AU");
        setSunTimeRange();
    }

    //recycler view adapter, dont touch me!
    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {
        List<sunTime> mValues;

        public RVAdapter(List<sunTime> mValues) {
            this.mValues = mValues;
        }

        @NonNull
        @Override
        public RVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.suntime_row, parent, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        //replaces the contents of the view, invoked by the layout manager
        @Override
        public void onBindViewHolder(@NonNull RVAdapter.ViewHolder holder, int position) {
            Log.d ("debugMode", "onBindHolder getting position in list....");
            sunTime sunTime = mValues.get(position);
            Log.d ("debugMode", "onBindHolder beginning to assign values to holder....");
            holder.mSunRise.setText(sunTime.getSunRise());
            holder.mSunSet.setText(sunTime.getSunSet());
            holder.mDate.setText(sunTime.getDate());
            Log.d ("debugMode", "onBindHolder succeeded in assigning to holder in adapter...");
        }

        //return the number of items in the view
        @Override
        public int getItemCount() {
            return mValues.size();
        }

        //creates the new views, invoked by the layout manager
        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mSunRise;
            public TextView mSunSet;
            public TextView mDate;

            public ViewHolder(View view) {
                super(view);
                mSunRise = view.findViewById(R.id.sunriseTimeTV_row);
                mSunSet = view.findViewById(R.id.sunsetTimeTV_row);
                mDate = view.findViewById(R.id.dateTV_row);
            }
        }
    }
}
