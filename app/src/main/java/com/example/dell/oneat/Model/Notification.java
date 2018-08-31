package com.example.dell.oneat.Model;

public class Notification {

    public String Title;
    public String Body;

    public Notification(String title, String body) {
        Title = title;
        Body = body;
    }

    public String getTitles() {
        System.out.println("in the get title");

        return Title;
    }

    public void setTitles(String title) {
        Title = title;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }
}
