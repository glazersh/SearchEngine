package View;

import Model.Indexer;
import ViewModel.ViewModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
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
    public Button bn_reset;
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


    public Button b_csv;
    public GridPane dp_table;
    public Label l_docs;
    public Label l_terms;
    public Label l_time;

    private ViewModel viewModel;

    public View(){}

    /**
     * gets the path where the corpus is
     */
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

    /**
     * gets the path in which to save the postins
     */
    public void BrowsePostingPath() {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Please Choose Indexer Path");
            PostingPath = directoryChooser.showDialog((Stage)btn_Posting.getScene().getWindow());
            lbl_PathPositng.setText(PostingPath.getAbsolutePath());
            if (PostingPath != null) {
                wrongPathPost.setText("");
            }
        } catch (Exception e) {

        }
    }

     //when asked, displays the dictionary with the terms and time it appeared in the whole corpus
    public void showDic() throws IOException {
        lv_terms.setVisible(true);
        lv_terms.getItems().add("shula-3");
        lv_terms.getItems().add("dor-2");
    }

    public void resetAll(){
        viewModel.resetAll();
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

    // start the prosses - sends the pathing to view model which sends to model and analyzes it
    public void startEngine(){
        btn_Start.setDisable(true);
        if(lbl_Stemming.isSelected())
            withStemming = true;
        else
            withStemming=false;

        viewModel.startEngine(selectedFolderBrowseCollection.getAbsoluteFile(),getStopWordsPath(),PostingPath.getPath(), withStemming);
        Set<String>lang = viewModel.getLang();
        List<String> sortLang = new ArrayList();
        //gets the languages list from the corpus and updates the option in the gui
        for(String langWord:lang){
            sortLang.add(langWord);
        }
        Collections.sort(sortLang);
        cb_leng.setItems(FXCollections.observableArrayList(
                "English",new Separator()
        ));
        cb_leng.getItems().addAll(sortLang);
        l_docs.setText(viewModel.getNumberOfDocs()+"");
        l_terms.setText(viewModel.getNumberOfTerms()+"");
        l_time.setText(viewModel.getRunnningTime()+"");
        dp_table.setVisible(true);
    }

    public void setViewModel(ViewModel vm) {
        this.viewModel = vm;
    }



    public String getStopWordsPath(){
        return selectedFolderBrowseCollection.getPath()+"\\stop_words.txt";
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
        Indexer.CSV();
    }
}




