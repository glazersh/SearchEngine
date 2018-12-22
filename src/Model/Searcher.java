package Model;

import Model.Term.ATerm;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Searcher {

    private TmpSearcher tmpSearcher;
    private DataCollector dc;

    private List<String> termsInQuery;
    private Set<String> docsRelevant;
    private String path;

    private Map<String,String[]> dictionaryToLoad;
    private Map<String,String[]> docsFilesToLoad ;
    private Map<String,String> citiesToLoad ;
    private Map<String,String[]>entityToLoad;

    private PriorityQueue<DocData> returnsDocs;

    private Map<String,Map<String,Integer>> tf;


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
        tmpSearcher = new TmpSearcher(dictionaryToLoad, docsFilesToLoad, citiesToLoad, termsInQuery, path,dc,tf);
        tmpSearcher.setDocsList(docsRelevant);
        List<DocData> listOfDocs = tmpSearcher.start();
        addListToQueue(listOfDocs);
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
        termsInQuery = new ArrayList<>();
        this.path = dc.getPostPath();
        docsRelevant = new HashSet();
        tf = new HashMap<>();
        boolean found ;
        for(int i=0;i<query.size();i++){
            found = false;
            String pointer = "";
            String numLine = "";
            if(dictionaryToLoad.containsKey(query.get(i).finalName.toUpperCase()) ) {
                pointer = dictionaryToLoad.get(query.get(i).finalName.toUpperCase())[2];
                numLine = dictionaryToLoad.get(query.get(i).finalName.toUpperCase())[3];
                termsInQuery.add(query.get(i).finalName.toUpperCase());
                found = true;
            }
            if(!found && dictionaryToLoad.containsKey(query.get(i).finalName.toLowerCase())){
                pointer = dictionaryToLoad.get(query.get(i).finalName.toLowerCase())[2];
                numLine = dictionaryToLoad.get(query.get(i).finalName.toLowerCase())[3];
                termsInQuery.add(query.get(i).finalName.toLowerCase());
                found = true;
            }
            if(found) {
                String df = readFromPost(pointer,numLine);
                docsRelevant.addAll(splitDocsName(df,termsInQuery.get(termsInQuery.size()-1)));
            }
        }
        createTmpSearcher();

    }

    private void initAllDict() {
        dictionaryToLoad = dc.getDictionaryToLoad();
        docsFilesToLoad = dc.getDocsFilesToLoad();
        citiesToLoad = dc.getCitiesToLoad();
        entityToLoad = dc.getEntityToLoad();
    }

    private List <String> splitDocsName(String df, String term) {
        List <String> docNames = new ArrayList();
        String withoutTermName = df.split(",\\{")[1];
        String[]docNameFQ = withoutTermName.split("\\{");
        for(int i=0;i<docNameFQ.length;i++){
            String[]DocNameFreq = docNameFQ[i].split(":");
            docNames.add(DocNameFreq[0]);
            if(tf.containsKey(term)){
                tf.get(term).put(DocNameFreq[0],Integer.parseInt(DocNameFreq[1]));
            }else {
                Map<String,Integer>tmp = new HashMap<>();
                tmp.put(DocNameFreq[0],Integer.parseInt(DocNameFreq[1]));
                tf.put(term, tmp);
            }
        }
        return docNames;
    }

    /**
     * Get the correct line from post file
     * @param pointer
     * @return String
     */
    private String readFromPost(String pointer,String numline) {
        String fileName = pointer;
        String pointerLine = numline;
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

        List<DocData>docsName = new ArrayList<>();
        int counter = 0;
        while(!returnsDocs.isEmpty() && counter < 50){
            DocData returnDoc = returnsDocs.poll();
            docsName.add(returnDoc);
            addEntityToDocData(returnDoc);
            counter++;
        }
        returnsDocs.clear();
        dc.setRelevantDocs(docsName);




        ///////////// here for app /////////////////
//        try {
//            BufferedWriter bf = new BufferedWriter(new FileWriter("C:\\Users\\USER\\Desktop\\search2018\\post\\N\\answer.txt"));
//            for (String s2 : docsName) {
//                bf.append("352 " + "0 " + s2 + " 1 " + "3.0 " + "test\n");
//            }
//            bf.flush();
//            bf.close();
//        }catch (IOException e){
//
//        }

    }

    private void addEntityToDocData(DocData docData) {
        String[]allEntity = entityToLoad.get(docData.getDocName());
        if(allEntity!=null) {
            int add5Entity = 0;
            for (int i = 0; i < allEntity.length && add5Entity < 5; i++) {
                if (dictionaryToLoad.containsKey(allEntity[i])) {
                    docData.addToTopEntities(allEntity[i]);
                    add5Entity++;
                }
            }
        }
    }


}
