package Model;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataCollector {

    Map<String,Integer> bigDictionary;
    Set<String> lang;
    int numberOfDocs;
    int numberOfTerms;
    long runningTime = 0;


    double averageNumOfDocs;

    private Map<String,String> dictionaryToLoad;
    private Map<String,String> citiesToLoad;
    private Map<String,String> docsFilesToLoad;

    private List<String>docsName;

    /////////////added
    List <String>entityDoc;


    public void insertToEntitiesDoc(String str){
        entityDoc.add(str);
    }


    public Map<String, String> getDictionaryToLoad() {
        return dictionaryToLoad;
    }


    public Map<String, String> getCitiesToLoad() {
        return citiesToLoad;
    }


    public Map<String, String> getDocsFilesToLoad() {
        return docsFilesToLoad;
    }

    public double getAverageNumOfDocs() {
        return averageNumOfDocs;
    }

    public void setAverageNumOfDocs(double averageNumOfDocs) {
        this.averageNumOfDocs = averageNumOfDocs;
    }


    public String getPostPath() {
        return postPath;
    }

    public void setPostPath(String postPath) {
        this.postPath = postPath;
    }

    String postPath;

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



    public void setAllDicToLoad(Map<String, String> docsFilesToLoad, Map<String, String> dictionaryToLoad, Map<String, String> citiesToLoad) {
        this.dictionaryToLoad=dictionaryToLoad;
        this.docsFilesToLoad=docsFilesToLoad;
        this.citiesToLoad=citiesToLoad;
    }

    public List<String>getDocs(){
        return docsName;
    }

    public void setRelevantDocs(List<String>relevantDocs){
        docsName = relevantDocs;
    }



    public void setEntityPost(List<String> entityDoc) {
        this.entityDoc = entityDoc;

    }

    public List<String> getEntities() {
        return entityDoc;
    }
}
