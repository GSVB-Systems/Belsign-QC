package dk.easv.belsign.BE;

public class Users {
    private int userId;
    private int roleId;
    private String firstName;
    private String lastName;
    private String email;
    private String hashedPassword;

    public Users(int userId, int roleId, String firstName, String lastName, String email, String hashedPassword) {
        this.userId = userId;
        this.roleId = roleId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hashedPassword = hashedPassword;
    }
}
