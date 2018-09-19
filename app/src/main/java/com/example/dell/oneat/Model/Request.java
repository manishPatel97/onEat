package com.example.dell.oneat.Model;

import java.util.List;

public class Request {

    private String name;
    private String phone;
    private String address;
    private String total;
    private String status;
    private String paymentState;
    private List<Order> foods;
    private String comment;
    private String latLng;
    public Request(){}
    public Request(String name, String phone, String address, String total, List<Order> foods,String paymentState,String latLng) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.total = total;
        this.foods = foods;
        this.status = "0";
        this.paymentState = paymentState;
        this.latLng = latLng;
        //this.comment = comment;

    }

  /*  public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
*/

    public String getLatLng() {
        return latLng;
    }

    public void setLatLng(String latLng) {
        this.latLng = latLng;
    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}
