package com.app.instantcab.Responses;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("user_login_register.php")
    Call<Users> login_register(
      @Query("user_phone") String user_phone,
      @Query("user_auth_token") String user_auth_token,
      @Query("token") String token
    );

    
 @GET("user_location.php")
    Call<Users> user_location(
      @Query("user_phone") String user_phone,
      @Query("user_auth_token") String user_auth_token,
      @Query("latitude") String latitude,
      @Query("longitude") String longitude
    );



}