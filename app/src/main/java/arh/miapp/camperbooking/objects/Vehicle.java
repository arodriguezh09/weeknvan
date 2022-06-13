package arh.miapp.camperbooking.objects;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

public class Vehicle implements Serializable {

    private String plate;
    private String owner;
    private String brand;
    private String model;
    private String photo;
    private String fuel;
    private String city;
    private String type;
    private String description;
    private String idUser;
    private int year;
    private int passengers;
    private int beds;
    private double pricePerDay;
    private double rating;

    private Bitmap bitmap;
    private ArrayList<Bitmap> photos;

    public ArrayList<Bitmap> getBitmaps() {
        return photos;
    }

    public void setBitmaps(ArrayList<Bitmap> photos) {
        this.photos = photos;
    }


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


    public Vehicle() {

    }

    public Vehicle(String plate, String owner, String brand, String model, String photo, String fuel, String city, String vehicleType, String description, int year, int passengers, int beds, double pricePerDay, double rating) {
        this.plate = plate;
        this.owner = owner;
        this.brand = brand;
        this.model = model;
        this.photo = photo;
        this.fuel = fuel;
        this.city = city;
        this.type = vehicleType;
        this.description = description;
        this.year = year;
        this.passengers = passengers;
        this.beds = beds;
        this.pricePerDay = pricePerDay;
        this.rating = rating;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }


    public void setDescription(String description) { this.description = description; }

    public String getIdUser() { return idUser; }

    public void setIdUser(String idUser) { this.idUser = idUser; }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public int getBeds() {
        return beds;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
