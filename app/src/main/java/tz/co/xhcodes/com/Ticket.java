package tz.co.xhcodes.com;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by iwachu on 12/7/16.
 */

public class Ticket {
    public String safari_date;
    public String reportingTime;
    public String departureTime;
    public String seatLabel;
    public String busNumber;
    public String ticketNumber;
    public String passengerName;
    public String farePaid;
    public String fareLabel;
    public String totalFare;
    public String agentName;
    public String anakoshukia;
    public String payment_status;
    public String busName;
    public String payment_token;
    public String paybill_number;
    public String iwachupay_code;
    public String count;
    public String status;
    public String route;
    // Constructor to convert JSON object into a Java class instance
    public Ticket(JSONObject object){
        try
        {
            this.busNumber      = object.getString("busNumber");
            this.safari_date    = object.getString("safari_date");
            this.reportingTime  = object.getString("reportingTime");
            this.departureTime  = object.getString("departureTime");
            this.seatLabel      = object.getString("seatLabel");
            this.ticketNumber   = object.getString("ticketNumber");
            this.passengerName  = object.getString("passengerName");
            this.farePaid       = object.getString("farePaid");
            this.fareLabel      = object.getString("fareLabel");
            this.totalFare      = object.getString("totalFare");
            this.agentName      = object.getString("agentName");
            this.count          = object.getString("count");
            this.anakoshukia    = object.getString("anakoshukia");
            this.payment_status = object.getString("payment_status");
            this.busName        = object.getString("busName");
            this.payment_token  = object.getString("payment_token");
            this.paybill_number = object.getString("paybill_number");
            this.iwachupay_code = object.getString("iwachupay_code");
            this.status         = object.getString("status");
            this.route          = object.getString("route");

        }
        catch(JSONException e)
        {
           System.out.println("ERROR: "+e.getMessage());
        }
    }

    // Factory method to convert an array of JSON objects into a list of objects
    public static ArrayList<Ticket> fromJson(JSONArray jsonObjects) {
        ArrayList<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try
            {
                Ticket ticket = new Ticket(jsonObjects.getJSONObject(i));
                tickets.add(ticket);
            }
            catch (JSONException e)
            {
                System.out.println("ERROR: "+e.getMessage());
            }
        }
        return tickets;
    }

}
