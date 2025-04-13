/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;

import java.io.Serializable;

/**
 *
 * @author The Anh
 */
public class ResponseInfo implements Serializable{
    private String Product_ID;
    private String Prodcut_Name;
    private String Origin;
    private String URL_Image;
    private static final long serialVersionUID = 1L;
    public String getProduct_ID() {
        return Product_ID;
    }

    public String getProdcut_Name() {
        return Prodcut_Name;
    }

    public String getOrigin() {
        return Origin;
    }

    public String getURL_Image() {
        return URL_Image;
    }
    
    public ResponseInfo(String Product_ID, String Prodcut_Name, String Origin, String URL_Image) {
        this.Product_ID = Product_ID;
        this.Prodcut_Name = Prodcut_Name;
        this.Origin = Origin;
        this.URL_Image = URL_Image;
    }
    
    
}
