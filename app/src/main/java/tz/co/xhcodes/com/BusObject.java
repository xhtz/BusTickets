package tz.co.xhcodes.com;

/**
 * Created by iwachu on 12/24/17.
 */

public class BusObject {
    String busNumber;
    String busName;
    public BusObject(String bus_number, String bus_name){
        this.busNumber = bus_number;
        this.busName = bus_name;
    }
    public void setBusNumber(String busNumber){
        this.busNumber = busNumber;
    }

    public void setBusName(String name){
        this.busName = name;
    }

    public String getBusNumber(){
        return this.busNumber;
    }

    public String getBusName(){
        return this.busName;
    }

}
