package Model;
import Model.Term.ATerm;

import java.io.*;
import java.util.*;

public class Indexer {

    private int numberOfFile = 1;
    Queue<File>theFiles;
    Queue<File> merge1;
    File postDocs;
    File postDictionary;

    Queue<File>postFilesYes;
    Queue<File>postFilesNo;

    List<File>finalPostFileYes;
    List<File>finalPostFileNo;
    String path;
    static String pathWithStem;
    static int numberTerms = 0;


    boolean withStem;

    DataCollector dataCollector;
    Map<String,Integer>bigDictionary = new HashMap<>(); // for the gui

    static Map<String,String> dictionaryToLoad = new HashMap<>();



    public Indexer(String path, boolean withStem, DataCollector dataCollector) {
        theFiles = new LinkedList<>();
        merge1 = new LinkedList<>();

        finalPostFileYes = new ArrayList<>();
        finalPostFileNo = new ArrayList<>();

        postFilesYes = new LinkedList<>();
        postFilesNo = new LinkedList<>();

        this.dataCollector = dataCollector;
        this.withStem = withStem;


        File f;
        if(withStem) {
            f = new File(path + "\\Y");
            pathWithStem=path+"\\Y\\";
        }
        else {
            f = new File(path + "\\N");
            pathWithStem=path+"\\N\\";
        }
        f.mkdirs();
        postDictionary = new File(pathWithStem+"\\Dictionary");
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
        File file;
        try {
            file = new File(pathWithStem+numberOfFile++);
            FileOutputStream out = new FileOutputStream(file);
            try {
                //Writer writer = new OutputStreamWriter(new GZIPOutputStream(out), "UTF-8");
                Writer writer = new OutputStreamWriter(out);
                try {
                    for (ATerm term : wordsInDictionary.keySet()) {
                        docNumber = new StringBuffer();
                        for (String docName : wordsInDictionary.get(term).keySet()) {
                            docNumber.append("{" + docName + ":" + wordsInDictionary.get(term).get(docName));
                        }
                        String tmp = term.finalName+","+docNumber+"\n";
                        writer.write(tmp);
                    }
                    writer.flush();
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

    /**
     * Fix it !
     * @param docInfo
     */
    public void writePerDoc(List<String>docInfo){
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
                try (Writer writer = new OutputStreamWriter(out)) {
                    for (String perDoc : docInfo) {
                        writer.write(perDoc + "\n");
                    }
                    writer.write(bf);
                } catch (IOException e) {
                    e.printStackTrace();
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
                MergeFiles(f1,f2);
                f1.delete();
                f2.delete();
            }
            makeThePostFiles(postFilesNo.poll());

        }else{
            while(postFilesYes.size() > 1){
                File f1=postFilesYes.poll();
                File f2=postFilesYes.poll();
                MergeFiles(f1,f2);
                f1.delete();
                f2.delete();
            }
            makeThePostFiles(postFilesYes.poll());
        }
        dataCollector.setNumberOfTerms(numberTerms);
    }

    private void MergeFiles(File file1, File file2) {
        File file = new File(pathWithStem+numberOfFile++);
        try {
            file.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String>lines = new ArrayList<>();
        BufferedWriter bw = null;
        BufferedReader br1 = null;
        BufferedReader br2 = null;
        FileWriter fw = null;
        FileReader fr1 = null;
        FileReader fr2 = null;

        try {
            fr1 = new FileReader(file1);
            fr2 = new FileReader(file2);
            br1 = new BufferedReader(fr1);
            br2 = new BufferedReader(fr2);
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);

            String sCurrentLine1;
            String sCurrentLine2;
            int counter = 0;
            char stop = '0';
            String tmp1 = "";
            String tmp2 = "";

            while(stop != 'z'+1) {
                while((sCurrentLine1 = br1.readLine()) !=null) {
                    if (Character.toLowerCase(sCurrentLine1.charAt(0)) != stop) {
                        lines.add(sCurrentLine1 + "\n");
                    }else{
                        tmp1 = sCurrentLine1;
                        break;
                    }
                }
                while((sCurrentLine2 = br2.readLine()) !=null ) {
                    if (sCurrentLine2.length()>0&&Character.toLowerCase(sCurrentLine2.charAt(0)) != stop) {
                        lines.add(sCurrentLine2 + "\n");
                    }else{
                        tmp2 = sCurrentLine2;
                        break;
                    }
                }
                Collections.sort(lines,new SortIgnoreCase());
                for(String l:lines){
                    bw.write(l);
                }
                bw.flush();
                lines.clear();
                lines.add(tmp1+"\n");
                lines.add(tmp2+"\n");
                counter++;
                if(counter==1)
                    stop = 'a';
                stop = (char)(stop+1);
            }
            bw.flush();
            while((sCurrentLine1 = br1.readLine()) !=null) {
                lines.add(sCurrentLine1 + "\n");
            }
            while((sCurrentLine2 = br2.readLine()) !=null ) {
                lines.add(sCurrentLine2 + "\n");
            }
            Collections.sort(lines,new SortIgnoreCase());
            for(String l:lines){
                bw.write(l);
            }
            bw.flush();
            if(withStem)
                postFilesYes.add(file);
            else
                postFilesNo.add(file);
        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (br1 != null) {
                    br1.close();
                    br2.close();
                    bw.close();
                }

                if (fr1 != null) {
                    fr1.close();
                    fr2.close();
                    bw.close();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void makeThePostFiles(File file) {
        List<String> fileInfo = new ArrayList<>();
        StringBuffer bf = new StringBuffer();
        FileInputStream out1 = null;
        BufferedReader br1=null;
        String line;
        String nextLine;
        String []term;
        String []nextTerm;
        try {
            out1 = new FileInputStream(file);
            br1 = new BufferedReader(new InputStreamReader(out1));
            boolean isLower = false;
            boolean isLetter = false;
            boolean first = true;
            line = br1.readLine();
            term = line.split(",\\{");
            char d = '0';
            char l = 'a';
            if (Character.isLetter(term[0].charAt(0))) {
                isLetter = true;
                if (Character.isLowerCase(term[0].charAt(0))) {
                    isLower = true;
                }
            }
            bf.append(",{" + term[1]);
            while ((nextLine = br1.readLine()) != null) {
                nextTerm = nextLine.split(",\\{");
                if (term[0].equalsIgnoreCase(nextTerm[0])) {
                    if (isLetter) {
                        if (!isLower ){
                            if(Character.isLowerCase(nextTerm[0].charAt(0))) {
                                isLower = true;
                            }
                        }
                    }
                    bf.append("{" + nextTerm[1]);
                } else {
                    if (nextTerm[0].charAt(0) == d) {
                        String tmp = term[0] + bf.toString();
                        fileInfo.add(tmp);
                        writeTheFinalFilePost(fileInfo, "sign");
                        fileInfo.clear();
                        bf.setLength(0);
                        d = (char) (d - 1);
                    } else {
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
                            bf.setLength(0);
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
                    } else
                        bf.append(",{" + nextTerm[1]);
                    term = nextTerm;
                    isLower = false;
                    isLetter = false;
                    if (Character.isLetter(term[0].charAt(0))) {
                        isLetter = true;
                        if (Character.isLowerCase(term[0].charAt(0))) {
                            isLower = true;
                        }
                    }
                }
            }
            String tmp = term[0] + bf.toString();
            fileInfo.add(tmp);
            writeTheFinalFilePost(fileInfo, (char) (l - 1) + " Final");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            br1.close();
            out1.close();
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writeTheFinalFilePost(List<String>info, String namePost) {
        numberTerms+=info.size();
        File file;
        String line;
        StringBuffer bf = new StringBuffer();
        try {
            if(withStem){
                namePost = "Y-"+namePost;
            }else{
                namePost = "N-"+namePost;
            }
            file = new File(pathWithStem +namePost);

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
        dictionaryToLoad.put(nameTerm,namePostFile+"/"+index);
        return lineInPost;

    }

    public void createCapitalPost(Map<String, String> capitalDictionary) {
        File capitalPost = new File(path + "\\" + "capitalPost");
        FileWriter out = null;
        try {
            capitalPost.createNewFile();
            BufferedWriter writer = null;
            try {
                out = new FileWriter(capitalPost);
                writer = new BufferedWriter(out);
                for (String str : capitalDictionary.keySet()) {
                    writer.write(capitalDictionary.get(str) + "\n");
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

    public void resetAll() {
        theFiles.clear();
        merge1.clear();
        postFilesNo.clear();
        postFilesYes.clear();
        finalPostFileNo.clear();
        finalPostFileYes.clear();
        bigDictionary.clear();


    }

    public void writeDictionary() {
        File file;
        try {
            if(withStem) {
                file = new File(path + "\\" +"Y-DictionaryToLoad");
            }else{
                file = new File(path + "\\" +"N-DictionaryToLoad");
            }
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            try {
                //Writer writer = new OutputStreamWriter(new GZIPOutputStream(out), "UTF-8");
                Writer writer = new OutputStreamWriter(out);
                try {
                    for(String term:dictionaryToLoad.keySet()){
                        writer.write(term+",{"+dictionaryToLoad.get(term) + "\n");
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

    public class SortIgnoreCase implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }

    public static void load(File file){
        List<String> lines = new ArrayList<>();
        dictionaryToLoad = new HashMap<>();
        try (//GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
             FileInputStream out = new FileInputStream(file);
             BufferedReader br = new BufferedReader(new InputStreamReader(out))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            Collections.sort(lines);
            for(String term:lines){
                String []tmp = term.split(",\\{");
                dictionaryToLoad.put(tmp[0],tmp[1]);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace(System.err);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

}