package com.digitech_maker.pvt;


import androidx.annotation.NonNull;
public interface LocationCallback {
    void onLocationReceived(String lokasi, int testType);

    class Holder {
        private static LocationCallback callback;

        public static void setCallback(LocationCallback cb) {
            callback = cb;
        }

        public static LocationCallback getCallback() {
            return callback;
        }
    }
}
