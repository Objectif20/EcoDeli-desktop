package fr.ecodeli.ecodelidesktop.warehouse;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Warehouse {
    @SerializedName("warehouse_id")
    private String warehouseId;

    private String city;
    private int capacity;
    private List<String> coordinates;
    private String photo;
    private String description;
    private String address;

    @SerializedName("postal_code")
    private String postalCode;

    public String getId() { return warehouseId; }
    public String getWarehouseId() { return warehouseId; }
    public String getCity() { return city; }
    public int getCapacity() { return capacity; }
    public List<String> getCoordinates() { return coordinates; }
    public String getPhoto() { return photo; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public String getPostalCode() { return postalCode; }

    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }
    public void setCity(String city) { this.city = city; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setCoordinates(List<String> coordinates) { this.coordinates = coordinates; }
    public void setPhoto(String photo) { this.photo = photo; }
    public void setDescription(String description) { this.description = description; }
    public void setAddress(String address) { this.address = address; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCoordinatesAsString() {
        return (coordinates != null && coordinates.size() == 2)
                ? coordinates.get(0) + ", " + coordinates.get(1)
                : "0.000000, 0.000000";
    }
}