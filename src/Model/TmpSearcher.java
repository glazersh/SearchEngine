package Model;
import Model.Model;
import Model.Term.ATerm;
import Model.DocData;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TmpSearcher {
    Set<String> DocsList;
    String postingPath;
    List<String> queryList;
    List<DocData> FinalListDocs;
    Ranker ranker;
    DataCollector dataCollector;
    List<DocData> ListDocs;


    Map<String, String[]> dictionaryToLoad;
    Map<String, String> docCitiesToLoad;
    Map<String, String[]> docFilesToLoad;

    private Map<String,Map<String,Integer>> tf;

    public TmpSearcher(Map<String, String[]> dictionaryToLoad, Map<String, String[]> docsFilesToLoad, Map<String, String> citiesToLoad, List<String> termsInQuery, String path, DataCollector dataCollector, Map<String,Map<String,Integer>> tf) {
        this.postingPath = path;
        this.dictionaryToLoad = dictionaryToLoad;
        this.docCitiesToLoad = citiesToLoad;
        this.docFilesToLoad = docsFilesToLoad;
        this.queryList = termsInQuery;
        this.FinalListDocs = new ArrayList<>();
        this.dataCollector = dataCollector;
        this.ranker = new Ranker(dataCollector);
        this.ListDocs = new ArrayList<>();
        this.tf = tf;
    }

    public void setDocsList(Set<String> docsList){
        this.DocsList=docsList;
    }

    public List<DocData> start() {
        for (String str : DocsList) {
            DocData docData = getFromDocPost(str);
            ListDocs.add(docData);
        }

        for (DocData docdata:ListDocs) {
           ranker.start(docdata);
           //////may change
           if(docdata.getSumBM25()>0) {
               //getEntities(docdata.getDocName(), docdata);
               FinalListDocs.add(docdata);
           }
        }

       return FinalListDocs;


    }


    public DocData getFromDocPost(String docName) {
        int counter = queryList.size();
        DocData docData = new DocData(docName);
        String DocInfo = docFilesToLoad.get(docName)[2];
        docData.setDocLength(Integer.parseInt(DocInfo));
        if (docFilesToLoad.get(docName).length == 4) {
            docData.setCity(docFilesToLoad.get(docName)[3]);
        } else
            docData.setCity("");

        for (String term : queryList) {
            String numberOfDocsInCorpus = dictionaryToLoad.get(term)[1];
            //number of docs the term occur in all corpus
            int intToAdd = Integer.parseInt(numberOfDocsInCorpus);
            docData.addNumberOfDocPerTerm(intToAdd);

            if (tf.get(term).containsKey(docName)) {
                int occur = tf.get(term).get(docName);
                docData.addToFreqList(occur);
            } else {
                docData.addToFreqList(0);
                counter--;
            }
        }
        docData.setTermsInDoc(counter);
        int And = counter;
        int docLength = docData.getDocLength();
        int Or = queryList.size()-counter;
        double Jaccard = (double)And/(docLength + Or);
        docData.setJaccard(Jaccard);


        return docData;
    }

}



