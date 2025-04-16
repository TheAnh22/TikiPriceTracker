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
import java.util.regex.Pattern;

/**
 *
 * @author The Anh
 */
public class ClientHandler {
    private String host;
    private int port;
    private static DataPacket data;
    private static ArrayList<ResponseInfo> list;
    private static ArrayList<String> reviews;
    private static ArrayList<ResponsePrice> price;
    private static String search;
    public ArrayList<ResponsePrice> getPrice() {
        return price;
    }
    
    
    
    public ArrayList<ResponseInfo> getList() {
        return list;
    }
    public ArrayList<String> getReviews(){
        return reviews;
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
                if (search.contains("<SearchRequest>")) {
                    Object objectInfo = ois.readObject();
                    if(objectInfo instanceof List<?>){
                        List <?> check = (List <?>) objectInfo;
                        if(!check.isEmpty()){
                            Object first = check.getFirst();
                            if(first instanceof ResponseInfo){
                                list = (ArrayList<ResponseInfo>) check;
                                System.out.println("Mảng chứa info");
                            }
                        }
                    }
                } else if (search.contains("<SearchPrice><SearchReviews>")) {
                    Object objectPrice = ois.readObject();
                    Object objectReview = ois.readObject();
                        if(objectPrice instanceof List<?>){
                            List <?> check1 = (List <?>) objectPrice;
                            List <?> check2 = (List <?>) objectReview;
                            if(!check1.isEmpty()){
                                Object first = check1.getFirst();
                                if(first instanceof ResponsePrice){
                                    price = (ArrayList<ResponsePrice>) check1;
                                    reviews = (ArrayList<String>) check2;
                                    System.out.println("Mảng chứa price");
                                }
                            }
                        }
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
