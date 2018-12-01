package Model;

import Model.Term.ATerm;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Posting {

    private int numberOfFile = 1;
    Queue<File>theFiles;
    Queue<File> merge1;
    File postDocs;

    public Posting() {
        theFiles = new LinkedList<>();
        merge1 = new LinkedList<>();
    }

    public void createPostingFileFirstTime(Map<ATerm, Map<String,Integer>> words) {
        if(true) {
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
            File file = new File("C:\\Users\\USER\\Desktop\\מערכות מידע דור\\סמסטר ד\\נושאים מתקדמים בתכנות\\SearchEngineJ\\src\\main\\java\\postings\\" + (numberOfFile++) + "");
            try {
                file.createNewFile();
                prepareForWriting(treeDict, file);
                theFiles.add(file);
                if(theFiles.size()==2){
                    mergeFiles(theFiles.poll(), theFiles.poll());
                    while(theFiles.size()==-1) {
                        //Thread t = new Thread(()->mergeFiles(theFiles.poll(),theFiles.poll()));
                        //t.start();
                        mergeFiles(theFiles.poll(), theFiles.poll());
                    }
                    while(merge1.size()==2){
                        //Thread t = new Thread(()->mergeFiles(merge1.poll(),merge1.poll()));
                        //t.start();
                        mergeFiles(merge1.poll(),merge1.poll());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    private void prepareForWriting(Map<ATerm,Map<String,Integer>> wordsInDictionary, File file) {
        StringBuffer wordsInfo = new StringBuffer();
        StringBuffer docNumber ;
        for (ATerm term : wordsInDictionary.keySet()) {
            docNumber = new StringBuffer();
            for(String docName:wordsInDictionary.get(term).keySet()){
                docNumber.append("{"+docName+":"+wordsInDictionary.get(term).get(docName)+"}");
            }
            if(Character.isUpperCase(term.finalName.charAt(0))){
                wordsInfo.append(term.finalName +'"'+ docNumber+"\n");
            }else{
                wordsInfo.append(term.finalName+'"' + docNumber+"\n");
            }
             // # number of [ is doc frequency (df), idf
        }
        writeToFile(wordsInfo,file);
    }

    private void mergeFiles(File file1, File file2) {
        Queue<String> lineFile1 = readFile(file1);
        Queue<String> lineFile2 = readFile(file2);

        StringBuffer add = new StringBuffer();


        String termInfoFromPos1="";
        String termInfoFromPos2="";


        if(lineFile1.size()>0 && lineFile2.size()>0){
            termInfoFromPos1 = lineFile1.poll();
            termInfoFromPos2 = lineFile2.poll();
        }

        while (lineFile1.size()!=0 && lineFile2.size()!=0) {

            int check = termInfoFromPos1.compareTo(termInfoFromPos2);
            // check when starts the a..b..c
            if(termInfoFromPos1.startsWith("back")){
                int x=4;
            }

            if (check > 0) {
                add.append(termInfoFromPos2 + "\n");
                if((lineFile1.size()!=0 && lineFile2.size()!=0)) {
                    termInfoFromPos2 = lineFile2.poll();
                }
            } else {
                add.append(termInfoFromPos1 + "\n");
                if(lineFile1.size()!=0 && lineFile2.size()!=0) {
                    termInfoFromPos1 = lineFile1.poll();
                }

            }
        }
        while (lineFile1.size()!=0) {
            add.append(lineFile1.poll() + "\n");
        }
        while (lineFile2.size()!=0) {
            add.append(lineFile2.poll() + "\n");
        }
        File file = new File( "C:\\Users\\USER\\Desktop\\מערכות מידע דור\\סמסטר ד\\נושאים מתקדמים בתכנות\\SearchEngineJ\\src\\main\\java\\postings\\" + (numberOfFile++) + "");
        try {
            file.createNewFile();
            file1.delete();
            file2.delete();
            merge1.add(file);

            writeToFile(add,file);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void createFileWithAllTerms(HashSet<String> allTerm) {
        File file = new File("C:\\Users\\USER\\Desktop\\מערכות מידע דור\\סמסטר ד\\נושאים מתקדמים בתכנות\\SearchEngineJ\\src\\main\\java\\postings\\"+"FileTerms");
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


    private void writeToFile(StringBuffer allTermInfo, File file){
        try {
            FileOutputStream out = new FileOutputStream(file);
            try {
                //Writer writer = new OutputStreamWriter(new GZIPOutputStream(out), "UTF-8");
                Writer writer = new OutputStreamWriter(out);
                try {
                    writer.write(allTermInfo.toString());
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




    public void writePerDoc(Map<String,String> docInfo){
        String bf="";
        if(postDocs==null){
            postDocs = new File("C:\\Users\\USER\\Desktop\\מערכות מידע דור\\סמסטר ד\\נושאים מתקדמים בתכנות\\SearchEngineJ\\src\\main\\java\\postings\\"+"FileDocs");
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




