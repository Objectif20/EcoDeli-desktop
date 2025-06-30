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

    @SerializedName("siret")
    private String siret;

    @SerializedName("city")
    private String city;

    @SerializedName("address")
    private String address;

    @SerializedName("country")
    private String country;

    @SerializedName("phone")
    private String phone;

    @SerializedName("description")
    private String description;

    @SerializedName("postalCode")
    private String postalCode;

    @SerializedName("profilePicture")
    private String profilePicture;

    @SerializedName("nomAbonnement")
    private String nomAbonnement;

    @SerializedName("nbDemandeDeLivraison")
    private int nbDemandeDeLivraison;

    public Merchant() {}

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getCompanyName() { return companyName; }
    public String getSiret() { return siret; }
    public String getCity() { return city; }
    public String getAddress() { return address; }
    public String getCountry() { return country; }
    public String getPhone() { return phone; }
    public String getDescription() { return description; }
    public String getPostalCode() { return postalCode; }
    public String getProfilePicture() { return profilePicture; }
    public String getNomAbonnement() { return nomAbonnement; }
    public int getNbDemandeDeLivraison() { return nbDemandeDeLivraison; }
}
