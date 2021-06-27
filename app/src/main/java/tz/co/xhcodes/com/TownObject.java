package tz.co.xhcodes.com;

/**
 * Created by iwachu on 12/24/17.
 */

public class TownObject {
    String id;
    String name;
    public TownObject(String townName, String townId){
        this.name = townName;
        this.id = townId;
    }
    public void setName(String townName){
        this.name = townName;
    }

    public void setId(String townId){
        this.id = townId;
    }

    public String getName(){
        return this.name;
    }

    public String getId(){
        return this.id;
    }

}
