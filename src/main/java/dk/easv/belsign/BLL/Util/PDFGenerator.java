package dk.easv.belsign.BLL.Util;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import dk.easv.belsign.BE.Orders;
import dk.easv.belsign.BE.Products;

import java.io.File;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class PDFGenerator {

    public static String convertDateToString(String date) {
    DateFormat df = new SimpleDateFormat(date);

        Date today = Calendar.getInstance().getTime();

        String dateToString = df.format(today);

        return (dateToString);
    }



        public static void createPDF(String filepath) throws Exception {
            File file = new File(filepath);
            file.getParentFile().mkdirs();
            PdfWriter writer = new PdfWriter(String.valueOf(file));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            pdf.addNewPage();

            String date = "dd-MM-yyyy";

            document.add(new Paragraph("OrderNumber: " + OrderSession.getEnteredOrder().getOrderId()).setFontSize(24).setBold());
            document.add(new Paragraph("Report made: " + convertDateToString(date)).setFontSize(16).setBold());
            document.add(new Paragraph("QC: " + UserSession.getLoggedInUser().getFirstName() + " " + UserSession.getLoggedInUser().getLastName()).setFontSize(16).setBold());

          /* for(Orders orders, OrderSession.getEnteredOrder().getProductQuantity()) {
               String product = pull productid from db;
               Image productImage = new Image(pull billeder fra db).setWidth(150).setHeight(150);
               document.add(productImage);
               String imageDirectoin = pull the image direction from db;
               String imageComment = pull comment from db if there is one;
               document.add(new Paragraph("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -").setFontSize(14).setBold());
            }

           */


            // Closing the document
            document.close();
            System.out.println("PDF Created");
        }
    }
