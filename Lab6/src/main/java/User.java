public class User {
    private String username;
    private String password;
    private int accNum;
    public User(){
        username = "";
        password = "";
        accNum = 0;
    }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getAccNum() { return accNum; }
    public void setAccNum(int accNum) { this.accNum = accNum; }
}
