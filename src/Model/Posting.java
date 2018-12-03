package Model;

import Model.IO.CountryInfo;
import Model.Term.ATerm;

import java.io.*;
import java.util.*;

public class Posting {

    private int numberOfFileNo = 1;
    private int numberOfFileYes = 1;
    Queue<File>theFiles;
    Queue<File> merge1;
    File postDocs;
    File postDictionary;

    Queue<File>postFilesYes;
    Queue<File>postFilesNo;

    List<File>finalPostFileYes;
    List<File>finalPostFileNo;
    String path;
    boolean withStem;
    Map<CountryInfo,String>capitalTerms;

    DataCollector dataCollector;
    Map<String,Integer>bigDictionary = new HashMap<>(); // for the gui

    public Posting(String path, boolean withStem, DataCollector dataCollector) {
        theFiles = new LinkedList<>();
        merge1 = new LinkedList<>();

        finalPostFileYes = new ArrayList<>();
        finalPostFileNo = new ArrayList<>();

        postFilesYes = new LinkedList<>();
        postFilesNo = new LinkedList<>();

        this.dataCollector = dataCollector;

        postDictionary = new File(path+"\\Dictionary");
        if(postDictionary.exists()){
            try {
                postDictionary.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.withStem = withStem;
        this.path = path;
    }

    /**
     * get dictionary and write the term in the correct postFile by his first character
     * @param words
     */
    public void fromMapToPostFiles(Map<ATerm, Map<String,Integer>> words) {

        SortedMap<ATerm,Map<String,Integer>> treeDict = new TreeMap(new Comparator<ATerm>() {
            @Override
            public int compare(ATerm o1, ATerm o2) {
                return compare(o1.finalName,o2.finalName);
            }
            private int compare(String finalName, String finalName1) {
                return finalName.compareToIgnoreCase(finalName1);
            }
        });
        for(ATerm term:words.keySet()){
            treeDict.put(term,words.get(term));
        }
        prepareForWriting(treeDict);
    }

    // need to send it in the end !
    public void setMap(){
        dataCollector.setMap(bigDictionary);
    }

    private List<String> readFile(File file) {
        List<String> lines = new ArrayList<>();

        try (//GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
             FileInputStream out = new FileInputStream(file);
             BufferedReader br = new BufferedReader(new InputStreamReader(out))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace(System.err);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return lines;


    }

    /**
     * write all the term in StringBuffer and buffer from every kind by "}"
     * @param wordsInDictionary
     */
    private void prepareForWriting(Map<ATerm,Map<String,Integer>> wordsInDictionary) {

        StringBuffer termInfo = new StringBuffer();
        StringBuffer docNumber ;
        for (ATerm term : wordsInDictionary.keySet()) {
            docNumber = new StringBuffer();
            for (String docName : wordsInDictionary.get(term).keySet()) {
                docNumber.append("{" + docName + ":" + wordsInDictionary.get(term).get(docName));
            }
            termInfo.append(term.finalName+","+docNumber+"\n");
        }
        writeToFile(termInfo.toString());
    }

    private void writeToFile(String termTXT){
        File file;
        try {
            if(withStem) {
                file = new File(path + "\\" +"Y"+numberOfFileYes++);
            }else{
                file = new File(path + "\\" +"N"+numberOfFileNo++);
            }
            FileOutputStream out = new FileOutputStream(file);
            try {
                //Writer writer = new OutputStreamWriter(new GZIPOutputStream(out), "UTF-8");
                Writer writer = new OutputStreamWriter(out);
                try {
                    writer.write(termTXT);
                    if(withStem){
                        postFilesYes.add(file);
                    }else{
                        postFilesNo.add(file);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    writer.close();
                }
            } finally {
                out.close();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // need to remove it !
    public void createFileWithAllTerms(HashSet<String> allTerm) {
        File file = new File(path+"\\"+"FileTerms");
        try {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            Writer writer = new OutputStreamWriter(out);
            try {
                for (String term : allTerm) {
                    writer.write(term + "\n");
                }
            }finally {
                writer.close();
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Fix it !
     * @param docInfo
     */
    public void writePerDoc(Map<String,String> docInfo){
        String bf="";
        if(postDocs==null){
            postDocs = new File(path +"\\"+"FileDocs");
            try {
                postDocs.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileOutputStream out = new FileOutputStream(postDocs,true);
            try {
                //Writer writer = new OutputStreamWriter(new GZIPOutputStream(out), "UTF-8");
                Writer writer = new OutputStreamWriter(out);
                try {
                    for (String perDoc:docInfo.keySet()) {
                        writer.write(perDoc+":"+docInfo.get(perDoc)+"\n");
                    }
                    writer.write(bf);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    writer.close();
                }
            } finally {
                out.close();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startMerge() {
        if(!withStem){
            while(postFilesNo.size() > 1){
                File f1=postFilesNo.poll();
                File f2=postFilesNo.poll();
                mergeFile(f1,f2);
            }
            makeThePostFiles(postFilesNo.poll());

            int x =4;
            // final here
        }
    }

    private void makeThePostFiles(File file) {
        List<String> fileInfo = new ArrayList<>();
        StringBuffer bf = new StringBuffer();

        FileInputStream out1 ;
        BufferedReader br1;
        String line;
        String nextLine;
        String []term;
        String []nextTerm;

        try {
            out1 = new FileInputStream(file);
            br1 =new BufferedReader(new InputStreamReader(out1));
            boolean isLower = false;
            boolean isLetter = false;
            boolean first = true;
            line = br1.readLine();
            term = line.split(",\\{");
            char d ='0';
            char l = 'a';
            if(Character.isLetter(term[0].charAt(0))){
                isLetter = true;
                if(Character.isLowerCase(term[0].charAt(0))){
                    isLower = true;
                }
            }

            bf.append(",{"+term[1]);
            while ((nextLine=br1.readLine()) != null) {
                nextTerm = nextLine.split(",\\{");
                if(term[0].equalsIgnoreCase(nextTerm[0])){
                    if(isLetter) {
                        if (!isLower) {
                            bf.append("{" + nextTerm[1]);
                        } else {
                            isLower = true;
                            bf.append("{" + nextTerm[1]);
                        }
                    }else{
                        bf.append("{" + nextTerm[1]);
                    }
                }else {
                    if (nextTerm[0].charAt(0) == d) {
                        String tmp = term[0] + bf.toString();
                        fileInfo.add(tmp);
                        writeTheFinalFilePost(fileInfo, "sign");
                        fileInfo.clear();
                        bf = new StringBuffer();
                        d=(char)(d-1);
                    }
                    else {
                        if (nextTerm[0].charAt(0) == l) {
                            if (first) {
                                String tmp = term[0] + bf.toString();
                                fileInfo.add(tmp);
                                writeTheFinalFilePost(fileInfo, "numbers Final");
                                first = false;
                            } else {
                                if (isLetter) {
                                    if (isLower) {
                                        term[0] = term[0].toLowerCase();
                                    } else {
                                        term[0] = term[0].toUpperCase();
                                    }
                                }
                                String tmp = term[0] + bf.toString();
                                fileInfo.add(tmp);
                                writeTheFinalFilePost(fileInfo, (char) (l - 1) + " Final");
                            }
                            fileInfo.clear();
                            l = (char) (l + 1);
                            bf = new StringBuffer();
                        }
                    }
                    if (bf.length() != 0) {
                        if (isLetter) {
                            if (isLower) {
                                term[0] = term[0].toLowerCase();
                            } else {
                                term[0] = term[0].toUpperCase();
                            }
                        }
                        String tmp = term[0] + bf.toString();
                        fileInfo.add(tmp);
                        bf = new StringBuffer();
                        bf.append(",{" + nextTerm[1]);
                    }

                    else
                        bf.append(",{"+nextTerm[1]);
                    term = nextTerm;
                    isLower = false;
                    isLetter = false;
                    if (Character.isLetter(term[0].charAt(0))) {
                        isLetter = true;
                        if (Character.isLowerCase(term[0].charAt(0))) {
                            isLower = true;
                        }else{
                            int x=4;
                        }
                    }

                }
            }

            writeTheFinalFilePost(fileInfo, (char)(l-1) + " Final");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }







    }

    private void writeTheFinalFilePost(List<String>info, String namePost) {
        File file;
        String line;
        StringBuffer bf = new StringBuffer();
        try {
            if(withStem){
                namePost = "Y-"+namePost;
            }else{
                namePost = "N-"+namePost;
            }
            file = new File(path + "\\" +namePost);

            FileWriter out = new FileWriter(file);
            FileWriter outDic = new FileWriter(postDictionary,true);
            try {
                //Writer writer = new OutputS treamWriter(new GZIPOutputStream(out), "UTF-8");
                Writer writer = new BufferedWriter(out);
                Writer writerDic = new BufferedWriter(outDic);
                try {
                    for(int i=0;i<info.size();i++){
                        line = info.get(i);
                        bf.append(buildDictionary(line, i,namePost));
                        writer.write(line+'\n');
                    }
                    writer.flush();
                    writerDic.write(bf.toString());
                    writerDic.flush();
                    if(withStem){
                        finalPostFileYes.add(file);
                    }else{
                        finalPostFileNo.add(file);
                    }
                    //writer.flush();
                    ///
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    writer.close();
                    writerDic.close();
                }
            } finally {
                out.close();
                outDic.close();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildDictionary(String line, int index,String namePostFile) {
        String []info = line.split(",\\{");
        String nameTerm = info[0];
        String[]docsInfo = info[1].split("\\{");
        int docFrequency = docsInfo.length;
        int counter = 0;
        for(int i=0;i<docsInfo.length;i++){
            counter+=Integer.parseInt(docsInfo[i].split(":")[1]);
        }
        String lineInPost = nameTerm+",{"+counter+":"+docFrequency+":"+namePostFile+"/"+index+"\n";
        bigDictionary.put(nameTerm, counter);
        return lineInPost;

    }

    public void createCapitalPost(Map<CountryInfo, String> capitalDictionary) {
        File capitalPost = new File(path + "\\" + "capitalPost");
        FileWriter out = null;
        try {
            capitalPost.createNewFile();
            BufferedWriter writer = null;
            try {
                out = new FileWriter(capitalPost);
                writer = new BufferedWriter(out);
                for (CountryInfo countryInfo : capitalDictionary.keySet()) {
                    writer.write(countryInfo.getCapitalName() + "-" + capitalDictionary.get(countryInfo) + "\n");
                }
                writer.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                writer.close();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class SortIgnoreCase implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }

    private void mergeFile(File f1, File f2) {

        List<String>readF1 = readFile(f1);
        List<String>readF2 = readFile(f2);
        readF1.addAll(readF2);
        Collections.sort(readF1, new SortIgnoreCase());

        writeToFileList(readF1);
    }

    private void writeToFileList(List<String> readF1) {
        File file;
        try {
            if(withStem) {
                file = new File(path + "\\" +"Y"+numberOfFileYes++);
            }else{
                file = new File(path + "\\" +"N"+numberOfFileNo++);
            }
            FileOutputStream out = new FileOutputStream(file);
            try {
                //Writer writer = new OutputStreamWriter(new GZIPOutputStream(out), "UTF-8");
                Writer writer = new OutputStreamWriter(out);
                try {
                    for(int i=0;i<readF1.size();i++) {
                        writer.write(readF1.get(i) + "\n");
                    }
                    if(withStem){
                        postFilesYes.add(file);
                    }else{
                        postFilesNo.add(file);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    writer.close();
                }
            } finally {
                out.close();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void buildCapitalPostFile(){
        File BigDic = new File(path + "\\" + "BigDic");
        FileWriter out = null;
        try {
            BigDic.createNewFile();
            BufferedWriter writer = null;
            try {
                out = new FileWriter(BigDic);
                writer = new BufferedWriter(out);
                for (String str : bigDictionary.keySet()) {
                    writer.write(str+"-"+bigDictionary.get(str) + "\n");
                }
                writer.flush();
            }catch (IOException e) {
                e.printStackTrace();
            }finally {
                writer.close();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}