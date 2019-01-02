package Model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ReadFile {


    Parse Parse ;
    DataCollector dataC;
    Set<String> languages = new HashSet<>();
    Set<String> docSet = new HashSet<>();
    Map<String,String>citiesMap = new HashMap<>();
    List<File> allFiles;
    int addFile = 0;
    int counterFiles = 0;
    int countDoc = 0;

    public ReadFile(String path,String stopWords, String PathPosting, boolean withStemming, DataCollector dataCollector) {
        this.dataC = dataCollector;
        Parse = new Parse(stopWords, PathPosting, withStemming, this.dataC);

        try {
            // Read all files from path
            allFiles = Files.walk(Paths.get(path)).
                    filter(Files::isRegularFile).
                    map(Path::toFile).
                    collect(Collectors.toList());
        } catch (IOException e1) {

        }
    }

    public void start(){

            for (File file : allFiles) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    Document doc = Jsoup.parse(new String(Files.readAllBytes(file.toPath())));
                    Elements elements = doc.getElementsByTag("DOC");

                    // For every doc in the file
                    // Cut all the string from <TEXT> until </TEXT>
                    // Send it to Parse
                    for (Element element : elements) {
                        countDoc++;
                        String docCity = element.getElementsByTag("DOC").select("F[P=104]").text();
                        if(docCity.equals("")) {
                            docCity = element.getElementsByTag("HEADER").select("F[P=104]").text();
                        }

                        if(docCity.equals("")) {
                            docCity = element.getElementsByTag("TEXT").select("F[P=104]").text();
                        }
                        //if the city contains more than 1 word, take the first one in capital letters
                        if(!docCity.equalsIgnoreCase(""))
                            docCity = docCity.split(" ")[0].toUpperCase();

                        String docText = element.getElementsByTag("TEXT").text();
                        String docName = element.getElementsByTag("DOCNO").text();
                        String docLanguage = element.getElementsByTag("DOC").select("F[P=105]").text();
                        if(!languages.contains(docLanguage)){
                            languages.add(docLanguage);
                        }
                        if(!docCity.equalsIgnoreCase("")) {
                            docSet.add(docCity);
                            if(citiesMap.containsKey(docCity)){
                                String tmp = citiesMap.get(docCity);
                                citiesMap.put(docCity,tmp+docName+":");
                            }else{
                                citiesMap.put(docCity,docCity+",{"+docName+":");
                            }
                        }
                        Parse.parse(docText, docName, docCity, true);

                    }

                }  catch (IOException e1) {

                }
                counterFiles++;
                if(counterFiles %50==0){
                    Parse.post.fromMapToPostFiles(Parse.allWordsDic);
                    Parse.post.writePerDoc(Parse.docInfo);
                    Parse.clearDictionary();
                }
            }

            if(Parse.docInfo.size()!=0) {
                Parse.post.fromMapToPostFiles(Parse.allWordsDic);
                Parse.post.writePerDoc(Parse.docInfo);
                Parse.clearDictionary();
            }

            Parse.post.setMap();
            Parse.post.startMerge();
            Parse.post.writeDictionary();
            Parse.post.setDocSet(docSet);
            Parse.post.setCitiesMap(citiesMap);
            Parse.post.createMap();
            Parse.post.createCapitalPost(Parse.getCapitalDictionary());
            Parse.setEntityPost();
            Parse.post.setLang(languages);
            //Parse.post.setEntitiesPost(Parse.getEntities());
            dataC.setNumberOfDocs(countDoc);
            dataC.setAverageNumOfDocs(Parse.avgDocs/countDoc);
            List<String>docCounter = new ArrayList<>();
            docCounter.add(countDoc+"");
            docCounter.add(Parse.avgDocs/countDoc+"");
            Parse.post.writePerDoc(docCounter);
            dataC.setLang(languages);


    }

    public void resetAll() {
        languages.clear();
        Parse.resetAll();

    }

    public Parse getParse(){
        return Parse;
    }
}
