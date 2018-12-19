package Model;

import java.util.List;

public class DocData {
    String DocName;
    int docWeight;
    String city;    List<Integer> freqList;
    int docLength;
    List<Integer>numberOfDocPerTerm;
    List<Integer>Jaccard;


    public DocData(String docName) {
        this.DocName = docName;
    }


    public int getDocWeight() {
        return docWeight;
    }

    public void setDocWeight(int docWeight) {
        this.docWeight = docWeight;
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

    public void setFreqList(List<Integer> freqList) {
        this.freqList = freqList;
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

    public void setNumberOfDocPerTerm(List<Integer> numberOfDocPerTerm) {
        this.numberOfDocPerTerm = numberOfDocPerTerm;
    }

    public List<Integer> getJaccard() {
        return Jaccard;
    }

    public void setJaccard(List<Integer> jaccard) {
        this.Jaccard = jaccard;
    }




}
