package tz.co.xhcodes.com;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by iwachu on 12/7/16.
 */

public class Saving {
    public String rdate;
    public String receipt;
    public String amount;
    // Constructor to convert JSON object into a Java class instance
    public Saving(JSONObject object){
        try
        {
            this.rdate      = object.getString("rdate");
            this.receipt    = object.getString("receipt");
            this.amount     = object.getString("amount");

        }
        catch(JSONException e)
        {
           System.out.println("ERROR: "+e.getMessage());
        }
    }

    // Factory method to convert an array of JSON objects into a list of objects
    public static ArrayList<Saving> fromJson(JSONArray jsonObjects) {
        ArrayList<Saving> buses = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try
            {
                Saving bus = new Saving(jsonObjects.getJSONObject(i));
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
