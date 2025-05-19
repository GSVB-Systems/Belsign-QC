package dk.easv.belsign.BLL.Util;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import dk.easv.belsign.BE.Orders;
import dk.easv.belsign.BE.Photos;
import dk.easv.belsign.BE.Products;
import dk.easv.belsign.BLL.Util.OrderSession;


import java.io.File;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class PDFGenerator {
    private OrderSession orderSession;
    //private Products products;
    //private Orders orders;
    //private Photos photos;


    public void initialize() {
        this.orderSession = new OrderSession();

    }
    public static String convertDateToString(String date) {
    DateFormat df = new SimpleDateFormat(date);

        Date today = Calendar.getInstance().getTime();

        String dateToString = df.format(today);

        return (dateToString);
    }



        public void createPDF(String filepath, Products products) throws Exception {


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

             int orderSize = OrderSession.getEnteredOrder().getProductQuantity();

            for (int i = 0; i < orderSize; i++) {
                String productName = products.getProductName();
                document.add(new Paragraph("Product: " + productName).setBold());
                for(Photos photo : products.getPhotos()) {
                    String productImagePath = photo.getPhotoPath();
                    InputStream imagePath = getClass().getResourceAsStream(productImagePath);
                    Image productImage = new Image(ImageDataFactory.create(imagePath.readAllBytes()));
                    document.add(productImage);
                    String imageName = photo.getPhotoName();
                    document.add(new Paragraph(imageName));
                    String imageComment = photo.getPhotoComment();
                    document.add(new Paragraph("comment for " + imageName + ": " + imageComment));
                }
                document.add(new Paragraph("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -").setFontSize(14).setBold());
            }



            // Closing the document
            document.close();
            System.out.println("PDF Created");
        }
    }
