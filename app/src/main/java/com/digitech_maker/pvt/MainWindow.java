package com.digitech_maker.pvt;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;


public class MainWindow extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 102;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_LAST_KNOWN_LOCATION = "last_known_location";
    private SharedPreferences sharedPreferences;

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

        Button btn1 = findViewById(R.id.suaraBtn);
        btn1.setOnClickListener(v -> handleTestStart(2)); // Audio test

        Button btn2 = findViewById(R.id.cahayaBtn);
        btn2.setOnClickListener(v -> handleTestStart(1)); // Visual test

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logout());

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Request location permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
    }

    private void handleTestStart(int testType) {
        Intent locationIntent = new Intent(MainWindow.this, LocationActivity.class);
        startActivityForResult(locationIntent, REQUEST_LOCATION);
    }

    private void startTest(int testType, String lokasi) {
        Intent intent = new Intent(MainWindow.this, Welcom.class);

        RadioGroup pengukuran = findViewById(R.id.radioPengukuran);
        int selectedId = pengukuran.getCheckedRadioButtonId();
        RadioButton selectedButton = findViewById(selectedId);
        int frq = 20;
        if (selectedButton.getId() == R.id.tiga0kali) {
            frq = 30;
        } else if (selectedButton.getId() == R.id.empat0kali) {
            frq = 40;
        } else if (selectedButton.getId() == R.id.enam0kali) {
            frq = 60;
        }
        Welcom.frekuensi = frq;

        RadioGroup dlyGroup = findViewById(R.id.radioPeriode);
        int delayId = dlyGroup.getCheckedRadioButtonId();
        RadioButton delayButton = findViewById(delayId);
        int delayPeriode = 5;
        if (delayButton.getId() == R.id.tujuhdetik) {
            delayPeriode = 7;
        } else if (delayButton.getId() == R.id.sepuluhdetik) {
            delayPeriode = 10;
        }
        Welcom.delay = delayPeriode;

        Welcom.tipetest = testType;

        intent.putExtra("lokasi", lokasi);
        startActivity(intent);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION) {
            if (resultCode == RESULT_OK && data != null) {
                String lokasi = data.getStringExtra("lokasi");
                startTest(1, lokasi); // Change testType according to your logic
            } else {
                Toast.makeText(this, "Failed to get location.", Toast.LENGTH_SHORT).show();
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

