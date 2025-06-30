package fr.ecodeli.ecodelidesktop.clients;

import com.google.gson.annotations.SerializedName;

public class Client {

    @SerializedName("id")
    private String id;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("profile_picture")
    private String profilePicture;

    @SerializedName("nbSignalements")
    private int nbSignalements;

    @SerializedName("profilTransporteur")
    private boolean profilTransporteur;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("email")
    private String email;

    @SerializedName("nomAbonnement")
    private String nomAbonnement;

    @SerializedName("nbDemandeDeLivraison")
    private int nbDemandeDeLivraison;

    @SerializedName("nombreDePrestations")
    private int nombreDePrestations;

    public Client() {}

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getNomAbonnement() { return nomAbonnement; }
    public int getNbDemandeDeLivraison() { return nbDemandeDeLivraison; }
    public int getNombreDePrestations() { return nombreDePrestations; }
    public String getProfilePicture() { return profilePicture; }
    public int getNbSignalements() { return nbSignalements; }
    public boolean isProfilTransporteur() { return profilTransporteur; }
}
