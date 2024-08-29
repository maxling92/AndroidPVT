package com.digitech_maker.pvt;

import android.content.Context;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class Welcom extends AppCompatActivity {

    private ImageView theicon;
    private TextView counter;
    private boolean response;
    private Button btn;
    private Random rand;
    private long thecounter_begin, thecounter_end;
    private MediaPlayer mp = null;
    private int BEEP_LENGTH = 50;
    private int SLEEP_ATTACT = 500;

    private String namaobservant;
    private String tgllahir;
    private String jabatan;
    private String namaPerusahaan;
    private String lokasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        namaobservant = sharedPreferences.getString("username", "ExampleUsername");
        tgllahir = sharedPreferences.getString("tgllahir", "1990/01/01");
        jabatan = sharedPreferences.getString("jabatan", "Driver");
        namaPerusahaan = sharedPreferences.getString("namaPerusahaan", "ExampleCompany");

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("lokasi")) {
            this.lokasi = intent.getStringExtra("lokasi");
        }

        Button lamp = findViewById(R.id.buttonlight);
        lamp.setOnClickListener(onLampClick);
        lamp.setVisibility(View.INVISIBLE);

        Button sound = findViewById(R.id.buttonsound);
        sound.setOnClickListener(onSoundClick);
        sound.setVisibility(View.INVISIBLE);

        Button mulai = findViewById(R.id.buttonstart);
        mulai.setOnClickListener(onStartClick);

        Button btnnext = findViewById(R.id.buttonnext);
        btnnext.setOnClickListener(onNextClick);
        btnnext.setVisibility(View.INVISIBLE);

        Button btnback = findViewById(R.id.buttonback);
        btnback.setOnClickListener(onBackClick);
        btnback.setVisibility(View.INVISIBLE);

        counter = findViewById(R.id.textView2);
        rand = new Random();
    }

    public View.OnClickListener onStartClick = new View.OnClickListener() {
        public void onClick(View v) {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(tgllahir, inputFormatter);
            String formattedTglLahir = date.format(outputFormatter);

            Button mulai = findViewById(R.id.buttonstart);
            mulai.setVisibility(View.INVISIBLE);
            if (tipetest == 1) {
                btn = findViewById(R.id.buttonlight);
                btn.setVisibility(View.VISIBLE);
                btn.setText("VISUAL");
                Button btn2 = findViewById(R.id.buttonsound);
                btn2.setVisibility(View.VISIBLE);
                btn2.setText("VISUAL");
            } else {
                Button button2 = findViewById(R.id.buttonlight);
                button2.setVisibility(View.VISIBLE);
                button2.setText("AUDIO");
                btn = findViewById(R.id.buttonsound);
                btn.setVisibility(View.VISIBLE);
                btn.setText("AUDIO");
            }

            counter.setText("Ready!!");

            Runnable runnable = new Runnable() {
                public void run() {
                    try {
                        hasil = new int[frekuensi];
                        gagal = 0;
                        for (int j = 0; j < frekuensi; j++) {
                            int sleepingtime = Math.abs(rand.nextInt(delay * 1000)) + 2000;
                            SystemClock.sleep(sleepingtime);
                            thecounter_begin = System.currentTimeMillis();
                            threadMsg("HIT IT");
                            response = false;

                            if (tipetest == 2) { // AUDIO TEST
                                mp = MediaPlayer.create(Welcom.this, R.raw.bell);
                                mp.start();
                            }

                            while (!response) {
                                SystemClock.sleep(5);
                            }

                            hasil[j] = (int) ((thecounter_end - thecounter_begin)) - 150;
                            threadMsg(String.valueOf(hasil[j]));
                        }

                        result = new Hasil(namaobservant, formattedTglLahir, jabatan, namaPerusahaan);
                        result.setTanggal(new Date().toString());
                        result.setRataRata(hasil);
                        result.setJeda(Arrays.toString(hasil));
                        result.setGagal(gagal);

                        btn.getHandler().post(new Runnable() {
                            public void run() {
                                btn.setVisibility(View.INVISIBLE);
                                Button nextbtn = findViewById(R.id.buttonnext);
                                nextbtn.setVisibility(View.VISIBLE);
                                Button backbtn = findViewById(R.id.buttonback);
                                backbtn.setVisibility(View.VISIBLE);
                                Button btnkiri = findViewById(R.id.buttonlight);
                                btnkiri.setVisibility(View.INVISIBLE);
                                Button btnkanan = findViewById(R.id.buttonsound);
                                btnkanan.setVisibility(View.INVISIBLE);
                                counter.setText("DONE");
                            }
                        });
                    } catch (Throwable t) {
                        Log.i("Function", "Thread  exception " + t);
                    }
                }

                private void threadMsg(String msg) {
                    if (msg != null && !msg.isEmpty()) {
                        Message msgObj = handler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("message", msg);
                        msgObj.setData(b);
                        handler.sendMessage(msgObj);
                    }
                }

                private final Handler handler = new Handler() {
                    public void handleMessage(Message msg) {
                        String aResponse = msg.getData().getString("message");
                        counter.setText(aResponse);
                    }
                };
            };

            Thread background = new Thread(runnable);
            background.start();
        }
    };

    private View.OnClickListener onLampClick = new View.OnClickListener() {
        public void onClick(View v) {
            if (!response) {
                thecounter_end = System.currentTimeMillis();
                response = true;
            } else {
                gagal++;
            }
        }
    };

    private View.OnClickListener onSoundClick = new View.OnClickListener() {
        public void onClick(View v) {
            if (!response) {
                thecounter_end = System.currentTimeMillis();
                response = true;
                if (mp != null) {
                    mp.stop();
                    mp.release();
                    mp = null;
                }
            } else {
                gagal++;
            }
        }
    };

    private View.OnClickListener onNextClick = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(Welcom.this, HasilPengukuran.class);
            intent.putExtra("username", namaobservant);
            intent.putExtra("tgllahir", tgllahir);
            intent.putExtra("jabatan", jabatan);
            intent.putExtra("namaPerusahaan", namaPerusahaan);
            intent.putExtra("lokasi", lokasi);
            startActivity(intent);
        }
    };

    private View.OnClickListener onBackClick = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(Welcom.this, MainWindow.class);
            startActivity(intent);
        }
    };

    public static int frekuensi = 20;
    public static int delay = 5;
    public static int tipetest = 1;
    private int gagal = 0;
    private int[] hasil;
    public static Hasil result;
}
