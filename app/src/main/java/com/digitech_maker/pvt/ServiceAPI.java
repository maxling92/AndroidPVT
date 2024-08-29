package com.digitech_maker.pvt;

import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface ServiceAPI {
    @POST("/upload")
    Call<ResponseBody> uploadResults(@Body List<Hasil> hasilList);
}
