/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;

import Objects.Group_Merchandise;
import Objects.Picture;
import Objects.Price_Records;
import Objects.Products;
import java.io.Serializable;
import java.util.ArrayList;



/**
 *
 * @author The Anh
 */
public class DataPacket implements Serializable {
    private ArrayList<Products> Products_Array;
    private ArrayList<Price_Records> Price_Records_Array;
    private ArrayList<Group_Merchandise> Group_Merchandise_Array;
    private ArrayList<Picture> Picture_Array;
    private static final long serialVersionUID = 1L;
    public  ArrayList<Products> getProducts_Array() {
        return Products_Array;
    }

    public  ArrayList<Price_Records> getPrice_Records_Array() {
        return Price_Records_Array;
    }

    public  ArrayList<Group_Merchandise> getGroup_Merchandise_Array() {
        return Group_Merchandise_Array;
    }

    public  ArrayList<Picture> getPicture_Array() {
        return Picture_Array;
    }

    public DataPacket(ArrayList<Products> Products_Array, 
           ArrayList<Price_Records> Price_Records_Array,
           ArrayList<Group_Merchandise> Group_Merchandise_Array,
           ArrayList<Picture> Picture_Array) {
        this.Products_Array = Products_Array;
        this.Group_Merchandise_Array = Group_Merchandise_Array;
        this.Picture_Array = Picture_Array;
        this.Price_Records_Array = Price_Records_Array;
        
    }

    public DataPacket() {
    }
    
    
}
