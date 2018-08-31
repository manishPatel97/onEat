package com.example.dell.oneat.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    //create Retrofit Client to send POST HTTP REQUEST
    private  static Retrofit retrofit;
    public static  Retrofit getClient(String baseURL){
        if(retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL).addConverterFactory(GsonConverterFactory.create()).build();

        }
        return  retrofit;
    }
}
