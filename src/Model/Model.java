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

    public Set getLang(){
        return dataCollector.getLang();
    }

    public void loadDict(File file){
        Posting.load(file);
    }
}
