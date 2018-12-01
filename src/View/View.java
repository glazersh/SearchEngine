package View;

import ViewModel.ViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


import java.io.File;

public class View {

    @FXML
    public TextField PathCollection;


    File selectedFolderBrowseCollection;
    @FXML
    public Button CollectionButton;

    public TextField tf_Path;
    @FXML
    Label wrongPath;
    Button BrowseFiles;
    private ViewModel vm;

    public View(){}
    public void BrowseCollection() {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Please Choose corpus Path");
            selectedFolderBrowseCollection = directoryChooser.showDialog((Stage)CollectionButton.getScene().getWindow());
            PathCollection.setText(selectedFolderBrowseCollection.getAbsolutePath());
           if (selectedFolderBrowseCollection != null) {
                wrongPath.setText("");
            }
        } catch (Exception e) {

        }
    }

    public void setViewModel(ViewModel vm) {
        this.vm = vm;
    }

    public File getFile() {
        return selectedFolderBrowseCollection;
    }
}




