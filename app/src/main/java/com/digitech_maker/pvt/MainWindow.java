package com.digitech_maker.pvt;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class MainWindow extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 102;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_LAST_KNOWN_LOCATION = "last_known_location";
    private SharedPreferences sharedPreferences;
    private BroadcastReceiver locationReceiver;
    private String lokasi;
    private int testType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_window);

        Button btn0 = findViewById(R.id.closebutton);
        btn0.setOnClickListener(v -> finish());

        Button btnekspor = findViewById(R.id.exporttbutton);
        btnekspor.setOnClickListener(v -> exportData());

        Button btnekspornew = findViewById(R.id.exportnewbutton);
        btnekspornew.setOnClickListener(v -> exportLatestData());

        Button btnreport = findViewById(R.id.reportbutton);
        btnreport.setOnClickListener(v -> showReport());

        Button btnAudioTest = findViewById(R.id.suaraBtn);
        btnAudioTest.setOnClickListener(v -> startTest(2)); // Audio test

        Button btnVisualTest = findViewById(R.id.cahayaBtn);
        btnVisualTest.setOnClickListener(v -> startTest(1)); // Visual test

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logout());

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Request location permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }

        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && "com.digitech_maker.pvt.LOCATION_UPDATE".equals(intent.getAction())) {
                    // Retrieve lokasi from the broadcast intent
                    lokasi = intent.getStringExtra("lokasi");
                    testType = intent.getIntExtra("testType", -1);

                    // If lokasi is null or empty, fall back to SharedPreferences
                    if (lokasi == null || lokasi.isEmpty()) {
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        lokasi = sharedPreferences.getString("lastKnownLocation", "Location unknown");
                    }

                    // Now, use the testType logic to start the appropriate test
                    if (testType == 1) {
                        startVisualTest(lokasi);
                    } else if (testType == 2) {
                        startAudioTest(lokasi);
                    } else {
                        Toast.makeText(MainWindow.this, "Unknown test type.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        // Register the receiver in the onCreate method
        IntentFilter filter = new IntentFilter("com.digitech_maker.pvt.LOCATION_UPDATE");
        registerReceiver(locationReceiver, filter);
    }


    private void startTest(int testType) {
        Intent locationIntent = new Intent(MainWindow.this, LocationActivity.class);
        locationIntent.putExtra("testType", testType);
        startActivity(locationIntent);
    }


    private void startVisualTest(String lokasi) {
        Intent intent = new Intent(MainWindow.this, Welcom.class);
        intent.putExtra("lokasi", lokasi);

        // Get the selected frequency
        RadioGroup pengukuran = findViewById(R.id.radioPengukuran);
        int idt = pengukuran.getCheckedRadioButtonId();
        RadioButton selectedbutton = findViewById(idt);
        int frq = 20; // default value
        if (selectedbutton.getId() == R.id.tiga0kali)
            frq = 30;
        if (selectedbutton.getId() == R.id.empat0kali)
            frq = 40;
        if (selectedbutton.getId() == R.id.enam0kali)
            frq = 60;
        Welcom.frekuensi = frq;

        // Get the selected delay period
        RadioGroup dlygroup = findViewById(R.id.radioPeriode);
        int dly = dlygroup.getCheckedRadioButtonId();
        RadioButton selectedbutton2 = findViewById(dly);
        int delayperiode = 5; // default value
        if (selectedbutton2.getId() == R.id.tujuhdetik)
            delayperiode = 7;
        if (selectedbutton2.getId() == R.id.sepuluhdetik)
            delayperiode = 10;
        Welcom.delay = delayperiode;

        Welcom.tipetest = 1;
        startActivity(intent);
        Toast.makeText(this, "Starting Visual Test at " + lokasi, Toast.LENGTH_SHORT).show();
    }

    private void startAudioTest(String lokasi) {
        Intent intent = new Intent(MainWindow.this, Welcom.class);
        intent.putExtra("lokasi", lokasi);

        // Get the selected frequency
        RadioGroup pengukuran = findViewById(R.id.radioPengukuran);
        int idt = pengukuran.getCheckedRadioButtonId();
        RadioButton selectedbutton = findViewById(idt);
        int frq = 20; // default value
        if (selectedbutton.getId() == R.id.tiga0kali)
            frq = 30;
        if (selectedbutton.getId() == R.id.empat0kali)
            frq = 40;
        if (selectedbutton.getId() == R.id.enam0kali)
            frq = 60;
        Welcom.frekuensi = frq;

        // Get the selected delay period
        RadioGroup dlygroup = findViewById(R.id.radioPeriode);
        int dly = dlygroup.getCheckedRadioButtonId();
        RadioButton selectedbutton2 = findViewById(dly);
        int delayperiode = 5; // default value
        if (selectedbutton2.getId() == R.id.tujuhdetik)
            delayperiode = 7;
        if (selectedbutton2.getId() == R.id.sepuluhdetik)
            delayperiode = 10;
        Welcom.delay = delayperiode;

        Welcom.tipetest = 2;
        startActivity(intent);
        Toast.makeText(this, "Starting Audio Test at " + lokasi, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationReceiver != null) {
            unregisterReceiver(locationReceiver);
        }
    }


    private void exportData() {
        DatabaseHandler db = new DatabaseHandler(MainWindow.this);
        List<Hasil> results = db.getAllHasil();
        if (!results.isEmpty()) {
            FileHandler fh = new FileHandler(MainWindow.this);
            fh.exportXlsKeepData(results.toArray());
        }
    }

    private void exportLatestData() {
        DatabaseHandler db = new DatabaseHandler(MainWindow.this);
        List<Hasil> results = db.getLastHasil();
        if (!results.isEmpty()) {
            FileHandler fh = new FileHandler(MainWindow.this);
            fh.exportXlsKeepData(results.toArray());
        }
    }

    private void showReport() {
        DatabaseHandler db = new DatabaseHandler(MainWindow.this);
        List<Hasil> results = db.getAllHasil();
        if (!results.isEmpty()) {
            Intent intent = new Intent(MainWindow.this, HasilPengukuran.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, do nothing extra here
            } else {
                Toast.makeText(this, "Location permission is required for this app.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void logout() {
        // Clear user session data
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Clear DataUser
        DataUser dataUser = new DataUser(this);
        dataUser.clearUserActive();

        // Redirect to LoginActivity
        Intent intent = new Intent(MainWindow.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
