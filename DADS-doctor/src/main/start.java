/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
import javax.swing.*;

/**
 *
 * @author Nitin.A
 */
public class start implements ActionListener {
    
    Connection conn=null;
    
    public start() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dads", "root", "anbu4444");
        } catch (Exception ex) {}
        
        JFrame f = new JFrame();
        JButton btn = new JButton("ok");
        f.add(btn);
        btn.addActionListener(this);
        f.setSize(100,100);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }
    public static void main(String[] args) {
        new start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new HomePage(conn,"nitin").setVisible(true);

    }
}
