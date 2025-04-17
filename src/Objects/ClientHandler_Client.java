/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author The Anh
 */
public class ClientHandler_Client {
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
        ClientHandler_Client.search = search;
    }
   

    public DataPacket getData(){
        return data;
    }
    public ClientHandler_Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // Khởi tạo Client socket và gắn kết stream
    public void start() {
           
        try (Socket socket = new Socket(host,port)) {
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
                // Nhận RSA public key từ server
                PublicKey serverPublicKey = (PublicKey) ois.readObject();
                // Tạo AES key
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(128);
                SecretKey aesKey = keyGen.generateKey();
                // Mã hóa AES key và gửi sang server
                Cipher rsaCipher = Cipher.getInstance("RSA");
                rsaCipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
                byte[] encryptedAesKey = rsaCipher.doFinal(aesKey.getEncoded());
                oos.writeObject(encryptedAesKey);
                oos.flush();
                //Đưa request từ dạng object thành mảng byte để mã hóa
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream tempOos = new ObjectOutputStream(bos);
                tempOos.writeObject(request);
                tempOos.flush();
                byte[] serializedRequest = bos.toByteArray();
                //Gửi request đã mã hóa cho server
                Cipher aesCipher = Cipher.getInstance("AES");
                aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
                byte[] encryptedMessage = aesCipher.doFinal(serializedRequest);
                oos.writeObject(encryptedMessage);
                oos.flush();
                
//                oos.writeObject(request);
//                oos.flush();
                //Nhận phản hồi từ server
                if (search.contains("<SearchRequest>")) {
                    //Giải mã AES
                    byte[] encryptedInfo = (byte[]) ois.readObject();
                    Cipher aesDecrypt = Cipher.getInstance("AES");
                    aesDecrypt.init(Cipher.DECRYPT_MODE, aesKey);
                    byte[] decrypted = aesDecrypt.doFinal(encryptedInfo);
                    // Deserialize lại đối tượng
                    ByteArrayInputStream bis = new ByteArrayInputStream(decrypted);
                    ObjectInputStream tempOis = new ObjectInputStream(bis);
                    ArrayList<ResponseInfo> arrayInfo  = (ArrayList<ResponseInfo>) tempOis.readObject();
                    if(!arrayInfo.isEmpty()){
                        Object first = arrayInfo.getFirst();
                        if(first instanceof ResponseInfo){
                            list = (ArrayList<ResponseInfo>) arrayInfo;
                            System.out.println("Mảng chứa info");
                        }
                    }
                    
                } else if (search.contains("<SearchPrice><SearchReviews>")) {
                    //Giải mã AES
                    byte[] encryptedPrice = (byte[]) ois.readObject();
                    byte[] encryptedReviews = (byte[]) ois.readObject();
                    Cipher aesDecrypt = Cipher.getInstance("AES");
                    aesDecrypt.init(Cipher.DECRYPT_MODE, aesKey);
                    byte[] decryptedPrice = aesDecrypt.doFinal(encryptedPrice);
                    byte[] decryptedReviews = aesDecrypt.doFinal(encryptedReviews);
                    // Deserialize lại đối tượng
                    ByteArrayInputStream bisPrice = new ByteArrayInputStream(decryptedPrice);
                    ObjectInputStream tempOisPrice = new ObjectInputStream(bisPrice);
                    ArrayList<ResponsePrice> arrayPrice  = (ArrayList<ResponsePrice>) tempOisPrice.readObject();
                    
                    ByteArrayInputStream bisReviews = new ByteArrayInputStream(decryptedReviews);
                    ObjectInputStream tempOisReviews = new ObjectInputStream(bisReviews);
                    ArrayList<String> arrayReviwes  = (ArrayList<String>) tempOisReviews.readObject();
                    
                    if(!arrayPrice.isEmpty()){
                        Object first = arrayPrice.getFirst();
                        if(first instanceof ResponsePrice){
                            price = (ArrayList<ResponsePrice>) arrayPrice ;
                            reviews = (ArrayList<String>) arrayReviwes;
                            System.out.println("Mảng chứa price");
                        }
                    }
                       
                }

        } catch (Exception e) {
            System.err.println("Lỗi kết nối từ client: " + e.getMessage());
            e.printStackTrace();
        }

    }
    
    public static void main(String[] args)  {

       ClientHandler_Client client = new ClientHandler_Client("26.213.228.78", 12345);
       client.start();
       
    }
}
