package Model;

import Model.Term.ATerm;

import java.io.*;
import java.util.*;

public class Posting {

    private int numberOfFile = 1;
    Queue<File>theFiles;
    Queue<File> merge1;
    File postDocs;
    List<File>postFiles;
    final String path =  "C:\\Users\\dorlev\\IdeaProjects\\SearchEngine\\src\\Model\\postings\\";
    List<String>typeOfFile;

    public Posting() {
        theFiles = new LinkedList<>();
        merge1 = new LinkedList<>();
        postFiles = new ArrayList<>();
        typeOfFile = new ArrayList();
        setTypeOfFile();
        buildFiles();
    }

    /**
     * all the file kind
     * 0-9, a-z, signs
     */
    private void setTypeOfFile(){
        for(int i=0;i<36;i++){
            if(i<10){
                typeOfFile.add(i+"");
            }
            else{
                typeOfFile.add((char)(i+87)+"");
            }
        }
    }

    /**
     * build the postFile
     */
    private void buildFiles(){
        for(int i=0;i<37;i++) {
            File file;
            if (i == 0) {
                file = new File( path + "signs");
            } else {
                file = new File (path+typeOfFile.get(i-1));
            }
            try {
                if(file.exists()){
                    file.delete();
                }
                file.createNewFile();
                postFiles.add(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("build all postFile");

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
        StringBuffer wordsInfo = new StringBuffer();
        StringBuffer docNumber ;
        boolean changeToLetter = true;
        boolean changeToDigit = true;
        char letter = 'a';
        char digit = '0';
        // term
        for (ATerm term : wordsInDictionary.keySet()) {
            docNumber = new StringBuffer();
            char firstCharacter = term.finalName.charAt(0);
            // signs
            if(!Character.isLetterOrDigit(firstCharacter)) {
                // doc and counter
                for (String docName : wordsInDictionary.get(term).keySet()) {
                    docNumber.append("{" + docName + ":" + wordsInDictionary.get(term).get(docName));
                }
                wordsInfo.append(term.finalName +","+ docNumber + "\n");
                continue;
            }
            // 0-9
            if(Character.isDigit(firstCharacter)) {
                // doc and counter
                for (String docName : wordsInDictionary.get(term).keySet()) {
                    docNumber.append("{" + docName + ":" + wordsInDictionary.get(term).get(docName));
                }
                if(changeToDigit){
                    wordsInfo.append("}" + term.finalName +","+ docNumber + "\n");
                    changeToDigit = false;
                    continue;
                }
                if(Character.toLowerCase(digit) == Character.toLowerCase(firstCharacter))
                    wordsInfo.append(term.finalName +","+ docNumber + "\n");
                else {
                    digit = (char) (digit+1);
                    while (Character.toLowerCase(digit) != Character.toLowerCase(firstCharacter)){
                        digit = (char) (digit+1);
                        wordsInfo.append("}");
                    }
                    wordsInfo.append("}" + term.finalName +","+ docNumber + "\n");
                    //letter = (char) (letter+1);
                }
                continue;
            }
            //a-z
            if(Character.isLetter(firstCharacter)) {
                // doc and counter
                for (String docName : wordsInDictionary.get(term).keySet()) {
                    docNumber.append("{" + docName + ":" + wordsInDictionary.get(term).get(docName));
                }
                if(changeToLetter){
                    wordsInfo.append("}" + term.finalName +","+ docNumber + "\n");
                    changeToLetter = false;
                    continue;
                }
                if(Character.toLowerCase(letter) == Character.toLowerCase(firstCharacter))
                    wordsInfo.append(term.finalName +","+ docNumber + "\n");
                else {
                    letter = (char) (letter+1);
                    while (Character.toLowerCase(letter) != Character.toLowerCase(firstCharacter)){
                        letter = (char) (letter+1);
                        wordsInfo.append("}");
                    }
                    wordsInfo.append("}" + term.finalName +","+ docNumber + "\n");
                    //letter = (char) (letter+1);
                }
                continue;
            }

        }
        writeToAllFiles(wordsInfo);
    }


    public void checkTheFiles(){
        for(int i=0;i<postFiles.size();i++){
            uniteTerm(postFiles.get(i));
        }
    }

    /**
     * check if exist duplicate term, and join them
     * @param file
     */
    private void uniteTerm(File file) {
        List<String> lines = new ArrayList<>();
        FileInputStream out;
        BufferedReader br;
        //GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
        try{
            out = new FileInputStream(file);
            br = new BufferedReader(new InputStreamReader(out));
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            Collections.sort(lines);
            lines = findDuplicateTerm(lines);
            file.delete();
            file.createNewFile();
            writeUniceTerm(file,lines);
            // write back here
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writeUniceTerm(File file, List<String>allLines){
        try {
            FileOutputStream out = new FileOutputStream(file);
            try {
                //Writer writer = new OutputStreamWriter(new GZIPOutputStream(out), "UTF-8");
                Writer writer = new OutputStreamWriter(out);
                try {
                    for (String line:allLines)
                    writer.write(line+"\n");
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

    private List<String> findDuplicateTerm(List<String> lines) {
        List <String>newLines= new ArrayList<>();
        StringBuffer bf = new StringBuffer();
        boolean isUpper = true;

        String line = lines.get(0);
        bf.append(line);
        String term = line.split(",\\{")[0];
        String nextLine = lines.get(1);
        String nextTerm = nextLine.split(",\\{")[0];

        for(int i=2;i<lines.size()-1;i++) {

            if (nextTerm.equalsIgnoreCase(term)) {
                if(Character.isUpperCase(term.charAt(0)) && Character.isLowerCase(nextTerm.charAt(0))){
                    int x=4;
                }
                if(Character.isUpperCase(nextTerm.charAt(0)) && Character.isLowerCase(term.charAt(0))){
                    int x=4;
                }
                String InfoTerm = nextLine.substring(nextTerm.length() + 1);
                bf.append(InfoTerm);
                nextLine = lines.get(i + 1);
                nextTerm = nextLine.split(",\\{")[0];
            } else {
                newLines.add(bf.toString());
                bf = new StringBuffer();
                term = nextTerm;
                line = nextLine;
                bf.append(line);
                nextLine = lines.get(i + 1);
                nextTerm = nextLine.split(",\\{")[0];
            }
        }
        if(nextTerm.equalsIgnoreCase(term)){
            String InfoTerm = nextLine.substring(nextTerm.length() + 1);
            bf.append(InfoTerm);
            newLines.add(bf.toString());
        }else{
            newLines.add(bf.toString());
            newLines.add(nextLine);
        }
        int x=4;
        return newLines;
    }


    public void createFileWithAllTerms(HashSet<String> allTerm) {
        File file = new File("C:\\Users\\dorlev\\IdeaProjects\\SearchEngine\\src\\Model\\postings\\"+"FileTerms");
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


    private void writeToAllFiles(StringBuffer allTermInfo){
        String[]term = allTermInfo.toString().split("}");
        for(int i=0;i<postFiles.size();i++) {
            try {
                FileOutputStream out = new FileOutputStream(postFiles.get(i),true);
                try {
                    //Writer writer = new OutputStreamWriter(new GZIPOutputStream(out), "UTF-8");
                    Writer writer = new OutputStreamWriter(out);
                    try {
                        writer.write(term[i]);
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


    public void writePerDoc(Map<String,String> docInfo){
        String bf="";
        if(postDocs==null){
            postDocs = new File("C:\\Users\\dorlev\\IdeaProjects\\SearchEngine\\src\\Model\\postings\\"+"FileDocs");
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




