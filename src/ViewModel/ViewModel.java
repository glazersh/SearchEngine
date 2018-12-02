package ViewModel;

import Model.Model;
import View.View;
import javafx.beans.InvalidationListener;
//import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

import java.io.File;
import java.util.Observer;
import java.util.Observable;


public class ViewModel extends java.util.Observable implements Observer  {


    private Model model;
    private View MainWindow;
    public File selectedFolderBrowseCollection;


    public ViewModel(Model model, View mainWindow) {
        this.model = model;
        this.MainWindow = mainWindow;
    }

    public void getFile() {

        model.setFiles(MainWindow.getFile());
    }
    public void startEngine(File fCorpus, String stopWords, String PathPosting, Boolean withStemming){
        model.readCorpus(fCorpus.getPath(),stopWords, PathPosting,withStemming);
    }




    @Override
    public void update(java.util.Observable o, Object arg) {
        if (o==model){
            selectedFolderBrowseCollection = model.getFiles();
            setChanged();
            notifyObservers();
        }
    }

}
