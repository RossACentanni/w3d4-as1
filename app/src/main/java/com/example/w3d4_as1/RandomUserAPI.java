package com.example.w3d4_as1;

import com.example.w3d4_as1.entities.UserResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RandomUserAPI {

    String BASE_URL = "https://randomuser.me/";

    //https://randomuser.me/api
    @GET("api")
    Call<UserResponse> getRandomUser();

    //https:randomuser.me/api?results=?
    @GET("api")
    Call<UserResponse> getRandomUsers(@Query("results") int results);

}
