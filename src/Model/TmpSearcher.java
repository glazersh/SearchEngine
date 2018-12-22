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
            }
        }



/*
            String termPostFile = dictionaryToLoad.get(term)[2];
            String termPosition = dictionaryToLoad.get(term)[3];
            int termPos = Integer.parseInt(termPosition);
            ///open post where the term is
            FileInputStream out = null;
            BufferedReader br = null;
            try {//GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
                out = new FileInputStream(postingPath + "\\" + termPostFile);
                br = new BufferedReader(new InputStreamReader(out, StandardCharsets.UTF_8));


                String line;
                int counter = 0;
                while ((line = br.readLine()) != null) {
                    if (counter == termPos - 1)
                        break;
                    counter++;
                }

                //split the term from the documents
                String termData = line.split(",\\{")[1];
                //split to array of documents
                String[] DocsInTermOccur = termData.split("\\{");
                boolean found = false;
                for (int i = 0; i < DocsInTermOccur.length; i++) {
                    String DocNameInPost = DocsInTermOccur[i].split(":")[0];
                    //if twe found the relevant doc
                    if (DocNameInPost.equals(docName)) {
                        docData.addToFreqList(Integer.parseInt(DocsInTermOccur[i].split(":")[1]));
                        found = true;
                        break;
                    }
                }
                if(!found)
                    docData.addToFreqList(0);


            } catch (IOException e) {
            } finally {
                try {
                    if (out != null)
                        out.close();

                    if (br != null)
                        br.close();
                } catch (IOException ex) {

                }
            }
        }
        */

        return docData;
    }



    public void getEntities(String docName,DocData docData){
        FileInputStream out = null;
        BufferedReader br = null;
        try {//GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
            out = new FileInputStream(postingPath + "\\Entities");
            br = new BufferedReader(new InputStreamReader(out, StandardCharsets.UTF_8));

            String line;
            while ((line = br.readLine()) != null) {
                String[] docInfo = line.split(",\\{");
                if (docInfo[0].equals(docName) && docInfo.length!=1) {
                    splitEntities(docInfo[1], docData);
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();

                if (br != null)
                    br.close();
            } catch (IOException ex) {

            }
        }

    }

    private void splitEntities(String strEntities, DocData docData) {
        String[] entities= strEntities.split(":");
        int counter=0;
        for(int i=0; i<entities.length || counter<5;i++){
            if(dictionaryToLoad.containsKey(entities[i])){
                docData.addToTopEntities(entities[i]);
                counter++;
            }
        }


    }

}



