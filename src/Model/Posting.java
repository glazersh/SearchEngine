package Model;

import Model.Term.ATerm;

import java.io.*;
import java.util.*;

public class Posting {

    private int numberOfFileNo = 1;
    private int numberOfFileYes = 1;
    Queue<File>theFiles;
    Queue<File> merge1;
    File postDocs;
    List<File>postFilesYes;
    List<File>postFilesNo;
    String path;
    boolean withStem;

    public Posting(String path, boolean withStem) {
        theFiles = new LinkedList<>();
        merge1 = new LinkedList<>();
        postFilesYes = new LinkedList<>();
        postFilesNo = new LinkedList<>();
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

    private Queue<String> readFile(File file) {
        List<String> lines = new ArrayList<>();
        Queue<String> lineInQueue = new LinkedList<>();

        try (//GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
             FileInputStream out = new FileInputStream(file);
             BufferedReader br = new BufferedReader(new InputStreamReader(out))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            //Collections.sort(lines, Collections.reverseOrder());

            for(String lineInFile:lines){
                lineInQueue.add(lineInFile);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace(System.err);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return lineInQueue;


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
}




