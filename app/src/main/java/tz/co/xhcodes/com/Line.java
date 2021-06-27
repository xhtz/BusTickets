package tz.co.xhcodes.com;

public class Line {

	private int lineId;
	private String line_name;

	public Line(){}

	public Line(int id, String name){
		this.lineId = id;
		this.line_name = name;
	}
	
	public void setId(int id){
		this.lineId = id;
	}
	
	public void setName(String name){
		this.line_name = name;
	}
	
	public int getId(){
		return this.lineId;
	}
	
	public String getName(){
		return this.line_name;
	}

}
