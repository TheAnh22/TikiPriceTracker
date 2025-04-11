/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BUS;
import DAO.Picture_DAO;
import Objects.*;
import java.util.ArrayList;
/**
 *
 * @author The Anh
 */
public class Picture_BUS {
    Picture_DAO picture_DAO = new Picture_DAO();
    public void addPicture(String Product_ID,String URL_Image){
        picture_DAO.addPicture(Product_ID, URL_Image);
    }
    public ArrayList<Picture> getAllPicture(){
        return picture_DAO.getAllPicture();
    }
}
