package com.digitech_maker.pvt;

import android.widget.DatePicker;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest {

    @Rule
    public ActivityScenarioRule<RegisterActivity> activityRule =
            new ActivityScenarioRule<>(RegisterActivity.class);

    @Test
    public void testRegisterSuccess() {
        // Isi semua data kecuali tanggal lahir (akan diisi lewat DatePicker)
        Espresso.onView(withId(R.id.namaobservant)).perform(typeText("John Doe"));
        Espresso.onView(withId(R.id.password)).perform(typeText("password123"));
        Espresso.onView(withId(R.id.company)).perform(typeText("TechCorp"));

        // Pilih tanggal lahir menggunakan DatePicker
        Espresso.onView(withId(R.id.birthdate)).perform(click()); // Klik untuk membuka DatePicker
        Espresso.onView(Matchers.instanceOf(DatePicker.class))
                .perform(PickerActions.setDate(1990, 1, 1)); // Set tanggal ke 1 Januari 2000
        Espresso.onView(withText("OK")).perform(click()); // Klik OK pada dialog DatePicker

        // Klik tombol register
        Espresso.onView(withId(R.id.registerButton)).perform(click());

        // Verifikasi bahwa aplikasi berpindah ke MainWindow
        // Anda bisa memverifikasi elemen spesifik jika ada di MainWindow
    }

    @Test
    public void testRegisterFailureWithEmptyField() {
        // Hanya isi sebagian data (misalnya, namaobservant kosong)
        Espresso.onView(withId(R.id.password)).perform(typeText("password123"));
        Espresso.onView(withId(R.id.company)).perform(typeText("TechCorp"));

        // Pilih tanggal lahir menggunakan DatePicker
        Espresso.onView(withId(R.id.birthdate)).perform(click());
        Espresso.onView(Matchers.instanceOf(DatePicker.class))
                .perform(PickerActions.setDate(2000, 1, 1));
        Espresso.onView(withText("OK")).perform(click());

        // Klik tombol register
        Espresso.onView(withId(R.id.registerButton)).perform(click());
        
    }
}
