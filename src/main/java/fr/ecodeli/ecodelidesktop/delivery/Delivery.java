package fr.ecodeli.ecodelidesktop.delivery;

import com.google.gson.annotations.SerializedName;

public class Delivery {
    @SerializedName("departure_date")
    private String departureDate;

    @SerializedName("arrival_date")
    private String arrivalDate;

    private String status;

    @SerializedName("cart_dropped")
    private boolean cartDropped;

    private boolean isBox;

    private Address departure;
    private Address arrival;

    public String getDepartureDate() { return departureDate; }
    public String getArrivalDate() { return arrivalDate; }
    public String getStatus() { return status; }
    public boolean isCartDropped() { return cartDropped; }
    public boolean isBox() { return isBox; }

    public Address getDeparture() { return departure; }
    public Address getArrival() { return arrival; }
}
