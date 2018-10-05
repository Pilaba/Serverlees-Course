package com.cyberpunk2077.cursochat;

public class mensajePOJO {
    public String UID_DE,UID_PARA;
    public String cuerpo, fechaMSJ;

    mensajePOJO(){}

    public mensajePOJO(String UID_DE, String UID_PARA, String cuerpo, String fecha) {
        this.UID_DE = UID_DE;
        this.UID_PARA = UID_PARA;
        this.cuerpo = cuerpo;
        this.fechaMSJ = fecha;
    }
}
