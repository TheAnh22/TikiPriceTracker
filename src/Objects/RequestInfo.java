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
public class RequestInfo implements Serializable{
    private static final long serialVersionUID = 1L;
    private String text;

    public String getText() {
        return text;
    }

    public RequestInfo(String text) {
        this.text = text;
    }
    
    
}
