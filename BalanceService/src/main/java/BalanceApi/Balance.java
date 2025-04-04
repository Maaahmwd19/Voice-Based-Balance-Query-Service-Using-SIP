package BalanceApi;

import jakarta.persistence.*;

@Entity
@Table(name = "balance")
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "msisdn")
    private String msisdn;
    @Column(name = "balance")
    private float value;

    public Balance(){
        id = 0;
        msisdn = "";
        value = 0.0f;
    }
    public Balance(int id, String msisdn, float value){
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
    public float getValue(){
        return value;
    }
    public void setId(int id){
        this.id = id;
    }
    public void setMsisdn(String msisdn){
        this.msisdn = msisdn;
    }
    public void setValue(float value){
        this.value = value;
    }
}
