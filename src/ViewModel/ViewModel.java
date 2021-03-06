package ViewModel;

import Model.Model;
import Model.DataCollector;
import Model.DocData;
import View.View;
//import javafx.beans.Observable;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Set;


public class ViewModel extends java.util.Observable implements Observer  {


    private Model model;
    private View MainWindow;
    public File selectedFolderBrowseCollection;



    public ViewModel(Model model, View mainWindow) {
        this.model = model;
        this.MainWindow = mainWindow;
    }



    @Override
    public void update(java.util.Observable o, Object arg) {
        if (o==model){
            selectedFolderBrowseCollection = model.getFiles();
            setChanged();
            notifyObservers();
        }
    }

    public Set getLang(){
        return model.getLang();
    }

    public void loadDic(String path) {
        model.loadAllDictionary(path);
    }
    public void resetAll() {

        model.resetAll();
    }

    public int getNumberOfDocs(){
        return model.getNumberOfDocs();
    }
    public int getNumberOfTerms(){
        return model.getNumberOfTerms();
    }
    public long getRunnningTime(){
        return model.getRunningTime();
    }

    public void startEngine(File fCorpus, String stopWords, String PathPosting, Boolean withStemming){
        model.readCorpus(fCorpus.getPath(),stopWords, PathPosting,withStemming);
    }

    public void startEngineQuery(String query, String StopWordsPath,boolean withStemming) {
        model.readQuery(query,StopWordsPath,withStemming);
    }

    public List<DocData> getDocsName() {
        return model.getDocsName();
    }

    public void fileQuery(String queryPath,String stopWords, boolean withstemming, boolean withSemantic, String cities) {
        model.fileOfQuery(queryPath,stopWords,withstemming,withSemantic,cities);
    }

    public List<String> getAllCities() {
        return model.getAllCities();
    }

    public String getSem(String query) {
        return model.getSemantics(query);
    }

    public List<String> getIDs() {
        return model.getIDs();
    }

    public List<DocData> getANS(int value) {
        List<DocData>tmp =  model.getANS(value);
        return tmp;
    }

    public void clearIDS(){
        model.IDsClear();
    }

    public void setQueryPathToSave(String queryPathToSave) {
        model.setQueryPathToSave(queryPathToSave);
    }
}
