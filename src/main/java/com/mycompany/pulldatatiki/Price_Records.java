/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pulldatatiki;

import java.sql.Date;

/**
 *
 * @author The Anh
 */
public class Price_Records {
    private int Product_Price_ID;
    private String Product_ID;
    private String Price;
    private Date Price_Date;
    
    public Price_Records(int Product_Price_ID, String Product_ID, String Price, Date Price_Date){
        this.Product_Price_ID = Product_Price_ID;
        this.Product_ID = Product_ID;
        this.Price = Price;
        this.Price_Date = Price_Date;
    }

    public int getProduct_Price_ID() {
        return Product_Price_ID;
    }

    public String getProduct_ID() {
        return Product_ID;
    }

    public String getPrice() {
        return Price;
    }

    public Date getPrice_Date() {
        return Price_Date;
    }
    
    
    
}
