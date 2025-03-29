/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BUS;

import Objects.Products;
import java.util.ArrayList;

/**
 *
 * @author The Anh
 */
public class Products_BUS {
    private DAO.Products_DAO products_dao = new DAO.Products_DAO();
    
    public ArrayList<Products> getAllProduct(){
        return products_dao.getAllProducts();
    }
    public void addProducts(Products product){
        products_dao.addProducts(product);
    }
}
