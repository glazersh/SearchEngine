package Model;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


public class Model extends Observable {

    File selectedFolderBrowseCollection;
    ReadFile readFile;
    Parse parse;
    String PathPosting;

    DataCollector dataCollector;
    Map<String, String[]> dictionaryToLoad;
    Map<String, String> citiesToLoad;
    Map<String, String[]> docsFilesToLoad;
    Map<String, String[]> entitesToLoad;

    Set<String>notR = new HashSet<>();

    public List<String> getIDs(){
        return dataCollector.getIDs();
    }

    public Model() {
        this.dataCollector = new DataCollector();
        notR.add("document");
        notR.add("documents");
        notR.add("Documents");
        notR.add("discussing");
        notR.add("considered");
        notR.add("contain");
        notR.add("least");
        notR.add("factor");
        notR.add("discuss");
        notR.add("shows");
        notR.add("include");
        notR.add("relevant");
        notR.add("Relevant");
        notR.add("following");
        notR.add("issues");
        notR.add("information");
    }

    public void readCorpus(String FileCorpus, String stopWords, String PathPosting, Boolean withStemming) {
        this.PathPosting = PathPosting;
        long start = System.nanoTime();
        readFile = new ReadFile(FileCorpus, stopWords, PathPosting, withStemming, dataCollector);
        readFile.start();
        long finish = System.nanoTime();

        long total = finish - start;
        dataCollector.setRunningTime(total / 1000000000);
        if (withStemming)
            dataCollector.setPostPath(PathPosting + "\\Y\\");
        else
            dataCollector.setPostPath(PathPosting + "\\N\\");


    }

    public void IDsClear(){
        dataCollector.resetIDs();
    }
    public void setFiles(File selectedFolderBrowseCollection) {
        this.selectedFolderBrowseCollection = selectedFolderBrowseCollection;
    }

    public void loadAllDictionary(String path) {


        loadDocs(path + "FileDocs");
        loadDict(path + "Dictionary");
        loadCities(path + "CitiesPost");

        dataCollector.setAllDicToLoad(docsFilesToLoad, dictionaryToLoad, citiesToLoad, entitesToLoad);
        dataCollector.setPostPath(path);
    }


