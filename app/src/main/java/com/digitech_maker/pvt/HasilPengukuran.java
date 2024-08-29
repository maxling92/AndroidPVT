package com.digitech_maker.pvt;

import android.content.Context;
import android.content.Intent;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;

public class HasilPengukuran extends AppCompatActivity {

    private DatabaseHandler db;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final int LOCATION_REQUEST_CODE = 10;
    private LocationManager locationManager;
    private static String currentAddress;
    private static String lokasi;
    private List<Hasil> results;
    public static int cur_idx = 0;
    private ManagerAPI managerAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_pengukuran);

        managerAPI = new ManagerAPI();

        db = DatabaseHandler.getInstance(this);
        results = db.getAllHasil();
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);


        Hasil item;
        int databaru = 0;
        if (Welcom.result != null) {
            item = Welcom.result;
            results.add(item);
            databaru = 1;
        } else {
            if (!results.isEmpty()) {
                item = results.get(results.size() - 1);
            } else {
                item = null;
            }
        }
        cur_idx = results.size() - 1;

        if (item != null) {
            displayHasil(item);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        checkPermissionMaps();

        Intent intent = getIntent();
        String namaobservant = intent.getStringExtra("username");
        String tgllahir = intent.getStringExtra("tgllahir");
        String jabatan = intent.getStringExtra("jabatan");
        String namaPerusahaan = intent.getStringExtra("namaPerusahaan");

        if (intent != null && intent.hasExtra("lokasi")) {
            lokasi = intent.getStringExtra("lokasi");
        }

        Button hapus = findViewById(R.id.hapusbutton);
        hapus.setOnClickListener(onHapusClick);

        Button simpan = findViewById(R.id.simpanbutton);
        simpan.setOnClickListener(onSimpanClick);

        Button berikut = findViewById(R.id.nextbutton);
        berikut.setOnClickListener(onNextClick);

        Button kirim = findViewById(R.id.kirimbutton);
        kirim.setOnClickListener(onKirimClick);

        EditText editnama = findViewById(R.id.editnama);
        editnama.setText(namaobservant);
        editnama.setEnabled(false);

        EditText edittgllahir = findViewById(R.id.edittgllahir);
        edittgllahir.setText(tgllahir);
        edittgllahir.setEnabled(false);

        EditText editjabatan = findViewById(R.id.editjabatan);
        editjabatan.setText(jabatan);
        editjabatan.setEnabled(false);

        EditText editperusahaan = findViewById(R.id.editperusahaan);
        editperusahaan.setText(namaPerusahaan);
        editperusahaan.setEnabled(false);

        if (databaru == 1) {
            editnama.setEnabled(true);
            edittgllahir.setEnabled(true);
            editjabatan.setEnabled(true);
            editperusahaan.setEnabled(true);
        }

        TextView tipe = findViewById(R.id.tipelabel);
        if (item != null) {
            this.setTitle("PVT: " + getString(R.string.title_hasilpengukuran) + " " + getString(R.string.mw_cahayabtn));
            if (item.getJenistest() == 2)
                this.setTitle("PVT: " + getString(R.string.title_hasilpengukuran) + " " + getString(R.string.mw_suarabtn));

            tipe.setText(getString(R.string.hasil_tipelabel) + ": " + item.getTanggal().toString());

            TextView lokasiLabel = findViewById(R.id.lokasilabel);
            if (lokasi != null && !lokasi.isEmpty()) {
                lokasiLabel.setText(getString(R.string.hasil_lokasilabel) + ": " + lokasi);
            } else {
                lokasiLabel.setText(getString(R.string.hasil_lokasilabel) + ": " + getString(R.string.hasil_lokasi_unknown));
            }
        }

            TextView hasillabel = findViewById(R.id.hasillabel);
            if (item.getRataRata() < 240) {
                hasillabel.setText(getString(R.string.hasil_hasillabel) + ": " + item.getRataRata() + " (" + getString(R.string.hasil_hasilnormal) + ")");
                hasillabel.setTextColor(Color.BLUE);
            } else if (item.getRataRata() < 480) {
                hasillabel.setText(getString(R.string.hasil_hasillabel) + ": " + item.getRataRata() + " (" + getString(R.string.hasil_hasilringan) + ")");
                hasillabel.setTextColor(Color.GREEN);
            } else if (item.getRataRata() < 540) {
                hasillabel.setText(getString(R.string.hasil_hasillabel) + ": " + item.getRataRata() + " (" + getString(R.string.hasil_hasilsedang) + ")");
                hasillabel.setTextColor(Color.YELLOW);
            } else {
                hasillabel.setText(getString(R.string.hasil_hasillabel) + ": " + item.getRataRata() + " (" + getString(R.string.hasil_hasilberat) + ")");
                hasillabel.setTextColor(Color.RED);
            }

            TextView gagal = findViewById(R.id.gagallabel);
            gagal.setText(getString(R.string.hasil_gagallabel) + ": " + item.getGagal() + " " + getString(R.string.hasil_frekuensilabel));

            String res = item.getJeda().substring(1, item.getJeda().length() - 1);
            String[] jedas = res.split(",");
            TableLayout tbllayout = findViewById(R.id.tabelhasil);
            int i = 0;
            while (i < jedas.length) {
                TableRow tbrow = new TableRow(this);
                TableRow.LayoutParams lp0 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                tbrow.setLayoutParams(lp0);
                TextView t1v = new TextView(this);
                t1v.setText("" + (i + 1));
                t1v.setTextColor(Color.BLACK);
                t1v.setGravity(Gravity.CENTER_HORIZONTAL);
                TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                lp1.column = 0;
                t1v.setGravity(Gravity.CENTER_HORIZONTAL);
                t1v.setLayoutParams(lp1);
                tbrow.addView(t1v);
                TextView t2v = new TextView(this);
                t2v.setText(jedas[i]);
                t2v.setTextColor(Color.BLACK);
                t2v.setGravity(Gravity.RIGHT);
                TableRow.LayoutParams lp2 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                lp2.column = 1;
                t2v.setGravity(Gravity.CENTER_HORIZONTAL);
                t2v.setLayoutParams(lp2);
                tbrow.addView(t2v);
                i = i + 1;
                TextView t3v = new TextView(this);
                t3v.setText("" + (i + 1));
                t3v.setTextColor(Color.BLACK);
                t3v.setGravity(Gravity.CENTER_HORIZONTAL);
                TableRow.LayoutParams lp3 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                lp3.column = 3;
                t3v.setLayoutParams(lp3);
                tbrow.addView(t3v);
                TextView t4v = new TextView(this);
                t4v.setText(jedas[i]);
                t4v.setTextColor(Color.BLACK);
                t4v.setGravity(Gravity.CENTER_HORIZONTAL);
                TableRow.LayoutParams lp4 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                lp4.column = 4;
                t4v.setLayoutParams(lp4);
                tbrow.addView(t4v);
                i = i + 1;
                tbllayout.addView(tbrow);
            }
        }


    private void checkPermissionMaps() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            getAddressFromLocation(latitude, longitude);
        }
    };

    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                currentAddress = address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(HasilPengukuran.this, MainWindow.class);
        startActivity(intent);
    }

    private View.OnClickListener onSimpanClick = new View.OnClickListener() {
        public void onClick(View v) {
            EditText editnama = (EditText) findViewById(R.id.editnama);
            results.get(cur_idx).setNamaObservant(editnama.getText().toString());

            EditText edittgllahir = (EditText) findViewById(R.id.edittgllahir);
            String tgllahir = edittgllahir.getText().toString();
            if (isValidDate(tgllahir)) {
                results.get(cur_idx).setTglLahir(tgllahir);
            } else {
                edittgllahir.setError("Invalid date format. Use yyyy-MM-dd");
                return;
            }

            EditText editjabatan = (EditText) findViewById(R.id.editjabatan);
            results.get(cur_idx).setJabatan(editjabatan.getText().toString());

            EditText editperusahaan = (EditText) findViewById(R.id.editperusahaan);
            results.get(cur_idx).setnamaPerusahaan(editperusahaan.getText().toString());

            String namaobservant = results.get(cur_idx).getNamaObservant();

            String lokasi = results.get(cur_idx).getLokasi();

            db.addHasil(results.get(cur_idx), namaobservant);
        }
    };

    View.OnClickListener onKirimClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (results.isEmpty()) {
                Toast.makeText(HasilPengukuran.this, "No data to send", Toast.LENGTH_SHORT).show();
                return;
            }
            Hasil current = results.get(cur_idx);
            if (current != null) {
                sendDataToWebsite(current);
            }
        }
    };

    private void sendDataToWebsite(Hasil hasil) {
        managerAPI.sendHasil(hasil, new ManagerAPI.DataCallback() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                runOnUiThread(() -> {
                    Toast.makeText(HasilPengukuran.this, "Data sent successfully", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(HasilPengukuran.this, "Failed to send data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    private View.OnClickListener onHapusClick = new View.OnClickListener() {
        public void onClick(View v) {
            Hasil hsl = results.remove(cur_idx);
            cur_idx = 0;
            db.removeHasil(hsl);
            Button berikut = (Button) findViewById(R.id.nextbutton);
            berikut.performClick();
        }
    };

    private View.OnClickListener onNextClick = new View.OnClickListener() {
        public void onClick(View v) {
            cur_idx++;
            if (cur_idx >= results.size())
                cur_idx = 0;
            if (results.get(cur_idx).getJenistest() == 1)
                HasilPengukuran.this.setTitle("PVT: " + getString(R.string.title_hasilpengukuran) + " " + getString(R.string.mw_cahayabtn));
            else
                HasilPengukuran.this.setTitle("PVT: " + getString(R.string.title_hasilpengukuran) + " " + getString(R.string.mw_suarabtn));

            Hasil hsl = results.get(cur_idx);
            EditText editnama = (EditText) findViewById(R.id.editnama);
            editnama.setText(hsl.getNamaObservant());
            editnama.setEnabled(false);

            EditText edittgllahir = (EditText) findViewById(R.id.edittgllahir);
            edittgllahir.setText(formatDate(hsl.getTglLahir()));
            edittgllahir.setEnabled(false);

            EditText editjabatan = (EditText) findViewById(R.id.editjabatan);
            editjabatan.setText(hsl.getJabatan());
            editjabatan.setEnabled(false);

            EditText editperusahaan = (EditText) findViewById(R.id.editperusahaan);
            editperusahaan.setText(hsl.getnamaPerusahaan());
            editperusahaan.setEnabled(false);

            TextView tanggal = (TextView) findViewById(R.id.tipelabel);
            tanggal.setText(getString(R.string.hasil_tipelabel) + ": " + hsl.getTanggal().toString());

            TextView lokasiLabel = findViewById(R.id.lokasilabel);
            if (lokasi != null) {
                lokasiLabel.setText(getString(R.string.hasil_lokasilabel) + ": " + lokasi);
            } else {
                lokasiLabel.setText(getString(R.string.hasil_lokasilabel) + ": Lokasi Tidak Diketahui");
            }

            TextView hasil = (TextView) findViewById(R.id.hasillabel);

            if (hsl.getRataRata() < 240) {
                hasil.setText(getString(R.string.hasil_hasillabel) + ": " + hsl.getRataRata() + " (" + getString(R.string.hasil_hasilnormal) + ")");
                hasil.setTextColor(Color.BLUE);
            } else if (hsl.getRataRata() < 480) {
                hasil.setText(getString(R.string.hasil_hasillabel) + ": " + hsl.getRataRata() + " (" + getString(R.string.hasil_hasilringan) + ")");
                hasil.setTextColor(Color.GREEN);
            } else if (hsl.getRataRata() < 540) {
                hasil.setText(getString(R.string.hasil_hasillabel) + ": " + hsl.getRataRata() + " (" + getString(R.string.hasil_hasilsedang) + ")");
                hasil.setTextColor(Color.YELLOW);
            } else {
                hasil.setText(getString(R.string.hasil_hasillabel) + ": " + hsl.getRataRata() + " (" + getString(R.string.hasil_hasilberat) + ")");
                hasil.setTextColor(Color.RED);
            }

            TextView gagal = (TextView) findViewById(R.id.gagallabel);
            gagal.setText(getString(R.string.hasil_gagallabel) + ": " + hsl.getGagal() + " " + getString(R.string.hasil_frekuensilabel));

            String res = hsl.getJeda().substring(1, hsl.getJeda().length() - 1);
            String[] jedas = res.split(",");
            TableLayout tbllayout = (TableLayout) findViewById(R.id.tabelhasil);
            tbllayout.removeViews(1, tbllayout.getChildCount() - 1);

            int i = 0;
            while (i < jedas.length) {
                TableRow tbrow = new TableRow(HasilPengukuran.this);
                TableRow.LayoutParams lp0 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                tbrow.setLayoutParams(lp0);
                TextView t1v = new TextView(HasilPengukuran.this);
                t1v.setText("" + (i + 1));
                t1v.setTextColor(Color.BLACK);
                t1v.setGravity(Gravity.CENTER_HORIZONTAL);
                TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                lp1.column = 0;
                t1v.setGravity(Gravity.CENTER_HORIZONTAL);
                t1v.setLayoutParams(lp1);
                tbrow.addView(t1v);
                TextView t2v = new TextView(HasilPengukuran.this);
                t2v.setText(jedas[i]);
                t2v.setTextColor(Color.BLACK);
                t2v.setGravity(Gravity.RIGHT);
                TableRow.LayoutParams lp2 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                lp2.column = 1;
                t2v.setGravity(Gravity.CENTER_HORIZONTAL);
                t2v.setLayoutParams(lp2);
                tbrow.addView(t2v);
                i = i + 1;
                TextView t3v = new TextView(HasilPengukuran.this);
                t3v.setText("" + (i + 1));
                t3v.setTextColor(Color.BLACK);
                t3v.setGravity(Gravity.CENTER_HORIZONTAL);
                TableRow.LayoutParams lp3 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                lp3.column = 3;
                t3v.setLayoutParams(lp3);
                tbrow.addView(t3v);
                TextView t4v = new TextView(HasilPengukuran.this);
                t4v.setText(jedas[i]);
                t4v.setTextColor(Color.BLACK);
                t4v.setGravity(Gravity.CENTER_HORIZONTAL);
                TableRow.LayoutParams lp4 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                lp4.column = 4;

                t4v.setLayoutParams(lp4);
                tbrow.addView(t4v);

                i = i + 1;
                tbllayout.addView(tbrow);
            }
        }
    };

    private String formatDate(String dateStr) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        try {
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr;
        }
    }

    private void displayHasil(Hasil hasil) {
        // Update this method to display a single Hasil item
        Log.d("HasilPengukuran", "Displaying Hasil: " + hasil.getNamaObservant());
        TextView namaObservantTextView = findViewById(R.id.editnama);
        TextView tanggalTextView = findViewById(R.id.tipelabel);
        // Set UI elements with hasil data
        namaObservantTextView.setText(hasil.getNamaObservant());
        tanggalTextView.setText(hasil.getTanggal());
    }


    private boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }


    protected void onDestroy() {
        super.onDestroy();
        // Save last known location to SharedPreferences when activity is destroyed
        SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
        editor.putString("lastKnownLocation", lokasi);
        editor.apply();
    }



}
