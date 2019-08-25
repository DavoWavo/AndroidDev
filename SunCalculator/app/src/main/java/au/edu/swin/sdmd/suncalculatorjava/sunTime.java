package au.edu.swin.sdmd.suncalculatorjava;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import au.edu.swin.sdmd.suncalculatorjava.calc.AstronomicalCalendar;
import au.edu.swin.sdmd.suncalculatorjava.calc.GeoLocation;

public class sunTime {
    private String sunRise;
    private String sunSet;
    private Location location;
    private int year;
    private int month;
    private int day;
//    private String date;

    private static final String NOT_FOUND = "NOT_FOUND";

    public sunTime() {
        this.sunRise = NOT_FOUND;
        this.sunSet = NOT_FOUND;
        this.location = null;
    }

    public sunTime(Location location, int year, int month, int day) {
        this.location = location;
        this.year = year;
        this.month = month;
        this.day = day;

        getSunTimes(location, year, month, day);
    }

    private void getSunTimes(Location location, int year, int month, int day) {
        TimeZone tz = TimeZone.getTimeZone(location.getTimezone());
        GeoLocation geoLocation = new GeoLocation(location.getName(), location.getLatitude(), location.getLongitude(), tz);
        AstronomicalCalendar ac = new AstronomicalCalendar(geoLocation);
        ac.getCalendar().set(this.year, this.month, this.day);
        Date srise = ac.getSunrise();
        Date sset = ac.getSunset();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        sunRise = sdf.format(srise);
        sunSet = sdf.format(sset);
    }

    public String getSunRise() {
        return sunRise;
    }

    public String getSunSet() {
        return sunSet;
    }

    public Location getLocation() {
        return location;
    }

    public String getDate() {
        return this.day + "/" + this.month + "/" + this.year;
    }
}