    /**
     * load cites
     *
     * @param path
     */
    private void loadCities(String path) {
        citiesToLoad = new HashMap<>();

        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            String line;


            while ((line = br.readLine()) != null) {
                String[] tmp = line.split(",\\{", 2);
                citiesToLoad.put(tmp[0], tmp[1]);
            }


        } catch (IOException e) {

        } finally {
            try {
                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * load docs
     *
     * @param path
     */
    private void loadDocs(String path) {
        docsFilesToLoad = new HashMap<>();
        entitesToLoad = new HashMap<>();

        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            String line;

            List<String> docCounter = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                String[] tmp = line.split(",", 2);
                if (tmp.length == 2) {
                    String[] docInfo = tmp[1].split(",",5);
                    if(docInfo.length==5) {
                        String[] entity = docInfo[4].split(":");
                        entitesToLoad.put(tmp[0],entity);
                    }else{
                        entitesToLoad.put(tmp[0],new String[]{""});
                    }

                    String[]untilCity = {docInfo[0],docInfo[1],docInfo[2],docInfo[3]};
                    docsFilesToLoad.put(tmp[0], untilCity);
                } else {
                    docCounter.add(tmp[0]);
                }
            }
            dataCollector.setNumberOfDocs(Integer.parseInt(docCounter.get(0)));
            dataCollector.setAverageNumOfDocs(Double.parseDouble(docCounter.get(1)));


        } catch (IOException e) {

        } finally {
            try {
                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Load Dictionary to memory
     *
     * @param path
     */
    private void loadDict(String path) {
        dictionaryToLoad = new HashMap<>();

        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null) {
                String[] tmp = line.split(",\\{");
                String[] docInfo = tmp[1].split(":");
                dictionaryToLoad.put(tmp[0], docInfo);
            }


        } catch (IOException e) {

        } finally {
            try {
                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
            }
        }
    }


    /**
     * Reset all files and data
     */
    public void resetAll() {
        readFile.resetAll();
    }

    //-----GETTERS-----

    public Set getLang() {
        return dataCollector.getLang();
    }

    public int getNumberOfDocs() {
        return dataCollector.getNumberOfDocs();
    }

    public int getNumberOfTerms() {
        return dataCollector.getNumberOfTerms();
    }

    public long getRunningTime() {
        return dataCollector.getRunningTime();
    }

    public Map getMap() {
        return dataCollector.getMap();
    }

    public File getFiles() {
        return selectedFolderBrowseCollection;
    }

    public void readQuery(String query, String stopWords, boolean withstemming) {
        if (parse == null)
            parse = new Parse(stopWords, PathPosting, withstemming, dataCollector);

        parse.parse(query, "", "", false);
        int ID = (int) (Math.random()*900+100);
        writeTheAnswer(ID+"",true);
        dataCollector.addID(ID+"");
    }

    public List<DocData> getDocsName() {
        return dataCollector.getDocs();
    }

    public void writeTheAnswer(String numQ, boolean first) {
        String path = dataCollector.getQueryToSavePath();
            File file = new File(path + "\\result.txt");
            //File file = new File("C:\\Users\\USER\\Desktop\\search2018\\post\\query\\result.txt");
            if(first){
                file.delete();
            }
        try {
            BufferedWriter bf = new BufferedWriter(new FileWriter(file,true));
            for (DocData s2 : this.getDocsName()) {
                bf.append(numQ + " 0 " + s2.getDocName() + " 1 " + "3.0 " + "test\n");
            }
            bf.flush();
            bf.close();
        } catch (IOException e) {

        }
    }

    /**
     * function which cuts the words of the query and sends to the parser
     * @param path
     * @param stopWords
     * @param withstemming
     * @param withSemantic
     * @param cities
     */

    public void fileOfQuery(String path, String stopWords, boolean withstemming, boolean withSemantic, String cities) {
        if (parse == null) {
            parse = new Parse(stopWords, PathPosting, withstemming, dataCollector);
        }

        boolean first = true;
        List<String[]> allQueries ;
        allQueries = splitQueries(path);
        for(String[]q : allQueries){
            if(withSemantic){
                String semanticWords = getSemantics(q[1]);
                Set<String> s1 = new HashSet<String>(Arrays.asList(semanticWords.split(" ")));
                for (String str : s1) {
                    q[1] += " " + str;
                }
            }
            q[1]+= check(q[2],q[1]);
            parse.parse(q[1]+" "+cities,"","",false);
            writeTheAnswer(q[0],first);
            first = false;
            dataCollector.addID(q[0]);
        }
        cmd(); // don't forget to remove !!!
        writeToCSV(); // don't forget to remove !!!
    }

    /***
     * adding the description to the query
     * @param str
     * @param query
     * @return
     */
    private String check(String str,String query) {
        Set queryF = new HashSet();
        StringBuffer bf = new StringBuffer(" ");
        String[]words = str.split(" ");
        for(String word:words){
            bf.append(" " + word);
        }

        return bf.toString();


    }

    /**
     * cuts the details from the given queries files
     * @param path
     * @return
     */
    private List<String[]> splitQueries(String path) {
        List<String[]> queries = new ArrayList<>();

        BufferedReader br = null;
        FileReader fr = null;

        try {
            //gets the file
            fr = new FileReader(path+"\\queries.txt");
            br = new BufferedReader(fr);
            String line;
            String[] numQuery = new String[4];
            while ((line = br.readLine()) != null) {
                if (line.startsWith("<num>")) {
                    numQuery[0] = line.substring(14);
                    if(numQuery[0].endsWith(" ")){
                        numQuery[0]=numQuery[0].substring(0,numQuery[0].length()-1);
                    }
                    continue;
                }
                //gets the title
                if (line.startsWith("<title>")) {
                    numQuery[1] = line.substring(8);
                    continue;
                }
                //gets the description
                if(line.startsWith("<desc>")){
                    String dec="";
                    while((line = br.readLine()) != null){
                        if(!line.startsWith("<narr>"))
                            dec += line+" ";
                        else
                            break;
                    }
                    numQuery[2] = dec;
                }
                //gets the narrative
                if(line.startsWith("<narr>")){
                    String narr="";
                    while((line = br.readLine()) != null && !line.equals("</top>") ){
                        narr += line+" ";
                    }
                    numQuery[3] = narr;
                    queries.add(numQuery);
                    numQuery = new String[4];
                }

            }

        } catch (IOException e) {

        } finally {
            try {
                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
            }
        }
        return queries;
    }

    /**
     *
     * @return list of cities from the cities dictionary
     */
    public List<String> getAllCities() {
        List<String>cities = new ArrayList<>();
        cities.addAll(citiesToLoad.keySet());
        return cities;
    }

    /**
     * function which gets the semantic from a given api
     * @param query
     * @return
     */
    public String getSemantics(String query){
        StringBuffer bf = new StringBuffer();
        //String[] wordsInQuery = query.split(" ");
        //for(String words:wordsInQuery) {

            String queryPlus = query.replace(" ", "+");

            final String urlString = "https://api.datamuse.com/words?ml=" + queryPlus;
            BufferedReader br = null;
            InputStreamReader isr = null;
            try {
                URL url = new URL(urlString);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                isr = new InputStreamReader(con.getInputStream());
                br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    int counter = 0;
                    String[] perWord = line.substring(2).split("\\{");
                    for (String word : perWord) {
                        String[] tmp = word.split(",")[0].split(":");
                        bf.append(tmp[1].substring(1).replace('"', ' '));
                        counter++;
                        if (counter == 7)
                            break;

                    }
                }
                br.close();
                isr.close();
                return query + " " + bf.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        //}
        return query;
    }

    // don't forget to remove !!!
    private void cmd() {
        String[] command = { "cmd" };
        String path = "D:\\documents\\users\\dorlev\\Downloads\\query"; // write your path here !
        //String path = "C:\\Users\\USER\\Desktop\\search2018\\post\\query"; // write your path here !
        Process p;
        try{
            p= Runtime.getRuntime().exec(command);
            new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
            new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
            PrintWriter stdin = new PrintWriter(p.getOutputStream());
            stdin.println("D:");
            stdin.println("cd "+path);
            stdin.println("treceval -q qrels.txt result.txt > output.txt");

            stdin.close();
            p.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void writeToCSV() {
        String readFromFile="D:\\documents\\users\\dorlev\\Downloads\\query\\output.txt"; // path to output.txt
        //String readFromFile="C:\\Users\\USER\\Desktop\\search2018\\post\\query\\output.txt"; // path to output.txt
        String writeToFile = "D:\\documents\\users\\dorlev\\Downloads\\query\\Ans.csv";
        //String writeToFile = "C:\\Users\\USER\\Desktop\\search2018\\post\\query\\Ans.csv";
        File fileW = new File(writeToFile);
        if(fileW.exists())
            fileW.delete();
        File file = new File(readFromFile);
        if(file.exists()){
            // first read
            BufferedReader br = null;
            FileReader fr = null;

            BufferedWriter bw = null;
            FileWriter fw = null;
            StringBuilder sb = new StringBuilder();

            try {
                fr = new FileReader(readFromFile);
                br = new BufferedReader(fr);
                fw= new FileWriter(writeToFile);
                bw = new BufferedWriter(fw);
                bw.write("ID,Retrieved,Relevant,Rel_ret\n");
                String line;
                while ((line = br.readLine()) != null) {
                    if(line.startsWith("Queryid")){
                        String ID=line.substring(20);
                        sb.append(ID);
                        sb.append(",");
                        continue;
                    }
                    if(line.contains("Retrieved:")){
                        String Retrieved=line.substring(21);
                        if(Retrieved.startsWith(" ")){
                            Retrieved = Retrieved.substring(1);
                        }
                        sb.append(Retrieved);
                        sb.append(",");
                        continue;
                    }
                    if(line.contains("Relevant:")){
                        String Relevant=line.substring(20);
                        if(Relevant.startsWith(" ")){
                            Relevant = Relevant.substring(1);
                        }
                        sb.append(Relevant);
                        sb.append(",");
                        continue;
                    }
                    if(line.contains("Rel_ret:")){
                        String Rel_ret=line.substring(20);
                        if(Rel_ret.startsWith(" ")){
                            Rel_ret = Rel_ret.substring(1);
                        }
                        sb.append(Rel_ret);
                        sb.append("\n");
                        bw.write(sb.toString());
                        sb=new StringBuilder();

                    }
                }



            } catch (IOException e) {

            } finally {
                try {
                    if (br != null)
                        br.close();

                    if (fr != null)
                        fr.close();

                    if (bw != null)
                        bw.close();

                    if (fw != null)
                        fw.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    public List<DocData> getANS(int value) {
        List<DocData>tmp = dataCollector.getCurrent(value);
        return tmp;
    }

    public void setQueryPathToSave(String queryPathToSave) {
        this.dataCollector.setQueryToSavePath(queryPathToSave);
    }

    class SyncPipe implements Runnable
    {
        public SyncPipe(InputStream istrm, OutputStream ostrm) {
            istrm_ = istrm;
            ostrm_ = ostrm;
        }
        public void run() {
            try
            {
                final byte[] buffer = new byte[1024];
                for (int length = 0; (length = istrm_.read(buffer)) != -1; )
                {
                    ostrm_.write(buffer, 0, length);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        private final OutputStream ostrm_;
        private final InputStream istrm_;
    }

}
