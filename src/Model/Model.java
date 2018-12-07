package Model;

import java.io.File;
import java.util.Map;
import java.util.Observable;
import java.util.Set;


public class Model extends Observable {

    File selectedFolderBrowseCollection;
    ReadFile readFile;
    DataCollector dataCollector;
    Map<String,Integer>bigDictionary;

    public Model(){
        this.dataCollector = new DataCollector();
    }

    public void readCorpus(String FileCorpus, String stopWords, String PathPosting, Boolean withStemming) {
        long start = System.nanoTime();
        readFile = new ReadFile(FileCorpus,stopWords, PathPosting, withStemming, dataCollector);
        long finish = System.nanoTime();
        long total = finish-start;
        System.out.println(total);
        dataCollector.setRunningTime(total/1000000000);
    }

    public void setFiles(File selectedFolderBrowseCollection) {
        this.selectedFolderBrowseCollection = selectedFolderBrowseCollection;
    }

    public File getFiles() {
        return selectedFolderBrowseCollection;
    }

    public Map getMap(){
        return dataCollector.getMap();
    }
    //function for the language choise
    public Set getLang(){
        return dataCollector.getLang();
    }

    public void loadDict(File file){
        Indexer.load(file);
    }
    //function for the reset button
    public void resetAll() {
        readFile.resetAll();
    }

    public int getNumberOfDocs(){
        return dataCollector.getNumberOfDocs();
    }
    public int getNumberOfTerms(){
        return dataCollector.getNumberOfTerms();
    }
    public long getRunningTime(){
        return dataCollector.getRunningTime();
    }
}
