/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package BUS;

import Objects.DataPacket;
import Objects.Products;
import Objects.Price_Records;
import Objects.Group_Merchandise;
import Objects.ResponseInfo;
import Objects.Picture;
import Objects.Price_Records;
import Objects.RequestInfo;
import Objects.ResponsePrice;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;


/**
 *
 * @author The Anh
 */


public class PullDataTiki {
    
    
    private static int port=12345;
    
    
    private static ArrayList<String> Group_Mechandise_ID_Array;
    private static ArrayList<String> Products_ID_Array;
    private static ArrayList<Products> Products_Array;
    private static ArrayList<Price_Records> Price_Records_Array;
    private static ArrayList<Group_Merchandise> Group_Merchandise_Array;
    private static ArrayList<Picture> Picture_Array;
    private static ArrayList<ResponseInfo> Info_Array;
    private static ArrayList<ResponsePrice> Price_Array;
    private static Group_Merchandise_BUS group_merchandise_BUS;
    private static Products_BUS products_bus;
    private static Price_Records_BUS price_records_bus;
    private static Picture_BUS picture_bus;
    private static ResponseInfoBUS infoBUS;
    private static DataPacket data;
    private static String search;
    
    public PullDataTiki(int port) {
        this.port = port;
    }

     
    // Khởi tạo server, tạo đối tượng socket tương ứng từng client
    public void start() throws ClassNotFoundException {

        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                System.out.println("Server đang lắng nghe tại port " + port);
                Socket socket = server.accept();
                handleClient(socket);
            }
        } catch (IOException e) {
            System.err.println("Lỗi khởi tạo server socket: " + e.getMessage());
        }
    }

    // Xử lý khi có client kết nối
    private void handleClient(Socket socket) throws ClassNotFoundException {
        System.out.println("Đã chấp nhận kết nối từ client: " + socket.getRemoteSocketAddress());
        try ( ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
                RequestInfo request = (RequestInfo) ois.readObject();
                System.out.println("Server receive: "+request.getText());
                
                search = request.getText();
                if(search.contains("<SearchRequest>")){
                    search = search.replaceFirst(Pattern.quote("<SearchRequest>")+"$", "");
                    infoSearch(search);
                    ArrayList<ResponseInfo> responseInfo = Info_Array;
                    oos.writeObject(responseInfo);
                } else if(search.contains("<SearchPrice>")){
                    search = search.replaceFirst(Pattern.quote("<SearchPrice>")+"$", "");
                    priceSearch(search);
                    System.out.println(search);
                    ArrayList<ResponsePrice> responsePrice = Price_Array;
                    System.out.println(Price_Array.getFirst().getProduct_ID());
                    oos.writeObject(responsePrice);
                } else {
                    System.out.println("NONE");
                }
                
                
                
                
//                oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("Lỗi kết nối từ client: " + e.getMessage());
        }
    }

    private static void loadData(){
        
        try {
            Group_Merchandise_Array = group_merchandise_BUS.getAllGroupMerchandise();
            Products_Array = products_bus.getAllProduct();
            Price_Records_Array=price_records_bus.getAllPriceRecord();
            Picture_Array = picture_bus.getAllPicture();
            data = new DataPacket(Products_Array, Price_Records_Array, Group_Merchandise_Array, Picture_Array);
            for (Group_Merchandise ob:Group_Merchandise_Array){
                Group_Mechandise_ID_Array.add(ob.getGroup_Merchandise_ID());
            }
            
        } catch (Exception ex) {
                ex.printStackTrace();
        }
    }
    
    
    //Thêm giá vào database
    private static void addPriceRecordToDB() throws SQLException{
        Date currentDate = Date.valueOf(LocalDate.now());
        
        OkHttpClient client = new OkHttpClient(); 
        Gson gson = new Gson();
        String data;
        JsonObject object;
        JsonArray array;
        try{
            for (int i = 0; i < Group_Mechandise_ID_Array.size(); i++) {
                for(int page = 1; page<=100;page++){
                        String url = "https://tiki.vn/api/personalish/v1/blocks/listings?limit=40&category="+ Group_Mechandise_ID_Array.get(i) +"&page="+Integer.toString(page);
                        Request requestPriceRecord= new Request.Builder().url(url).get().build();
                        System.out.println(url);
                        try(Response responsePriceRecord = client.newCall(requestPriceRecord).execute()){
                            data=responsePriceRecord.body().string();
                            object = gson.fromJson(data, JsonObject.class);
                            array = object.getAsJsonArray("data");
                            if(array.isEmpty()){
                                System.out.println("BREAK");
                                break;
                            } else {
                                for (int j = 0;j<array.size();j++){
                                JsonObject ob = array.get(j).getAsJsonObject();
                                if(ob.has("id")&&ob.has("price")){
                                    
                                    String product_id = ob.get("id").getAsString();
                                    String price = ob.get("price").getAsString();
                                    Date date = currentDate;
                                    Price_Records price_record = new Price_Records(product_id,price,date);
                                    Boolean check = false;
                                    for(Price_Records record : Price_Records_Array){
                                        if(record.getPrice_Date().equals(currentDate)){
                                            check = true;
                                        } else {
                                            check = false;
                                        }
                                    }
                                    
                                    if( check == false){
                                        price_records_bus.addPriceRecords(price_record);
                                    }
                                } else {
                                    System.out.println("NONE");
                                }
                            }
                            }
                            
                        }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private static void addPicture(){
        try{
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();
            String data;
            JsonObject object;
            JsonArray array;
            
            
            for (String ID : Group_Mechandise_ID_Array){
                for(int page =1 ;page<=100;page++){
                    String urlProduct = "https://tiki.vn/api/personalish/v1/blocks/listings?limit=40&category="+ ID +"&page="+Integer.toString(page);
                    System.out.println(urlProduct);
                    Request request = new Request.Builder().url(urlProduct).get().build();
                    try(Response response = client.newCall(request).execute()){
                        if(response.isSuccessful() && response.body()!=null){
                            data = response.body().string();
                            object = gson.fromJson(data, JsonObject.class);
                            array=object.getAsJsonArray("data");
                            if(array.isEmpty()){
                                System.out.println("BREAK");
                                break;
                            } else {
                                for(int j = 0; j<array.size();j++){
                                    JsonObject ob = array.get(j).getAsJsonObject();
                                    
                                    if (
                                            ob.has("id") &&
                                            ob.has("thumbnail_url")
                                            
                                        ){
                                            String id=ob.get("id").getAsString();
                                            String urlImage = ob.get("thumbnail_url").getAsString();
                                            picture_bus.addPicture(id, urlImage);
                                            System.out.println("ID: "+id+"URL:"+urlImage);
                                            
                                         } else{
                                        System.out.println("ERROR");
                                    }
                                }
                            }
                        }
                    }
                }
            } 
        }catch (Exception e){
            System.out.println("Lỗi khi parse Json: "+e.getMessage());
        }
    }
     //Thêm sản phẩm vào database
    private static void addProductsDataToDB(){
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();
        String data;
        JsonObject object;
        JsonArray array;
        for (int i = 0; i<Group_Mechandise_ID_Array.size();i++){
            
            for (int page = 1; page<=100 ; page++){
                String urlProduct = "https://tiki.vn/api/personalish/v1/blocks/listings?limit=40&category="+ Group_Mechandise_ID_Array.get(i) +"&page="+Integer.toString(page);
                
                Request requestProduct = new Request.Builder()
                        .url(urlProduct)
                        .get()
                        .build();
                
                
                try {
                    Response responseProduct = client.newCall(requestProduct).execute();
                    if (responseProduct.isSuccessful() && responseProduct.body() != null) {
                        data = responseProduct.body().string();
                        // Parse JSON bằng Gson
                       
                        JsonObject jsonObjectProduct = gson.fromJson(data, JsonObject.class);
                        array = jsonObjectProduct.getAsJsonArray("data");
                        
                        
                        
                        for (int j = 0;j<array.size();j++){
                            
                            JsonObject ob=array.get(j).getAsJsonObject();
                            if (
                                    ob.has("name") &&
                                    ob.getAsJsonObject("visible_impression_info").getAsJsonObject("amplitude").has("origin") &&
                                    ob.has("id") &&
                                    ob.has("price")
                                    ){
                                String name = ob.get("name").getAsString();
                                String origin = ob.getAsJsonObject("visible_impression_info").getAsJsonObject("amplitude").get("origin").getAsString();
                                String id = ob.get("id").getAsString() ;
                                String category = Group_Mechandise_ID_Array.get(i);
                                String price = ob.get("price").getAsString();
                                Products product = new Products(id, category, name, origin);
                                products_bus.addProducts(product);
                            }
                        }
                        
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("lỗi");
                }
            }
            
        }
        
    }
    private static void infoSearch(String search){
        infoBUS =new ResponseInfoBUS();
        Info_Array = infoBUS.getResponse(search);
    }
    private static void priceSearch(String id){
        price_records_bus = new Price_Records_BUS();
        Price_Array = price_records_bus.getPriceRecordByID(id);
    }
    //Thêm các loại sản phẩm vào database
    private static void addGroupMerchandiseToDB(){
        
        String id = "";
        String name = "";
        String urlPhuKienSo = "https://tiki.vn/api/v2/categories?parent_id=1815";
        String urlDienThoai = "https://tiki.vn/api/v2/categories?parent_id=1789";
        String urlLaptop = "https://tiki.vn/api/v2/categories?parent_id=1846";
        OkHttpClient client =new OkHttpClient();
        Request requestPhuKienSo = new Request.Builder()
                .url(urlPhuKienSo)
                .get()
                .build();
        Request requestDienThoai = new Request.Builder()
                .url(urlDienThoai)
                .get()
                .build();
        Request requestLaptop = new Request.Builder()
                .url(urlLaptop)
                .get()
                .build();
        try (   Response responsePhuKienSo = client.newCall(requestPhuKienSo).execute();
                Response responseDienThoai = client.newCall(requestDienThoai).execute();
                Response responseLaptop = client.newCall(requestLaptop).execute();
            ) {
            if (responsePhuKienSo.isSuccessful() && responsePhuKienSo.body()!= null && responseDienThoai.isSuccessful() && responseDienThoai.body()!= null && responseLaptop.isSuccessful() && responseLaptop.body()!= null){
                String dataPhuKienSo = responsePhuKienSo.body().string();
                String dataDienThoai = responseDienThoai.body().string();
                String dataLaptop = responseLaptop.body().string();
                Gson gson = new Gson();
                JsonObject jsonObjectPhuKienSo = gson.fromJson(dataPhuKienSo,JsonObject.class);
                JsonObject jsonObjectDienThoai = gson.fromJson(dataDienThoai,JsonObject.class);
                JsonObject jsonObjectLaptop = gson.fromJson(dataLaptop, JsonObject.class);
                JsonArray arrayPhuKienSo = jsonObjectPhuKienSo.getAsJsonArray("data");
                JsonArray arrayDienThoai = jsonObjectDienThoai.getAsJsonArray("data");
                JsonArray arrayLaptop = jsonObjectLaptop.getAsJsonArray("data");
                for (int i = 0; i<arrayPhuKienSo.size(); i++){
                    JsonObject object = arrayPhuKienSo.get(i).getAsJsonObject();
                    id = object.get("id").getAsString();
                    name = object.get("name").getAsString();
                    group_merchandise_BUS.addGroupMerchandise(id, name);
                    
                }
                for (int i = 0; i<arrayDienThoai.size(); i++){
                    JsonObject object = arrayDienThoai.get(i).getAsJsonObject();
                    id = object.get("id").getAsString();
                    name = object.get("name").getAsString();
                    group_merchandise_BUS.addGroupMerchandise(id, name);
                }
                for (int i = 0; i<arrayLaptop.size(); i++){
                    JsonObject object = arrayLaptop.get(i).getAsJsonObject();
                    id = object.get("id").getAsString();
                    name = object.get("name").getAsString();
                    group_merchandise_BUS.addGroupMerchandise(id, name);    
                }
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
        
                
    }
    
//    // Xử lý dữ liệu
    private String processData(String input) {
        return "";
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        
        group_merchandise_BUS = new Group_Merchandise_BUS();
        products_bus = new Products_BUS();
        price_records_bus =new Price_Records_BUS();
        picture_bus=new Picture_BUS();
        Group_Mechandise_ID_Array = new ArrayList<>();
        Products_ID_Array = new ArrayList<>();
        Products_Array = new ArrayList<>();
        Price_Records_Array = new ArrayList<>();
        Group_Merchandise_Array = new ArrayList<>();
        Picture_Array =new ArrayList<>();
        Price_Array = new ArrayList<>();
        data = new DataPacket();
        loadData();
        
        
//        addPicture();
//        addPriceRecordToDB();
        
        ExecutorService pool = Executors.newFixedThreadPool(10);
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {                
                System.out.println("Server đang lắng nghe tại cổng " + port);
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SERVER] Kết nối từ " + clientSocket.getInetAddress().getHostAddress());
                ClientHandler handler = new ClientHandler(clientSocket);
                pool.execute(handler);
            }
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
   
}
 
    
    

