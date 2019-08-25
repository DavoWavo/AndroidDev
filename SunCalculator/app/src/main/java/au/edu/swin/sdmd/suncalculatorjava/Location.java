package au.edu.swin.sdmd.suncalculatorjava;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.TimeZone;

public class Location implements Parcelable{
    private String name;
    private double latitude;
    private double longitude;
    private String timezone;
    //using a string as a boolean because annoyingly no readBoolean();
    private String custom;

    //will need a boolean indicating whether this was user created or not

    public Location(String name, double latitude, double longitude, String timezone, String custom) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
        this.custom = custom;
    }

    private Location(Parcel in) {
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        timezone = in.readString();
        custom = in.readString();
    }

    public static final Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!custom.equals("false")) {
            this.name = name;
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        if (!custom.equals("false")) {
            this.latitude = latitude;
        }
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        if (!custom.equals("false")) {
            this.longitude = longitude;
        }
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        if (!custom.equals("false")) {
            this.timezone = timezone;
        }
    }

    public String getCustom() {
        return custom;
    }

    public String returnFull() {
        return this.name + "," + Double.toString(this.latitude) + "," + Double.toString(this.longitude) + "," + this.timezone + "," + this.custom;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(timezone);
        dest.writeString(custom);
    }
}
