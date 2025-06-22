package fr.ecodeli.ecodelidesktop.services;

import com.google.gson.annotations.SerializedName;

public class Service {
    @SerializedName("service_id")
    private String serviceId;

    @SerializedName("service_type")
    private String serviceType;

    private String status;
    private String name;
    private String city;
    private double price;

    @SerializedName("price_admin")
    private double priceAdmin;

    @SerializedName("duration_time")
    private int durationTime;

    private boolean available;
    private String description;
    private Author author;
    private double rate;

    public String getServiceId() { return serviceId; }
    public String getServiceType() { return serviceType; }
    public String getStatus() { return status; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public double getPrice() { return price; }
    public double getPriceAdmin() { return priceAdmin; }
    public int getDurationTime() { return durationTime; }
    public boolean isAvailable() { return available; }
    public String getDescription() { return description; }
    public Author getAuthor() { return author; }
    public double getRate() { return rate; }
}
