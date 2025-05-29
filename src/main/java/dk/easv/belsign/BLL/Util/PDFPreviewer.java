package dk.easv.belsign.BLL.Util;

import com.itextpdf.kernel.pdf.PdfDocument;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class PDFPreviewer {
    public static void showPDFPreview(File pdfFile, Stage parentStage) throws IOException {
        PDDocument document = PDDocument.load(pdfFile);
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        VBox pdfContainer = new VBox(5);
        pdfContainer.setStyle("-fx-background-color: #ffffff;");

        for (int pageNr = 0; pageNr < document.getNumberOfPages(); pageNr++) {
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageNr,150);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(600);
            pdfContainer.getChildren().add(imageView);
        }
        document.close();

        ScrollPane scrollPane = new ScrollPane(pdfContainer);
        scrollPane.setFitToWidth(true);

        Stage previewStage = new Stage(StageStyle.DECORATED);
        previewStage.initOwner(parentStage);
        previewStage.initModality(Modality.APPLICATION_MODAL);

        previewStage.setScene(new Scene(scrollPane, 615, 800));
        previewStage.setTitle("PDF Preview");


        previewStage.showAndWait();
    }
}
