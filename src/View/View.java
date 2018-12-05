package View;

import Model.Posting;
import ViewModel.ViewModel;
//import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import javax.xml.soap.Node;
import javax.xml.soap.Text;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;


public class View  implements Observer {

    @FXML
    public TextField PathCollection;
    public TextField lbl_PathPositng;

    Boolean withStemming;
    File selectedFolderBrowseCollection;
    File PostingPath;
    public File BigDic;

    @FXML
    public Button CollectionButton;
    public Button btn_Posting;
    public Button btn_Start;
    public Button btn_showDic;
    @FXML
    Label wrongPath;

    @FXML
    public Button bn_loadDict;
    @FXML
    Label wrongPathPost;
    @FXML
    CheckBox lbl_Stemming;
    @FXML
    ChoiceBox cb_leng;
    @FXML
    public ListView lv_terms;
    ListView lv_bigDic;
    TextArea dicText;

    public Button b_csv;

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

    public void showDic() throws IOException {
        lv_terms.setVisible(true);
        lv_terms.getItems().add("shula-3");
        lv_terms.getItems().add("dor-2");
    }

    public void loadDict(){
        String path;
        if(withStemming){
            path = PostingPath+"\\Y-DictionaryToLoad";
        }else{
            path = PostingPath+"\\N-DictionaryToLoad";
        }
        File file = new File(path);
        viewModel.loadDic(file);
    }

    public void startEngine(){
        btn_Start.setDisable(true);
        if(lbl_Stemming.isSelected())
            withStemming = true;
        else
            withStemming=false;

        viewModel.startEngine(selectedFolderBrowseCollection.getAbsoluteFile(),getStopWordsPath(),PostingPath.getPath(), withStemming);
        Set<String>lang = viewModel.getLang();
        List<String> sortLang = new ArrayList();
        for(String langWord:lang){
            sortLang.add(langWord);
        }
        Collections.sort(sortLang);
        cb_leng.setItems(FXCollections.observableArrayList(
                "English",new Separator()
        ));
        cb_leng.getItems().addAll(sortLang);

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

    // remove !
    public void CSV() throws FileNotFoundException {
        Posting.CSV();
    }
}




