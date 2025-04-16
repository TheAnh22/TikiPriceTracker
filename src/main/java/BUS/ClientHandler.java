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
import java.util.ArrayList;
import java.util.regex.Pattern;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private ArrayList<ResponseInfo> Info_Array;
    private ArrayList<ResponsePrice> Price_Array;
    private ArrayList<String> reviews;
    private static Price_Records_BUS price_BUS;
    private static ResponseInfoBUS info_BUS;
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())
        ) {
            
            
                RequestInfo request = (RequestInfo) ois.readObject();
                String search = request.getText();
                System.out.println("Server nhận từ client: " + search);
                
                    if (search.contains("<SearchRequest>")) {
                        System.out.println(search);
                        search = search.replaceFirst(Pattern.quote("<SearchRequest>") + "$", "");
                        infoSearch(search);
                        ArrayList<ResponseInfo> responseInfo = Info_Array;
                        oos.writeObject(responseInfo);
                        oos.flush();

                    } else if (search.contains("<SearchPrice><SearchReviews>")) {
                        System.out.println(search);
                        search = search.replaceFirst(Pattern.quote("<SearchPrice><SearchReviews>") + "$", "");
                        priceSearch(search);
                        review(search);
                        ArrayList<ResponsePrice> responsePrice = Price_Array;
                        System.out.println(responsePrice.get(0).getPrice_Date());
                        ArrayList<String> responseReview = reviews;
                        System.out.println(responseReview.get(0));
                        oos.writeObject(responsePrice);
                        oos.writeObject(responseReview);
                        oos.flush();

                    } else {
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
