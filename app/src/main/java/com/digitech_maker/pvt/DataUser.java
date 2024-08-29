package com.digitech_maker.pvt;

import android.content.Context;
import android.content.SharedPreferences;

public class DataUser {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private DatabaseHandler dbHelper;

    private static final String PREF_NAME = "UserDetails";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_NAMAOBSERVANT = "namaobservant";
    private static final String KEY_TGLLAHIR = "tgllahir";
    private static final String KEY_JABATAN = "jabatan";
    private static final String KEY_PERUSAHAAN = "namaPerusahaan";

    public DataUser(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        dbHelper = new DatabaseHandler(context);  // Initialize dbHelper here
    }

    public void UserActive(String namaobservant, String tgllahir, String jabatan, String namaPerusahaan) {
        editor.putString(KEY_NAMAOBSERVANT, namaobservant);
        editor.putString(KEY_TGLLAHIR, tgllahir);
        editor.putString(KEY_JABATAN, jabatan);
        editor.putString(KEY_PERUSAHAAN, namaPerusahaan);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public void clearUserActive() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

}
