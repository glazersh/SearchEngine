package Model;

import Model.Term.ATerm;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Searcher {

    private ReadFile readFile;
    private String query;
    private Parse parse;
    private List<ATerm> termsInQuery;
    private List<String> docsRelevant;

    private String path;
    private TmpSearcher tmpSearcher;

    Map<String,String> dictionaryToLoad;
    Map<String,String> docsFilesToLoad ;
    Map<String,String> citiesToLoad ;


    public Searcher(ReadFile readFile){
        this.readFile = readFile;
        parse = readFile.getParse();
        termsInQuery = new ArrayList<>();
        this.path = readFile.dataC.getPostPath();

        loadCitiesDocs();
        loadDictionary();
        loadFileDocs();

    }

    public void createTmpSearcher(){
        tmpSearcher = new TmpSearcher(dictionaryToLoad, docsFilesToLoad, citiesToLoad, termsInQuery, docsRelevant, path);
    }

    public void getQuery(String query){
        this.query = query;
        parseQuery();

    }

    private void parseQuery(){
        parse.parse(query,"","",false);
        this.termsInQuery = parse.queryFinalTerms();
    }

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
