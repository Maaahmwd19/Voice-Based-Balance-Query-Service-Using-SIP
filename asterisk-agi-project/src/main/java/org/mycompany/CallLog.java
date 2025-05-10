package org.mycompany;

import java.util.Date;

public class CallLog {
    private String msisdn;
    private String dtmf;
    private double balance;
    private Date startTime;
    private Date endTime;

    public CallLog(String msisdn, String dtmf, double balance, Date startTime, Date endTime) {
        this.msisdn = msisdn;
        this.dtmf = dtmf;
        this.balance = balance;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getDtmf() {
        return dtmf;
    }

    public void setDtmf(String dtmf) {
        this.dtmf = dtmf;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    
}
