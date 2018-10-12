package com.cyberpunk2077.cursochat;

public class mensajePOJO {
    public String UID_DE, UID_PARA;
    public String cuerpo, fechaMSJ;
    public String urlImagen;

    mensajePOJO(){}

    public mensajePOJO(String UID_DE, String UID_PARA, String cuerpo, String fecha, String urlImagen) {
        this.UID_DE = UID_DE;
        this.UID_PARA = UID_PARA;
        this.cuerpo = cuerpo;
        this.fechaMSJ = fecha;
        this.urlImagen = urlImagen;
    }
}
