package au.edu.swin.sdmd.suncalculatorjava;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LocationPickerFragment extends DialogFragment {

    //test location variables
    Location selectedLocation;
    List<Location> LocationList = new ArrayList<Location>();

    //recycler view
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    RecyclerView rvLocations;


    public LocationPickerFragment() {
        //Required empty public constructor
    }

    //read locations list from the mainActivity

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_picker, container, false);

        //this grabs the list of locations from the mainActivity
        MainActivity activity = (MainActivity) getActivity();
        LocationList = activity.getLocationList();

        //initializing RV
        rvLocations = (RecyclerView) view.findViewById(R.id.rv_location);

        //linear layout manager
        Log.d("debugMode", "Creating layoutManager for dialog fragment....");
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        Log.d("debugMode", "The application created the layoutManager for location dialog fragment");
        rvLocations.setLayoutManager(mLayoutManager);

        //specifying the adapters
        mAdapter = new RVAdapter(LocationList);
        rvLocations.setAdapter(mAdapter);

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Location");

        return dialog;
    }

    //RV adapter for locations, don't touch
    public class RVAdapter extends RecyclerView.Adapter<LocationPickerFragment.RVAdapter.ViewHolder> {
        List<Location> mValues;

        public RVAdapter(List<Location> mValues) {
            this.mValues = mValues;
        }

        @NonNull
        @Override
        public RVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.location_row, parent, false);
            view.setOnClickListener(onClickListener);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        //replaces the contents of the view, invoked by the layout manager
        @Override
        public void onBindViewHolder(@NonNull LocationPickerFragment.RVAdapter.ViewHolder holder, int position) {
            Log.d ("debugMode", "onBindHolder getting position in list....");
            Location location = mValues.get(position);
            Log.d ("debugMode", "onBindHolder beginning to assign values to holder....");
            holder.mLocation.setText(location.getName());
            Log.d ("debugMode", "onBindHolder succeeded in assigning to holder in adapter...");
        }

        //return the number of items in the view
        @Override
        public int getItemCount() {
            return mValues.size();
        }

        //creates the new views, invoked by the layout manager
        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mLocation;

            public ViewHolder(View view) {
                super(view);
                mLocation = view.findViewById(R.id.locationTV_row);
            }
        }
    }

    //setting the location within the mainActivity to the selected location
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = rvLocations.getChildLayoutPosition(view);
            //getting reference to main activity
            MainActivity activity = (MainActivity) getActivity();
            activity.setLocation(LocationList.get(position));
            //closing the dialog fragment
            dismiss();
        }
    };
}
