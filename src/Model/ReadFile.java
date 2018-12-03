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
    public ReadFile(String path,String stopWords, String PathPosting, boolean withStemming) {
        Parse = new ParseUnit(stopWords, PathPosting, withStemming);
        List<File> allFiles = null;
        int addFile = 0;
        int counter =0;
        try {
            // Read all files from path
            allFiles = Files.walk(Paths.get(path)).
                    filter(Files::isRegularFile).
                    map(Path::toFile).
                    collect(Collectors.toList());

            for (File file : allFiles) {
                //System.out.println(file.getName());
                try {
                    FileInputStream fis = new FileInputStream(file);
                    Document doc = Jsoup.parse(new String(Files.readAllBytes(file.toPath())));
                    Elements elements = doc.getElementsByTag("DOC");

                    // For every doc in the file
                    // Cut all the string from <TEXT> until </TEXT>
                    // Send it to Model.ParseUnit
                    for (Element element : elements) {
                        if(true) {
                            String docText = element.getElementsByTag("TEXT").text();
                            String docName = element.getElementsByTag("DOCNO").text();
                            String docCity = element.getElementsByTag("F P=104").text();
                            if(!docCity.equals("")){
                                if(docCity.contains("<F P=104>")){
                                    int x=4;
                                }
                            }
                            String[] withoutSpaceText = docText.split(" "); // split the text by " "(space) into array
                            //System.out.println("~~~~~" + docName + "~~~~~~");
                            Parse.parse(withoutSpaceText, docName,docCity);

                        }
                        else{ // for debug
                            int doNothing;
                        }
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                counter++;

                if(counter %2==0){
                    Parse.post.fromMapToPostFiles(Parse.allWordsDic);
                    Parse.allWordsDic.clear();
                    Parse.post.writePerDoc(Parse.docInfo);
                    Parse.docInfo.clear();
                    //counter = 0;
                    System.out.println("Insert more 50 file " + (++addFile)*50);
                    if(counter == 32)
                        break;
                }
            }
            //Parse.post.fromMapToPostFiles(Parse.allWordsDic);
            //Parse.allWordsDic.clear();
            Parse.post.createFileWithAllTerms(Parse.allTerm);
            Parse.post.startMerge();
        } catch (IOException e) { }
    }

    public static void main(String [] args){
        long start = System.nanoTime();
        ReadFile rf = new ReadFile("C:\\Users\\USER\\Desktop\\search2018\\corpus","C:\\Users\\USER\\Desktop\\search2018\\SearchEngine\\src\\resources\\","C:\\Users\\USER\\Desktop\\search2018\\SearchEngine\\src\\Model\\postings",false);
        long end = System.nanoTime();
        System.out.println(end-start);
    }



}
