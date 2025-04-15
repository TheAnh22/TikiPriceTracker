/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author The Anh
 */
public class ClientHandler {
    private String host;
    private int port;
    private static DataPacket data;
    private static ArrayList<ResponseInfo> list;
    private static ArrayList<ResponsePrice> price;
    private static String search;
    public static ArrayList<ResponsePrice> getPrice() {
        return price;
    }
    
    
    public ArrayList<ResponseInfo> getList() {
        return list;
    }

    public void setSearch(String search) {
        ClientHandler.search = search;
    }
   

    public DataPacket getData(){
        return data;
    }
    public ClientHandler(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // Khởi tạo Client socket và gắn kết stream
    public void start() {
        try (Socket socket = new Socket(host, port)) {
            System.out.println("Kết nối đến server " + socket.getRemoteSocketAddress());
            startCommunication(socket);
        } catch (IOException e) {
            System.err.println("Lỗi kết nối đến server: " + e.getMessage());
        }
    }
    
    private void startCommunication(Socket socket) {
        try ( ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
            
            RequestInfo request = new RequestInfo(search);
            oos.writeObject(request);
            oos.flush();
            Object object = ois.readObject();
            System.out.println(object.getClass());
            
            if(object instanceof List<?>){
                List <?> check = (List <?>) object;
                if(!check.isEmpty()){
                    Object first = check.getFirst();
                    if(first instanceof ResponsePrice){
                        System.out.println("Mảng chứa price");
                    } else if(first instanceof ResponseInfo){
                        list = (ArrayList<ResponseInfo>) check;
                        System.out.println("Mảng chứa info");
                    }
                }
                
            }else{
                System.out.println("NOT ARRAY");
            }
            
            
        } catch (Exception e) {
            System.err.println("Lỗi kết nối từ client: " + e.getMessage());
            e.printStackTrace();
        }

    }
    
    public static void main(String[] args)  {

       ClientHandler client = new ClientHandler("localhost", 12345);
       client.start();
       
    }
}
