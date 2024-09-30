package com.digitech_maker.pvt;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataUser {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private DatabaseHandler dbHelper;

    private static final String PREF_NAME = "UserDetails";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_NAMAOBSERVANT = "namaobservant";
    private static final String KEY_TGLLAHIR = "tgllahir"; // Stores date of birth as a string
    private static final String KEY_JABATAN = "jabatan";
    private static final String KEY_PERUSAHAAN = "namaPerusahaan";

    // Define date format
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public DataUser(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        dbHelper = new DatabaseHandler(context);  // Initialize dbHelper here
    }

    // Method to store user data (including formatted birth date)
    public void UserActive(String namaobservant, String tgllahir, String jabatan, String namaPerusahaan) {
        try {

            tgllahir = tgllahir.replace("/", "-");
            // Konversi string tanggal lahir menjadi format Date
            Date dateOfBirth = dateFormat.parse(tgllahir); // Parse String to Date using SimpleDateFormat
            String formattedDate = dateFormat.format(dateOfBirth); // Format it back to String if needed

            // Simpan data dalam SharedPreferences
            editor.putString(KEY_NAMAOBSERVANT, namaobservant);
            editor.putString(KEY_TGLLAHIR, formattedDate); // Save the formatted date string
            editor.putString(KEY_JABATAN, jabatan);
            editor.putString(KEY_PERUSAHAAN, namaPerusahaan);
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.apply();
        } catch (ParseException e) {
            e.printStackTrace();
            // Tambahkan handling jika parsing tanggal gagal
        }
    }


    // Method to clear user session data
    public void clearUserActive() {
        editor.clear();
        editor.apply();
    }

    // Check if the user is logged in
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Method to retrieve user's date of birth and convert it back to Date object
    public Date getTglLahir() {
        String tgllahirStr = sharedPreferences.getString(KEY_TGLLAHIR, null);
        if (tgllahirStr != null) {
            try {
                // Parse the stored string back into a Date object
                return dateFormat.parse(tgllahirStr);
            } catch (ParseException e) {
                e.printStackTrace(); // Handle parsing error
                return null; // Return null if parsing fails
            }
        }
        return null; // Return null if no date found
    }

    // Optional: Method to retrieve user's date of birth as string (if needed in string format)
    public String getTglLahirAsString() {
        return sharedPreferences.getString(KEY_TGLLAHIR, "");
    }

}
