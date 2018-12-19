package Model;
import Model.Model;
import Model.Term.ATerm;
import Model.DocData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TmpSearcher {
    List<String>DocsList;
    String postingPath;
    ReadFile readFile;
    List<ATerm> queryList;


    Map<String,String> dictionaryToLoad;
    Map<String,String> docCitiesToLoad;
    Map<String,String> docFilesToLoad;

    public TmpSearcher(ReadFile readFile){
        this.readFile = readFile;
        this.dictionaryToLoad = Searcher.dictionaryToLoad;
        this.docCitiesToLoad = Searcher.citiesToLoad;
        this.docFilesToLoad = Searcher.docsFilesToLoad;

    }



    public void getFromDocPost(String docName) {
        DocData docData = new DocData(docName);
        String[] DocInfo = docFilesToLoad.get(docName).split(",");
        docData.setDocLength(Integer.parseInt(DocInfo[2]));



    }



    public void setDocList(List<String>docsList, List<ATerm> queryList){
        this.DocsList = DocsList;
        this.queryList=queryList;
    }


    public void setPostingPath(String postingPath) {
        this.postingPath = postingPath;
    }


}
