package com.digitech_maker.pvt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;

public class ClientAPI {

    private static final String BASE_URL = "https://pvtinfo.web.id/api/";
    private static Retrofit retrofit = null;

    // Singleton instance of Retrofit client
    public static Retrofit getClient() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    // Interface ApiService
    public interface ApiService {
        @retrofit2.http.POST("upload")
        Call<ResponseBody> sendData(@Body Map<String, Object> data);
    }

    // Method to send data to server
    public static void SendDatatoWeb(Map<String, Object> data, final ResponseListener listener) {
        ApiService apiService = getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.sendData(data);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    listener.onResponseSuccess("Data terkirim!");
                } else {
                    listener.onResponseError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onResponseError("Failure: " + t.getMessage());
            }
        });
    }


    // ResponseListener interface
    public interface ResponseListener {
        void onResponseSuccess(String response);
        void onResponseError(String error);
    }
}
