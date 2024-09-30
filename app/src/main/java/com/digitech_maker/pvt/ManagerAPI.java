package com.digitech_maker.pvt;

import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerAPI {
    private static final String TAG = "ApiManager";
    private ServiceAPI serviceAPI;

    public ManagerAPI() {
        serviceAPI = ClientAPI.getClient().create(ServiceAPI.class);
    }

    public void uploadResults(List<Hasil> hasilList, final ApiCallback callback) {
        Call<ResponseBody> call = serviceAPI.uploadResults(hasilList);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    // Handle failure response codes like 400 or 500
                    callback.onFailure("Failed to upload: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle network failure
                callback.onFailure("Network failure: " + t.getMessage());
            }
        });
    }


    public interface ApiCallback {
        void onSuccess(ResponseBody responseBody);
        void onFailure(String message);
    }

    public interface DataCallback {
        void onSuccess(ResponseBody responseBody);
        void onFailure(Throwable t);
    }

    public void sendHasil(Hasil hasil, DataCallback callback) {
        ServiceAPI serviceAPI = ClientAPI.getClient().create(ServiceAPI.class);
        Call<ResponseBody> call = serviceAPI.uploadResults(Collections.singletonList(hasil));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(new Exception("Failed to send data"));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }
}
