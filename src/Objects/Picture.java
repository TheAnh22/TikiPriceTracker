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
public class Picture implements Serializable {

    private String Product_ID;
    private String URL_Image;
    private static final long serialVersionUID = 1L;
    public Picture(String Product_ID, String URL_Image) {
        this.Product_ID = Product_ID;
        this.URL_Image = URL_Image;
    }
    public String getProduct_ID() {
        return Product_ID;
    }

    public String getURL_Image() {
        return URL_Image;
    }
    
    
    
    
}
