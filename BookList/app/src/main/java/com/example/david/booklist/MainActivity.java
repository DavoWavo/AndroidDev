package com.example.david.booklist;

import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    Library mLibrary;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    RecyclerView rvLibrary;
    List<String> mBookNames;

    Book book;
    List<Book> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createBookList(R.drawable.book, "title 1", "author 1");
        initialiseUI();
    }

    private void createBookList(View view) {
        bookList.add(new Book())
    }
    void initialiseUI() {
        mLibrary = Library.get(getApplicationContext());
        mBookNames = mLibrary.getBookNames();

        //recyclerviews layout manager
        rvLibrary = findViewById(R.id.rvLibrary); //c
        mLayoutManager = new LinearLayoutManager(this); //c
        rvLibrary.setLayoutManager(mLayoutManager); //c
        //recyclerviews adapter
        mAdapter = new RVAdapter(mBookNames);
        rvLibrary.setAdapter(mAdapter);
    }

    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {
        List<String> mValues;

        public RVAdapter(List<String> values) {
            mValues = values;
        }

        @NonNull
        @Override
        public RVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_row, parent, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull RVAdapter.ViewHolder holder, int position) {
            String name = mValues.get(position);
            holder.mTitle.setText(name);
            if(name.startsWith("Dune")) {
                holder.mRating.setText(R.string.dune_rating);
                holder.mImage.setImageResource(R.drawable.book);
            } else if (name.startsWith("Nineteen Eighty-Four")) {
                holder.mRating.setText(R.string.nineteen_eighty_four_rating);
                holder.mImage.setImageResource(R.drawable.book);
            } else if (name.startsWith("Ender's game")) {
                holder.mRating.setText(R.string.enders_game_rating);
                holder.mImage.setImageResource(R.drawable.book);
            }  else if (name.startsWith("The Left Hand of Darkness")) {
                holder.mRating.setText(R.string.the_left_hand_of_darkness_rating);
                holder.mImage.setImageResource(R.drawable.book);
            } else if (name.startsWith("The Time Machine")) {
                holder.mRating.setText(R.string.the_time_machine_rating);
                holder.mImage.setImageResource(R.drawable.book);
            } else if (name.startsWith("Fahrenheit 451")) {
                holder.mRating.setText(R.string.fahrenheit_451_rating);
                holder.mImage.setImageResource(R.drawable.book);
            } else if (name.startsWith("The Forever War")) {
                holder.mRating.setText(R.string.the_forever_war_rating);
                holder.mImage.setImageResource(R.drawable.book);
            } else if (name.startsWith("Hyperion")) {
                holder.mRating.setText(R.string.hyperion_rating);
                holder.mImage.setImageResource(R.drawable.book);
            } else if (name.startsWith("Brave New World")) {
                holder.mRating.setText(R.string.brave_new_world_rating);
                holder.mImage.setImageResource(R.drawable.book);
            } else if (name.startsWith("The Hitchhiker's Guide to the Galaxy")) {
                holder.mRating.setText(R.string.the_hitchhikers_guide_to_the_galaxy_rating);
                holder.mImage.setImageResource(R.drawable.book);
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTitle;
            public ImageView mImage;
            public TextView mRating;

            public ViewHolder(View view) {
                super(view);
                mTitle = view.findViewById(R.id.item_title);
                mImage = view.findViewById(R.id.item_image);
                mRating = view.findViewById(R.id.item_rating);
            }
        }
    }
}
