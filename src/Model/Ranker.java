package Model;

import java.util.ArrayList;
import java.util.List;

public class Ranker {

     int numberOfDocs;
    double averageNumOfDocs;
    List <Double> IDFperDoc;
    DocData docData;
    DataCollector dataCollector;

    public Ranker(DataCollector dataCollector){
     IDFperDoc = new ArrayList<>();
     this.dataCollector = dataCollector;
     numberOfDocs = dataCollector.getNumberOfDocs();
     averageNumOfDocs = dataCollector.getAverageNumOfDocs();
    }



    public void start(DocData docData){
        calculateIDF(docData);
    }
    public int getNumberOfDocs() {
        return numberOfDocs;
    }

    public void setNumberOfDocs(int numberOfDocs) {
        this.numberOfDocs = numberOfDocs;
    }

    public double getAverageNumOfDocs() {
        return averageNumOfDocs;
    }

    public void setAverageNumOfDocs(double averageNumOfDocs) {
        this.averageNumOfDocs = averageNumOfDocs;
    }

    public void calculateIDF(DocData docData){
        for (int num:docData.getNumberOfDocPerTerm()) {
            double upNum = numberOfDocs-num+0.5;
            double downNum = num+0.5;
            double IDF = Math.log(upNum/downNum);
            IDFperDoc.add(IDF);

        }
        calculateBM25(docData);



    }

    private void calculateBM25(DocData docData) {

        double upNum ;
        double downNum;
        double B=0.75;
        double K=1.2;
        //double K = Math.min(docData.getDocLength()*(1.2/dataCollector.getAverageNumOfDocs()),2);
        //if(K<0.05)
        //    K=0.05;
        double BM25 =0;



        for (int num=0; num<docData.getNumberOfDocPerTerm().size(); num++){
                upNum = docData.getFreqList().get(num)*(K+1);
                double tmpNum = docData.getDocLength()/dataCollector.getAverageNumOfDocs();
                downNum = docData.getFreqList().get(num)+K*(1-B+(B*tmpNum));

            BM25 +=  IDFperDoc.get(num)*(upNum/downNum);
        }

        docData.setSumBM25(BM25);


    }


}
