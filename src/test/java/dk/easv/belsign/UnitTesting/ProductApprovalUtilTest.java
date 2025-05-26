package dk.easv.belsign.UnitTesting;

import dk.easv.belsign.BE.Photos;
import dk.easv.belsign.BE.Products;
import dk.easv.belsign.BE.Users;
import dk.easv.belsign.BLL.Util.ProductApprovalUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductApprovalUtilTest {

    private Products testProduct;
    private Users adminUser;
    private ProductApprovalUtil productApprovalUtil;

    @BeforeEach
    void setUp() throws Exception {
        productApprovalUtil = new ProductApprovalUtil(null);
        testProduct = new Products(1, 1, "Test Product", 5, 10, "Pending");

        adminUser = new Users();
        adminUser.setUserId(100);
        adminUser.setRoleId(1);
    }

    @Test
    void approveProduct_WithAllPhotosApproved_ShouldSucceed() {
        // Arrange
        Photos photo1 = new Photos(1, 1, null);
        Photos photo2 = new Photos(2, 1, null);
        photo1.setPhotoStatus("Approved"); // Explicitly set status
        photo2.setPhotoStatus("Approved");

        List<Photos> photos = new ArrayList<>();
        photos.add(photo1);
        photos.add(photo2);
        testProduct.setPhotos(photos);

        LocalDateTime beforeApproval = LocalDateTime.now();

        // Act
        productApprovalUtil.approveProduct(testProduct, adminUser.getUserId());

        // Assert
        assertEquals("Approved", testProduct.getProductStatus());
        assertNotNull(testProduct.getApprovalDate());
        assertTrue(testProduct.getApprovalDate().isAfter(beforeApproval) ||
                testProduct.getApprovalDate().isEqual(beforeApproval));
        assertEquals(adminUser.getUserId(), testProduct.getApprovedBy());
    }

    @Test
    void approveProduct_WithPendingPhotos_ShouldThrowException() {
        // Arrange
        Photos photo1 = new Photos(1, 1, null);
        Photos photo2 = new Photos(2, 1, null);
        photo1.setPhotoStatus("Approved");
        photo2.setPhotoStatus("Pending");

        List<Photos> photos = new ArrayList<>();
        photos.add(photo1);
        photos.add(photo2);
        testProduct.setPhotos(photos);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> productApprovalUtil.approveProduct(testProduct, adminUser.getUserId()));
        assertEquals("Cannot approve product: Not all photos are approved", exception.getMessage());
        assertNotEquals("Approved", testProduct.getProductStatus());
    }

    @Test
    void approveProduct_WithNoPhotos_ShouldSucceed() {
        // Arrange
        testProduct.setPhotos(new ArrayList<>());
        LocalDateTime beforeApproval = LocalDateTime.now();

        // Act
        productApprovalUtil.approveProduct(testProduct, adminUser.getUserId());

        // Assert
        assertEquals("Approved", testProduct.getProductStatus());
        assertNotNull(testProduct.getApprovalDate());
        assertTrue(testProduct.getApprovalDate().isAfter(beforeApproval) ||
                testProduct.getApprovalDate().isEqual(beforeApproval));
    }

    @Test
    void rejectProduct_WithDeclinedPhoto_ShouldSucceed() {
        // Arrange
        Photos photo1 = new Photos(1, 1, null);
        Photos photo2 = new Photos(2, 1, null);
        photo1.setPhotoStatus("Approved");
        photo2.setPhotoStatus("Declined");

        List<Photos> photos = new ArrayList<>();
        photos.add(photo1);
        photos.add(photo2);
        testProduct.setPhotos(photos);

        LocalDateTime beforeRejection = LocalDateTime.now();

        // Act
        productApprovalUtil.rejectProduct(testProduct);

        // Assert
        assertEquals("Rejected", testProduct.getProductStatus());
        assertNotNull(testProduct.getApprovalDate());
        assertTrue(testProduct.getApprovalDate().isAfter(beforeRejection) ||
                testProduct.getApprovalDate().isEqual(beforeRejection));
    }

    @Test
    void rejectProduct_WithRejectedPhoto_ShouldSucceed() {
        // Arrange
        Photos photo1 = new Photos(1, 1, null);
        Photos photo2 = new Photos(2, 1, null);
        photo1.setPhotoStatus("Approved");
        photo2.setPhotoStatus("Rejected");

        List<Photos> photos = new ArrayList<>();
        photos.add(photo1);
        photos.add(photo2);
        testProduct.setPhotos(photos);

        LocalDateTime beforeRejection = LocalDateTime.now();

        // Act
        productApprovalUtil.rejectProduct(testProduct);

        // Assert
        assertEquals("Rejected", testProduct.getProductStatus());
        assertNotNull(testProduct.getApprovalDate());
        assertTrue(testProduct.getApprovalDate().isAfter(beforeRejection) ||
                testProduct.getApprovalDate().isEqual(beforeRejection));
    }

    @Test
    void rejectProduct_WithoutDeclinedPhotos_ShouldThrowException() {
        // Arrange
        Photos photo1 = new Photos(1, 1, null);
        Photos photo2 = new Photos(2, 1, null);
        photo1.setPhotoStatus("Approved");
        photo2.setPhotoStatus("Approved");

        List<Photos> photos = new ArrayList<>();
        photos.add(photo1);
        photos.add(photo2);
        testProduct.setPhotos(photos);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> productApprovalUtil.rejectProduct(testProduct));
        assertEquals("Cannot reject product: No photos are declined", exception.getMessage());
        assertNotEquals("Rejected", testProduct.getProductStatus());
    }

    @Test
    void rejectProduct_WithNoPhotos_ShouldThrowException() {
        // Arrange
        testProduct.setPhotos(new ArrayList<>());

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> productApprovalUtil.rejectProduct(testProduct));
        assertEquals("Cannot reject product: No photos associated with this product", exception.getMessage());
    }

    @Test
    void getApprovalStatusText_ShowsFormattedDate() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        testProduct.setApprovalDate(now);

        // Test approved status with date
        testProduct.setProductStatus("Approved");
        String approvedText = productApprovalUtil.getApprovalStatusText(testProduct);
        assertTrue(approvedText.startsWith("Approved on"));

        // Test rejected status with date
        testProduct.setProductStatus("Rejected");
        String rejectedText = productApprovalUtil.getApprovalStatusText(testProduct);
        assertTrue(rejectedText.startsWith("Rejected on"));
    }
}