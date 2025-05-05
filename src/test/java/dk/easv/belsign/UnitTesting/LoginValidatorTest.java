package dk.easv.belsign.UnitTesting;

import at.favre.lib.crypto.bcrypt.BCrypt;
import dk.easv.belsign.BE.Users;
import dk.easv.belsign.BLL.Util.LoginValidator;
import dk.easv.belsign.BLL.Util.UserSession;
import dk.easv.belsign.DAL.IUsersDataAccess; // Make sure this interface exists
import dk.easv.belsign.DAL.UsersDAO;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginValidatorTest {

    @Mock
    private IUsersDataAccess mockUsersDAO;

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
        String email = "test@example.com";
        String password = "correctPassword";
        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        Users mockUser = new Users(1, 2, "John", "Doe", email, hashedPassword);
        when(mockUsersDAO.getUserByEmail(email)).thenReturn(CompletableFuture.completedFuture(mockUser));

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
    void testValidateLogin_EmptyCredentials() {
        // Act & Assert
        assertFalse(loginValidator.validateLogin("", "password"));
        assertFalse(loginValidator.validateLogin("email@test.com", ""));
        assertFalse(loginValidator.validateLogin("", ""));
        assertFalse(loginValidator.validateLogin(null, "password"));
        assertFalse(loginValidator.validateLogin("email@test.com", null));
        assertFalse(loginValidator.validateLogin(null, null));
    }
}