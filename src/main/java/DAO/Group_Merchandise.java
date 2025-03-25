/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

/**
 *
 * @author The Anh
 */
public class Group_Merchandise {
    private String Group_Merchandise_ID;
    private String Merchandise_Name;

    public Group_Merchandise(String Group_Merchandise_ID, String Merchandise_Name) {
        this.Group_Merchandise_ID = Group_Merchandise_ID;
        this.Merchandise_Name = Merchandise_Name;
    }

    public String getGroup_Merchandise_ID() {
        return Group_Merchandise_ID;
    }

    public String getMerchandise_Name() {
        return Merchandise_Name;
    }
    
    
}
