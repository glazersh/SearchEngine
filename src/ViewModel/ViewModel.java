package ViewModel;

import Model.Model;
import Model.DataCollector;
import View.View;
//import javafx.beans.Observable;

import java.io.File;
import java.util.Map;
import java.util.Observer;
import java.util.Set;


public class ViewModel extends java.util.Observable implements Observer  {


    private Model model;
    private DataCollector dataCollector;
    private View MainWindow;
    public File selectedFolderBrowseCollection;
    Map<String, Integer> bigDic;


    public ViewModel(Model model, View mainWindow) {
        this.model = model;
        this.MainWindow = mainWindow;
    }

    public void getFile() {

        model.setFiles(MainWindow.getFile());
    }


    public Map getMap(){
        return model.getMap();
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

    public void loadDic(File file) {
        model.loadDict(file);
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

    public void startEngineQuery(String query) {
        model.readQuery(query);
    }

}
