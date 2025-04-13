/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BUS;

import DAO.InfoDAO;
import Objects.ResponseInfo;
import java.util.ArrayList;

/**
 *
 * @author The Anh
 */
public class ResponseInfoBUS {
    InfoDAO info_DAO = new InfoDAO();
    public ArrayList<ResponseInfo> getResponse(String search){
        return info_DAO.getInfo(search);
    }
}
