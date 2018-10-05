package com.cyberpunk2077.cursochat;

public class usuariosPOJO {
    public String Nombre, UID, PhotoURL, token;

    usuariosPOJO(){}

    usuariosPOJO(String NOM, String UID, String PhotoURL){
        this.Nombre = NOM;
        this.UID = UID;
        this.PhotoURL = PhotoURL;
    }

    @Override
    public String toString() {
        return this.Nombre;
    }
}
