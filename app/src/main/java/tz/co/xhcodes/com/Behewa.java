package tz.co.xhcodes.com;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by iwachu on 12/7/16.
 */

public class Behewa {
    public String bogieId;
    public String bogie_number;
    public String timetable_id;
    public String no_seats;
    public String from_station;
    public String from_station_name;
    public String to_station;
    public String to_station_name;
    public String line_id;
    public String adult_fare;
    public String adult_fare_amount;
    public String child_fare;
    public String child_fare_amount;
    public String line_name;
    public String treni_class;
    public String safari_date;
    public String arrive_time;
    public String departure_time;
    public String no_available_seats;
    // Constructor to convert JSON object into a Java class instance
    public Behewa(JSONObject object){
        try
        {
            this.bogieId             = object.getString("bogieId");
            this.bogie_number        = object.getString("bogie_number");
            this.timetable_id        = object.getString("timetable_id");
            this.no_seats            = object.getString("no_seats");
            this.no_available_seats  = object.getString("no_available_seats");
            this.from_station        = object.getString("from_station");
            this.from_station_name   = object.getString("from_station_name");
            this.to_station             = object.getString("to_station");
            this.to_station_name        = object.getString("to_station_name");
            this.line_id                = object.getString("line_id");
            this.adult_fare             = object.getString("adult_fare");
            this.child_fare             = object.getString("child_fare");
            this.line_name              = object.getString("line_name");
            this.treni_class            = object.getString("treni_class");
            this.safari_date            = object.getString("safari_date");
            this.arrive_time            = object.getString("arrive_time");
            this.departure_time         = object.getString("departure_time");
            this.adult_fare_amount      = object.getString("adult_fare_amount");
            this.child_fare_amount      = object.getString("child_fare_amount");
        }
        catch(JSONException e)
        {
           System.out.println("ERROR: "+e.getMessage());
        }
    }

    // Factory method to convert an array of JSON objects into a list of objects
    public static ArrayList<Behewa> fromJson(JSONArray jsonObjects) {
        ArrayList<Behewa> buses = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try
            {
                Behewa bus = new Behewa(jsonObjects.getJSONObject(i));
                buses.add(bus);
            }
            catch (JSONException e)
            {
                System.out.println("ERROR: "+e.getMessage());
            }
        }
        return buses;
    }

}
