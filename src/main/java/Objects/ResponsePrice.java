/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;

import java.io.Serializable;
import java.sql.Date;

/**
 *
 * @author The Anh
 */
public class ResponsePrice implements Serializable{
    private static final long serialVersionUID = 1L;
    private String Product_ID;
    private String Price ;
    private Date Price_Date ;

    public ResponsePrice(String Product_ID, String Price, Date Price_Date) {
        this.Product_ID = Product_ID;
        this.Price = Price;
        this.Price_Date = Price_Date;
    }

    public String getProduct_ID() {
        return Product_ID;
    }

    public String getPrice() {
        return Price;
    }

    public Date getPrice_Date() {
        return Price_Date;
    }
    
}
