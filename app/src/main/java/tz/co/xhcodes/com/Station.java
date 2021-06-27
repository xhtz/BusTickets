package tz.co.xhcodes.com;

public class Station {

	private int stationId;
	private String station_name;

	public Station(){}

	public Station(int id, String name){
		this.stationId = id;
		this.station_name = name;
	}
	
	public void setId(int id){
		this.stationId = id;
	}
	
	public void setName(String name){
		this.station_name = name;
	}
	
	public int getId(){
		return this.stationId;
	}
	
	public String getName(){
		return this.station_name;
	}

}
