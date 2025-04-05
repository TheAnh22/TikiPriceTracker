/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package BUS;

import Objects.Products;
import Objects.Group_Merchandise;
import Objects.Price_Records;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author The Anh
 */


public class PullDataTiki {

    private int port;
    private static final String url="jdbc:mysql://localhost:3306/tiki";
    private static final String USER = "root";
    private static final String PASSWORD = "Atheanh123";
    private static final String dbname = "tiki";
    
   
    private static ArrayList<String> Group_Mechandise_ID;
    private static ArrayList<Products> Products;
    private static ArrayList<Price_Records> Price_Records;
    private static ArrayList<Group_Merchandise> Group_Merchandise;
    public PullDataTiki(int port) {
        this.port = port;
    }

    // Khởi tạo server, tạo đối tượng socket tương ứng từng client
    public void start() {

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
    private void handleClient(Socket socket) {
        System.out.println("Đã chấp nhận kết nối từ client: " + socket.getRemoteSocketAddress());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {
            String dataFromClient;
            while ((dataFromClient = reader.readLine()) != null) {
                System.out.println("Server nhận: " + dataFromClient);
                if (dataFromClient.equalsIgnoreCase("bye")) {
                    System.out.println("Server nhận yêu cầu đóng kết nối từ client.");
                    break;
                }
                String response = processData(dataFromClient);
                writer.println(response);
                writer.println("<END>"); // Báo client biết đã kết thúc gửi dữ liệu.
            }
        } catch (IOException e) {
            System.err.println("Lỗi kết nối từ client: " + e.getMessage());
        }
    }

    private static void loadData(){
        Products = new ArrayList<>();
        Group_Merchandise = new ArrayList<>();
        Price_Records = new ArrayList<>();
        Group_Mechandise_ID = new ArrayList<>();
        try {
            Connection conn = null;
            conn = DriverManager.getConnection(url, USER, PASSWORD);
            String sqlSelectProduct = "SELECT * FROM products";
            String sqlSelectGroupMerchandise = "SELECT * FROM group_merchandise";
            String sqlSelectPriceRecord = "SELECT * FROM price_record";
            
            PreparedStatement stmSelectProduct = conn.prepareStatement(sqlSelectProduct);
            PreparedStatement stmSelectGroupMerchandise = conn.prepareStatement(sqlSelectGroupMerchandise);
            PreparedStatement stmSelectPriceRecord = conn.prepareStatement(sqlSelectPriceRecord);
            
            ResultSet rsSelectProducts=stmSelectProduct.executeQuery();
            ResultSet rsSelectGroupMerchandise=stmSelectGroupMerchandise.executeQuery();
            ResultSet rsSelectPriceRecord=stmSelectPriceRecord.executeQuery();
            while (rsSelectProducts.next()){
                String Product_ID = rsSelectProducts.getString("Product_ID");
                String Group_Merchandise_ID = rsSelectProducts.getString("Group_Merchandise_ID");
                String Product_Name = rsSelectProducts.getString("Product_Name");
                String Origin = rsSelectProducts.getString("Origin");
                
                Products product= new Products(Product_ID,Group_Merchandise_ID,Product_Name,Origin);
                Products.add(product);
                
            }
            while (rsSelectGroupMerchandise.next()){
                String Group_Merchandise_ID = rsSelectGroupMerchandise.getString("Group_Merchandise_ID");
                String Merchandise_Name = rsSelectGroupMerchandise.getString("Merchandise_Name");
                
                Group_Merchandise group_merchandise= new Group_Merchandise(Group_Merchandise_ID,Merchandise_Name);
                Group_Merchandise.add(group_merchandise);
                Group_Mechandise_ID.add(Group_Merchandise_ID);
            }
            while (rsSelectPriceRecord.next()){
                int Product_Price_ID = rsSelectPriceRecord.getInt("Product_Price_ID");
                String Product_ID = rsSelectPriceRecord.getString("Product_ID");
                String Price = rsSelectPriceRecord.getString("Price");
                Date Price_Date = rsSelectPriceRecord.getDate("Price_Date");
                
                Price_Records price_record = new Price_Records(Product_Price_ID,Product_ID,Price,Price_Date);
                Price_Records.add(price_record);
            }
            
        } catch (SQLException ex) {
                ex.printStackTrace();
        }
    }
    //Thêm giá vào database
    private static void addPriceRecordToDB() throws SQLException{
        Connection conn = null;
        conn = DriverManager.getConnection(url, USER, PASSWORD);
        ExecutorService exService = Executors.newFixedThreadPool(10);
        Date currentDate = Date.valueOf(LocalDate.now());
        
        
                   try {
            conn = DriverManager.getConnection(url, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối đến cơ sở dữ liệu" );
            e.printStackTrace();
        }
        for (int i=0;i<Products.size();i++){
            callAPI call=new callAPI(Products.get(i).getProductID(), conn, currentDate);
            exService.execute(call);
        }
        exService.shutdown();
        try {
            if(!exService.awaitTermination(60, TimeUnit.SECONDS)){
                exService.shutdownNow();
            }
        } catch (Exception e) {
            exService.shutdownNow();
            
        }
        System.out.println("ALL DONE");

    }
    //Thêm sản phẩm vào database    //Thêm sản phẩm vào database
    private static void addProductsDataToDB(){
        Group_Mechandise_ID= new ArrayList<>();
        Products_BUS products_bus = new Products_BUS();
        Connection conn = null;
        
        try {
            conn =DriverManager.getConnection(url, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối đến cơ sở dữ liệu" );
            e.printStackTrace();
        }
        
        for (int i = 0; i<Group_Mechandise_ID.size();i++){
            
            for (int page = 1; page<=10 ; page++){
                String urlProduct = "https://tiki.vn/api/personalish/v1/blocks/listings?limit=40&category="+ Group_Mechandise_ID.get(i) +"&page="+Integer.toString(page);
                System.out.println(urlProduct);
                Request requestProduct = new Request.Builder()
                        .url(urlProduct)
                        .get()
                        .build();
                OkHttpClient client = new OkHttpClient();
                
                try {
                    Response responseProduct = client.newCall(requestProduct).execute();
                    if (responseProduct.isSuccessful() && responseProduct.body() != null) {
                        String dataProduct = responseProduct.body().string();
                        // Parse JSON bằng Gson
                        Gson gson = new Gson();
                        JsonObject jsonObjectProduct = gson.fromJson(dataProduct, JsonObject.class);
                        JsonArray array = jsonObjectProduct.getAsJsonArray("data");
                        System.out.println(array.size());
                        
                        
                        for (int ob = 0;ob<array.size();ob++){
                            
                            JsonObject object=array.get(ob).getAsJsonObject();
                            if (
                                    object.has("name") &&
                                    object.getAsJsonObject("visible_impression_info").getAsJsonObject("amplitude").has("origin") &&
                                    object.has("id") &&
                                    object.has("price")
                                    ){
                                String name = object.get("name").getAsString();
                                String origin = object.getAsJsonObject("visible_impression_info").getAsJsonObject("amplitude").get("origin").getAsString();
                                String id = object.get("id").getAsString() ;
                                String category = Group_Mechandise_ID.get(i);
                                String price = object.get("price").getAsString();
                                Products product = new Products(id, category, name, origin);
                                products_bus.addProducts(product);
//                                            
//                                            String sqlInsertProduct = "INSERT IGNORE INTO products (Product_ID, Group_Merchandise_ID, Product_Name, Origin) VALUES (?, ?, ?, ?)";
//                                            PreparedStatement stm = conn.prepareStatement(sqlInsertProduct);
//                                            stm.setString(1, id);
//                                            stm.setString(2, category);
//                                            stm.setString(3, name);
//                                            stm.setString(4, origin);
//                                            stm.executeUpdate();
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
    //Thêm các loại sản phẩm vào database
    private static void addGroupMerchandiseToDB(){
        Connection conn = null;
        try {
            conn =DriverManager.getConnection(url, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối đến cơ sở dữ liệu" );
            e.printStackTrace();
        }
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
                    System.out.println(id + name);
                    String sql = "INSERT IGNORE INTO group_merchandise (Group_Merchandise_ID, Merchandise_Name) VALUES (?, ?)";
                    PreparedStatement preStatement = conn.prepareStatement(sql);
                    preStatement.setString(1,id);
                    preStatement.setString(2,name);
                    preStatement.executeUpdate();
                    
                }
                for (int i = 0; i<arrayDienThoai.size(); i++){
                    JsonObject object = arrayDienThoai.get(i).getAsJsonObject();
                    id = object.get("id").getAsString();
                    name = object.get("name").getAsString();
                    System.out.println(id + name);
                    String sql = "INSERT IGNORE INTO group_merchandise (Group_Merchandise_ID, Merchandise_Name) VALUES (?, ?)";
                    PreparedStatement preStatement = conn.prepareStatement(sql);
                    preStatement.setString(1,id);
                    preStatement.setString(2,name);
                    preStatement.executeUpdate();
                    
                }
                for (int i = 0; i<arrayLaptop.size(); i++){
                    JsonObject object = arrayLaptop.get(i).getAsJsonObject();
                    id = object.get("id").getAsString();
                    name = object.get("name").getAsString();
                    System.out.println(id + name);
                    String sql = "INSERT IGNORE INTO group_merchandise (Group_Merchandise_ID, Merchandise_Name) VALUES (?, ?)";
                    PreparedStatement preStatement = conn.prepareStatement(sql);
                    preStatement.setString(1,id);
                    preStatement.setString(2,name);
                    preStatement.executeUpdate();
                    
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
                
    }
    
    // Xử lý dữ liệu
    private String processData(String input) {
        return "";
    }

    public static void main(String[] args) throws SQLException {
      
       Date currentDate = Date.valueOf(LocalDate.now());
        System.out.println(currentDate);
        loadData();
        if(Products.isEmpty() && Price_Records.isEmpty() && Group_Merchandise.isEmpty()&&Group_Mechandise_ID.isEmpty()){
            System.out.println("EMPTY");
        }else{
            System.out.println("SUCCESS");
            try {
                addPriceRecordToDB();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static class callAPI implements Runnable{
        private  String id;
        private  Connection conn;
        private  Date currentDate;
        public callAPI(String id,Connection conn,Date currentDate){
        this.id=id;
        this.conn=conn;
        this.currentDate=currentDate;
    }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "bắt đầu. ID:" + id);
            String urlProduct = "https://tiki.vn/api/v2/products/"+ id;
            System.out.println(urlProduct);
            Request requestProduct = new Request.Builder()
                    .url(urlProduct)
                    .get()
                    .build();
            OkHttpClient client = new OkHttpClient();
            try {
                Response responseProduct = client.newCall(requestProduct).execute();
                if (responseProduct.isSuccessful() && responseProduct.body() != null){
                    String dataProduct = responseProduct.body().string();
                    JsonElement element = JsonParser.parseString(dataProduct);
                    if (element.isJsonObject()) {
                        Gson gson = new Gson();
                        JsonObject jsonObjectProduct = gson.fromJson(dataProduct, JsonObject.class);
                            String price = jsonObjectProduct.get("price").getAsString();
                            String sqlInsertPriceRecord = """
                                                      INSERT INTO price_record (Product_ID, Price, Price_Date)
                                                      SELECT ?, ?, ?
                                                     WHERE NOT EXISTS (
                                                              SELECT * FROM price_record WHERE Price_Date = ? AND Product_ID = ?
                                                          )
                                                      """;
                            PreparedStatement stm = conn.prepareStatement(sqlInsertPriceRecord);
                            stm.setString(1, id);
                            stm.setString(2, price);
                            stm.setDate(3, currentDate);
                            stm.setDate(4, currentDate);
                            stm.setString(5, id);
                            stm.executeUpdate();
                    } else {
                        
                    }
                    
                }
            } catch (Exception e) {
            }
        }
    }
}
