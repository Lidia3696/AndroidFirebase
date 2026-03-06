package com.example.firebaseandroid;

public class Pokemon {
    //classe objecte per esborrar i actualitzar
    public String id;
    public String nom;
    public String atac1;
    public String atac2;
    public String atac3;
    public String atac4;
    public boolean shiny;

    public Pokemon( String id, String nom, String atac1, String atac2, String atac3, String atac4, boolean shiny) {
        this.nom = nom;
        this.id = id;
        // si l'atac no es null, agafa l'atac del constructor, si no, el text es queda buit
        this.atac1 = atac1 != null ? atac1 : "";
        this.atac2 = atac2 != null ? atac2 : "";
        this.atac3 = atac3 != null ? atac3 : "";
        this.atac4 = atac4 != null ? atac4 : "";
        this.shiny = shiny;
    }
}