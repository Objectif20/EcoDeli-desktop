package fr.ecodeli.ecodelidesktop.services;

import java.util.List;

public class ServiceDetails {
    private String serviceId;
    private String serviceType;
    private String status;
    private String name;
    private String city;
    private double price;
    private double priceAdmin;
    private int durationTime;
    private boolean available;
    private List<String> keywords;
    private List<String> images;
    private String description;
    private Author author;
    private double rate;
    private List<Comment> comments;

    public static class Author {
        private String id;
        private String name;
        private String email;
        private String photo;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhoto() { return photo; }
        public void setPhoto(String photo) { this.photo = photo; }
    }

    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getPriceAdmin() { return priceAdmin; }
    public void setPriceAdmin(double priceAdmin) { this.priceAdmin = priceAdmin; }

    public int getDurationTime() { return durationTime; }
    public void setDurationTime(int durationTime) { this.durationTime = durationTime; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }

    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
}
