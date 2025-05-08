package dk.easv.belsign.UnitTesting;

import at.favre.lib.crypto.bcrypt.BCrypt;
import dk.easv.belsign.BE.Users;
import dk.easv.belsign.BLL.Util.LoginValidator;
import dk.easv.belsign.BLL.Util.UserSession;
import dk.easv.belsign.DAL.ICrudRepo;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginValidatorTest {

    @Mock
    private ICrudRepo mockUsersDAO;

    private LoginValidator loginValidator;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // Create the validator (which internally creates a real UsersDAO)
        loginValidator = new LoginValidator();

        // Replace the real DAO with our mock using the setter
        loginValidator.setUsersDAO(mockUsersDAO);

        // Reset user session before each test
        UserSession.setLoggedInUser(null);
    }

    @Test
    void testValidateLogin_Success() throws Exception {
        // Arrange
        int userId = 1;
        String password = "correctPassword";
        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        Users mockUser = new Users(userId, 2, "John", "Doe", "example@email.dk", hashedPassword);
        when(mockUsersDAO.read(userId)).thenReturn(CompletableFuture.completedFuture(mockUser));

        // Act
        boolean result = loginValidator.validateLogin(userId, password);

        // Assert
        assertTrue(result);
        Users loggedInUser = UserSession.getLoggedInUser();
        assertNotNull(loggedInUser);
        assertEquals(userId, loggedInUser.getUserId());
    }

    @Test
    void testValidateLogin_WrongPassword() throws Exception {
        // Arrange
        int userId = 1;
        String correctPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        String hashedPassword = BCrypt.withDefaults().hashToString(12, correctPassword.toCharArray());

        Users mockUser = new Users(userId, 2, "John", "Doe", "example@email.dk", hashedPassword);
        when(mockUsersDAO.read(userId)).thenReturn(CompletableFuture.completedFuture(mockUser));

        // Act
        boolean result = loginValidator.validateLogin(userId, wrongPassword);

        // Assert
        assertFalse(result);
        assertNull(UserSession.getLoggedInUser());
    }

    @Test
    void testValidateLogin_UserNotFound() throws Exception {
        // Arrange
        int userId = 1;
        String password = "password";

        when(mockUsersDAO.read(userId)).thenReturn(CompletableFuture.completedFuture(null));

        // Act
        boolean result = loginValidator.validateLogin(userId, password);

        // Assert
        assertFalse(result);
        assertNull(UserSession.getLoggedInUser());
    }



    @Test
    void testUserSessionIsSetOnSuccessfulLogin() throws Exception {
        // Arrange
        int userId = 1;
        String password = "sessionPassword";
        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        Users expectedUser = new Users(userId, 3, "Session", "User", "example@email.dk", hashedPassword);
        when(mockUsersDAO.read(userId)).thenReturn(CompletableFuture.completedFuture(expectedUser));

        // Verify session is null before login
        assertNull(UserSession.getLoggedInUser());

        // Act
        boolean result = loginValidator.validateLogin(userId, password);

        // Assert
        assertTrue(result);

        // Verify user session contains the correct user
        Users sessionUser = UserSession.getLoggedInUser();
        assertNotNull(sessionUser);
        assertEquals(expectedUser.getRoleId(), sessionUser.getRoleId());
        assertEquals(expectedUser.getFirstName(), sessionUser.getFirstName());
        assertEquals(expectedUser.getLastName(), sessionUser.getLastName());
        assertEquals(expectedUser.getEmail(), sessionUser.getEmail());
    }
}