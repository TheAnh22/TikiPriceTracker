package ClientMenu;

import Objects.ResponsePrice;
import java.awt.Color;
import java.awt.Font;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import raven.chart.ModelChart;

/**
 *
 * @author RAVEN
 */
public class CharFrame extends javax.swing.JFrame {
   private ArrayList<ResponsePrice> Price_Array;
   private String title;
   private ArrayList<String> reviews;

    
   
    /**
     * Creates new form Test
     */
    public CharFrame(ArrayList<ResponsePrice> Price_Array, String title,ArrayList<String> reviews) {
        this.Price_Array = Price_Array;
        this.reviews = reviews;
        title = "<html><div style='width:200px;'>"+title+"</div></html>";
        this.title = title;
        
        initComponents();
        this.setSize(1080,720);
        this.setLocationRelativeTo(null);
        JPanel reviewSection = new JPanel();
        reviewSection.setOpaque(false);
        reviewSection.setLayout(new BoxLayout(reviewSection,BoxLayout.Y_AXIS));
        for(String review: reviews){
            JLabel reviewLabel = new JLabel();
            Border border = BorderFactory.createEmptyBorder(10,10,10,10);
            reviewLabel.setBorder(border);
            String info ="<html><div style='width:250px;'> - "+ review+"</div></html>";
            reviewLabel.setText(info);
            reviewLabel.setForeground(Color.WHITE);
            reviewLabel.setFont(new Font("Arial", Font.BOLD, 20));
            reviewSection.add(Box.createVerticalStrut(10));
            reviewSection.add(reviewLabel);
            
       }
        reviewSection.revalidate(); 
        JScrollPane scrollPane = new JScrollPane(reviewSection,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(668, 10, 380, 660); // Đặt đúng kích thước & vị trí
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setBorder(null);
        scrollPane.getViewport().setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(new Color(0,0,0,0)); 
        scrollPane.getViewport().setOpaque(false);
        // Thêm scrollPane vào panelShadow1
        panelShadow1.setLayout(null);   
        panelShadow1.add(scrollPane);
        reviewSection.setSize(380,660);
        reviewSection.setLocation(668, 10);
        chart.setSize(648,650);
        chart.setLocation(10, 10);
        chart.setTitle(title);
        chart.addLegend("Price", Color.decode("#7b4397"), Color.decode("#dc2430"));
        test();
    }


    private void test() {
        chart.clear();
        for (ResponsePrice rp : Price_Array){
            Date sqlDate = rp.getPrice_Date();  // Tạo 1 SQL Date
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM");
            String dateString = formatter.format(sqlDate);
            String price = rp.getPrice();
            double priceData = Double.parseDouble(price);
            System.out.println(dateString +" "+ priceData);
            chart.addData(new ModelChart(dateString, new double[]{priceData}));
            
        }
        chart.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelShadow1 = new raven.panel.PanelShadow();
        chart = new raven.chart.CurveLineChart();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelShadow1.setBackground(new java.awt.Color(34, 59, 69));
        panelShadow1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelShadow1.setColorGradient(new java.awt.Color(17, 38, 47));

        chart.setForeground(new java.awt.Color(237, 237, 237));
        chart.setFillColor(true);

        javax.swing.GroupLayout panelShadow1Layout = new javax.swing.GroupLayout(panelShadow1);
        panelShadow1.setLayout(panelShadow1Layout);
        panelShadow1Layout.setHorizontalGroup(
            panelShadow1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelShadow1Layout.createSequentialGroup()
                .addComponent(chart, javax.swing.GroupLayout.PREFERRED_SIZE, 547, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 182, Short.MAX_VALUE))
        );
        panelShadow1Layout.setVerticalGroup(
            panelShadow1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chart, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelShadow1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelShadow1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
            java.util.logging.Logger.getLogger(CharFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CharFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CharFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CharFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private raven.chart.CurveLineChart chart;
    private raven.panel.PanelShadow panelShadow1;
    // End of variables declaration//GEN-END:variables
}
