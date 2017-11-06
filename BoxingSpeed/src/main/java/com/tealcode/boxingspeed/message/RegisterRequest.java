package com.tealcode.boxingspeed.message;

/**
 * Created by YuBo on 2017/9/18.
 */

public class RegisterRequest {

    public static final String EMAIL_REG = "email";
    public static final String PHONE_REG = "phone";

    private String regMethod;
    private String username;
    private String password;

    public String getRegMethod() {
        return regMethod;
    }

    public void setRegMethod(String regMethod) {
        this.regMethod = regMethod;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
