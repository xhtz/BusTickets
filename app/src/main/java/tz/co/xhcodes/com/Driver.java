package tz.co.xhcodes.com;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by iwachu on 12/7/16.
 */

public class Driver {
    public String townName;
    public String regNumber;
    public String phoneNumber;
    public String parkingArea;
    public String driverName;
    public String streetName;
    public String driverCategory;
    public String paymentCode;
    public String days;
    public String regDate;
    public String expireDate;
    public String feeAmount;
    public String companyCode;

    // Constructor to convert JSON object into a Java class instance
    public Driver(JSONObject object){
        try
        {
            this.townName       = object.getString("townName");
            this.regNumber      = object.getString("regNumber");
            this.phoneNumber    = object.getString("phoneNumber");
            this.parkingArea    = object.getString("parkingArea");
            this.driverName     = object.getString("driverName");
            this.streetName     = object.getString("streetName");
            this.driverCategory = object.getString("driverCategory");
            this.paymentCode    = object.getString("paymentCode");
            this.companyCode    = object.getString("companyCode");
            this.feeAmount    = object.getString("feeAmount");
            this.days           = object.getString("days");
            this.regDate        = object.getString("regDate");
            this.expireDate     = object.getString("expireDate");
        }
        catch(JSONException e)
        {
           System.out.println("ERROR: "+e.getMessage());
        }
    }

    // Factory method to convert an array of JSON objects into a list of objects
    public static ArrayList<Driver> fromJson(JSONArray jsonObjects) {
        ArrayList<Driver> drivers = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try
            {
                Driver driver = new Driver(jsonObjects.getJSONObject(i));
                drivers.add(driver);
            }
            catch (JSONException e)
            {
                System.out.println("ERROR: "+e.getMessage());
            }
        }
        return drivers;
    }

}
