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
import java.util.Scanner;

/**
 *
 * @author The Anh
 */
public class ClientHandler {
     private String host;
    private int port;
    private static final long serialVersionUID = 1L;
    private static ArrayList<Group_Merchandise> list;

    public static ArrayList<Group_Merchandise> getList() {
        return list;
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
            Object receive = ois.readObject();
            if(receive instanceof ArrayList){
                list = (ArrayList<Group_Merchandise>) receive;
                
            }
        } catch (Exception e) {
            System.err.println("Lỗi kết nối từ client: " + e.getMessage());
        }

    }
    
    public static void main(String[] args)  {

       ClientHandler client = new ClientHandler("localhost", 12345);
        client.start();
    }
}
