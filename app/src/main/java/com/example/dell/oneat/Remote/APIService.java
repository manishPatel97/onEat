package com.example.dell.oneat.Remote;

import com.example.dell.oneat.Model.Sender;
import com.example.dell.oneat.Model.myResponses;
import  retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
//create Retrofit Client to send POST HTTP REQUEST
    @Headers(
            {
                    "Content-Type: application/json",
                    "Authorization: key=AAAAeKYtRs0:APA91bGQv1wOyq-9Ad5cTpWfNKdATjlLt55GmJM74R8jKdX4dijGi4RdZcQlsLcKca39E25QkTQH9EfMVgD63g75ZcWhX33P0yB4F2fDp_xNlnx1NzOm4bISP-kF-4KWPX8yt4wrgYv3dq-E00rw9q2MyYh9Fq1QMg"
            }
    )
    @POST("fcm/send")
    Call<myResponses> sendnotifiaction(@Body Sender body);
    // Call<myResponses> sendNotification(@Body Sender body);
}
