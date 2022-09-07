/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import java.sql.*;


/**
 *
 * @author Nitin.A
 */
public class PdfMaker {
    
    String doc_name,doc_special,doc_phone,p_name,date,status,p_phone,pres;
    
    public void create(Connection conn,int id) {
        try 
        {
            PreparedStatement ps = conn.prepareStatement("select * from history where id=\""+id+"\"");
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                doc_name = rs.getString("doctor_name");
                p_name = rs.getString("user_name");
                date = rs.getDate("date").toString();
                pres = rs.getString("prescription").replaceAll("<br>", "\n");
                
                status = "completed";
                
                ps = conn.prepareStatement("select * from users where name=\""+p_name+"\"");
                rs = ps.executeQuery();
                rs.next();
                p_phone = rs.getString("phone_no");
                
                ps = conn.prepareStatement("select * from doctors where name=\""+doc_name+"\"");
                rs = ps.executeQuery();
                rs.next();
                doc_phone = rs.getString("phone_no");
                doc_special = rs.getString("special");
            }

            PdfWriter pdfWriter = new PdfWriter("src/doc/prescription.pdf");
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);
            pdfDocument.setDefaultPageSize(PageSize.A4);
            float col = 280f;
            float columnWidth[] = {col,col};

            Table table = new Table(columnWidth);   
            
            table.setBackgroundColor(new DeviceRgb(255,51,51))
                    .setFontColor(new DeviceRgb(255,255,255));
            
            table.addCell(new Cell().add(new Paragraph("DADS OFFICIAL"))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setFontSize(30f)
                    .setPaddingLeft(30f)
                    .setBorder(Border.NO_BORDER)
            );
            
            table.addCell(new Cell().add(new Paragraph("Booking Id: "+id+"\nDoctor Name: "+doc_name+"\nSpecialist In: "+doc_special+"\nPhone No: "+doc_phone))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setBorder(Border.NO_BORDER)
                    .setPaddingLeft(60f)
                    .setPaddingTop(20f)
                    .setPaddingBottom(20f)
            );
            
            float col1 = 280f;
            float columnWidth1[] = {col1,col1};

            Table table1 = new Table(columnWidth1);  
            table1.setMarginTop(30f);
            
            table1.addCell(new Cell().add(new Paragraph("Patient Name: "+p_name))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setBorder(Border.NO_BORDER)
            );
            
            table1.addCell(new Cell().add(new Paragraph("Date: "+date))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBorder(Border.NO_BORDER)
            );
            
            table1.addCell(new Cell().add(new Paragraph("Phone No: "+p_phone))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setBorder(Border.NO_BORDER)
            );
            
            table1.addCell(new Cell().add(new Paragraph("Status: "+status))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBorder(Border.NO_BORDER)
            );
            
            float col2 = 540f;
            float columnWidth2[] = {col2};

            Table table2 = new Table(columnWidth2);  
            table2.setMarginTop(30f);
            
            table2.addCell(new Cell().add(new Paragraph("YOUR PRESCRIPTION"))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20f)
            );
            
            table2.addCell(new Cell().add(new Paragraph(pres))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMargin(100f)
                    .setPaddingTop(30f)
                    .setPaddingLeft(30f)
                    .setFontSize(15f)
                    .setHeight(300f)
            );
            
            ImageData imageData = ImageDataFactory.create("src/images/check.png");
            Image pdfImg = new Image(imageData);
            pdfImg.setFixedPosition(pdfDocument.getDefaultPageSize().getWidth()/2-120, pdfDocument.getDefaultPageSize().getHeight()/2-180);
            pdfImg.setOpacity(0.3f);
            
            Paragraph para = new Paragraph("Please Take The Medicines Prescribed To You.\nThank You");
            para.setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10f);
            
            document.add(table);
            document.add(table1);
            document.add(table2);
            document.add(pdfImg);
            document.add(para);
            document.close();
            
        } catch (Exception ex) {}
    }
}
