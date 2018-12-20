package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Model extends Observable {

    File selectedFolderBrowseCollection;
    ReadFile readFile;
    String PathPosting;

    DataCollector dataCollector;
    Map<String,String> dictionaryToLoad = new HashMap<>();
    Map<String,String> citiesToLoad = new HashMap<>();
    Map<String,String> docsFilesToLoad = new HashMap<>();


    public Model(){
        this.dataCollector = new DataCollector();
    }

    public void readCorpus(String FileCorpus, String stopWords, String PathPosting, Boolean withStemming) {
        this.PathPosting = PathPosting;
        long start = System.nanoTime();
        readFile = new ReadFile(FileCorpus,stopWords, PathPosting, withStemming, dataCollector);
        long finish = System.nanoTime();

        long total = finish-start;
        dataCollector.setRunningTime(total/1000000000);
        if(withStemming)
            dataCollector.setPostPath(PathPosting+"\\Y\\");
        else
            dataCollector.setPostPath(PathPosting+"\\N\\");


    }




    public void setFiles(File selectedFolderBrowseCollection) {
        this.selectedFolderBrowseCollection = selectedFolderBrowseCollection;
    }

    /**
     * Load Dictionary to memory
     * @param path
     */
    public void loadDict(String  path){
        List<String> lines = new ArrayList<>();
        dictionaryToLoad = new HashMap<>();

        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(path+"\\Dictionary");
            br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            Collections.sort(lines);
            for(String term:lines){
                String []tmp = term.split(",\\{");
                dictionaryToLoad.put(tmp[0],tmp[1]);
            }

        } catch (IOException e) {

        } finally {
            try {
                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();
            } catch (IOException ex) {

            }
        }

        loadFileDocs(path);


    }

    /**
     * Load fileDocs
     */
    public void loadFileDocs(String path){
        List<String> lines = new ArrayList<>();
        docsFilesToLoad = new HashMap<>();

        BufferedReader br = null;
        FileReader fr = null;

        try{
            fr = new FileReader(path+"\\FileDocs");
            br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

            for(String term:lines){
                String []tmp = term.split(",",2);
                docsFilesToLoad.put(tmp[0],tmp[1]);
            }

        } catch (IOException e) {

        } finally {
            try {
                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();
            } catch (IOException ex) {

            }
        }
        loadCitiesDocs(path);

    }

    /**
     * Load cities
     */
    public void loadCitiesDocs(String path){
        List<String> lines = new ArrayList<>();
        citiesToLoad = new HashMap<>();

        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(path + "\\CitiesPost");
            br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

            for(String term:lines){
                String []tmp = term.split(",");
                citiesToLoad.put(tmp[0],tmp[1]);
            }

        } catch (IOException e) {

        } finally {
            try {
                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();
            } catch (IOException ex) {

            }
        }


        dataCollector.setAllDicToLoad(docsFilesToLoad,dictionaryToLoad,citiesToLoad);


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
