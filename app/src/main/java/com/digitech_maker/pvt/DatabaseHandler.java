package com.digitech_maker.pvt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "hasil";

    // Table names
    private static final String TABLE_HASIL = "hasil";
    private static final String TABLE_WAKTU = "waktu";
    public static final String TABLE_USERS = "users";

    // TABLE_HASIL Columns names

    private static final String KEY_ID = "id";
    private static final String KEY_RATA = "ratarata";
    private static final String KEY_GAGAL = "gagal";
    private static final String KEY_JEDA = "jeda";
    private static final String KEY_TANGGAL = "tanggal";
    private static final String KEY_JENISTEST = "tipe";
    private static final String KEY_LASTID = "lastid";
    private static final String KEY_LOKASI = "lokasi";
    private static final String KEY_NAMADATA = "namadata";

    public static final String KEY_NAMAOBSERVANT = "namaobservant";
    public static final String KEY_TGLLAHIR = "tgllahir";
    public static final String KEY_JABATAN = "jabatan";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_PERUSAHAAN = "namaPerusahaan";

    private static DatabaseHandler instance;

    public static synchronized DatabaseHandler getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHandler(context.getApplicationContext());
        }
        return instance;
    }


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("DatabaseHandler", "DatabaseHandler instantiated");
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHandler", "onCreate called");

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAMAOBSERVANT + " TEXT,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_TGLLAHIR + " TEXT,"
                + KEY_JABATAN + " TEXT,"
                + KEY_PERUSAHAAN + " TEXT "
                + ")";
        Log.d("DatabaseHandler", "Creating table: " + CREATE_USERS_TABLE);
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_HASIL_TABLE = "CREATE TABLE " + TABLE_HASIL + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAMAOBSERVANT + " TEXT,"
                + KEY_RATA + " TEXT,"
                + KEY_GAGAL + " TEXT,"
                + KEY_JEDA + " INT,"
                + KEY_TANGGAL + " TEXT,"
                + KEY_TGLLAHIR + " TEXT,"
                + KEY_JENISTEST + " INT,"
                + KEY_JABATAN + " TEXT,"
                + KEY_LOKASI + " TEXT,"
                + KEY_PERUSAHAAN + " TEXT,"
                + KEY_NAMADATA + " TEXT "
                + ")";
        Log.d("DatabaseHandler", "Creating table: " + CREATE_HASIL_TABLE);
        db.execSQL(CREATE_HASIL_TABLE);

        String CREATE_WAKTU_TABLE = "CREATE TABLE " + TABLE_WAKTU + "("
                + KEY_ID + " INTEGER,"
                + KEY_LASTID + " INTEGER"
                + ")";
        Log.d("DatabaseHandler", "Creating table: " + CREATE_WAKTU_TABLE);
        db.execSQL(CREATE_WAKTU_TABLE);

        ContentValues values = new ContentValues();
        values.put(KEY_ID, 1);
        values.put(KEY_LASTID, 0);
        db.insert(TABLE_WAKTU, null, values);

        Log.d("DatabaseHandler", "Tables created and initialized");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HASIL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WAKTU);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }


    public void removeHasil(Hasil item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HASIL, KEY_NAMADATA + "=" + item.getNamadata(), null);
        db.close();
    }

    public void addHasil(Hasil item, String namaobservant) {
        SQLiteDatabase db = this.getWritableDatabase();

        Log.d("DatabaseHandler", "Fetching user details for: " + namaobservant);

        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_TGLLAHIR, KEY_JABATAN, KEY_PERUSAHAAN},
                KEY_NAMAOBSERVANT + "=?", new String[]{namaobservant}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            item.setTglLahir(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TGLLAHIR)));
            item.setJabatan(cursor.getString(cursor.getColumnIndexOrThrow(KEY_JABATAN)));
            item.setnamaPerusahaan(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PERUSAHAAN)));
            cursor.close();
        } else {
            Log.e("DatabaseHandler", "Cursor is null or empty while fetching user details");
            if (cursor != null) {
                cursor.close();
            }
        }

        ContentValues values = new ContentValues();
        values.put(KEY_NAMAOBSERVANT, namaobservant);
        values.put(KEY_RATA, item.getRataRata());
        values.put(KEY_GAGAL, item.getGagal());
        values.put(KEY_JEDA, item.getJeda());
        values.put(KEY_LOKASI, item.getLokasi());
        values.put(KEY_TANGGAL, item.getTanggal());
        values.put(KEY_TGLLAHIR, item.getTglLahir());
        values.put(KEY_JENISTEST, item.getJenistest());
        values.put(KEY_JABATAN, item.getJabatan());
        values.put(KEY_PERUSAHAAN, item.getnamaPerusahaan());


        if (item.getNamadata() != null && !item.getNamadata().isEmpty()) {
            db.update(TABLE_HASIL, values, KEY_NAMADATA + "=?", new String[]{item.getNamadata()});
        } else {
            long theid = db.insert(TABLE_HASIL, null, values);
            item.setNamadata(String.valueOf(theid));
        }
        db.close();
    }


    public List<Hasil> getAllHasil() {
        List<Hasil> hasilList = new ArrayList<Hasil>();
        String selectQuery = "SELECT  * FROM " + TABLE_HASIL;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String namaobservant = cursor.getString(1);
                String tgllahirStr = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TGLLAHIR));
                LocalDate tgllahir = LocalDate.parse(tgllahirStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String jabatan = cursor.getString(8);
                String namaPerusahaan = cursor.getString(10);


                Hasil hasil = new Hasil(namaobservant, jabatan, tgllahirStr, namaPerusahaan);
                hasil.setID(Integer.parseInt(cursor.getString(0)));
                hasil.setNamadata(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAMADATA)));
                hasil.setNamaObservant(namaobservant);
                hasil.setRataRata(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_RATA)));
                hasil.setGagal(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_GAGAL)));
                hasil.setJeda(cursor.getString(cursor.getColumnIndexOrThrow(KEY_JEDA)));
                hasil.setTanggal(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TANGGAL)));
                hasil.setTglLahir(tgllahirStr);
                hasil.setJenisTest(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_JENISTEST)));
                hasil.setJabatan(jabatan);
                hasil.setLokasi(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOKASI)));
                hasil.setnamaPerusahaan(namaPerusahaan);


                hasilList.add(hasil);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return hasilList;
    }

    public List<Hasil> getLastHasil() {
        List<Hasil> hasilList = new ArrayList<Hasil>();
        int lastid = 0;

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor1 = db.rawQuery("SELECT lastid FROM " + TABLE_WAKTU, null);
            if (cursor1.moveToFirst()) {
                lastid = cursor1.getInt(0);
            }
            cursor1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String selectQuery = "SELECT  * FROM " + TABLE_HASIL + " WHERE id > " + lastid;
        Cursor cursor = db.rawQuery(selectQuery, null);

        int nextlastid = lastid;
        if (cursor.moveToFirst()) {
            do {
                String namaobservant = cursor.getString(1);
                String tgllahir = cursor.getString(6);
                String jabatan = cursor.getString(8);
                String namaPerusahaan = cursor.getString(10);

                Hasil hasil = new Hasil(namaobservant, tgllahir, jabatan, namaPerusahaan);
                hasil.setID(Integer.parseInt(cursor.getString(0)));
                hasil.setRataRata(cursor.getInt(2));
                hasil.setGagal(cursor.getInt(3));
                hasil.setJeda(cursor.getString(4));
                hasil.setTanggal(cursor.getString(5));
                hasil.setJenisTest(cursor.getInt(7));
                hasil.setLokasi(cursor.getString(9));

                hasilList.add(hasil);
                nextlastid = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        ContentValues values = new ContentValues();
        values.put(KEY_LASTID, nextlastid);
        db.update(TABLE_WAKTU, values, KEY_NAMADATA + "= 1", null);

        cursor.close();
        db.close();
        return hasilList;
    }


    // Methods for users
    public void addUser (String namaobservant, String password, String tgllahir, String jabatan, String namaPerusahaan) {
        Log.d("DatabaseHandler", "addUser called with username: " + namaobservant);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAMAOBSERVANT, namaobservant);
        values.put(KEY_PASSWORD, password);
        values.put(KEY_TGLLAHIR, tgllahir);
        values.put(KEY_JABATAN, jabatan);
        values.put(KEY_PERUSAHAAN, namaPerusahaan);
        long result = db.insert(TABLE_USERS, null, values);
        if (result == -1) {
            Log.e("DatabaseHandler", "Failed to insert user");
        } else {
            Log.d("DatabaseHandler", "User added successfully");
        }
        db.close();
    }

    public boolean checkUser(String namaobservant, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_NAMADATA}, KEY_NAMAOBSERVANT + "=? AND " + KEY_PASSWORD + "=?",
                new String[]{namaobservant, password}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    public String getUserTanggallahir(String namaobservant) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_TGLLAHIR}, KEY_NAMAOBSERVANT + "=?",
                new String[]{namaobservant}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String tgllahir = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TGLLAHIR));
            cursor.close();
            db.close();
            return tgllahir;
        }
        db.close();
        return null;
    }

    public String getUserJabatan(String namaobservant) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_JABATAN}, KEY_NAMAOBSERVANT + "=?",
                new String[]{namaobservant}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String jabatan = cursor.getString(cursor.getColumnIndexOrThrow(KEY_JABATAN));
            cursor.close();
            db.close();
            return jabatan;
        }
        db.close();
        return null;
    }

    public String getUserPerusahaan(String namaobservant) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_PERUSAHAAN}, KEY_NAMAOBSERVANT + "=?",
                new String[]{namaobservant}, null,null,null);
        if (cursor != null && cursor.moveToFirst()) {
            String namaPerusahaan = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PERUSAHAAN));
            cursor.close();
            db.close();
            return namaPerusahaan;
        }
        db.close();
        return null;
    }

    public void logDatabaseState() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if (cursor.moveToFirst()) {
            do {
                Log.d("DatabaseHandler", "Table: " + cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    public boolean isTableExists(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{tableName});
        boolean tableExists = cursor.getCount() > 0;
        cursor.close();
        return tableExists;
    }

}
