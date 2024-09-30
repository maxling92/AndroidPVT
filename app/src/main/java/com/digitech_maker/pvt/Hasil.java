package com.digitech_maker.pvt;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

    @SerializedName("jabatan")
    private String jabatan;

    @SerializedName("namaPerusahaan")
    private String namaPerusahaan;

    // LocalDate will be converted to String for Gson serialization
    @SerializedName("tgllahir")
    private String tgllahirString;

    private LocalDate tgllahir;

    @SerializedName("namaobservant")
    private String namaobservant;

    @SerializedName("lokasi")
    private  String lokasi;

    @SerializedName("hasilData")
    private int[] hasilData;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    // Constructor
    public Hasil(String namaobservant, String jabatan, String tgllahir, String namaPerusahaan) {
        this.namaobservant = namaobservant;
        this.namaPerusahaan = namaPerusahaan;
        this.jabatan = jabatan;
        try {
            this.tgllahir = LocalDate.parse(tgllahir, formatter);
            this.tgllahirString = this.tgllahir.format(formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Error: Date format should be " + DATE_FORMAT + ". Received: " + tgllahir);
        }
    }

    public Hasil(String namaObservant, String jabatan, String tglLahir, String namaPerusahaan, String lokasi) {
        this(namaObservant, jabatan, tglLahir, namaPerusahaan);  // Call the original constructor
        this.lokasi = lokasi;  // Set the lokasi
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
        return tgllahir.format(formatter); // Return the formatted date as a String
    }

    public String getNamaObservant() {
        return namaobservant;
    }

    public String getJabatan() {
        return jabatan;
    }

    public String getnamaPerusahaan() { return namaPerusahaan; }

    public int getRataRata() {
        return rata_rata;
    }

    public int getGagal() {
        return gagal;
    }

    public void generateNamadata(String namaobservant, String tanggal, int number) {
        this.namadata = namaobservant + "-" + tanggal + "-" + number;
    }

    public String getNamadata() {
        return namadata;
    }

    public int[] getHasilData() {
        return hasilData;
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
        System.out.println("Setting tanggal: " + dt);
        tanggal = dt;
    }

    public void setTglLahir(String tglLahir) {
        try {
            this.tgllahir = LocalDate.parse(tglLahir, formatter);
            this.tgllahirString = this.tgllahir.format(formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Error: Date format should be " + DATE_FORMAT + ". Received: " + tglLahir);
        }
    }

    public void setJabatan(String jbtn) {
        System.out.println("Setting jabatan: " + jbtn);
        jabatan = jbtn;
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
