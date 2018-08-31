package com.example.dell.oneat.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.dell.oneat.Model.User;
import com.example.dell.oneat.Remote.APIService;
import com.example.dell.oneat.Remote.RetrofitClient;

public class currentUser {
    public static User currentuser;
    public static int number;
    public static final String DELETE = "Delete";
    public static final String USER_KEY ="User";
    public static final String PWD_KEY = "Password";
    public static final String FCM_URL ="https://fcm.googleapis.com/";

    public static APIService getFCMService(){
        return RetrofitClient.getClient(FCM_URL).create(APIService.class);
    }

    public static String convertCodeToStatus(String status){
        if(status.equals("0")){
            return "Placed";
        }else if(status.equals("1")){
            return "On my way";
        }else{
            return "Shipped";
        }
    }

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null){
            NetworkInfo []info = connectivityManager.getAllNetworkInfo();
            if(info!=null){
                for(int i=0;i<info.length;i++){
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}
