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

    private PriorityQueue<DocData> returnsDocs;


    public Searcher(DataCollector dataCollector){
        this.dc=dataCollector;
        termsInQuery = new ArrayList<>();
        this.path = dc.getPostPath();
        returnsDocs = new PriorityQueue(new Comparator<DocData>() {
            @Override
            public int compare(DocData o1, DocData o2) {
                if(o1.getSumBM25()*0.9 + o1.getJaccard()*0.1 > o2.getSumBM25()*0.9 + o2.getJaccard()*0.1)
                    return -1;
                else
                    return 1;
            }
        });

    }

    public void createTmpSearcher(){
        tmpSearcher = new TmpSearcher(dictionaryToLoad, docsFilesToLoad, citiesToLoad, termsInQuery, docsRelevant, path);
        //List<DocData> listOfDocs = tmpSearcher.start();
        //addListToQueue(listOfDocs);
    }

    private void addListToQueue(List<DocData> listOfDocs) {
        for(DocData doc:listOfDocs){
            addNewDocToQueue(doc);
        }
        getRelevantDocs();
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

    /**
     * add new doc to queue
     * @param newDoc
     */
    public void addNewDocToQueue(DocData newDoc){
        returnsDocs.add(newDoc);
    }

    /**
     * @return list of docs name (max 50 docs)
     */
    public void getRelevantDocs(){

        List<String>docsName = new ArrayList<>();
        int counter = 0;
        while(!returnsDocs.isEmpty() && counter < 50){
            docsName.add(returnsDocs.poll().getDocName());
            counter++;
        }
        dc.setRelevantDocs(docsName);

    }

}
