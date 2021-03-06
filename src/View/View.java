package View;

import Model.DocData;
import ViewModel.ViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    public Label lb_dictionary;

    public ListView lv_terms;

    public CheckBox cb_isStem;


    public ChoiceBox cb_Languages;

    public GridPane gd_info;

    private Boolean withStemming=false;
    private File corpusFile;
    private File PostingPath;

    private ViewModel viewModel;
    private String corpusPath="";
    private String postingPath="";

    //partB
    public Button b_query;
    public TextField tf_query;
    public TextField tf_resultsPath;
    private File queryResultPath;
    private String queryToSavePath="";





    public ListView<DocData> lv_returndocs;
    public ListView lv_entity;
    public ListView<String> lv_city;
    public CheckBox cb_Path;
    public CheckBox cb_queryPath;

    public TextField tf_queryPath;
    private File queryFile;
    public Button b_queryPath;
    public Button b_queryPath1;

    private String queryPath;
    public ComboBox<String> cb_cities;

    public TextField tf_docname;
    public TextArea ta_doc;

    public CheckBox cb_isSem;
    public ListView lv_idQuery;
    private boolean isLoad = false;


    public void initialize(){
        cb_cities.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                selectCities();
            }
        });
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

    public void checkBoxQuery(){
        cb_Path.setSelected(true);
        cb_queryPath.setSelected(false);
    }


    public void checkBoxBrowse(){
        cb_queryPath.setVisible(true);
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

    public void browseQueryPathToSave(){
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            queryResultPath = directoryChooser.showDialog(b_queryPath1.getScene().getWindow());
            tf_resultsPath.setText(queryResultPath.getAbsolutePath());
            queryToSavePath = queryResultPath.getAbsolutePath();
            viewModel.setQueryPathToSave(queryToSavePath);
        } catch (Exception e) {}
    }

    /**
     * When asked, displays the dictionary with the terms and time it appeared in the whole corpus
     */
    public void showDic() {

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
                lv_terms.getItems().add(tmp[0]+" - "+tmp[1].split(":")[0]);
            }
            lv_terms.setVisible(true);
            lb_dictionary.setVisible(true);
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
    public void loadDict() {
        String path;
        if (postingPath.equals("") || corpusPath.equals("")) {
            l_warning.setVisible(true);
            l_warning.setText("Empty path");
        } else {
            if (cb_isStem.isSelected()) {
                path = PostingPath + "\\Y\\";
            } else {
                path = PostingPath + "\\N\\";
            }
            viewModel.loadDic(path);
            cb_cities.getItems().addAll(viewModel.getAllCities());
            isLoad = true;
            Set<String> lang = viewModel.getLang();
            List<String> langList = new ArrayList<>();
            for(String str :lang){
                langList.add(str);
            }
            cb_Languages.getItems().addAll(langList);
            l_info.setText("The dictionary is loaded");
        }
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
            loadDict();
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


    @Override
    public void update(java.util.Observable o, Object arg) {
        if (o == viewModel) {
            BrowseCollection();
            BrowsePostingPath();
        }
    }

    /////////////////////////// part B //////////////////

    public void query(){
        lv_idQuery.getItems().clear();
        viewModel.clearIDS();
        if(cb_Path.isSelected()){
            if(!tf_query.getText().equals("")) {

                String Query = tf_query.getText();
                if (cb_isSem.isSelected()) {
                    Query = viewModel.getSem(Query);
                }
                if (lv_city.getItems().size() > 0) {
                    for (String city : lv_city.getItems()) {
                        Query += " " + city;
                    }
                }
                viewModel.startEngineQuery(Query, getStopWordsPath(), cb_isStem.isSelected());
                lv_returndocs.getItems().clear();
                getAnswer();
                l_info.setText("The results are shown");

            }
            else{
                int x=4;
            }
        }else{
            String cities = "";
            if(lv_city.getItems().size()>0){
                for(String city : lv_city.getItems()) {
                    cities += " "+city;
                }
            }
            viewModel.fileQuery(queryPath,getStopWordsPath(),cb_isStem.isSelected(),cb_isSem.isSelected(),cities );
            getAnswer();
            l_info.setText("The results are shown");
        }
    }

    public void findEntity(){
        lv_entity.getItems().clear();
        DocData doc  = lv_returndocs.getSelectionModel().getSelectedItem();
        if(doc.getTopEntities() != null && doc.getTopEntities().size()>0){
            lv_entity.getItems().addAll(doc.getTopEntities());
            l_info.setText("Entity is shown !");
        }
    }

    public void browseQueryPath(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        queryFile = directoryChooser.showDialog(b_queryPath.getScene().getWindow());
        tf_queryPath.setText(queryFile.getAbsolutePath());
        if(!tf_queryPath.getText().equals("") && isLoad==true){
            b_query.setDisable(false);
        }
        queryPath = queryFile.getAbsolutePath();
        cb_queryPath.setSelected(true);
        cb_Path.setSelected(false);


    }

    public void selectCities(){
        String city=cb_cities.getSelectionModel().getSelectedItem();
            if(!lv_city.getItems().contains(city)) {
                lv_city.getItems().add(city);
            }

    }

    public void removeCity(){
        lv_city.getItems().remove(lv_city.getSelectionModel().getSelectedItem());
    }

    public void canRun(){
        String tmp = tf_query.getText();
        if(!tmp.equals("") && isLoad==true)
            b_query.setDisable(false);
        else
            b_query.setDisable(true);
    }



    public void getAnswer(){
        lv_idQuery.getItems().clear();
        lv_returndocs.getItems().clear();
        lv_idQuery.getItems().addAll(viewModel.getIDs());
    }

    public void showAns(){
        lv_returndocs.getItems().clear();
        int value = lv_idQuery.getSelectionModel().getSelectedIndex();
        List<DocData> tmp = viewModel.getANS(value+1);
        lv_returndocs.getItems().addAll(tmp);
    }


}




