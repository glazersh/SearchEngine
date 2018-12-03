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
    Queue<File>postFilesYes;
    Queue<File>postFilesNo;
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

    private List<String> readFile(File file) {
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

            //for(String lineInFile:lines){
            //    lineInQueue.add(lineInFile);
            //}

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

    public void startMerge() {
        if(!withStem){
            while(postFilesNo.size() > 1){
                File f1=postFilesNo.poll();
                File f2=postFilesNo.poll();
                mergeFile(f1,f2);
            }/*
            File lastFile1 = postFilesNo.poll();
            File lastFile2 = postFilesNo.poll();
            mergeLastTime(lastFile1,lastFile2);
*/

            //finalFile1.addAll(finalFile2);
            //Collections.sort(finalFile1,new SortIgnoreCase());
            //List<String> postFileFina = findDuplicate(finalFile1);
            int x =4;
            // final here
        }
    }

    public class SortIgnoreCase implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }

    private List<String> findDuplicate(List<String> linePerChar) {
        List <String> finalTerms = new ArrayList<>();
        StringBuffer bf = new StringBuffer();

        String[]term; // current term
        String[]nextTerm; // check the next Term

        String lineTerm;
        String nextLineTerm;

        int index = 1;
        boolean isLower = false;
        boolean isLetter = false;

        lineTerm = linePerChar.get(0); // line of term
        nextLineTerm = linePerChar.get(1);// line of first Term
        term = lineTerm.split(",\\{");// firstTerm
        nextTerm = nextLineTerm.split(",\\{");// line of first Term
        if(Character.isLetter(term[0].charAt(0))){
            isLetter = true;
        }
        if(isLetter && Character.isLowerCase(term[0].charAt(0))){
            isLower = true;
        }
        bf.append("{"+term[1]);
        while(index!=linePerChar.size()-1) {
            if (term[0].equalsIgnoreCase(nextTerm[0])) { // are the same term, now check if upper or lower
                if(isLetter && !isLower) {
                    if(term[0].equals(nextTerm[0])){
                        bf.append("{"+nextTerm[1]);
                    }else{
                        isLower = true;
                        bf.append("{"+nextTerm[1]);
                    }
                }else{
                    bf.append("{"+nextTerm[1]);
                }
                index= index+1;
                lineTerm = nextLineTerm;
                nextLineTerm = linePerChar.get(index);
                nextTerm = nextLineTerm.split(",\\{");
                if(Character.isLetter(nextTerm[0].charAt(0))){
                    isLetter = true;
                    isLower = false;
                }
                if(isLetter && Character.isLowerCase(nextTerm[0].charAt(0))){
                    isLower = true;
                }

            }
            else{
                if(isLetter) {
                    if (isLower) {
                        term[0] = term[0].toLowerCase();
                    } else {
                        term[0] = term[0].toUpperCase();
                    }
                }
                finalTerms.add(term[0]+bf.toString());
                bf = new StringBuffer("{"+nextTerm[1]);
                term = nextTerm;
                lineTerm = nextLineTerm;
                index= index+1;
                nextLineTerm = linePerChar.get(index);
                nextTerm = nextLineTerm.split(",\\{");
                if(Character.isLetter(nextTerm[0].charAt(0))){
                    isLetter = true;
                    isLower = false;
                }
                if(isLetter && Character.isLowerCase(nextTerm[0].charAt(0))){
                    isLower = true;
                }
            }
        }
        if(term[0].equalsIgnoreCase(nextTerm[0])){
            if(isLetter && !isLower) {
                if(term[0].equals(nextTerm[0])){
                    bf.append("{"+nextTerm[1]);
                }else{
                    isLower = true;
                    bf.append("{"+nextTerm[1]);
                }
            }else{
                bf.append("{"+nextTerm[1]);
                finalTerms.add(bf.toString());
            }
        }else{
            if(isLetter) {
                if (isLower) {
                    term[0] = term[0].toLowerCase();
                } else {
                    term[0] = term[0].toUpperCase();
                }
            }
            finalTerms.add(term[0]+bf.toString());
            finalTerms.add(nextLineTerm);
        }

        return finalTerms;
    }

    private int endOfTerm(String line){
        for(int i=0;i<line.length()-1;i++){
            if(line.charAt(i) == ',' && line.charAt(i+1)=='{')
                return i;
        }
        return 0;
    }

    private void mergeLastTime(File lastFile1, File lastFile2) {
//        List <String> file1Txt = readFile(lastFile1);
//        List <String> file2Txt = readFile(lastFile2);
        List<String> mergeFile = new ArrayList<>();
        boolean first = true;
        StringBuffer bf = new StringBuffer();
        FileInputStream out1 ;
        FileInputStream out2 ;
        BufferedReader br1;
        BufferedReader br2;
        String line1;
        String line2;
        try {
            out1 = new FileInputStream(lastFile1);
            out2 = new FileInputStream(lastFile2);
            br1 =new BufferedReader(new InputStreamReader(out1));
            br2 = new BufferedReader(new InputStreamReader(out2));
            line1 = br1.readLine();
            line2 = br2.readLine();
            while (true) {
                int sizeTermF1 = endOfTerm(line1);
                String term1 = line1.split(",\\{")[0];

                //int sizeTermF2 = endOfTerm(line2);
                String term2 = line2.split(",\\{")[0];
                if(term2.equalsIgnoreCase("0")){
                    int x=4;
                }
                if(term1.compareToIgnoreCase(term2) == 0){// equals
                    bf.append(line1);
                    bf.append(line2.split(",\\{")[1]);
                    while((line1=br1.readLine()).split(",\\{")[0].equalsIgnoreCase(term1)){
                        bf.append(line1.substring(sizeTermF1));
                    }
                    while((line2=br2.readLine()).split(",\\{")[0].equalsIgnoreCase(term2)){
                        bf.append(line2.split(",\\{")[1]);
                    }
                    // write here
                    mergeFile.add(bf.toString());
                    bf = new StringBuffer();
                    continue;
                }
                if(bf.length()!=0){
                    // write here
                    mergeFile.add(bf.toString());
                    bf = new StringBuffer();
                }
                if(term1.compareToIgnoreCase(term2) < 0){// term1 is smaller
                    String lineTmp = line1;
                    while((line1=br1.readLine()).split(",\\{")[0].equalsIgnoreCase(term1)){
                        bf.append(line1.split(",\\{")[1]);
                    }
                    mergeFile.add(lineTmp + bf.toString());
                    bf = new StringBuffer();
                    if(line1 == null)
                        break;
                }else{
                    String lineTmp = line2;
                    while((line2=br2.readLine()).split(",\\{")[0].equalsIgnoreCase(term2)){
                        bf.append(line2.split(",\\{")[1]);
                    }
                    mergeFile.add(lineTmp + bf.toString());
                    bf = new StringBuffer();
                    if(line2 == null)
                        break;
                }


//                if(line1.compareToIgnoreCase(line2) < 0){
//                    bf.append(line1+"\n");
//                    line1 = br1.readLine();
//                    if(line1 == null)
//                        break;
//                }else{
//                    //write line2 first
//                    bf.append(line2+"\n");
//                    line2 = br2.readLine();
//                    if(line2 == null)
//                        break;
//                }
            }
            while((line1 = br1.readLine())!= null){
                bf.append(line1+"\n");
            }
            while((line2 = br2.readLine())!= null){
                bf.append(line2+"\n");
            }
            writeToFile(bf.toString());
//            f1.delete();
//            f2.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void mergeFile(File f1, File f2) {

        List<String>readF1 = readFile(f1);
        List<String>readF2 = readFile(f2);
        readF1.addAll(readF2);
        Collections.sort(readF1, new SortIgnoreCase());

        writeToFileList(readF1);


//        StringBuffer bf = new StringBuffer();
//        FileInputStream out1 ;
//        FileInputStream out2 ;
//        BufferedReader br1;
//        BufferedReader br2;
//        String line1;
//        String line2;
//        try {
//            out1 = new FileInputStream(f1);
//            out2 = new FileInputStream(f2);
//            br1 =new BufferedReader(new InputStreamReader(out1));
//            br2 = new BufferedReader(new InputStreamReader(out2));
//            line1 = br1.readLine();
//            line2 = br2.readLine();
//            while (true) {
//                if(line1.compareToIgnoreCase(line2) < 0){
//                    bf.append(line1+"\n");
//                    line1 = br1.readLine();
//                    if(line1 == null)
//                        break;
//                }else{
//                    //write line2 first
//                    bf.append(line2+"\n");
//                    line2 = br2.readLine();
//                    if(line2 == null)
//                        break;
//                }
//            }
//            while((line1 = br1.readLine())!= null){
//                bf.append(line1+"\n");
//            }
//            while((line2 = br2.readLine())!= null){
//                bf.append(line2+"\n");
//            }
//            writeToFile(bf.toString());
//            f1.delete();
//            f2.delete();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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
}




