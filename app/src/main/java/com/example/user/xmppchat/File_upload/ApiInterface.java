package com.example.user.xmppchat.File_upload;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {
    @Multipart
    @Headers({
            "Authorization: Client-ID a2181c246a3c5d0"
    })
    @POST("image")
    Call<ImageResponse> postImage(
                                 @Query("album") String albumId,
                                 @Query("account_url") String username,
                                 @Part MultipartBody.Part image);
}
