package Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

public class mainParse {



    public static void main(String [] args) {

        //ParseUnit p = new ParseUnit();
        String tmp = "mandargli";
        Stemmer s = new Stemmer();
        SnowballStemmer sb = new englishStemmer() ;
        long start1 = System.nanoTime();
        s.add(tmp.toCharArray(), tmp.length());
        s.stem();
        long end1 = System.nanoTime();
        System.out.println("regular- "+s.toString()+ " time :  " +(end1-start1));
        long start = System.nanoTime();
        sb.setCurrent(tmp);
        sb.stem();
        long end = System.nanoTime();
        System.out.println("Snowball- "+sb.getCurrent() + " time :  " +(end-start));



    }


}