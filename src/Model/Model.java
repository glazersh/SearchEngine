package Model;

import java.io.File;

public class Model {
    File selectedFolderBrowseCollection;
    ReadFile readFile;




    public void readCorpus() {
        readFile = new ReadFile(selectedFolderBrowseCollection.getPath());
        long start = System.nanoTime();
        long finish = System.nanoTime();
        System.out.println(finish - start);
        readFile.convertTo();

    }
    public void setFiles(File selectedFolderBrowseCollection) {
        this.selectedFolderBrowseCollection = selectedFolderBrowseCollection;
    }
}
