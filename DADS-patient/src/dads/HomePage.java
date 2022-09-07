/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package dads;

import java.awt.*;
import java.io.*;
import static java.lang.Math.random;
import java.net.*;
import javax.swing.BorderFactory;
import java.sql.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.table.DefaultTableModel;
import mail.SendMail;


/**
 *
 * @author Nitin.A
 */
public class HomePage extends javax.swing.JFrame {

    /**
     * Creates new form HomePage
     */
    Connection conn=null;
    ResultSet rs = null;
    private JTable table;
    private JScrollPane scrollPane;
    String username;
    PreparedStatement ps;
    
    String get_name(String username){
        String name=null;
        try {   
            ps = conn.prepareStatement("select name from users where username=?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            if(rs.next())
                name=rs.getString("name");
            
        } catch (SQLException ex) {}
        return name;
    }
    
    void refresh(){
        if(label1.getText().equals("Bookings"))
        {
            showTable("all");
            display_content("booking");
        }
        else if(label1.getText().equals("Prescription")){
            display_history();
            display_content("prescription");
        }
        else if(label1.getText().equals("Specialists")){
            display_content("specialist");
        }
        else if(label1.getText().equals("Home")){
            display_content("home");
        }
        else if(label1.getText().equals("Your Profile")){
            display_profile(get_name(username),"users");
        }
        else if(label1.getText().equals("Book Now")){
            display_content("home");
        }
        
        respondMsg.setText("");
        choice.select("all");
    }

    void change_header(String choice){
        DefaultTableModel model = new DefaultTableModel();
        if(choice.equals("history")){
            model.addColumn("BOOKING ID");
            model.addColumn("DOCTOR NAME");
            model.addColumn("DATE");
        }
        else
        {
            model.addColumn("DOCTOR NAME");
            model.addColumn("SPECIALITY");
            model.addColumn("STATUS");
        }
        doctorTable.setModel(model);
        doctorTable.getTableHeader().resizeAndRepaint();
    }
    
    void showTable(String choice){
        String name;
        String status;
        String speciality;
        ps=null;
        change_header("");
        DefaultTableModel tblModel = (DefaultTableModel)doctorTable.getModel();
        tblModel.setRowCount(0);
        try {   
            if(choice.equals("all"))
                ps = conn.prepareStatement("select * from doctors");
            else{
                ps = conn.prepareStatement("select * from doctors where special=?");
                ps.setString(1, choice);
            }
            rs = ps.executeQuery();
            while(rs.next()){
                name=rs.getString("name");
                status = rs.getString("status");
                speciality = rs.getString("special");
                String tbData[] = {name,speciality,status};
                tblModel.addRow(tbData);
                               
            }
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
            choice.setVisible(false);
            searchBtn.setVisible(false);
            showTable("all");
            label2.setFont(new Font("Segoe UI",Font.PLAIN,16));
        }
        else if(name.equals("prescription")){
            String text = "<html>"
                    + "<span style=\\\"color:rgb(85,65,118);font-size:20pt;"
                    + "font-weight:bold\">The list of bookings</span> which are completed "
                    + "is displayed on the right along with the date of booking "
                    + "and booking id.<br><br><span style=\\\"font-size:10pt;color:rgb(255,51,51)\">"
                    + " Click the required session to view the prescription.</span></html>";
            btn1Panel.setVisible(false);
            label2.setText(text);
            label1.setText("Prescription");
            respondMsg.setText("");
            choice.setVisible(false);
            searchBtn.setVisible(false);
            display_history();
            label2.setFont(new Font("Segoe UI",Font.PLAIN,14));
        }
        else if(name.equals("specialist")){
            String text = "<html><span>Choose "
                    + "<span style=\\\"color:rgb(85,65,118);font-size:20pt;"
                    + "font-weight:bold\">A Doctor</span>"
                    + "<br>From The List.</span></html>";
            btn1Panel.setVisible(false);
            label2.setText(text);
            label1.setText("Specialist");
            respondMsg.setText("");
            choice.setVisible(true);
            searchBtn.setVisible(true);
            showTable("all");
            choice.select("all");
            label2.setFont(new Font("Segoe UI",Font.PLAIN,16));
        }
        else if(name.equals("booking")){
            try{
                PreparedStatement ps = conn.prepareStatement("select * from bookings where user_name=? and request_status=?");
                ps.setString(1, get_name(username));
                ps.setString(2, "accepted");
                rs = ps.executeQuery();
                if(rs.next()){
                    String text = "<html><span>Your Booking with Mr."
                            +rs.getString("doctor_name")+" has been<br>"
                            + "<span style=\\\"color:rgb(85,65,118);font-size:20pt;"
                            + "font-weight:bold\">Confirmed.</span><br></span>";

                    ps = conn.prepareStatement("select * from doctors where name=?");
                    ps.setString(1, rs.getString("doctor_name"));
                    rs = ps.executeQuery();  
                    if(rs.next()){
                        text+="<br><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Name: </span>"+rs.getString("name")+"<br>"
                            + "<span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Specialist In: </span>"+rs.getString("special")+"<br>"
                            + "<span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Phone No: </span>"+rs.getString("phone_no")+"<br>"
                            + "<span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Email: </span>"+rs.getString("email")+"<br>"
                            + "<span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Status: </span>"+rs.getString("status")+"<br>"
                            + "</html>";
                        label2.setText(text);
                    }
                }
                else
                {
                    String text;
                    ps = conn.prepareStatement("select * from bookings where user_name=? and request_status not in (\"completed\")");
                    ps.setString(1, get_name(username));
                    rs = ps.executeQuery();  
                    if(rs.next()){
                        text = "<html><span>Wait until a <br>"
                            +" doctor accepts your request.</span></html>";
                    }
                    else
                    {
                        text = "<html><span>Firstly Send request to a doctor "
                                + "to the view booking details.</span></html>";
                    }
                    label2.setText(text);
                }
                label2.setFont(new Font("Segoe UI",Font.PLAIN,14));
            }catch(Exception e){}
            
            label1.setText("Bookings");
            respondMsg.setText("");
            choice.setVisible(false);
            searchBtn.setVisible(false);
            showTable("all");
            btn1Panel.setVisible(false);
        }
    }
    
    void display_profile(String name,String side){
        try{
            PreparedStatement ps=null;
            if(side.equals("users"))
                ps = conn.prepareStatement("select * from users where name=?");
            else ps = conn.prepareStatement("select * from doctors where name=?");
            
            ps.setString(1,name);
            rs = ps.executeQuery();

            if(rs.next()){
                if(side.equals("doctors")){
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
                    label2.setText(text);
                    respondMsg.setText("");
                }    
                else
                {
                    label1.setText("Your Profile");
                    btn1Panel.setVisible(true);
                    btn1Label.setText("Edit");
                    String text = 
                            "<html>"
                            + "<span style=\\\"font-size:18pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Name: </span>"+name+"<br></span>"
                            + "<span style=\\\"font-size:18pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Phone No: </span>"+rs.getString("phone_no")+"<br></span>"
                            + "<span style=\\\"font-size:16pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Email: </span>"+rs.getString("email")+"<br></span>"
                            + "<span style=\\\"font-size:18pt\"><span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "</html>";
                    label2.setText(text);
                    respondMsg.setText("");
                }
            }
        }catch(Exception e){}
        label2.setFont(new Font("Segoe UI",Font.PLAIN,14));
    }
    
    void display_history(){
        change_header("history");
        String doc_name,id,date;
        DefaultTableModel tblModel = (DefaultTableModel)doctorTable.getModel();
        tblModel.setRowCount(0);
        try {   
            
            ps = conn.prepareStatement("select * from history where user_name=\""+get_name(username)+"\"");
            rs = ps.executeQuery();

            while(rs.next()){
                id = String.valueOf(rs.getInt("id"));
                doc_name=rs.getString("doctor_name");
                date = rs.getDate("date").toString();
                String tbData[] = {id,doc_name,date};
                tblModel.addRow(tbData);      
            }
        } catch (SQLException ex) {}
        
        JTableHeader th = doctorTable.getTableHeader();
        th.setFont(new Font("Segoe UI",Font.BOLD,12));
        th.setBackground(new Color(51,51,51));
        th.setForeground(Color.WHITE);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        doctorTable.getColumnModel().getColumn(0).setCellRenderer( centerRenderer );
    }
    
    void fill_choice(){
        try {   
            PreparedStatement ps = conn.prepareStatement("select distinct special from doctors");
            rs = ps.executeQuery();
            choice.add("all");
            while(rs.next()){
               choice.add(rs.getString("special"));
            }
        } catch (Exception ex) {}
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
        showTable("all");
        fill_choice();
        display_content("home");
    }
    
    void create_file(int id){
        String home = System.getProperty("user.home");
        File file = new File(home+"/Downloads/prescription-"+id+".pdf");
        if(file.exists())
        {
            respondMsg.setText("Downloaded Already.");
        }
        else
        {
            try {
                ps = conn.prepareStatement("select * from history where id=?");
                ps.setInt(1, id);
                rs = ps.executeQuery();
                rs.next();
                PdfMaker pm = new PdfMaker();
                pm.create(conn, id);
                respondMsg.setText("Downloaded Successfully.");
            } catch (Exception ex) {}
        }
    }
    
    void change_respond(){
        respondMsg.setText("Changes made successfully");
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
        specNav = new javax.swing.JLabel();
        presNav = new javax.swing.JLabel();
        bookingNav = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        doctorTable = new javax.swing.JTable();
        choice = new java.awt.Choice();
        searchBtn = new javax.swing.JLabel();
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
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
            .addComponent(profileText, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        logout.setBackground(new java.awt.Color(51, 51, 51));
        logout.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        logout.setForeground(new java.awt.Color(255, 51, 51));
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 515, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(logout, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(logout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 881, -1));

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
        jPanel3.add(homeNav, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 30));

        specNav.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        specNav.setForeground(new java.awt.Color(255, 51, 51));
        specNav.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        specNav.setText("Specialists");
        specNav.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                specNavMouseMoved(evt);
            }
        });
        specNav.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                specNavMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                specNavMouseExited(evt);
            }
        });
        jPanel3.add(specNav, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 110, 30));

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
        jPanel3.add(presNav, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 10, 110, 30));

        bookingNav.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        bookingNav.setForeground(new java.awt.Color(255, 51, 51));
        bookingNav.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bookingNav.setText("Bookings");
        bookingNav.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                bookingNavMouseMoved(evt);
            }
        });
        bookingNav.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bookingNavMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                bookingNavMouseExited(evt);
            }
        });
        jPanel3.add(bookingNav, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 10, 100, 30));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(282, 43, 599, 47));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane2.setBorder(null);
        jScrollPane2.setForeground(new java.awt.Color(255, 255, 255));

        doctorTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        doctorTable.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        doctorTable.setForeground(new java.awt.Color(51, 51, 51));
        doctorTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "DOCTOR NAME", "SPECIALIST IN", "STATUS"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        doctorTable.setShowGrid(false);
        doctorTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                doctorTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(doctorTable);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 68, 539, 320));

        choice.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        jPanel1.add(choice, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 21, 167, 23));

        searchBtn.setBackground(new java.awt.Color(255, 51, 51));
        searchBtn.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        searchBtn.setForeground(new java.awt.Color(51, 51, 51));
        searchBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        searchBtn.setText("Search");
        searchBtn.setOpaque(true);
        searchBtn.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                searchBtnMouseMoved(evt);
            }
        });
        searchBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchBtnMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                searchBtnMouseExited(evt);
            }
        });
        jPanel1.add(searchBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(209, 22, 66, 20));

        refreshBtn.setBackground(new java.awt.Color(255, 51, 51));
        refreshBtn.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        refreshBtn.setForeground(new java.awt.Color(51, 51, 51));
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
        jPanel1.add(refreshBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(512, 21, 59, 23));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(282, 90, 600, 410));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));

        btn1Panel.setBackground(new java.awt.Color(255, 51, 51));
        btn1Panel.setForeground(new java.awt.Color(51, 51, 51));

        btn1Label.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn1Label.setForeground(new java.awt.Color(51, 51, 51));
        btn1Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn1Label.setText("Book");
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

        javax.swing.GroupLayout btn1PanelLayout = new javax.swing.GroupLayout(btn1Panel);
        btn1Panel.setLayout(btn1PanelLayout);
        btn1PanelLayout.setHorizontalGroup(
            btn1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btn1Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        btn1PanelLayout.setVerticalGroup(
            btn1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btn1PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn1Label, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                .addContainerGap())
        );

        label2.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N

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
                    .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btn1Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(respondMsg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(respondMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(btn1Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 43, -1, 460));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void homeNavMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeNavMouseMoved
        homeNav.setBorder(BorderFactory.createLineBorder(new Color(255,102,102)));
    }//GEN-LAST:event_homeNavMouseMoved

    private void homeNavMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_homeNavMouseExited
        homeNav.setBorder(null);
    }//GEN-LAST:event_homeNavMouseExited

    private void specNavMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_specNavMouseMoved
        specNav.setBorder(BorderFactory.createLineBorder(new Color(255,102,102)));
    }//GEN-LAST:event_specNavMouseMoved

    private void specNavMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_specNavMouseExited
        specNav.setBorder(null);
    }//GEN-LAST:event_specNavMouseExited

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
            if(btn1Label.getText().equals("Book")){
                int index = doctorTable.getSelectedRow();
                TableModel model = doctorTable.getModel();
                String doc_name = model.getValueAt(index, 0).toString();
                String user_name = get_name(username);

                PreparedStatement ps = conn.prepareStatement("select * from bookings "
                        + "where user_name=? and doctor_name=? and request_status in (\"waiting\")");
                ps.setString(1, user_name);
                ps.setString(2, doc_name);
                rs = ps.executeQuery();
                if(rs.next()){
                    respondMsg.setText("This doctor is already been requested.");
                }
                else 
                {
                    ps = conn.prepareStatement("insert into bookings(doctor_name,user_name,request_status) values(?,?,?)");
                    ps.setString(1, doc_name);
                    ps.setString(2, user_name);
                    ps.setString(3, "waiting");
                    ps.executeUpdate();
                    respondMsg.setText("Request sent successfully.");
                }
            }
            else if(btn1Label.getText().equals("Edit"))
            {
                ps = conn.prepareStatement("select email from users where name=\""+get_name(username)+"\"");
                rs = ps.executeQuery();
                if(rs.next()){
                    Random rand = new Random();
                    int pin = rand.nextInt(10000);
                    String formatted = String.format("%05d", pin);
                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try {
                                String email = rs.getString("email");
                                SendMail s = new SendMail(email,"Message from DADS Official App.",formatted);
                            } catch (Exception ex) {}
                        }
                    }).start();

                    ps = conn.prepareStatement("select * from otp where username=\""+username+"\"");
                    rs = ps.executeQuery();

                    if(rs.next()){
                        ps = conn.prepareStatement("update otp set code =\""+formatted+"\" where username=\""+username+"\"");
                        ps.executeUpdate();
                    }
                    else
                    {
                        ps = conn.prepareStatement("insert into otp (username,code) values (\""+username+"\",\""+formatted+"\")");
                        ps.executeUpdate();
                    }

                    Profile p = new Profile(this,conn,get_name(username),username);
                    p.setVisible(true);
                    this.setEnabled(false);
                    btn1Panel.setBackground(new Color(255,51,51));
                    btn1Label.setForeground(new Color(51,51,51));
                }
            }
            else if(btn1Label.getText().equals("Download"))
            {
                int index = doctorTable.getSelectedRow();
                TableModel model = doctorTable.getModel();
                int id = Integer.parseInt(model.getValueAt(index, 0).toString());
                create_file(id);
            }
        } catch (Exception ex) {}
    }//GEN-LAST:event_btn1LabelMouseClicked

    private void searchBtnMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchBtnMouseMoved
            searchBtn.setForeground(new Color(255,51,51));
            searchBtn.setBackground(new Color(51,51,51));
    }//GEN-LAST:event_searchBtnMouseMoved

    private void searchBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchBtnMouseExited
            searchBtn.setForeground(new Color(51,51,51));
            searchBtn.setBackground(new Color(255,51,51));
    }//GEN-LAST:event_searchBtnMouseExited

    private void searchBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchBtnMouseClicked
            int index=choice.getSelectedIndex();
            String selected = choice.getItem(index);
            showTable(selected);
    }//GEN-LAST:event_searchBtnMouseClicked

    private void specNavMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_specNavMouseClicked
        display_content("specialist");
    }//GEN-LAST:event_specNavMouseClicked

    private void profileTextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_profileTextMouseClicked
        display_profile(get_name(username),"users");
        showTable("all");
        doctorTable.clearSelection();
    }//GEN-LAST:event_profileTextMouseClicked

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

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        display_profile(get_name(username),"users");
        showTable("all");
        doctorTable.clearSelection();
    }//GEN-LAST:event_jLabel2MouseClicked

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

    private void bookingNavMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bookingNavMouseMoved
        bookingNav.setBorder(BorderFactory.createLineBorder(new Color(255,102,102)));
    }//GEN-LAST:event_bookingNavMouseMoved

    private void bookingNavMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bookingNavMouseClicked
        display_content("booking");
    }//GEN-LAST:event_bookingNavMouseClicked

    private void bookingNavMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bookingNavMouseExited
        bookingNav.setBorder(null);
    }//GEN-LAST:event_bookingNavMouseExited

    private void doctorTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_doctorTableMouseClicked
        try{
            int index = doctorTable.getSelectedRow();
            TableModel model = doctorTable.getModel();
            String header_value = model.getColumnName(0);
            if(header_value.equals("DOCTOR NAME")){
                String doc_name = model.getValueAt(index, 0).toString();
                String status = model.getValueAt(index, 2).toString();
                if(status.equals("available")){
                    btn1Panel.setVisible(true);
                    label1.setText("Book Now");
                    String text = "<html><span>You can now book<br> "
                    + "<span style=\\\"color:rgb(85,65,118);font-size:20pt;"
                    + "font-weight:bold\">Mr."+doc_name+"</span> <br>by just clicking"
                    + "<br>book button below</span></html>";
                    btn1Label.setText("Book");
                    label2.setText(text);
                    respondMsg.setText("");
                }
                else
                {
                    btn1Panel.setVisible(false);
                    label1.setText("Book Now");
                    String text = "<html><span>"
                    + "<span style=\\\"color:rgb(85,65,118);font-size:20pt;"
                    + "font-weight:bold\">Mr."+doc_name+"</span><br>is not"
                    + " available<br>right now.</span></html>";

                    label2.setText(text);
                    respondMsg.setText("");
                }
                choice.setVisible(false);
                searchBtn.setVisible(false);
            }
            else
            {
                int id = Integer.parseInt(model.getValueAt(index, 0).toString());
                ps = conn.prepareStatement("select * from history where id=?");
                ps.setInt(1, id);
                rs = ps.executeQuery();
                if(rs.next()){
                    String pres = rs.getString("prescription");
                    label2.setText("<html>This prescription was written by "
                            + "<span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            + "Dr."+rs.getString("doctor_name")
                            +"</span> dated <span style=\\\"color:rgb(85,65,118);font-weight:bold\">"
                            +rs.getString("date")+".</span><br><br>"+pres+"<br><br>"
                            + "<span style=\\\"font-size:10pt;color:rgb(255,51,51)\">"
                            + "Please take your medicines.</span></html>");
                    label2.setFont(new Font("Segoe UI",Font.PLAIN,16));
                    btn1Panel.setVisible(true);
                    btn1Label.setText("Download");
                }
                label2.setFont(new Font("Segoe UI",Font.PLAIN,14));
            }   
        }
        catch(Exception e){}
    }//GEN-LAST:event_doctorTableMouseClicked

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
    private javax.swing.JLabel bookingNav;
    private javax.swing.JLabel btn1Label;
    private javax.swing.JPanel btn1Panel;
    private java.awt.Choice choice;
    private javax.swing.JTable doctorTable;
    private javax.swing.JLabel homeNav;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label1;
    private javax.swing.JLabel label2;
    private javax.swing.JLabel logout;
    private javax.swing.JLabel presNav;
    private javax.swing.JLabel profileText;
    private javax.swing.JLabel refreshBtn;
    private javax.swing.JLabel respondMsg;
    private javax.swing.JLabel searchBtn;
    private javax.swing.JSeparator seperator;
    private javax.swing.JLabel specNav;
    // End of variables declaration//GEN-END:variables
}