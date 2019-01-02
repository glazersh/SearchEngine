package Model;

import Model.Term.ATerm;

import java.io.*;
import java.util.*;

public class Searcher {

    private InformationCollector tmpSearcher;
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
                if(0.9 * o1.getSumBM25()/100 + 0.1*o1.getJaccard() > 0.9*o2.getSumBM25()/100+0.1*o2.getJaccard())
                    return -1;
                else
                    return 1;
            }
        });

    }

    public void createTmpSearcher(){
        tmpSearcher = new InformationCollector(dictionaryToLoad, docsFilesToLoad, citiesToLoad, termsInQuery, path,dc,tf);
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
        List<String>cities = new ArrayList<>();
        termsInQuery = new ArrayList<>();
        this.path = dc.getPostPath();
        docsRelevant = new HashSet();
        tf = new HashMap<>();
        boolean found ;
        for(int i=0;i<query.size();i++){
            String term = query.get(i).finalName;
            if(citiesToLoad.containsKey(term.toUpperCase())){
                cities.add(term.toUpperCase());
                continue;
            }
            found = false;
            String pointer = "";
            String numLine = "";
            if(dictionaryToLoad.containsKey(term.toUpperCase()) ) {
                pointer = dictionaryToLoad.get(term.toUpperCase())[2];
                numLine = dictionaryToLoad.get(term.toUpperCase())[3];
                termsInQuery.add(term.toUpperCase());
                found = true;
            }
            if(!found && dictionaryToLoad.containsKey(term.toLowerCase())){
                pointer = dictionaryToLoad.get(term.toLowerCase())[2];
                numLine = dictionaryToLoad.get(term.toLowerCase())[3];
                termsInQuery.add(term.toLowerCase());
                found = true;
            }
            if(found) {
                String df = readFromPost(pointer,numLine);
                docsRelevant.addAll(splitDocsName(df,termsInQuery.get(termsInQuery.size()-1)));
            }
        }
        if(cities.size()>0){
            Set <String> citydoc= new HashSet<>();
            for(String city : cities){
                found = false;
                String pointer = "";
                String numLine = "";
                if(dictionaryToLoad.containsKey(city.toUpperCase()) ) {
                    pointer = dictionaryToLoad.get(city.toUpperCase())[2];
                    numLine = dictionaryToLoad.get(city.toUpperCase())[3];

                    found = true;
                }
                if(!found && dictionaryToLoad.containsKey(city.toLowerCase())){
                    pointer = dictionaryToLoad.get(city.toLowerCase())[2];
                    numLine = dictionaryToLoad.get(city.toLowerCase())[3];

                    found = true;
                }
                if(found) {
                    String df = readFromPost(pointer,numLine);
                    citydoc.addAll(splitDocsName(df,termsInQuery.get(termsInQuery.size()-1)));
                }
                citydoc.addAll(fromTAGS(city));

            }
            docsRelevant.retainAll(citydoc);
        }
        createTmpSearcher();

    }


    private List<String> fromTAGS(String city) {
        String[] docs = citiesToLoad.get(city).split(":");
        List<String>returnList = new ArrayList<>();
        for(String doc : docs){
            returnList.add(doc);
        }
        return returnList;
    }

    /**
     * function for the loading in the gui
     */
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

    }

    /**
     * function for adding the entities we found to the post of the docs
     * @param docData
     */

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
