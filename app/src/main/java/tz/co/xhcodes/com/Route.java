package tz.co.xhcodes.com;

public class Route {

	private int routeId;
	private String fromName;
	private String toName;

	public Route(){}

	public Route(int id, String name, String toname){
		this.routeId 	= id;
		this.fromName 	= name;
		this.toName 	= toname;
	}
	
	public void setId(int id){
		this.routeId = id;
	}
	
	public void setName(String name){
		this.fromName = fromName;
	}
	public void setToName(String name){
		this.toName = name;
	}
	
	public int getId(){
		return this.routeId;
	}
	
	public String getFromName(){
		return this.fromName;
	}
	public String getToName(){
		return this.toName;
	}

}
