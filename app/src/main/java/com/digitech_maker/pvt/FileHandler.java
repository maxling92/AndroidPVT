package com.digitech_maker.pvt;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class FileHandler {

    private final Context context;
    public FileHandler(Context context) {
        this.context = context;
    }

    // Method untuk menyiapkan data yang akan dikirim
    public Map<String, Object> prepareDataForTransfer(Hasil hasil) {
        Map<String, Object> data = new HashMap<>();
        data.put("namaobservant", hasil.getNamaObservant());
        data.put("namadata", hasil.getNamadata());
        data.put("jeda", hasil.getJedaArray()); // Pastikan ini mengembalikan array int atau Integer
        data.put("gagal", hasil.getGagal());
        data.put("rata_rata", hasil.getRataRata());
        data.put("jenistest", hasil.getJenistest());
        data.put("tanggal", hasil.getTanggal());
        data.put("namaPerusahaan", hasil.getnamaPerusahaan());
        data.put("tgllahir", hasil.getTglLahir());
        data.put("lokasi", hasil.getLokasi());

        return data;
    }

    // Method untuk mengirim data ke server
    public void sendData(Hasil hasil) {
        // Siapkan data untuk dikirim
        Map<String, Object> data = prepareDataForTransfer(hasil);

        // Gunakan ClientAPI untuk mengirim data
        ClientAPI client = new ClientAPI();
        client.SendDatatoWeb(data, new ClientAPI.ResponseListener() {
            @Override
            public void onResponseSuccess(String response) {
                Log.i("FileHandler", "Data berhasil dikirim: " + response);
                Toast.makeText(context, "Data berhasil dikirim!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponseError(String error) {
                String fullErrorMessage = getFullErrorMessage(new Exception(error));
                Log.e("FileHandler", "Gagal mengirim data: " + fullErrorMessage);
                Toast.makeText(context, "Gagal mengirim data: " + fullErrorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    public String getFullErrorMessage(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }
}

