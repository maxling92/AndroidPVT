package com.digitech_maker.pvt2025;

import android.widget.DatePicker;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RegisterAndLoginTest {

    @Rule
    public ActivityScenarioRule<RegisterActivity> registerActivityRule =
            new ActivityScenarioRule<>(RegisterActivity.class);

    @Test
    public void testRegisterAndLoginFlow() {
        // **Step 1: Registrasi**
        // Isi semua data untuk registrasi
        onView(withId(R.id.namaobservant)).perform(typeText("John Doe"));
        onView(withId(R.id.password)).perform(typeText("password123"));
        onView(withId(R.id.company)).perform(typeText("TechCorp"));

        // Pilih tanggal lahir menggunakan DatePicker
        onView(withId(R.id.birthdate)).perform(click());
        onView(Matchers.instanceOf(DatePicker.class))
                .perform(PickerActions.setDate(1990, 1, 1));
        onView(withText("OK")).perform(click());

        // Klik tombol register
        onView(withId(R.id.registerButton)).perform(click());

        // Verifikasi registrasi berhasil

        // **Step 2: Logout**
        // Klik tombol logout
        onView(withId(R.id.logoutButton)).perform(click());

        // Verifikasi kembali ke halaman login
        onView(withId(R.id.loginButton)).check(matches(withText("Login")));

        // **Step 3: Login Berhasil**
        // Isi username dan password yang sudah didaftarkan
        onView(withId(R.id.namaobservantEditText)).perform(typeText("John Doe"));
        onView(withId(R.id.passwordEditText)).perform(typeText("password123"));
        Espresso.closeSoftKeyboard();

        // Klik tombol login
        onView(withId(R.id.loginButton)).perform(click());

        onView(withId(R.id.logoutButton)).perform(click());
        // **Step 4: Login Gagal**
        // Kosongkan salah satu field untuk tes login gagal
        onView(withId(R.id.namaobservantEditText)).perform(typeText(""));
        Espresso.closeSoftKeyboard();

        // Klik tombol login
        onView(withId(R.id.loginButton)).perform(click());


    }
}

