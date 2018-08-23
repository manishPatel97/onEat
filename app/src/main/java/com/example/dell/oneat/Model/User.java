package com.example.dell.oneat.Model;

public class User {

    private String UserName;
    private String password;
    private String email;
    private String phone;



    public User(){

    }
    public User(String Name,String passw,String Email,String Phone){
        UserName = Name;
        password = passw;
        email = Email;
        phone = Phone;

    }
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getName() {
        return UserName;
    }

    public void setName(String Name) {
        UserName = Name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
