/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;

/**
 *
 * @author The Anh
 */
public class Picture {

    private String Product_ID;
    private String URL_Image;
    public String getProduct_ID() {
        return Product_ID;
    }

    public String getURL_Image() {
        return URL_Image;
    }
    
    public Picture(String Product_ID, String URL_Image) {
        this.Product_ID = Product_ID;
        this.URL_Image = URL_Image;
    }
    
    
}
