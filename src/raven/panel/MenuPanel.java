package raven.panel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import javax.swing.BoxLayout;

public class MenuPanel extends javax.swing.JPanel {

    
    public MenuPanel() {
        initComponents();
        
    }

    
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(getWidth(), getHeight());
        float[] fractions = {0.0f, 0.5f, 1.0f};
        Color[] colors = {
            Color.decode("#0f0c29"),
            Color.decode("#302b63"),
            Color.decode("#24243e")
        };
        GradientPaint gpBorder = new GradientPaint(
            0, 0, Color.decode("#A1FFCE"),           // Điểm bắt đầu + màu
            getWidth(), getHeight(), Color.decode("#FAFFD1") // Điểm kết thúc + màu
        );
        LinearGradientPaint paint = new LinearGradientPaint(start,end, fractions, colors);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(paint);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        g2.setPaint(gpBorder);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,30,30);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
