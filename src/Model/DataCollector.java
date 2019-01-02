package Model;

import javax.print.Doc;
import java.io.File;
import java.util.*;

public class DataCollector {

    Map<String,Integer> bigDictionary;
    Set<String> lang;
    int numberOfDocs;
    int numberOfTerms;
    long runningTime = 0;


    double averageNumOfDocs;

    private Map<String,String[]> dictionaryToLoad;
    private Map<String,String> citiesToLoad;
    private Map<String,String[]> docsFilesToLoad;
    public boolean refresh;

    public boolean getRefresh(){
        return refresh;
    }

    public Map<String, String[]> getEntityToLoad() {
        return entityToLoad;
    }

    private Map<String,String[]> entityToLoad;

    private List<DocData>docsName;

    /////////////added
    List <String>entityDoc;
    String queryToSavePath;
    private  Map<String,List> IDWithDocs = new HashMap<>();
    private List<String> ID = new ArrayList<>();
    int counter =1 ;


    public void insertToEntitiesDoc(String str){
        entityDoc.add(str);
    }


    public Map<String, String[]> getDictionaryToLoad() {
        return dictionaryToLoad;
    }


    public Map<String, String> getCitiesToLoad() {
        return citiesToLoad;
    }


    public Map<String, String[]> getDocsFilesToLoad() {
        return docsFilesToLoad;
    }

    public double getAverageNumOfDocs() {
        return averageNumOfDocs;
    }

    public void setAverageNumOfDocs(double averageNumOfDocs) {
        this.averageNumOfDocs = averageNumOfDocs;
    }

    public void resetIDs(){
        ID.clear();
        IDWithDocs.clear();
        counter=1;
        refresh = true;
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



    public void setAllDicToLoad(Map<String, String[]> docsFilesToLoad, Map<String, String[]> dictionaryToLoad, Map<String, String> citiesToLoad, Map<String,String[]> entity) {
        this.dictionaryToLoad=dictionaryToLoad;
        this.docsFilesToLoad=docsFilesToLoad;
        this.citiesToLoad=citiesToLoad;
        this.entityToLoad = entity;

    }

    public List<DocData>getDocs(){
        return docsName;
    }

    public void setRelevantDocs(List<DocData>relevantDocs){
        docsName = relevantDocs;
    }

    public void setEntityPost(List<String> entityDoc) {
        this.entityDoc = entityDoc;
    }

    public List<String> getEntities() {
        return entityDoc;
    }

    public void setQueryToSavePath(String queryToSavePath) {
        this.queryToSavePath = queryToSavePath;

    }
    public String getQueryToSavePath(){
        return queryToSavePath;
    }

    public void setIDWithDocs(List<DocData> IDWithDocs) {
        this.IDWithDocs.put((counter++)+"",IDWithDocs);
    }

    public Map<String, List> getIDWithDocs() {
        return IDWithDocs;
    }

    public List<DocData>getCurrent(int index){
        List<DocData>tmp = IDWithDocs.get(index+"");
        return tmp;
    }

    public void addID(String s) {
        ID.add(s);
    }
    public List<String> getIDs(){
        return ID;
    }
}
