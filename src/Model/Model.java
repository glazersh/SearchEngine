package Model;

import java.io.File;
import java.util.Observable;


public class Model extends Observable {
    File selectedFolderBrowseCollection;
    ReadFile readFile;



    public void readCorpus(String FileCorpus, String stopWords, String PathPosting, Boolean withStemming) {
        readFile = new ReadFile(FileCorpus,stopWords, PathPosting, withStemming);
        long start = System.nanoTime();
        long finish = System.nanoTime();
        System.out.println(finish - start);

    }

    public void setFiles(File selectedFolderBrowseCollection) {
        this.selectedFolderBrowseCollection = selectedFolderBrowseCollection;
    }

    public File getFiles() {
        return selectedFolderBrowseCollection;
    }
}
