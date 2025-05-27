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
import java.util.ArrayList;
import java.util.List;


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
        try {
            if (!openCamera()) {
                throw new Exception("Failed to open camera.");
            }

            Mat frame = new Mat();
            boolean success = capture.read(frame);

            if (!success || frame.empty()) {
                throw new Exception("Camera returned an empty or unreadable frame.");
            }

            Core.flip(frame, frame, 1);
            return matToImage(frame);
        } catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
            return null;
        }
    }

    public String saveImagesToOrders(Image image, int orderId, int productId, String photo) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        String path = "images/Orders";
        String orderPath = path + "/" + orderId;
        File folder = new File(orderPath);
        if (!folder.exists()) folder.mkdirs();

        String imgName = productId + "_" + PhotoSession.getCurrentPhoto().getPhotoName() + ".png";
        File truefile = new File(folder, imgName);
        String returnPath = path + "/" + orderId + "/" + imgName;

        try {
            ImageIO.write(bufferedImage, "png", truefile);
            System.out.println("Success! Saved to: " + truefile.getAbsolutePath());
            return returnPath;
        } catch (IOException e) {
            ExceptionHandler.handleUnexpectedException(e);
            return null;
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

    //scanner device for cams
    public List<Integer> getAvailableCameras() {
        List<Integer> availableCameras = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            VideoCapture tempCapture = new VideoCapture(i);
            if (tempCapture.isOpened()) {
                availableCameras.add(i);
                tempCapture.release();
            }
        }
        return availableCameras;
    }

    public boolean isCamRunning() {
        return isRunning;
    }
    
}
