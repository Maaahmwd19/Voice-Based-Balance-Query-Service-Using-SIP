package org.mycompany;

public class Balance {
    private int id;
    private String msisdn;
    private double value;

    public Balance() {
        id = 0;
        msisdn = "";
        value = 0.0f;
    }

    public Balance(int id, String msisdn, double value) {
        this.id = id;
        this.msisdn = msisdn;
        this.value = value;
    }

    public int getId(){
        return id;
    }
    public String getMsisdn(){
        return msisdn;
    }
    public double getValue(){
        return value;
    }
    public void setId(int id){
        this.id = id;
    }
    public void setMsisdn(String msisdn){
        this.msisdn = msisdn;
    }
    public void setValue(double value){
        this.value = value;
    }
    public String toString(){
        return "id = " + id + ", msisdn = " + msisdn + ", value = " + value;
    }
}
