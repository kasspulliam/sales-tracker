package com.kassidy.sales_tracker;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private int soldCount;
    private String imageUrl;

    //seperate workspaces
    private String workspaceId;

    
    //constructors---------------------------------------
    public Product() {
    }
    public Product(String name, double price) {
        this.name = name;
        this.price = price;
        this.soldCount = 0;
    }

    //setters---------------------------------------------------
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setWorkspaceId(String workspaceId){
        this.workspaceId= workspaceId;
    } 
    public void setName(String name) {
        this.name = name;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    
    //actions----------------------------------------------
    public void addSale() {
        soldCount++;
    }
    public void removeSale() {
        if (soldCount > 0) {
            soldCount--;
        }
    }


    
    //getters---------------------------------------------------
    public String getImageUrl(){
        return imageUrl;
    }
    public String getWorkspaceId(){
        return workspaceId;
    }
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getSoldCount() {
        return soldCount;
    }

    public double getRevenue() {
        return price * soldCount;
    }
}
