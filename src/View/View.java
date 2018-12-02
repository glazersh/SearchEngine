package View;

import ViewModel.ViewModel;
//import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.control.CheckBox;

import javax.xml.soap.Text;
import java.io.File;
import java.util.Observer;
import java.util.Observable;


public class View  implements Observer {

    @FXML
    public TextField PathCollection;
    public TextField lbl_PathPositng;
    public javafx.scene.control.Label lbl_Corpus;

    Boolean withStemming;
    File selectedFolderBrowseCollection;
    File PostingPath;
    @FXML
    public Button CollectionButton;
    public Button btn_Posting;
    public Button btn_Start;
    @FXML
    Label wrongPath;
    @FXML
    Label wrongPathPost;
    @FXML
    CheckBox lbl_Stemming;

    private ViewModel viewModel;

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
            else
                wrongPath.setText("Wrongggg");
        } catch (Exception e) {

        }
    }

    public void BrowsePostingPath() {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Please Choose Posting Path");
            PostingPath = directoryChooser.showDialog((Stage)btn_Posting.getScene().getWindow());
            lbl_PathPositng.setText(PostingPath.getAbsolutePath());
            if (PostingPath != null) {
                wrongPathPost.setText("");
            }
        } catch (Exception e) {

        }
    }


    public void startEngine(){
        btn_Start.setDisable(true);
        if(lbl_Stemming.isSelected())
            withStemming = true;
        else
            withStemming=false;

        viewModel.startEngine(selectedFolderBrowseCollection.getAbsoluteFile(),getStopWordsPath(),PostingPath.getPath(), withStemming);
    }

    public void setViewModel(ViewModel vm) {
        this.viewModel = vm;
        bindProperties(viewModel);
    }
    private void bindProperties(ViewModel viewModel) {
        //  lbl_Corpus.textProperty().bind(viewModel.selectedFolderBrowseCollection.getPath());

    }

    public String getStopWordsPath(){
        return selectedFolderBrowseCollection.getPath()+"\\stopWords.txt";
    }

    public File getFile() {
        return selectedFolderBrowseCollection;
    }

    @Override
    public void update(java.util.Observable o, Object arg) {
        if (o == viewModel) {
            BrowseCollection();

        }
    }
}




