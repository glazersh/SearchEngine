package Model.Term;

public abstract class ATerm {



    private int position;
    private int tf;
    public String finalName;


    public ATerm(String word){
        finalName = word;
    }

    @Override
    public boolean equals(Object o){
        if(o == this)
            return true;
        if(!(o instanceof ATerm)){
            return false;
        }
        ATerm term = (ATerm)o;

        return term.finalName.equals(finalName);
    }

    @Override
    public int hashCode(){
        int result = 17;
        int hash = finalName.hashCode();
        result = 31 * result + hash;
        return result;
    }

    public void setPosition(int pos){
        this.position = pos;
    }

    public void setTF(int tf){
        this.tf = tf;
    }

    public int getPosition() {
        return position;
    }

    public int getTf() {
        return tf;
    }

}
