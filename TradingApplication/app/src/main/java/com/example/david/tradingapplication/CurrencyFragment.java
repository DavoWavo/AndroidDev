package com.example.david.tradingapplication;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class CurrencyFragment extends Fragment{
    private int page;
    private String pageTitle;

    //recyclerView variables
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    RecyclerView rvCurrency;

    CurrencyObject currencyObject;
    List<CurrencyObject> currencyList;

    public CurrencyFragment() {
        // Required empty public constructor
    }

    public static CurrencyFragment newInstance(int page, String pageTitle) {
        CurrencyFragment currencyFragment = new CurrencyFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("pageNumber", page);
        bundle.putString("pageTitle", pageTitle);
        currencyFragment.setArguments(bundle);
        return currencyFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.page = getArguments().getInt("pageNumber", 0);
        this.pageTitle = getArguments().getString("pageTitle");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_currency_picker, container, false);

        CurrencySelectionActivity activity = (CurrencySelectionActivity) getActivity();
        currencyList = new ArrayList<>();

        if (activity != null) {
            currencyList = activity.currencyList;
        }

        InitalizeRecyclerViewUI(view);

        return view;
    }

    public void InitalizeRecyclerViewUI(View view) {
        rvCurrency = (RecyclerView) view.findViewById(R.id.rv_currency_selection);

        //linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        rvCurrency.setLayoutManager(mLayoutManager);

        //specifying the adapters
        //this passes in the list of items
        mAdapter = new CurrencyRVAdapter(currencyList);
        rvCurrency.setAdapter(mAdapter);
    }

    public void ToggleWatching(int position) {
        currencyList.get(position).toggleWatching();

        CurrencySelectionActivity activity = (CurrencySelectionActivity) getActivity();
        if (activity != null) {
            activity.currencyList = this.currencyList;
        }
    }

    public class CurrencyRVAdapter extends RecyclerView.Adapter<CurrencyFragment.CurrencyRVAdapter.ViewHolder> {
        List<CurrencyObject> mValues;

        //constructed used to pass in the values to display
        public CurrencyRVAdapter(List<CurrencyObject> mValues) {
            this.mValues = mValues;
        }

        @NonNull
        @Override
        public CurrencyFragment.CurrencyRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.currency_selection_row, parent, false);
            CurrencyFragment.CurrencyRVAdapter.ViewHolder vh = new CurrencyFragment.CurrencyRVAdapter.ViewHolder(view);
            return vh;
        }

        //replaces the contents of the view, invoked by the layout manager
        @Override
        public void onBindViewHolder(@NonNull CurrencyFragment.CurrencyRVAdapter.ViewHolder holder, final int position) {
            holder.mCurrencySymbol.setText(mValues.get(position).getSymbol());
            holder.mCurrencyName.setText(mValues.get(position).getName());

            holder.mWatchButton.setChecked(mValues.get(position).watching());

            holder.mWatchButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ToggleWatching(position);
                }
            });
        }

        //returns the number of items in the view
        @Override
        public int getItemCount() {
            return mValues.size();
        }

        //creates the new views, invoked by the layout manager
        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mCurrencySymbol;
            public TextView mCurrencyName;
            public ToggleButton mWatchButton;

            public ViewHolder(View view) {
                super(view);
                mCurrencySymbol = view.findViewById(R.id.currency_row_selection_symbol_tv);
                mCurrencyName = view.findViewById(R.id.currency_row_selection_name_tv);
                mWatchButton = view.findViewById(R.id.currency_row_selection_switch);
            }
        }
    }
}
