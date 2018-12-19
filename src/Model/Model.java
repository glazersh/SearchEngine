package Model;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Observable;
import java.util.Set;


public class Model extends Observable {

    File selectedFolderBrowseCollection;
    ReadFile readFile;
    Searcher search;
    DataCollector dataCollector;

    public Model(){
        this.dataCollector = new DataCollector();
    }

    public void readCorpus(String FileCorpus, String stopWords, String PathPosting, Boolean withStemming) {
        long start = System.nanoTime();
        readFile = new ReadFile(FileCorpus,stopWords, PathPosting, withStemming, dataCollector);
        long finish = System.nanoTime();

        long total = finish-start;
        dataCollector.setRunningTime(total/1000000000);
        if(withStemming)
            dataCollector.setPostPath(PathPosting+"\\Y\\");
        else
            dataCollector.setPostPath(PathPosting+"\\N\\");
        search = new Searcher(readFile); // check
    }

    public void setFiles(File selectedFolderBrowseCollection) {
        this.selectedFolderBrowseCollection = selectedFolderBrowseCollection;
    }

    /**
     * Load Dictionary to memory
     * @param file
     */
    public void loadDict(File file){
        Indexer.load(file);
    }

    /**
     * Reset all files and data
     */
    public void resetAll() {
        readFile.resetAll();
    }

    //-----GETTERS-----

    public Set getLang(){
        return dataCollector.getLang();
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

    public Map getMap(){
        return dataCollector.getMap();
    }

    public File getFiles() {
        return selectedFolderBrowseCollection;
    }

    public void readQuery(String query) {
        readFile.Parse.parse(query,"","",false);
    }
}
