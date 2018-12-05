package Model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ReadFile {


    ParseUnit Parse ;
    DataCollector dataC;
    Set<String> languages = new HashSet<>();

    public ReadFile(String path,String stopWords, String PathPosting, boolean withStemming, DataCollector dataCollector) {
        this.dataC = dataCollector;
        Parse = new ParseUnit(stopWords, PathPosting, withStemming, this.dataC);
        List<File> allFiles;
        int addFile = 0;
        int counterFiles =0;
        int countDoc = 0;
        try {
            // Read all files from path
            allFiles = Files.walk(Paths.get(path)).
                    filter(Files::isRegularFile).
                    map(Path::toFile).
                    collect(Collectors.toList());

            for (File file : allFiles) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    Document doc = Jsoup.parse(new String(Files.readAllBytes(file.toPath())));
                    Elements elements = doc.getElementsByTag("DOC");

                    // For every doc in the file
                    // Cut all the string from <TEXT> until </TEXT>
                    // Send it to Model.ParseUnit
                    for (Element element : elements) {
                        countDoc++;
                        String docCity = element.getElementsByTag("DOC").select("F[P=104]").text(); // why F[P=104]
                        if(docCity.equals("")) {
                            docCity = element.getElementsByTag("HEADER").select("F[P=104]").text();
                        }
                        if(docCity.equals("")) {
                            docCity = element.getElementsByTag("TEXT").select("F[P=104]").text();
                        }
                        if(!docCity.equalsIgnoreCase(""))
                            docCity = docCity.split(" ")[0].toUpperCase();
                        String docText = element.getElementsByTag("TEXT").text();
                        String docName = element.getElementsByTag("DOCNO").text();
                        String docLanguage = element.getElementsByTag("DOC").select("F[P=105]").text();
                        if(!languages.contains(docLanguage)){
                            languages.add(docLanguage);
                        }
                        String[] withoutSpaceText = docText.split(" "); // split the text by " "(space) into array
                        Parse.parse(withoutSpaceText, docName,docCity);
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                counterFiles++;
                if(counterFiles %2 ==0){
                    Parse.post.fromMapToPostFiles(Parse.allWordsDic);
                    Parse.post.writePerDoc(Parse.docInfo);
                    Parse.clearDictionary();
                    //counterFiles = 0;
                    System.out.println("Insert more 50 file " + (++addFile)*50);
                    if(counterFiles == 10)
                        break;
                }
            }
            //
            Parse.post.fromMapToPostFiles(Parse.allWordsDic);
            Parse.post.writePerDoc(Parse.docInfo);
            Parse.clearDictionary();

            Parse.post.createCapitalPost(Parse.getCapitalDictionary());
            Parse.post.setMap();
            Parse.post.startMerge();

            dataCollector.setLang(languages);
        } catch (IOException e) { }
    }

    public void resetAll() {
        languages.clear();

        Parse.resetAll();

    }
}
