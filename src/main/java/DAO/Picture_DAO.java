/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;


import Objects.Picture;
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
public class Picture_DAO {
    private Connection conn;
    private static final String url="jdbc:mysql://localhost:3306/tiki";
    private static final String USER = "root";
    private static final String PASSWORD = "Atheanh123";
    private static final String dbname = "tiki";
    private static ArrayList<Picture> Picture;
    public Picture_DAO (){
        try {
            conn = DriverManager.getConnection(url, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addPicture(String ID,String URL_Image){
        
        try {
            String sql = "INSERT IGNORE picture (Product_ID, URL_Image) VALUES (?, ?)";
            PreparedStatement preStatement = conn.prepareStatement(sql);
            preStatement.setString(1,ID);
            preStatement.setString(2,URL_Image);
            preStatement.executeUpdate();
        } catch (Exception ex) {
            
        }
    }
    public ArrayList<Picture> getAllPicture(){
        try {
            PreparedStatement stm= conn.prepareStatement("SELECT * FROM picture");
            ResultSet rs = stm.executeQuery();
            while (rs.next()){
                String Product_ID = rs.getString("Product_ID");
                String URL_Image = rs.getString("URL_Image");
                Picture picture= new Picture(Product_ID,URL_Image);
                Picture.add(picture);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Picture;
    }
}
