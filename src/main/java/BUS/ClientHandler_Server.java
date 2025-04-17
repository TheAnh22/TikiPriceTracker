package BUS;

import BUS.Price_Records_BUS;
import BUS.ResponseInfoBUS;
import Objects.RequestInfo;
import Objects.ResponseInfo;
import Objects.ResponsePrice;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.*;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLSocket;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ClientHandler_Server implements Runnable {
    private final Socket socket;
    private ArrayList<ResponseInfo> Info_Array;
    private ArrayList<ResponsePrice> Price_Array;
    private ArrayList<String> reviews;
    private static Price_Records_BUS price_BUS;
    private static ResponseInfoBUS info_BUS;
    public ClientHandler_Server(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())
        ) {
                //Tạo cặp khóa RSA bên phía server
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
                keyGen.initialize(2048);
                KeyPair keyPair = keyGen.generateKeyPair();
                PrivateKey privateKey = keyPair.getPrivate();
                PublicKey publicKey = keyPair.getPublic();
                //Gửi public key cho client
                oos.writeObject(publicKey);
                oos.flush();
                
                // Nhận AES key đã mã hóa và giải mã key  
                byte[] encryptedAesKey = (byte[]) ois.readObject();
                Cipher rsaCipher = Cipher.getInstance("RSA");
                rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAesKey);
                SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");
                //Nhận request đã mã hóa từ client
                byte[] encryptedMessage = (byte[]) ois.readObject();
                //Giải mã AES
                Cipher aesDecrypt = Cipher.getInstance("AES");
                aesDecrypt.init(Cipher.DECRYPT_MODE, aesKey);
                byte[] decrypted = aesDecrypt.doFinal(encryptedMessage);
                // Deserialize lại đối tượng RequestInfo
                ByteArrayInputStream bis = new ByteArrayInputStream(decrypted);
                ObjectInputStream tempOis = new ObjectInputStream(bis);
                RequestInfo request = (RequestInfo) tempOis.readObject();
                String search = request.getText();
                System.out.println("Server nhận từ client: " + search);
                
                    if (search.contains("<SearchRequest>")) {
                        System.out.println(search);
                        search = search.replaceFirst(Pattern.quote("<SearchRequest>") + "$", "");
                        infoSearch(search);
                        ArrayList<ResponseInfo> responseInfo = Info_Array;
                         //Đưa responseInfo từ dạng object thành mảng byte để mã hóa
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        ObjectOutputStream tempOos = new ObjectOutputStream(bos);
                        tempOos.writeObject(responseInfo);
                        tempOos.flush();
                        byte[] serializedResponseInfo = bos.toByteArray();
                        //Gửi responseInfo đã mã hóa từ server
                        Cipher aesCipher = Cipher.getInstance("AES");
                        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
                        byte[] encryptedInfo = aesCipher.doFinal(serializedResponseInfo);
                        // Xuất mã hóa ra màn hình console
                        System.out.println("Mảng byte đã mã hóa:");
                        for (byte b : encryptedInfo) {
                            System.out.print(String.format("%02X ", b));  // In ra giá trị hex của byte
                        }
                        oos.writeObject(encryptedInfo);
                        oos.flush();
                        

                    } 
                    else if (search.contains("<SearchPrice><SearchReviews>")) {
                        System.out.println(search);
                        search = search.replaceFirst(Pattern.quote("<SearchPrice><SearchReviews>") + "$", "");
                        priceSearch(search);
                        review(search);
                        ArrayList<ResponsePrice> responsePrice = Price_Array;
                        ArrayList<String> responseReview = reviews;
                        //Đưa responseInfo từ dạng object thành mảng byte để mã hóa
                        ByteArrayOutputStream bosReview = new ByteArrayOutputStream();
                        ObjectOutputStream tempOosReview = new ObjectOutputStream(bosReview);
                        tempOosReview.writeObject(responseReview);
                        tempOosReview.flush();
                        byte[] serializedResponseReviews = bosReview.toByteArray();
                        
                        ByteArrayOutputStream bosPrice = new ByteArrayOutputStream();
                        ObjectOutputStream tempOosPrice = new ObjectOutputStream(bosPrice);
                        tempOosPrice.writeObject(responsePrice);
                        tempOosPrice.flush();
                        byte[] serializedResponsePrice = bosPrice.toByteArray();
                        
                        //Bắt đầu mã hóa để gửi lại cho client
                        Cipher aesCipher = Cipher.getInstance("AES");
                        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
                        byte[] encryptedPrice = aesCipher.doFinal(serializedResponsePrice);
                        byte[] encryptedReviews = aesCipher.doFinal(serializedResponseReviews);
                        // Xuất mã hóa ra màn hình console
                        System.out.println("Mảng byte đã mã hóa:");
                        for (byte b : encryptedPrice) {
                            System.out.print(String.format("%02X ", b));  // In ra giá trị hex của byte
                        }
                        oos.writeObject(encryptedPrice);
                        oos.flush();
                        oos.writeObject(encryptedReviews);
                        oos.flush();

                    }
                    
                    else {
                        System.out.println("Yêu cầu không hợp lệ.");
                        oos.flush();
                    }
                
      
        } catch (Exception e) {
            System.err.println("Lỗi xử lý client: " + e.getMessage());
            e.printStackTrace();
        } 
//        finally {
//            try {
//                socket.close();
//                System.out.println("Đã đóng kết nối với client.");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public void infoSearch(String search){
        info_BUS =new ResponseInfoBUS();
        Info_Array = info_BUS.getResponse(search);
    }
    public void priceSearch(String id){
        price_BUS = new Price_Records_BUS();
        Price_Array = price_BUS.getPriceRecordByID(id);
    }
    public void review(String id){
        reviews = new ArrayList<>();
        String urlReview = "https://tiki.vn/api/v2/reviews?limit=10&product_id=" + id;
        OkHttpClient client = new OkHttpClient();
       //Tạo đối tượng request 
        Request requestReview = new Request.Builder()
                .url(urlReview)
                .get()
                .build();
        try (Response responseReview = client.newCall(requestReview).execute()) {
            if (responseReview.isSuccessful() && responseReview.body() != null) {
                //lấy chuỗi json từ response
                String jsonDataReview = responseReview.body().string();
                // Parse data từ json thành JsonObject
                Gson gson = new Gson();
                
                JsonObject jsonObjectReview = gson.fromJson(jsonDataReview, JsonObject.class);
                double rating = jsonObjectReview.get("rating_average").getAsDouble();
                String ratingCount = jsonObjectReview.get("reviews_count").getAsString();
                JsonArray array=jsonObjectReview.getAsJsonArray("data");
                if(!(array.isEmpty())){
                    for (int i=0;i<array.size();i++){
                    JsonObject object=array.get(i).getAsJsonObject();
                    reviews.add(object.get("content").getAsString());
                                       
                    }
                } else {
                    reviews.add("KHÔNG CÓ REVIEW");
                    System.out.println("KHÔNG CÓ REVIEW");
                }
                

                  
            } else {
                System.out.println("Lỗi");
            }
        } catch (IOException e) {
            System.out.println("ERROR");
            //e.printStackTrace();
        }
    }
}
