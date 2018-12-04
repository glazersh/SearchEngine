package Model;

import java.util.Map;
import java.util.Set;

public class DataCollector {

    Map<String,Integer> bigDictionary;
    Set<String> lang;

    public void setMap(Map<String,Integer> bigDictionary){
        this.bigDictionary = bigDictionary;
    }

    public Map getMap(){
        return bigDictionary;
    }

    public void setLang(Set<String> lang) {
        this.lang = lang;
    }
    public Set getLang(){
        return lang;
    }
}
