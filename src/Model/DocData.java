package Model;

import java.util.ArrayList;
import java.util.List;

public class DocData {
    private String DocName;
    private String city;
    private List<Integer> freqList;
    private int docLength;
    private List<Integer>numberOfDocPerTerm;
    private double Jaccard;
    private double sumBM25;


    public DocData(String docName) {
        this.DocName = docName;
        this.numberOfDocPerTerm = new ArrayList<>();
        this.freqList = new ArrayList<>();

    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Integer> getFreqList() {
        return freqList;
    }

    public void addToFreqList(int num) {
        this.freqList.add(num);
    }

    public int getDocLength() {
        return docLength;
    }

    public void setDocLength(int docLength) {
        this.docLength = docLength;
    }

    public List<Integer> getNumberOfDocPerTerm() {
        return numberOfDocPerTerm;
    }

    public void addNumberOfDocPerTerm(int num) {
        this.numberOfDocPerTerm.add(num);
    }

    public double getJaccard() {
        return Jaccard;
    }

    public void setJaccard(double jaccard) {
        this.Jaccard = jaccard;
    }

    public double getSumBM25() {
        return sumBM25;
    }

    public void setSumBM25(double sumBM25) {
        this.sumBM25 = sumBM25;
    }




}
