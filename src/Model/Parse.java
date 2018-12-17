package Model;

import Model.IO.DBCountries;
import Model.IO.Countries;
import Model.Term.*;
import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Parse {

    Indexer post ;

    Stemmer stem = new Stemmer();


    Map<String,String> month= new HashMap<>();
    HashSet<String> afterNumber = new HashSet<>();
    Set<String> stopWords = new HashSet<>();
    Set<String>signs = new HashSet<>();

    Map<ATerm,Map<String,Integer>> allWordsDic = new HashMap<>(); // clear
    Map<String,Integer> termMap;

    List<String> docInfo = new ArrayList<>(); // clear
    Map <String, String> CitiesMap = new HashMap<>();
    Set <String> citiesSet;


    ATerm term;

    StringBuffer termBeforeChanged;

    Map<ATerm,Integer>wordsInDoc = new HashMap<>();

    DBCountries countryInMemory;

    Map<String,String> capitalTerms = new HashMap<>();



    int maxTermCounter;
    int counterMinTerm;

    boolean isTNumber = false;
    boolean isInteger = true;
    boolean isTermPrice = false;
    boolean isTermNumber = false;
    boolean isTermPercent = false;
    boolean isTermDate = false;
    boolean found = false;

    int termInDoc = 0;

    boolean withStem;
    boolean inIndex ;

    DataCollector dt ;
///////////////////////////////
    private int pos;
    private PriorityQueue<ATerm>entityTerms;
    List <String>entityDoc;
    Map <String,Pair<ATerm,Integer>> setPerDoc;


    public Parse(String stopWords, String PathPosting, boolean  withStemming, DataCollector dataCollector){
        this.withStem = withStemming;
        this.dt = dataCollector;
        post = new Indexer(PathPosting, withStemming, dataCollector);
        citiesSet = post.getCitiesMap();
        insertMonth(); // init all months
        insertAfterWords(); // init special words for our parse
        StopWords(stopWords); // init all stopWords from stop_words.txt
        insertSigns(); // init all the signs
        entityTerms  = new PriorityQueue<>(new Comparator<ATerm>() {
            @Override
            public int compare(ATerm o1, ATerm o2) {
                if(o1.getTf()<o2.getTf())
                    return 1;
                if(o1.getTf()>o2.getTf())
                    return -1;
                else{
                    if(o1.getPosition() < o2.getPosition())
                        return -1;
                    if(o1.getPosition() > o2.getPosition())
                        return 1;
                }
                return 0;
            }
        });
        entityDoc = new ArrayList<>();
        setPerDoc = new HashMap<>();
        try {
            countryInMemory = new DBCountries("https://restcountries.eu/rest/v2/all?fields=name;capital;population;currencies");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //converts the stop words txt to a dictionary
    private void StopWords(String path){
        Scanner file = null;
        try {
            //don't forget to change the path !!!!
            file = new Scanner(new File(path));
            // For each word in the input
            while (file.hasNext()) {
                // Convert the word to lower case, trim it and insert into the set
                // In this step, you will probably want to remove punctuation marks
                stopWords.add(file.next());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void insertMonth() {
        month.put("January", "01");
        month.put("JANUARY", "01");
        month.put("Jan", "01");
        month.put("February", "02");
        month.put("FEBRUARY", "02");
        month.put("Feb", "02");
        month.put("March", "03");
        month.put("MARCH", "03");
        month.put("Mar", "03");
        month.put("April", "04");
        month.put("APRIL", "04");
        month.put("Apr", "04");
        month.put("May", "05");
        month.put("MAY", "05");
        month.put("June", "06");
        month.put("JUNE", "06");
        month.put("Jun", "06");
        month.put("July", "07");
        month.put("JULY", "07");
        month.put("Jul", "07");
        month.put("August", "08");
        month.put("AUGUST", "08");
        month.put("Aug", "08");
        month.put("September", "09");
        month.put("SEPTEMBER", "09");
        month.put("Sep", "09");
        month.put("October", "10");
        month.put("OCTOBER", "10");
        month.put("Oct", "10");
        month.put("November", "11");
        month.put("NOVEMBER", "11");
        month.put("Nov", "11");
        month.put("December", "12");
        month.put("DECEMBER", "12");
        month.put("Dec", "12");
    }
    private void insertAfterWords(){
        afterNumber.add("Thousand");
        afterNumber.add("Million");
        afterNumber.add("Billion");
        afterNumber.add("Trillion");
        afterNumber.add("percent");
        afterNumber.add("percentage");
        afterNumber.add("Dollars");
        afterNumber.add("million");
        afterNumber.add("billion");
        afterNumber.add("trillion");
        afterNumber.add("U.S.");
        afterNumber.add("dollars");
        afterNumber.add("m");
        afterNumber.add("bn");
    }
    private void insertSigns(){
        signs.add(".");
        signs.add(",");
        signs.add(";");
        signs.add("(");
        signs.add("{");
        signs.add("[");
        signs.add(")");
        signs.add("}");
        signs.add("]");
        signs.add(":");
        signs.add("!");
        signs.add("?");
        signs.add("`");
        signs.add("|");
        signs.add("+");
        signs.add("'");
        signs.add("*");
        signs.add(" ");
        signs.add("#");
        signs.add("=");//
        signs.add("/");
        signs.add("@");
        signs.add("--");
        signs.add(""+'"');

    }


    /**
     * Check if the string is number
     * @param str
     * @return true / false
     */
    private boolean isNumber(String str) {
        if (str == null || str.equals("")) {
            return false;
        }
        int length = str.length();
        int i = 0;

        // set the length and value for highest positive int or lowest negative int
        int maxlength = 12;
        String maxnum = String.valueOf(Integer.MAX_VALUE);
        if (str.length()>1 && str.charAt(0) == '-' ) {
            maxlength = 13;
            i = 1;
            maxnum = String.valueOf(Integer.MIN_VALUE);
        }

        // verify digit length does not exceed int range
        if (length > maxlength) {
            return false;
        }

        // verify that all characters are numbers
        if (maxlength == 11 && length == 1) {
            return false;
        }
        int counter = 0;
        for (int num = i; num < length; num++) {
            char c = str.charAt(num);
            if (c < '0' || c > '9') {
                if(counter==0 && ((c=='.' || c=='/') && length!=1))
                    counter++;
                else
                    return false;

            }
        }

        // verify that number value is within int range
        if (length == maxlength) {
            for (; i < length; i++) {
                if (str.charAt(i) < maxnum.charAt(i)) {
                    return true;
                }
                else if (str.charAt(i) > maxnum.charAt(i)) {
                    return false;
                }
            }
        }
        isTNumber = true;
        return true;
    }

    /**
     * Remove the comma from the string
     * @param str
     * @return string without comma
     */
    private String removeComma(String str){
        StringBuffer newStr = new StringBuffer();
        for(int i=0;i<str.length();i++){
            if(str.charAt(i) != ',')
                newStr.append(str.charAt(i));
        }
        return newStr.toString();
    }

    /**
     * Check if the string need Special case
     * @param word
     * @param second
     * @return true / false
     */
    private boolean isNormalWord(String word, String second){
        if(isNumber(removeComma(word)))
            return false;
        if(word.charAt(0) == '$')
            return false;
        if(word.charAt(word.length()-1) == '%')
            return false;
        if(month.containsKey(word) && isNumber(second))
            return false;
        if(word.equalsIgnoreCase("Between"))
            return false;
        if(word.contains("-"))
            return false;
        if(word.contains("/"))
            return false;

//        if(word.contains("-"))
//            return false;
        return true;
    }

    /**
     * converts price (above 2 words) above million dollars and under million dollars
     * @param word
     * @param allTerm
     * @param isInteger
     */
    private void termPrice(String word, String [] allTerm, boolean isInteger){
        boolean aboveM = true;
        termBeforeChanged = new StringBuffer(word);
        switch (allTerm[1]) {
            case "m":
            case "million":
                termBeforeChanged.append(" M Dollars");
                break;
            case "bn":
            case "billion":
                if (isInteger) {
                    termBeforeChanged.append("000 M Dollars");
                }
                else {
                    int numD = (int)(Double.parseDouble(word)*1000);
                    termBeforeChanged.replace(0,allTerm[0].length(),Integer.toString(numD));
                    termBeforeChanged.append(" M Dollars");
                }
                break;
            case "trillion":
                if (isInteger) {
                    termBeforeChanged.append("000000 M Dollars");
                } else {
                    int numD = (int)(Double.parseDouble(word)*1000000);
                    termBeforeChanged.replace(0,allTerm[0].length(),Integer.toString(numD));
                    termBeforeChanged.append(" M Dollars");
                }
                break;
            case "Dollars" :
                if(word.contains("/") || Double.parseDouble(word)/1000000<1) {
                    aboveM = false;
                    termBeforeChanged.append(" Dollars");
                }
                else{
                    double numD = (Double.parseDouble(word)/1000000);

                    termBeforeChanged.replace(0,allTerm[0].length(),Double.toString(numD));
                    termBeforeChanged.append(" M Dollars");
                }
                break;

            default :
                aboveM = false;
                termBeforeChanged.append(" "+allTerm[1]+" Dollars");
                break;
        }

        if(aboveM)
            term = new PriceM(termBeforeChanged.toString());
        else
            term = new Price(termBeforeChanged.toString());
        increaseCounter(term);

    }

    /**
     * converts price with 1 word that above million dollars and under million dollars
     * @param word
     * @param oneTerm
     */

    private void oneTermPrice(String word, String oneTerm){
        boolean aboveM = true;
        termBeforeChanged = new StringBuffer(oneTerm);
        if(Double.parseDouble(word)/1000000<=1) {
            aboveM = false;
            termBeforeChanged.append(" Dollars");
        }
        else{
            double numD = (Double.parseDouble(word)/1000000);
            //numD = Math.round(numD*100.0)/100.0;
            termBeforeChanged.replace(0,word.length(),Double.toString(numD));
            termBeforeChanged.append(" M Dollars");
        }
        if(aboveM)
            term = new PriceM(termBeforeChanged.toString());
        else
            term = new Price(termBeforeChanged.toString());
        increaseCounter(term);
    }

    /**
     * converts numbers (above 2 words) above thousand to K, above million to m, above billion to B
     * @param realword
     * @param termWords
     */
    private void termNumber(String realword, String[] termWords) {

        if(realword.contains("/"))
            term = new NumberK(realword);
        else {
            termBeforeChanged = new StringBuffer(realword);
            switch (termWords[1]) {
                case "thousand":
                case "Thousand": {
                    termBeforeChanged.append("K");
                    term = new NumberK(termBeforeChanged.toString());
                    break;
                }
                case "million":
                case "Million": {
                    termBeforeChanged.append("M");
                    term = new NumberM(termBeforeChanged.toString());
                    break;
                }
                case "billion":
                case "Billion": {
                    termBeforeChanged.append("B");
                    term = new NumberB(termBeforeChanged.toString());
                    break;
                }
                case "trillion":
                case "Trillion": {
                    termBeforeChanged.append("000B");
                    term = new NumberB(termBeforeChanged.toString());
                    break;
                }
                default:
                    termBeforeChanged.append(" " + termWords[1]);
                    term = new NumberU(termBeforeChanged.toString());
                    break;
            }
        }
        increaseCounter(term);
    }


    /**
     * converts numbers (with one word) above thousand to K, above million to m, above billion to B
     * @param word
     */
    private void oneTermNumber(String word) {
        if(word.contains("/"))
            term = new NumberK(word);
        else {
            double numberWord = Double.parseDouble(word);
            // under 1K
            if (numberWord < 1000) {
                term = new NumberU(word);
            } else {
                // 1k - 1M
                if (numberWord < 1000000) {
                    numberWord = numberWord / 1000;
                    //numberWord = Math.round(numberWord*100.0)/100.0;
                    termBeforeChanged = new StringBuffer(cutDot0(numberWord) + "K");
                    term = new NumberK(termBeforeChanged.toString());

                } else {
                    // 1M - 1B
                    if (numberWord < 1000000000) {
                        numberWord = numberWord / 1000000;
                        //numberWord = Math.round(numberWord*100.0)/100.0;
                        termBeforeChanged = new StringBuffer(cutDot0(numberWord) + "M");
                        term = new NumberM(termBeforeChanged.toString());
                    }
                    // over 1B
                    else {
                        numberWord = numberWord / 1000000000;
                        //numberWord = Math.round(numberWord*100.0)/100.0;
                        termBeforeChanged = new StringBuffer(cutDot0(numberWord) + "B");
                        term = new NumberB(termBeforeChanged.toString());
                    }
                }
            }
        }
        increaseCounter(term);

    }

    /**
     * converts dates to MM-DD or YYYY-MM format as needed
     * @param wordsTerm
     */
    private void termDate(String [] wordsTerm) {
        if(month.containsKey(wordsTerm[0])){ // first is month
            if(wordsTerm[1].length() > 2) { // year
                termBeforeChanged = new StringBuffer(wordsTerm[1]+"-"+month.get(wordsTerm[0]));
                term = new DateYear(termBeforeChanged.toString());
            }
            else{ // day
                if(wordsTerm[1].length()!= 1)
                    termBeforeChanged = new StringBuffer(month.get(wordsTerm[0])+"-"+wordsTerm[1]);
                else
                    termBeforeChanged = new StringBuffer(month.get(wordsTerm[0])+"-0"+wordsTerm[1]);
                term = new DateDay(termBeforeChanged.toString());
            }
        }
        else{ // day
            if(wordsTerm[1].length() == 1) {
                termBeforeChanged = new StringBuffer(month.get(wordsTerm[1])+"-0"+wordsTerm[0]);
            }else{
                termBeforeChanged = new StringBuffer(month.get(wordsTerm[1])+"-"+wordsTerm[0]);
            }
            term = new DateDay(termBeforeChanged.toString());
        }
        increaseCounter(term);
    }



    private boolean isTermNumber(String word){
        if (isNumber(word)) {
            if (word.contains(".")) {
                isInteger = false;
            }
            return true;
        }
        return false;
    }

    private void init(){
        isTNumber = false;
        isInteger = true;
        isTermPrice = false;
        isTermNumber = false;
        isTermPercent = false;
        isTermDate = false;
        found = false;
    }

    //function which specifies terms with more than 1 word to types
    private void typeTerm(String [] termWords, String word){
        // if the term is date
        if (isTermDate) {
            termDate(termWords);
            return;
        }
        // if the term is price
        if (isTermPrice) {
            termPrice(word, termWords, isInteger);
            return;
        }
        // if the term is percent
        if (isTermPercent) {
            term = new Percent(termBeforeChanged.toString());
            increaseCounter(term);
            return;
        }
        // the term is number
        else
            termNumber(word, termWords);
    }

    //function which specifies terms with 1 word to types
    private void oneWordTypeTerm(String word, String real) {

        // if the term is price
        if (isTermPrice) {
            oneTermPrice(word, real);
            return;
        }

        // if the term is percent
        if (isTermPercent) {
            term = new Percent(real);
            increaseCounter(term);
            return;
        }
        //  the term is number
        if(isTNumber) {
            oneTermNumber(word);
            return;
        }
        // is normal string
        else // trash here
            term = new Word(real);
    }

    /**
     * our rule - dates with full format of MM-DD-YYYY and converts into MM-DD and YYYY-MM
     * @param word
     */
    private void termFullDate(String word) {
        String[]fullDate = word.split("/");
        String day = fullDate[0];
        String month = fullDate[1];
        String year = fullDate[2];
        if(year.length()==2){
            year = "19"+year;
        }
        try{
            if(Integer.parseInt(month) > 12 && Integer.parseInt(month) <= 31 && Integer.parseInt(day) < 13){
                String tmp = month;
                month =day;
                day= tmp;
            }

            if(Integer.parseInt(month) < 13 && Integer.parseInt(day) <= 31){
                term = new DateDay(month+"-"+day);
                increaseCounter(term);
                term = new DateYear(year+"-"+month);
                increaseCounter(term);
            }
            else{
                term = new Word(word);
                increaseCounter(term);
            }

        }catch (Exception e){
            term = new Word(word);
            increaseCounter(term);
        }
    }


    private boolean isFullDate(String word) {
        if(word.length()<8)
            return false;
        int counter =0;
        for(int i=0;i<word.length();i++){
            if(counter<6 &&counter%3==2){
                if(word.charAt(i) != '/'){
                    return false;
                }
                counter++;
            }else{
                if(!Character.isDigit(word.charAt(i))){
                    return false;
                }
                counter++;
            }
        }
        return true;
    }

    /**
     * returns whether the string is range between and converts into word-word format
     * @param num1String
     * @param andString
     * @param num2String
     * @return
     */
    private boolean betweenTerm(String num1String, String andString, String num2String){
        String num1 = cutSigns(num1String);
        String and = cutSigns(andString);
        String num2 = cutSigns(num2String);
        if(and.equals("and") && isNumber(removeComma(num1)) && isNumber(removeComma(num2))) {
            num1 = TermNumber(removeComma(num1));
            num2 = TermNumber(removeComma(num2));
            term = new Range(num1+"-"+num2);
            increaseCounter(term);
            return true;
        }
        return false;
    }

    /**
     *
     * @param number
     * @param kind of number
     * @return number after converting the term range between numbers into the right format
     */
    public String transferNumberInRange(String number, String kind){
        boolean isTrillion = false;
        double firstNumber = Double.parseDouble(number);
        if(kind.equalsIgnoreCase("Thousand")){
            firstNumber*=1000;
        }
        if(kind.equalsIgnoreCase("Million")){
            firstNumber*=1000000;
        }
        if(kind.equalsIgnoreCase("Billion")){
            firstNumber*=1000000000;
        }
        if(kind.equalsIgnoreCase("Trillion")){
            firstNumber*=1000000000;
            isTrillion = true;
        }
        String finalRange = TermNumber(firstNumber+"");
        if(isTrillion){
            finalRange = finalRange.replace("B","000B");
        }
        return finalRange;
    }

    /**
     *
     * @param word
     * @param word2
     * @param minusWord
     * @return
     */
    private int RangeTerm(String word, String word2, String minusWord){
        int addIndex = 0;

        String afterWord = cutSigns(word2);
        String beforeWord = cutSigns(minusWord);
        String beforeHypen = word.split("-")[0];
        String afterHypen = word.split("-")[1];

        // 1-2 Month -> 1 month, 2 month
        if (month.containsKey(afterWord) && isNumber(beforeHypen) && isNumber(afterHypen) ) {
            String[] first = {beforeHypen, afterWord};
            String[] second = {afterHypen, afterWord};
            termDate(first);
            termDate(second);
            term = new NumberU(beforeHypen);
            increaseCounter(term);
            term = new NumberU(afterHypen);
            increaseCounter(term);
            return 1+addIndex;
        }

        // equalIgnore
        if(isNumber(afterHypen) && (afterWord.equalsIgnoreCase("Thousand") || afterWord.equalsIgnoreCase("Million") || afterWord.equalsIgnoreCase("Billion") || afterWord.equalsIgnoreCase("Trillion"))){
            String numberRange = transferNumberInRange(afterHypen, afterWord);
            term=new Range(beforeHypen+"-"+numberRange);
            increaseCounter(term);
            return 1+addIndex;

        }
        if (isNumber(beforeHypen) && isNumber(afterHypen) && month.containsKey(beforeWord)) {
            String[] first = {beforeHypen, beforeWord};
            String[] second = {afterHypen, beforeWord};
            termDate(first);
            termDate(second);
            return addIndex;
        }
        if((beforeHypen.equalsIgnoreCase("Thousand") || beforeHypen.equalsIgnoreCase("Million") || beforeHypen.equalsIgnoreCase("Billion") || beforeHypen.equalsIgnoreCase("Trillion"))) {
            if(isNumber(beforeWord) && isNumber(afterHypen)){
                String[]numbers = {beforeWord,afterHypen};
                String[]kinds = {beforeHypen,afterWord};
                String[]finalRange = new String[2];
                for(int a=0;a<2;a++){
                    finalRange[a] = transferNumberInRange(numbers[a],kinds[a]);
                }
                term=new Range(finalRange[0]+"-"+finalRange[1]);
                increaseCounter(term);
                return 1+addIndex;
            }
            if(isNumber(removeComma(beforeWord))){
                oneTermNumber(removeComma(beforeWord));
                String finalRange = transferNumberInRange(removeComma(beforeWord),beforeHypen);
                term=new Range(finalRange+"-"+afterHypen);
                increaseCounter(term);
                return addIndex;
            }
            if(isNumber(removeComma(afterHypen))){
                oneTermNumber(removeComma(afterHypen));
                String finalRange = transferNumberInRange(removeComma(afterHypen),afterWord);
                term=new Range(finalRange+"-"+afterHypen);
                increaseCounter(term);
                return addIndex;
            }
            else{
                term = new Range(word);
                increaseCounter(term);
                return addIndex;
            }
        }
        if(word.startsWith("-") && !isNumber(removeComma(word.substring(1)))){
            if(!stopWords.contains(word.substring(1))){// trash here !
                stem.add(word.toCharArray(), word.length());
                stem.stem();
                term=new Word(word.substring(1));
                increaseCounter(term);
            }
            return addIndex;
        }
        /// here
        if(isNumber(removeComma(beforeHypen)) && isNumber(removeComma(afterHypen))) {
            beforeHypen = removeComma(beforeHypen);
            afterHypen = removeComma(afterHypen);
            oneTermNumber(beforeHypen);
            oneTermNumber(afterHypen);
            beforeHypen = TermNumber(beforeHypen);
            afterHypen = TermNumber(afterHypen);
            word = beforeHypen + "-" + afterHypen;
        }
        term = new Range(word);
        increaseCounter(term);
        return addIndex;
    }


    /**
     * The Main parser function
     * @param text
     * @param docName
     */

    public void parse(String text, String docName,String cityName, boolean inIndex) {
        maxTermCounter = 0;
        counterMinTerm = 0;
        wordsInDoc = new HashMap<>();
        this.inIndex = inIndex;
        termInDoc=0;
        String replace = text.replaceAll("[()?!@#|&+*\\[\\];{}\"]+"," ");
        String replace2 = replace.replace("--"," ");
        String[] allText = replace2.split(" "); // split the text by " "(space) into array

        /**
         * Check every words :
         *           1. cut all the signs in the first and the last character
         *           2. check if stopWords List contain the word
         *               2.1 if true, continue
         *               2.2 else, send to stemmer
         */
        for (pos = 0; pos < allText.length; pos++) {
            // init all boolean variable
            init();
            // cut the signs
            String word = cutSigns(allText[pos]);

            //if the word contains one char - ignore
            if ((word.length() == 1 && !isNumber(word)))
                continue;
            if (!word.equals("") && ((word.equals("Between") || (word.equals("May")  || !stopWords.contains(word.toLowerCase()))))) {
                // regular text
                if (pos + 1 < allText.length && isNormalWord(word, cutSigns(allText[pos + 1]))) {

                    // stemmer
                    if(withStem) {
                        stem.add(word.toCharArray(), word.length());
                        stem.stem();
                        if (stem.toString().endsWith("'") || stem.toString().endsWith("`")) {
                            word = cutSigns(stem.toString());
                        } else {
                            word = stem.toString();
                        }
                    }

                    term = new Word(word);
                    checkCapital(term.finalName,docName,pos);
                    increaseCounter(term);

                } else {
                    if (pos == allText.length - 1) {
                        if (isNormalWord(word, "no")) {
                            if(withStem) {
                                stem.add(word.toCharArray(), word.length());
                                stem.stem();
                            }
                            term = new Word(word);
                            checkCapital(term.finalName,docName,pos);
                            increaseCounter(term);
                            continue;
                        }
                    }

                    if ((pos + 3 < allText.length && word.equals("Between"))) {
                        if(betweenTerm(allText[pos+1],allText[pos+2],allText[pos+3]))
                            pos=pos+3;
                        continue;
                    }

                    if(((word.contains("-") && !word.endsWith("-") && pos - 1 >= 0 && pos+1 <allText.length))) {
                        int index = RangeTerm(word,allText[pos+1],allText[pos-1]);
                        pos += index;
                        continue;
                    }

                    if (!isTermNumber(word)) {
                        word = removeComma(word);
                        isTermNumber(word);
                    }

                    // if word's first character is with "$"
                    // and then check if word is Number.
                    if (!isTNumber && word.charAt(0) == '$') {
                        allText[pos] = allText[pos].substring(1); // cut $
                        word = word.substring(1);
                        if (isTermNumber(word)) {
                            isTermPrice = true;
                        }

                        // the number is fraction
                        if (isTNumber && word.contains("/")) {
                            term = new Price(allText[pos] + "Dollars");
                            increaseCounter(term);
                            continue;
                        }

                        // string that first character is "$"
                        if (!isTNumber) {
                            boolean flag = false;
                            for(int charW = 0; charW<word.length()-1;charW++){
                                if(signs.contains(word.charAt(charW)+"")&& !Character.isDigit(word.charAt(charW+1))){
                                    flag = true;
                                    break;
                                }
                            }

                            if(flag)
                                continue;

                            term = new Word('$' + word);
                            increaseCounter(term);
                            continue;
                        }
                    }

                    // if word's last character is "%"
                    // and then check if is number
                    if (!isTNumber && word.charAt(word.length() - 1) == '%') {
                        word = word.substring(0, word.length() - 1);
                        if (isTermNumber(word)) {
                            isTermPercent = true;
                        }
                        // if number is fraction
                        if (isTNumber && word.contains("/")) {
                            term = new Percent(word);
                            increaseCounter(term);
                            continue;
                        }
                        if (!isTNumber) {

                            boolean flag = false;
                            for(int charW = 0; charW<word.length()-1;charW++){
                                if(signs.contains(word.charAt(charW)+"")&& !Character.isDigit(word.charAt(charW+1))){
                                    flag = true;
                                    break;
                                }
                            }
                            if(flag)
                                continue;

                            term = new Word(word);
                            increaseCounter(term);
                            continue;
                        }
                    }
                    if(isFullDate(word)){
                        termFullDate(word);
                        continue;
                    }

                    int next = 0;
                    String nextWord = "";

                    if (pos + 1 < allText.length) {
                        nextWord = cutSigns(allText[pos + 1]);
                    }

                    while (pos + 1 < allText.length && (afterNumber.contains(nextWord)) || (month.containsKey(word) && isNumber(nextWord)) || (isTNumber && month.containsKey(nextWord))) {
                        if (next == 2) {
                            if (!nextWord.equals("U.S.") && !nextWord.contains("/"))
                                break;
                        }
                        next++;
                        pos = pos + 1;
                        if (pos + 1 < allText.length) {
                            nextWord = cutSigns(allText[pos + 1]);
                        } else
                            break;
                    }
                    pos = pos - next;
                    if (next > 0 && pos + next < allText.length) {
                        String[] termWords = new String[next + 1];
                        for (int j = 0; j < next + 1; j++) {
                            String wordTmp = cutSigns(allText[pos + j]);
                            if ((wordTmp.equalsIgnoreCase("Dollars"))) {
                                isTermPrice = true;
                                found = true;
                            }
                            if ((wordTmp.equalsIgnoreCase("Thousand") || wordTmp.equalsIgnoreCase("Million") || wordTmp.equalsIgnoreCase("Billion") || wordTmp.equalsIgnoreCase("Trillion"))) {
                                isTermNumber = true;
                                found = true;
                            }
                            if (month.containsKey(wordTmp)) {
                                isTermDate = true;
                                found = true;
                            }
                            if (wordTmp.equals("percent") || wordTmp.equals("percentage")) {
                                isTermPercent = true;
                                found = true;
                                termBeforeChanged = new StringBuffer(word + "%");
                            }

                            termWords[j] = wordTmp;
                        }
                        typeTerm(termWords, word);
                    }
                    // the term is one word
                    else {
                        String another = cutSigns(allText[pos]);
                        oneWordTypeTerm(word, another);
                    }
                    pos = pos + next;
                }
            }
        }
        // setPerDoc
        // after move all the terms
        for(ATerm term:wordsInDoc.keySet()){
            if(term instanceof Word ) {
                if(term.finalName.equalsIgnoreCase("PARTY"))
                {
                    int x= 4;
                }
                char c = term.finalName.charAt(0);
                int counterWord;
                if (Character.isUpperCase(c)) {
                    String tp = term.finalName.toLowerCase();
                    ATerm a = new Word(tp);
                    if (allWordsDic.containsKey(a)) {
                        if (allWordsDic.get(a).containsKey(docName)) {
                            //check hoe many times appeared in this doc
                            counterWord = wordsInDoc.get(term) + allWordsDic.get(a).get(docName);
                        } else {
                            counterWord = wordsInDoc.get(term);
                        }
                        // max
                        if(maxTermCounter<counterWord){
                            maxTermCounter = counterWord;
                        }

                        allWordsDic.get(a).put(docName, counterWord);
                    } else {
                        checkIfExistsUpper(docName, term);

                    }
                } else {
                    String tp = term.finalName.toUpperCase();
                    ATerm a = new Word(tp);
                    if (allWordsDic.containsKey(a)) {
                        Map<String, Integer> p;
                        p = allWordsDic.get(a);
                        //maybe no need to remove just put
                        allWordsDic.remove(a);
                        allWordsDic.put(term, p);
                        CheckCities(term, docName);
                        //may not be needed if checking earlier
                        if (allWordsDic.get(term).containsKey(docName))
                            counterWord = wordsInDoc.get(term) + allWordsDic.get(term).get(docName);
                        else
                            counterWord = wordsInDoc.get(term);
                        // max
                        if(maxTermCounter<counterWord){
                            maxTermCounter = counterWord;
                        }

                        allWordsDic.get(term).put(docName, counterWord);

                    } else {
                        checkIfExistsLower(docName, term);
                    }
                    if(setPerDoc.containsKey(term.finalName.toUpperCase())){
                        Pair<ATerm,Integer>remove = new Pair<>(term,1);
                        setPerDoc.put(term.finalName.toUpperCase(),remove);
                    }
                }
            }
            else{
                termMap = new HashMap<>();
                checkMinMaxCounter(wordsInDoc.get(term));
                //if do not exist
                termMap.put(docName, wordsInDoc.get(term));
                allWordsDic.put(term, termMap);
                CheckCities(term, docName);
            }
        }
        //// Finish

        docInfo.add(docName+","+maxTermCounter+","+wordsInDoc.size()+","+termInDoc+","+cityName);
        StringBuffer bf = new StringBuffer();
        while(!entityTerms.isEmpty()){
            ATerm tmp = entityTerms.poll();
            if(setPerDoc.get(tmp.finalName).getValue()==0)
                bf.append(":"+tmp.finalName);
        }
        entityDoc.add(docName+",{"+bf.toString());
    }

    private void CheckCities(ATerm term, String docName) {
        if(citiesSet.contains(term.finalName)){
            if(CitiesMap.containsKey(term.finalName)){
                String tmp = CitiesMap.get(term.finalName);
                CitiesMap.put(term.finalName, tmp + "," + docName);
            }
        }
    }

    private void checkMinMaxCounter( int numTermInDoc ){
        //max
        if(maxTermCounter<numTermInDoc){
            maxTermCounter = numTermInDoc;
        }
    }

    /**
     * checks whether the term exists in the dictionary in lower case
     * @param docName
     * @param termOld
     */
    private void checkIfExistsLower(String docName, ATerm termOld) {
        if (allWordsDic.containsKey(termOld)) {
            int counterWord = wordsInDoc.get(termOld);
            if(allWordsDic.get(termOld).get(docName)==null){
                // max
                checkMinMaxCounter(counterWord);
                allWordsDic.get(termOld).put(docName, counterWord);
            }else{
                counterWord = wordsInDoc.get(termOld)+allWordsDic.get(termOld).get(docName);
                checkMinMaxCounter(counterWord);
                allWordsDic.get(termOld).put(docName, counterWord);
            }
        } else {
            //if term do not exist
            termMap = new HashMap<>();
            checkMinMaxCounter(wordsInDoc.get(termOld));
            termMap.put(docName, wordsInDoc.get(termOld));
            allWordsDic.put(termOld, termMap);
            CheckCities(termOld, docName);
        }
    }

    /**
     * checks whether the term exists in the dictionary in upper case
     * @param docName
     * @param termOld
     */
    private void checkIfExistsUpper(String docName, ATerm termOld) {
        ATerm termUp = new Word(termOld.finalName.toUpperCase());
        termUp.setPosition(termOld.getPosition());
        //if termUp exists
        if (allWordsDic.containsKey(termUp)) {
            int counterWord = wordsInDoc.get(termOld);
            //if the doc does not exists in the term dictionary
            if(allWordsDic.get(termUp).get(docName)==null){
                checkMinMaxCounter(wordsInDoc.get(termOld));
                allWordsDic.get(termUp).put(docName, counterWord);

            }
            else {
                counterWord = wordsInDoc.get(termOld) + allWordsDic.get(termUp).get(docName);
                if(maxTermCounter<counterWord){
                    maxTermCounter = counterWord;
                }

                allWordsDic.get(termUp).put(docName, counterWord);
            }

            termUp.setTF(counterWord);

        } else {
            //if term do not exist
            termMap = new HashMap<>();
            checkMinMaxCounter(wordsInDoc.get(termOld));
            termMap.put(docName, wordsInDoc.get(termOld));
            allWordsDic.put(termUp, termMap);
            CheckCities(termUp, docName);
            termUp.setTF(wordsInDoc.get(termOld));
            termUp.setPosition(termOld.getPosition());
            entityTerms.add(termUp);
            Pair<ATerm,Integer> isEntity = new Pair<>(termUp,0);
            setPerDoc.put(termUp.finalName,isEntity);
        }
        if(setPerDoc.containsKey(termUp.finalName)){
            if(setPerDoc.get(termUp.finalName).getValue()==0) {
                setPerDoc.get(termUp.finalName).getKey().setTF(termUp.getTf());
                if (setPerDoc.get(termUp.finalName).getKey().getPosition() > termUp.getPosition()) {
                    setPerDoc.get(termUp.finalName).getKey().setPosition(termUp.getPosition());
                }
            }
        }else {
            entityTerms.add(termUp);
        }

    }

    /**
     * Function which updates the counter of the term in the doc
     * in case the term doesn't exist, insert.
     * @param term
     */
    private void increaseCounter(ATerm term){
        if(inIndex) {
            termInDoc++;
            if (wordsInDoc.containsKey(term)) {
                Integer tmp = wordsInDoc.get(term);
                wordsInDoc.put(term, tmp + 1);
            } else {
                wordsInDoc.put(term, 1);
                term.setPosition(this.pos);
            }
        }else{
            // insert here to Dictionary only for the query
        }
    }

    private String cutSigns(String beforeCut) {
        int lengthBeforeWord = beforeCut.length();
        int startCharacter=0;
        int endCharacter=beforeCut.length();

        // -,$,%,a-z,A-Z,0-9
        for(int i=0;i<beforeCut.length()-1;i++){
            if(Character.isLetterOrDigit(beforeCut.charAt(i)) || ((beforeCut.charAt(i) == '-' || beforeCut.charAt(i) == '$') && Character.isDigit(beforeCut.charAt(i+1))) ){
                break;
            }
            else
                startCharacter++;
        }
        if(startCharacter!=0) {
            beforeCut = beforeCut.substring(startCharacter);
            lengthBeforeWord = beforeCut.length();
            endCharacter=beforeCut.length();

        }//remove " 's "
        int startFromEnd = lengthBeforeWord;
        if(beforeCut.length()-2>=0 && ((beforeCut.charAt(beforeCut.length()-2) == '`' || (int)beforeCut.charAt(beforeCut.length()-2) == 39) && beforeCut.endsWith("s"))){
            endCharacter=beforeCut.length()-2;
            startFromEnd-=2;
        }
        for(int i=startFromEnd-1;i>0;i--){
            if(Character.isLetterOrDigit(beforeCut.charAt(i)) || (beforeCut.charAt(i) == '%'  && Character.isDigit(beforeCut.charAt(i-1))) || (beforeCut.charAt(i) == '.' && beforeCut.charAt(i-1) == 'S')){
                break;
            }
            else
                endCharacter--;
        }
        if(endCharacter!=lengthBeforeWord) {
            beforeCut = beforeCut.substring(0, endCharacter);
        }
        if(beforeCut.length()==1 && !Character.isLetterOrDigit(beforeCut.charAt(0))){
            beforeCut = "";
        }

        return beforeCut;
    }

    private String TermNumber(String word) {
        if(word.contains("/"))
            return word;
        double numberWord = Double.parseDouble(word);
        String numberInString;
        // under 1K
        if (numberWord < 1000) {
            return word;
        } else {
            // 1k - 1M
            if (numberWord < 1000000) {
                numberWord = numberWord / 1000;
                //numberWord = Math.round(numberWord*100.0)/100.0;
                numberInString = cutDot0(numberWord);
                termBeforeChanged = new StringBuffer(numberInString + "K");
                return termBeforeChanged.toString();

            } else {
                // 1M - 1B
                if (numberWord < 1000000000) {
                    numberWord = numberWord / 1000000;
                    //numberWord = Math.round(numberWord*100.0)/100.0;
                    numberInString = cutDot0(numberWord);
                    termBeforeChanged = new StringBuffer(numberInString + "M");
                    return termBeforeChanged.toString();
                }
                // over 1B
                else {
                    numberWord = numberWord / 1000000000;
                    //numberWord = Math.round(numberWord*100.0)/100.0;
                    numberInString = cutDot0(numberWord);
                    termBeforeChanged = new StringBuffer(numberInString + "B");
                    return termBeforeChanged.toString();
                }
            }
        }
    }

    private String cutDot0(double number){
        String numberInString = number+"";
        String dot0 = numberInString.substring(numberInString.length()-2);
        if(dot0.equals(".0")){
            return numberInString.substring(0,numberInString.length()-2);
        }
        return String.valueOf(number);
    }

    private void checkCapital(String termName, String docName, int pos){
        if(withStem){
            stem.add(termName.toCharArray(),termName.length());
            stem.stem();
            termName = stem.toString();
        }
        Countries capitalTerm = countryInMemory.getCountryByCapital(termName.toUpperCase());
        StringBuffer str = new StringBuffer(docName+":"+pos+",");
        // if is capital
        if(capitalTerm!=null){
            String pop =capitalTerm.getPopulation();
            String rightWord = TermNumber(pop);
            if(capitalTerms.containsKey(capitalTerm.getCapitalName())) {
                String tmp = capitalTerms.get(capitalTerm.getCapitalName());
                StringBuffer value = new StringBuffer(tmp+str);
                capitalTerms.put(capitalTerm.getCapitalName(), value.toString());
            }else{
                String t = capitalTerm.getCapitalName()+ "-" +capitalTerm.getCountryName() +" : "+capitalTerm.getCurrency()+" : "+rightWord+" : "+str;
                capitalTerms.put(capitalTerm.getCapitalName(),t);
            }
        }
    }

    public Map<String, String> getCapitalDictionary(){
        return capitalTerms;
    }

    public void clearDictionary() {
        docInfo.clear();
        allWordsDic.clear();
    }


    public void resetAll() {
        month.clear();
        afterNumber.clear();
        stopWords.clear();
        signs.clear();
        termMap.clear();
        wordsInDoc.clear();
        capitalTerms.clear();
        post.resetAll();
        countryInMemory.resetAll();
    }
}