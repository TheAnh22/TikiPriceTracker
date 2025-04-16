/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ClientMenu;


import Objects.ClientHandler;
import Objects.DataPacket;
import Objects.Group_Merchandise;
import Objects.ResponseInfo;
import Objects.Picture;
import Objects.Price_Records;
import Objects.Products;
import Objects.ResponsePrice;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import raven.panel.BorderPanel;
import raven.panel.ProductPanel;




/**
 *
 * @author The Anh
 */

public class Main extends javax.swing.JFrame {
    
    final Point clickPoint = new Point();
    private String host;
    private int port;
    
    private static DataPacket data;
    private static String input;
    private int itemsPerPage = 4;
    private int totalProduct = 0;
    private int totalPage = 0;
    private ArrayList<String> items;
    private CardLayout card;
    private FlowLayout flow;
    private JButton next;
    private JButton previous;
    private JPanel btnPanel;
    private JPanel cardPanel;
    private static String search;
    private JFrame loadingFrame;
    private static ClientHandler client;
//    private static ArrayList<Products> Products_Array;
//    private static ArrayList<Price_Records> Price_Records_Array;
//    private static ArrayList<Group_Merchandise> Group_Merchandise_Array;
//    private static ArrayList<Picture> Picture_Array;
    private static ArrayList<ResponseInfo> Info_Array;
    private static ArrayList<ResponsePrice> Price_Array;
    private static ArrayList<String> Reviews;
    
    
    public JPanel createPage (int index) throws MalformedURLException{
        JPanel page = new JPanel(new GridLayout(itemsPerPage, 1));
        
        int firstProductInPage = index * itemsPerPage;
        int lastProductInPage  = Math.min((firstProductInPage + itemsPerPage)-1, totalProduct-1);
        for(int i = firstProductInPage;i<=lastProductInPage;i++){
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
            Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
            panel.setBorder(border);
            
            String imageUrl = Info_Array.get(i).getURL_Image(); // thay bằng URL thật
            if(imageUrl.isBlank()){
                System.out.println("BLANK");
            } else {
                String info ="<html><div style='width:300px;'>"+ Info_Array.get(i).getProdcut_Name()+"</div></html>";
                String originText = "<html><div style='width:100px;'>"+"Nguồn gốc: " +Info_Array.get(i).getOrigin()+"</div></html>";
                String id = Info_Array.get(i).getProduct_ID();
                System.out.println(imageUrl);
                URL url = new URL(imageUrl);
            
                ImageIcon icon = new ImageIcon(url);
            
            panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                // Lấy kích thước hiện tại của panel
                int height = panel.getHeight();
                
                // Resize ảnh sao cho vừa với panel
                Image scaledImage = icon.getImage().getScaledInstance(height-40, height-40, Image.SCALE_SMOOTH);
                icon.setImage(scaledImage);

                // Cập nhật JLabel với ảnh đã thay đổi kích thước
                JLabel label = new JLabel(icon);
                JLabel product_name = new JLabel();
                JLabel origin = new JLabel();
                
                panel.removeAll(); // Xóa tất cả các component cũ
                //Thêm ảnh vào
                panel.add(Box.createHorizontalStrut(10));
                panel.add(label);
                panel.add(Box.createHorizontalStrut(10));
                //Thêm tên sản phẩm vào
                product_name.setText(info);
                product_name.setFont(new Font("Arial", Font.BOLD, 20));
                panel.add(product_name);
                panel.putClientProperty("info", info);
                panel.add(Box.createHorizontalStrut(10));
                //Thêm nguồn gốc vào
                origin.setText(originText);
                origin.setFont(new Font("Arial", Font.BOLD, 20));
                panel.add(origin);
                panel.add(Box.createHorizontalStrut(10));
                //Thêm ID vào
                panel.putClientProperty("id", id);
                panel.revalidate(); // Làm mới layout
                panel.repaint();    // Vẽ lại
                //hiện biểu đồ
                panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // In ra vị trí click
                        
                        System.out.println("Panel được nhấn tại: " + e.getPoint());
                        String pid = (String) panel.getClientProperty("id")+"<SearchPrice><SearchReviews>";
                        String title = (String) panel.getClientProperty("info");
                        System.out.println(pid);
                        client.setSearch(pid);
                        client.start();
                        Price_Array = client.getPrice();
                        Reviews = client.getReviews();
                        CharFrame test = new CharFrame(Price_Array,title,Reviews);
                        test.setVisible(true);
                        test.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        System.out.println(Price_Array.getLast().getPrice_Date());
                    }
                });
            }
        });
            }
            
           
            
            page.add(panel);
            
        }
        return page;
    }
    public Main() throws MalformedURLException, IOException {
        initComponents();
        loadingFrame = new JFrame("Đang tải...");
        loadingFrame.setSize(300, 120);
        loadingFrame.setLocationRelativeTo(null); // Căn giữa màn hình
        loadingFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Không cho đóng
        loadingFrame.setUndecorated(true);

        // Panel chính
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
                    

        // Label thông báo
        JLabel label = new JLabel("Đang tải sản phẩm...");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Arial", Font.PLAIN, 16));

        // Progress bar chạy không xác định thời gian
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressBar.setPreferredSize(new Dimension(250, 20));

        // Thêm vào panel
        contentPanel.add(label);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15))); // khoảng cách
        contentPanel.add(progressBar);

        // Thêm panel vào frame
        loadingFrame.add(contentPanel);

        card = new CardLayout();
        cardPanel = new JPanel(card);
        this.setLayout(null);
        MenuBackround.setLayout(null);
        setBackground(new Color(0,0,0,0));
        this.setSize(1080,720);
        MenuBackround.setSize(1080,720);
        barPanel.setLayout(null);
        barPanel.setLocation(0, 0);
        
        SearchTextBox.setSize(540,30);
        SearchTextBox.setLocation(1080/4,10);
        
        barPanel.setSize(MenuBackround.getWidth(), 50); 
        windowControl.setLocation(barPanel.getWidth() - 50,0);
        windowControl.setSize(50, 50);
