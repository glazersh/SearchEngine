package ViewModel;

import Model.Model;
import View.View;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.io.File;

public class ViewModel implements Observable{

File selectedFolderBrowseCollection;

    private Model model;
    private View MainWindow;

    public ViewModel(Model model, View mainWindow) {
        this.model = model;
        this.MainWindow = mainWindow;
    }

    public void getFile() {
        model.setFiles(MainWindow.getFile());
    }


    @Override
    public void addListener(InvalidationListener listener) {

    }

    @Override
    public void removeListener(InvalidationListener listener) {

    }
}
