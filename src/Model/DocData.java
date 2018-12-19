package Model;

import java.util.List;

public class DocData {
    String DocName;
    String city;
    List<Integer> freqList;
    int docLength;
    List<Integer>numberOfDocPerTerm;
    List<Double>Jaccard;
    double sumBM25;


    public DocData(String docName) {
        this.DocName = docName;
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

    public List<Double> getJaccard() {
        return Jaccard;
    }

    public void setJaccard(List<Double> jaccard) {
        this.Jaccard = jaccard;
    }

    public double getSumBM25() {
        return sumBM25;
    }

    public void setSumBM25(double sumBM25) {
        this.sumBM25 = sumBM25;
    }




}
