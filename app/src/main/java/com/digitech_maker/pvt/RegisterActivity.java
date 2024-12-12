package com.digitech_maker.pvt;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    public EditText namaobservantEditText, passwordEditText, tgllahirEditText, namaperusahaanEditText;
    private Button registerButton;
    public DatabaseHandler db;
    public DataUser dataUser;
    private TextView loginLink;
    private Calendar myCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        namaobservantEditText = findViewById(R.id.namaobservant);
        passwordEditText = findViewById(R.id.password);
        tgllahirEditText = findViewById(R.id.birthdate);
        namaperusahaanEditText = findViewById(R.id.company);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);

        db = new DatabaseHandler(getApplicationContext());
        dataUser = new DataUser(this);

        db.logDatabaseState();

        tgllahirEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tgllahirEditText.setText(dateFormatter.format(myCalendar.getTime()));
            }
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void registerUser() {
        String namaobservant = namaobservantEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String tgllahir = tgllahirEditText.getText().toString().trim();
        String namaPerusahaan = namaperusahaanEditText.getText().toString().trim();

        if (namaobservant.isEmpty() || password.isEmpty() || tgllahir.isEmpty() || namaPerusahaan.isEmpty()) {
            showToast("Please enter all details");
            return;
        }

        if (db != null) {
            db.addUser(namaobservant, password, tgllahir, namaPerusahaan);
            dataUser.UserActive(namaobservant, tgllahir,  namaPerusahaan);

            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", namaobservant);
            editor.putString("tgllahir", tgllahir);
            editor.putString("namaPerusahaan", namaPerusahaan);
            editor.putBoolean("isLoggedIn", true);
            editor.apply();

            showToast("User Registered Successfully");;

            Intent intent = new Intent(RegisterActivity.this, MainWindow.class);
            startActivity(intent);

        } else {
            Log.e("RegisterActivity", "DatabaseHandler is null");
        }
    }
}
