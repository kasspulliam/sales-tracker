package com.kassidy.sales_tracker;

// imports allow us to use JPA annotations.
// JPA lets Java objects be stored in a database.
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// @Entity tells Spring this class represents something
// that should be stored in the database.
@Entity
public class Product {

    // @Id means this variable is the unique identifier
    // for each Product object.
    @Id

    // Automatically generates a new ID for each Product.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Stores the product's name.
    private String name;

    // Stores the product's price.
    private double price;

    // Stores how many of this product have been sold.
    private int soldCount;

    private String imageUrl;

    //seperate workspaces
    private String workspaceId;

    public String getImageUrl(){
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Default constructor.
    // JPA needs this so it can recreate Product objects
    // when reading them from the database.
    public Product() {
    }

    // Constructor used when creating a new product.
    public Product(String name, double price) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;

        // Every new product starts with zero sales.
        this.soldCount = 0;
    }

    //getter and setter for workspaceId
    public String getWorkspaceId(){
        return workspaceId;
    }
    public void setWorkspaceId(String workspaceId){
        this.workspaceId= workspaceId;
    }    

    // Getter for the product ID.
    public Long getId() {
        return id;
    }

    // Getter for the product name.
    public String getName() {
        return name;
    }

    // Getter for the product price.
    public double getPrice() {
        return price;
    }

    // Getter for number sold.
    public int getSoldCount() {
        return soldCount;
    }

    // Calculates revenue from this product.
    public double getRevenue() {
        return price * soldCount;
    }

    // Changes the product name.
    public void setName(String name) {
        this.name = name;
    }

    // Changes the product price.
    public void setPrice(double price) {
        this.price = price;
    }

    // Adds one sale.
    public void addSale() {
        soldCount++;
    }

    // Removes one sale, but never goes below zero.
    public void removeSale() {
        if (soldCount > 0) {
            soldCount--;
        }
    }
}
