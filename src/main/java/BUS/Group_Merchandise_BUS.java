/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BUS;

import DAO.Group_Merchandise_DAO;
import Objects.Group_Merchandise;
import java.util.ArrayList;

/**
 *
 * @author The Anh
 */
public class Group_Merchandise_BUS {
    private Group_Merchandise_DAO group_merchandise_dao = new Group_Merchandise_DAO();
    
    public ArrayList<Group_Merchandise> getAllGroupMerchandise(){
        return group_merchandise_dao.getAllGroupMerchandise();
    }
    public void addGroupMerchandise(String ID,String name){
        group_merchandise_dao.addGroup_Merchandise(ID, name);
    }
}
