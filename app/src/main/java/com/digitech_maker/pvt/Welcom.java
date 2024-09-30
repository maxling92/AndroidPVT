package com.digitech_maker.pvt;

import android.content.Context;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
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

            Button mulai = (Button) findViewById(R.id.buttonstart);
            mulai.setVisibility(View.INVISIBLE);
            if (tipetest==1) {
                btn = (Button) findViewById(R.id.buttonlight);
                btn.setVisibility(View.VISIBLE);
                btn.setText("VISUAL");
                Button btn2 = (Button) findViewById(R.id.buttonsound);
                btn2.setVisibility(View.VISIBLE);
                btn2.setText("VISUAL");
            } else {
                Button button2 = (Button) findViewById(R.id.buttonlight);
                button2.setVisibility(View.VISIBLE);
                button2.setText("AUDIO");
                btn = (Button) findViewById(R.id.buttonsound);
                btn.setVisibility(View.VISIBLE);
                btn.setText("AUDIO");
            }
            //btn.setGravity(Gravity.RIGHT);
            //TextView teks = (TextView) findViewById(R.id.textView2);
            counter.setText("Ready!!");

            if (tipetest==1) {
                Thread background = new Thread(new Runnable() {
                    public void run() {
                        try {
                            //theicon.setImageResource(R.drawable.light_on);
                            //frekuensi = 6;
                            hasil = new int[frekuensi];
                            gagal = 0;
                            for (int j = 0; j < frekuensi; j++) {
                                int sleepingtime = Math.abs(rand.nextInt(delay * 1000)) + 2000;
                                SystemClock.sleep(sleepingtime);
                                thecounter_begin = System.currentTimeMillis();
                                threadMsg("HIT IT");
                                response = false;
                                int i = 0;
                                while (!response) {
                                    SystemClock.sleep(5);
                                    //i++;
                                }
                                hasil[j] = (int) ((thecounter_end-thecounter_begin))-150;
                                threadMsg(String.valueOf(hasil[j]));
                            }
                            result = new Hasil(namaobservant, formattedTglLahir, jabatan, namaPerusahaan);
                            result.setTanggal(String.valueOf(new Date()));
                            result.setRataRata(hasil);
                            result.setJeda(Arrays.toString(hasil));
                            result.setGagal(gagal);

//                            Button lamp = (Button) findViewById(R.id.buttonlight);
                            btn.getHandler().post(new Runnable() {
                                public void run() {
                                    btn.setVisibility(View.INVISIBLE);
                                    Button nextbtn = (Button) findViewById(R.id.buttonnext);
                                    nextbtn.setVisibility(View.VISIBLE);
                                    //nextbtn.setGravity(Gravity.RIGHT);
                                    Button backbtn = (Button) findViewById(R.id.buttonback);
                                    backbtn.setVisibility(View.VISIBLE);
                                    //backbtn.setGravity(Gravity.LEFT);

                                    Button btnkiri = (Button) findViewById(R.id.buttonlight);
                                    btnkiri.setVisibility(View.INVISIBLE);
                                    Button btnkanan = (Button) findViewById(R.id.buttonsound);
                                    btnkanan.setVisibility(View.INVISIBLE);

                                    counter.setText("DONE");
                                }
                            });
                            //result.setGagal(gagal);


                        } catch (Throwable t) {
                            // just end the background thread
                            Log.i("Function", "Thread  exception " + t);
                        }
                    }

                    private void threadMsg(String msg) {

                        if (!msg.equals(null) && !msg.equals("")) {
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

                });
                background.start();
            } else {
                Thread background = new Thread(new Runnable() {
                    public void run() {
                        try {
                            //frekuensi=6;
                            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
//                            mp = MediaPlayer.create(Welcom.this, R.raw.bell);
                            hasil = new int[frekuensi];
                            gagal = 0;
                            for (int j = 0; j < frekuensi; j++) {
                                int sleepingtime = Math.abs(rand.nextInt(delay * 1000)) + 2000;
                                SystemClock.sleep(sleepingtime);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,BEEP_LENGTH);
//           s                     mp.start();
                                SystemClock.sleep(100);
                                thecounter_begin = System.currentTimeMillis();
                                response = false;
                                int i = 0;
                                while (!response) {
                                    SystemClock.sleep(5);
                                    if (i>SLEEP_ATTACT) {
                                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, BEEP_LENGTH - 25);
                                        i=0;
                                    } else {
                                        i++;
                                    }

                                }
//                                mp.stop();
                                hasil[j] = (int) ((thecounter_end-thecounter_begin))-BEEP_LENGTH;
//                                mp.prepare();
                                threadMsg(String.valueOf(hasil[j]));
                            }
//                            mp.release();
                            toneGen1.release();
                            result = new Hasil(namaobservant, formattedTglLahir, jabatan, namaPerusahaan);
                            result.setTanggal(String.valueOf(new Date()));
                            result.setRataRata(hasil);
                            result.setJeda(Arrays.toString(hasil));
                            result.setGagal(gagal);
                            result.setJenisTest(2);

                            btn.getHandler().post(new Runnable() {
                                public void run() {
                                    btn.setVisibility(View.INVISIBLE);

                                    Button nextbtn = (Button) findViewById(R.id.buttonnext);
                                    nextbtn.setVisibility(View.VISIBLE);
                                    //nextbtn.setGravity(Gravity.RIGHT);
                                    Button backbtn = (Button) findViewById(R.id.buttonback);
                                    backbtn.setVisibility(View.VISIBLE);
                                    //backbtn.setGravity(Gravity.LEFT);

                                    Button btnkiri = (Button) findViewById(R.id.buttonlight);
                                    btnkiri.setVisibility(View.INVISIBLE);
                                    Button btnkanan = (Button) findViewById(R.id.buttonsound);
                                    btnkanan.setVisibility(View.INVISIBLE);

                                    counter.setText("DONE");
                                }
                            });


                        } catch (Throwable t) {
                            // just end the background thread
                            Log.i("Function", "Thread  exception " + t);
                        }
                    }

                    private void threadMsg(String msg) {

                        if (!msg.equals(null) && !msg.equals("")) {
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

                });
                background.start();

            }
            //EditText address=(EditText)findViewById(R.id.addr);
            //r.setName(name.getText().toString());
            //r.setAddress(address.getText().toString());
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
