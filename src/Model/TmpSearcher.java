package Model;
import Model.Model;
import Model.Term.ATerm;
import Model.DocData;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TmpSearcher {
    List<String>DocsList;
    String postingPath;
    List<ATerm> queryList;
    List<DocData> FinalListDocs;
    Ranker ranker;


    Map<String,String> dictionaryToLoad;
    Map<String,String> docCitiesToLoad;
    Map<String,String> docFilesToLoad;

    public TmpSearcher(Map<String,String> dictionaryToLoad, Map<String,String> docsFilesToLoad, Map<String,String> citiesToLoad,List<ATerm>  termsInQuery,  List<String>docsRelevant,String path){
        ranker = new Ranker();
        this.postingPath = path;
        this.dictionaryToLoad = dictionaryToLoad;
        this.docCitiesToLoad = citiesToLoad;
        this.docFilesToLoad = docsFilesToLoad;
        this.DocsList = docsRelevant;
        this.queryList = termsInQuery;
        this.FinalListDocs = new ArrayList<>();
    }

    public void start() {
        for (String str:DocsList) {
            DocData docData = getFromDocPost(str);
            FinalListDocs.add(docData);
        }

        ranker.setDocDataList(FinalListDocs);


    }


    public DocData getFromDocPost(String docName) {

        DocData docData = new DocData(docName);
        String[] DocInfo = docFilesToLoad.get(docName).split(",");
        docData.setDocLength(Integer.parseInt(DocInfo[2]));
        if(DocInfo.length==4) {
            docData.setCity(DocInfo[3]);
        }
        else
            docData.setCity("");

        for (ATerm term:queryList) {
            String[] termInfo = dictionaryToLoad.get(term.finalName).split(":");
            //number of docs the term occur in all corpus
            int intToAdd = Integer.parseInt(termInfo[1]);
            docData.addNumberOfDocPerTerm(intToAdd);
            String[] TermPos = termInfo[2].split("/");
            int termPos = Integer.parseInt(TermPos[1]);
            ///open post where the term is
            try (//GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
                 FileInputStream out = new FileInputStream(postingPath+"\\" + TermPos[0]);
                 BufferedReader br = new BufferedReader(new InputStreamReader(out, StandardCharsets.UTF_8))) {
                String line;
                int counter=0;
                while ((line = br.readLine()) != null) {
                    if(counter == termPos-1)
                        break;
                    counter++;
                }
                //split the term from the documents
                String termData = line.split(",\\{")[1];
                //split to array of documents
                String[] DocsInTermOccur = termData.split("\\{");
                for (int i = 0; i<DocsInTermOccur.length;i++) {
                    String DocNameInPost = DocsInTermOccur[i].split(":")[0];
                    //if twe found the relevant doc
                    if(DocNameInPost.equals(docName)) {
                        docData.addToFreqList(Integer.parseInt(DocsInTermOccur[i].split(":")[1]));
                        break;
                    }
                }

            } catch (FileNotFoundException e) {

            } catch (IOException e) {
            }

        }

        return docData;

    }




}
