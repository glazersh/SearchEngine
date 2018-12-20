package View;

import ViewModel.ViewModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;

import java.io.*;
import java.util.*;
import java.util.List;


public class View  implements Observer {

    @FXML
    public TextField tf_corpusPath;
    public TextField tf_postingPath;

    public Button b_corpusPath;
    public Button b_postPath;
    public Button b_Start;
    public Button b_showDic;
    public Button b_reset;
    public Button b_loadDict;

    public Label l_docs;
    public Label l_terms;
    public Label l_time;
    public Label l_info;
    public Label wrongPath;
    public Label l_warning;

    public ListView lv_terms;
    public ListView lv_dictionary;

    public CheckBox cb_isStem;

    public ChoiceBox cb_Languages;

    public GridPane gd_info;

    private Boolean withStemming=true;
    private File corpusFile;
    private File PostingPath;

    private ViewModel viewModel;
    private String corpusPath="";
    private String postingPath="";

    //partB
    public Button b_query;
    public TextField tf_query;

    public View(){

    }

    /**
     * gets the path where the corpus is
     */
    public void BrowseCollection() {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            corpusFile = directoryChooser.showDialog(b_corpusPath.getScene().getWindow());
            tf_corpusPath.setText(corpusFile.getAbsolutePath());
            corpusPath = corpusFile.getAbsolutePath();
        } catch (Exception e) {}
    }

    /**
     * gets the path in which to save the postins
     */
    public void BrowsePostingPath() {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            PostingPath = directoryChooser.showDialog(b_postPath.getScene().getWindow());
            tf_postingPath.setText(PostingPath.getAbsolutePath());
            postingPath = PostingPath.getAbsolutePath();
        } catch (Exception e) {}
    }

    /**
     * When asked, displays the dictionary with the terms and time it appeared in the whole corpus
     */
    public void showDic() {
        lv_terms.setVisible(true);
        String postInto = postingPath;
        if(withStemming){
            postInto+="\\Y\\";
        }else{
            postInto+="\\N\\";
        }
        try (//GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
             FileInputStream out = new FileInputStream(postInto+"Dictionary");
             BufferedReader br = new BufferedReader(new InputStreamReader(out))) {
            String line;
            while ((line = br.readLine()) != null) {
                String []tmp = line.split(",\\{");
                lv_dictionary.getItems().add(tmp[0]+" - "+tmp[1].split(":")[0]);
            }
            l_info.setText("Dictionary is shown");
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) {}

    }

    /**
     * Reset all the post Files
     */
    public void resetAll(){
        viewModel.resetAll();
        l_info.setText("The reset is Done");
    }

    /**
     * Load Dictionary to memory
     */
    public void loadDict(){
        String path;
        if(withStemming){
            path = PostingPath+"\\Y-DictionaryToLoad";
        }else{
            path = PostingPath+"\\N-DictionaryToLoad";
        }
        File file = new File(path);
        viewModel.loadDic(file);
        l_info.setText("The dictionary is loaded");
    }

    /**
     * Start the prosses - sends the pathing to view model which sends to model and analyzes it
     */
    public void startEngine(){
        if(postingPath.equals("")||corpusPath.equals("")){
            l_warning.setVisible(true);
            l_warning.setText("Empty path");
        }else {
            l_warning.setVisible(false);
            if (cb_isStem.isSelected())
                withStemming = true;
            else
                withStemming = false;
            viewModel.startEngine(corpusFile.getAbsoluteFile(), getStopWordsPath(), PostingPath.getPath(), withStemming);
            Set<String> lang = viewModel.getLang();
            List<String> sortLang = new ArrayList();
            //gets the languages list from the corpus and updates the option in the gui
            for (String langWord : lang) {
                sortLang.add(langWord);
            }
            Collections.sort(sortLang);
            cb_Languages.setItems(FXCollections.observableArrayList(
                    "English", new Separator()
            ));
            cb_Languages.getItems().addAll(sortLang);
            l_docs.setText(viewModel.getNumberOfDocs() + "");
            l_terms.setText(viewModel.getNumberOfTerms() + "");
            l_time.setText(viewModel.getRunnningTime() + "");
            gd_info.setVisible(true);
            b_Start.setDisable(false);
            l_info.setText("The process is done !");


            /////////////////////////////////////////////////////////////////

        }
    }

    public void setViewModel(ViewModel vm) {
        this.viewModel = vm;
    }

    public String getStopWordsPath(){
        return corpusFile.getPath()+"\\stop_words.txt";
    }

    public File getFile() {
        return corpusFile;
    }

    @Override
    public void update(java.util.Observable o, Object arg) {
        if (o == viewModel) {
            BrowseCollection();
            BrowsePostingPath();
        }
    }

    /////////////////////////// part B //////////////////

    public void query(){
        String Query = tf_query.getText();
        viewModel.startEngineQuery(Query);
    }

}




