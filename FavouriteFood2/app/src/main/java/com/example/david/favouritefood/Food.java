package com.example.david.favouritefood;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

public class Food implements Parcelable{
    private String name = "";
    private String url = "";
    private String keywords = "";
    private String date = "";
    private boolean share;
    private String email = "";
    private float rating = 0;
    private int itemNo;

    //constructor
    public Food(String name, String email, int itemNo) {
        this.name = name;
        this.email = email;
        this.share = false;
        this.rating = 0;
        this.itemNo = itemNo;
    }

    //getting information from the parcel
    private Food(Parcel in) {
        name = in.readString();
        url = in.readString();
        keywords = in.readString();
        date = in.readString();
        share = in.readInt() != 0;
        email = in.readString();
        rating = in.readFloat();
        itemNo = in.readInt();
    }

    //storing the information in the parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(keywords);
        dest.writeString(date);
        dest.writeInt(share ? 1 : 0);
        dest.writeString(email);
        dest.writeFloat(rating);
        dest.writeInt(itemNo);
    }

    public static final Parcelable.Creator<Food> CREATOR = new Parcelable.Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel source) {
            return new Food(source);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    //getters and setters for each variable
    public String getName() {
        return name;
    }
    public void setName(String _name)  {
        name = _name;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String _url) {
        url = _url;
    }
    public String getKeywords() {
        return keywords;
    }
    public void setKeywords(String _keywords) {
        keywords = _keywords;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String _date) {
        date = _date;
    }
    public boolean getShare() {
        return share;
    }
    public void setShare(boolean _share) {
        share = _share;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String _email) {
        email = _email;
    }
    public float getRating() {
        return rating;
    }
    public void setRating(float _rating) {
        rating = _rating;
    }
    public int getID() {
        return itemNo;
    }
    public void setID(int _ID) {
        itemNo = _ID;
    }
}
