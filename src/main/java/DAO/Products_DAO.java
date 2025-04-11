/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Objects.Products;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author The Anh
 */
public class Products_DAO {
    private Connection conn;
    private static final String url="jdbc:mysql://localhost:3306/tiki";
    private static final String USER = "root";
    private static final String PASSWORD = "Atheanh123";
    private static final String dbname = "tiki";
    public Products_DAO(){
        try {
            conn = DriverManager.getConnection(url, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ArrayList<Products> getAllProducts(){
        ArrayList<Products> Products = new ArrayList<>();
        try {
            PreparedStatement stm= conn.prepareStatement("SELECT * FROM products");
            ResultSet rs = stm.executeQuery();
            while (rs.next()){
                
                String Product_ID = rs.getString("Product_ID");
                String Group_Merchandise_ID = rs.getString("Group_Merchandise_ID");
                String Product_Name = rs.getString("Product_Name");
                String Origin = rs.getString("Origin");
                
                Products product= new Products(Product_ID,Group_Merchandise_ID,Product_Name,Origin);
                Products.add(product);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Products;
    }
    public Products getOneProducts(String ID){
        Products product = new Products();
        try{
            String query="""
                         SELECT * FROM products where product_id = ?
                         """;
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setString(1, ID);
            ResultSet rs =stm.executeQuery();
            while(rs.next()){
                String Product_ID = rs.getString("Product_ID");
                String Group_Merchandise_ID = rs.getString("Group_Merchandise_ID");
                String Product_Name = rs.getString("Product_Name");
                String Origin = rs.getString("Origin");
                product = new Products(Product_ID,Group_Merchandise_ID,Product_Name,Origin);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return product;
    }
    public void addProducts(Products product){
        try {
            PreparedStatement stm = conn.prepareStatement("INSERT IGNORE INTO products (Product_ID, Group_Merchandise_ID, Product_Name, Origin) VALUES (?, ?, ?, ?)");
            stm.setString(1, product.getProductID());
            stm.setString(2, product.getGroup_Merchandise_ID());
            stm.setString(3, product.getProduct_Name());
            stm.setString(4, product.getOrigin());
            stm.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
