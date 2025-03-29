/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import BUS.Group_Merchandise_BUS;
import BUS.Price_Records_BUS;
import BUS.Products_BUS;
import DAO.Products_DAO;
import DAO.Price_Records_DAO;
import Objects.Price_Records;
import Objects.Products;
import java.util.ArrayList;

/**
 *
 * @author The Anh
 */
public class Test {
    public static void main(String[] args) {
        Products_BUS pb = new Products_BUS();
        Price_Records_BUS prb= new Price_Records_BUS();
        Group_Merchandise_BUS gmb = new Group_Merchandise_BUS();
        ArrayList <Objects.Group_Merchandise> objects = gmb.getAllGroupMerchandise();
        for(Objects.Group_Merchandise c : objects){
            System.out.println(c.getMerchandise_Name());
        }
    }
}
