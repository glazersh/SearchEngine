package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Model extends Observable {

    File selectedFolderBrowseCollection;
    ReadFile readFile;
    Parse parse;
    String PathPosting;

    DataCollector dataCollector;
    Map<String,String[]> dictionaryToLoad ;
    Map<String,String> citiesToLoad ;
    Map<String,String[]> docsFilesToLoad ;
    Map<String,String[]>entitesToLoad;


    public Model(){
        this.dataCollector = new DataCollector();
    }

    public void readCorpus(String FileCorpus, String stopWords, String PathPosting, Boolean withStemming) {
        this.PathPosting = PathPosting;
        long start = System.nanoTime();
        readFile = new ReadFile(FileCorpus,stopWords, PathPosting, withStemming, dataCollector);
        readFile.start();
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

    public void loadAllDictionary(String path){


        loadDocs(path+"FileDocs");
        loadDict(path+"Dictionary");
        loadCities(path+"CitiesPost");
        loadEntity(path+"Entities");

        dataCollector.setAllDicToLoad(docsFilesToLoad,dictionaryToLoad,citiesToLoad,entitesToLoad);
        dataCollector.setPostPath(path);
    }

    private void loadEntity(String path) {
        entitesToLoad = new HashMap<>();

        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            String line;


            while ((line = br.readLine()) != null) {
                String []tmp = line.split(",\\{");
                if(tmp.length==2) {
                    String[] entity = tmp[1].split(":");
                    entitesToLoad.put(tmp[0], entity);
                }
            }



        } catch (IOException e) {

        } finally {
            try {
                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();
            } catch (IOException ex) { }
        }
    }

    /**
     * load cites
     * @param path
     */
    private void loadCities(String path) {
        citiesToLoad = new HashMap<>();

        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            String line;


            while ((line = br.readLine()) != null) {
                String []tmp = line.split(",",2);
                citiesToLoad.put(tmp[0],tmp[1]);
            }



        } catch (IOException e) {

        } finally {
            try {
                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();
            } catch (IOException ex) { }
        }
    }

    /**
     * load docs
     * @param path
     */
    private void loadDocs(String path){
        docsFilesToLoad = new HashMap<>();

        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            String line;

            List<String>docCounter = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                String []tmp = line.split(",",2);
                if(tmp.length==2) {
                    String[] docInfo = tmp[1].split(",");
                    docsFilesToLoad.put(tmp[0], docInfo);
                }
                else{
                    docCounter.add(tmp[0]);
                }
            }
            dataCollector.setNumberOfDocs(Integer.parseInt(docCounter.get(0)));
            dataCollector.setAverageNumOfDocs(Double.parseDouble(docCounter.get(1)));


        } catch (IOException e) {

        } finally {
            try {
                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();
            } catch (IOException ex) { }
        }
    }

    /**
     * Load Dictionary to memory
     * @param path
     */
    private void loadDict(String  path){
        dictionaryToLoad = new HashMap<>();

        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null) {
                String[] tmp = line.split(",\\{");
                String[] docInfo = tmp[1].split(":");
                dictionaryToLoad.put(tmp[0],docInfo);
            }


        } catch (IOException e) {

        } finally {
            try {
                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();
            } catch (IOException ex) { }
        }
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

    public void readQuery(String query, String stopWords, boolean withstemming) {
        if(parse==null)
            parse = new Parse(stopWords, PathPosting, withstemming, dataCollector);

        parse.parse(query,"","",false);
    }

    public List<DocData> getDocsName() {
        return dataCollector.getDocs();
    }
}
