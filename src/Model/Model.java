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
    Map<String,String> dictionaryToLoad ;
    Map<String,String> citiesToLoad ;
    Map<String,String> docsFilesToLoad ;


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

        String dictionary = "Dictionary";
        dictionaryToLoad = new HashMap<>();
        loadDict(path,dictionary, dictionaryToLoad,",\\{");

        String FileDocs = "FileDocs";
        docsFilesToLoad = new HashMap<>();
        loadDict(path,FileDocs, docsFilesToLoad, ",");


        String CitiesPost = "CitiesPost";
        citiesToLoad = new HashMap<>();
        loadDict(path,CitiesPost, citiesToLoad, ",");

        dataCollector.setAllDicToLoad(docsFilesToLoad,dictionaryToLoad,citiesToLoad);
        dataCollector.setPostPath(path);
    }



    /**
     * Load Dictionary to memory
     * @param path
     */
    public void loadDict(String  path,String kind, Map<String,String> dictionary, String split){
        List<String> lines = new ArrayList<>();

        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(path+kind);
            br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            if(kind.equals("FileDocs")){
                List<String>docCounter = new ArrayList<>();
                for(String term:lines){
                    String []tmp = term.split(split,2);
                    if(tmp.length==2)
                        docsFilesToLoad.put(tmp[0],tmp[1]);
                    else{
                        docCounter.add(tmp[0]);
                    }
                }
                dataCollector.setNumberOfDocs(Integer.parseInt(docCounter.get(0)));
                dataCollector.setAverageNumOfDocs(Double.parseDouble(docCounter.get(1)));
            }else {
                for (String term : lines) {
                    String[] tmp = term.split(split);
                    dictionary.put(tmp[0], tmp[1]);
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

    public List<String> getDocsName() {
        return dataCollector.getDocs();
    }
}
