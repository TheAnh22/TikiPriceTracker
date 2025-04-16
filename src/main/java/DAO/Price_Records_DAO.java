/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Objects.Price_Records;
import Objects.Products;
import Objects.ResponsePrice;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author The Anh
 */
public class Price_Records_DAO {
    private Connection conn;
    private static final String url="jdbc:mysql://localhost:3306/tiki";
    private static final String USER = "root";
    private static final String PASSWORD = "Atheanh123";
    private static final String dbname = "tiki";
    public Price_Records_DAO (){
        try {
            conn = DriverManager.getConnection(url, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ArrayList<ResponsePrice> getPriceRecordByID(String id){
        ArrayList<ResponsePrice> Price_Records = new ArrayList<>();
        try {
            PreparedStatement stm= conn.prepareStatement("SELECT * FROM price_record where product_id=?");
            stm.setString(1, id);
            ResultSet rs = stm.executeQuery();
            while (rs.next()){
                
                int Product_Price_ID = rs.getInt("Product_Price_ID");
                String Product_ID = rs.getString("Product_ID");
                String Price = rs.getString("Price");
                Date Price_Date = rs.getDate("Price_Date");
                
                ResponsePrice price_record= new ResponsePrice(Product_ID,Price,Price_Date);
                Price_Records.add(price_record);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Price_Records;
    }
    
    public ArrayList<Price_Records> getAllPriceRecord(){
        ArrayList<Price_Records> Price_Records = new ArrayList<>();
        try {
            PreparedStatement stm= conn.prepareStatement("SELECT * FROM price_record");
            ResultSet rs = stm.executeQuery();
            while (rs.next()){
                String Product_ID = rs.getString("Product_ID");
                String Price = rs.getString("Price");
                Date Price_Date = rs.getDate("Price_Date");
                
                Price_Records price_record= new Price_Records(Product_ID,Price,Price_Date);
                Price_Records.add(price_record);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Price_Records;
    }
    
    public void addPriceRecord(Price_Records pricerecord){
        try {
            String query = """
                           INSERT IGNORE INTO price_record (Product_ID, Price, Price_Date) VALUES ( ?, ?,?)
                           """;
            PreparedStatement stm = conn.prepareStatement(query);
            
            stm.setString(1, pricerecord.getProduct_ID());
            stm.setString(2, pricerecord.getPrice());
            stm.setDate(3, pricerecord.getPrice_Date());
            stm.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
