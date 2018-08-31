package com.example.dell.oneat.Model;

public class Token {
    private String token;
    private Boolean isServerToken;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getServerToken() {
        return isServerToken;
    }

    public void setServerToken(Boolean serverToken) {
        isServerToken = serverToken;
    }

    public  Token(){}
    public Token(String token, Boolean isServerToken) {

        this.token = token;
        this.isServerToken = isServerToken;
    }
}
