/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Objects.ResponseInfo;
import Objects.Products;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author The Anh
 */
public class InfoDAO {
    private Connection conn;
    private static final String url="jdbc:mysql://localhost:3306/tiki";
    private static final String USER = "root";
    private static final String PASSWORD = "Atheanh123";
    private static final String dbname = "tiki";
    public InfoDAO(){
        try {
            conn = DriverManager.getConnection(url, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ArrayList<ResponseInfo> getInfo(String search){
        ArrayList<ResponseInfo> list = new ArrayList<>();
        String sql= """
                    select p.Product_ID,p.Product_Name,p.Origin,pic.URL_Image from products p INNER JOIN picture pic on p.product_id = pic.product_id WHERE product_name  REGEXP ?
                    """;
        try {
            PreparedStatement stm= conn.prepareStatement(sql);
            stm.setString(1, search);
            ResultSet rs = stm.executeQuery();
            while (rs.next()){
                
                String Product_ID = rs.getString("Product_ID");
                String Product_Name = rs.getString("Product_Name");
                String Origin = rs.getString("Origin");
                String URL_Image = rs.getString("URL_Image");
                ResponseInfo info = new ResponseInfo(Product_ID, Product_Name, Origin, URL_Image);
                list.add(info);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;        
    }

}
