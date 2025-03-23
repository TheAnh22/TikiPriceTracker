/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.pulldatatiki;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    
    private ArrayList<String> names ;
    private ArrayList<String> origins ;
    private ArrayList<String> ids ;
    
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
    //Khởi tạo connection đến database
    
    //Thêm sản phẩm vào database
    private static void addProductsDataToDB(){
        ArrayList<String> Group_Mechandise_ID= new ArrayList<>();
        
        Connection conn = null;
        int count = 0;
        try {
            conn =DriverManager.getConnection(url, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối đến cơ sở dữ liệu" );
            e.printStackTrace();
        }
        
        try {
            String sql = "SELECT * FROM group_merchandise";
            PreparedStatement prepareStament = conn.prepareStatement(sql);
            ResultSet rs = prepareStament.executeQuery();
            while (rs.next()){
                Group_Mechandise_ID.add(rs.getString("Group_Merchandise_ID"));
            }
            for (int i = 0; i<Group_Mechandise_ID.size();i++){
                count = 0;
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
                                if (array.size()==1){
                                    JsonObject object=array.get(0).getAsJsonObject();
                                    String name = object.get("name").getAsString();
                                    String origin = object.getAsJsonObject("visible_impression_info").getAsJsonObject("amplitude").get("origin").getAsString();
                                    String id = object.get("id").getAsString() ;
                                    String category = Group_Mechandise_ID.get(i);
                                    String sqlInsert = "INSERT IGNORE INTO products (Product_ID, Group_Merchandise_ID, Product_Name, Origin) VALUES (?, ?, ?, ?)";
                                    PreparedStatement stm = conn.prepareStatement(sqlInsert);
                                    stm.setString(1, id);
                                    stm.setString(2, category);
                                    stm.setString(3, name);
                                    stm.setString(4, origin);
                                    stm.executeUpdate();
                                    count=count + 1;
                                } else {
                                    for (int ob = 0;ob<array.size();ob++){
                                    
                                    JsonObject object=array.get(i).getAsJsonObject();
                                    if (
                                            object.has("name") &&
                                            object.getAsJsonObject("visible_impression_info").getAsJsonObject("amplitude").has("origin") &&
                                            object.has("id")
                                       ){
                                            String name = object.get("name").getAsString();
                                            String origin = object.getAsJsonObject("visible_impression_info").getAsJsonObject("amplitude").get("origin").getAsString();
                                            String id = object.get("id").getAsString() ;
                                            String category = Group_Mechandise_ID.get(i);
                                            String sqlInsert = "INSERT IGNORE INTO products (Product_ID, Group_Merchandise_ID, Product_Name, Origin) VALUES (?, ?, ?, ?)";
                                            PreparedStatement stm = conn.prepareStatement(sqlInsert);
                                            stm.setString(1, id);
                                            stm.setString(2, category);
                                            stm.setString(3, name);
                                            stm.setString(4, origin);
                                            stm.executeUpdate();
                                            count=count + 1;
                                        
                                        }
                                    }
                                }    
                        }
                        
                    } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("lỗi");
                        }
                }                 
                System.out.println("Dữ liệu đã lấy:"+count);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(PullDataTiki.class.getName()).log(Level.SEVERE, null, ex);
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
        OkHttpClient client =new OkHttpClient();
        Request requestPhuKienSo = new Request.Builder()
                .url(urlPhuKienSo)
                .get()
                .build();
        Request requestDienThoai = new Request.Builder()
                .url(urlDienThoai)
                .get()
                .build();
        try (   Response responsePhuKienSo = client.newCall(requestPhuKienSo).execute();
                Response responseDienThoai = client.newCall(requestDienThoai).execute();
            ) {
            if (responsePhuKienSo.isSuccessful() && responsePhuKienSo.body()!= null && responseDienThoai.isSuccessful() && responseDienThoai.body()!= null){
                String dataPhuKienSo = responsePhuKienSo.body().string();
                String dataDienThoai = responseDienThoai.body().string();
                Gson gson = new Gson();
                JsonObject jsonObjectPhuKienSo = gson.fromJson(dataPhuKienSo,JsonObject.class);
                JsonObject jsonObjectDienThoai = gson.fromJson(dataDienThoai,JsonObject.class);
                JsonArray arrayPhuKienSo = jsonObjectPhuKienSo.getAsJsonArray("data");
                JsonArray arrayDienThoai = jsonObjectDienThoai.getAsJsonArray("data");
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
                
    }
    //Thêm lịch sử giá vào database
    private void addPriceRecord(){
       
    }
    
    // Xử lý dữ liệu
    private String processData(String input) {
        String name = "";
        String origin = "";
        String id = "";
        String traVe ="";
        int pool=0;
        
        names = new ArrayList<>();
        origins = new ArrayList<>();
        ids = new ArrayList<>();
        
        for (int pages=1;pages<=Integer.parseInt(input);pages++){
            
            String urlItemInfo = "https://tiki.vn/api/personalish/v1/blocks/listings?limit=10&category=2667&page="+Integer.toString(pages);
            OkHttpClient client = new OkHttpClient();
        
            Request requestItemInfo = new Request.Builder()
                .url(urlItemInfo)
                .get()
                .build();
        
        try (
                Response responseItemInfo = client.newCall(requestItemInfo).execute();
            ) {
            if (responseItemInfo.isSuccessful() && responseItemInfo.body() != null) {
                String dataItemInfo = responseItemInfo.body().string();
                System.out.println("+" + dataItemInfo);
                // Parse JSON bằng Gson
                Gson gson = new Gson();
                JsonObject jsonObjectItem = gson.fromJson(dataItemInfo, JsonObject.class);
                JsonArray array = jsonObjectItem.getAsJsonArray("data");

                for (int i=0;i<array.size();i++){
                    JsonObject object=array.get(i).getAsJsonObject();
                    
                    name = object.get("name").getAsString();
                    names.add(name);
                    
                    origin = object.getAsJsonObject("visible_impression_info").getAsJsonObject("amplitude").get("origin").getAsString();
                    origins.add(origin);
                    
                    id = object.get("id").getAsString() ;
                    ids.add(id);
                    
                }
                  
            } else {
                System.out.println("Lỗi");
            }
        } catch (IOException e) {
            System.out.println("ERROR");
            //e.printStackTrace();
        }
           
    }
        for (int u=0;u<names.size();u++){
            traVe = traVe + String.format("\n%-5s%-15s%-30s%-15s",Integer.toString(pool+1),ids.get(u),origins.get(u),names.get(u));
            pool=pool+1;
            
            try {
            Connection connection = DriverManager.getConnection(url, USER, PASSWORD);
            
            String sql = "INSERT IGNORE INTO products (ProductIDs, ProductName, Origin) VALUES (?, ?, ?)";
            PreparedStatement stm = connection.prepareStatement(sql);
            
            stm.setString(1, ids.get(u));
            stm.setString(2,names.get(u));
            stm.setString(3,origins.get(u));
            
            int count =stm.executeUpdate();
                System.out.println(count);
            
            
            } catch (SQLException e){
                e.printStackTrace();
            }
        }

        return traVe;
    }

    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(url, USER, PASSWORD);
            String sql = "SHOW DATABASES LIKE '"+dbname+"'";
            Statement stm= conn.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            if (rs.next()){
                addProductsDataToDB();
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(PullDataTiki.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
