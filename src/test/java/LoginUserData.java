public class LoginUserData {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public LoginUserData(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
