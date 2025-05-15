package dk.easv.belsign.BLL.Util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class  CameraHandler {
    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private static CameraHandler instance;

    private VideoCapture capture;
    private boolean isRunning;

    private CameraHandler() {
        capture = new VideoCapture();
    }

    public static synchronized CameraHandler getInstance() {
        if (instance == null) {
            instance = new CameraHandler();
        }
        return instance;
    }

    private Image matToImage(Mat frame) {
        Mat rgb = new Mat();
        Imgproc.cvtColor(frame, rgb, Imgproc.COLOR_BGR2RGB);

        byte[] data = new byte[(int) (rgb.total() * rgb.rows())];
        rgb.get(0, 0,data);

        WritableImage image = new WritableImage(rgb.cols(), rgb.rows());
        PixelWriter writer = image.getPixelWriter();
        writer.setPixels(0, 0, rgb.cols(), rgb.rows(),
                PixelFormat.getByteRgbInstance(), data, 0, rgb.cols() * 3);

        return image;
    }

    public Image capturePic() {
        if(!openCamera()) {
            System.err.println("Failed To capture picture.");
            return null;
        }
        Mat frame = new Mat();
        boolean success = capture.read(frame);
        if(!success || frame.empty()) {
            System.err.println("Failed to capture pic.");
            return null;
        }
        return matToImage(frame);
    }

    public void saveImagesToOrders(Image image, String orderId, String productId, String photo) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        String basePath = "src/main/resources/dk/easv/belsign/images/Orders";

        String path = basePath + File.separator + OrderSession.getEnteredOrder().getOrderId();
        File folder = new File(path);
        if (!folder.exists()) folder.mkdirs();

        String imgName = productId + "_" + PhotoSession.getCurrentPhoto().getPhotoName() + ".png";
        File truefile = new File(folder, imgName);

        try{
            ImageIO.write(bufferedImage, "png", truefile);
            System.out.println("Success! Saved to: " + truefile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving image " + e.getMessage());
        }

    }

    public boolean openCamera() {
        if (capture == null) {
            capture = new VideoCapture(0);
        }
        if (!capture.isOpened()) {
            capture.open(0);
        }
        isRunning = capture.isOpened();
        return isRunning;
    }

    public void releaseCam() {
        if (capture != null && capture.isOpened()) {
            capture.release();
        }
        isRunning = false;
    }

    public boolean isCamRunning() {
        return isRunning;
    }
    
}
