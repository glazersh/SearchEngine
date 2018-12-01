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


    ParseUnit Parse = new ParseUnit();
    public ReadFile(String path) {
        List<File> allFiles = null;
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

                if(counter==50){
                    Parse.post.createPostingFileFirstTime(Parse.allWordsDic);
                    Parse.allWordsDic.clear();
                    Parse.post.writePerDoc(Parse.docInfo);
                    Parse.docInfo.clear();
                    counter = 0;
                }




            }
            Parse.post.createPostingFileFirstTime(Parse.allWordsDic);
            Parse.allWordsDic.clear();
            Parse.post.createFileWithAllTerms(Parse.allTerm);
        } catch (IOException e) { }
    }


    public static void convertTo(){
        List<String> lines = new ArrayList<>();
        Stack<String> lineInStack = new Stack<>();
        File f = new File("C:\\Users\\glazersh\\IdeaProjects\\SearchEngineJ\\src\\main\\java\\postings\\post15");
        try (GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(f));
             BufferedReader br = new BufferedReader(new InputStreamReader(gzip))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            File f2 = new File("C:\\Users\\glazersh\\IdeaProjects\\SearchEngineJ\\src\\main\\java\\postings\\post16");
            FileOutputStream out = new FileOutputStream(f2);
            Writer writer = new OutputStreamWriter(out);
            writer.write(lines.toString());
            writer.close();
            out.close();


        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


}
