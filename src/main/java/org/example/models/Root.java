package org.example.models;

public class Root{
    public int Id;
    public String Code;
    public String Ccy;
    public String CcyNm_RU;
    public String CcyNm_UZ;
    public String CcyNm_UZC;
    public String CcyNm_EN;
    public String Nominal;
    public String Rate;
    public String Diff;
    public String Date;

    @Override
    public String toString() {
        return "Valyuta nomi: " + CcyNm_UZ + "\n " + Ccy + " 1 = " + Rate + " UZS";
    }
}
