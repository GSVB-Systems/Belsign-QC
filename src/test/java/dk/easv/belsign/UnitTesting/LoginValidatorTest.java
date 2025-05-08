package dk.easv.belsign.UnitTesting;

import at.favre.lib.crypto.bcrypt.BCrypt;
import dk.easv.belsign.BE.Users;
import dk.easv.belsign.BLL.Util.LoginValidator;
import dk.easv.belsign.BLL.Util.UserSession;
import dk.easv.belsign.DAL.ICrudRepo;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.IOException;
import java.sql.SQLException;
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
    void testValidateLogin_Success() throws SQLException {
        // Arrange
        int userId = 1;
        String password = "correctPassword";
        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        Users mockUser = new Users(1, 2, "John", "Doe", email, hashedPassword);
        when(mockUsersDAO.getUserById()).thenReturn(CompletableFuture.completedFuture(mockUser));

        // Act
        boolean result = loginValidator.validateLogin(email, password);

        // Assert
        assertTrue(result);
        Users loggedInUser = UserSession.getLoggedInUser();
        assertNotNull(loggedInUser);
        assertEquals(email, loggedInUser.getEmail());
    }

    @Test
    void testValidateLogin_WrongPassword() throws SQLException {
        // Arrange
        String email = "test@example.com";
        String correctPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        String hashedPassword = BCrypt.withDefaults().hashToString(12, correctPassword.toCharArray());

        Users mockUser = new Users(1, 2, "John", "Doe", email, hashedPassword);
        when(mockUsersDAO.getUserByEmail(email)).thenReturn(CompletableFuture.completedFuture(mockUser));

        // Act
        boolean result = loginValidator.validateLogin(email, wrongPassword);

        // Assert
        assertFalse(result);
        assertNull(UserSession.getLoggedInUser());
    }

    @Test
    void testValidateLogin_UserNotFound() throws SQLException {
        // Arrange
        String email = "nonexistent@example.com";
        String password = "password";

        when(mockUsersDAO.getUserByEmail(email)).thenReturn(CompletableFuture.completedFuture(null));

        // Act
        boolean result = loginValidator.validateLogin(email, password);

        // Assert
        assertFalse(result);
        assertNull(UserSession.getLoggedInUser());
    }



    @Test
    void testUserSessionIsSetOnSuccessfulLogin() throws SQLException {
        // Arrange
        String email = "session@example.com";
        String password = "sessionPassword";
        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        Users expectedUser = new Users(42, 3, "Session", "User", email, hashedPassword);
        when(mockUsersDAO.getUserByEmail(email)).thenReturn(CompletableFuture.completedFuture(expectedUser));

        // Verify session is null before login
        assertNull(UserSession.getLoggedInUser());

        // Act
        boolean result = loginValidator.validateLogin(email, password);

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