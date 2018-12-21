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


    Map<String, String> dictionaryToLoad;
    Map<String, String> docCitiesToLoad;
    Map<String, String> docFilesToLoad;

    public TmpSearcher(Map<String, String> dictionaryToLoad, Map<String, String> docsFilesToLoad, Map<String, String> citiesToLoad, List<String> termsInQuery, String path, DataCollector dataCollector) {
        this.postingPath = path;
        this.dictionaryToLoad = dictionaryToLoad;
        this.docCitiesToLoad = citiesToLoad;
        this.docFilesToLoad = docsFilesToLoad;
        this.queryList = termsInQuery;
        this.FinalListDocs = new ArrayList<>();
        this.dataCollector = dataCollector;
        this.ranker = new Ranker(dataCollector);
        this.ListDocs = new ArrayList<>();

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
           if(docdata.getSumBM25()>2) {
               getEntities(docdata.getDocName(), docdata);
               FinalListDocs.add(docdata);

           }
        }

       return FinalListDocs;


    }


    public DocData getFromDocPost(String docName) {
        DocData docData = new DocData(docName);
        String[] DocInfo = docFilesToLoad.get(docName).split(",");
        docData.setDocLength(Integer.parseInt(DocInfo[2]));
        if (DocInfo.length == 4) {
            docData.setCity(DocInfo[3]);
        } else
            docData.setCity("");

        for (String term : queryList) {
            String[] termInfo = dictionaryToLoad.get(term).split(":");
            //number of docs the term occur in all corpus
            int intToAdd = Integer.parseInt(termInfo[1]);
            docData.addNumberOfDocPerTerm(intToAdd);
            String[] TermPos = termInfo[2].split("/");
            int termPos = Integer.parseInt(TermPos[1]);
            ///open post where the term is
            FileInputStream out = null;
            BufferedReader br = null;
            try {//GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
                out = new FileInputStream(postingPath + "\\" + TermPos[0]);
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



