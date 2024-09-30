package com.digitech_maker.pvt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    DatabaseHandler dbHandler;
    EditText namaobservantEditText, passwordEditText;
    Button loginButton;
    private TextView registerLink;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataUser dataUser = new DataUser(this);
        if (dataUser.isLoggedIn()) {
            // User is already logged in, redirect to MainWindow
            startActivity(new Intent(LoginActivity.this, MainWindow.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_login);

        dbHandler = new DatabaseHandler(this);

        namaobservantEditText = findViewById(R.id.namaobservantEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namaobservant = namaobservantEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                loginUser(namaobservant, password);

            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        if (dbHandler.isTableExists(DatabaseHandler.TABLE_USERS)) {
            Log.d("LoginActivity", "Users table exists.");
        } else {
            Log.e("LoginActivity", "Users table does not exist.");
            // Handle the case where the table does not exist, maybe by recreating it or showing an error message
        }
    }

    public void loginUser(String namaobservant, String password) {
        if (dbHandler.isTableExists(DatabaseHandler.TABLE_USERS)) {
            boolean isValid = dbHandler.checkUser(namaobservant, password);
            if (isValid) {
                String tgllahir = dbHandler.getUserTanggallahir(namaobservant);
                String jabatan = dbHandler.getUserJabatan(namaobservant);
                String namaPerusahaan = dbHandler.getUserPerusahaan(namaobservant);

                // Ensure that the date format is consistent
                if (tgllahir != null && tgllahir.contains("/")) {
                    tgllahir = tgllahir.replace("/", "-");  // Ensure consistent format
                }

                DataUser dataUser = new DataUser(this);  // Create an instance of DataUser
                dataUser.UserActive(namaobservant, tgllahir, jabatan, namaPerusahaan);

                // Save user details to SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", namaobservant);
                editor.putString("tgllahir", tgllahir);  // Save formatted tgllahir
                editor.putString("jabatan", jabatan);
                editor.putString("namaPerusahaan", namaPerusahaan);
                editor.putBoolean("isLoggedIn", true);
                editor.apply();

                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, MainWindow.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("LoginActivity", "Table 'users' does not exist.");
            Toast.makeText(this, "Database error. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }


}

