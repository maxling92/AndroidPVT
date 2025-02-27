package com.digitech_maker.pvt2025;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Hasil {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @SerializedName("ID")
    private int myID = 0;

    @SerializedName("namadata")
    private String namadata;

    @SerializedName("jeda")
    private String jeda = "";

    @SerializedName("gagal")
    private int gagal = 0;

    @SerializedName("rata_rata")
    private int rata_rata = 0;

    @SerializedName("jenistest")
    private int jenistest = 1;  // 1 visual, 2 audio

    @SerializedName("tanggal")
    private String tanggal;

    @SerializedName("namaPerusahaan")
    private String namaPerusahaan;

    // LocalDate will be converted to String for Gson serialization
    @SerializedName("tgllahir")
    private LocalDate tgllahir;

    @SerializedName("namaobservant")
    private String namaobservant;

    @SerializedName("lokasi")
    private  String lokasi;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    // Constructor
    public Hasil(String namaobservant, String tgllahir, String namaPerusahaan) {
        this.namaobservant = namaobservant;
        this.namaPerusahaan = namaPerusahaan;
        try {
            this.tgllahir = LocalDate.parse(tgllahir, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Error: Date format should be " + DATE_FORMAT + ". Received: " + tgllahir);
        }
    }

    public List<Integer> getJedaArray() {
        List<Integer> jedaArray = new ArrayList<>();
        if (jeda != null && !jeda.isEmpty()) {
            String[] jedaStrings = jeda.split(",\\s*"); // Memisahkan jeda berdasarkan koma
            for (String jedaStr : jedaStrings) {
                try {
                    jedaArray.add(Integer.parseInt(jedaStr));
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing jeda value: " + jedaStr);
                }
            }
        }
        return jedaArray;
    }

    // Getter methods
    public String getJeda() {
        return jeda;
    }

    public int getJenistest() {
        return jenistest;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getTglLahir() {
        return tgllahir.format(formatter);
    }

    public String getNamaObservant() {
        return namaobservant;
    }

    public String getnamaPerusahaan() { return namaPerusahaan; }

    public int getRataRata() {
        return rata_rata;
    }

    public int getGagal() {
        return gagal;
    }

    public String getNamadata() {
        return namadata;
    }

    public  String getLokasi() {
        return lokasi;
    }

    public int getID() {
        return myID;
    }

    public void setID(int ID) {
        myID = ID;
    }

    public void setJeda(String hasil) {
        jeda = hasil;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public void setTanggal(String dt) {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault());
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            LocalDate parsedDate = LocalDate.parse(dt, inputFormatter);
            tanggal = parsedDate.format(outputFormatter);
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing date: " + dt);
            tanggal = dt; // Simpan dalam format asli jika parsing gagal
        }
    }

    public void setTglLahir(String tglLahir) {
        try {
            this.tgllahir = LocalDate.parse(tglLahir, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Error: Date format should be " + DATE_FORMAT + ". Received: " + tglLahir);
        }
    }

    public void setNamaObservant(String nama) {
        namaobservant = nama;
    }

    public void setRataRata(int rr) {
        rata_rata = rr;
    }

    public void setRataRata(int[] rr) {
        int rata = 0;
        for (int i = 5; i < rr.length; i++) {
            rata = rata + rr[i];
        }
        rata_rata = Math.round(rata / (rr.length - 5));
    }

    public void setGagal(int ggl) {
        gagal = ggl;
    }

    public void setJenisTest(int jenis) {
        jenistest = jenis;
    }

    public void setNamadata(String namadata) {
        this.namadata = namadata;
    }

    public void setnamaPerusahaan(String Perusahaan){namaPerusahaan = Perusahaan;}


}
