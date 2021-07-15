package bgu.spl.net.srv;

public class Account {
    private final String username;
    private final String password;
    private boolean isLoggedIn = false;

    public Account(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isLoggedIn(){
        return isLoggedIn;
    }

    public void login(){
        isLoggedIn = true;
    }

    public void logout(){
        isLoggedIn = false;
    }
}
