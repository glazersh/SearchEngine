package Model;

import Model.Term.ATerm;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Searcher {

    private TmpSearcher tmpSearcher;
    private DataCollector dc;

    private List<ATerm> termsInQuery;
    private Set<String> docsRelevant;
    private String path;

    private Map<String,String> dictionaryToLoad;
    private Map<String,String> docsFilesToLoad ;
    private Map<String,String> citiesToLoad ;


    public Searcher(DataCollector dataCollector){
        this.dc=dataCollector;
        termsInQuery = new ArrayList<>();
        this.path = dc.getPostPath();
    }

    public void createTmpSearcher(){
        tmpSearcher = new TmpSearcher(dictionaryToLoad, docsFilesToLoad, citiesToLoad, termsInQuery, docsRelevant, path,);
        tmpSearcher.start();
    }

    public void getQuery(List<ATerm> query){

        if(dictionaryToLoad == null){
            initAllDict();
        }
        this.path = dc.getPostPath();
        this.termsInQuery = query;
        docsRelevant = new HashSet();
        for(int i=0;i<query.size();i++){
            String pointer = dictionaryToLoad.get(query.get(i).finalName).split(":")[2];
            String df = readFromPost(pointer);
            docsRelevant.addAll(splitDocsName(df));
        }
        createTmpSearcher();

    }

    private void initAllDict() {
        dictionaryToLoad = dc.getDictionaryToLoad();
        docsFilesToLoad = dc.getDocsFilesToLoad();
        citiesToLoad = dc.getCitiesToLoad();
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

}
