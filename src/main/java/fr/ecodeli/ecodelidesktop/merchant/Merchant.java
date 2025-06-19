package fr.ecodeli.ecodelidesktop.merchant;

import com.google.gson.annotations.SerializedName;

public class Merchant {

    @SerializedName("id")
    private String id;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("companyName")
    private String companyName;

    @SerializedName("city")
    private String city;

    @SerializedName("nomAbonnement")
    private String nomAbonnement;

    @SerializedName("nbDemandeDeLivraison")
    private int nbDemandeDeLivraison;

    public Merchant() {}

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getCompanyName() { return companyName; }
    public String getCity() { return city; }
    public String getNomAbonnement() { return nomAbonnement; }
    public int getNbDemandeDeLivraison() { return nbDemandeDeLivraison; }
}
