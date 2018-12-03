package Model;

import java.util.Map;

public class DataCollector {

    Map<String,Integer> bigDictionary;

    public void setMap(Map<String,Integer> bigDictionary){
        this.bigDictionary = bigDictionary;
    }

    public Map getMap(){
        return bigDictionary;
    }
}
