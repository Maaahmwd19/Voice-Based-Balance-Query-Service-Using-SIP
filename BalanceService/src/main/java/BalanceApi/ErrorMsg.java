/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BalanceApi;

/**
 *
 * @author omar
 */
public class ErrorMsg {
    private String errorMsg;
    private int errorCode;
    private String errorDescription;

    public ErrorMsg() {
        this.errorMsg = "";
        this.errorCode = 0;
        this.errorDescription = "";
    }

    public ErrorMsg(String errorMsg, int errorCode, String errorDescription) {
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }       

    public String getErrorMsg() {
        return errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

}
