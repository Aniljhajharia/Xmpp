package com.example.user.xmppchat.File_upload;

import com.example.user.xmppchat.ResultObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface VideoInterface {
    @Multipart
    @Headers({
            "API_KEY: 494861765915649"
    })
    @POST("video/upload")
    Call<ResultObject> uploadVideoToServer(@Part MultipartBody.Part video);
}