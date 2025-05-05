/*
package dk.easv.belsign.UnitTesting;

import at.favre.lib.crypto.bcrypt.BCrypt;
import dk.easv.belsign.DAL.DBConnector;
import dk.easv.belsign.DAL.UsersDAO;
import dk.easv.belsign.BE.Users;
import dk.easv.belsign.BLL.Util.UserSession;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsersDAOTest {

    @Mock
    private DBConnector mockDbConnector;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private UsersDAO usersDAO;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(mockDbConnector.getConnection()).thenReturn(mockConnection);

        // No reflection needed here - remove the problematic code
        UserSession.setLoggedInUser(null);

        usersDAO = new UsersDAO(mockDbConnector);
    }

    @Test
    void testCheckUserCredentials_Success() throws Exception {
        // Arrange
        String email = "test@example.com";
        String hashedPassword = "hashedPassword";
        String storedPassword = BCrypt.withDefaults().hashToString(12, hashedPassword.toCharArray());

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("hashedPassword")).thenReturn(storedPassword);
        when(mockResultSet.getInt("userId")).thenReturn(1);
        when(mockResultSet.getInt("roleId")).thenReturn(2);
        when(mockResultSet.getString("firstName")).thenReturn("John");
        when(mockResultSet.getString("lastName")).thenReturn("Doe");

        // Act
        CompletableFuture<Boolean> result = usersDAO.checkUserCredentials(email, hashedPassword);

        // Assert
        assertTrue(result.get());
        Users loggedInUser = UserSession.getLoggedInUser();
        assertNotNull(loggedInUser);
        assertEquals(email, loggedInUser.getEmail());
    }

    @Test
    void testCheckUserCredentials_InvalidPassword() throws Exception {
        // Arrange
        String email = "test@example.com";
        String hashedPassword = "wrongPassword";
        String storedPassword = BCrypt.withDefaults().hashToString(12, "correctPassword".toCharArray());

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("hashedPassword")).thenReturn(storedPassword);

        // Act
        CompletableFuture<Boolean> result = usersDAO.checkUserCredentials(email, hashedPassword);

        // Assert
        assertFalse(result.get());
        assertNull(UserSession.getLoggedInUser());
    }

    @Test
    void testCheckUserCredentials_UserNotFound() throws Exception {
        // Arrange
        String email = "nonexistent@example.com";
        String hashedPassword = "password";

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        // Act
        CompletableFuture<Boolean> result = usersDAO.checkUserCredentials(email, hashedPassword);

        // Assert
        assertFalse(result.get());
        assertNull(UserSession.getLoggedInUser());
    }

    @Test
    void testCheckUserCredentials_DatabaseError() throws Exception {
        // Arrange
        String email = "bennie@belman.dk";
        String hashedPassword = "test";

        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        // Act & Assert
        ExecutionException exception = assertThrows(ExecutionException.class,
                () -> usersDAO.checkUserCredentials(email, hashedPassword).get());

        // Verify the underlying cause is a RuntimeException
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Error checking user credentials", exception.getCause().getMessage());
    }

    @Test
    void testUserSessionLogsLoggedInUserAfterSuccessfulLogin() throws Exception {
        // Arrange
        String email = "session@test.com";
        String plainPassword = "password123";

        // Create a hash that would be stored in the database
        String storedHash = BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());

        int expectedUserId = 42;
        int expectedRoleId = 1;
        String expectedFirstName = "Session";
        String expectedLastName = "User";

        // Reset UserSession to ensure a clean state
        UserSession.setLoggedInUser(null);
        assertNull(UserSession.getLoggedInUser(), "UserSession should be empty before test");

        // Set up database mock to return a valid user
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("hashedPassword")).thenReturn(storedHash);
        when(mockResultSet.getInt("userId")).thenReturn(expectedUserId);
        when(mockResultSet.getInt("roleId")).thenReturn(expectedRoleId);
        when(mockResultSet.getString("firstName")).thenReturn(expectedFirstName);
        when(mockResultSet.getString("lastName")).thenReturn(expectedLastName);

        // Act
        CompletableFuture<Boolean> result = usersDAO.checkUserCredentials(email, plainPassword);

        // Assert
        assertTrue(result.get(), "Login should be successful");

        // Verify the correct user was stored in UserSession
        Users loggedInUser = UserSession.getLoggedInUser();
        assertNotNull(loggedInUser, "UserSession should contain a user after login");
        assertEquals(expectedUserId, loggedInUser.getUserId());
        assertEquals(expectedRoleId, loggedInUser.getRoleId());
        assertEquals(expectedFirstName, loggedInUser.getFirstName());
        assertEquals(expectedLastName, loggedInUser.getLastName());
        assertEquals(email, loggedInUser.getEmail());

        // Verify the SQL statement was executed correctly
        verify(mockStatement).setString(1, email);
        verify(mockStatement).executeQuery();
    }

}

 */