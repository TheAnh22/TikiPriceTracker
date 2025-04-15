/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BUS;

import DAO.Price_Records_DAO;
import Objects.Price_Records;
import Objects.Price_Records;
import Objects.ResponsePrice;
import java.util.ArrayList;

/**
 *
 * @author The Anh
 */
public class Price_Records_BUS {
    private Price_Records_DAO price_records_dao = new Price_Records_DAO();
    
    public ArrayList<ResponsePrice> getAllPriceRecord(String id){
        return price_records_dao.getAllPriceRecord(id);
    }
    public void addPriceRecords(Price_Records pricerecords){
        price_records_dao.addPriceRecord(pricerecords);
    }
}
