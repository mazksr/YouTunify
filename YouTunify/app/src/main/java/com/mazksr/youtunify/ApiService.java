package com.mazksr.youtunify;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ApiService {
    @Headers({
            "X-Rapidapi-Key: 38be14b232msh9169d540cc95ee7p18ed06jsnd78bad4f79f6",
            "X-Rapidapi-Host: spotify-scraper.p.rapidapi.com"
    })
    @GET("v1/search")
    Call<UserResponse> search(@Query("term") String term, @Query("type") String type, @Query("offset") int offset, @Query("limit") int limit);

    @Headers({
            "X-Rapidapi-Key: 38be14b232msh9169d540cc95ee7p18ed06jsnd78bad4f79f6",
            "X-Rapidapi-Host: spotify-scraper.p.rapidapi.com"
    })
    @GET("v1/track/download")
    Call<UserResponse> download(@Query("track") String nameOrId);

}