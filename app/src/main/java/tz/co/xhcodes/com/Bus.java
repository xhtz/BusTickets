package tz.co.xhcodes.com;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by iwachu on 12/7/16.
 */

public class Bus {
    public String busNumber;
    public String routeId;
    public String fromName;
    public String toName;
    public String busName;
    public String recordDate;
    public String busLogo;
    public String seatsAvailable;
    public String busType;
    public String noSeats;
    public String reportingTime;
    public String departureTime;
    public String companyName;
    public String companyAddress;
    public String companyPhone;
    public String TIN;
    public String timetableId;
    public String companyId;
    public String iwachupay_code;
    public String routeFare;
    public String fareAmount;
    public String paybill_number;
    // Constructor to convert JSON object into a Java class instance
    public Bus(JSONObject object){
        try
        {
            this.busNumber      = object.getString("busNumber");
            this.routeId        = object.getString("route_id");
            this.busName        = object.getString("busName");
            this.recordDate     = object.getString("recordDate");
            this.busLogo        = object.getString("logo_name");
            this.fromName       = object.getString("fromName");
            this.toName         = object.getString("toName");
            this.seatsAvailable = object.getString("seatsAvailable");
            this.noSeats        = object.getString("noSeats");
            this.busType        = object.getString("busType");
            this.reportingTime  = object.getString("reportingTime");
            this.departureTime  = object.getString("departureTime");
            this.companyName    = object.getString("companyName");
            this.companyAddress = object.getString("companyAddress");
            this.companyPhone   = object.getString("companyPhone");
            this.TIN            = object.getString("TIN");
            this.timetableId    = object.getString("timetableId");
            this.companyId      = object.getString("companyId");
            this.iwachupay_code = object.getString("iwachupay_code");
            this.fareAmount     = object.getString("fareAmount");
            this.routeFare     = object.getString("routeFare");
            this.paybill_number = object.getString("paybill_number");

        }
        catch(JSONException e)
        {
           System.out.println("ERROR: "+e.getMessage());
        }
    }

    // Factory method to convert an array of JSON objects into a list of objects
    public static ArrayList<Bus> fromJson(JSONArray jsonObjects) {
        ArrayList<Bus> buses = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try
            {
                Bus bus = new Bus(jsonObjects.getJSONObject(i));
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
