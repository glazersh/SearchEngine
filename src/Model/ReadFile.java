package Model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ReadFile {


    ParseUnit Parse ;
    DataCollector dataC;

    public ReadFile(String path,String stopWords, String PathPosting, boolean withStemming, DataCollector dataCollector) {
        this.dataC = dataCollector;
        Parse = new ParseUnit(stopWords, PathPosting, withStemming, this.dataC);
        List<File> allFiles = null;
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
                        String docText = element.getElementsByTag("TEXT").text();
                        String docName = element.getElementsByTag("DOCNO").text();
                        String docCity = element.getElementsByTag("F P=104").text();
                        String[] withoutSpaceText = docText.split(" "); // split the text by " "(space) into array
                        //System.out.println("~~~~~" + docName + "~~~~~~");
                        Parse.parse(withoutSpaceText, docName,docCity);
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                counterFiles++;
                if(counterFiles == 50){
                    Parse.post.fromMapToPostFiles(Parse.allWordsDic);
                    Parse.allWordsDic.clear();
                    Parse.post.writePerDoc(Parse.docInfo);
                    Parse.docInfo.clear();
                    counterFiles = 0;
                    System.out.println("Insert more 50 file " + (++addFile)*50);
//                    if(counter == 32)
//                        break;
                }
            }
            System.out.println("number of Doc - "+countDoc);
            Parse.post.fromMapToPostFiles(Parse.allWordsDic);
            Parse.allWordsDic.clear();
            Parse.post.createCapitalPost(Parse.getCapitalDictionary());
            Parse.post.setMap();
            Parse.post.startMerge();
        } catch (IOException e) { }
    }

}
