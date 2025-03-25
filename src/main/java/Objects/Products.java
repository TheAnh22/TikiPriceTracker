/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;

/**
 *
 * @author The Anh
 */
public class Products {
    private String Product_ID;
    private String Group_Merchandise_ID;
    private String Product_Name;
    private String Origin;
    
    public Products(String Product_ID, String Group_Merchandise_ID, String Product_Name, String Origin){
        this.Product_ID = Product_ID;
        this.Group_Merchandise_ID = Group_Merchandise_ID;
        this.Product_Name = Product_Name;
        this.Origin = Origin;
    }
    public String getProductID (){
        return Product_ID;
    }
    public String getGroup_Merchandise_ID (){
        return Group_Merchandise_ID;
    }
    public String getProduct_Name (){
        return Product_Name;
    }
    public String getOrigin (){
        return Origin;
    }
}