//        searchButton.setSize(100, 30);
//        searchButton.setLocation(810, 10);
        
        
        setLocationRelativeTo(null);
        repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MenuBackround = new raven.panel.MenuPanel();
        barPanel = new raven.panel.BarPanel();
        SearchTextBox = new javax.swing.JTextField();
        windowControl = new raven.panel.WindowControlPanel();
        pictureLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(153, 255, 51));
        setUndecorated(true);

        MenuBackround.setBackground(new java.awt.Color(255, 153, 153));
        MenuBackround.setOpaque(false);

        barPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                barPanelMouseDragged(evt);
            }
        });
        barPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                barPanelMousePressed(evt);
            }
        });

        SearchTextBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchTextBoxActionPerformed(evt);
            }
        });

        windowControl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                windowControlMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout barPanelLayout = new javax.swing.GroupLayout(barPanel);
        barPanel.setLayout(barPanelLayout);
        barPanelLayout.setHorizontalGroup(
            barPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, barPanelLayout.createSequentialGroup()
                .addGap(158, 158, 158)
                .addComponent(SearchTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(windowControl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        barPanelLayout.setVerticalGroup(
            barPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(barPanelLayout.createSequentialGroup()
                .addComponent(windowControl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(barPanelLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(SearchTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout MenuBackroundLayout = new javax.swing.GroupLayout(MenuBackround);
        MenuBackround.setLayout(MenuBackroundLayout);
        MenuBackroundLayout.setHorizontalGroup(
            MenuBackroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(barPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(MenuBackroundLayout.createSequentialGroup()
                .addGap(566, 566, 566)
                .addComponent(pictureLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        MenuBackroundLayout.setVerticalGroup(
            MenuBackroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MenuBackroundLayout.createSequentialGroup()
                .addComponent(barPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(122, 122, 122)
                .addComponent(pictureLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(265, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MenuBackround, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MenuBackround, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void barPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_barPanelMousePressed
        // TODO add your handling code here:

        clickPoint.x = evt.getX();
        clickPoint.y = evt.getY();
    }//GEN-LAST:event_barPanelMousePressed

    private void barPanelMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_barPanelMouseDragged
        // TODO add your handling code here:
        Point currentLocation = evt.getLocationOnScreen();
        this.setLocation(currentLocation.x-clickPoint.x,currentLocation.y-clickPoint.y);
    }//GEN-LAST:event_barPanelMouseDragged

    private void windowControlMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_windowControlMouseClicked
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_windowControlMouseClicked

    private void SearchTextBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchTextBoxActionPerformed
        // TODO add your handling code here:
        
        SearchTextBox.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    cardPanel.removeAll();
                    search = SearchTextBox.getText() + "<SearchRequest>";

                    loadingFrame.setVisible(true);
                          
                    cardPanel.setLocation(10, 60);
                    cardPanel.setSize(1060, 590);     
                    MenuBackround.add(cardPanel);
                    MenuBackround.revalidate();
                    MenuBackround.repaint();
                    SwingWorker<Void, Void> worker = new SwingWorker<>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            client.setSearch(search);
                            client.start();
                            Info_Array = client.getList();
                            totalProduct =  Info_Array.size();
                            totalPage=(int)Math.ceil((double)totalProduct/itemsPerPage);
                            return null;
                        }

                        @Override
                        protected void done() {
                            cardPanel.removeAll();
                            
                            for(int i= 0; i<totalPage;i++){
                            try {
                                JPanel page = new JPanel();
                                page = createPage(i);
                                cardPanel.add(page, "page"+i);
                            } catch (MalformedURLException ex) {
                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            btnPanel = new JPanel();
                            previous = new JButton("← Trước");
                            next = new JButton("Tiếp →");

                            previous.addActionListener(ex -> card.previous(cardPanel));
                            next.addActionListener(ex -> card.next(cardPanel));
                            btnPanel.add(previous);
                            btnPanel.add(next);
                            MenuBackround.add(btnPanel);
                            btnPanel.setLocation(270, 660);
                            btnPanel.setSize(540, 50);

                            // Hiển thị panel đầu tiên
                            card.show(cardPanel, "page0");
                            cardPanel.revalidate();
                            cardPanel.repaint();
                            loadingFrame.dispose();
                        }  
                    };
                    worker.execute();
                
                
                }
            }
        });
    }//GEN-LAST:event_SearchTextBoxActionPerformed

    
   
    /**
     * @param args the command line arguments
     */
    public static void main(String args[])  {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        client =new ClientHandler("localhost", 12345);
                
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
               
                try {
                    new Main().setVisible(true);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
   
            }
        });
    }

   
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private raven.panel.MenuPanel MenuBackround;
    private javax.swing.JTextField SearchTextBox;
    private raven.panel.BarPanel barPanel;
    private javax.swing.JLabel pictureLabel;
    private raven.panel.WindowControlPanel windowControl;
    // End of variables declaration//GEN-END:variables
}
