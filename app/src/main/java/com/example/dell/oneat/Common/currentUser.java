package com.example.dell.oneat.Common;

import com.example.dell.oneat.Model.User;

public class currentUser {
    public static User currentuser;
    public static int number;
    public static String convertCodeToStatus(String status){
        if(status.equals("0")){
            return "Placed";
        }else if(status.equals("1")){
            return "On my way";
        }else{
            return "Shipped";
        }
    }
}
