package Model;

import java.io.File;
import java.util.Map;
import java.util.Set;

public class DataCollector {

    Map<String,Integer> bigDictionary;
    Set<String> lang;
    int numberOfDocs;
    int numberOfTerms;
    long runningTime = 0;

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

    public void setNumberOfTerms(int number){
        this.numberOfTerms = number;
    }

    public void setNumberOfDocs(int number){
        this.numberOfDocs = number;
    }

    public void setRunningTime(long number){
        this.runningTime = number;
    }

    public int getNumberOfDocs(){
        return numberOfDocs;
    }

    public int getNumberOfTerms(){
        return numberOfTerms;
    }

    public long getRunningTime(){
        return runningTime;
    }
}
