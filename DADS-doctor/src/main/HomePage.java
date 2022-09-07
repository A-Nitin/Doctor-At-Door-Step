/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package main;

import java.awt.*;
import javax.swing.BorderFactory;
import java.sql.*;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.table.DefaultTableModel;
import mail.SendMail;


/**
 *
 * @author Nitin.A
 */
public class HomePage extends JFrame {

    /**
     * Creates new form HomePage
     */
    Connection conn=null;
    ResultSet rs = null;
    PreparedStatement ps=null;
    private JTable table;
    private JScrollPane scrollPane;
    String username;
    
    String get_name(String username){
        String name=null;
        try {   
            ps = conn.prepareStatement("select name from doctors where username=?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if(rs.next())
                name=rs.getString("name");
            
        } catch (SQLException ex) {}
        return name;
    }
    
    void showTable(String choice){
        String name;
        String request_status;
        String booking_id;
        PreparedStatement ps=null;
        DefaultTableModel tblModel = (DefaultTableModel)doctorTable.getModel();
        tblModel.setRowCount(0);
        try {   
            if(choice.equals("completed"))
                ps = conn.prepareStatement("select * from bookings where doctor_name=? and request_status in (\"completed\")");
            else ps = conn.prepareStatement("select * from bookings where doctor_name=? and request_status not in (\"completed\")");
            
            ps.setString(1, get_name(username));
            rs = ps.executeQuery();
            while(rs.next()){
                name=rs.getString("user_name");
                request_status = rs.getString("request_status");
                booking_id = String.valueOf(rs.getInt("id"));
                String tbData[] = {booking_id,name,request_status};
                tblModel.addRow(tbData);              
            }
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment( JLabel.CENTER );
            doctorTable.getColumnModel().getColumn(0).setCellRenderer( centerRenderer );
        } catch (SQLException ex) {}
        
        JTableHeader th = doctorTable.getTableHeader();
        th.setFont(new Font("Segoe UI",Font.BOLD,12));
        th.setBackground(new Color(51,51,51));
        th.setForeground(Color.WHITE);
    }
    
    void display_content(String name){
        if(name.equals("home")){
            String text = "<html><span>Welcome to <br> "
                    + "<span style=\\\"color:rgb(85,65,118);font-size:20pt;"
                    + "font-weight:bold\">DADS App </span>"
                    + "<br>Doctor At Door Step.</span></html>";
            btn1Panel.setVisible(false);
            label2.setText(text);
            label1.setText("Home");
            respondMsg.setText("");
            showTable(""); 
        }
        else if(name.equals("prescription")){
            try{
                ps = conn.prepareStatement("select * from bookings where doctor_name=\""
                        +get_name(username)+"\" and request_status in (\"accepted\")");
                rs = ps.executeQuery();

                if(rs.next()){
                    String text = "<html><span>You need to prescribe "
                            + "<span style=\\\"color:rgb(85,65,118);font-size:20pt;"
                            + "font-weight:bold\">Medicines</span>"
                            + "<br>to end your session.</span></html>";
                    label2.setText(text);
                    label1.setText("Prescription");
                    respondMsg.setText("");
                    show_prescription();
                }
                else
                { 
                    ps = conn.prepareStatement("select * from bookings where "
                            + "doctor_name=\""+get_name(username)+"\" and request_status "
                            + "not in (\"accepted\",\"completed\")");
                    rs = ps.executeQuery();

                    if(rs.next()){
                        String text = "<html><span>Currently you dont have any "
                            + "<span style=\\\"color:rgb(85,65,118);font-size:20pt;"
                            + "font-weight:bold\">Patient</span> to prescribe.<br><br> "
                            + "<span style=\\\"font-size:10pt;color:rgb(255,51,51)\">You need accept "
                            + "request to make them your patient.</span></span></html>";
                        btn1Panel.setVisible(false);
                        label2.setText(text);
                        label1.setText("Prescription");
                        respondMsg.setText("");
                    }   
                    else
                    {
                        String text = "<html><span>Currently you dont have any "
                            + "<span style=\\\"color:rgb(85,65,118);font-size:20pt;"
                            + "font-weight:bold\">Patient</span> to prescribe.</span></html>";
                        btn1Panel.setVisible(false);
                        label2.setText(text);
                        label1.setText("Prescription");
                        respondMsg.setText("");
                    }
                }
                label2.setFont(new Font("Segoe UI",Font.PLAIN,16));
            }
            catch(Exception e){}
            showTable("");
        }
        else if(name.equals("request")){
            String text = "<html><span>"
                    + "<span style=\\\"color:rgb(85,65,118);font-size:20pt;"
                    + "font-weight:bold\">Accept or Decline</span>"
                    + " request from clients.<br><br> "
                    + "<span style=\\\"font-size:10pt;color:rgb(255,51,51)\">"
                    + "Request List is displayed on the right.</span></span></html>";
            btn1Panel.setVisible(false);
            label2.setText(text);
            label1.setText("Requests");
            respondMsg.setText("");
            showTable("");
            label2.setFont(new Font("Segoe UI",Font.PLAIN,16));
        }
        else if(name.equals("history")){
            String text = "<html><span>You can view the details <br>of the"
                    + "<span style=\\\"color:rgb(85,65,118);font-size:20pt;"
                    + "font-weight:bold\"> Completed Sessions.</span><br><br>"
                    + "<span style=\\\"font-size:10pt;color:rgb(255,51,51)\">"
                    + "All you need to do is click the required session diplayed "
                    + "in table on the right.</span></span></html>";
            btn1Panel.setVisible(false);
            label2.setText(text);
            label1.setText("History");
            respondMsg.setText("");
            showTable("completed");
            label2.setFont(new Font("Segoe UI",Font.PLAIN,14));
        } 
        respondMsg.setFont(new Font("Segoe UI",Font.PLAIN,14));
    }
    
    void show_prescription(){

        btn1Panel.setVisible(true);
        btn1Label.setText("Write Prescription");

    }
    
    void display_profile(int id,String name,String type,String side,String diff){
        try{

            if(side.equals("users"))
                ps = conn.prepareStatement("select * from users where name=?");
            else ps = conn.prepareStatement("select * from doctors where name=?");
            
            ps.setString(1,name);
            rs = ps.executeQuery();
 
            if(rs.next()){
                String email = rs.getString("email");
                String phone = rs.getString("phone_no");
                if(type.equals("accepted")){
                    label1.setText("Client Detail");
                    btn1Panel.setVisible(false);   
                    String text = 
                            "<html>"
                            + "<span style=\\\"font-size:18pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Name: </span>"+name+"<br></span>"
                            + "<span style=\\\"font-size:18pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Phone No: </span>"+phone+"<br></span>"
                            + "<span style=\\\"font-size:16pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Email: </span>"+email+"<br></span>"
                            + "<span style=\\\"font-size:18pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Status: </span>Accepted<br><br></span>"
                            + "<span style=\\\"font-size:10pt;color:rgb(255,51,51)\">";
                    if(diff.equals("another")){
                        text+= "Another docter has been<br>"
                            + "booked by this client.<br></span>"
                            + "</html>";
                    }
                    else
                    {
                        text+= "You are booked to<br>"
                            + "this client.<br></span>"
                            + "</html>";
                    }
                    label2.setText(text);
                    respondMsg.setText("Decline");
                    respondMsg.setForeground(new Color(51,51,51));
                    label2.setFont(new Font("Segoe UI",Font.PLAIN,14));
                }
                else if(type.equals("waiting")){
                    label1.setText("Client Detail");
                    btn1Panel.setVisible(true);
                    btn1Label.setText("Accept");
                    String text = 
                            "<html>"
                            + "<span style=\\\"font-size:18pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Name: </span>"+name+"<br></span>"
                            + "<span style=\\\"font-size:18pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Phone No: </span>"+phone+"<br></span>"
                            + "<span style=\\\"font-size:16pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Email: </span>"+email+"<br></span>"
                            + "<span style=\\\"font-size:18pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Status: </span>Waiting.<br><br></span>"
                            + "<span style=\\\"font-size:10pt;color:rgb(255,51,51)\">"
                            + "You can accept the request<br>"
                            + "by clicking the button<br>bellow.<br></span>"
                            + "</html>";
                    respondMsg.setText("Reject");
                    respondMsg.setForeground(new Color(51,51,51));
                    label2.setText(text);
                    label2.setFont(new Font("Segoe UI",Font.PLAIN,14));
                }    
                else if(type.equals("completed")){
                    label1.setText("Session Detail");
                    btn1Panel.setVisible(false);
                    ps = conn.prepareStatement("select * from history where id =?");
                    ps.setInt(1, id);
                    rs = ps.executeQuery();
                    if(rs.next()){
                        String text = 
                                "<html>"
                                + "<span style=\\\"font-size:18pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                                + "Name: </span>"+name+"<br></span>"
                                + "<span style=\\\"font-size:18pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                                + "Phone No: </span>"+phone+"<br></span>"
                                + "<span style=\\\"font-size:16pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                                + "Email: </span>"+email+"<br></span>"
                                +"<span style=\\\"font-size:16pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                                + "Date: </span>"+rs.getDate("date")+"<br></span>"
                                +"<span style=\\\"font-size:16pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                                + "Time: </span>"+rs.getTime("time")+"<br></span>"
                                + "<span style=\\\"font-size:18pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                                + "Status: </span>Completed.<br><br></span>"
                                + "<span style=\\\"font-size:10pt;color:rgb(255,51,51)\">"
                                + "Your session with this client "
                                + "has been completed.<br></span>"
                                + "</html>";
                        respondMsg.setText("");
                        label2.setText(text);
                        label2.setFont(new Font("Segoe UI",Font.PLAIN,14));
                    }
                }    
                else if(type.equals("profile")){
                    label1.setText("Your Profile");
                    btn1Panel.setVisible(true);
                    String text = 
                            "<html>"
                            + "<span style=\\\"font-size:18pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Name: </span>"+name+"<br></span>"
                            + "<span style=\\\"font-size:18pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Specialist In: </span>"+rs.getString("special")+"<br></span>"
                            + "<span style=\\\"font-size:18pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Phone No: </span>"+rs.getString("phone_no")+"<br></span>"
                            + "<span style=\\\"font-size:16pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Email: </span>"+rs.getString("email")+"<br></span>"
                            + "<span style=\\\"font-size:18pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Status: </span>"+rs.getString("status")+"<br><br></span>"
                            + "<span style=\\\"font-size:10pt;color:rgb(255,51,51)\">"
                            + "You can change your status<br>"
                            + "by clicking the button<br>bellow.<br></span>"
                            + "</html>";
                    
                    label2.setFont(new Font("Segoe UI",Font.PLAIN,14));
                    label2.setText(text);
                    respondMsg.setText("");
                    respondMsg.setForeground(new Color(255,51,51));
                    if(rs.getString("status").equals("available"))
                        btn1Label.setText("Make Status Busy");
                    else btn1Label.setText("Make Status Available");
                }    
            }
        }catch(SQLException e){}
        respondMsg.setFont(new Font("Segoe UI",Font.PLAIN,14));
    }
    
    void send_message(String choice){

        int index = doctorTable.getSelectedRow();
        TableModel model = doctorTable.getModel();
        String user_name = model.getValueAt(index, 0).toString();
        try {
            ps = conn.prepareStatement("select * from users where name=?");
            ps.setString(1, user_name);
            rs = ps.executeQuery();
            rs.next();
            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        String sub="",body="";
                        if(choice.equals("accepted")){
                            sub="Message from DADS-app Official";
                            body= "your request to Dr."+get_name(username)+" was Accepted.";
                        }
                        else if(choice.equals("rejected")){
                            sub="Message from DADS-app Official";
                            body= "your request to Dr."+" was rejected.";
                        }
                        else if(choice.equals("declined")){
                            sub="Message from DADS-app Official";
                            body= "your Booking with Dr."+get_name(username)+" was cancelled.";
                        }
                        SendMail s = new SendMail("salimlias4444qwez@gmail.com",sub,body);
                    } catch (Exception ex) {}
                }
            }).start();
        } catch (SQLException ex) {}
    }
    
    void refresh(){
        if(label1.getText().equals("History") || label2.getText().indexOf("completed")>0)
        {
            showTable("completed");
            display_content("history");
        }
        else if(label1.getText().equals("Client Detail"))
        {
            showTable("");
            display_content("request");
        }
        else if(label1.getText().equals("Prescription")){
            showTable("");
            display_content("prescription");
        }
        else if(label1.getText().equals("Requests")){
            showTable("");
            display_content("request");
        }
        else if(label1.getText().equals("Home")){
            showTable("");
            display_content("home");
        }
        else if(label1.getText().equals("Your Profile")){
            showTable("");
            display_content("profile");
        }
        doctorTable.clearSelection();
        respondMsg.setText("");
    }
    
    void chage_respond(){
        respondMsg.setText("<html>You have written the prescription and<br>the session "
                + "has ended successfully.<html>");
        respondMsg.setFont(new Font("Segoe UI",Font.PLAIN,12));
    }
    
    void set_enabled(){
        this.setEnabled(true);
    }
    
    public HomePage(Connection conn,String username) {
        initComponents();
        this.conn = conn;
        this.username=username;
        String name = get_name(username);
        profileText.setText(name);
        showTable("");
        display_content("home");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        profileText = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        logout = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        homeNav = new javax.swing.JLabel();
        reqNav = new javax.swing.JLabel();
        presNav = new javax.swing.JLabel();
        historyNav = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        doctorTable = new javax.swing.JTable();
        refreshBtn = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        btn1Panel = new javax.swing.JPanel();
        btn1Label = new javax.swing.JLabel();
        label2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        label1 = new javax.swing.JLabel();
        seperator = new javax.swing.JSeparator();
        respondMsg = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(255, 51, 51));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("DADS");

        jPanel4.setBackground(new java.awt.Color(51, 51, 51));

        profileText.setBackground(new java.awt.Color(255, 255, 255));
        profileText.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        profileText.setForeground(new java.awt.Color(255, 51, 51));
        profileText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        profileText.setText("Profile");
        profileText.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 1, 1, new java.awt.Color(255, 51, 51)));
        profileText.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                profileTextMouseMoved(evt);
            }
        });
        profileText.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                profileTextMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                profileTextMouseExited(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(51, 51, 51));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/baseline_person_white_18dp.png"))); // NOI18N
        jLabel2.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 2, 1, 0, new java.awt.Color(255, 51, 51)));
        jLabel2.setOpaque(true);
        jLabel2.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jLabel2MouseMoved(evt);
            }
        });
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel2MouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(profileText, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(profileText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        logout.setBackground(new java.awt.Color(51, 51, 51));
        logout.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/back.png"))); // NOI18N
        logout.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(255, 51, 51)));
        logout.setOpaque(true);
        logout.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                logoutMouseMoved(evt);
            }
        });
        logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutMouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 516, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(logout, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(logout, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
        );

        jPanel3.setBackground(new java.awt.Color(51, 51, 51));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        homeNav.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        homeNav.setForeground(new java.awt.Color(255, 51, 51));
        homeNav.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        homeNav.setText("Home");
        homeNav.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                homeNavMouseMoved(evt);
            }
        });
        homeNav.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                homeNavMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                homeNavMouseExited(evt);
            }
        });
        jPanel3.add(homeNav, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 30));

        reqNav.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        reqNav.setForeground(new java.awt.Color(255, 51, 51));
        reqNav.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        reqNav.setText("Requests");
        reqNav.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                reqNavMouseMoved(evt);
            }
        });
        reqNav.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reqNavMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                reqNavMouseExited(evt);
            }
        });
        jPanel3.add(reqNav, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 120, 30));

        presNav.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        presNav.setForeground(new java.awt.Color(255, 51, 51));
        presNav.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        presNav.setText("Prescription");
        presNav.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                presNavMouseMoved(evt);
            }
        });
        presNav.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                presNavMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                presNavMouseExited(evt);
            }
        });
        jPanel3.add(presNav, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 10, 110, 30));

        historyNav.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        historyNav.setForeground(new java.awt.Color(255, 51, 51));
        historyNav.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        historyNav.setText("History");
        historyNav.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                historyNavMouseMoved(evt);
            }
        });
        historyNav.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                historyNavMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                historyNavMouseExited(evt);
            }
        });
        jPanel3.add(historyNav, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, 110, 30));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane1.setBorder(null);

        doctorTable.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        doctorTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "BOOKING ID", "PATIENT NAME", "STATUS"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        doctorTable.setGridColor(new java.awt.Color(255, 255, 255));
        doctorTable.setRowHeight(35);
        doctorTable.setRowMargin(3);
        doctorTable.setSelectionBackground(new java.awt.Color(255, 102, 102));
        doctorTable.setShowVerticalLines(false);
        doctorTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                doctorTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(doctorTable);

        refreshBtn.setBackground(new java.awt.Color(255, 51, 51));
        refreshBtn.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        refreshBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        refreshBtn.setText("Refresh");
        refreshBtn.setOpaque(true);
        refreshBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                refreshBtnMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                refreshBtnMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                refreshBtnMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 533, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(refreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(28, Short.MAX_VALUE)
                .addComponent(refreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));

        btn1Panel.setBackground(new java.awt.Color(255, 51, 51));
        btn1Panel.setForeground(new java.awt.Color(51, 51, 51));
        btn1Panel.setPreferredSize(new java.awt.Dimension(35, 54));
        btn1Panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn1Label.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn1Label.setForeground(new java.awt.Color(51, 51, 51));
        btn1Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn1Label.setText("Accept");
        btn1Label.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                btn1LabelMouseMoved(evt);
            }
        });
        btn1Label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn1LabelMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn1LabelMouseExited(evt);
            }
        });
        btn1Panel.add(btn1Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 280, 54));

        label2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        jPanel6.setOpaque(false);

        label1.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        label1.setForeground(new java.awt.Color(255, 51, 51));
        label1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label1.setText("Home Page");

        seperator.setBackground(new java.awt.Color(51, 51, 51));
        seperator.setForeground(new java.awt.Color(51, 51, 51));
        seperator.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        seperator.setOpaque(true);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(seperator, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seperator, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        respondMsg.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        respondMsg.setForeground(new java.awt.Color(255, 51, 51));
        respondMsg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        respondMsg.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                respondMsgMouseMoved(evt);
            }
        });
        respondMsg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                respondMsgMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                respondMsgMouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(btn1Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(respondMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 10, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(respondMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn1Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 599, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void homeNavMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeNavMouseMoved
        homeNav.setBorder(BorderFactory.createLineBorder(new Color(255,102,102)));
    }//GEN-LAST:event_homeNavMouseMoved

    private void homeNavMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeNavMouseExited
        homeNav.setBorder(null);
    }//GEN-LAST:event_homeNavMouseExited

    private void reqNavMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reqNavMouseMoved
        reqNav.setBorder(BorderFactory.createLineBorder(new Color(255,102,102)));
    }//GEN-LAST:event_reqNavMouseMoved

    private void reqNavMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reqNavMouseExited
        reqNav.setBorder(null);
    }//GEN-LAST:event_reqNavMouseExited

    private void presNavMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_presNavMouseMoved
        presNav.setBorder(BorderFactory.createLineBorder(new Color(255,102,102)));
    }//GEN-LAST:event_presNavMouseMoved

    private void presNavMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_presNavMouseExited
        presNav.setBorder(null);
    }//GEN-LAST:event_presNavMouseExited

    private void btn1LabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn1LabelMouseExited
        btn1Panel.setBackground(new Color(255,51,51));
        btn1Label.setForeground(new Color(51,51,51));
    }//GEN-LAST:event_btn1LabelMouseExited

    private void btn1LabelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn1LabelMouseMoved
        btn1Panel.setBackground(new Color(51,51,51));
        btn1Label.setForeground(new Color(255,51,51));
    }//GEN-LAST:event_btn1LabelMouseMoved

    private void homeNavMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeNavMouseClicked
        display_content("home");
    }//GEN-LAST:event_homeNavMouseClicked

    private void presNavMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_presNavMouseClicked
        display_content("prescription");
    }//GEN-LAST:event_presNavMouseClicked

    private void btn1LabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn1LabelMouseClicked
        try { 
            int index = doctorTable.getSelectedRow();
            TableModel model = doctorTable.getModel();
            String doc_name = get_name(username);

            if(btn1Label.getText().equals("Accept")){
                String user_name = model.getValueAt(index, 1).toString();
                int id = Integer.parseInt(model.getValueAt(index, 0).toString());
                ps = conn.prepareStatement("select * from doctors where status=? and name=?");
                ps.setString(1, "available");
                ps.setString(2, doc_name);
                rs = ps.executeQuery();
                if(rs.next()){
                    ps = conn.prepareStatement("update bookings set request_status=? where id=?");
                    ps.setString(1, "accepted");
                    ps.setInt(2, id);
                    ps.executeUpdate();
                    send_message("accepted");
                    display_profile(-1,user_name,"accepted","users","");
                    showTable("");

                    ps = conn.prepareStatement("update doctors set status=? where name=?");
                    ps.setString(1, "busy");
                    ps.setString(2, doc_name);
                    ps.executeUpdate();

                    display_content("prescription");
                    respondMsg.setText("Accepted Successfully");
                }
                else
                {
                    respondMsg.setText("You already have clients to attend.");
                }
            }
            else if(btn1Label.getText().equals("Make Status Busy")){
                ps = conn.prepareStatement("update doctors set status=? where name=?");
                ps.setString(1, "busy");
                ps.setString(2, doc_name);
                ps.executeUpdate();
                display_profile(-1,doc_name,"profile","doctors","");
                respondMsg.setText("Updated Successfully");
            }
            else if(btn1Label.getText().equals("Make Status Available")){
                ps = conn.prepareStatement("select * from bookings where doctor_name=? and request_status=\"accepted\"");
                ps.setString(1,doc_name);
                rs = ps.executeQuery();
                if(rs.next()){
                    String text = "<html>Have patients to attend,can't change.</html>";
                    respondMsg.setText(text);

                }
                else{
                    ps = conn.prepareStatement("update doctors set status=? where name=?");
                    ps.setString(1, "available");
                    ps.setString(2, doc_name);
                    ps.executeUpdate();
                    display_profile(-1,doc_name,"profile","doctors","");
                    respondMsg.setText("Updated Successfully");  
                }
            }
            else if(btn1Label.getText().equals("Write Prescription")){ 
                Prescription pres = new Prescription(this,conn,doc_name);
                Thread t = new Thread(pres);
                t.start();
                this.setEnabled(false);
                btn1Panel.setBackground(new Color(255,51,51));
                btn1Label.setForeground(new Color(51,51,51));
            }
        } catch (Exception ex) {}
        respondMsg.setFont(new Font("Segoe UI",Font.PLAIN,14));
    }//GEN-LAST:event_btn1LabelMouseClicked

    private void reqNavMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reqNavMouseClicked
        display_content("request");
    }//GEN-LAST:event_reqNavMouseClicked

    private void profileTextMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_profileTextMouseMoved
        profileText.setForeground(Color.WHITE);
        
    }//GEN-LAST:event_profileTextMouseMoved

    private void profileTextMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_profileTextMouseExited
        profileText.setForeground(new Color(255,51,51));
    }//GEN-LAST:event_profileTextMouseExited

    private void jLabel2MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseMoved
        profileText.setForeground(Color.WHITE);
    }//GEN-LAST:event_jLabel2MouseMoved

    private void jLabel2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseExited
        profileText.setForeground(new Color(255,51,51));
    }//GEN-LAST:event_jLabel2MouseExited

    private void profileTextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_profileTextMouseClicked
        display_profile(-1,get_name(username),"profile","doctors","");
        showTable("");
    }//GEN-LAST:event_profileTextMouseClicked

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        display_profile(-1,get_name(username),"profile","doctors","");
        showTable("");
    }//GEN-LAST:event_jLabel2MouseClicked

    private void respondMsgMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_respondMsgMouseMoved
        if(respondMsg.getText().equals("Reject")||respondMsg.getText().equals("Decline")){
            respondMsg.setForeground(new Color(255,51,51));
            respondMsg.setFont(new Font("Segoe UI",Font.PLAIN,14));
        }
    }//GEN-LAST:event_respondMsgMouseMoved

    private void respondMsgMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_respondMsgMouseExited
        if(respondMsg.getText().equals("Reject")||respondMsg.getText().equals("Decline")){
            respondMsg.setForeground(new Color(51,51,51));
            respondMsg.setFont(new Font("Segoe UI",Font.PLAIN,14));
        }
    }//GEN-LAST:event_respondMsgMouseExited

    private void respondMsgMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_respondMsgMouseClicked
        if(respondMsg.getText().equals("Reject")){
            int index = doctorTable.getSelectedRow();
            TableModel model = doctorTable.getModel();
            int id = Integer.parseInt(model.getValueAt(index, 0).toString());
            send_message("rejected");
            try {
                ps = conn.prepareStatement("delete from bookings where id=?");
                ps.setInt(1, id);
                ps.executeUpdate();
                display_content("request");
                showTable("");
                respondMsg.setText("Request rejected successfully.");
            } catch (SQLException ex) {
                Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
            }     
        }
        else if(respondMsg.getText().equals("Decline")){
            int index = doctorTable.getSelectedRow();
            TableModel model = doctorTable.getModel();
            int id = Integer.parseInt(model.getValueAt(index, 0).toString());
            String doc_name = get_name(username);
            send_message("declined");
            try {
                ps = conn.prepareStatement("delete from bookings where id=?");
                ps.setInt(1, id);
                ps.executeUpdate();
                display_content("request");
                showTable("");
                respondMsg.setText("Booking cancelled successfully.");

                ps = conn.prepareStatement("update doctors set status=? where name=?");
                ps.setString(1, "available");
                ps.setString(2, doc_name);
                ps.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(HomePage.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        doctorTable.clearSelection();
        respondMsg.setFont(new Font("Segoe UI",Font.PLAIN,14));
    }//GEN-LAST:event_respondMsgMouseClicked

    private void doctorTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_doctorTableMouseClicked
        try{
            int index = doctorTable.getSelectedRow();
            TableModel model = doctorTable.getModel();
            String user_name = model.getValueAt(index, 1).toString();
            String request_status = model.getValueAt(index, 2).toString();
            int id = Integer.parseInt(model.getValueAt(index, 0).toString());
            ps = conn.prepareStatement("select * from bookings where id=?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next())
            {   
                if(request_status.equals("waiting")){
                    ps = conn.prepareStatement("select doctor_name from bookings where user_name=? and request_status=?");
                    ps.setString(1, user_name);
                    ps.setString(2, "accepted");
                    rs = ps.executeQuery();
                    if(rs.next()){
                        display_profile(-1,user_name,"accepted","users","another");
                    } 
                    else display_profile(-1,user_name,"waiting","users","");
                }
                else if(request_status.equals("accepted")){
                    display_profile(-1,user_name,"accepted","users","");}
                else if(request_status.equals("completed"))
                    display_profile(id,user_name,"completed","users","");            
            }
        }
        catch(Exception e){}
    }//GEN-LAST:event_doctorTableMouseClicked

    private void refreshBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_refreshBtnMousePressed
        refreshBtn.setBackground(new Color(51,51,51));
        refreshBtn.setForeground(new Color(255,51,51));
    }//GEN-LAST:event_refreshBtnMousePressed

    private void refreshBtnMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_refreshBtnMouseReleased
        refreshBtn.setBackground(new Color(255,51,51));
        refreshBtn.setForeground(new Color(51,51,51));
    }//GEN-LAST:event_refreshBtnMouseReleased

    private void refreshBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_refreshBtnMouseClicked
        refresh();
    }//GEN-LAST:event_refreshBtnMouseClicked

    private void historyNavMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_historyNavMouseMoved
        historyNav.setBorder(BorderFactory.createLineBorder(new Color(255,102,102)));
    }//GEN-LAST:event_historyNavMouseMoved

    private void historyNavMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_historyNavMouseClicked
        display_content("history");
        doctorTable.clearSelection();
    }//GEN-LAST:event_historyNavMouseClicked

    private void historyNavMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_historyNavMouseExited
        historyNav.setBorder(null);
        
    }//GEN-LAST:event_historyNavMouseExited

    private void logoutMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseMoved
        logout.setBackground(Color.WHITE);
    }//GEN-LAST:event_logoutMouseMoved

    private void logoutMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseExited
        logout.setBackground(new Color(51,51,51));
    }//GEN-LAST:event_logoutMouseExited

    private void logoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseClicked
        int a = JOptionPane.showConfirmDialog(this,"Are you sure?","Confirm",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
        if(a==JOptionPane.YES_OPTION){
            Main main = new Main();
            main.setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_logoutMouseClicked

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btn1Label;
    private javax.swing.JPanel btn1Panel;
    private javax.swing.JTable doctorTable;
    private javax.swing.JLabel historyNav;
    private javax.swing.JLabel homeNav;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label1;
    private javax.swing.JLabel label2;
    private javax.swing.JLabel logout;
    private javax.swing.JLabel presNav;
    private javax.swing.JLabel profileText;
    private javax.swing.JLabel refreshBtn;
    private javax.swing.JLabel reqNav;
    private javax.swing.JLabel respondMsg;
    private javax.swing.JSeparator seperator;
    // End of variables declaration//GEN-END:variables
}
