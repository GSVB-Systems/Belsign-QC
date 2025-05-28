package dk.easv.belsign.BE;

public class Users {
    private int userId;
    private int roleId;
    private String firstName;
    private String lastName;
    private String email;
    private String hashedPassword;

    public Users() {

    }




    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    // Used when retrieving users from DB
    public Users(int userId, int roleId, String firstName, String lastName, String email, String hashedPassword) {
        this.userId = userId;
        this.roleId = roleId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hashedPassword = hashedPassword;
    }

    // Used when creating a new user
    public Users(int roleId, String firstName, String lastName, String email, String hashedPassword) {
        this.roleId = roleId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hashedPassword = hashedPassword;
    }
}
