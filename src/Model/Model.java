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
        System.out.println(finish - start);
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
}
