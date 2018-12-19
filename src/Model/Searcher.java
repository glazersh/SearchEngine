package Model;

import Model.Term.ATerm;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Searcher {

    private TmpSearcher tmpSearcher;
    private DataCollector dc;

    private List<ATerm> termsInQuery;
    private Set docsRelevant;
    private String path;


    Map<String,String> dictionaryToLoad;
    Map<String,String> docsFilesToLoad ;
    Map<String,String> citiesToLoad ;


    public Searcher(DataCollector dataCollector){
        this.dc=dataCollector;
        termsInQuery = new ArrayList<>();
        this.path = dc.getPostPath();
        loadCitiesDocs();
        loadDictionary();
        loadFileDocs();

    }

    public void createTmpSearcher(){
        //tmpSearcher = new TmpSearcher(dictionaryToLoad, docsFilesToLoad, citiesToLoad, termsInQuery, docsRelevant, path);
    }

    public void getQuery(List<ATerm> query){
        this.path = dc.getPostPath();
        loadCitiesDocs();
        loadDictionary();
        loadFileDocs();
        this.termsInQuery = query;
        docsRelevant = new HashSet();
        for(int i=0;i<query.size();i++){
            String pointer = dictionaryToLoad.get(query.get(i).finalName).split(":")[2];
            String df = readFromPost(pointer);
            docsRelevant.addAll(splitDocsName(df));
        }
        createTmpSearcher();
    }


    private List <String> splitDocsName(String df) {
        List <String> docNames = new ArrayList();
        String withoutTermName = df.split(",\\{")[1];
        String[]docNameFQ = withoutTermName.split("\\{");
        for(int i=0;i<docNameFQ.length;i++){
            docNames.add(docNameFQ[i].split(":")[0]);
        }
        return docNames;
    }

    /**
     * Get the correct line from post file
     * @param pointer
     * @return String
     */
    private String readFromPost(String pointer) {
        String [] point = pointer.split("/");
        String fileName = point[0];
        String pointerLine = point[1];
        int numLine = 0;
        String sCurrentLine = ""; // check if doesn't exist
        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(path+"\\"+fileName);
            br = new BufferedReader(fr);

            while (!pointerLine.equals(numLine+"") && (sCurrentLine = br.readLine()) != null) {
                numLine++;
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();
            } catch (IOException ex) {

            }
        }
        return sCurrentLine;
    }

    /**
     * Load dictionary
     */
    public void loadDictionary(){
        List<String> lines = new ArrayList<>();
        dictionaryToLoad = new HashMap<>();
        try (//GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
             FileInputStream out = new FileInputStream(path+"\\Dictionary");
             BufferedReader br = new BufferedReader(new InputStreamReader(out, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            Collections.sort(lines);
            for(String term:lines){
                String []tmp = term.split(",\\{");
                dictionaryToLoad.put(tmp[0],tmp[1]);
            }

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
    }

    /**
     * Load fileDocs
     */
    public void loadFileDocs(){
        List<String> lines = new ArrayList<>();
        docsFilesToLoad = new HashMap<>();
        try (//GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
             FileInputStream out = new FileInputStream(path+"\\fileDocs");
             BufferedReader br = new BufferedReader(new InputStreamReader(out, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            Collections.sort(lines);
            for(String term:lines){
                String []tmp = term.split(",",2);
                docsFilesToLoad.put(tmp[0],tmp[1]);
            }

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
    }

    /**
     * Load cities
     */
    public void loadCitiesDocs(){
        List<String> lines = new ArrayList<>();
        citiesToLoad = new HashMap<>();
        try (//GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
             FileInputStream out = new FileInputStream(path+"\\CitiesPost");
             BufferedReader br = new BufferedReader(new InputStreamReader(out, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            Collections.sort(lines);
            for(String term:lines){
                String []tmp = term.split(",");
                citiesToLoad.put(tmp[0],tmp[1]);
            }

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
    }



}
